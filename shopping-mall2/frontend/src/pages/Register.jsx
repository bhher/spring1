import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { apiFetch } from '../api.js'

export default function Register() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '', name: '' })
  const [err, setErr] = useState(null)

  const onSubmit = async (e) => {
    e.preventDefault()
    setErr(null)
    await apiFetch('/api/auth/csrf').catch(() => {})
    const res = await apiFetch('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify(form),
    })
    if (!res.ok) {
      const j = await res.json().catch(() => ({}))
      setErr(j.message || '가입 실패')
      return
    }
    navigate('/login')
  }

  return (
    <div style={{ maxWidth: 420 }} className="mx-auto">
      <h1 className="h4 mb-3">회원가입</h1>
      {err && <div className="alert alert-danger">{err}</div>}
      <form onSubmit={onSubmit} className="card card-body">
        <div className="mb-3">
          <label className="form-label">이메일</label>
          <input
            type="email"
            className="form-control"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
            required
          />
        </div>
        <div className="mb-3">
          <label className="form-label">비밀번호 (4자 이상)</label>
          <input
            type="password"
            className="form-control"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            required
            minLength={4}
          />
        </div>
        <div className="mb-3">
          <label className="form-label">이름</label>
          <input
            className="form-control"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
            required
          />
        </div>
        <button type="submit" className="btn btn-primary w-100">
          가입
        </button>
        <Link className="btn btn-link" to="/login">
          로그인으로
        </Link>
      </form>
    </div>
  )
}
