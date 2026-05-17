# RoomFit — 1인가구 원룸 인테리어 커뮤니티

Spring Boot 3 + Thymeleaf + JPA + Spring Security 기반 파이널 프로젝트 구현체입니다.

## 실행

```bash
cd roomfit
./gradlew bootRun
```

### IDE에서 `non-project file` 경고가 뜰 때 (Cursor / VS Code)

`spring1` 전체 폴더를 열면 Java 확장이 `roomfit`을 Gradle 프로젝트로 못 잡는 경우가 있습니다.

1. **권장**: `roomfit.code-workspace` 파일을 연다 (`파일 → 워크스페이스 열기`)
2. 또는 **`roomfit` 폴더만** 연다 (`E:\spring1\roomfit`)
3. 명령 팔레트(`Ctrl+Shift+P`) → **`Java: Clean Java Language Server Workspace`** → Reload
4. 다시 → **`Java: Import Java projects in workspace`**
5. 실행 (둘 중 하나):
   - 터미널: `.\gradlew.bat bootRun` ← **가장 확실**
   - Run and Debug → **RoomFit (Spring Boot)** 선택 후 F5  
   - ⚠️ 파일 위 **Run | Debug** 로 단독 실행하면 `SpringApplication cannot be resolved` 오류 남 (Gradle classpath 미사용)

접속 주소: **http://localhost:8081** (8080 아님)

- URL: http://localhost:8081
- H2 콘솔: http://localhost:8081/h2-console (JDBC URL: `jdbc:h2:mem:roomfit`)

## 데모 계정

| 구분 | 아이디 | 비밀번호 |
|------|--------|----------|
| 관리자 | admin | admin1234 |
| 일반회원 | user1 | user1234 |

## MySQL 사용

```bash
./gradlew bootRun --args='--spring.profiles.active=mysql'
```

## 주요 기능

- 회원가입 / 로그인 / 아이디·비밀번호 찾기 / 마이페이지 / 탈퇴
- 인테리어 게시판 (CRUD, 이미지, 조회수, 좋아요, 댓글)
- 맞춤 추천 알고리즘 (프로필 가중치 + 유사 사용자 + 인기글)
- 소품 쇼핑 (장바구니, 찜, 리뷰)
- 커뮤니티 (자유/질문/후기, 신고)
- 관리자 대시보드

## 문서

- [프로젝트 설계서](docs/프로젝트-설계서.md)

## 패키지 구조 (Spring MVC)

| 계층 | 패키지 | 역할 |
|------|--------|------|
| Controller | `web` | 요청 처리, View 선택 |
| Service | `service` | 비즈니스 로직 |
| Repository | `repository` | JPA 데이터 접근 |
| Domain | `domain` | 엔티티 |
| Recommend | `recommend` | 추천 알고리즘 |

원 설계서의 MVC2(Servlet+JSP)는 **Spring MVC(Controller + Thymeleaf View + Service/Repository Model)** 로 구현했습니다.
