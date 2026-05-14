import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { apiFetch } from '../api.js'

export default function OrderDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [order, setOrder] = useState(null)
  const [err, setErr] = useState(null)
  const [msg, setMsg] = useState(null)

  const load = () => {
    setErr(null)
    apiFetch(`/api/mypage/orders/${id}`)
      .then((r) => {
        if (!r.ok) throw new Error('주문을 불러올 수 없습니다.')
        return r.json()
      })
      .then(setOrder)
      .catch((e) => setErr(e.message))
  }

  useEffect(() => {
    load()
  }, [id])

  const cancel = async () => {
    setMsg(null)
    const res = await apiFetch(`/api/orders/${id}/cancel`, { method: 'POST' })
    const j = await res.json().catch(() => ({}))
    if (!res.ok) {
      setMsg(j.message || '취소 실패')
      return
    }
    setMsg(j.message || '취소되었습니다.')
    load()
  }

  if (err) return <div className="alert alert-danger">{err}</div>
  if (!order) return <p className="text-muted">불러오는 중…</p>

  return (
    <div>
      <h1 className="h3 mb-3">주문 상세</h1>
      {msg && <div className="alert alert-info">{msg}</div>}
      <p className="text-muted small">
        주문번호 {order.orderNumber} · 상태 {order.status} · 합계 {order.totalAmount?.toLocaleString()}원
      </p>
      <table className="table table-bordered bg-white">
        <thead>
          <tr>
            <th>상품</th>
            <th>단가</th>
            <th>수량</th>
            <th>소계</th>
          </tr>
        </thead>
        <tbody>
          {order.lines?.map((line, i) => (
            <tr key={i}>
              <td>{line.productName}</td>
              <td>{line.unitPrice?.toLocaleString()}</td>
              <td>{line.quantity}</td>
              <td>{line.lineTotal?.toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
      {order.status !== 'CANCELLED' && (
        <button type="button" className="btn btn-outline-danger" onClick={cancel}>
          주문 취소
        </button>
      )}
      <button type="button" className="btn btn-secondary ms-2" onClick={() => navigate('/mypage/orders')}>
        목록
      </button>
    </div>
  )
}
