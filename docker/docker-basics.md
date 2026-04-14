# Docker 기반 이해 — 아키텍처, 흐름, 주요 용어

Docker를 사용하기 위해 필요한 **기본 아키텍처**, **동작 흐름**, **주요 용어**를 한 곳에서 정리한 문서입니다.

---

## 참고 링크 (상세 학습용)

| 분류 | 설명 | 링크 |
|------|------|------|
| 이해하기 | Docker 환경 설치 및 실행 방법 | [adjh54.tistory.com/350](https://adjh54.tistory.com/350) |
| 이해하기 | Docker 기초 이론 (아키텍처, 흐름, 주요 용어) | [adjh54.tistory.com/352](https://adjh54.tistory.com/352) |
| 이해하기 | Docker 컨테이너 라이프사이클 · CLI | [adjh54.tistory.com/359](https://adjh54.tistory.com/359) |
| 이해하기 | Dockerfile 이론 + Nginx 환경 구성 및 배포 | [adjh54.tistory.com/414](https://adjh54.tistory.com/414) |
| 이해하기 | Docker Compose 이해하고 구성 | [adjh54.tistory.com/503](https://adjh54.tistory.com/503) |
| 환경 구성 | Dockerfile + Vault 환경 구성 및 배포 | [adjh54.tistory.com/415](https://adjh54.tistory.com/415) |
| 환경 구성 | Dockerfile + React 환경 구성 및 배포 | [adjh54.tistory.com/417](https://adjh54.tistory.com/417) |
| 환경 구성 | Dockerfile + Spring Boot 환경 구성 및 배포 | [adjh54.tistory.com/420](https://adjh54.tistory.com/420) |
| 환경 구성 | Dockerfile + Redis 환경 구성 및 배포 | [adjh54.tistory.com/449](https://adjh54.tistory.com/449) |
| 환경 구성 | Dockerfile + RabbitMQ 환경 구성 및 실행 | [adjh54.tistory.com/496](https://adjh54.tistory.com/496) |
| 환경 구성 | Docker Compose + RabbitMQ 노드 클러스터링 | [adjh54.tistory.com/517](https://adjh54.tistory.com/517) |
| 환경 구성 | Docker Compose + Apache Kafka, Kafka-UI | [adjh54.tistory.com/637](https://adjh54.tistory.com/637) |

**공식 문서**

- Docker 개요 · 아키텍처: [Docker — Get started / Overview](https://docs.docker.com/get-started/overview/)
- Dockerfile 레퍼런스: [Dockerfile reference](https://docs.docker.com/reference/dockerfile/)
- Docker 가격: [Docker Pricing](https://www.docker.com/pricing/)

**추가 참고 (VM vs Docker)**

- [ubackup.com — Docker vs VM](https://www.ubackup.com/enterprise-backup/docker-vs-vm.html)

---

## 1. Docker란

- **컨테이너화**를 사용하여 애플리케이션의 **배포·확장·관리**를 자동화할 수 있는 **오픈소스 플랫폼**입니다.
- **코드, 런타임, 시스템 도구, 라이브러리** 등 실행에 필요한 요소를 **가볍고 격리된 환경**에 담습니다.
- 애플리케이션과 **의존성(종속성)**을 **표준화된 단위**로 묶어, **다양한 환경에서 동일하게** 배포·실행하기 쉽게 합니다.

### 1-1. 컨테이너화(Containerization)

- 애플리케이션 실행에 필요한 구성 요소를 **가볍고 격리된 환경**으로 제공합니다.
- 코드, 런타임, 도구, 라이브러리 등을 포함해 **일관된 배포·실행**을 목표로 합니다.

### 1-2. 이름 유래

- 컨테이너 운송·물류의 **도크(Dock, 부두)** 에서 아이디어를 가져왔다는 설명이 흔합니다. 애플리케이션을 격리된 단위에 **“도킹”** 해서 배포·실행한다는 이미지와 맞닿아 있습니다.

### 1-3. Docker는 무료인가?

- **기본적으로 무료**로 사용할 수 있는 구성이 있습니다.
- 팀 기능, 추가 보안·레지스트리 옵션 등은 **유료 플랜**이 있을 수 있습니다. 최신 요금은 [Docker Pricing](https://www.docker.com/pricing/)에서 확인하는 것이 좋습니다.

---

## 2. Docker를 쓰는 목적

| 목적 | 설명 |
|------|------|
| 빠르고 일관된 애플리케이션 제공 | 로컬에서도 **표준화된 환경**으로 개발하고, **CI/CD** 파이프라인에 잘 맞습니다. |
| 효율적인 배포 | 앱과 의존성을 컨테이너 하나 단위로 묶어 **배포 자동화**, 환경 차이를 줄입니다. |
| 확장성 | 컨테이너를 **늘리거나 줄여** 성능·가용성을 조절합니다(**수평 확장**). |
| 격리된 환경 | 컨테이너마다 **격리**되어 서로 영향을 줄이고, 안정성·보안에 유리한 면이 있습니다. |
| 개발 환경의 일관성 | 팀원이 **같은 구성**을 쓰기 쉬워 빌드·테스트·디버깅이 수월해집니다. |

**조금 풀어 쓰면**

1. **일관된 제공** — 개발자가 로컬 컨테이너에서 작업하고, CI/CD에 옮기기 좋습니다.  
2. **효율적인 배포** — 컨테이너 단위로 패키징해 배포 프로세스를 자동화합니다.  
3. **확장성** — 인스턴스(컨테이너) 수를 조절해 부하에 대응합니다.  
4. **격리** — 프로세스·파일시스템 등이 **컨테이너 단위로 분리**됩니다(VM과 격리 강도는 다름).  
5. **환경 통일** — “내 PC에서는 됐는데 서버에서는…” 같은 문제를 줄입니다.

---

## 3. VM(Virtual Machine)과 Docker의 차이

### 3-1. 가상 머신(VM)

- 호스트 위에 **게스트 OS 전체**를 올려 실행합니다. 게스트마다 **자체 커널**을 가지며 **강한 겹리**가 가능합니다.
- 대신 **용량·시작 시간·오버헤드**가 상대적으로 큽니다.

### 3-2. Docker(컨테이너)

- 호스트 OS 위에서 **컨테이너** 단위로 애플리케이션과 라이브러리·의존성을 패키징합니다.
- 일반적으로 **호스트 커널을 공유**하므로 **가볍고 빠른 시작**에 유리합니다.
- 여러 컨테이너가 **리소스를 공유**하므로 효율적이지만, **VM만큼의 OS 단위 격리**는 아닙니다.

### 3-3. 언제 무엇을 쓰나

- **완전한 OS 격리**, **서로 다른 OS를 한 머신에서** — VM이 유리한 경우가 많습니다.  
- **앱 패키징·배포·스케일 아웃**에 초점 — Docker(컨테이너)가 많이 쓰입니다.

### 3-4. 비교 표

| 비교 요소 | 가상 머신 (VM) | Docker(컨테이너) |
|-----------|----------------|------------------|
| 구조 | 하이퍼바이저 위에 게스트 OS | 호스트 OS 위에서 컨테이너로 앱 실행 |
| 성능 | 하이퍼바이저 오버헤드 | 상대적으로 가벼운 편 |
| 시작 시간 | 상대적으로 김 | 수 초 내 기동이 일반적 |
| 자원 사용 | 게스트 OS마다 부담 | 커널·자원 공유로 효율적 |
| 확장성 | 하드웨어·VM 추가 등 | 컨테이너 복제로 수평 확장 |
| 격리성 | OS 단위로 강함 | 프로세스/네임스페이스 수준(상대적으로 VM보다 약할 수 있음) |
| 관리 | 게스트 OS까지 관리 | 이미지·컨테이너 중심으로 단순해지는 편 |

(출처 일부: [Docker vs VM 참고 글](https://www.ubackup.com/enterprise-backup/docker-vs-vm.html))

---

## 4. Docker 아키텍처와 흐름

Docker는 전형적인 **클라이언트–서버** 구조로 이해할 수 있습니다.

### 4-1. 구성 요소

| 구성 | 역할 |
|------|------|
| **Docker Client** | 사용자가 입력하는 `docker` **CLI**. 빌드, 실행, 이미지 pull 등 **명령을 보냄**. |
| **Docker Host** | **Docker Engine(데몬)** 이 도는 머신(로컬 PC, 서버 등). 컨테이너·이미지·네트워크 등을 실제로 관리. |
| **Docker Registry** | **이미지 저장소**. Docker Hub, 사설 레지스트리(Harbor, ECR 등)가 여기에 해당. |

- **Docker Daemon(도커 데몬)**  
  - 클라이언트 요청을 받아 **이미지·컨테이너·네트워크·볼륨** 등을 처리하는 **백그라운드 프로세스**입니다.  
  - Docker Desktop에서는 **Engine running** 상태로 엔진(데몬) 동작을 확인할 수 있습니다.

> 참고: “Docker Host = 가상머신”처럼 적힌 자료도 있으나, **로컬 PC에 Docker Desktop만 깔아도 그 PC가 Host**입니다. VM은 Host가 될 **수도 있는** 경우의 한 예입니다.

### 4-2. 흐름 (요약)

1. **Client → Host(Daemon)**  
   `docker run`, `docker build`, `docker pull` 등 명령 전달.
2. **Docker Host(Daemon)**  
   요청에 따라 컨테이너 생성·시작·중지·삭제, 이미지 관리.
3. **Host ↔ Registry**  
   이미지 **pull**(다운로드) / **push**(업로드), 태그 저장 등.
4. **결과**  
   컨테이너 로그·상태 등이 클라이언트(터미널)로 돌아옵니다.

공식 개요: [Docker overview](https://docs.docker.com/get-started/overview/#docker-architecture)

### 4-3. 자주 쓰는 Docker 명령 (CLI)

| 명령 | 설명 |
|------|------|
| `docker run` | 이미지를 기반으로 컨테이너 생성·실행 |
| `docker build` | Dockerfile로 이미지 빌드 |
| `docker pull` | 레지스트리에서 이미지 다운로드 |
| `docker push` | 이미지를 레지스트리에 업로드 |
| `docker ps` / `docker ps -a` | 실행 중 / 전체 컨테이너 목록 |
| `docker images` (또는 `docker image ls`) | 로컬 이미지 목록 |
| `docker tag` | 이미지에 이름·태그 지정 |
| `docker login` | 레지스트리 로그인 |
| `docker -v` | Docker 클라이언트 버전 확인 |

명령과 옵션은 버전에 따라 세부가 다를 수 있으므로, 최신 정보는 [CLI 문서](https://docs.docker.com/reference/cli/docker/)를 참고합니다.

---

## 5. Docker 주요 용어 요약표

| 용어 | 분류 | 설명 |
|------|------|------|
| Docker Client | 아키텍처 | CLI 등으로 명령을 보내는 **클라이언트**. |
| Docker Host | 아키텍처 | 엔진(데몬)이 동작하는 **호스트 머신**. |
| Docker Registry | 아키텍처 | 이미지를 **저장·배포**하는 저장소(Hub, GitHub Container Registry 등). |
| Docker Daemon | 구성 요소 | 요청 처리·컨테이너·이미지 관리를 하는 **백그라운드 프로세스**. |
| Docker Image | 구성 요소 | 컨테이너를 만들기 위한 **읽기 전용 템플릿**(레이어 구조). |
| Docker Container | 구성 요소 | 이미지를 실행한 **인스턴스**(가변 상태, 격리된 프로세스). |
| Docker Hub | 구성 요소 | Docker가 운영하는 **공개 레지스트리** 예시. |
| Dockerfile | 구성 요소 | 이미지 빌드 절차를 적은 **텍스트 파일**. |

---

## 6. 용어별 설명

### 6-1. Docker Client

- 터미널에서 `docker` 명령을 실행할 때 쓰는 **클라이언트 도구**입니다.  
- API/Daemon을 통해 Host와 통신하며, 컨테이너·이미지를 **생성·실행·삭제**합니다.

### 6-2. Docker Host (= Docker가 동작하는 서버·머신)

- **Docker Engine이 설치되어 데몬이 동작하는 시스템**을 가리키는 말로 쓰입니다.  
- 클라이언트 명령을 받아 컨테이너와 이미지를 관리합니다.

### 6-3. Docker Registry

- 이미지를 **보관·버전 관리·공유**하는 **중앙 저장소** 역할입니다.  
- 팀·회사는 **사설 레지스트리**를 둘 수도 있습니다.

### 6-4. Docker Daemon

- **Docker Engine의 핵심 프로세스**로, API 요청을 처리하고 컨테이너 생명주기와 리소스를 담당합니다.  
- Docker Desktop에서는 엔진이 켜져 있어야 명령이 정상 동작합니다.

### 6-5. Docker Image

- 컨테이너를 만들 **설계도·스냅샷**에 가깝고, **읽기 전용 레이어**가 쌓인 구조입니다.  
- Dockerfile로 빌드하거나, Registry에서 `pull` 받아 씁니다.

### 6-6. Docker Container

- **이미지를 실행한 인스턴스**입니다.  
- 파일 시스템·네트워크 등이 격리되어 앱을 독립적으로 실행합니다.

### 6-7. Docker Hub

- 널리 쓰이는 **공개 이미지 저장소** 중 하나입니다.  
- 공식 이미지·커뮤니티 이미지를 검색·다운로드할 수 있습니다.  
- 사이트: [https://hub.docker.com](https://hub.docker.com)

### 6-8. Dockerfile

- 이미지를 어떻게 만든지 **단계별로 적는 스크립트**입니다.  
- 베이스 이미지, 패키지 설치, 파일 복사, 실행 명령 등을 한 줄씩 작성합니다.  
- 참고: [Dockerfile reference](https://docs.docker.com/reference/dockerfile/)

---

## 7. 정리 한 줄

- **Image** = 실행 가능한 **꾸러미(템플릿)**  
- **Container** = 그 꾸러미로 띄운 **실행 중인 인스턴스**  
- **Registry** = 이미지를 **보관·공유**하는 저장소  
- **Client / Daemon(Host)** = **명령**과 **실제 실행**의 관계  

이 문서는 학습용 요약이며, 세부 동작은 **공식 문서**와 상단 **참고 링크**를 함께 보시면 좋습니다.
