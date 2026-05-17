import { useEffect, useState } from 'react'
import { Link, Route, Routes, useLocation, useNavigate, useParams } from 'react-router-dom'
import { axiosErrorMessage, doitsApi } from './api/client.js'

function Layout({ children }) {
  const location = useLocation()
  const flash = location.state?.msg
  return (
    <>
      <nav className="app-nav py-3 mb-4">
        <div className="container d-flex gap-3 align-items-center">
          <Link className="navbar-brand mb-0 text-decoration-none fw-bold" to="/list">
            crud3 (axios)
          </Link>
          <Link className="btn btn-sm btn-outline-primary" to="/list">
            목록
          </Link>
          <Link className="btn btn-sm btn-primary" to="/mains/add">
            글쓰기
          </Link>
        </div>
      </nav>
      {flash && (
        <div className="container flash-msg">
          <div className="alert alert-primary alert-dismissible fade show" role="alert">
            {flash}
            <button type="button" className="btn-close" data-bs-dismiss="alert" aria-label="Close" />
          </div>
        </div>
      )}
      <div className="container pb-5">{children}</div>
    </>
  )
}

function ListPage() {
  const location = useLocation()
  const [items, setItems] = useState([])
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    setError(null)
    doitsApi
      .get('')
      .then((res) => setItems(res.data))
      .catch((e) => setError(axiosErrorMessage(e)))
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

function DeleteButton({ num }) {
  const navigate = useNavigate()
  const onDelete = async () => {
    if (!confirm('삭제할까요?')) return
    try {
      const res = await doitsApi.delete(`/${num}`)
      if (res.status === 204) {
        navigate('/list', { replace: true, state: { msg: '삭제가 완료되었습니다.' } })
      } else {
        alert('삭제 실패')
      }
    } catch (e) {
      alert(axiosErrorMessage(e))
    }
  }
  return (
    <button type="button" className="btn btn-sm btn-outline-danger" onClick={onDelete}>
      삭제
    </button>
  )
}

function DetailPage() {
  const { num } = useParams()
  const [item, setItem] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    doitsApi
      .get(`/${num}`)
      .then((res) => setItem(res.data))
      .catch(() => setError('없는 글이거나 오류가 났습니다.'))
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

function AddPage() {
  const navigate = useNavigate()
  const [title, setTitle] = useState('')
  const [content, setContent] = useState('')
  const [err, setErr] = useState(null)

  const onSubmit = async (e) => {
    e.preventDefault()
    setErr(null)
    try {
      const { data: saved } = await doitsApi.post('', { title, content })
      navigate(`/list/${saved.num}`, { replace: true })
    } catch (e2) {
      setErr(axiosErrorMessage(e2))
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

function EditPage() {
  const { num } = useParams()
  const navigate = useNavigate()
  const [title, setTitle] = useState('')
  const [content, setContent] = useState('')
  const [loading, setLoading] = useState(true)
  const [err, setErr] = useState(null)

  useEffect(() => {
    doitsApi
      .get(`/${num}`)
      .then((res) => {
        const row = res.data
        setTitle(row.title ?? '')
        setContent(row.content ?? '')
      })
      .catch(() => setErr('글을 불러올 수 없습니다.'))
      .finally(() => setLoading(false))
  }, [num])

  const onSubmit = async (e) => {
    e.preventDefault()
    setErr(null)
    try {
      await doitsApi.put(`/${num}`, { title, content })
      navigate(`/list/${num}`, { replace: true })
    } catch (e2) {
      setErr(axiosErrorMessage(e2))
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

export default function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<ListPage />} />
        <Route path="/list" element={<ListPage />} />
        <Route path="/list/:num" element={<DetailPage />} />
        <Route path="/list/:num/edit" element={<EditPage />} />
        <Route path="/mains/add" element={<AddPage />} />
      </Routes>
    </Layout>
  )
}
