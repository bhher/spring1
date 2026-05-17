import { useCallback, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { apiFetch } from '../api.js'

export default function Cart() {
  const navigate = useNavigate()
  const [lines, setLines] = useState([])
  const [err, setErr] = useState(null)
  const [msg, setMsg] = useState(null)

  const load = useCallback(() => {
    setErr(null)
    apiFetch('/api/cart').then((r) => {
      if (r.status === 401) {
        navigate('/login')
        return null
      }
      if (!r.ok) throw new Error('장바구니를 불러올 수 없습니다.')
      return r.json()
    })
      .then((data) => {
        if (data) setLines(data)
      })
      .catch((e) => setErr(e.message))
  }, [navigate])

  useEffect(() => {
    load()
  }, [load])

  const updateQty = async (itemId, quantity) => {
    setMsg(null)
    const res = await apiFetch(`/api/cart/items/${itemId}/quantity`, {
      method: 'PUT',
      body: JSON.stringify({ quantity: Number(quantity) }),
    })
    if (!res.ok) {
      const j = await res.json().catch(() => ({}))
      setMsg(j.message || '수정 실패')
      return
    }
    load()
  }

  const remove = async (itemId) => {
    setMsg(null)
    await apiFetch(`/api/cart/items/${itemId}`, { method: 'DELETE' })
    load()
  }

  if (err) return <div className="alert alert-danger">{err}</div>

  return (
    <div>
      <h1 className="h3 mb-3">장바구니</h1>
      {msg && <div className="alert alert-warning">{msg}</div>}
      {lines.length === 0 ? (
        <p className="text-muted">비어 있습니다.</p>
      ) : (
        <>
          <div className="mb-3">
            <button
              type="button"
              className="btn btn-success"
              onClick={async () => {
                setMsg(null)
                const res = await apiFetch('/api/orders/checkout', { method: 'POST' })
                const j = await res.json().catch(() => ({}))
                if (!res.ok) {
                  setMsg(j.message || '주문 실패')
                  return
                }
                navigate(`/mypage/orders/${j.orderId}`)
              }}
            >
              주문하기 (모의 결제)
            </button>
          </div>
          <table className="table table-bordered bg-white">
          <thead>
            <tr>
              <th>상품</th>
              <th>단가</th>
              <th>수량</th>
              <th>금액</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {lines.map((line) => (
              <tr key={line.cartItemId}>
                <td>{line.productName}</td>
                <td>{line.unitPrice?.toLocaleString()}</td>
                <td>
                  <input
                    type="number"
                    min={1}
                    className="form-control form-control-sm"
                    style={{ width: 90 }}
                    defaultValue={line.quantity}
                    onBlur={(e) => updateQty(line.cartItemId, e.target.value)}
                  />
                </td>
                <td>{line.lineAmount?.toLocaleString()}</td>
                <td>
                  <button type="button" className="btn btn-sm btn-outline-danger" onClick={() => remove(line.cartItemId)}>
                    삭제
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        </>
      )}
    </div>
  )
}
