# Real-Time Stock Trading System

업비트 실시간 시세를 WebSocket으로 수신하고 Kafka로 분산 처리하는 주식 거래 백엔드 시스템입니다.

## 구현 내용

**실시간 시세 파이프라인**

```
업비트 WebSocket → UpbitWebSocketClient
                       ↓ (수신)
              StockPricePublisher → Kafka("market.price.updates")
                                         ↓ (소비)
                              KafkaConsumerService
                                         ↓ (Redis Pub/Sub 발행)
                              Redis Channel("stock-price")
                                         ↓ (구독)
                              RedisMessageSubscriber
                                         ↓ (STOMP 브로드캐스트)
                              클라이언트 WebSocket(/topic/stock-price)
```

- 업비트 공개 API에서 BTC/ETH 실시간 시세 수신 (WebSocket)
- Kafka `market.price.updates` 토픽으로 이벤트 발행
- Redis Pub/Sub → STOMP WebSocket으로 전체 연결 클라이언트에 브로드캐스트

**거래 기능**
- 주문 생성/체결 (시장가·지정가)
- 계좌 잔고 관리, 포트폴리오 조회
- JWT 인증 (httpOnly 쿠키 기반)

## 기술 스택

| 분류 | 기술 |
|------|------|
| 언어 / 프레임워크 | Java 21, Spring Boot 3 |
| 실시간 통신 | WebSocket (Java-WebSocket 클라이언트), STOMP (서버→클라이언트) |
| 메시징 | Apache Kafka |
| 캐싱 / 분산 | Redis (Pub/Sub, 시세 분산 브로드캐스트) |
| 인증 | Spring Security, JWT (JJWT) |
| ORM | Spring Data JPA |
| 외부 API | 업비트 WebSocket API (wss://api.upbit.com/websocket/v1) |

## 프로젝트 구조

```
real-time-trading/src/main/java/com/trading/realtimetrading/
├── external/
│   ├── UpbitWebSocketClient.java   # 업비트 실시간 시세 수신
│   └── StockPricePublisher.java    # Redis Pub/Sub 발행
├── kafka/
│   ├── KafkaProducerService.java   # 시세 이벤트 발행
│   └── KafkaConsumerService.java   # 시세 이벤트 소비
├── websocket/
│   ├── WebSocketConfig.java        # STOMP 설정
│   └── RedisMessageSubscriber.java # Redis → STOMP 브로드캐스트
├── service/
│   ├── OrderService.java           # 주문 생성/체결
│   ├── AccountService.java         # 계좌 잔고
│   ├── PortfolioService.java       # 포트폴리오
│   └── StockService.java           # 종목 관리
├── security/
│   ├── JwtTokenProvider.java
│   └── JwtAuthenticationFilter.java
└── domain/
    ├── order/   ├── account/   ├── portfolio/   └── stock/
```

## 로컬 실행

```bash
# 인프라 (Kafka + Redis) 실행
docker-compose up -d

# 애플리케이션 실행
./gradlew :real-time-trading:bootRun
```

WebSocket 대시보드: `http://localhost:8080/dashboard.html`

## 설계 포인트

- **Redis Pub/Sub 분산 브로드캐스트**: 업비트 시세를 수신한 인스턴스가 Redis 채널에 발행하면, 모든 인스턴스의 STOMP 구독자가 수신 → 수평 확장 시에도 모든 클라이언트에게 시세 전달 보장
- **Kafka 중간 버퍼링**: 시세 수신과 클라이언트 전달 사이에 Kafka를 두어 처리 지연/장애 시 메시지 유실 방지
