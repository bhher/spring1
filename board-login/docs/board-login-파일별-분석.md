# board-login — 파일별 분석 가이드

**프로젝트 경로:** `d:\spring1\board-login`  
**인증 방식:** 세션 기반 (`HttpSession`). **Spring Security는 사용하지 않습니다.** (인터셉터 + 서비스 검증)

---

## 읽는 순서 (추천)

1. `pom.xml` → 의존성 파악  
2. `BoardLoginApplication.java` → 진입점  
3. `application.properties` → DB·JPA·포트  
4. `domain/User.java`, `domain/Board.java` → 데이터 모델  
5. `repository/*` → DB 접근  
6. `service/*` → 비즈니스 규칙  
7. `config/WebConfig.java` → 인터셉터 등록  
8. `web/LoginCheckInterceptor.java` → 로그인 강제 URL  
9. `web/dto/*` → 폼·검증  
10. `web/AuthController.java`, `HomeController.java`, `BoardController.java` → URL·세션 연동  
11. `templates/**/*.html` → 화면  
12. `BoardLoginApplicationTests.java`, `Spring-Boot-게시판-로그인-실무교안.md`

---

## 0) 프로젝트 루트 (빌드·도구)

| 파일 | 역할 |
|------|------|
| `pom.xml` | Maven 의존성: `web`, `thymeleaf`, `data-jpa`, `validation`, `h2`, `test` 등. **`spring-security` 없음.** |
| `mvnw`, `mvnw.cmd`, `.mvn/wrapper/*` | Maven Wrapper — 로컬 Maven 없이 `./mvnw`로 빌드·실행 |
| `.gitignore` | Git 제외 목록 |

---

## 1) 애플리케이션 진입점

| 파일 | 역할 |
|------|------|
| `src/main/java/com/example/boardlogin/BoardLoginApplication.java` | `@SpringBootApplication` + `main`. 컴포넌트 스캔·내장 Tomcat 기동 |

---

## 2) 실행 환경

| 파일 | 역할 |
|------|------|
| `src/main/resources/application.properties` | `server.port=8080`, H2 인메모리 DB, `ddl-auto=create-drop`, H2 콘솔 `/h2-console`, `spring.jpa.open-in-view=false`, Thymeleaf 캐시 비활성(개발 편의) |

---

## 3) 도메인 (엔티티)

| 파일 | 역할 |
|------|------|
| `domain/User.java` | 테이블 `users`. `username`, `password`, `name`. 이 예제는 비밀번호 **평문 저장·비교** (학습용). |
| `domain/Board.java` | 테이블 `boards`. 제목·내용·작성일. `@ManyToOne`으로 **`User author`** (FK `author_id`). `update()`로 제목·내용 갱신 |

---

## 4) 리포지토리

| 파일 | 역할 |
|------|------|
| `repository/UserRepository.java` | `JpaRepository<User, Long>`. `findByUsername`, `existsByUsername` |
| `repository/BoardRepository.java` | `findAllWithAuthor`, `findByIdWithAuthor` — **`JOIN FETCH`** 로 작성자를 함께 조회 (`open-in-view=false`와 맞춤) |

---

## 5) 서비스

| 파일 | 역할 |
|------|------|
| `service/UserService.java` | `register`: 중복 아이디면 예외 후 저장. `login`: DB 비밀번호와 `equals`. `findByUsername`: 글쓰기 시 작성자 엔티티 조회 |
| `service/BoardService.java` | 목록·상세·생성. **`update` / `delete`에서 작성자와 현재 로그인 아이디 비교** — 인터셉터만으로는 막기 어려운 “타인 글 수정·삭제” 방지 |

---

## 6) 웹 전역 설정

| 파일 | 역할 |
|------|------|
| `config/WebConfig.java` | `LoginCheckInterceptor`를 **지정 URL 패턴**에만 등록 |

---

## 7) 웹 계층

| 파일 | 역할 |
|------|------|
| `web/dto/RegisterForm.java` | 회원가입 폼. `@NotBlank`, `@Size` 검증 |
| `web/dto/BoardForm.java` | 글쓰기·수정 폼. 제목·내용 검증 |
| `web/LoginCheckInterceptor.java` | `preHandle`: 세션에 `loginUser` 없으면 `/login`으로 **redirect**, `false`로 요청 중단 |
| `web/AuthController.java` | `/login` GET·POST, `/logout`, `/register` GET·POST. 로그인 성공 시 `session.setAttribute(loginUser, loginName)`. 상수 `SESSION_USER` |
| `web/HomeController.java` | `/` → `/posts` 리다이렉트. `/home`은 로그인 시에만, 아니면 `/login` |
| `web/BoardController.java` | `@RequestMapping("/posts")`. 목록·상세 공개. 쓰기/수정/삭제는 인터셉터 경로 + 서비스 작성자 검증. `canEdit`으로 UI용 플래그. `@GetMapping("/{id:\\d+}")` 로 **`/posts/write`와 숫자 id 충돌 방지** |

### 인터셉터가 막는 URL (`WebConfig`)

- `/posts/write` (GET/POST)
- `/posts/*/edit` (GET/POST)
- `/posts/*/delete` (POST)

목록·상세는 막지 않음 (비로그인도 열람 가능).

---

## 8) 템플릿 (Thymeleaf)

| 파일 | 역할 |
|------|------|
| `templates/login.html` | 로그인 폼, 오류·가입 완료 메시지 |
| `templates/register.html` | 회원가입 폼 |
| `templates/home.html` | 로그인 후 환영 — `session.loginName`, `session.loginUser` |
| `templates/board/list.html` | 목록, 로그인 여부에 따른 네비 |
| `templates/board/detail.html` | 상세 |
| `templates/board/write.html` | 글쓰기 |
| `templates/board/edit.html` | 수정 |

---

## 9) 테스트·교안

| 파일 | 역할 |
|------|------|
| `src/test/java/com/example/boardlogin/BoardLoginApplicationTests.java` | `contextLoads()` — 컨텍스트 기동 smoke 테스트 |
| `docs/Spring-Boot-게시판-로그인-실무교안.md` | 설계 의도·용어·흐름 정리 (Security 미사용 명시) |

---

## 한 줄 요약

**설정 → 엔티티 → 리포지토리 → 서비스(규칙) → WebConfig·인터셉터(로그인 URL) → 컨트롤러(세션 연동) → HTML** 순으로 읽으면 전체 흐름이 이어집니다.
