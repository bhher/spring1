# join3 — Spring Boot 회원가입·로그인 예제

Thymeleaf 화면과 H2 인메모리 DB를 사용하는 **회원가입·로그인** 샘플 프로젝트입니다. **Spring Security는 사용하지 않으며**, `HttpSession`으로 로그인 상태를 유지합니다.

---

## 기술 스택

| 항목 | 버전·내용 |
|------|-----------|
| Java | 17 |
| Spring Boot | 4.0.5 |
| 빌드 | Maven |
| 웹 | Spring Web (Tomcat) |
| 화면 | Thymeleaf |
| DB | H2 (인메모리), Spring Data JPA |
| 검증 | `spring-boot-starter-validation` (Bean Validation) |
| 인증 | **미사용** — 세션 + 서비스 레이어에서 비밀번호 비교 |

---

## 프로젝트 구조

```
join3/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/example/join3/
    │   ├── Join3Application.java      # 진입점
    │   ├── domain/
    │   │   └── User.java              # 엔티티 (users 테이블)
    │   ├── repository/
    │   │   └── UserRepository.java    # JPA 리포지토리
    │   ├── service/
    │   │   └── UserService.java       # 회원가입·로그인 비즈니스 로직
    │   └── web/
    │       ├── AuthController.java    # 로그인·로그아웃·회원가입
    │       ├── HomeController.java    # 홈·루트 리다이렉트
    │       └── RegisterForm.java      # 회원가입 폼 DTO
    └── resources/
        ├── application.properties
        ├── data.sql                   # 더미 회원 3명 INSERT
        └── templates/
            ├── login.html
            ├── register.html
            └── home.html
```

---

## 주요 동작

### 회원가입 (`/register`)

- 아이디·비밀번호·비밀번호 확인·이름을 입력합니다.
- Bean Validation으로 빈 값·길이 제한을 검사합니다.
- 비밀번호와 비밀번호 확인이 다르면 오류 메시지를 보여줍니다.
- 이미 존재하는 아이디면 `UserService`에서 예외를 던집니다.
- 성공 시 `/login?registered` 로 리다이렉트합니다.

### 로그인 (`/login`)

- 아이디·비밀번호로 `User`를 조회한 뒤, **비밀번호 문자열이 동일한지** 비교합니다 (암호화 없음).
- 성공 시 세션에 다음을 저장합니다.
  - `loginUser`: 로그인 아이디 (`String`)
  - `loginName`: 화면에 표시할 이름 (`String`)

### 로그아웃 (`/logout`)

- 세션을 무효화(`invalidate`) 후 `/login`으로 이동합니다.

### 홈 (`/home`)

- 세션에 `loginUser`가 없으면 `/login`으로 보냅니다.
- 로그인한 사용자에게만 화면을 보여줍니다.

### 루트 (`/`)

- 이미 로그인되어 있으면 `/home`, 아니면 `/login`으로 리다이렉트합니다.

---

## URL / HTTP 메서드

| 경로 | 메서드 | 설명 |
|------|--------|------|
| `/` | GET | 로그인 여부에 따라 `/home` 또는 `/login` |
| `/login` | GET | 로그인 폼 |
| `/login` | POST | 로그인 처리 |
| `/logout` | GET | 로그아웃 |
| `/register` | GET | 회원가입 폼 |
| `/register` | POST | 회원가입 처리 |
| `/home` | GET | 로그인 후 홈 (미로그인 시 `/login`) |

기본 서버 포트: **8080** (`application.properties`의 `server.port`).

---

## 실행 방법

### 사전 요구사항

- JDK 17
- Maven (또는 IDE에서 Maven 연동)

### 명령줄

```bash
cd join3
mvn spring-boot:run
```

또는 패키징 후 실행:

```bash
mvn -DskipTests package
java -jar target/join3-0.0.1-SNAPSHOT.jar
```

브라우저에서 `http://localhost:8080` 으로 접속합니다.

---

## 더미 데이터 (초기 회원 3명)

`src/main/resources/data.sql` 에서 애플리케이션 기동 시 삽입됩니다.

| 아이디 | 비밀번호 | 이름 |
|--------|----------|------|
| `hong` | `1234` | 홍길동 |
| `kim` | `1234` | 김철수 |
| `lee` | `1234` | 이영희 |

DB는 `spring.jpa.hibernate.ddl-auto=create-drop` 이므로 **애플리케이션을 끄면 스키마·데이터가 사라집니다** (인메모리 H2 특성).

---

## H2 콘솔 (Spring Boot 4 주의)

Spring Boot **4.x**에서는 H2 콘솔 자동설정이 **`spring-boot-h2console`** 의존성에 들어 있습니다. `h2` 드라이버만 있으면 DB는 동작하지만 **`/h2-console`이 404**가 될 수 있으므로, 이 프로젝트의 `pom.xml`에는 다음이 포함되어 있습니다.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-h2console</artifactId>
</dependency>
```

- 접속 URL: `http://localhost:8080/h2-console` (경로는 `spring.h2.console.path`로 변경 가능)
- 콘솔 로그인 시 **JDBC URL**은 `application.properties`와 동일해야 합니다. 예:

  `jdbc:h2:mem:join3db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`

- 사용자명: `sa`  
- 비밀번호: 비워둠 (설정에 맞게)

---

## 설정 요약 (`application.properties`)

- **데이터소스**: 인메모리 H2 `join3db`
- **JPA**: `ddl-auto=create-drop`, `defer-datasource-initialization=true` 로 Hibernate 스키마 생성 후 `data.sql` 실행
- **H2 콘솔**: `spring.h2.console.enabled=true`

---

## 보안·운영 관련 안내

- 비밀번호는 **평문 저장·평문 비교**입니다. **실습·데모용**으로만 사용하고, 실제 서비스에는 부적합합니다.
- Spring Security를 쓰지 않으므로 **CSRF 토큰** 등도 없습니다. 학습 목적의 로컬 실행을 가정한 구성입니다.
- 로그인 판별은 컨트롤러에서 세션 속성만 확인합니다. API를 추가할 경우 동일한 인증 방식을 일관되게 적용해야 합니다.

---

## 라이선스

예제 프로젝트입니다. 필요에 맞게 수정해 사용하세요.
