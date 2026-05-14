import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { createDoit } from '../api/doitsApi.js'

export default function AddPage() {
  const navigate = useNavigate()
  const [title, setTitle] = useState('')
  const [content, setContent] = useState('')
  const [err, setErr] = useState(null)

  const onSubmit = async (e) => {
    e.preventDefault()
    setErr(null)
    try {
      const saved = await createDoit({ title, content })
      navigate(`/list/${saved.num}`, { replace: true })
    } catch (e2) {
      setErr(e2.message)
    }
  }

  return (
    <>
      <h1 className="h3 mb-3">글 작성</h1>
      {err && <div className="alert alert-danger">{err}</div>}
      <form onSubmit={onSubmit} className="card card-body">
        <div className="mb-3">
          <label className="form-label">제목</label>
          <input className="form-control" value={title} onChange={(e) => setTitle(e.target.value)} required />
        </div>
        <div className="mb-3">
          <label className="form-label">내용</label>
          <textarea className="form-control" rows={5} value={content} onChange={(e) => setContent(e.target.value)} required />
        </div>
        <button type="submit" className="btn btn-primary">
          등록
        </button>
        <Link className="btn btn-link" to="/list">
          취소
        </Link>
      </form>
    </>
  )
}
