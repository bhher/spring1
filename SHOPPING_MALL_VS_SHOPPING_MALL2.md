# shopping-mall vs shopping-mall2 차이점

같은 도메인·서비스 레이어를 기준으로 한 프로젝트가 **Thymeleaf 서버 렌더링**인지 **React SPA + REST API**인지에 따라 갈라진 버전입니다.

---

## 한 줄 요약

| 항목 | shopping-mall | shopping-mall2 |
|------|----------------|----------------|
| UI | Thymeleaf (`templates/`) | React (Vite, `frontend/`) |
| 백엔드 노출 | `@Controller` + HTML | `/api/**` JSON + 정적 `index.html` |
| 서버 포트 | 8080 | 8082 |
| DB 스키마(기본) | `shopping_mall` | `shopping_mall2` |
| 업로드 경로 | `~/shopping-mall-uploads/` | `~/shopping-mall2-uploads/` |

---

## 빌드·의존성 (`build.gradle`)

**shopping-mall**

- `spring-boot-starter-thymeleaf`, `thymeleaf-extras-springsecurity6`
- Lombok: `compileOnly` + `annotationProcessor` (표준 방식)

**shopping-mall2**

- Thymeleaf 관련 의존성 **없음**
- `io.freefair.lombok` 플러그인으로 Lombok 처리 (Gradle 9 환경에서 안정적으로 동작하도록 구성된 경우가 많음)
- `copyReactOverlay` 태스크: `frontend/dist`가 있으면 `build/resources/main/static`에 복사, `classes`가 이 태스크에 의존 → **프론트 `npm run build` 후** `./gradlew classes`/`bootJar` 시 SPA가 JAR 정적 리소스에 포함됨

---

## 설정 (`application.yml`)

- **애플리케이션 이름**: `shopping-mall` ↔ `shopping-mall2`
- **데이터소스 URL**: DB 이름만 위 표와 같이 분리 (동시에 두 앱을 띄울 때 충돌 방지)
- **서버 포트**: 8080 ↔ 8082
- **`app.upload-dir`**: 각각 별도 디렉터리
- **shopping-mall만**: `spring.thymeleaf.cache: false` (개발 편의)

---

## 웹 계층 구조

### shopping-mall

- **MVC 컨트롤러** (HTML 반환): 예) `HomeController`, `ProductController`, `CartController`, `OrderController`, `MyPageController`, `AdminProductController`, `AuthController`
- **`GlobalExceptionHandler`**: 주로 Thymeleaf/리다이렉트에 맞는 예외 처리
- **`src/main/resources/templates/`**: 홈, 로그인/가입, 상품 목록·상세, 장바구니, 마이페이지 주문, 관리자 상품, 에러 페이지 등

### shopping-mall2

- **`templates/` 디렉터리 없음** (Thymeleaf 제거)
- **`SpaForwardController`**: React Router 경로(`GET`)를 `forward:/index.html`로 처리
- **`web/api/`** REST 컨트롤러:
  - `ApiAuthController` — `/api/auth` (CSRF 조회, `me`, 회원가입 등)
  - `ApiProductController` — `/api/products`
  - `ApiCartController` — `/api/cart`
  - `ApiOrderController` — `/api/orders`
  - `ApiMyPageController` — `/api/mypage`
  - `ApiAdminProductController` — `/api/admin/products`
- **`ApiExceptionHandler`**: API용 JSON 예외 응답
- **추가 설정 클래스**: `CorsConfig`, `ApiAuthenticationEntryPoint`, `ApiAccessDeniedHandler` (예: `/api/**`는 401/403 JSON 등 SPA에 맞게 처리)

---

## Spring Security

### shopping-mall

- 폼 로그인 기본 URL (`loginPage("/login")`, `logoutUrl("/logout")`)
- 공개 경로: `/`, `/login`, `/register`, `/products`, `/products/**` 등
- 인증 필요: `/cart/**`, `/orders/**`, `/mypage/**`
- 관리자: `/admin/**` → `ADMIN`
- 별도 CORS·CSRF·API 엔트리포인트 커스터마이징은 최소 수준(기본 동작에 가깝게 사용 가능)

### shopping-mall2

- **CORS** 활성화 (`CorsConfig`)
- **CSRF**: `CookieCsrfTokenRepository.withHttpOnlyFalse()` — 브라우저 JS가 쿠키 기반 CSRF 토큰을 읽어 헤더로 보낼 수 있게 구성
- **폼 로그인 처리 URL**: `loginProcessingUrl("/api/auth/login")`
- **로그아웃**: `/api/auth/logout`, 쿠키 정리에 `XSRF-TOKEN` 포함
- **권한**: 정적·SPA GET 경로와 `/api/**`를 메서드·경로별로 나누어 `permitAll` / `authenticated` / `ADMIN` 적용
- **`ApiAuthenticationEntryPoint` / `ApiAccessDeniedHandler`**와 연동해 API·페이지 요청을 구분 처리

---

## 프론트엔드 (shopping-mall2만)

- 디렉터리: **`shopping-mall2/frontend/`**
- **Vite + React**, `react-router-dom`
- 개발 서버 포트 예: **5178** (`vite.config.js`)
- 프록시: `/api`, `/login`, `/logout`, `/uploads`, `/images`, `/css` → `http://localhost:8082`
- API 호출 시 **`credentials: 'include'`** 및 **XSRF-TOKEN → `X-XSRF-TOKEN` 헤더** 패턴 (`src/api.js` 등)

**운영/통합 빌드 흐름 (요지)**

1. `frontend`에서 `npm install` → `npm run build`
2. `./gradlew bootJar` (또는 `classes`) 시 `dist`가 static에 합쳐짐  
3. **미빌드 시**에는 `src/main/resources/static/index.html` 등 안내용 리소스만 있을 수 있음

---

## DTO·API 전용 타입 (shopping-mall2)

shopping-mall2에서 JSON 요청/응답에 맞춰 추가되거나 역할이 분명해진 예:

- `MemberMeDto` — 로그인 사용자 정보 API
- `CartAddRequest`, `CartQuantityRequest` — 장바구니 API 바디
- `AdminProductEditResponse` — 관리자 상품 편집 화면용 응답 등

(도메인·JPA 엔티티·핵심 서비스는 동일 계열로 유지되는 것이 목적입니다.)

---

## 공통으로 거의 동일한 부분

- `WebConfig` — `/uploads/**` 로컬 파일 서빙
- JPA 엔티티, 리포지토리, `ProductService`, `CartService`, `OrderService`, `MemberService`, `FileStorageService` 등 **비웹 비즈니스 로직**
- `DataInitializer` 등 시드 데이터 개념(내용은 각 프로젝트 설정에 따름)
- `CustomUserDetailsService`, `PasswordEncoder` 등 인증 기반

---

## 실행 시 참고

- **동시 실행**: 포트·DB·업로드 디렉터리가 다르므로 shopping-mall(8080)과 shopping-mall2(8082)를 함께 띄우기 쉽습니다.
- **shopping-mall2 개발**: 백엔드 `8082` + `frontend`에서 `npm run dev`(`5178`)로 프록시 개발이 일반적입니다.

---

*문서는 저장소의 `shopping-mall`, `shopping-mall2` 소스 구조를 기준으로 작성되었습니다. 세부 엔드포인트는 코드의 `@RequestMapping` / `@GetMapping` 등을 확인하세요.*
