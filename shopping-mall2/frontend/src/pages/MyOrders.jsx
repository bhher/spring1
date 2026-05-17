import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { apiFetch } from '../api.js'

export default function MyOrders() {
  const [orders, setOrders] = useState([])
  const [err, setErr] = useState(null)

  useEffect(() => {
    apiFetch('/api/mypage/orders')
      .then((r) => {
        if (!r.ok) throw new Error('주문 목록을 불러올 수 없습니다.')
        return r.json()
      })
      .then(setOrders)
      .catch((e) => setErr(e.message))
  }, [])

  if (err) return <div className="alert alert-danger">{err}</div>

  return (
    <div>
      <h1 className="h3 mb-3">내 주문</h1>
      <table className="table table-bordered bg-white">
        <thead>
          <tr>
            <th>주문번호</th>
            <th>금액</th>
            <th>상태</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {orders.map((o) => (
            <tr key={o.id}>
              <td>{o.orderNumber}</td>
              <td>{o.totalAmount?.toLocaleString()}</td>
              <td>{o.status}</td>
              <td>
                <Link className="btn btn-sm btn-outline-primary" to={`/mypage/orders/${o.id}`}>
                  상세
                </Link>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {orders.length === 0 && <p className="text-muted">주문 내역이 없습니다.</p>}
    </div>
  )
}
