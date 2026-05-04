# board-login-img-security — 실제 만드는 순서

`board-login-img`(또는 동일 구조의 게시판+이미지)를 기준으로 **Spring Security**를 붙이며 처음부터 만든다고 가정한 **권장 작업 순서**입니다. 앞 단계가 뒤 단계의 의존 관계를 맞춥니다.

---

## 0. 전제

- JDK 17, Maven(또는 Spring Initializr), MySQL 로컬 실행 가능
- 이미 **글 목록·상세·작성·수정·삭제 + 멀티파트 이미지 + Thymeleaf**가 있는 프로젝트를 복사해 두었거나, 동일 기능을 단계적으로 구현 중이라고 가정

---

## 1. 프로젝트 뼈대와 의존성 (`pom.xml`)

1. Spring Boot **4.0.x** 부모 POM, `artifactId`를 `board-login-img-security` 등으로 구분
2. 의존성 추가·유지  
   - `spring-boot-starter-web`, `thymeleaf`, `data-jpa`, `validation`  
   - `spring-boot-starter-security`  
   - `thymeleaf-extras-springsecurity6` (Thymeleaf에서 `sec:*`)  
   - `mysql-connector-j`, `thumbnailator`  
   - 테스트: `h2`, `spring-boot-starter-test`, `spring-security-test`

---

## 2. 설정 파일

1. **`src/main/resources/application.properties`**  
   - `server.port`, `spring.application.name`  
   - MySQL `datasource` (DB 이름은 원본과 겹치지 않게, 예: `board_login_img_security`)  
   - `spring.jpa.*` (`ddl-auto`, `open-in-view=false` 등)  
   - `spring.servlet.multipart.*`  
   - **`app.upload-dir`** (업로드 절대/홈 경로)
2. **`src/test/resources/application.properties`**  
   - H2 인메모리 + 테스트용 `app.upload-dir` (MySQL 없이 테스트)

---

## 3. 진입점

- **`BoardLoginImgSecurityApplication.java`** — `@SpringBootApplication`, `main`

---

## 4. 도메인 (JPA 엔티티)

순서는 참조 관계 기준입니다.

1. **`User`** — `username`, `password`(해시 저장), `name`
2. **`Board`** — `title`, `content`, `author`(ManyToOne `User`), `createdAt`, `images` 컬렉션
3. **`BoardImage`** — `originalName`, `savedName`, `filePath`, `board`(ManyToOne)

---

## 5. 리포지토리

1. **`UserRepository`** — `findByUsername`, `existsByUsername`
2. **`BoardRepository`** — `JpaRepository` + `findAllWithAuthor`, `findByIdWithAuthorAndImages` 등 **fetch join** 쿼리 (N+1 완화)

---

## 6. 업로드 URL (`WebConfig`)

- **`WebConfig`** implements `WebMvcConfigurer`  
- `app.upload-dir`를 절대 경로로 바꿔 **`/uploads/**`** 리소스 핸들러 등록  
- (구버전에서 쓰던 **인터셉터 로그인 검사**는 넣지 않음 → 이후 Security로 대체)

---

## 7. 게시판 서비스 (`BoardService`)

- `@PostConstruct`로 업로드 디렉터리 생성
- `findAll` / `findById` / `create` / `update` / `delete`
- 이미지: `MultipartFile` 저장, **Thumbnailator**로 썸네일, `BoardImage` 연관 저장·삭제 시 파일 삭제  
- **아직** `SecurityUtils` 없이도 되도록, 메서드 인자로 **작성자 검증에 필요한 값**(예: `currentUsername`)을 받도록 설계

---

## 8. 비밀번호 암호화 Bean → 회원 서비스

1. **`SecurityConfig`(또는 별도 `@Configuration`)** 에 **`PasswordEncoder`** 빈 — `BCryptPasswordEncoder`
2. **`UserService`**  
   - `UserRepository`, `PasswordEncoder` 주입  
   - `register`: 중복 아이디 검사 후 **`passwordEncoder.encode(password)`** 로 저장  
   - `findByUsername` (게시글 작성 시 작성자 조회용)

> 인증용 `DaoAuthenticationProvider`는 같은 `SecurityConfig`에서 이어서 구성합니다.

---

## 9. Spring Security — 사용자·인증

1. **`LoginUserDetails`** — `UserDetails` 구현 (`id`, `username`, `password`, `displayName`, `ROLE_USER`)
2. **`LoginUserDetailsService`** — `UserDetailsService`, DB → `LoginUserDetails` 변환
3. **`SecurityConfig`** (Spring Security **7** 기준)  
   - `DaoAuthenticationProvider(loginUserDetailsService)` + `setPasswordEncoder`  
   - `SecurityFilterChain`: URL별 `authorizeHttpRequests`, **폼 로그인**, **로그아웃**  
   - GET `/logout`: `PathPatternRequestMatcher.pathPattern(HttpMethod.GET, "/logout")` 등  
   - `GET /logout`은 `permitAll`에 포함
4. **`SecurityUtils`** — `SecurityContextHolder`로 로그인 여부·현재 사용자명

---

## 10. 웹 DTO

- **`RegisterForm`** — 가입 폼 + 검증 애너테이션
- **`BoardForm`** — 제목·본문·`List<MultipartFile> files`

---

## 11. 컨트롤러

1. **`AuthController`** — GET `/login`, GET·POST `/register` (로그인 POST·로그아웃 GET은 시큐리티가 처리하므로 **여기서 제거**)
2. **`BoardController`** — 세션 대신 **`SecurityUtils.currentUsername()`** + `UserService.findByUsername` 로 작성자 연결
3. **`HomeController`** — `/` → 리다이렉트, `/home`은 템플릿 (접근은 Security에서 `authenticated`)

---

## 12. Thymeleaf 뷰

1. 레이아웃·네비: **`xmlns:sec`**, `sec:authorize="isAnonymous()"` / `isAuthenticated()` 로 분기
2. **`home.html`** — `sec:authentication`, `principal.displayName` 등
3. **`login.html`**, **`register.html`**
4. **`board/list`, `detail`, `write`, `edit`** — 기존과 동일하게 유지하되, 수정·삭제 버튼은 `sec:authorize` 또는 `canEdit` 등과 맞출 것
5. **CSRF**: POST 폼에 **`th:action`**, **`_csrf`** hidden (시큐리티 기본)

---

## 13. 문서·검증

- **`docs/board-login-img-vs-board-login-img-security.md`** — 원본과 차이 정리
- `./mvnw.cmd compile` / `spring-boot:run`으로 동작 확인 (MySQL 기동, 포트·DB 이름 확인)

---

## 한 줄 요약

**의존성·properties → 엔티티·리포지토리 → WebConfig·BoardService → PasswordEncoder·UserService → Security(Details·Service·Config·Utils) → DTO·컨트롤러 → Thymeleaf(sec·CSRF)** 순으로 만들면 중간에 막히는 의존이 적습니다.
