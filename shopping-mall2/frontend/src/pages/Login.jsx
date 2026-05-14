import { useState } from 'react'
import { apiFetch, xsrfHeader } from '../api.js'

export default function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [err, setErr] = useState(null)

  const onSubmit = async (e) => {
    e.preventDefault()
    setErr(null)
    await apiFetch('/api/auth/csrf').catch(() => {})
    const body = new URLSearchParams()
    body.append('username', email)
    body.append('password', password)
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      credentials: 'include',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        ...xsrfHeader(),
      },
      body,
    })
    if (!res.ok) {
      setErr('이메일 또는 비밀번호가 올바르지 않습니다.')
      return
    }
    window.location.assign('/')
  }

  return (
    <div style={{ maxWidth: 420 }} className="mx-auto">
      <h1 className="h4 mb-3">로그인</h1>
      {err && <div className="alert alert-danger">{err}</div>}
      <form onSubmit={onSubmit} className="card card-body">
        <div className="mb-3">
          <label className="form-label">이메일</label>
          <input type="email" className="form-control" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </div>
        <div className="mb-3">
          <label className="form-label">비밀번호</label>
          <input
            type="password"
            className="form-control"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        <button type="submit" className="btn btn-primary w-100">
          로그인
        </button>
      </form>
    </div>
  )
}
