import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { apiFetch } from '../api.js'

export default function ProductDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [p, setP] = useState(null)
  const [err, setErr] = useState(null)
  const [msg, setMsg] = useState(null)

  useEffect(() => {
    apiFetch(`/api/products/${id}`)
      .then((r) => {
        if (!r.ok) throw new Error('상품을 찾을 수 없습니다.')
        return r.json()
      })
      .then(setP)
      .catch((e) => setErr(e.message))
  }, [id])

  const addCart = async () => {
    setMsg(null)
    const res = await apiFetch('/api/cart/items', {
      method: 'POST',
      body: JSON.stringify({ productId: Number(id), quantity: 1 }),
    })
    if (res.status === 401) {
      navigate('/login')
      return
    }
    if (!res.ok) {
      const j = await res.json().catch(() => ({}))
      setMsg(j.message || '장바구니 담기 실패')
      return
    }
    setMsg('장바구니에 담았습니다.')
  }

  if (err) return <div className="alert alert-warning">{err}</div>
  if (!p) return <p className="text-muted">불러오는 중…</p>

  return (
    <div>
      <h1 className="h3 mb-3">{p.name}</h1>
      {msg && <div className="alert alert-info">{msg}</div>}
      <p className="text-muted">{p.price?.toLocaleString()}원 · 재고 {p.stockQuantity}</p>
      <p style={{ whiteSpace: 'pre-wrap' }}>{p.description}</p>
      <div className="row g-2 mb-3">
        {p.images?.map((img) => (
          <div className="col-md-3" key={img.id}>
            <img src={img.thumbnailUrl || img.url} className="img-fluid rounded border" alt="" />
          </div>
        ))}
      </div>
      <button type="button" className="btn btn-primary me-2" onClick={addCart}>
        장바구니 담기
      </button>
    </div>
  )
}
