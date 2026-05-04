# Setter 방식 — 무엇을 어디서 바꿨는지 (`board-login-pattern-setter`)

이 문서는 **생성자 모듈**(`board-login-pattern-constructor`)과 비교했을 때, **Setter 모듈**에서 **어디에 setter 조립/필드 변경**을 썼는지 정리합니다.

---

## 1. 엔티티 (`domain`) — 여기가 Setter 패턴의 핵심

### `User.java`

**파일:** `src/main/java/com/example/boardlogin/setter/domain/User.java`

| 구분 | 생성자 모듈 | Setter 모듈 (변경) |
|------|-------------|---------------------|
| 기본 생성자 | `protected User() {}` | **`public User() {}`** — 서비스가 **다른 패키지**에서 `new User()` 호출 가능해야 함 |
| 값 주입 | `public User(username, password, name)` 만 존재 | **인자 있는 생성자 없음** → `setUsername`, `setPassword`, `setName` 으로만 채움 |

학습용 주석에 “JPA용 기본 생성자”라고 적혀 있을 수 있으나, **패키지 밖 서비스에서 `new User()`** 를 쓰려면 실제 코드는 **`public` 무인자 생성자**입니다.

### `Board.java`

**파일:** `src/main/java/com/example/boardlogin/setter/domain/Board.java`

| 구분 | 생성자 모듈 | Setter 모듈 (변경) |
|------|-------------|---------------------|
| 기본 생성자 | `protected Board() {}` | **`public Board() {}`** — 이유는 `User` 와 동일 |
| 생성 시 값 | `new Board(title, content, author, createdAt)` | **`new Board()` 후** `setTitle`, `setContent`, `setAuthor`, `setCreatedAt` |
| 수정 시 값 | 도메인 메서드 `board.update(title, content)` | **`setTitle`, `setContent`** 로 직접 변경 (도메인 `update` 없음) |

---

## 2. DTO (`web/dto`) — 프로필 응답도 Setter 예시

### `UserProfileDto.java`

**파일:** `src/main/java/com/example/boardlogin/setter/web/dto/UserProfileDto.java`

| 구분 | 생성자 모듈 | Setter 모듈 (변경) |
|------|-------------|---------------------|
| 필드 | `private final` + 생성자로만 주입 | **`String` 필드 + Lombok `@Data`** → **setter/getter** 생성 |
| 의도 | 불변에 가깝게 응답 DTO | “setter로 조립” 교육용 |

---

## 3. `UserService` — 바꾼 곳

**파일:** `src/main/java/com/example/boardlogin/setter/service/UserService.java`

### `register`

| 생성자 모듈 | Setter 모듈 |
|-------------|-------------|
| `userRepository.save(new User(form.getUsername(), form.getPassword(), form.getName()));` | `User user = new User();` → `setUsername` / `setPassword` / `setName` → `save(user)` |

### `loadProfile`

| 생성자 모듈 | Setter 모듈 |
|-------------|-------------|
| `return new UserProfileDto(user.getUsername(), user.getName());` | `UserProfileDto dto = new UserProfileDto();` → `setUsername` / `setName` → `return dto` |

**그대로 둔 부분:** `login`, `findByUsername` (setter와 무관)

---

## 4. `BoardService` — 바꾼 곳

**파일:** `src/main/java/com/example/boardlogin/setter/service/BoardService.java`

### `create`

| 생성자 모듈 | Setter 모듈 |
|-------------|-------------|
| `new Board(form.getTitle(), form.getContent(), author, LocalDateTime.now())` | `new Board()` → `setTitle` / `setContent` / `setAuthor` / `setCreatedAt` |

### `update`

| 생성자 모듈 | Setter 모듈 |
|-------------|-------------|
| `board.update(form.getTitle(), form.getContent())` | `board.setTitle(form.getTitle());` `board.setContent(form.getContent());` |

**그대로 둔 부분:** `findAll`, `findById`, `delete` (조회·권한·삭제 로직 동일)

---

## 5. 바꾸지 않은 계층 (다른 모듈과 동일)

- `repository/*Repository.java`
- `web/*Controller.java`, `RegisterForm`, `BoardForm`(Lombok `@Data` — 폼 바인딩용, 생성자 모듈과 동일 스타일)
- `LoginCheckInterceptor`, `WebConfig`, `application.properties`(DB·포트만 setter 모듈 값)

---

## 6. 폴더 구조에서 보는 위치

```text
board-login-pattern-setter/
└── src/main/java/com/example/boardlogin/setter/
    ├── domain/                 ← 엔티티에 setter + public 무인자 생성자
    │   ├── User.java
    │   └── Board.java
    ├── service/                ← new 후 setter 연쇄 / 수정도 setter
    │   ├── UserService.java
    │   └── BoardService.java
    └── web/dto/
        └── UserProfileDto.java ← 응답 DTO도 setter 조립 예시
```

---

## 7. 한 줄 요약

- **바꾼 것:** 엔티티·일부 DTO에 **setter**를 두고, 서비스에서 **`new` + setter** 로 객체를 맞춤. 수정도 **`update()` 대신 setter** 로 표현.  
- **대가:** 서비스 패키지에서 `new User()`/`new Board()` 가 가능하도록 **무인자 생성자를 `public`** 으로 연 것이 포인트(실무에서는 보통 피함).
