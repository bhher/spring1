# shopping-mall1 정리

`shopping-mall`을 복사한 **`shopping-mall1`** 프로젝트에 **OAuth2 소셜 로그인(구글·네이버·카카오)** 을 추가한 내용을 정리합니다.

---

## 1. 원본과의 차이

| 항목 | shopping-mall | shopping-mall1 |
|------|-----------------|----------------|
| 서버 포트 | 8080 | **8081** |
| DB 스키마 | `shopping_mall` | **`shopping_mall1`** |
| 업로드 디렉터리 | `~/shopping-mall-uploads/` | **`~/shopping-mall1-uploads/`** |
| Gradle 프로젝트명 | `shopping-mall` | **`shopping-mall1`** |
| 인증 | 폼 로그인만 | 폼 로그인 + **OAuth2 클라이언트** |

Java 패키지명은 **`com.example.shoppingmall`** 로 동일합니다.

---

## 2. 소셜 로그인 동작 요약

1. 로그인 페이지에서 **Google / 네이버 / 카카오** 버튼 → `/oauth2/authorization/{google|naver|kakao}` 로 이동  
2. 제공자 인증 후 콜백 → **`CustomOAuth2UserService`** 가 사용자 정보를 받아 **`MemberService.registerOrUpdateOAuthMember`** 호출  
3. **(oauthProvider + oauthProviderSubject)** 로 기존 회원 조회 → 있으면 이름 등 갱신  
4. 없으면 **이메일**로 기존 회원 조회 → 있으면 소셜 필드 연동  
5. 둘 다 없으면 **신규 회원** 생성 (비밀번호는 내부용 랜덤 값)  
6. 인증 주체는 **`LoginMember`** (`UserDetails` + `OAuth2User`) 로 통일 → 컨트롤러에서는 `@AuthenticationPrincipal(expression = "member") Member member` 로 주입

---

## 3. 추가·수정된 주요 파일

| 경로 | 설명 |
|------|------|
| `build.gradle` | `spring-boot-starter-oauth2-client` 의존성 |
| `settings.gradle` | `rootProject.name = 'shopping-mall1'` |
| `src/main/resources/application.yml` | 포트·DB·OAuth2 `registration` / `provider` |
| `config/SecurityConfig.java` | `/oauth2/**`, `/login/oauth2/**` 허용, `oauth2Login`, **`@Lazy` 주입**으로 순환 참조 방지 |
| `domain/Member.java` | `oauthProvider`, `oauthProviderSubject` 필드 |
| `repository/MemberRepository.java` | `findByOauthProviderAndOauthProviderSubject` |
| `service/MemberService.java` | `registerOrUpdateOAuthMember` |
| `service/CustomUserDetailsService.java` | `LoginMember.of(member)` 반환 |
| `security/LoginMember.java` | 폼·소셜 공통 Principal |
| `security/OAuthUserProfile.java` | 소셜에서 뽑은 id·이메일·이름 DTO |
| `security/CustomOAuth2UserService.java` | 구글/네이버/카카오 속성 파싱 후 회원 저장 |
| `web/CartController.java` 등 | `@AuthenticationPrincipal(expression = "member")` |
| `templates/auth/login.html` | 소셜 로그인 버튼 3개 |

---

## 4. 제공자별 콘솔 설정 (리다이렉트 URI)

로컬 기준 예시 (포트 **8081**):

- Google: `http://localhost:8081/login/oauth2/code/google`
- Naver: `http://localhost:8081/login/oauth2/code/naver`
- Kakao: `http://localhost:8081/login/oauth2/code/kakao`

배포 시에는 실제 도메인으로 같은 패턴의 URI를 등록합니다.

---

## 5. 환경 변수 (또는 `application.yml` 수정)

`application.yml` 에서 기본값 `change-me` 를 실제 값으로 바꾸거나, 아래 환경 변수를 설정합니다.

| 변수 | 용도 |
|------|------|
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | Google OAuth |
| `NAVER_CLIENT_ID` / `NAVER_CLIENT_SECRET` | Naver OAuth |
| `KAKAO_CLIENT_ID` / `KAKAO_CLIENT_SECRET` | Kakao OAuth (앱에 시크릿이 있을 때) |

**카카오**는 `client-authentication-method: client_secret_post` 로 설정되어 있습니다. 앱 설정에 맞게 시크릿을 넣으세요.

---

## 6. 빌드·실행

```text
cd shopping-mall1
.\gradlew.bat test
.\gradlew.bat bootRun
```

- MySQL이 떠 있고, `application.yml` 의 DB 사용자·비밀번호가 맞아야 합니다.  
- `shopping-mall` 과 동시에 실행할 때는 포트·DB·업로드 경로가 **서로 다르므로** 충돌하지 않습니다.

---

## 7. 알려진 이슈 / 참고

- **빈 순환 참조**: `SecurityConfig` ↔ `CustomOAuth2UserService` 초기화 순서 문제가 있어, 생성자 파라미터에 **`@Lazy CustomOAuth2UserService`** 를 사용했습니다.  
- **카카오 이메일 미동의**: 이메일이 없으면 `kakao_{id}@users.noreply.kakao` 형태로 가입합니다.  
- **네이버/카카오 스코프**: `application.yml` 의 `scope` 는 앱에서 허용한 항목과 맞춰야 합니다.

---

## 8. 원본 `shopping-mall`

원본 폴더는 수정하지 않았습니다. 소셜 로그인은 **`shopping-mall1` 전용**입니다.
