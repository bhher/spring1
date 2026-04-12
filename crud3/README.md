# crud3

`crud2`와 동일한 도메인(DoIt: 제목·내용 CRUD)을 **백엔드는 Spring Boot REST API**, **프론트는 React(Vite)** 로 구성한 예제입니다. Thymeleaf는 사용하지 않습니다.

## 구조

| 구분 | 경로 | 설명 |
|------|------|------|
| API | `crud3/` (Gradle) | JPA, H2, `/api/doits` JSON API |
| UI | `crud3/frontend/` | React 18 + React Router + Vite |

## API (`http://localhost:8080`)

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/api/doits` | 전체 목록 |
| GET | `/api/doits/{num}` | 단건 |
| POST | `/api/doits` | 등록 (JSON: `title`, `content`) |
| PUT | `/api/doits/{num}` | 수정 |
| DELETE | `/api/doits/{num}` | 삭제 |

CORS: 개발 시 React(`http://localhost:5173`)에서 API 호출 가능하도록 설정됨.

## 실행 방법

### 1) 백엔드

```bash
cd e:\spring1\crud3
.\gradlew.bat bootRun
```

- H2 파일 DB: `~/test_crud3`
- H2 콘솔: `http://localhost:8080/h2-console` (JDBC URL은 `application.properties`와 동일하게)

### 2) 프론트엔드 (다른 터미널)

```bash
cd e:\spring1\crud3\frontend
npm install
npm run dev
```

브라우저: `http://localhost:5173/list`  
Vite가 `/api` 요청을 `http://localhost:8080`으로 프록시합니다.

## UI 경로 (crud2와 유사)

- `/list` — 목록
- `/list/{num}` — 상세
- `/list/{num}/edit` — 수정
- `/mains/add` — 작성

## 빌드

```bash
.\gradlew.bat test
cd frontend && npm run build
```

`frontend/dist`를 Spring `static`에 복사하면 한 포트로 서빙할 수 있으나, 이 저장소에서는 기본적으로 **개발 시 두 프로세스**로 나눕니다.
