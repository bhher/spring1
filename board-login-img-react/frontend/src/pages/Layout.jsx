import { Link, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Layout() {
  const { user, logout } = useAuth()

  return (
    <div className="layout">
      <header className="nav">
        <Link to="/">홈</Link>
        {' · '}
        <Link to="/posts">게시판</Link>
        {user ? (
          <>
            {' · '}
            <Link to="/posts/write">글쓰기</Link>
            {' · '}
            <span className="muted">
              {user.name}({user.username})
            </span>
            {' · '}
            <button type="button" className="linkish" onClick={() => logout()}>
              로그아웃
            </button>
          </>
        ) : (
          <>
            {' · '}
            <Link to="/login">로그인</Link>
            {' · '}
            <Link to="/register">회원가입</Link>
          </>
        )}
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  )
}
