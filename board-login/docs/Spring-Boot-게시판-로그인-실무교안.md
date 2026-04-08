# Spring Boot 게시판 + 로그인 통합 프로젝트 — 실무형 교안

**대상:** 비전공·초보를 넘어, 실무에서 코드를 읽고 확장할 수 있는 수준  
**주제:** 회원가입 → 로그인(세션) → 게시판 CRUD (Controller + Service + Repository + JPA + Thymeleaf)  
**예제 프로젝트 경로:** `board-login/` (이 저장소의 `d:\spring1\board-login`)

---

## 1. 개념 설명

### 1.1 레이어를 나누는 이유 (비유)

- **Controller (웹 진입점):** 식당에서 주문을 받는 직원. “어떤 URL로 무엇을 요청했는지”만 보고, 화면에 넘길 값을 정리한다.
- **Service (업무 규칙):** 주방장. “회원만 글 쓸 수 있다”, “작성자만 수정한다” 같은 **규칙**을 여기서 처리한다.
- **Repository (DB 접근):** 창고 관리. JPA가 SQL을 대신 짜 주지만, **조회 메서드 이름·JPQL**은 여기에 둔다.
- **Entity (도메인 모델):** DB 테이블과 1:1로 매핑되는 자바 객체 (`User`, `Board`).
- **DTO (폼 객체):** 화면에서 들어온 입력값을 검증(`@Valid`)하기 좋게 묶은 객체 (`RegisterForm`, `BoardForm`). Entity와 분리하면, 화면이 바뀌어도 DB 구조를 덜 흔든다.

**왜 이렇게 나누나?**  
한 파일에 다 넣으면 수정·테스트·재사용이 어렵다. 실무에서는 역할별로 나누는 것이 기본이다.

### 1.2 인증 방식 (이 프로젝트)

- **세션 기반 로그인:** 로그인 성공 시 `HttpSession`에 `loginUser`(아이디), `loginName`(이름)을 넣는다.
- **Spring Security 미사용:** 학습·구조 이해용으로 인터셉터로 “로그인 필요 URL”만 막는다. 실서비스에서는 보통 **Spring Security + BCrypt**로 옮긴다.

### 1.3 게시글과 회원의 관계

- `Board`는 `User`를 **다대일(`@ManyToOne`)** 로 참조한다. “글은 반드시 작성자(User)가 있다”는 도메인 규칙을 DB 제약(FK)과 같이 표현한다.

---

## 2. 프로젝트 구조 설명

```
board-login/
├── pom.xml
├── src/main/java/com/example/boardlogin/
│   ├── BoardLoginApplication.java      # 진입점
│   ├── config/
│   │   └── WebConfig.java                # 인터셉터 등록
│   ├── domain/
│   │   ├── User.java
│   │   └── Board.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   └── BoardRepository.java
│   ├── service/
│   │   ├── UserService.java
│   │   └── BoardService.java
│   └── web/
│       ├── AuthController.java           # 로그인·회원가입·로그아웃
│       ├── HomeController.java           # / , /home
│       ├── BoardController.java          # /posts/*
│       ├── LoginCheckInterceptor.java    # 로그인 강제
│       └── dto/
│           ├── RegisterForm.java
│           └── BoardForm.java
└── src/main/resources/
    ├── application.properties
    └── templates/
        ├── login.html, register.html, home.html
        └── board/
            ├── list.html, detail.html, write.html, edit.html
```

**왜 이렇게 설계했는지**

- `config`: 웹 전역 설정(인터셉터)만 모아 두면 나중에 CORS·리소스 핸들러 추가가 쉽다.
- `domain`: DB와 직결되는 엔티티만 — 화면 전용 필드는 DTO로 분리.
- `web`: Spring MVC 컨트롤러만 — 비즈니스 판단은 Service로 넘긴다.

---

## 3. 핵심 코드 (요약 + 파일 위치)

### 3.1 회원가입·로그인 (`AuthController`)

- **GET `/register`:** `RegisterForm`을 모델에 넣고 `register.html` 표시.
- **POST `/register`:** `@Valid`로 검증 → 비밀번호 확인 일치 → `UserService.register`.
- **POST `/login`:** `UserService.login` 성공 시 세션에 `loginUser`, `loginName` 저장 후 `redirect:/home`.

세션 키 상수: `AuthController.SESSION_USER` (`"loginUser"`).

### 3.2 로그인이 필요한 URL (`LoginCheckInterceptor` + `WebConfig`)

다음 경로는 세션에 `loginUser`가 없으면 `/login`으로 보낸다.

- `/posts/write` (글쓰기 GET/POST)
- `/posts/*/edit` (수정 GET/POST)
- `/posts/*/delete` (삭제 POST)

**왜 인터셉터인가?**  
컨트롤러마다 `if (session == null)` 을 반복하지 않기 위함. 다만 **권한(작성자만 수정)** 은 Service·Controller에서 한 번 더 검증한다(인터셉터는 “로그인 여부”만).

### 3.3 게시판 (`BoardController`)

- **GET `/posts`:** 목록 — 누구나.
- **GET `/posts/{id}`:** 상세 — 누구나. `canEdit`은 작성자와 세션 아이디 비교.
- **GET/POST `/posts/write`:** 글쓰기 — 인터셉터 + `User` 조회 후 `BoardService.create`.
- **GET/POST `/posts/{id}/edit`:** 수정 — 작성자 확인 후 `BoardService.update`.
- **POST `/posts/{id}/delete`:** 삭제 — `BoardService.delete`.

`{id:\\d+}` 로 숫자만 매칭해 `/posts/write` 와 `/posts/{id}` 가 섞이지 않게 했다.

### 3.4 권한 규칙 (`BoardService`)

- `update`, `delete` 시 `board.getAuthor().getUsername()` 과 현재 로그인 아이디를 비교한다.
- 불일치 시 `IllegalStateException` — 컨트롤러에서 메시지로 화면에 표시.

### 3.5 N+1·Lazy 문제 (`BoardRepository`)

목록/상세에서 작성자 이름을 쓰기 위해 `JOIN FETCH` 로 한 번에 가져온다.

- `findAllWithAuthor()`
- `findByIdWithAuthor(id)`

`spring.jpa.open-in-view=false` 일 때도 뷰에서 `author`에 접근할 수 있도록 한 패턴이다.

---

## 4. 실행 흐름 (요청 하나씩 따라가기)

### 4.1 회원가입 후 로그인

1. 브라우저 **GET** `/register` → `register.html`.
2. **POST** `/register` → 검증 통과 → `UserService.register` → DB `users` 에 INSERT → **redirect** `/login?registered`.
3. **POST** `/login` → `UserService.login` → 세션 저장 → **redirect** `/home`.

### 4.2 글쓰기

1. 로그인 상태에서 **GET** `/posts/write` → 인터셉터 통과 → `write.html`.
2. **POST** `/posts/write` → `BoardForm` 검증 → 세션의 아이디로 `User` 조회 → `BoardService.create` → `boards` INSERT → **redirect** `/posts`.

### 4.3 수정·삭제

1. **GET** `/posts/1/edit` → 인터셉터 통과 → `canEdit` 이 false 이면 상세로 redirect.
2. **POST** `/posts/1/edit` → `BoardService.update` 에서 작성자 검증.
3. **POST** `/posts/1/delete` → `BoardService.delete` 에서 작성자 검증.

---

## 5. 실행 방법

**요구 사항:** JDK 17, Maven(또는 프로젝트에 포함된 Maven Wrapper).

```text
cd board-login
mvnw.cmd test
mvnw.cmd spring-boot:run
```

(Maven이 PATH에 있으면 `mvn` 으로 동일.)

- 애플리케이션: `http://localhost:8080`
- H2 콘솔: `http://localhost:8080/h2-console` (JDBC URL은 `application.properties` 와 동일하게 `jdbc:h2:mem:boardlogindb`)

**권장 시나리오:** 회원가입 → 로그인 → 게시판에서 글쓰기 → 목록/상세 → 수정/삭제.

---

## 6. 실무 팁

1. **비밀번호:** 예제는 비교를 단순화하기 위해 **평문 저장**이다. 실무에서는 반드시 **BCrypt** 등으로 해시하고, Security 연동을 검토한다.
2. **CSRF:** Spring Security 없이 폼을 쓰면 CSRF 토큰이 기본으로 없다. 공개 서비스에서는 Security의 CSRF 보호를 쓰는 것이 일반적이다.
3. **세션 고정 공격:** 로그인 성공 후 `session.invalidate()` 후 새 세션에 넣는 방식 등을 실무에서 고려한다.
4. **Open Session In View:** 끄고(`open-in-view=false`) **JOIN FETCH / DTO** 로 뷰에 필요한 데이터만 맞춘다 — 이 프로젝트는 FETCH 방식을 사용한다.
5. **예외 처리:** 지금은 `IllegalArgumentException` 이 상세 조회에서 그대로 터질 수 있다. 확장 시 `@ControllerAdvice` 로 404 페이지로 통일한다.

---

## 7. 실무에서 자주 나는 오류와 디버깅

| 증상 | 흔한 원인 | 확인 방법 |
|------|------------|-----------|
| `LazyInitializationException` | `open-in-view=false` 인데 뷰에서 지연 로딩 엔티티 접근 | Repository에서 FETCH / DTO 조회, 또는 트랜잭션 범위 확인 |
| 글 목록에서 작성자 이름이 안 나옴 | `author` 미로딩 | `findAllWithAuthor()` 사용 여부, JPQL 오타 |
| 로그인했는데 글쓰기에서 로그인으로 튕김 | 세션 쿠키 없음, 다른 포트/도메인 | 같은 호스트에서 요청하는지, 쿠키 차단 여부 |
| `/posts/write` 가 상세로 해석됨 | 경로 매핑 순서·패턴 | `{id:\\d+}` 처럼 숫자만 매칭 |
| H2 콘솔 404 | Boot 4에서 H2 콘솔 모듈 누락 | `spring-boot-h2console` 의존성 확인 (`pom.xml`) |

**디버깅 순서 제안**

1. **브라우저 개발자 도구 → Network:** redirect 여부, 상태 코드 302/403/500.
2. **`logging.level.org.springframework.web=DEBUG`** (일시) 로 핸들러 매핑 확인.
3. **`spring.jpa.show-sql=true`** 로 실제 SQL 확인 (이미 `application.properties` 에 있음).
4. IDE **브레이크포인트:** `BoardService.update` 의 작성자 비교, `LoginCheckInterceptor.preHandle`.

---

## 8. 단계별로 “왜 이렇게 설계했는지” (구현 순서와 연결)

| 단계 | 내용 | 설계 이유 |
|------|------|-----------|
| 1 | `User` 엔티티 + `UserRepository` | 회원 정보 영속화와 중복 아이디 검사 |
| 2 | `UserService` + 회원가입/로그인 | 컨트롤러에 SQL·규칙을 넣지 않기 위함 |
| 3 | 세션 + `AuthController` | HTTP는 Stateless이므로, 로그인 상태를 세션에 저장하는 전형적 학습용 방식 |
| 4 | `Board` + `User` 연관 | “누가 썼는지”를 FK로 보장 |
| 5 | `BoardRepository` FETCH | 성능·OSIV 끈 환경에서 안전하게 뷰에 데이터 전달 |
| 6 | `BoardService` 권한 | URL만 알아서 남의 글을 수정하지 못하게 **서버에서** 검증 |
| 7 | `LoginCheckInterceptor` | 인증 공통 처리 — DRY |
| 8 | Thymeleaf | 서버 렌더링으로 빠른 프로토타입·SSR 학습 |

---

## 9. 실습 문제 3개 + 정답

### 문제 1

`Board` 에 **조회수** 필드를 추가하고, 상세 페이지(`GET /posts/{id}`)에 들어갈 때마다 1씩 증가시키시오. (Entity, Service, 템플릿 수정)

**정답 요지**

- Entity에 `long viewCount` (또는 `Long`) 필드 추가, 기본값 0.
- `BoardService` 에 `@Transactional` 메서드 `incrementViewCount(Long id)` 추가 후, 상세 조회 시 호출하거나, 상세용 메서드 안에서 처리.
- `detail.html` 에 조회수 표시.
- 주의: 새 필드 추가 후 `ddl-auto=create-drop` 이면 재기동 시 스키마 재생성 — 운영에서는 마이그레이션 도구(Flyway 등) 사용.

### 문제 2

게시글 목록을 **페이징**하시오. (예: 한 페이지 10건, `Pageable` 사용)

**정답 요지**

- `BoardRepository` 가 `JpaRepository` 이므로 `Page<Board> findAllBy(Pageable pageable);` 또는 커스텀 쿼리 + `count` 쿼리.
- FETCH 가 필요하면 `@Query` + `countQuery` 를 가진 `Page`용 메서드 작성.
- 컨트롤러에서 `@RequestParam(defaultValue = "0") int page` 받아 `PageRequest.of(page, 10)` 전달.
- `list.html` 에 이전/다음 링크 (`?page=`).

### 문제 3

로그인하지 않은 사용자는 **목록·상세만** 보고, 글쓰기 버튼은 숨기거나 비활성화하시오. (템플릿 조건만으로 끝낼 수 있는지, 서버에서 막아야 하는지 서술하시오.)

**정답 요지**

- 템플릿: `th:if="${session.loginUser != null}"` 로 버튼 표시 — 이미 `list.html` 등에서 유사 패턴 가능.
- 서버: 글쓰기 URL은 **인터셉터로 반드시** 막아야 한다. 화면에서 버튼을 숨겨도 URL 직접 입력으로 우회할 수 있기 때문이다.

---

## 10. 핵심 파일 바로가기 (코드 읽는 순서 추천)

1. `application.properties` — DB, JPA, 포트  
2. `User.java`, `Board.java`  
3. `UserService.java`, `BoardService.java`  
4. `AuthController.java`, `BoardController.java`  
5. `LoginCheckInterceptor.java`, `WebConfig.java`  
6. `BoardRepository.java` (JPQL)  
7. `templates/board/*.html`

---

이 문서는 `board-login` 프로젝트와 함께 읽으면, “회원가입 → 로그인 → 게시판” 흐름을 실무형 레이어 구조로 한 번에 연습할 수 있다.
