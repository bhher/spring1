# `App.jsx` 전체 정리

`crud3/frontend/src/App.jsx` 한 파일에 레이아웃, API 헬퍼, 게시판 CRUD 페이지, 라우팅이 모두 들어 있습니다.

---

## 1. 상수·API 헬퍼

### `API`

- 값: `'/api/doits'`
- 백엔드 게시글 리소스의 베이스 URL로 사용합니다.

### `fetchJson(url, options)`

`fetch`를 감싸 JSON 요청/응답을 통일합니다.

```js
const res = await fetch(url, {
  headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
  ...options,
})
```

| 동작 | 설명 |
|------|------|
| 기본 헤더 | JSON 요청/응답에 맞는 `Content-Type`, `Accept` 설정 |
| `...options` | `options` 객체 속성을 **같은 설정 객체에 펼쳐 넣음** (전개 연산자). `method`, `body` 등 `fetch` 두 번째 인자와 동일한 형태로 넘깁니다. |
| `options` 생략 | `undefined`를 펼쳐도 문제 없고, 기본 헤더만 적용됩니다. |
| 실패 시 | `!res.ok`이면 본문 텍스트(또는 status)로 `Error` throw |
| 204 | 본문 없음 → `null` 반환 |
| 그 외 성공 | `res.json()` 반환 |

**`...options`가 의미하는 것**

- 호출부에서 `fetchJson(API, { method: 'POST', body: JSON.stringify({...}) })`처럼 넘기면, 최종 `fetch` 인자는 `{ headers: {...}, method: 'POST', body: '...' }`가 됩니다.
- **주의:** 객체에서 뒤에 오는 키가 앞을 덮습니다. `options`에 `headers`를 넣으면 **기본 `headers` 전체가 교체**될 수 있습니다. 인증 헤더만 추가하려면 호출부에서 기본 헤더를 다시 합치거나, 헬퍼에서 `headers` 병합 로직을 따로 두는 방식이 필요합니다.

**이 파일에서 `fetch`를 직접 쓰는 곳**

- `DeleteButton`: `DELETE`만 쓰고 응답은 상태 코드 위주라 `fetchJson` 대신 `fetch`를 직접 사용합니다.

---

## 2. 컴포넌트 요약

### `Layout({ children })`

- 상단 네비: 브랜드·목록·글쓰기 링크 (`react-router-dom`의 `Link`).
- `useLocation()`으로 `location.state?.msg`를 읽어 **플래시 메시지** 영역 표시 (예: 삭제 후 `navigate(..., { state: { msg } })`).
- 본문은 `{children}` — 아래 `Routes`의 각 페이지가 여기 들어갑니다.

### `ListPage`

- `GET` `API`로 목록 로드 (`fetchJson(API)`).
- `useEffect` 의존성 `[location.key]`: 같은 `/list`라도 네비게이션할 때마다 목록을 다시 불러옵니다.
- 테이블에 `num`, `title`, `content`, 수정/삭제 버튼.
- 제목 링크 → 상세 `/list/:num`.

### `DeleteButton({ num })`

- 확인 후 `DELETE` `${API}/${num}`.
- 성공(204) 시 `navigate('/list', { replace: true, state: { msg: '...' } })`로 목록으로 이동하며 메시지 전달.

### `DetailPage`

- `useParams()`로 `num` 추출.
- `GET` `${API}/${num}` → 상세 카드, 수정/목록 링크.

### `AddPage`

- 제목·내용 폼 → `POST` `API` (`fetchJson`에 `method`, `body`).
- 성공 시 `navigate(\`/list/${saved.num}\`, { replace: true })`.

### `EditPage`

- `num`으로 기존 글 `GET` 후 폼에 채움.
- 제출 시 `PUT` `${API}/${num}` → 상세로 이동.
- 로딩/에러 분기 처리.

### `App` (기본보내기)

- `Layout`으로 감싼 뒤 그 안에 `Routes` / `Route` 정의.

---

## 3. 라우트 표

| 경로 | 컴포넌트 | 역할 |
|------|----------|------|
| `/` | `ListPage` | 목록 (루트) |
| `/list` | `ListPage` | 목록 |
| `/list/:num` | `DetailPage` | 상세 (`num` 동적) |
| `/list/:num/edit` | `EditPage` | 수정 |
| `/mains/add` | `AddPage` | 작성 |

---

## 4. 이 파일에서 쓰는 React Router DOM

| API | 용도 |
|-----|------|
| `Link` | SPA 내 이동 (목록·상세·수정·글쓰기) |
| `Route`, `Routes` | URL별 페이지 매칭 |
| `useNavigate` | 삭제/등록/수정 후 프로그램matic 이동 |
| `useParams` | 상세·수정에서 `:num` 읽기 |
| `useLocation` | 목록 재조회 트리거(`key`), 플래시 메시지(`state`) |

더 넓은 React Router v6 설명은 `REACT_ROUTER_DOM.md`를 참고하면 됩니다.

---

## 5. 백엔드와의 계약(개념)

- 목록: `GET /api/doits`
- 단건: `GET /api/doits/{num}`
- 생성: `POST /api/doits` + JSON `{ title, content }` → 응답에 `num` 등
- 수정: `PUT /api/doits/{num}` + JSON
- 삭제: `DELETE /api/doits/{num}` → 204 기대

실제 스키마는 Spring 쪽 컨트롤러/DTO와 맞춰져 있어야 합니다.

---

## 6. 의존성

- React: `useState`, `useEffect`
- `react-router-dom` v6
- UI: Bootstrap 클래스 (`container`, `btn`, `card` 등)
