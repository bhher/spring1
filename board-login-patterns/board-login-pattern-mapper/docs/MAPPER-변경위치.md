# Mapper 방식 — 무엇을 어디서 바꿨는지 (`board-login-pattern-mapper`)

이 문서는 **생성자 모듈**(`board-login-pattern-constructor`)과 비교했을 때, **Mapper 모듈**에서 **DTO ↔ Entity 변환**을 어디로 빼고 어디는 그대로 뒀는지 정리합니다.

비교 기준: 같은 기능(회원가입, 프로필 DTO, 게시글 작성)이지만 변환 코드의 **위치**만 다릅니다.

---

## 1. 새로 만든 파일 (Mapper 전용)

| 파일 | 역할 |
|------|------|
| `src/main/java/com/example/boardlogin/mapper/support/UserMapper.java` | `RegisterForm` → 신규 `User`, `User` → `UserProfileDto` |
| `src/main/java/com/example/boardlogin/mapper/support/BoardMapper.java` | `BoardForm` + `User` + `LocalDateTime` → 신규 `Board` |

- 두 클래스 모두 **`public final class`** + **`private` 생성자** → 인스턴스화 방지, **`static` 메서드**만 사용하는 유틸 형태입니다.

---

## 2. `UserService`에서 바꾼 곳

**파일:** `src/main/java/com/example/boardlogin/mapper/service/UserService.java`

| 메서드 | 생성자 모듈 (이전 패턴) | Mapper 모듈 (변경 후) |
|--------|-------------------------|-------------------------|
| `register` | `userRepository.save(new User(form.getUsername(), form.getPassword(), form.getName()));` | `userRepository.save(UserMapper.toNewEntity(form));` |
| `loadProfile` | `return new UserProfileDto(user.getUsername(), user.getName());` | `return UserMapper.toProfileDto(user);` |

추가된 import:

- `import com.example.boardlogin.mapper.support.UserMapper;`

**그대로 둔 부분**

- `login`, `findByUsername` — DTO 변환이 아니라 **조회·비밀번호 비교**만 하므로 Mapper로 옮기지 않았습니다.

---

## 3. `BoardService`에서 바꾼 곳

**파일:** `src/main/java/com/example/boardlogin/mapper/service/BoardService.java`

| 메서드 | 생성자 모듈 (이전 패턴) | Mapper 모듈 (변경 후) |
|--------|-------------------------|-------------------------|
| `create` | `Board board = new Board(form.getTitle(), form.getContent(), author, LocalDateTime.now());` | `Board board = BoardMapper.toNewEntity(form, author, LocalDateTime.now());` |

추가된 import:

- `import com.example.boardlogin.mapper.support.BoardMapper;`

**그대로 둔 부분**

- `findAll`, `findById`, `delete` — 변환 없이 **리포지토리·권한·삭제**만 담당.
- `update` — 여전히 서비스 안에서 `board.update(form.getTitle(), form.getContent())` 로 **기존 엔티티에 필드 반영**. (이 샘플에서는 “수정용 DTO → 엔티티”를 별도 `BoardMapper.applyUpdate(...)` 로 빼지 않았음. 필요하면 같은 `support` 패키지에 메서드 추가하면 됩니다.)

---

## 4. 바꾸지 않은 계층 (다른 모듈과 동일)

아래는 Mapper 도입과 **무관**하게 다른 패턴 모듈과 같은 구조입니다.

- `domain/User.java`, `domain/Board.java`
- `repository/*Repository.java`
- `web/*Controller.java`, `web/dto/*.java`, `LoginCheckInterceptor`, `WebConfig`
- `application.properties` (DB 이름·포트만 Mapper 모듈용 값)

---

## 5. 한 줄 요약

- **바꾼 것:** “`new User(...)`, `new UserProfileDto(...)`, `new Board(...)`”처럼 **폼/DTO와 엔티티를 오가는 코드**를 `UserMapper`, `BoardMapper` **static 메서드**로 옮김.  
- **안 바꾼 것:** 트랜잭션, 중복 아이디 검사, 작성자 검증, `board.update(...)`, 컨트롤러·리포지토리.

---

## 6. 폴더 구조에서 보는 위치

```text
board-login-pattern-mapper/
└── src/main/java/com/example/boardlogin/mapper/
    ├── support/           ← 여기가 Mapper (변환 전용)
    │   ├── UserMapper.java
    │   └── BoardMapper.java
    └── service/           ← 여기서 Mapper 호출로 “짧아짐”
        ├── UserService.java
        └── BoardService.java
```
