# 시스템 아키텍처 설계 (Architecture)

## 🏗️ 전체 시스템 구조
```
┌─────────────────────────────────────────────────────────────┐
│                         Client (Browser)                      │
│                    WebSocket + REST API                       │
└───────────────────────────┬─────────────────────────────────┘
                            │ HTTPS
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   CloudFront (CDN) - Optional                │
│                      정적 파일 캐싱                           │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ↓
┌─────────────────────────────────────────────────────────────┐
│              ALB (Application Load Balancer)                 │
│           Health Check / Sticky Session / SSL                │
└───────────────────────────┬─────────────────────────────────┘
                            │
                ┌───────────┼───────────┐
                ↓           ↓           ↓
        ┌──────────┐ ┌──────────┐ ┌──────────┐
        │ EC2 #1   │ │ EC2 #2   │ │ EC2 #3   │
        │ Spring   │ │ Spring   │ │ Spring   │
        │ Boot App │ │ Boot App │ │ Boot App │
        └────┬─────┘ └────┬─────┘ └────┬─────┘
             │            │            │
             └────────────┼────────────┘
                          ↓
        ┌─────────────────────────────────────┐
        │    ElastiCache (Redis Cluster)      │
        │  - 시세 캐싱 (TTL 1초)              │
        │  - Pub/Sub (실시간 브로드캐스팅)     │
        │  - 세션 관리 (JWT 블랙리스트)        │
        │  - Sorted Set (호가창)              │
        └─────────────────┬───────────────────┘
                          ↓
        ┌─────────────────────────────────────┐
        │         RDS MySQL                   │
        │  ┌──────────┐      ┌──────────┐     │
        │  │  Master  │──────│ Replica  │     │
        │  │  (Write) │      │  (Read)  │     │
        │  └──────────┘      └──────────┘     │
        │                                     │
        │  * Multi-AZ는 비용 고려하여 제외   │
        │  * 일단 단일 AZ로 시작              │
        └─────────────────────────────────────┘
                          │
                          ↓
        ┌─────────────────────────────────────┐
        │         External API (업비트)        │
        │  - REST API (시세 조회)              │
        │  - WebSocket (실시간 시세)            │
        └─────────────────────────────────────┘
                          │
                          ↓
        ┌─────────────────────────────────────┐
        │     CloudWatch (모니터링/알람)        │
        │  - CPU/Memory 사용률                 │
        │  - API 응답 시간                     │
        │  - 에러 로그 수집                     │
        └─────────────────────────────────────┘
```

---

## 📊 계층별 상세 설계

### 1. Presentation Layer (클라이언트)

#### 기술 스택
- HTML5 / CSS3 / JavaScript (Vanilla 또는 React - 선택)
- WebSocket Client API
- Fetch API (REST 호출)

#### 주요 기능
- 실시간 시세 차트 렌더링
- 호가창 표시
- 주문 폼
- 포트폴리오 현황

---

### 2. Application Layer (Spring Boot)

#### 패키지 구조
```
com.stock.trading
├── config/              # 설정 클래스
│   ├── SecurityConfig.java
│   ├── RedisConfig.java
│   ├── WebSocketConfig.java
│   └── JpaConfig.java
│
├── domain/              # 도메인 모델
│   ├── user/
│   │   ├── User.java
│   │   ├── UserRepository.java
│   │   └── UserService.java
│   ├── stock/
│   │   ├── Stock.java
│   │   └── StockService.java
│   ├── order/
│   │   ├── Order.java
│   │   ├── OrderRepository.java
│   │   └── OrderService.java
│   └── portfolio/
│       ├── Portfolio.java
│       └── PortfolioService.java
│
├── api/                 # REST Controller
│   ├── AuthController.java
│   ├── StockController.java
│   ├── OrderController.java
│   └── PortfolioController.java
│
├── websocket/           # WebSocket Handler
│   ├── StockWebSocketHandler.java
│   └── WebSocketSessionManager.java
│
├── external/            # 외부 API 연동
│   └── UpbitApiClient.java
│
├── security/            # 인증/인가
│   ├── JwtTokenProvider.java
│   └── JwtAuthenticationFilter.java
│
└── common/              # 공통 유틸
    ├── exception/
    ├── response/
    └── util/
```

---

### 3. Data Layer

#### ERD (Entity Relationship Diagram)
```
┌─────────────────┐
│      User       │
├─────────────────┤
│ id (PK)         │◄─────┐
│ email           │      │
│ password        │      │
│ nickname        │      │
│ balance         │      │ 1
│ created_at      │      │
└─────────────────┘      │
                         │
                         │ N
┌─────────────────┐      │
│    Portfolio    │      │
├─────────────────┤      │
│ id (PK)         │      │
│ user_id (FK)    ├──────┘
│ stock_code      │
│ quantity        │
│ avg_price       │
│ updated_at      │
└─────────────────┘
        │
        │ 1
        │
        │ N
┌─────────────────┐
│      Order      │
├─────────────────┤
│ id (PK)         │
│ user_id (FK)    │
│ stock_code      │
│ order_type      │ (BUY/SELL)
│ price_type      │ (MARKET/LIMIT)
│ quantity        │
│ price           │
│ status          │ (PENDING/COMPLETED/CANCELLED)
│ created_at      │
└─────────────────┘

┌─────────────────┐
│      Stock      │
├─────────────────┤
│ code (PK)       │ (BTC-KRW, ETH-KRW 등)
│ name            │
│ current_price   │
│ updated_at      │
└─────────────────┘
```

---

### 4. Cache Layer (Redis)

#### Redis 데이터 구조
```
# 1. 실시간 시세 캐싱
Key: stock:price:{stock_code}
Type: String
Value: {"price": 50000000, "timestamp": 1706428800}
TTL: 1초

# 2. 호가창 (매수/매도 상위 10개)
Key: stock:orderbook:{stock_code}:ask
Type: Sorted Set
Score: 가격
Member: {"price": 50000000, "quantity": 10}

# 3. 실시간 시세 Pub/Sub
Channel: stock:price:{stock_code}
Message: {"code": "BTC-KRW", "price": 50000000, "change": "+1.5%"}

# 4. 세션 관리 (JWT 블랙리스트)
Key: blacklist:token:{token}
Type: String
Value: "revoked"
TTL: JWT 만료 시간과 동일
```

---

## 🔄 주요 플로우 설계

### 1. 실시간 시세 업데이트 플로우
```
[업비트 WebSocket]
      │
      │ 1초마다 시세 전송
      ↓
[UpbitApiClient]
      │
      │ 시세 파싱
      ↓
[Redis Pub/Sub]
      │
      │ publish("stock:price:BTC-KRW", data)
      ↓
[Spring Boot (Subscriber)]
      │
      │ 모든 EC2 인스턴스가 구독
      ↓
[WebSocket Session Manager]
      │
      │ 연결된 모든 클라이언트에게 전송
      ↓
[Client Browser]
      │
      │ 차트 업데이트
```

---

### 2. 주문 처리 플로우 (동시성 제어)
```
[Client] POST /api/orders
      ↓
[OrderController]
      ↓
[OrderService] ──┐
      ↓          │ 1. 잔고 확인 (Redis Cache)
[Redis]          │
      ↓          │
[OrderService] ◄─┘
      ↓
[Redis Lock] ← 분산 락 획득 (동시성 제어)
      ↓
[MySQL Transaction 시작]
      │
      ├─ User 잔고 차감
      ├─ Order 레코드 생성
      ├─ Portfolio 업데이트
      │
[MySQL Transaction 커밋]
      ↓
[Redis Lock 해제]
      ↓
[WebSocket] 체결 알림 전송
      ↓
[Client] 알림 수신
```

---

### 3. 읽기 트래픽 분산 (Read Replica)
```
[Client] GET /api/portfolio
      ↓
[PortfolioController]
      ↓
[PortfolioService]
      │
      ├─ Write 작업 → Master DB
      └─ Read 작업 → Replica DB (부하 분산)
```

**설정 예시 (application.yml):**
```yaml
spring:
  datasource:
    master:
      url: jdbc:mysql://master.rds.amazonaws.com:3306/trading
    replica:
      url: jdbc:mysql://replica.rds.amazonaws.com:3306/trading
```

---

## 🚀 배포 아키텍처

### CI/CD 파이프라인 (GitHub Actions)
```
[Developer] git push
      ↓
[GitHub Actions]
      │
      ├─ 1. Build (Gradle)
      ├─ 2. Test (JUnit)
      ├─ 3. Docker Image Build
      ├─ 4. Push to ECR (AWS Container Registry)
      ↓
[AWS EC2]
      │
      ├─ Docker Pull from ECR
      ├─ Docker Container 재시작
      ├─ Health Check
      └─ ALB 트래픽 전환 (Blue-Green 배포)
```

---

## 📈 Auto Scaling 전략

### Scale-Out 조건
- CPU 사용률 > 70% (5분 이상 유지)
- 평균 응답 시간 > 500ms (3분 이상 유지)

### Scale-In 조건
- CPU 사용률 < 30% (10분 이상 유지)

### 최소/최대 인스턴스 (2025.01.29 수정)
- Min: **1대** (프리티어 고려, 초기에는 단일 서버로 충분)
- Max: 3대 (부하 테스트 후 필요시 조정)

> **변경 사유:** 처음부터 2대는 과한 설계. 실제 트래픽 확인 후 스케일링 적용하는 게 현실적.

---

## 🔒 보안 설계

### 1. 인증/인가 (JWT)
```
[Client] POST /api/auth/login
      ↓
[AuthController]
      ↓
[UserService] 비밀번호 검증 (BCrypt)
      ↓
[JwtTokenProvider] Access Token 생성 (30분)
      ↓
[Client] Header에 Token 저장
      ↓
[모든 요청] Authorization: Bearer {token}
      ↓
[JwtAuthenticationFilter] 토큰 검증
      ↓
[Controller] 요청 처리
```

### 2. Rate Limiting
- IP당 API 호출: **100회/분**
- Redis로 카운트 관리

### 3. SQL Injection 방지
- JPA/PreparedStatement 사용
- 사용자 입력 검증 (@Validated)

---

## 📊 모니터링 대시보드 (CloudWatch)

### 주요 메트릭
1. **애플리케이션**
   - API 응답 시간 (평균/P95/P99)
   - 에러 발생률 (5xx, 4xx)
   - 활성 WebSocket 연결 수

2. **인프라**
   - EC2 CPU/Memory 사용률
   - RDS 커넥션 풀 사용률
   - Redis 히트율

3. **비즈니스**
   - 신규 회원 가입 수
   - 주문 체결 건수 (시간당)
   - 평균 거래 금액

### 알람 설정
- CPU 사용률 > 80% → Slack 알림
- 에러율 > 5% → 이메일 알림
- RDS 커넥션 > 90% → 긴급 알림

---

## 🔧 트러블슈팅 예상 시나리오

### 1. WebSocket 연결 끊김
**원인:** ALB Idle Timeout (기본 60초)  
**해결:** Heartbeat 메시지 전송 (30초마다)

### 2. Redis 메모리 부족
**원인:** TTL 설정 누락  
**해결:** 모든 캐시에 TTL 설정, maxmemory-policy 설정

### 3. DB 커넥션 풀 고갈
**원인:** 트랜잭션 미종료  
**해결:** @Transactional 적절히 사용, 커넥션 풀 크기 조정

---

## 📝 변경 이력

| 날짜 | 내용 | 작성자 |
|------|------|--------|
| 2025-01-28 | 초안 작성 | N-78-bot |
| 2025-01-29 | AWS 인스턴스 최소 1대로 조정, Multi-AZ 제외 (비용 고려) | N-78-bot |

---

**다음 단계:**
- [ ] ERD 상세 설계
- [ ] API 명세서 작성 (Swagger)
- [ ] Docker Compose 작성
- [ ] Spring Boot 프로젝트 생성