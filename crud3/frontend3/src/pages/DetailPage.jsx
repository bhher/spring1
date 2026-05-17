import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { fetchDoit } from '../api/doitsApi.js'

export default function DetailPage() {
  const { num } = useParams()
  const [item, setItem] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    let cancelled = false
    setError(null)
    setItem(null)
    fetchDoit(num)
      .then((row) => {
        if (cancelled) return
        if (row === null) setError('없는 글이거나 오류가 났습니다.')
        else setItem(row)
      })
      .catch(() => {
        if (!cancelled) setError('없는 글이거나 오류가 났습니다.')
      })
    return () => {
      cancelled = true
    }
  }, [num])

  if (error) return <div className="alert alert-warning">{error}</div>
  if (!item) return <p className="text-muted">불러오는 중…</p>

  return (
    <>
      <h1 className="h3 mb-3">글 상세</h1>
      <div className="card">
        <div className="card-body">
          <h2 className="h5">{item.title}</h2>
          <p className="text-muted small">번호: {item.num}</p>
          <p className="mb-0">{item.content}</p>
        </div>
      </div>
      <div className="mt-3">
        <Link className="btn btn-primary me-2" to={`/list/${num}/edit`}>
          수정
        </Link>
        <Link className="btn btn-secondary" to="/list">
          목록
        </Link>
      </div>
    </>
  )
}
