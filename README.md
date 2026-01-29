# Real-Time Stock Trading System
> 실시간 주식/코인 모의투자 시스템 - 대용량 트래픽 처리 학습 프로젝트

## 📋 프로젝트 개요
- **목표**: 대용량 실시간 트래픽 처리 경험 습득
- **기간**: 2026.01.26 ~ 2026.04.15 (약 10주)
- **최종 기술 스택**: Spring Boot, MySQL, Redis, Kubernetes(EKS)

---

## 🗓️ 기획 과정 타임라인

### 2026.01.26 (일) - 프로젝트 구상
**작업 내용:**
- 프로젝트 아이디어 도출
  * 실시간 주식/코인 모의투자 시스템
  * 대용량 트래픽 처리 학습 목표
- 초기 요구사항 정의 시작
  * 기능 요구사항: 회원가입, 실시간 시세, 주문 처리
  * 비기능 요구사항: 대용량 처리, 실시간성
- **개발 기간: 2주로 계획** (너무 짧다는 생각 들었음)

**당시 기술 스택 초안:**
- Backend: Spring Boot
- Database: MySQL
- Cache: Redis
- Infrastructure: AWS EC2, RDS
- DevOps: Docker, GitHub Actions

---

### 2026.01.27 (월) - 1차 검토 및 수정
**고민 사항:**
- 2주는 너무 짧다 → 학습 시간 부족
- K8s를 배워야 하나? (대용량 트래픽 처리 목표)
- AWS 비용 문제

**수정 내용:**
- **개발 기간 재조정**: 2주 → 약 2개월로 확대 고려
- 기술 스택 재검토 시작
  * Docker만으로 충분한가?
  * Kubernetes 필요성 검토
- 아키텍처 설계 초안 작성

**당시 결론:**
- 일단 Docker 기반으로 계획
- K8s는 추후 고려

---

### 2026.01.28 (화) - K8s 추가 결정
**중요한 결정:**
- **Kubernetes(EKS) 추가 결정!**
  * 이유 1: 대용량 트래픽 처리가 프로젝트 핵심 목표
  * 이유 2: Auto Scaling 실습 기회
  * 이유 3: 취업 시장에서 K8s 수요 높음
- **개발 기간 최종 확정**: 10주
  * Week 1-2: Docker + K8s 기초 학습 (minikube)
  * Week 3-8: 핵심 기능 개발
  * Week 9-10: EKS 배포 + 성능 테스트

**기술 스택 최종 수정:**
- DevOps에 Kubernetes, Helm, ECR 추가
- 아키텍처를 K8s 기반으로 전면 재설계
- AWS 비용 재산정: 월 약 $158

**작업 내용:**
- tech-stack.md 대폭 수정 (K8s 관련 내용 추가)
- architecture.md 재작성 (EKS 기반 구조로)
- AWS 인프라 계획 현실화
  * EKS 워커 노드 최소 1대로 시작
  * RDS Multi-AZ 제외 (비용 절감)

---

### 2026.01.29 (수) - 최종 확정 및 업로드
**작업 내용:**
- 10주 학습 로드맵 작성 완료 (learning-plan.md)
  * 주차별 세부 계획
  * 학습 방법론
  * 체크리스트
- 모든 기획 문서 최종 검토
- **GitHub Repository 생성**
- **기획 문서 일괄 업로드**

**최종 기획 문서:**
1. `requirements.md`: 요구사항 정의서 (10주 개발 계획)
2. `tech-stack.md`: 기술 스택 (K8s 포함)
3. `architecture.md`: K8s 기반 시스템 아키텍처
4. `learning-plan.md`: 10주 학습 로드맵

---

## 📂 문서 구조
```
docs/
├── 00-planning/          # 프로젝트 기획
│   ├── requirements.md   # 요구사항 정의서
│   ├── tech-stack.md     # 기술 스택 선정
│   ├── architecture.md   # 시스템 아키텍처
│   └── learning-plan.md  # 10주 학습 계획
├── 01-daily-logs/        # 일일 작업 기록
├── 02-troubleshooting/   # 문제 해결 과정
├── 03-performance/       # 성능 측정 결과
└── 04-api-docs/          # API 문서
```

---

## 🛠️ 최종 기술 스택

### Backend
- Spring Boot 3.x
- Spring Data JPA / Hibernate
- Spring Security + JWT

### Database
- MySQL 8.0
- Redis 7.x (캐싱, 실시간 데이터)

### Infrastructure
- AWS EKS (Kubernetes)
- AWS RDS (MySQL)
- AWS ElastiCache (Redis)
- AWS ECR (Docker 이미지 저장소)

### DevOps
- Docker
- Kubernetes + Helm
- GitHub Actions (CI/CD)
- Prometheus + Grafana (모니터링)

### 개발 환경
- minikube (로컬 K8s 학습)
- kubectl, helm
- IntelliJ IDEA

---

## 📚 학습 목표
1. **대용량 트래픽 처리**: Redis 캐싱, DB 최적화, Connection Pool
2. **Kubernetes 운영**: EKS 배포, HPA, 로깅/모니터링
3. **시스템 설계**: 확장 가능한 아키텍처 설계 경험
4. **성능 최적화**: JMeter 부하 테스트 및 튜닝

---

## 📝 기획 변경 이력

| 날짜 | 항목 | 변경 내용 |
|------|------|-----------|
| 01.26 | 개발 기간 | 2주 계획 |
| 01.27 | 개발 기간 | 2주 → 약 2개월 검토 시작 |
| 01.28 | 기술 스택 | Kubernetes(EKS) 추가 결정 |
| 01.28 | 개발 기간 | 최종 10주 확정 |
| 01.28 | 아키텍처 | K8s 기반으로 전면 재설계 |
| 01.29 | 학습 계획 | 10주 로드맵 작성 완료 |

---

## ⚠️ Note
- 2026.01.26~28: 로컬에서 기획 작업 (수정/보완 과정)
- 2026.01.29: Repository 생성 및 최종 기획 문서 업로드
- 커밋 히스토리는 업로드 시점(01.29) 기준
- 실제 기획 변경 과정은 위 타임라인 참조
