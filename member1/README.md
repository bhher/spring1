# member1 프로젝트 설명

Spring Boot 기반의 **회원(Member) CRUD** 예제입니다. 화면은 **Thymeleaf**, DB는 **MySQL**을 사용합니다.

---

## 핵심 기능

- **회원 CRUD**: 등록/목록/수정/삭제
- **이메일 중복 가입 방지**: 최초 회원가입 시 같은 이메일이 이미 있으면 저장하지 않고 가입 폼으로 되돌림(에러 메시지 + 입력값 유지)

---

## 기술 스택

| 구분 | 내용 |
|------|------|
| Java | 17 |
| Spring Boot | 3.3.12 |
| Web | `spring-boot-starter-web` |
| Thymeleaf | `spring-boot-starter-thymeleaf` |
| JPA | `spring-boot-starter-data-jpa` |
| DB | MySQL (`mysql-connector-j`) |
| 기타 | Lombok은 `build.gradle`에 포함되어 있으나, 현재 소스의 Entity/DTO는 **수동 getter/setter**로 작성됨 |

---

## 패키지 구조

```
member.member1
├── Member1Application.java      # 진입점
├── controller/MemberController.java
├── service/MemberService.java
├── repository/MemberRepository.java
├── entity/Member.java
└── dto/MemberDto.java
```

흐름: **Controller → Service → Repository → Entity**  
폼/요청 데이터는 **DTO(`MemberDto`)**로 받고, 저장·수정 시 **Entity(`Member`)**로 변환합니다.

---

## 데이터베이스 설정 (`application.properties`)

- **DB 이름**: `member1_db` (로컬 MySQL `localhost:3306`)
- **사용자**: `root` / **비밀번호**: `1234` (환경에 맞게 수정 필요)
- **DDL**: `spring.jpa.hibernate.ddl-auto=update` — 스키마를 엔티티에 맞춰 갱신
- **다이얼렉트**: MySQL 8
- **문자셋**: Hikari 초기 SQL로 `utf8mb4` 설정
- **Thymeleaf**: 개발 시 캐시 끔 (`spring.thymeleaf.cache=false`)

앱을 실행하려면 **MySQL에 `member1_db` 데이터베이스를 만들고**, 위 계정·URL이 맞는지 확인해야 합니다.

---

## 도메인 모델

### Entity: `Member`

| 필드 | 설명 |
|------|------|
| `id` | 기본키, 자동 증가 |
| `username` | 이름(사용자명) |
| `email` | 이메일 |
| `password` | 비밀번호 (평문 저장 — 실서비스에서는 암호화 필요) |

- `Member(MemberDto dto)`: **신규 생성** 시 DTO → 엔티티 변환
- `updateFromDto(MemberDto dto)`: **수정** 시 필드 갱신

### DTO: `MemberDto`

화면과 컨트롤러 사이에서 쓰는 데이터 객체입니다. `id` 유무에 따라 생성자가 나뉩니다(목록/등록/수정 폼 바인딩용).

---

## Repository: `MemberRepository`

- `CrudRepository<Member, Long>` 상속
- `List<Member> findAll()` — 전체 목록 조회(Spring Data JPA 쿼리 메서드)
- `Optional<Member> findByEmail(String email)` — 이메일로 중복 여부 확인(회원가입 시 사용)

---

## Service: `MemberService`

| 메서드 | 역할 |
|--------|------|
| `isEmailRegistered(String email)` | 이메일 중복 여부 확인(공백 제거 후 검사) |
| `create(MemberDto dto)` | DTO로 `Member` 생성 후 저장 |
| `findAll()` | 전체 회원 목록 |
| `findById(Long id)` | 단건 조회, 없으면 `null` |
| `update(Long id, MemberDto dto)` | ID로 조회 후 `updateFromDto`로 반영하고 저장 |
| `delete(Long id)` | ID로 삭제 |

---

## Controller: `MemberController`

기본 URL prefix: **`/members`**

| HTTP | 경로 | 설명 | 뷰 / 응답 |
|------|------|------|-----------|
| GET | `/members` | 회원 목록 | `members/list.html` |
| GET | `/members/new` | 회원 등록 폼 | `members/create.html` |
| POST | `/members/new` | 등록 처리(이메일 중복이면 폼으로) | 성공: `redirect:/members` / 실패: `redirect:/members/new` |
| GET | `/members/{id}/edit` | 수정 폼 (없으면 목록으로) | `members/edit.html` |
| POST | `/members/{id}/edit` | 수정 처리 | `redirect:/members` |
| POST | `/members/{id}/delete` | 삭제 처리 | `redirect:/members` |

등록·수정·삭제 후에는 **PRG 패턴**처럼 목록(`/members`)으로 리다이렉트합니다.

이메일 중복으로 가입이 실패하는 경우에도 PRG 흐름을 유지하기 위해 `RedirectAttributes.addFlashAttribute(...)`를 사용해서
에러 메시지(`errorMessage`)와 입력값(`memberDto`)을 한 번만 전달하고, `GET /members/new`에서 해당 값이 있으면 그대로 폼에 바인딩합니다.

---

## Thymeleaf 템플릿

| 파일 | 용도 |
|------|------|
| `templates/members/list.html` | 목록, 추가 링크, 수정 링크, 삭제 폼 |
| `templates/members/create.html` | 신규 가입 폼 (`th:object="${memberDto}"`), 이메일 중복 시 `errorMessage` 표시 |
| `templates/members/edit.html` | 수정 폼 (action에 `memberDto.id` 반영) |

---

## 정적 페이지

- `static/index.html`  
  루트(`/`) 접속 시 보이는 단순 안내 페이지입니다(제목에 "멤버2" 등 표기).  
  **회원 기능 진입**은 브라우저에서 **`http://localhost:8080/members`** 로 이동하면 됩니다.

---

## 실행 순서 요약

1. MySQL 실행 후 `member1_db` 생성  
2. `application.properties`의 URL·계정 확인  
3. `./gradlew bootRun` (또는 IDE에서 `Member1Application` 실행)  
4. 브라우저: `http://localhost:8080/members` → 목록 / 추가 / 수정 / 삭제 테스트  

---

## 참고 (개선 여지)

- 비밀번호는 **BCrypt 등으로 암호화** 후 저장하는 것이 안전합니다.  
- `findById`가 `null`일 때 수정 POST는 서비스에서 아무 것도 하지 않을 수 있어, **404 처리**나 검증을 넣을 수 있습니다.  
- `MemberController`의 `java.util.Optional` import는 현재 코드에서 사용되지 않습니다(정리 가능).
