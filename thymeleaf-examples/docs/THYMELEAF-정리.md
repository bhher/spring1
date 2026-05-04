# Thymeleaf 정리 (Spring Boot 기준)

**Thymeleaf**는 서버에서 HTML을 렌더링할 때, HTML 파일에 `th:*` 속성을 붙여 **모델 값**을 넣는 템플릿 엔진입니다. 브라우저로 그대로 열면 `th:*`는 무시되고 정적 내용이 보이므로(프로토타입에 유리), 서버에서는 같은 파일이 동적 뷰가 됩니다.

이 저장소의 실행 예제: **`thymeleaf-examples`** 모듈 (`http://localhost:<server.port>`, `application.properties` 참고).

---

## 1. Spring Boot와 연결

1. **의존성:** `spring-boot-starter-thymeleaf` (이 프로젝트의 `build.gradle` 참고).
2. **템플릿 위치:** 기본은 `src/main/resources/templates/` 아래 `.html`.
3. **컨트롤러:** `@Controller` + `String` 반환 시 뷰 이름으로 해석됩니다.

```java
@GetMapping("/demo/text")
public String text(Model model) {
    model.addAttribute("plain", "일반 텍스트");
    return "demo/text";  // → templates/demo/text.html
}
```

4. **설정 예시** (`application.properties`):

```properties
spring.thymeleaf.cache=false   # 개발 시 템플릿 즉시 반영
```

---

## 2. 네임스페이스

HTML 루트에 선언합니다.

```html
<html xmlns:th="http://www.thymeleaf.org" lang="ko">
```

---

## 3. 값 출력: `th:text` / `th:utext`

| 속성 | 동작 |
|------|------|
| `th:text="${var}"` | HTML 이스케이프(태그가 문자로 보임). 기본이 안전함. |
| `th:utext="${var}"` | 이스케이프 안 함. **신뢰할 수 있는 HTML만** 사용. |

**인라인**(본문 안에서):

- `[[${var}]]` → `th:text`와 동일하게 이스케이프.
- `[(${var})]` → `th:utext`와 동일(주의).

예제 페이지: `/demo/text`.

---

## 4. 조건: `th:if` / `th:unless` / `th:switch`

```html
<div th:if="${user != null}">로그인됨</div>
<div th:unless="${admin}">일반 사용자</div>
<div th:switch="${role}">
  <p th:case="'admin'">관리자</p>
  <p th:case="*">기타</p>
</div>
```

예제: `/demo/condition` (`?role=admin` 등).

---

## 5. 반복: `th:each`

```html
<tr th:each="item, stat : ${items}">
  <td th:text="${item.name}">이름</td>
  <td th:text="${stat.index}">0</td>
  <td th:if="${stat.first}">첫 행</td>
</tr>
```

- `stat`: `index`, `count`, `size`, `first`, `last` 등.
- 컬렉션은 컨트롤러에서 `List`, `Set` 등으로 `model.addAttribute` 하면 됩니다.

예제: `/demo/loop`.

---

## 6. URL / 링크: `th:href` 와 `@{...}`

컨텍스트 경로를 자동으로 붙이려면 **`@{...}`** 를 씁니다.

```html
<a th:href="@{/list}">목록</a>
<a th:href="@{/item(id=${id})}">쿼리스트링</a>
<a th:href="@{/user/{uid}(uid=${user.id})}">경로 변수</a>
```

예제: `/demo/link`.

---

## 7. 폼: `th:action`, `th:object`, `th:field`

- `th:object="${form}"` : 폼에 바인딩할 객체(보통 빈 객체를 GET에서 넣음).
- `th:field="*{name}"` : `th:object` 기준으로 `name` 필드에 맞는 `name`/`id`/`value` 생성. CSRF가 켜져 있으면 hidden 토큰도 함께 처리됩니다.

```html
<form th:action="@{/demo/form}" th:object="${demoForm}" method="post">
  <input type="text" th:field="*{username}">
  <input type="checkbox" th:field="*{agree}">
</form>
```

Java 쪽은 getter/setter가 있는 일반 클래스(또는 필요 시 record 대신 클래스)를 쓰는 경우가 많습니다.

예제: `/demo/form` → POST → 결과 HTML.

---

## 8. 레이아웃 / 조각: `th:fragment`, `th:replace`, `th:insert`

**프래그먼트 정의** (`layouts/fragments.html` 등):

```html
<head th:fragment="head">
  <title th:text="${pageTitle}">기본 제목</title>
</head>
```

**다른 템플릿에서 끼워 넣기:**

```html
<head th:replace="~{layouts/fragments :: head}"></head>
```

- `th:replace` : **현재 태그를 통째로** 프래그먼트로 교체.
- `th:insert` : 현재 태그 **안에 자식으로** 삽입.

**파라미터:**

```html
<div th:fragment="alert (title)" class="alert" th:text="${title}"></div>
```

호출:

```html
<div th:replace="~{demo/partials/messageBox :: alert(title=${boxTitle})}"></div>
```

예제: `/demo/fragment`, 템플릿 `demo/partials/messageBox.html`.

---

## 9. 유틸 객체(자주 쓰는 것)

Spring 통합 시 예시:

- `#temporals` : `java.time` (`LocalDateTime` 등) 포맷.
- `#numbers` : 숫자 시퀀스 `th:each="n : ${#numbers.sequence(1,5)}"` 등.
- `#strings` : 문자열 유틸.

```html
<span th:text="${#temporals.format(submittedAt, 'yyyy-MM-dd HH:mm:ss')}"></span>
```

---

## 10. `crud2` 등 기존 프로젝트와의 대응

| 이 예제 | 흔한 게시판 프로젝트 |
|---------|---------------------|
| `layouts/fragments` | `layouts/header.html`, `footer.html` |
| `th:replace="~{layouts/header :: head}"` | 공통 `<head>` / 네비 |
| `th:object="${doDto}"` | 글쓰기/수정 폼 DTO |

---

## 11. 참고

- 공식 문서: [Thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html)
- Spring: [Spring MVC + Thymeleaf](https://docs.spring.io/spring-boot/reference/web/servlet.html#web.servlet.spring-mvc.template-engines)

**예제 코드 위치:** `D:\spring1\thymeleaf-examples\` (컨트롤러 `web/DemoController.java`, 템플릿 `src/main/resources/templates/`).
