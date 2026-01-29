# Kubernetes 배포 가이드 (minikube)

## 📋 사전 준비

### 1. minikube 설치

**Windows (PowerShell 관리자 권한):**
```powershell
# Chocolatey로 설치 (추천)
choco install minikube

# 또는 직접 다운로드
# https://minikube.sigs.k8s.io/docs/start/
```

**설치 확인:**
```powershell
minikube version
kubectl version --client
```

---

## 🚀 배포 단계

### Step 1: minikube 시작

```powershell
# minikube 클러스터 시작 (Docker 드라이버 사용)
minikube start --driver=docker

# 상태 확인
minikube status

# kubectl 컨텍스트 확인
kubectl config current-context
# 결과: minikube
```

---

### Step 2: Spring Boot Docker 이미지 빌드

```powershell
# 프로젝트 루트 디렉토리에서
cd C:\Users\405\Desktop\real-time-stock-trading\real-time-trading

# minikube Docker 환경 사용 (중요!)
minikube docker-env | Invoke-Expression

# Docker 이미지 빌드
docker build -t trading-app:latest .

# 이미지 확인
docker images | findstr trading-app
```

---

### Step 3: MySQL, Redis 배포

```powershell
# k8s 디렉토리로 이동
cd k8s

# MySQL 배포
kubectl apply -f mysql-deployment.yaml

# Redis 배포
kubectl apply -f redis-deployment.yaml

# 배포 확인 (Running 상태 확인)
kubectl get pods

# 서비스 확인
kubectl get services
```

**MySQL Pod가 Running 될 때까지 대기 (약 1-2분):**
```powershell
kubectl get pods -w
# Ctrl+C로 중지
```

---

### Step 4: Spring Boot 애플리케이션 배포

```powershell
# Deployment 배포
kubectl apply -f deployment.yaml

# Service 배포
kubectl apply -f service.yaml

# 배포 상태 확인
kubectl get deployments
kubectl get pods
kubectl get services
```

**예상 결과:**
```
NAME                          READY   STATUS    RESTARTS   AGE
trading-app-xxxxxxxxx-xxxxx   1/1     Running   0          1m
trading-app-xxxxxxxxx-xxxxx   1/1     Running   0          1m
mysql-xxxxxxxxx-xxxxx         1/1     Running   0          2m
redis-xxxxxxxxx-xxxxx         1/1     Running   0          2m
```

---

### Step 5: 애플리케이션 접속

```powershell
# 서비스 URL 확인
minikube service trading-app-service --url

# 예: http://192.168.49.2:30080
```

**브라우저에서 접속:**
```
http://<minikube-ip>:30080/api/users
http://<minikube-ip>:30080/health/db
http://<minikube-ip>:30080/health/redis
```

---

## 🔍 유용한 명령어

### Pod 로그 확인
```powershell
# Pod 이름 확인
kubectl get pods

# 로그 보기
kubectl logs <pod-name>

# 실시간 로그
kubectl logs -f <pod-name>
```

### Pod 내부 접속
```powershell
# Shell 접속
kubectl exec -it <pod-name> -- /bin/sh

# MySQL 접속 테스트
kubectl exec -it <mysql-pod-name> -- mysql -utrading_user -ptrading1234 trading
```

### 리소스 상태 확인
```powershell
# 모든 리소스 확인
kubectl get all

# 특정 리소스 상세 정보
kubectl describe pod <pod-name>
kubectl describe service <service-name>
```

### 리소스 삭제
```powershell
# 개별 삭제
kubectl delete -f deployment.yaml
kubectl delete -f service.yaml

# 전체 삭제
kubectl delete -f mysql-deployment.yaml
kubectl delete -f redis-deployment.yaml
kubectl delete -f deployment.yaml
kubectl delete -f service.yaml
```

---

## 🔄 업데이트 배포

### 코드 수정 후 재배포
```powershell
# 1. Docker 이미지 재빌드
minikube docker-env | Invoke-Expression
docker build -t trading-app:latest .

# 2. Deployment 재시작 (이미지 재로드)
kubectl rollout restart deployment/trading-app

# 3. 배포 상태 확인
kubectl rollout status deployment/trading-app

# 4. Pod 재시작 확인
kubectl get pods
```

---

## ❌ 트러블슈팅

### 1. Pod가 Pending 상태
```powershell
# 원인 확인
kubectl describe pod <pod-name>

# 리소스 부족이면 minikube 재시작
minikube stop
minikube start --memory=4096 --cpus=2
```

### 2. ImagePullBackOff 에러
```powershell
# 원인: minikube Docker 환경 사용 안 함
# 해결: Step 2 다시 실행 (minikube docker-env)
minikube docker-env | Invoke-Expression
docker build -t trading-app:latest .
```

### 3. CrashLoopBackOff 에러
```powershell
# Pod 로그 확인
kubectl logs <pod-name>

# 주요 원인:
# - MySQL/Redis 연결 실패 → Service 이름 확인
# - application.yml 설정 오류
```

### 4. Service 접속 안 됨
```powershell
# minikube 터널 실행 (필요시)
minikube tunnel

# 또는 직접 포트 포워딩
kubectl port-forward service/trading-app-service 8080:8080
# 접속: http://localhost:8080
```

---

## 🧹 정리 (minikube 종료)

```powershell
# 클러스터 중지
minikube stop

# 클러스터 삭제 (데이터 모두 삭제)
minikube delete

# 다시 시작하려면
minikube start
```

---

## 📝 참고 사항

- **로컬 개발**: minikube + Docker Desktop
- **운영 환경**: AWS EKS (Week 9-10에 진행)
- **이미지 저장소**: 
  - 로컬: minikube 내부 Docker
  - 운영: AWS ECR

---

## 🎯 다음 단계

Week 1-2 완료 후:
- [ ] Week 3-4: JWT 인증/인가
- [ ] Week 5-6: WebSocket 실시간 기능
- [ ] Week 7-8: 주문 + 동시성 제어
- [ ] Week 9-10: AWS EKS 배포
