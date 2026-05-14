# `App.tsx` 전체 정리 (TSX)

`crud3/frontend4/src/App.tsx` 한 파일에 레이아웃, API 헬퍼(`fetchJson<T>`), 게시판 CRUD 페이지, 라우팅이 모두 들어 있습니다. 확장자는 **`.tsx`** (TypeScript + JSX)입니다.

---

## 0. TSX에서의 엔트리

- `index.html` → `/src/main.tsx`
- `main.tsx`에서 `BrowserRouter`로 `App` 렌더
- 타입 선언: `src/vite-env.d.ts` (`/// <reference types="vite/client" />`)
- 빌드 설정: `tsconfig.json`, `vite.config.js`

---

## 1. 상수·API 헬퍼

### `API`

- 값: `'/api/doits'`
- 백엔드 게시글 리소스의 베이스 URL로 사용합니다.

### `fetchJson<T>(url, options?)`

`fetch`를 감싸 JSON 요청/응답을 통일합니다. 반환 타입은 **`Promise<T>`** (비동기로 나중에 `T`가 온다는 뜻).

```ts
async function fetchJson<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(url, {
    headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
    ...options,
  })
  // ...
}
```

| 동작 | 설명 |
|------|------|
| 기본 헤더 | JSON 요청/응답에 맞는 `Content-Type`, `Accept` 설정 |
| `...options` | `options?: RequestInit`를 같은 객체에 펼침 (`method`, `body` 등) |
| `options` 생략 | `undefined`를 펼쳐도 문제 없음 |
| 실패 시 | `!res.ok`이면 본문 텍스트(또는 status)로 `Error` throw |
| 204 | 본문 없음 → `null as T` 반환 (타입 맞추기용 캐스팅) |
| 그 외 성공 | `res.json()` 결과를 `T`로 캐스팅해 반환 |

**주의:** `options`에 `headers`를 넣으면 기본 `headers`가 통째로 덮일 수 있습니다.

**이 파일에서 `fetch`를 직접 쓰는 곳**

- `DeleteButton`: `DELETE`는 상태 코드(204 등)만 보면 되어 `fetchJson` 대신 `fetch` 사용.

### `DoIt` 타입

```ts
type DoIt = {
  num: number
  title: string
  content: string
}
```

예: `useState<DoIt[]>([])`, `fetchJson<DoIt[]>(API)`, `fetchJson<DoIt>(...)`.

---

## 2. 컴포넌트 요약

### `Layout`

- 네비: `Link`로 목록·글쓰기.
- 플래시 메시지: `location.state`에서 `msg` 읽기. TS에서는 타입 단언을 씁니다.

```ts
const flash = (location.state as { msg?: string } | null)?.msg
```

- `children` 타입: `{ children: React.ReactNode }`

### `ListPage`

- `fetchJson<DoIt[]>(API)`로 목록 로드.
- `useEffect` 의존성 `[location.key]`: 같은 `/list`라도 이동할 때마다 다시 조회.

### `DeleteButton`

- `DELETE` `${API}/${num}`, 204면 `navigate` + `state.msg`.

### `DetailPage` / `EditPage`

- `useParams<{ num: string }>()`로 URL의 `:num` 읽기.
- 단건/수정은 `fetchJson<Doit>` 또는 `PUT`에 `fetchJson` 사용.

### `AddPage`

- 폼 `onSubmit` 타입: `React.FormEvent<HTMLFormElement>`
- `POST` 후 `saved.num`으로 이동.

### 기본 export `App`

- `Layout` + `Routes` / `Route`.

---

## 3. 라우트 표

| 경로 | 컴포넌트 | 역할 |
|------|----------|------|
| `/` | `ListPage` | 목록 (루트) |
| `/list` | `ListPage` | 목록 |
| `/list/:num` | `DetailPage` | 상세 |
| `/list/:num/edit` | `EditPage` | 수정 |
| `/mains/add` | `AddPage` | 작성 |

---

## 4. React Router DOM (v6)

| API | 용도 |
|-----|------|
| `Link` | SPA 내 이동 |
| `Route`, `Routes` | URL별 화면 매칭 |
| `useNavigate` | 코드로 이동 |
| `useParams` | 동적 세그먼트 |
| `useLocation` | `key`(재조회), `state`(플래시) |

자세한 설명: 같은 폴더의 `REACT_ROUTER_DOM.md`.

---

## 5. 백엔드 계약(개념)

- `GET /api/doits` — 목록
- `GET /api/doits/{num}` — 단건
- `POST /api/doits` — 생성
- `PUT /api/doits/{num}` — 수정
- `DELETE /api/doits/{num}` — 삭제 (204 기대)

---

## 6. 의존성

- React 18, `react-router-dom` v6
- TypeScript: `typescript`, `@types/react`, `@types/react-dom`
- UI: Bootstrap (CDN)
