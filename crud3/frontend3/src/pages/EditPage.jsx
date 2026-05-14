import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { fetchDoit, updateDoit } from '../api/doitsApi.js'

export default function EditPage() {
  const { num } = useParams()
  const navigate = useNavigate()
  const [title, setTitle] = useState('')
  const [content, setContent] = useState('')
  const [loading, setLoading] = useState(true)
  const [err, setErr] = useState(null)

  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setErr(null)
    fetchDoit(num)
      .then((row) => {
        if (cancelled || row === null) {
          if (!cancelled && row === null) setErr('글을 불러올 수 없습니다.')
          return
        }
        setTitle(row.title ?? '')
        setContent(row.content ?? '')
      })
      .catch(() => {
        if (!cancelled) setErr('글을 불러올 수 없습니다.')
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [num])

  const onSubmit = async (e) => {
    e.preventDefault()
    setErr(null)
    try {
      await updateDoit(num, { title, content })
      navigate(`/list/${num}`, { replace: true })
    } catch (e2) {
      setErr(e2.message)
    }
  }

  if (loading) return <p className="text-muted">불러오는 중…</p>
  if (err && !title && !content) return <div className="alert alert-warning">{err}</div>

  return (
    <>
      <h1 className="h3 mb-3">글 수정</h1>
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
          저장
        </button>
        <Link className="btn btn-link" to={`/list/${num}`}>
          취소
        </Link>
      </form>
    </>
  )
}
