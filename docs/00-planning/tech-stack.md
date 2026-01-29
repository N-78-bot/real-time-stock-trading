# 기술 스택 선정 (Tech Stack)

## 🎯 기술 스택 선정 원칙

1. **대용량 트래픽 처리**에 적합한 기술
2. **실무에서 많이 사용**되는 기술 (채용 시장 고려)
3. **AWS와의 호환성**이 좋은 기술
4. **학습 곡선**이 너무 가파르지 않은 기술

---

## 🛠️ 선정된 기술 스택

### Backend
| 기술 | 버전 | 선정 이유 |
|------|------|----------|
| **Java** | 17 | LTS 버전, Spring Boot와 호환성 최고 |
| **Spring Boot** | 3.2.x | 생산성 높고 레퍼런스 풍부 |
| **Spring Data JPA** | - | ORM으로 개발 속도 향상 |
| **Spring WebSocket** | - | 실시간 양방향 통신 지원 |
| **Spring Security** | - | JWT 인증 구현 용이 |

### Database
| 기술 | 버전 | 선정 이유 |
|------|------|----------|
| **MySQL** | 8.0 | RDS 지원, 트랜잭션 안정성 |
| **Redis** | 7.x | 시세 캐싱, 세션 관리, Pub/Sub |

### Infrastructure (AWS)
| 서비스 | 용도 | 선정 이유 |
|--------|------|----------|
| **EKS (Elastic Kubernetes Service)** | K8s 클러스터 관리 | 관리형 K8s, Auto Scaling 지원 |
| **EC2** | EKS 워커 노드 | t3.medium x 2~5대 (Auto Scaling) |
| **ECR (Elastic Container Registry)** | Docker 이미지 저장소 | EKS와 통합, private registry |
| **RDS (MySQL)** | 데이터베이스 | 관리형 서비스, Read Replica 지원 |
| **ElastiCache (Redis)** | 캐싱 서버 | 관리형 Redis, 클러스터 모드 |
| **S3** | 정적 파일, 로그 저장 | 저렴하고 안정적 |
| **CloudWatch** | 모니터링, 알람 | EKS Pod 메트릭 수집 |
| **ALB (Application Load Balancer)** | 로드 밸런서 | Ingress Controller와 연동 |
| **CloudFront** | CDN (선택) | 정적 파일 캐싱 |

### DevOps
| 기술 | 용도 | 선정 이유 |
|------|------|----------|
| **Docker** | 컨테이너화 | 환경 일관성, K8s Pod 기반 |
| **Kubernetes (EKS)** | 컨테이너 오케스트레이션 | Auto Scaling, Self-Healing, 무중단 배포 |
| **minikube** | 로컬 K8s 클러스터 | 개발 환경 (비용 절감) |
| **Helm** | K8s 패키지 관리 | MySQL, Redis 차트 활용 |
| **GitHub Actions** | CI/CD | K8s 배포 자동화, ECR 푸시 |
| **Gradle** | 빌드 도구 | Maven보다 빠름 |

> **K8s 도입 이유 (2025.01.29):**  
> - 대용량 트래픽 처리를 위한 Auto Scaling 필수  
> - Rolling Update로 무중단 배포 구현  
> - Pod 자동 복구 (Self-Healing)로 가용성 확보  
> - 실무 환경과 동일한 아키텍처 경험

### Monitoring & Testing
| 기술 | 용도 | 선정 이유 |
|------|------|----------|
| **JMeter** | 부하 테스트 | 무료, WebSocket 지원 |
| **JUnit 5** | 단위 테스트 | Spring Boot 기본 지원 |
| **Postman** | API 테스트 | 사용 편리 |

### External API
| API | 용도 | 선정 이유 |
|-----|------|----------|
| **업비트 API** | 실시간 코인 시세 | 무료, REST/WebSocket 지원 |

---

## 🤔 기술 선정 과정 (비교 분석)

### 1. Database: MySQL vs PostgreSQL

| 항목 | MySQL | PostgreSQL |
|------|-------|-----------|
| 성능 | 읽기 중심 워크로드에 유리 ✅ | 복잡한 쿼리에 유리 |
| RDS 지원 | ✅ 완벽 지원 | ✅ 지원 |
| 학습 곡선 | ✅ 낮음 | 중간 |
| 커뮤니티 | ✅ 매우 큼 | 큼 |

**선택: MySQL**
- 실시간 시세 조회는 "읽기 위주"
- RDS Read Replica 구성 용이
- 레퍼런스가 많아 트러블슈팅 쉬움

---

### 2. Cache: Redis vs Memcached

| 항목 | Redis | Memcached |
|------|-------|-----------|
| 데이터 타입 | 다양 (String, List, Set, Sorted Set) ✅ | Key-Value만 |
| Pub/Sub | ✅ 지원 | 미지원 |
| 영속성 | ✅ RDB/AOF 지원 | 미지원 |
| 성능 | 매우 빠름 | 약간 더 빠름 (단순 작업) |

**선택: Redis**
- Sorted Set으로 호가창 구현 가능
- Pub/Sub으로 실시간 시세 브로드캐스팅
- ElastiCache 지원

---

### 3. API: 업비트 vs 한국투자증권

| 항목 | 업비트 | 한국투자증권 |
|------|--------|-------------|
| API Key 발급 | ✅ 무료, 즉시 | 계좌 개설 필요 |
| WebSocket | ✅ 지원 | 지원 |
| 문서화 | ✅ 상세함 | 보통 |
| Rate Limit | 초당 10회 | 초당 20회 |

**선택: 업비트**
- 즉시 사용 가능
- 문서가 친절하고 예제 많음
- 코인 시세가 변동성 커서 테스트에 유리

---

### 4. CI/CD: GitHub Actions vs Jenkins

| 항목 | GitHub Actions | Jenkins |
|------|---------------|---------|
| 설치 | ✅ 불필요 (클라우드) | 별도 서버 필요 |
| 비용 | ✅ 무료 (Public Repo) | 서버 비용 |
| 학습 곡선 | ✅ 낮음 (YAML) | 높음 |
| GitHub 연동 | ✅ 네이티브 | 플러그인 필요 |

**선택: GitHub Actions**
- 별도 서버 불필요 (비용 절감)
- GitHub과 완벽 통합
- 2주 프로젝트에 적합

---

## 🚀 아키텍처 구성도 (Kubernetes 기반)
```
[Client (Browser)]
      ↓ HTTPS
[CloudFront (CDN) - Optional]
      ↓
[ALB (Application Load Balancer)]
      ↓
[AWS EKS Cluster]
      │
      ├─ [Ingress Controller (NGINX)]
      │        ↓
      ├─ [Deployment: Spring Boot]
      │   ├─ Pod 1 (Auto Scaling 2~5개)
      │   ├─ Pod 2
      │   └─ Pod 3
      │        ↓
      └─ [Service: ClusterIP]
                ↓
[ElastiCache (Redis)] ← 시세 캐싱, Pub/Sub
      ↓
[RDS MySQL]
  ├─ Master (Write)
  └─ Read Replica (Read)
      ↓
[S3] ← 로그, 정적 파일
      ↓
[CloudWatch] ← EKS Pod 메트릭, 로그
```

---

## 📊 예상 비용 (AWS EKS 기준)

| 서비스 | 월 예상 비용 |
|--------|-------------|
| **EKS 클러스터** | $72 (클러스터 자체) |
| **EC2 워커 노드 (t3.medium x 2)** | ~$60 |
| **RDS (db.t3.micro)** | $0 (프리티어) |
| **ElastiCache (cache.t3.micro)** | $0 (프리티어) |
| **ECR (이미지 저장)** | ~$1 |
| **S3 + CloudWatch** | ~$5 |
| **ALB** | ~$20 |
| **합계** | **~$158/월** |

> ⚠️ **비용 관리 전략:**  
> - 개발 초기: minikube로 로컬 학습 (비용 $0)  
> - 필요시에만 EKS 가동 (작업 안 할 땐 노드 0으로 축소)  
> - 프로젝트 종료 후 즉시 삭제  
> - 예상 총 비용: 2개월 기준 $100~200 (효율적 사용 시)

---

## 🔄 향후 개선 가능성

### Phase 2 (추후 추가 가능)
- [ ] **Kafka**로 이벤트 스트리밍
- [ ] **Elasticsearch + Kibana**로 로그 분석 및 시각화
- [ ] **Prometheus + Grafana**로 고급 모니터링 (K8s 메트릭)
- [ ] **Terraform**으로 IaC (Infrastructure as Code)
- [ ] **ArgoCD**로 GitOps 구현
- [ ] **Istio**로 Service Mesh (마이크로서비스 고도화)

> K8s는 이미 Phase 1에 포함되어 있으므로 제외

---

## 📝 변경 이력

| 날짜 | 내용 | 작성자 |
|------|------|--------|
| 2025-01-28 | 초안 작성 | N-78-bot |
| 2025-01-29 | Kubernetes (EKS) 메인 스택 추가, 아키텍처 K8s 기반으로 재설계 | N-78-bot |

---

**다음 단계:**
- [ ] 아키텍처 상세 설계 (architecture.md)
- [ ] ERD 설계
- [ ] API 명세서 작성