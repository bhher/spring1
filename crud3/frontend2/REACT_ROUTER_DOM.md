# React Router DOM (v6) 요약

이 프로젝트는 **React Router DOM v6** (`^6.28.0`)을 사용합니다. 아래는 v6 기준 API 정리입니다.

---

## 라우터를 감싸는 것

| 이름 | 역할 |
|------|------|
| **`BrowserRouter`** | HTML5 History API로 URL을 바꿉니다. 보통 `main.jsx`에서 앱 전체를 한 번 감쌉니다. |

---

## 경로 정의 (선언적)

| 이름 | 역할 |
|------|------|
| **`Routes`** | 그 안의 **`Route`** 중에서 **현재 URL과 맞는 하나**만 렌더합니다. (v5의 `Switch` 역할) |
| **`Route`** | `path`와 `element`(또는 예전 방식 `component`)로 “이 URL이면 이 UI”를 연결합니다. |
| **`Navigate`** | 렌더되면 **다른 경로로 리다이렉트**합니다. 예: 로그인 필요 시 `/login`으로 보내기. |

중첩 라우트를 쓸 때는 부모 `Route` 안에 자식 `Route`를 넣고, 부모 `element` 안에 **`Outlet`**을 두어 자식이 그 자리에 그려지게 합니다.

---

## 링크·탐색

| 이름 | 역할 |
|------|------|
| **`Link`** | `<a href>` 대신 쓰는 링크. 클릭 시 **전체 새로고침 없이** SPA 내에서 경로만 바꿉니다. `to="/path"` 형태. |
| **`NavLink`** | `Link` + **현재 경로와 같을 때 클래스/스타일**을 줄 수 있어서 메뉴 강조에 쓰기 좋습니다. |

---

## 훅 (함수형 컴포넌트에서 “명령”에 가까운 것들)

| 훅 | 역할 |
|-----|------|
| **`useNavigate()`** | `navigate('/path')`, `navigate(-1)`처럼 **코드로 이동**합니다. 폼 제출 후 이동, 버튼 클릭 시 이동에 사용. |
| **`useParams()`** | `Route`의 `path`에 `:id` 같은 **동적 세그먼트** 값을 객체로 받습니다. 예: `/user/:id` → `{ id: '123' }`. |
| **`useLocation()`** | 현재 **URL 경로, 쿼리, state** 등이 담긴 location 객체. 같은 경로라도 state만 바뀌는 경우 등에 유용. |
| **`useSearchParams()`** | 쿼리스트링 `?a=1&b=2`를 읽고/바꿀 때 사용 (URLSearchParams와 비슷한 느낌). |
| **`useOutletContext()`** | 부모가 `<Outlet context={값} />`으로 넘긴 값을 자식 라우트 컴포넌트에서 받을 때 사용. |

---

## 자주 헷갈리는 점 (v6)

- **`element={<SomeComponent />}`** 형태가 기본입니다. 예전 v5의 `component={SomeComponent}`만 쓰던 방식과 문법이 다릅니다.
- **`Routes` 안에는 `Route`만** 두는 것이 일반적입니다. (조건부로 여러 `Route`를 두면 매칭 규칙에 따라 하나가 선택됩니다.)

---

## 이 프로젝트에서의 사용 예

`App.jsx` 등에서 다음을 import해 사용할 수 있습니다.

```js
import { Link, Route, Routes, useLocation, useNavigate, useParams } from 'react-router-dom'
```

`main.jsx`에서는 앱을 `BrowserRouter`로 감쌉니다.

---

## 공식 문서

- [React Router v6 문서](https://reactrouter.com/en/main)
