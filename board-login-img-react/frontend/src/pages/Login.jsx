import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Login() {
  const { login } = useAuth()
  const nav = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [err, setErr] = useState('')

  async function onSubmit(e) {
    e.preventDefault()
    setErr('')
    const r = await login(username, password)
    if (r.ok) {
      nav('/posts')
    } else {
      setErr('아이디 또는 비밀번호가 올바르지 않습니다.')
    }
  }

  return (
    <div className="narrow">
      <h1>로그인</h1>
      {err && <p className="err">{err}</p>}
      <form onSubmit={onSubmit}>
        <label>
          아이디
          <input value={username} onChange={(e) => setUsername(e.target.value)} autoComplete="username" />
        </label>
        <label>
          비밀번호
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
          />
        </label>
        <button type="submit">로그인</button>
      </form>
      <p>
        <Link to="/register">회원가입</Link>
      </p>
    </div>
  )
}
