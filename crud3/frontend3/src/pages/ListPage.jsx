import { useEffect, useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { fetchDoitList } from '../api/doitsApi.js'
import DeleteButton from '../components/DeleteButton.jsx'

export default function ListPage() {
  const location = useLocation()
  const [items, setItems] = useState([])
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    setError(null)
    fetchDoitList()
      .then(setItems)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }, [location.key])

  if (loading) return <p className="text-muted">불러오는 중…</p>
  if (error) return <div className="alert alert-danger">{error}</div>

  return (
    <>
      <h1 className="h3 mb-3">게시판</h1>
      <table className="table table-bordered bg-white">
        <thead>
          <tr>
            <th>Num</th>
            <th>Title</th>
            <th>Content</th>
            <th>액션</th>
          </tr>
        </thead>
        <tbody>
          {items.map((row) => (
            <tr key={row.num}>
              <td>{row.num}</td>
              <td>
                <Link to={`/list/${row.num}`}>{row.title}</Link>
              </td>
              <td>{row.content}</td>
              <td>
                <Link className="btn btn-sm btn-outline-primary me-1" to={`/list/${row.num}/edit`}>
                  수정
                </Link>
                <DeleteButton num={row.num} />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {items.length === 0 && <p className="text-muted">데이터가 없습니다.</p>}
    </>
  )
}
