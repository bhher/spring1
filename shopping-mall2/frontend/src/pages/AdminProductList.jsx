import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { apiFetch } from '../api.js'

export default function AdminProductList() {
  const [searchParams, setSearchParams] = useSearchParams()
  const keyword = searchParams.get('keyword') || ''
  const page = Number(searchParams.get('page') || '0')
  const [data, setData] = useState(null)
  const [err, setErr] = useState(null)

  const load = () => {
    setErr(null)
    const q = new URLSearchParams({ page: String(page), size: '20', sort: 'id,desc' })
    if (keyword) q.set('keyword', keyword)
    apiFetch(`/api/admin/products?${q}`)
      .then((r) => {
        if (r.status === 403) throw new Error('관리자만 접근할 수 있습니다.')
        if (!r.ok) throw new Error('목록 로드 실패')
        return r.json()
      })
      .then(setData)
      .catch((e) => setErr(e.message))
  }

  useEffect(() => {
    load()
  }, [keyword, page])

  const del = async (pid) => {
    if (!confirm('삭제할까요?')) return
    await apiFetch(`/api/admin/products/${pid}`, { method: 'DELETE' })
    load()
  }

  if (err) return <div className="alert alert-danger">{err}</div>
  if (!data) return <p className="text-muted">불러오는 중…</p>

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h1 className="h3 mb-0">관리자 · 상품</h1>
        <Link className="btn btn-primary" to="/admin/products/new">
          상품 등록
        </Link>
      </div>
      <form
        className="row g-2 mb-3"
        onSubmit={(e) => {
          e.preventDefault()
          const next = new URLSearchParams(searchParams)
          const kw = e.target.keyword.value.trim()
          if (kw) next.set('keyword', kw)
          else next.delete('keyword')
          next.set('page', '0')
          setSearchParams(next)
        }}
      >
        <div className="col-auto flex-grow-1">
          <input name="keyword" className="form-control" placeholder="검색" defaultValue={keyword} />
        </div>
        <div className="col-auto">
          <button className="btn btn-outline-secondary" type="submit">
            검색
          </button>
        </div>
      </form>
      <table className="table table-bordered bg-white">
        <thead>
          <tr>
            <th>ID</th>
            <th>이름</th>
            <th>가격</th>
            <th>재고</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {data.content?.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.name}</td>
              <td>{p.price?.toLocaleString()}</td>
              <td>{p.stockQuantity}</td>
              <td>
                <Link className="btn btn-sm btn-outline-primary me-1" to={`/admin/products/${p.id}/edit`}>
                  수정
                </Link>
                <button type="button" className="btn btn-sm btn-outline-danger" onClick={() => del(p.id)}>
                  삭제
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className="d-flex justify-content-between">
        <button
          type="button"
          className="btn btn-outline-secondary"
          disabled={data.first}
          onClick={() => {
            const next = new URLSearchParams(searchParams)
            next.set('page', String(Math.max(0, page - 1)))
            setSearchParams(next)
          }}
        >
          이전
        </button>
        <button
          type="button"
          className="btn btn-outline-secondary"
          disabled={data.last}
          onClick={() => {
            const next = new URLSearchParams(searchParams)
            next.set('page', String(page + 1))
            setSearchParams(next)
          }}
        >
          다음
        </button>
      </div>
    </div>
  )
}
