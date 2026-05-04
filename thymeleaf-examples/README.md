# thymeleaf-examples

Spring Boot + Thymeleaf로 자주 쓰는 문법을 페이지별로 보여 주는 작은 예제입니다.

## 실행

이 폴더에 Gradle Wrapper가 없으면, 같은 저장소의 `crud2` 등에서 복사하거나 전역 Gradle로 실행하세요.

```text
cd thymeleaf-examples
..\crud2\gradlew.bat bootRun
```

또는 이 폴더에 `gradlew`를 둔 뒤:

```text
.\gradlew.bat bootRun
```

브라우저: `http://localhost:<server.port>` (기본 예: `application.properties`의 `server.port`, 예시 `9291`)

### `Port ... was already in use` 가 나올 때

이미 예전에 띄운 **bootRun** 이 같은 포트를 잡고 있으면 위 오류가 납니다.

1. **그 터미널에서 Ctrl+C** 로 앱을 끄거나  
2. PowerShell에서 해당 포트를 쓰는 PID 확인 후 종료 (예: `9291`):

```powershell
netstat -ano | findstr :9291
# LISTENING 줄의 마지막 숫자(PID) 확인 후:
taskkill /PID <PID> /F
```

또는 `application.properties` 의 `server.port` 를 다른 번호로 바꿉니다.

## H2 Console

1. 앱을 띄운 뒤 `http://localhost:<server.port>/h2-console` 접속  
2. **JDBC URL** 에 `application.properties` 의 `spring.datasource.url` 값을 **그대로** 입력 (예: `jdbc:h2:~/test_boardlogin`)  
3. **User Name** `sa`, **Password** 비움 → Connect

## 구성

| 경로 | 설명 |
|------|------|
| `/` | 데모 목록 |
| `/h2-console` | H2 웹 콘솔 (DB 확인·SQL) |
| `/notes`, `/notes/new`, … | 메모 CRUD (JPA) |
| `/demo/text` | `th:text`, `th:utext`, 인라인 `[[...]]` |
| `/demo/condition` | `th:if`, `th:unless`, `th:switch` |
| `/demo/loop` | `th:each`, 반복 상태(`stat`) |
| `/demo/link` | `th:href`, `@{...}` |
| `/demo/form` | `th:object`, `th:field` (POST 결과 페이지 포함) |
| `/demo/fragment` | `th:replace`, `th:insert`, 파라미터 있는 프래그먼트 |

정리 문서: [`docs/THYMELEAF-정리.md`](docs/THYMELEAF-정리.md)
