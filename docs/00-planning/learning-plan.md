# 학습 로드맵 (Learning Plan)

## 📚 학습 목표

이 프로젝트는 단순히 "코드 짜서 배포"가 아니라, **각 기술을 왜 쓰는지 이해하고 설명할 수 있는 것**이 목표.

면접에서 "왜 Redis를 썼나요?", "동시성 문제를 어떻게 해결했나요?" 같은 질문에 제대로 답변할 수 있어야 함.

---

## 🗓️ 8주 학습 계획 (2025.01.28 ~ 2025.03.28)

### Week 1-2: 환경 구축 + 기초 학습

#### 학습 내용
- [ ] Docker 개념 (컨테이너, 이미지, 볼륨)
- [ ] **Kubernetes 기초 개념** (Pod, Deployment, Service, Namespace)
- [ ] **minikube 설치 및 사용법**
- [ ] **kubectl 기본 명령어** (get, describe, logs, exec)
- [ ] docker-compose 사용법
- [ ] Spring Boot 프로젝트 구조 복습
- [ ] MySQL 기본 쿼리, 인덱스 개념
- [ ] Redis 기본 명령어 (SET, GET, EXPIRE)
- [ ] Git 브랜치 전략 (main, develop, feature)

#### 실습 목표
- [ ] **minikube로 로컬 K8s 클러스터 실행**
- [ ] **첫 Pod 배포해보기** (nginx 예제)
- [ ] **Deployment, Service 작성 및 배포**
- [ ] Docker로 MySQL, Redis 로컬 환경 구축
- [ ] Spring Boot 프로젝트 생성 (Gradle)
- [ ] **Spring Boot 애플리케이션 Docker 이미지 빌드**
- [ ] MySQL 연결 테스트
- [ ] Redis 연결 테스트
- [ ] 간단한 User CRUD API 만들기

#### 산출물
- `Dockerfile`
- **`k8s/deployment.yaml`** (Spring Boot Deployment)
- **`k8s/service.yaml`** (ClusterIP Service)
- `docker-compose.yml` (로컬 테스트용)
- `build.gradle` 설정 파일
- User 도메인 코드
- 일일 로그 7~10개
- 트러블슈팅 문서 2~3개 (예: 포트 충돌, K8s Pod 실행 안 됨, 이미지 pull 실패 등)

#### 예상 에러
- Docker Desktop 설치 안 됨
- minikube start 실패 (Hyper-V/VirtualBox 충돌)
- kubectl 명령어 안 먹힘 (PATH 설정)
- Pod 상태가 ImagePullBackOff
- MySQL 포트 3306 이미 사용 중
- Spring Boot와 MySQL 연결 안 됨 (JDBC URL 오타)

---

### Week 3-4: 핵심 기능 개발 (인증/인가)

#### 학습 내용
- [ ] JWT (JSON Web Token) 개념과 구조
- [ ] Spring Security 기본 개념
- [ ] BCrypt 비밀번호 암호화
- [ ] JPA Entity 설계 원칙
- [ ] REST API 설계 (GET, POST, PUT, DELETE)
- [ ] 예외 처리 전략 (@ControllerAdvice)

#### 실습 목표
- [ ] 회원가입 API (POST /api/auth/signup)
- [ ] 로그인 API (POST /api/auth/login)
- [ ] JWT 토큰 발급 및 검증
- [ ] 입금/출금 API
- [ ] 보유 자산 조회 API

#### 산출물
- User Entity, Repository, Service, Controller
- JwtTokenProvider 클래스
- JwtAuthenticationFilter
- API 문서 (Postman Collection 또는 Swagger)
- Postman 테스트 스크린샷

#### 예상 에러
- JWT 토큰 만료 시간 설정 실수
- BCrypt 해싱 안 됨 (라이브러리 추가 안 함)
- CORS 에러 (프론트엔드 연동 시)

---

### Week 5-6: 실시간 기능 (WebSocket, Redis)

#### 학습 내용
- [ ] WebSocket vs HTTP 차이점
- [ ] STOMP 프로토콜 기본 개념
- [ ] Redis Pub/Sub 동작 원리
- [ ] 업비트 API 문서 읽기
- [ ] REST API vs WebSocket API 차이

#### 실습 목표
- [ ] WebSocket 연결 설정 (STOMP)
- [ ] 업비트 API로 실시간 시세 받기
- [ ] Redis Pub/Sub으로 시세 브로드캐스팅
- [ ] 클라이언트에서 WebSocket으로 시세 수신
- [ ] 간단한 차트 그리기 (Chart.js 사용)

#### 산출물
- WebSocketConfig 클래스
- UpbitApiClient 클래스
- Redis Pub/Sub 구현 코드
- WebSocket 연결 테스트 결과 (스크린샷)
- 실시간 시세 차트 HTML

#### 예상 에러
- WebSocket 연결 끊김 (ALB Timeout 설정 필요)
- 업비트 API Rate Limit 초과
- Redis Pub/Sub 메시지 안 받아짐 (채널 이름 오타)

---

### Week 7-8: 주문 기능 + 동시성 제어

#### 학습 내용
- [ ] 낙관적 락 vs 비관적 락
- [ ] Redis 분산 락 (Redisson)
- [ ] 데이터베이스 트랜잭션 격리 수준
- [ ] Race Condition이란?
- [ ] 동시성 테스트 방법 (JMeter, CountDownLatch)

#### 실습 목표
- [ ] 시장가 주문 API
- [ ] 지정가 주문 API
- [ ] 주문 취소 API
- [ ] Redis 분산 락으로 동시 주문 제어
- [ ] 동시 주문 시뮬레이션 (100명이 동시에 같은 종목 매수)

#### 산출물
- Order Entity, Service, Controller
- Redis Lock 구현 코드
- 동시성 테스트 코드 (JUnit)
- 성능 비교 문서 (락 사용 전 vs 후)

#### 예상 에러
- 잔고 부족인데 주문 성공 (동시성 미처리)
- 데드락 발생 (락 해제 안 함)
- 트랜잭션 롤백 안 됨 (@Transactional 누락)

---

### Week 9-10: EKS 배포 + 대용량 트래픽 처리

#### 학습 내용
- [ ] **AWS EKS 클러스터 생성** (eksctl 사용)
- [ ] **ECR (Container Registry) 사용법**
- [ ] **K8s Ingress Controller** (NGINX Ingress)
- [ ] **Horizontal Pod Autoscaler (HPA)** 설정
- [ ] **K8s ConfigMap, Secret** 활용
- [ ] **K8s Persistent Volume** (RDS, ElastiCache 연동)
- [ ] **부하 테스트** (JMeter, Locust)
- [ ] **CloudWatch Container Insights** 설정

#### 실습 목표
- [ ] EKS 클러스터 생성 (워커 노드 2대)
- [ ] Spring Boot 이미지를 ECR에 푸시
- [ ] Deployment를 EKS에 배포
- [ ] Ingress로 외부 노출 (ALB 연동)
- [ ] HPA로 Auto Scaling 설정 (CPU 70% 기준)
- [ ] 동시 접속자 10,000명 부하 테스트
- [ ] Pod가 자동으로 Scale Out/In 되는지 확인
- [ ] CloudWatch로 메트릭 모니터링

#### 산출물
- **`k8s/ingress.yaml`** (ALB Ingress)
- **`k8s/hpa.yaml`** (Horizontal Pod Autoscaler)
- **`k8s/configmap.yaml`**, **`k8s/secret.yaml`**
- **부하 테스트 보고서** (동시 접속 10,000명 처리 결과)
- **성능 개선 문서** (Scale Out 전후 비교)
- **CloudWatch 대시보드 스크린샷**
- **EKS 배포 가이드 문서**

#### 예상 에러
- eksctl 실행 안 됨 (AWS CLI 설정 오류)
- ECR 푸시 권한 에러
- Ingress 생성했는데 ALB 안 생김
- HPA가 동작 안 함 (metrics-server 설치 필요)
- Pod가 Pending 상태 (노드 리소스 부족)
- 비용 폭발 (노드 개수 제한 설정 필요)

#### Week 9-10 완료 조건
- [ ] EKS에 애플리케이션 배포 성공
- [ ] 외부에서 도메인으로 접속 가능
- [ ] 부하 테스트에서 동시 10,000명 처리 성공
- [ ] CPU 부하 증가 시 Pod 자동 증가 확인
- [ ] CloudWatch에서 메트릭 확인 가능

---

## 📊 학습 방법

### 1. 에러 기반 학습
- 에러 만남 → 구글링 → 해결 → 문서화
- 트러블슈팅 문서에 기록: 에러 메시지, 원인, 해결 방법

### 2. 코드 리뷰 (셀프)
- 매주 금요일: 이번 주 짠 코드 다시 보기
- "이 코드 왜 이렇게 짰지?" 스스로 질문
- 개선할 점 찾아서 리팩토링

### 3. 공식 문서 읽기
- Spring Boot 공식 문서
- Redis 공식 문서
- 업비트 API 문서
- "예제 코드 복붙" 금지 → 이해하고 직접 타이핑

### 4. 일일 로그 작성
- 매일 30분: 오늘 한 일, 배운 점, 내일 할 일
- 커밋만 보고도 어떤 작업 했는지 알 수 있게

---

## 🎯 주차별 체크리스트

### Week 1-2 완료 조건
- [ ] Docker 컨테이너 3개 정상 실행 (MySQL, Redis, Spring Boot)
- [ ] User CRUD API 동작
- [ ] 일일 로그 7개 이상 작성
- [ ] 트러블슈팅 문서 1개 이상

### Week 3-4 완료 조건
- [ ] Postman에서 회원가입/로그인 테스트 성공
- [ ] JWT 토큰으로 인증된 요청 성공
- [ ] 입출금 API 동작
- [ ] 단위 테스트 작성 (최소 5개)

### Week 5-6 완료 조건
- [ ] 브라우저에서 실시간 시세 확인 가능
- [ ] 1초마다 차트 업데이트 확인
- [ ] Redis에 시세 캐싱 확인 (redis-cli로 확인)
- [ ] WebSocket 연결 유지 (5분 이상)

### Week 7-8 완료 조건
- [ ] 동시에 100명이 주문해도 잔고 정확히 차감
- [ ] 주문 성공률 99% 이상
- [ ] 분산 락 적용 전후 성능 비교 문서 작성

---

## 💡 학습 팁

### DO
- ✅ 에러 만나면 로그 복사해서 저장
- ✅ 해결 과정 스크린샷 찍기
- ✅ 코드에 주석 달기 (나중에 볼 때 이해 쉽게)
- ✅ 커밋 메시지 상세하게 쓰기
- ✅ README에 실행 방법 정리

### DON'T
- ❌ AI가 준 코드 그대로 복붙
- ❌ 에러 무시하고 넘어가기
- ❌ 하루에 너무 많이 하려고 하기
- ❌ 커밋 안 하고 코드 계속 쌓기
- ❌ 문서화 미루기 ("나중에 쓰지" → 절대 안 씀)

---

## 📝 변경 이력

| 날짜 | 내용 | 작성자 |
|------|------|--------|
| 2025-01-29 | 10주 학습 로드맵 작성 (K8s/EKS 포함) | N-78-bot |

---

**다음 단계:**
- [ ] Week 1 학습 시작 (Docker 개념 공부)
- [ ] docker-compose.yml 작성
- [ ] 첫 번째 트러블슈팅 경험하기
