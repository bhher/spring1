# board-login-patterns — DTO·Entity 생성 방식 비교 실습

`board-login`(세션 로그인 + 게시판) 흐름을 기준으로, **동일 기능**을 네 가지 방식으로 나눈 **독립 실행 가능한** Spring Boot 3.4 / Java 17 모듈입니다.

| 모듈 | 포트 | DB 스키마(기본값) | 핵심 학습 |
|------|------|-------------------|-----------|
| `board-login-pattern-setter` | 8101 | `board_login_pattern_setter` | 엔티티 `setter`로 조립 — **실무 비권장 이유** 체감 |
| `board-login-pattern-constructor` | 8102 | `board_login_pattern_constructor` | `new Entity(...)` — **필수 값 한 번에** |
| `board-login-pattern-builder` | 8103 | `board_login_pattern_builder` | Lombok `@Builder` — **가독성·필드 증가** |
| `board-login-pattern-mapper` | 8104 | `board_login_pattern_mapper` | `UserMapper` / `BoardMapper` — **변환 책임 분리** |

## 실행 방법

1. MySQL 실행 후, 각 모듈 `application.properties`의 `spring.datasource.username` / `password`를 환경에 맞게 수정합니다. (`createDatabaseIfNotExist=true` 로 스키마는 자동 생성)
2. 루트에서:

```text
.\mvnw.cmd -pl board-login-pattern-setter spring-boot:run
```

다른 방식은 `-pl board-login-pattern-constructor` 등으로 바꿉니다.

3. 테스트(`mvn test`)는 **H2 인메모리**로 동작합니다 (`src/test/resources/application.properties`).

---

## 공통 개념 정리

### DTO → Entity 변환

- **요청 DTO**(예: `RegisterForm`, `BoardForm`)는 HTTP(또는 API) 경계에서 들어온 **검증된 입력 모델**입니다.
- **Entity**는 DB 영속성·도메인 규칙을 담습니다. DTO를 그대로 엔티티에 두면 계층이 섞입니다.
- 변환 시점에 **허용된 필드만** 엔티티로 옮기고, 비밀번호 해시·작성자 주입·작성 시각 등 **서버 전용 값**을 채웁니다.

### Entity → DTO 변환

- 화면/API에 **엔티티를 직접 노출**하면 Lazy 로딩·순환 참조·민감 필드(비밀번호) 노출 위험이 있습니다.
- **응답 DTO**(여기서는 `UserProfileDto`)로 필요한 필드만 복사합니다.

### Service 계층 책임

- **트랜잭션 경계**, **도메인 규칙**(중복 아이디, 작성자만 수정), **저장소 호출**을 담당합니다.
- DTO↔Entity 변환을 서비스 안에 두든 Mapper로 빼든, “**유스케이스 흐름**”이 읽히게 유지하는 것이 목표입니다.

### Mapper 분리 이유

- 변환 로직이 커지면 서비스가 길어지고, **같은 매핑을 여러 서비스에서 재사용**하기 어렵습니다.
- `UserMapper`, `BoardMapper`처럼 **변환만 모아두면** 테스트·가독성이 좋아집니다. (대규모에서는 MapStruct 등도 검토)

### Builder를 쓰는 이유

- 인자가 많거나 선택 필드가 늘면 `new A(a,b,c,d)`만으로는 **무엇이 무엇인지** 읽기 어렵습니다.
- `A.builder().title(...).author(...).build()` 형태는 **이름이 붙은 인자**처럼 읽혀 유지보수에 유리합니다.

### 생성자와 Builder의 차이

| 구분 | 생성자 | Builder (Lombok 등) |
|------|--------|----------------------|
| 읽기 | 인자 순서를 외워야 함 | 메서드 체인으로 의미가 드러남 |
| 선택 필드 | 오버로드/팩토리 난립 | 선택적으로 `.필드()` 호출 |
| JPA | 기본 생성자 + 인자 생성자 조합이 흔함 | `@NoArgsConstructor(PROTECTED)` + `@Builder` 패턴이 일반적 |

### Setter 방식의 단점

- **반쯤 만들어진 객체**에 순서대로 `set`하다 보면, 중간 상태가 유효하지 않은 채로 퍼질 수 있습니다.
- 엔티티에 `setter`가 많으면 **어디서든 상태 변경**이 가능해져 불변성·도메인 캡슐화가 약해집니다.
- 리팩터링 시 “필수 필드 누락”을 **컴파일 타임**에 잡기 어렵습니다.

### 실무에서 어떤 방식을 많이 쓰는지

- **엔티티**: 생성자 또는 JPA + Lombok `@Builder` 조합, 수정은 `update(...)` 같은 **도메인 메서드**로 제한하는 경우가 많습니다.
- **요청/응답 DTO**: Builder 또는 생성자, 혹은 **record**(Java 16+)도 증가 중입니다.
- **DTO ↔ Entity**: 팀 규모가 커지면 **전용 Mapper 클래스** 또는 **MapStruct**(컴파일 타임 생성)를 많이 씁니다.
- **Setter로 엔티티 조립**은 레거시·프로토타입 외에는 피하는 편입니다.

---

## 1단계 — Setter (`board-login-pattern-setter`)

### 파일 목록

- `.../setter/BoardLoginSetterApplication.java` — 부트스트랩
- `.../setter/config/WebConfig.java` — 로그인 필요 URL에 인터셉터 등록
- `.../setter/domain/User.java`, `Board.java` — **public 무인자 생성자 + setter** (학습용)
- `.../setter/repository/*Repository.java`
- `.../setter/service/UserService.java`, `BoardService.java` — `new` 후 setter 연쇄
- `.../setter/web/*Controller.java`, `LoginCheckInterceptor.java`
- `.../setter/web/dto/*.java`

### 코드에서 볼 포인트

- `UserService.register`: `User u = new User(); u.setUsername(...);` — **중간에 필수값이 빠져도 컴파일러가 막지 못함**.
- `BoardService.create`: 동일 패턴으로 게시글 생성.

### 비교표 (요약)

| 방식 | 장점 | 단점 | 실무 사용도 |
|------|------|------|--------------|
| Setter | 작성이 직관적일 수 있음 | 불완전 객체·캡슐화 약화 | 낮음 |

### 실무 추천 (1단계 끝)

- “동작은 한다” 수준이지만 **도메인 모델 품질** 측면에서 설계 수업·안티패턴 시연용으로만 두는 것을 권합니다.

---

## 2단계 — 생성자 (`board-login-pattern-constructor`)

### 파일 목록

- `domain/User.java` — `public User(username, password, name)` + **setter 없음**
- `domain/Board.java` — 생성자 + `update(title, content)` 도메인 메서드
- `service/*` — `new User(...)`, `new Board(...)`, 수정은 `board.update(...)`

### Setter 대비 포인트

- **한 번에 유효한 조합**을 만들도록 유도합니다.
- 수정은 `setTitle` 대신 **`update`로 의도**를 드러냅니다.

### 비교표 (요약)

| 방식 | 장점 | 단점 | 실무 사용도 |
|------|------|------|--------------|
| 생성자 | 필수 필드 강제·불변에 가깝게 | 필드 많을 때 생성자 시그니처 부담 | 높음(소·중형) |

### 실무 추천 (2단계 끝)

- 엔티티·값 객체에서 **가장 무난한 기본기**입니다. 필드가 늘면 Builder로 확장합니다.

---

## 3단계 — Builder (`board-login-pattern-builder`)

### 파일 목록

- `domain/User.java`, `Board.java` — `@Getter`, `@NoArgsConstructor(PROTECTED)`, `@AllArgsConstructor(PRIVATE)`, `@Builder`
- `service/UserService.java` — `User.builder().username(...).build()`
- `web/dto/UserProfileDto.java` — 응답도 `@Builder` 예시

### 코드에서 볼 포인트

- `@Builder` — Lombok이 **빌더 클래스**를 생성해 체인 호출을 가능하게 함.
- `@NoArgsConstructor(access = PROTECTED)` — JPA 프록시·리플렉션용 **최소 노출**.

### 비교표 (요약)

| 방식 | 장점 | 단점 | 실무 사용도 |
|------|------|------|--------------|
| Builder | 가독성, 선택 필드, 테스트 데이터 생성 편 | Lombok/JPA 조합 규칙 이해 필요 | 높음 |

### 실무 추천 (3단계 끝)

- 필드 수가 늘거나 **테스트 픽스처** 작성이 많을 때 특히 유리합니다.

---

## 4단계 — Mapper 분리 (`board-login-pattern-mapper`)

### 파일 목록

- `support/UserMapper.java` — `toNewEntity(RegisterForm)`, `toProfileDto(User)`
- `support/BoardMapper.java` — `toNewEntity(BoardForm, User, LocalDateTime)`
- `service/*` — 변환은 Mapper 위임, 서비스는 흐름만 기술

### 코드에서 볼 포인트

- `UserMapper`가 **DTO→Entity**, **Entity→응답 DTO**를 한곳에 모음.
- 서비스 한 줄이 `UserMapper.toNewEntity(form)`처럼 읽혀 **책임이 분리**됨.

### 비교표 (요약)

| 방식 | 장점 | 단점 | 실무 사용도 |
|------|------|------|--------------|
| Mapper 클래스 | 재사용·테스트·가독성 | 파일 수 증가 | 높음(팀·중대형) |

### 실무 추천 (4단계 끝)

- API가 늘고 DTO 종류가 많아지면 **Mapper 전담**(수동 또는 MapStruct)으로 가는 경우가 많습니다.

---

## 전체 비교표 (한눈에)

| 방식 | 장점 | 단점 | 실무 사용도 |
|------|------|------|-------------|
| Setter | 작성 단순해 보임 | 불완전 객체·도메인 약화 | 낮음 |
| 생성자 | 필수값 강제 | 인자 많으면 부담 | 높음 |
| Builder | 읽기 쉬움·확장 | 보일러플레이트(롬복으로 완화) | 높음 |
| Mapper 분리 | 변환 응집·재사용 | 클래스 증가 | 높음 |

---

## 보안 참고

- 비밀번호는 **평문 저장**입니다(원본 `board-login`과 동일한 학습용 수준). 실서비스에서는 반드시 **BCrypt 등 단방향 해시**와 별도 `PasswordEncoder` 적용이 필요합니다.
