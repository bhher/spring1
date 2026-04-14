# Mustache 정리 (Spring Boot · crud1 기준)

**Mustache**는 “**로직 없는(logic-less)**” 템플릿 문법입니다.  
HTML 안에 `{{이름}}`처럼 표시만 하고, **반복·조건은 최소한의 섹션 문법**으로만 처리합니다.  
(JSP에서 `if`/`for`처럼 자유롭게 Java 코드를 쓰는 방식과는 다릅니다.)

Spring Boot에서는 **`spring-boot-starter-mustache`**를 넣으면,  
`src/main/resources/templates/` 아래 **`.mustache` 파일**이 자동으로 뷰로 연결됩니다.

---

## crud1 · Mustache 관련 파일 수록

아래 경로는 **저장소 기준 `crud1/` 폴더**를 루트로 두었습니다. (실제 워크스페이스가 `d:\spring1`이면 `d:\spring1\crud1\...` 와 동일합니다.)

### `.mustache` 템플릿 (전체 7개)

| # | 프로젝트 내 경로 (crud1 기준) | 역할 |
|---|------------------------------|------|
| 1 | `src/main/resources/templates/index.mustache` | 홈 (`GET /`) |
| 2 | `src/main/resources/templates/layouts/header.mustache` | 공통 헤더·내비·플래시 알림 (partial) |
| 3 | `src/main/resources/templates/layouts/footer.mustache` | 공통 푸터·스크립트 (partial) |
| 4 | `src/main/resources/templates/mains/doList.mustache` | 목록 (`GET /list`) |
| 5 | `src/main/resources/templates/mains/detail.mustache` | 상세 (`GET /list/{num}`) |
| 6 | `src/main/resources/templates/mains/add.mustache` | 작성 폼 (`GET /mains/add`) |
| 7 | `src/main/resources/templates/mains/edit.mustache` | 수정 폼 (`GET /list/{num}/edit`) |

> **참고:** `header` / `footer`는 다른 페이지에서 `{{>layouts/header}}`, `{{>layouts/footer}}`로 끼워 넣는 **부분 템플릿**입니다.

**경로만 한 줄씩 (검색·복사용):**

```
crud1/src/main/resources/templates/index.mustache
crud1/src/main/resources/templates/layouts/header.mustache
crud1/src/main/resources/templates/layouts/footer.mustache
crud1/src/main/resources/templates/mains/doList.mustache
crud1/src/main/resources/templates/mains/detail.mustache
crud1/src/main/resources/templates/mains/add.mustache
crud1/src/main/resources/templates/mains/edit.mustache
```

### 뷰 이름을 반환하는 컨트롤러 (Mustache와 직접 연결)

| 프로젝트 내 경로 | 반환하는 뷰 이름 예 |
|------------------|---------------------|
| `src/main/java/com/example/crud1/controller/HomeController.java` | `index` |
| `src/main/java/com/example/crud1/controller/DoController.java` | `mains/doList`, `mains/detail`, `mains/add`, `mains/edit` |

### Mustache 의존성·설정이 있는 파일

| 프로젝트 내 경로 | 비고 |
|------------------|------|
| `build.gradle` | `spring-boot-starter-mustache` 선언 |
| `src/main/resources/application.properties` | 애플리케이션 공통 설정 (뷰 엔진 공통) |

---

## crud1 · Mustache 소스 전문 (프로젝트와 동일)

아래는 `crud1` 프로젝트에 있는 **`.mustache` 파일 내용 전부**입니다. (문서만 보고도 템플릿을 검토할 수 있도록 붙여 두었습니다.)

### `src/main/resources/templates/index.mustache`

```mustache
{{>layouts/header}}
<main class="container py-4">
	<div class="p-4 p-md-5 mb-4 bg-body-secondary rounded-3">
		<h1 class="display-6">crud1</h1>
		<p class="lead mb-3">Spring Boot + Mustache + JPA(H2) 로 만든 간단한 게시판 예제입니다.</p>
		<a class="btn btn-primary btn-lg" href="/list">글 목록 보기</a>
		<a class="btn btn-outline-secondary btn-lg ms-2" href="/mains/add">새 글 쓰기</a>
	</div>
</main>
{{>layouts/footer}}
```

### `src/main/resources/templates/layouts/header.mustache`

```mustache
<!doctype html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>{{#pageTitle}}{{pageTitle}} — {{/pageTitle}}crud1</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.6/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4Q6Gf2aSP4eDXB8Miphtr37CMZZQ5oXLH2yaXMJ2w8e2ZtHTl7GptT4jmndRuHDT" crossorigin="anonymous">
</head>
<body>
<nav class="navbar navbar-expand-lg bg-body-tertiary border-bottom">
    <div class="container-fluid">
        <a class="navbar-brand" href="/">crud1</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarMain"
                aria-controls="navbarMain" aria-expanded="false" aria-label="메뉴">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarMain">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="/">홈</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/list">목록</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="/mains/add">작성</a>
                </li>
            </ul>
        </div>
    </div>
</nav>
<!-- redirect 후 1회 표시되는 플래시 메시지: 문자열이면 본문은 {{.}} 로 출력 -->
{{#msg}}
<div class="container mt-3">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        {{.}}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="닫기"></button>
    </div>
</div>
{{/msg}}
```

### `src/main/resources/templates/layouts/footer.mustache`

```mustache
<div class="mb-5 container-fluid">
    <hr>
    <p>ⓒ CloudStudying | <a href="#">Privacy</a> | <a href="#">Terms</a></p>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.6/dist/js/bootstrap.bundle.min.js" integrity="sha384-j1CDi7MgGQ12Z7Qab0qlWQ/Qqz24Gc6BM0thvEMVjHnfYGF0rmFCozFSxQBxwHKO" crossorigin="anonymous"></script>
</body>
</html>
```

### `src/main/resources/templates/mains/doList.mustache`

```mustache
{{>layouts/header}}
<main class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h1 class="h3 mb-0">글 목록</h1>
        <a href="/mains/add" class="btn btn-primary">새 글</a>
    </div>
    <div class="table-responsive">
        <table class="table table-hover align-middle">
            <thead class="table-light">
            <tr>
                <th scope="col" style="width:5rem">번호</th>
                <th scope="col">제목</th>
                <th scope="col">내용</th>
            </tr>
            </thead>
            <tbody>
            {{#DoList}}
                <tr>
                    <td>{{num}}</td>
                    <td><a href="/list/{{num}}" class="text-decoration-none fw-medium">{{title}}</a></td>
                    <td class="text-muted small text-truncate" style="max-width:24rem">{{content}}</td>
                </tr>
            {{/DoList}}
            </tbody>
        </table>
    </div>
    {{^DoList}}
    <p class="text-muted py-5 text-center">등록된 글이 없습니다. <a href="/mains/add">첫 글 작성하기</a></p>
    {{/DoList}}
</main>
{{>layouts/footer}}
```

### `src/main/resources/templates/mains/detail.mustache`

```mustache
{{>layouts/header}}
<main class="container py-4">
    <h1 class="h3 mb-4">글 상세</h1>
    {{#detail}}
    <div class="table-responsive mb-4">
        <table class="table table-bordered">
            <tbody>
            <tr>
                <th class="table-light" style="width:8rem">번호</th>
                <td>{{num}}</td>
            </tr>
            <tr>
                <th class="table-light">제목</th>
                <td>{{title}}</td>
            </tr>
            <tr>
                <th class="table-light">내용</th>
                <td style="white-space: pre-wrap; word-break: break-word;">{{content}}</td>
            </tr>
            </tbody>
        </table>
    </div>
    <div class="d-flex flex-wrap gap-2">
        <a href="/list/{{num}}/edit" class="btn btn-primary">수정</a>
        <a href="/list/{{num}}/delete" class="btn btn-outline-danger" onclick="return confirm('삭제할까요?');">삭제</a>
        <a href="/list" class="btn btn-outline-secondary">목록</a>
    </div>
    {{/detail}}
    {{^detail}}
    <p class="text-muted">존재하지 않는 글입니다.</p>
    <a href="/list" class="btn btn-outline-secondary">목록으로</a>
    {{/detail}}
</main>
{{>layouts/footer}}
```

### `src/main/resources/templates/mains/add.mustache`

```mustache
{{>layouts/header}}
<main class="container py-4">
    <h1 class="h3 mb-4">새 글 작성</h1>
    <form action="/mains/create" method="post" class="needs-validation">
        <div class="mb-3">
            <label class="form-label">제목</label>
            <input type="text" class="form-control" name="title" required maxlength="500">
        </div>
        <div class="mb-3">
            <label class="form-label">내용</label>
            <textarea class="form-control" rows="5" name="content" required></textarea>
        </div>
        <button type="submit" class="btn btn-primary">등록</button>
        <a href="/list" class="btn btn-outline-secondary">목록</a>
    </form>
</main>
{{>layouts/footer}}
```

### `src/main/resources/templates/mains/edit.mustache`

```mustache
{{>layouts/header}}
<main class="container py-4">
    <h1 class="h3 mb-4">글 수정</h1>
    {{#editData}}
    <form action="/mains/update" method="post" class="needs-validation">
        <input type="hidden" name="num" value="{{num}}">
        <div class="mb-3">
            <label class="form-label">제목</label>
            <input type="text" class="form-control" name="title" value="{{title}}" required>
        </div>
        <div class="mb-3">
            <label class="form-label">내용</label>
            <textarea class="form-control" rows="5" name="content" required>{{content}}</textarea>
        </div>
        <button type="submit" class="btn btn-primary">저장</button>
        <a href="/list/{{num}}" class="btn btn-outline-secondary">취소</a>
    </form>
    {{/editData}}
    {{^editData}}
    <p class="text-muted">수정할 글을 찾을 수 없습니다.</p>
    <a href="/list" class="btn btn-outline-secondary">목록으로</a>
    {{/editData}}
</main>
{{>layouts/footer}}
```

> **주의:** 위 블록은 문서용이며, 실제 동작은 항상 `src/main/resources/templates/` 아래 원본 파일을 기준으로 합니다. 원본을 고친 뒤 문서와 맞추려면 이 섹션도 함께 수정하세요.

---

## 1. Spring Boot에서의 동작 방식

| 항목 | 내용 |
|------|------|
| 의존성 | `implementation 'org.springframework.boot:spring-boot-starter-mustache'` |
| 파일 위치 | `src/main/resources/templates/` (하위 폴더 가능) |
| 컨트롤러 반환값 | `return "mains/doList"` → `templates/mains/doList.mustache` |
| 데이터 전달 | `Model` / `ModelAndView`에 `model.addAttribute("키", 값)` → 템플릿의 `{{키}}`와 매칭 |

컨트롤러 예 (crud1 `DoController`):

```java
model.addAttribute("DoList", doList);  // 템플릿에서 {{#DoList}} ... {{/DoList}}
model.addAttribute("detail", doIt);    // 템플릿에서 {{#detail}} ... {{/detail}}
// redirect 후 플래시 (문자열이면 템플릿 안에서는 {{#msg}}{{.}}{{/msg}} 권장)
rttr.addFlashAttribute("msg", "삭제가 완료되었습니다.");
```

---

## 2. 기본 문법 요약

### 2-1. 값 출력 (이스케이프 O)

```
{{변수명}}
```

- 모델에 넣은 객체의 **프로퍼티 이름**과 맞춥니다 (JavaBean getter 규칙: `getTitle()` → `title`).
- HTML 특수문자는 **자동 이스케이프**되어 XSS를 줄입니다.

### 2-2. HTML 그대로 출력 (이스케이프 X)

```
{{{변수명}}}
```

- 신뢰할 수 있는 HTML만 넣을 때만 사용합니다.

### 2-3. 섹션: 반복 또는 “객체 블록”

```
{{#리스트또는객체}}
  ... 내부에서 필드 접근 ...
{{/리스트또는객체}}
```

- **리스트(Array/List)**이면: 항목 개수만큼 **반복**합니다.
- **단일 객체**이면: 그 객체를 **현재 컨텍스트**로 잡고, 안쪽에서 필드를 씁니다 (한 번만 출력).

crud1 예:

- 목록: `{{#DoList}}` … `{{/DoList}}` — `DoList`가 리스트 → 각 `DoIt`마다 한 행.
- 상세: `{{#detail}}` … `{{/detail}}` — `detail`이 객체 → 블록 안에서 `num`, `title`, `content`.

### 2-4. 역섹션: 비었거나 false일 때

```
{{^변수}}
  ... 없을 때 보여줄 내용 ...
{{/변수}}
```

- 리스트가 비었거나, 값이 null/false일 때 등에 쓸 수 있습니다.

### 2-5. 부분 템플릿(Partial): 다른 파일 끼워 넣기

```
{{>layouts/header}}
```

- `templates/layouts/header.mustache`를 **이 위치에 삽입**합니다.  
- 공통 헤더·푸터를 나눌 때 사용합니다 (crud1에서 `header` / `footer`).

경로 규칙: `{{>layouts/header}}` → `templates/layouts/header.mustache` (확장자 제외)

### 2-6. 주석

```
{{! 이 줄은 화면에 안 나옵니다 }}
```

---

## 3. crud1에서의 매핑 정리

| 템플릿 | 컨트롤러에서 넣는 값 | Mustache에서의 의미 |
|--------|----------------------|----------------------|
| `mains/doList.mustache` | `DoList` → 글 목록 | `{{#DoList}}` … `{{/DoList}}` 안에서 각 항목의 `{{num}}`, `{{title}}`, `{{content}}` |
| `mains/detail.mustache` | `detail` → `DoIt` 한 건 | `{{#detail}}` 블록 또는 `{{detail.num}}`처럼 점 표기 |
| `layouts/header.mustache` | `pageTitle` (선택), 리다이렉트 후 `msg` 플래시 | `<title>`에 `pageTitle` 반영; 플래시는 `{{#msg}}{{.}}{{/msg}}` (**문자열은 `{{.}}`로 본문 출력**) |
| 공통 | `{{>layouts/header}}`, `{{>layouts/footer}}` | 모든 본문 페이지 상·하단 공통 |
| `index.mustache` | `pageTitle` → `"홈"` | 루트 `/` 안내 화면 |

**이름 규칙 주의:** Mustache는 대소문자를 구분합니다. 컨트롤러의 `"DoList"`와 템플릿의 `{{#DoList}}`가 **정확히 같아야** 합니다.

---

## 4. 자주 헷갈리는 점

1. **`{{#detail}}` 안의 `{{num}}` vs `{{detail.num}}`**  
   섹션 안에 들어가면 “detail이 가리키는 객체”가 컨텍스트가 되어 `{{num}}`만 써도 됩니다.  
   섹션 밖에서는 `{{detail.num}}`처럼 경로를 적습니다.

2. **빈 리스트**  
   `{{#DoList}}` … `{{/DoList}}` 사이는 **한 번도 출력되지 않습니다.**  
   “글 없음” 메시지는 `{{^DoList}}` 역섹션을 쓰거나, 컨트롤러에서 별도 플래그를 줄 수 있습니다.

3. **로직은 템플릿이 아니라 서버**  
   복잡한 정렬·필터·권한 검사는 **Controller / Service**에서 끝내고, Mustache에는 **이미 준비된 데이터**만 넘기는 편이 맞습니다.

4. **Thymeleaf와 비교 (crud2)**  
   - Mustache: 문법이 단순하고, 서버 태그가 HTML에 거의 안 보임.  
   - Thymeleaf: `th:text`, `th:each` 등으로 HTML 속성에 바인딩, IDE 지원·폼 연동이 풍부함.

---

## 5. URL · 모델 요약 (파일 경로는 상단 § 수록 참고)

| 뷰 (템플릿) | HTTP | 모델(주요 키) |
|-------------|------|---------------|
| `index` | `GET /` | `pageTitle` |
| `mains/doList` | `GET /list` | `DoList`, `pageTitle` |
| `mains/detail` | `GET /list/{num}` | `detail`, `pageTitle` |
| `mains/add` | `GET /mains/add` | `pageTitle` |
| `mains/edit` | `GET /list/{num}/edit` | `editData`, `pageTitle` |
| `layouts/header`, `layouts/footer` | (partial) | `pageTitle`, `msg` 등 |

- **부분 템플릿:** 본문은 `{{>layouts/header}}` … `{{>layouts/footer}}`로 감쌉니다.  
- **역섹션 예:** `{{^DoList}}`, `{{^detail}}`, `{{^editData}}`

---

## 6. 한 줄 요약

- **`{{이름}}`**: 모델 값 출력.  
- **`{{#이름}}` … `{{/이름}}`**: 리스트면 반복, 객체면 한 블록.  
- **`{{>경로}}`**: 다른 `.mustache` 파일을 부분 삽입.  
- Spring Boot는 **`templates/…/*.mustache`**와 **`return "경로(확장자 제외)"`**만 맞추면 연결됩니다.

이 구조가 **crud1**에서 사용하는 Mustache 구성입니다.
