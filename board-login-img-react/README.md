# board-login-img-react

Spring Boot **REST API** + **MySQL** 백엔드와 **React(Vite)** 프론트엔드로, 로그인·회원가입·이미지 첨부 게시판을 제공합니다.

## 구조

- `backend/` — Spring Boot 4, JPA, 세션 쿠키 기반 인증, `/api/*` JSON API
- `frontend/` — Vite + React, 개발 시 프록시로 API·이미지를 백엔드(8080)에 연결

## 사전 준비

- JDK 17, Maven(또는 `backend/mvnw`)
- Node.js 20+ (프론트)
- MySQL (로컬), 계정은 `backend/src/main/resources/application.properties`에 맞춤 (기본 DB: `board_login_img_react`, `createDatabaseIfNotExist=true`)

## 실행 방법

**터미널 1 — 백엔드**

```powershell
cd d:\spring1\board-login-img-react\backend
.\mvnw.cmd spring-boot:run
```

**터미널 2 — 프론트**

```powershell
cd d:\spring1\board-login-img-react\frontend
npm install
npm run dev
```

브라우저에서 **http://localhost:5173** 을 엽니다. (API는 Vite가 `/api`, `/uploads`를 `http://localhost:8080`으로 넘깁니다.)

## API 요약

| 메서드 | 경로 | 설명 |
|--------|------|------|
| POST | `/api/auth/register` | 회원가입 (JSON) |
| POST | `/api/auth/login` | 로그인 (JSON) |
| POST | `/api/auth/logout` | 로그아웃 |
| GET | `/api/auth/me` | 현재 사용자 |
| GET | `/api/posts` | 목록 |
| GET | `/api/posts/{id}` | 상세 |
| POST | `/api/posts` | 글 등록 (multipart, 로그인 필요) |
| POST | `/api/posts/{id}/update` | 수정 (multipart, 로그인 필요) |
| DELETE | `/api/posts/{id}` | 삭제 (로그인 필요) |

업로드 이미지 URL: `/uploads/파일명` (프록시로 동일 출처에서 로드)
