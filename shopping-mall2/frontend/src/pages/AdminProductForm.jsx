import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { apiFetch } from '../api.js'

export default function AdminProductForm() {
  const { id } = useParams()
  const isNew = !id
  const navigate = useNavigate()
  const [form, setForm] = useState({
    name: '',
    description: '',
    price: '',
    stockQuantity: '',
  })
  const [images, setImages] = useState([])
  const [detail, setDetail] = useState(null)
  const [err, setErr] = useState(null)

  useEffect(() => {
    if (isNew) return
    apiFetch(`/api/admin/products/${id}`)
      .then((r) => {
        if (!r.ok) throw new Error('불러오기 실패')
        return r.json()
      })
      .then((payload) => {
        const f = payload.form
        setForm({
          name: f.name || '',
          description: f.description || '',
          price: f.price != null ? String(f.price) : '',
          stockQuantity: f.stockQuantity != null ? String(f.stockQuantity) : '',
        })
        setDetail(payload.detail)
      })
      .catch((e) => setErr(e.message))
  }, [id, isNew])

  const onSubmit = async (e) => {
    e.preventDefault()
    setErr(null)
    const payload = {
      name: form.name,
      description: form.description,
      price: Number(form.price),
      stockQuantity: Number(form.stockQuantity),
    }
    const fd = new FormData()
    fd.append('data', new Blob([JSON.stringify(payload)], { type: 'application/json' }))
    for (const file of images) {
      fd.append('images', file)
    }
    const url = isNew ? '/api/admin/products' : `/api/admin/products/${id}`
    const method = isNew ? 'POST' : 'PUT'
    const res = await apiFetch(url, { method, body: fd })
    if (!res.ok) {
      const j = await res.json().catch(() => ({}))
      setErr(j.message || '저장 실패')
      return
    }
    navigate('/admin/products')
  }

  return (
    <div style={{ maxWidth: 720 }}>
      <h1 className="h3 mb-3">{isNew ? '상품 등록' : '상품 수정'}</h1>
      {err && <div className="alert alert-danger">{err}</div>}
      {!isNew && detail?.images?.length > 0 && (
        <div className="mb-3">
          <p className="small text-muted">기존 이미지</p>
          <div className="d-flex flex-wrap gap-2">
            {detail.images.map((img) => (
              <img key={img.id} src={img.thumbnailUrl || img.url} alt="" className="thumb border rounded" />
            ))}
          </div>
        </div>
      )}
      <form onSubmit={onSubmit} className="card card-body">
        <div className="mb-3">
          <label className="form-label">이름</label>
          <input
            className="form-control"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
            required
          />
        </div>
        <div className="mb-3">
          <label className="form-label">설명</label>
          <textarea
            className="form-control"
            rows={5}
            value={form.description}
            onChange={(e) => setForm({ ...form, description: e.target.value })}
            required
          />
        </div>
        <div className="mb-3">
          <label className="form-label">가격</label>
          <input
            type="number"
            className="form-control"
            value={form.price}
            onChange={(e) => setForm({ ...form, price: e.target.value })}
            required
            min={0}
            step="0.01"
          />
        </div>
        <div className="mb-3">
          <label className="form-label">재고</label>
          <input
            type="number"
            className="form-control"
            value={form.stockQuantity}
            onChange={(e) => setForm({ ...form, stockQuantity: e.target.value })}
            required
            min={0}
          />
        </div>
        <div className="mb-3">
          <label className="form-label">추가 이미지 (여러 장)</label>
          <input type="file" className="form-control" multiple accept="image/*" onChange={(e) => setImages([...e.target.files])} />
        </div>
        <button type="submit" className="btn btn-primary">
          저장
        </button>
      </form>
    </div>
  )
}
