# board-login-img vs board-login-img-security — 추가·변경 사항

`board-login-img-security`는 `board-login-img`를 복사한 뒤 **Spring Security**를 붙인 프로젝트입니다. 게시판·이미지 업로드·JPA 구조는 같고, **인증 방식과 보안 관련 설정·코드**가 바뀝니다.

---

## 1. 의존성 (`pom.xml`)

| 추가 항목 | 설명 |
|-----------|------|
| `spring-boot-starter-security` | 폼 로그인, 세션 기반 인증, `PasswordEncoder` 등 |
| `thymeleaf-extras-springsecurity6` | Thymeleaf에서 `sec:*` 네임스페이스 (`sec:authorize`, `sec:authentication` 등) |
| `spring-security-test` (test) | 보안 관련 테스트 지원 (선택적 사용) |

`artifactId` / 앱 이름은 `board-login-img-security`로 구분됩니다.

---

## 2. 새로 생긴 Java 코드

| 경로 | 역할 |
|------|------|
| `config/SecurityConfig.java` | `SecurityFilterChain`: URL별 접근 제어, 폼 로그인(`/login` POST는 스프링 시큐리티가 처리), 로그아웃(GET `/logout`), `PasswordEncoder`(BCrypt), `DaoAuthenticationProvider` |
| `security/LoginUserDetails.java` | `UserDetails` 구현 — 아이디·암호화된 비밀번호·표시 이름(`displayName`), `ROLE_USER` |
| `security/LoginUserDetailsService.java` | `UserDetailsService` — DB에서 사용자 조회 후 `LoginUserDetails`로 반환 |
| `security/SecurityUtils.java` | `SecurityContextHolder`로 로그인 여부·현재 사용자명 조회 헬퍼 |
| `BoardLoginImgSecurityApplication.java` | 진입점 (패키지 `com.example.boardloginimgsecurity`) |

---

## 3. 제거·대체된 부분

| board-login-img | board-login-img-security |
|-----------------|---------------------------|
| `LoginCheckInterceptor` + `WebConfig`에서 `/posts/write`, `/*/edit`, `/*/delete` 등록 | **삭제**. 동일 URL은 `SecurityConfig`의 `authorizeHttpRequests`로 제어 |
| `AuthController`의 **POST `/login`** (세션에 `loginUser` 저장) | **삭제**. 스프링 시큐리티 **폼 로그인**이 POST `/login` 처리 |
| `AuthController`의 **GET `/logout`** (세션 무효화) | **삭제**. 시큐리티 **logout** 설정으로 처리 |
| `UserService.login()` (평문 비밀번호 비교) | **삭제**. 인증은 `LoginUserDetailsService` + `PasswordEncoder` |

---

## 4. 수정된 기존 코드 (요지)

- **`UserService`**: `PasswordEncoder` 주입, 회원가입 시 비밀번호 **BCrypt 인코딩** 후 저장.
- **`AuthController`**: 로그인 폼(GET), 회원가입만 유지. 로그인 여부 확인은 `SecurityUtils` 등으로 처리.
- **`BoardController`**: `HttpSession` / `AuthController.SESSION_USER` 대신 **`SecurityUtils.currentUsername()`** 등으로 작성자 확인.
- **`HomeController`**: 세션 직접 검사 제거. `/home` 접근은 시큐리티에서 **authenticated**로 막음.
- **`WebConfig`**: **`/uploads/**` 리소스 핸들러만** 유지 (인터셉터 등록 없음).

---

## 5. 설정 (`application.properties`)

| 항목 | board-login-img | board-login-img-security |
|------|-----------------|---------------------------|
| 앱 이름 | `board-login-img` | `board-login-img-security` |
| 포트 | `8080` | `8082` (원본과 동시 실행 시 충돌 완화) |
| MySQL DB 이름 | `board_login_img` | `board_login_img_security` |
| 업로드 폴더 | `~/board-login-img-uploads/` | `~/board-login-img-security-uploads/` |

테스트용 `src/test/resources/application.properties`도 DB 메모리 이름·업로드 임시 경로가 security용으로 맞춰져 있습니다.

---

## 6. 뷰 (Thymeleaf)

- **`xmlns:sec="http://www.thymeleaf.org/extras/spring-security"`** 추가.
- **`sec:authorize="isAnonymous()"` / `isAuthenticated()`** 로 네비게이션 분기 (`session.loginUser` 대신).
- **`home.html`**: `sec:authentication`, `${#authentication.principal.displayName}` 로 이름·아이디 표시.
- **CSRF**: 로그인·회원가입·글 작성·수정·삭제 등 **POST 폼**에 `_csrf` hidden 필드 추가 (시큐리티 기본 CSRF 대응).

`board/detail.html`의 본문·이미지 갤러리 구조는 동일합니다.

---

## 7. 접근 제어 요약 (`SecurityConfig`)

- **공개**: `/`, `/login`, `/register`, `POST /register`, `/uploads/**`, 게시판 **GET** (`/posts`, `/posts/{id}` 등).
- **인증 필요**: `/home`, `/posts/write`, `/posts/*/edit`, **POST** `/posts/**` (등록·수정·삭제).
- **로그아웃**: GET `/logout` → 성공 시 `/posts`로 이동 (설정 기준).

---

## 8. 한 줄 요약

**board-login-img-security = board-login-img + Spring Security(폼 로그인·BCrypt·URL 권한·CSRF) − 세션 수동 로그인/인터셉터**, 패키지·DB·포트·업로드 경로는 별도 프로젝트로 겹치지 않게 조정된 버전입니다.
