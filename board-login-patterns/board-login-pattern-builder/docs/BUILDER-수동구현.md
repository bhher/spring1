# Builder 패턴 — Lombok 없이 직접 구현 (`board-login-pattern-builder`)

> **현재 소스:** `User`, `Board` 엔티티는 **Lombok `@Builder`** 를 사용합니다. 아래 내용은 **수동 빌더와 동일한 호출 형태(`Xxx.builder()...build()`)의 원리**를 설명하는 자료입니다.

이 문서는 예전에 이 모듈에서 **Lombok `@Builder`를 주석 처리**하고, 컴파일러가 보는 것과 비슷한 형태로 **수동 Builder**를 적어 두었을 때의 구조를 정리합니다.

## 왜 직접 쓰나요?

- Lombok `@Builder`는 **애노테이션 프로세서**가 빌드 시점에 `UserBuilder` 같은 코드를 **생성**합니다. IDE에서 “소스”로는 안 보일 수 있습니다.
- 학습 목적으로는 **정적 중첩 클래스 + `build()`** 구조를 한 번 직접 적어 보는 것이 이해에 도움이 됩니다.

## 공통 구조 (Gang of Four 스타일)

1. **대상 클래스**에 `public static XxxBuilder builder()` — 빌더 생성 진입점  
2. **`public static class XxxBuilder`** — 필드를 복사해 두었다가  
3. 각 필드마다 **`필드명(값)`** 메서드가 **`this`(빌더)** 를 반환 → **메서드 체인**  
4. **`build()`** 에서 **`new 대상클래스(...)`** 한 번에 생성  

호출 예는 Lombok과 동일합니다.

```java
User user = User.builder()
        .username("hong")
        .password("secret")
        .name("홍길동")
        .build();
```

---

## 1. `User` 엔티티

**파일:** `src/main/java/com/example/boardlogin/builder/domain/User.java`

### JPA와 함께 쓸 때의 포인트

- **`@NoArgsConstructor(access = PROTECTED)`**  
  Hibernate 등이 프록시/리플렉션으로 객체를 만들 때 필요한 **기본 생성자**입니다. 외부에 `public`으로 열지 않는 것이 일반적입니다.
- **전 필드 `private` 생성자**  
  `UserBuilder.build()` 안에서만 `new User(id, username, password, name)` 을 호출합니다.  
  (패키지가 다르면 `private` 생성자는 빌더 클래스에서만 호출 가능합니다. 빌더는 `User` 안에 중첩되어 있으므로 가능합니다.)

### 수동 `UserBuilder` 요약

| 요소 | 역할 |
|------|------|
| `private Long id` 등 | `build()` 전까지 값을 쌓아 두는 임시 저장소 |
| `username(String)` … | 한 필드 세팅 후 `return this` 로 체인 |
| `build()` | `return new User(id, username, password, name);` |

신규 가입 시에는 보통 **`id`는 세팅하지 않음** → `null`이 들어가고, 저장 시 DB가 ID를 채웁니다.

---

## 2. `Board` 엔티티

**파일:** `src/main/java/com/example/boardlogin/builder/domain/Board.java`

`User`와 같은 패턴입니다.

- `protected Board()` — JPA용  
- `private Board(Long id, String title, String content, User author, LocalDateTime createdAt)` — 빌더 전용  
- `BoardBuilder` — `title`, `content`, `author`, `createdAt` 등 체인 후 `build()`

게시글 **수정**은 Builder가 아니라 도메인 메서드 **`update(String title, String content)`** 로 처리합니다. (생성과 변경 책임을 나누는 흔한 방식)

---

## 3. `UserProfileDto` (응답 DTO)

**파일:** `src/main/java/com/example/boardlogin/builder/web/dto/UserProfileDto.java`

- 필드가 `final` 이므로 **setter 없이** 생성자로만 주입합니다.
- **`private UserProfileDto(String username, String name)`** — 외부에서 임의 생성 방지  
- **`UserProfileDto`** 는 현재 Lombok `@Builder` 사용 (응답 DTO라 JPA 무인자 생성자 불필요)

엔티티와 달리 JPA 기본 생성자가 필요 없어서 **`@NoArgsConstructor` 없이** 둘 수 있습니다.

---

## 4. 서비스에서의 사용

**파일:**

- `service/UserService.java` — `User.builder()...`, `UserProfileDto.builder()...`
- `service/BoardService.java` — `Board.builder()...`

`User` / `Board` 엔티티는 **Lombok이 생성한 빌더**를 씁니다. DTO 쪽(`UserProfileDto` 등)도 Lombok `@Builder` 인 경우가 많습니다.

---

## Lombok `@Builder` 와 수동 빌더를 바꿀 때

1. 수동 `XxxBuilder` / `builder()` / `private` 전체 인자 생성자를 제거하고  
2. 클래스에 `@Builder` (엔티티는 `@NoArgsConstructor(PROTECTED)` + `@AllArgsConstructor(access = PRIVATE)` 조합이 흔함) 추가  

둘 중 하나만 쓰면 됩니다. **동시에 두면** 중복 정의로 컴파일 오류가 납니다.

---

## 한 줄 요약

**Builder = “값을 모았다가 마지막에 `build()` 한 번으로 객체를 만든다”**는 패턴이고, Lombok은 그 보일러플레이트를 생성해 줍니다. 엔티티 `User` / `Board`는 현재 **Lombok `@Builder`** 로 그 코드를 생성합니다.
