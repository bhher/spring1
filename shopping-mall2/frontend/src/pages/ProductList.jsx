import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { apiFetch } from '../api.js'

export default function ProductList() {
  const [searchParams, setSearchParams] = useSearchParams()
  const keyword = searchParams.get('keyword') || ''
  const page = Number(searchParams.get('page') || '0')
  const [data, setData] = useState(null)
  const [err, setErr] = useState(null)

  useEffect(() => {
    setErr(null)
    const q = new URLSearchParams({ page: String(page), size: '12', sort: 'id,desc' })
    if (keyword) q.set('keyword', keyword)
    apiFetch(`/api/products?${q}`)
      .then((r) => {
        if (!r.ok) throw new Error('목록 로드 실패')
        return r.json()
      })
      .then(setData)
      .catch((e) => setErr(e.message))
  }, [keyword, page])

  const setKeyword = (kw) => {
    const next = new URLSearchParams(searchParams)
    if (kw) next.set('keyword', kw)
    else next.delete('keyword')
    next.set('page', '0')
    setSearchParams(next)
  }

  if (err) return <div className="alert alert-danger">{err}</div>
  if (!data) return <p className="text-muted">불러오는 중…</p>

  return (
    <div>
      <h1 className="h3 mb-3">상품 목록</h1>
      <form
        className="row g-2 mb-4"
        onSubmit={(e) => {
          e.preventDefault()
          setKeyword(e.target.keyword.value.trim())
        }}
      >
        <div className="col-auto flex-grow-1">
          <input name="keyword" className="form-control" placeholder="검색" defaultValue={keyword} />
        </div>
        <div className="col-auto">
          <button className="btn btn-outline-primary" type="submit">
            검색
          </button>
        </div>
      </form>
      <div className="row g-3">
        {data.content?.map((p) => (
          <div className="col-md-3" key={p.id}>
            <div className="card h-100">
              <img src={p.thumbnailUrl} className="card-img-top thumb" alt="" />
              <div className="card-body">
                <Link to={`/products/${p.id}`} className="stretched-link text-decoration-none">
                  {p.name}
                </Link>
                <p className="small text-muted mb-0">{p.price?.toLocaleString()}원</p>
              </div>
            </div>
          </div>
        ))}
      </div>
      <div className="d-flex justify-content-between mt-4">
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
        <span className="small text-muted">
          {page + 1} / {data.totalPages || 1}
        </span>
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
