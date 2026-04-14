import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { register } from '../api'

export default function Register() {
  const nav = useNavigate()
  const [form, setForm] = useState({
    username: '',
    password: '',
    passwordConfirm: '',
    name: '',
  })
  const [err, setErr] = useState('')

  async function onSubmit(e) {
    e.preventDefault()
    setErr('')
    const res = await register(form)
    if (res.status === 201) {
      nav('/login')
      return
    }
    const text = await res.text()
    setErr(text || '가입에 실패했습니다.')
  }

  function set(k, v) {
    setForm((f) => ({ ...f, [k]: v }))
  }

  return (
    <div className="narrow">
      <h1>회원가입</h1>
      {err && <p className="err">{err}</p>}
      <form onSubmit={onSubmit}>
        <label>
          아이디
          <input value={form.username} onChange={(e) => set('username', e.target.value)} />
        </label>
        <label>
          비밀번호
          <input type="password" value={form.password} onChange={(e) => set('password', e.target.value)} />
        </label>
        <label>
          비밀번호 확인
          <input
            type="password"
            value={form.passwordConfirm}
            onChange={(e) => set('passwordConfirm', e.target.value)}
          />
        </label>
        <label>
          이름
          <input value={form.name} onChange={(e) => set('name', e.target.value)} />
        </label>
        <button type="submit">가입</button>
      </form>
      <p>
        <Link to="/login">로그인</Link>
      </p>
    </div>
  )
}
