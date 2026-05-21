# roomfit → roomfit-social 변경 사항

## 프로젝트 분리 목적

| 프로젝트 | 설명 |
|----------|------|
| **roomfit** | 소셜 로그인 **적용 전** 원본 (폼 로그인만) |
| **roomfit-social** | **Google · Naver · Kakao** OAuth2 소셜 로그인 포함 |

두 프로젝트는 **같은 `d:\spring1` 폴더** 아래에 별도 Gradle 프로젝트로 공존합니다.

---

## 실행 구분 (동시 실행 시)

| 항목 | roomfit | roomfit-social |
|------|---------|----------------|
| Gradle 프로젝트명 | `roomfit` | `roomfit-social` |
| MySQL 포트 | **8081** | **8083** |
| H2(dev) 포트 | **8082** | **8084** |
| MySQL DB명 | `roomfit` | `roomfit_social` |
| 업로드 폴더 | `~/roomfit-uploads/` | `~/roomfit-social-uploads/` |
| 로그인 | 아이디/비밀번호만 | 폼 + 소셜 3종 |

---

## 추가된 의존성

**`build.gradle`**

```gradle
implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
```

---

## 신규 파일 (roomfit-social만)

| 경로 | 역할 |
|------|------|
| `security/OAuthUserProfile.java` | 소셜에서 추출한 id·email·name·nickname |
| `security/LoginMember.java` | 폼·소셜 공통 Principal (`UserDetails` + `OAuth2User`) |
| `security/CustomOAuth2UserService.java` | Google/Naver/Kakao 프로필 파싱 → 회원 저장 |
| `docs/소셜-로그인-설정.md` | Redirect URI · 환경 변수 안내 |
| `docs/ROOMFIT-SOCIAL-변경사항.md` | 본 문서 |

---

## 수정된 파일 (roomfit 대비)

### `application.yml`

- `spring.application.name`: `roomfit-social`
- `spring.security.oauth2.client` 블록 추가 (google, naver, kakao)
- MySQL URL → DB `roomfit_social`, 포트 **8083**
- H2 URL → `roomfit_social`, 포트 **8084**
- `app.upload-dir` → `roomfit-social-uploads`

### `domain/Member.java`

```java
private String oauthProvider;        // google, naver, kakao
private String oauthProviderSubject; // 제공자 쪽 사용자 ID
```

### `repository/MemberRepository.java`

```java
Optional<Member> findByOauthProviderAndOauthProviderSubject(String oauthProvider, String oauthProviderSubject);
```

### `service/MemberService.java`

- `registerOrUpdateOAuthMember(registrationId, profile)` 추가
  - (provider + subject) 기존 회원 → 이름·닉네임 갱신
  - 동일 이메일 일반 회원 → OAuth 연동
  - 없으면 신규 가입 (`loginId` = `google_xxx` 형식, 랜덤 비밀번호)

### `config/CustomUserDetailsService.java`

- `Member` 대신 **`LoginMember.of(member)`** 반환

### `config/SecurityConfig.java`

- `oauth2Login` 설정
- `/oauth2/**`, `/login/oauth2/**` `permitAll`
- `@Lazy CustomOAuth2UserService` 주입 (순환 참조 방지)
- `/images/**` permitAll 유지

### `templates/auth/login.html`

소셜 버튼 3개:

- `/oauth2/authorization/google`
- `/oauth2/authorization/naver`
- `/oauth2/authorization/kakao`

### 웹 컨트롤러 (전부)

`@AuthenticationPrincipal Member` →  
`@AuthenticationPrincipal(expression = "member") Member`

대상: `InteriorController`, `MemberController`, `RecommendController`, `ShopController`, `CommunityController`

---

## roomfit(원본)에 없는 것

- `src/main/java/.../security/` 패키지 전체
- `application.yml`의 `spring.security.oauth2` 섹션
- 로그인 화면 소셜 버튼

---

## 소셜 로그인 설정 (roomfit-social)

### Redirect URI (MySQL 8083 기준)

| 제공자 | URI |
|--------|-----|
| Google | `http://localhost:8083/login/oauth2/code/google` |
| Naver | `http://localhost:8083/login/oauth2/code/naver` |
| Kakao | `http://localhost:8083/login/oauth2/code/kakao` |

### 환경 변수

```text
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
NAVER_CLIENT_ID=
NAVER_CLIENT_SECRET=
KAKAO_CLIENT_ID=
KAKAO_CLIENT_SECRET=
```

---

## 실행 방법

**roomfit (원본)**

```powershell
cd d:\spring1\roomfit
.\gradlew.bat bootRun
```

→ http://localhost:8081

**roomfit-social**

```powershell
cd d:\spring1\roomfit-social
.\gradlew.bat bootRun
```

→ http://localhost:8083

---

## 참고

- OAuth 구현 참고: 워크스페이스 `shopping-mall1` 패턴
- `board-login-img-security`에는 OAuth 코드 없음 (폼 로그인 + Security만)

---

*작성: roomfit 소셜 로그인 분리 시점*
