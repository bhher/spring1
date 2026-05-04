# `board-login` 과 `board-login-pattern-constructor` 비교

이 문서는 저장소 기준 아래 두 프로젝트를 비교합니다.

| 이름 | 경로 |
|------|------|
| **board-login** | `spring1/board-login/` |
| **board-login-pattern-constructor** | `spring1/board-login-patterns/board-login-pattern-constructor/` |

말씀하시는 “board-login-constructor”는 위 **pattern-constructor** 모듈을 가리킨다고 가정했습니다. (루트에 `board-login-constructor`라는 별도 폴더는 없습니다.)

---

## 1. 프로젝트 목적

| 항목 | board-login | board-login-pattern-constructor |
|------|----------------|----------------------------------|
| 목적 | 게시판 + 세션 로그인 **통합 예제 앱** | **생성자 방식**으로 DTO·엔티티를 맞추는 **학습용** 앱 (patterns 집합의 한 모듈) |
| 문서/배지 | 없음 | Thymeleaf에 `app.pattern-label=생성자` 등 **패턴 표시** |

---

## 2. 빌드·런타임 스택

| 항목 | board-login | board-login-pattern-constructor |
|------|----------------|----------------------------------|
| Spring Boot | **4.0.5** | **3.4.2** (부모 `board-login-patterns` POM) |
| Java | 17 | 17 |
| Lombok | **미사용** | **사용** (`RegisterForm`, `BoardForm`, `UserProfileDto` 등 `@Data` 등) |
| DB | **H2** (파일 `~/test_boardlogin`) + **H2 콘솔** | **MySQL** (`board_login_pattern_constructor`, 로컬 기준) |
| 기본 포트 | **8080** | **8102** |
| DDL | `create-drop` | `update` |
| SQL 로그 | `show-sql=true` | `show-sql=false` |

---

## 3. 기능 차이

| 기능 | board-login | board-login-pattern-constructor |
|------|----------------|----------------------------------|
| 회원가입 / 로그인 / 로그아웃 | 있음 | 있음 |
| 게시판 목록·상세·작성·수정·삭제 | 있음 | 있음 |
| **회원정보 페이지 `/profile`** | **없음** | **있음** (`ProfileController`, `UserProfileDto`) |
| 홈 `/home` | 있음 (로그인 후) | 있음 |

---

## 4. 인터셉터(로그인 필요 URL)

**board-login** — 글쓰기·수정·삭제만 보호 (`/home` 은 인터셉터에 없음. 다만 로그인 폼 리다이렉트 등은 컨트롤러에서 처리).

```text
/posts/write, /posts/*/edit, /posts/*/delete
```

**board-login-pattern-constructor** — 홈·프로필까지 보호.

```text
/home, /profile, /posts/write, /posts/*/edit, /posts/*/delete
```

---

## 5. 서비스 계층 API 차이

### 회원가입

| board-login | board-login-pattern-constructor |
|-------------|----------------------------------|
| `UserService.register(String username, String password, String name)` | `UserService.register(RegisterForm form)` |
| `AuthController`에서 `userService.register(form.getUsername(), form.getPassword(), form.getName())` | `userService.register(form)` |

엔티티 생성은 둘 다 **`new User(username, password, name)`** 형태의 **생성자**를 사용합니다. 차이는 **서비스가 문자열 3개를 받느냐, `RegisterForm`을 받느냐**입니다.

### 게시글 작성

| board-login | board-login-pattern-constructor |
|-------------|----------------------------------|
| `BoardService.create(String title, String content, User author)` | `BoardService.create(BoardForm form, User author)` |
| `BoardController`에서 `boardService.create(form.getTitle(), form.getContent(), author)` | `boardService.create(form, author)` |

게시글 엔티티 생성은 둘 다 **`new Board(title, content, author, LocalDateTime.now())`** 로 **생성자**를 씁니다.

### 회원 프로필

| board-login | board-login-pattern-constructor |
|-------------|----------------------------------|
| 해당 API 없음 | `UserService.loadProfile(String)` → `UserProfileDto` |

---

## 6. 엔티티·도메인 스타일

`User`, `Board`는 **둘 다** 대체로 동일한 패턴입니다.

- `User`: `protected` 기본 생성자 + **이름 있는 생성자**로 필드 채움, **setter 없음**(board-login 기준).
- `Board`: 생성자 + **`update(title, content)`** 로 수정.

즉 “생성자 vs setter” 교육 축에서 보면, **엔티티 자체는 둘 다 생성자 쪽**에 가깝고, **pattern-constructor**는 그 주변(서비스 인자로 DTO 사용, 프로필, 인터셉터, 스택)이 학습용으로 확장된 형태입니다.

---

## 7. DTO (폼)

| board-login | board-login-pattern-constructor |
|-------------|----------------------------------|
| `RegisterForm`, `BoardForm` — **수동 getter/setter** | 동일 역할이나 **Lombok `@Data`** |

---

## 8. 한 줄 요약

- **board-login**: H2·Boot 4·단일 앱, **프로필 없음**, 서비스는 **문자열/개별 인자** 위주 API.  
- **board-login-pattern-constructor**: MySQL·Boot 3.4·**프로필·인터셉터 범위 확장**, 서비스는 **`RegisterForm` / `BoardForm`** 을 받는 형태로 **경계가 조금 더 명확**하고, **patterns 실습 모듈**로 묶여 있음.

두 앱을 **동시에 띄울 때**는 포트(8080 vs 8102)와 DB(H2 vs MySQL)만 맞추면 됩니다.
