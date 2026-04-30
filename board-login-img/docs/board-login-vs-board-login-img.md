# board-login vs board-login-img 차이점

같은 흐름의 **회원가입·세션 로그인·게시판 CRUD**를 공유하고, **board-login-img**는 그 위에 **이미지 첨부·디스크 저장·썸네일·MySQL**을 얹은 확장판입니다.

---

## 한눈에 비교

| 구분 | board-login | board-login-img |
|------|----------------|-----------------|
| 메인 클래스 | `BoardLoginApplication` (`com.example.boardlogin`) | `BoardLoginImgApplication` (`com.example.boardloginimg`) |
| DB (기본 실행) | H2 파일 `~/test_boardlogin`, H2 콘솔 사용 | MySQL `board_login_img` (로컬 `root`/`1234` 등 설정값) |
| H2 | 런타임 + `spring-boot-h2console` | **테스트 전용** (`test` 스코프), `src/test/resources/application.properties`에서 인메모리 H2 |
| 이미지 | 없음 | `BoardImage` 엔티티 + 디스크 저장 + Thumbnailator 썸네일 |
| 업로드 | — | `app.upload-dir`, multipart 크기 제한, `/uploads/**` 정적 매핑 |
| 의존성 특이 | H2 콘솔 스타터 | `mysql-connector-j`, `thumbnailator` |

---

## 빌드·의존성 (`pom.xml`)

- **공통**: Spring Boot Web, Thymeleaf, Data JPA, Validation, Test.
- **board-login만**: `com.h2database:h2` (runtime), `spring-boot-h2console`.
- **board-login-img만**: `mysql-connector-j` (runtime), `h2`는 **test** scope, `net.coobird:thumbnailator` (썸네일 생성).

---

## 도메인·영속성

### Board

- **board-login**: `Board`만 존재. `author`는 `@ManyToOne(fetch = LAZY, optional = false)`.
- **board-login-img**: `Board`에 `@OneToMany`로 **`images`** (`List<BoardImage>`) 추가, `cascade = ALL`, `orphanRemoval = true`. 작성자 매핑은 동일하게 LAZY.

### BoardImage (board-login-img 전용)

- 테이블 `board_images`: `originalName`, `savedName`, `filePath`, `board_id`.
- 썸네일 파일명 규칙: `getThumbnailSavedName()` → `"s_" + savedName`.

### BoardRepository

- **board-login**: `findAllWithAuthor()`, `findByIdWithAuthor(id)`.
- **board-login-img**: 목록은 동일하게 `findAllWithAuthor()`. 상세/수정/삭제용으로 **`findByIdWithAuthorAndImages(id)`** — `JOIN FETCH author`, `LEFT JOIN FETCH images` 로 이미지 컬렉션까지 한 트랜잭션에서 로딩 (`open-in-view=false` 대응).

---

## 서비스·업로드 (`BoardService`)

| 메서드 | board-login | board-login-img |
|--------|-------------|-----------------|
| `create` | `(title, content, author)` | `(title, content, author, files)` — `MultipartFile` 목록 |
| `update` | `(id, title, content, username)` | 동일 + `files` — **추가** 업로드만 (기존 이미지 유지) |
| `delete` | DB 행만 삭제 | 디스크의 원본·썸네일 파일 삭제 후 `boardRepository.delete` |

- **board-login-img**: `@PostConstruct`로 `app.upload-dir` 디렉터리 생성, UUID 파일명 저장, **Thumbnailator**로 `200×200` 썸네일(`s_` 접두) 생성, IOException 시 런타임 예외로 감쌈.

---

## 웹 계층

### `BoardForm`

- **board-login**: `title`, `content`만.
- **board-login-img**: 위 + **`List<MultipartFile> files`**.

### `BoardController`

- URL·권한(작성자만 수정/삭제)·인터셉터 적용 경로는 동일한 패턴.
- **차이**: 등록/수정 시 `boardService.create(..., form.getFiles())`, `update(..., form.getFiles())` 호출.

### `WebConfig`

- **board-login**: 로그인 인터셉터만 등록.
- **board-login-img**: 동일 인터셉터 + **`addResourceHandlers`**: `/uploads/**` → `app.upload-dir`의 절대 경로 URI로 매핑 (브라우저에서 `<img th:src="@{/uploads/...}">` 가능).

### `LoginCheckInterceptor`

- 동작은 동일(비로그인 시 `/login` 리다이렉트).
- **board-login** 소스 하단에 주석 처리된 예전 인터셉터 코드 블록이 남아 있음(실행에는 영향 없음). **board-login-img**는 해당 주석 없음.

---

## 설정 (`application.properties`)

### board-login

- `spring.h2.console.enabled=true`, `ddl-auto=create-drop` 등 H2 중심.

### board-login-img

- MySQL URL·계정·`driver-class-name`.
- `spring.jpa.hibernate.ddl-auto=create` (기본값이 **create-drop이 아님** — 운영/재기동 시 스키마·데이터 유지 방식이 다를 수 있음).
- `spring.servlet.multipart.max-file-size` / `max-request-size`.
- `app.upload-dir=${user.home}/board-login-img-uploads/` (실제 경로는 OS 사용자 홈 기준).

### 테스트 (`board-login-img`)

- `src/test/resources/application.properties`: 인메모리 H2 + `app.upload-dir`을 임시 디렉터리로 지정해 **MySQL 없이** 테스트 실행.

---

## 뷰 (Thymeleaf)

- **board-login**: 글쓰기/수정 폼은 일반 `method="post"` (기본 `application/x-www-form-urlencoded`).
- **board-login-img**: **`enctype="multipart/form-data"`**, 파일 input (`multiple`, `accept="image/*"`), 상세에 갤러리(원본·썸네일 `<img>`), 제목 등에 “이미지” 문구 차이.

---

## 정리

- **기능 범위**: 인증·게시판 흐름은 같고, **board-login-img = board-login + 이미지(메타 DB + 파일 시스템) + MySQL + 썸네일 + 정적 URL**.
- **실행 시**: board-login은 H2만 있으면 되고, board-login-img는 **MySQL 기동·DB 생성·계정**과 **`app.upload-dir` 쓰기 권한**이 필요합니다. 두 앱을 동시에 띄울 때는 **포트**(기본 둘 다 8080이면 충돌)도 맞춰야 합니다.
