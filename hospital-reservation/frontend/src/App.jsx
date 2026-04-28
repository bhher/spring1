import { useEffect, useState } from 'react'
import { Link, Route, Routes, useLocation, useNavigate, useParams } from 'react-router-dom'

const API_DOCTORS = '/api/doctors'
const API_RESERVATIONS = '/api/reservations'

async function fetchJson(url, options) {
  const res = await fetch(url, {
    headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
    ...options,
  })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || res.statusText)
  }
  if (res.status === 204) return null
  return res.json()
}

function Layout({ children }) {
  const location = useLocation()
  const flash = location.state?.msg
  return (
    <>
      <nav className="app-nav py-3 mb-4">
        <div className="container d-flex gap-3 align-items-center">
          <Link className="navbar-brand mb-0 text-decoration-none fw-bold" to="/doctors">
            hospital-reservation
          </Link>
          <Link className="btn btn-sm btn-outline-primary" to="/doctors">
            의사
          </Link>
          <Link className="btn btn-sm btn-outline-secondary" to="/reservations">
            예약
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

function DoctorsPage() {
  const location = useLocation()
  const [items, setItems] = useState([])
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    setError(null)
    fetchJson(API_DOCTORS)
      .then(setItems)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }, [location.key])

  if (loading) return <p className="text-muted">불러오는 중…</p>
  if (error) return <div className="alert alert-danger">{error}</div>

  return (
    <>
      <h1 className="h3 mb-3">의사 목록</h1>
      <table className="table table-bordered bg-white">
        <thead>
          <tr>
            <th>ID</th>
            <th>이름</th>
            <th>진료과</th>
            <th>예약</th>
          </tr>
        </thead>
        <tbody>
          {items.map((row) => (
            <tr key={row.id}>
              <td>{row.id}</td>
              <td>{row.name}</td>
              <td>{row.specialty}</td>
              <td>
                <Link className="btn btn-sm btn-primary" to={`/doctors/${row.id}/slots`}>
                  시간 선택
                </Link>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {items.length === 0 && <p className="text-muted">데이터가 없습니다.</p>}
    </>
  )
}

function SlotsPage() {
  const { doctorId } = useParams()
  const navigate = useNavigate()
  const [date, setDate] = useState(() => new Date().toISOString().slice(0, 10))
  const [slots, setSlots] = useState([])
  const [loading, setLoading] = useState(true)
  const [err, setErr] = useState(null)
  const [patientName, setPatientName] = useState('')
  const [patientPhone, setPatientPhone] = useState('')

  useEffect(() => {
    setLoading(true)
    setErr(null)
    fetchJson(`/api/doctors/${doctorId}/slots?date=${date}`)
      .then(setSlots)
      .catch((e) => setErr(e.message))
      .finally(() => setLoading(false))
  }, [doctorId, date])

  return (
    <>
      <h1 className="h3 mb-3">시간 선택</h1>
      <div className="card card-body mb-3">
        <div className="mb-3">
          <label className="form-label">날짜</label>
          <input className="form-control" type="date" value={date} onChange={(e) => setDate(e.target.value)} />
        </div>
        <div className="mb-3">
          <label className="form-label">예약자 이름</label>
          <input className="form-control" value={patientName} onChange={(e) => setPatientName(e.target.value)} />
        </div>
        <div className="mb-3">
          <label className="form-label">예약자 전화(선택)</label>
          <input className="form-control" value={patientPhone} onChange={(e) => setPatientPhone(e.target.value)} />
        </div>
        {err && <div className="alert alert-danger mb-0">{err}</div>}
      </div>

      {loading ? (
        <p className="text-muted">불러오는 중…</p>
      ) : (
        <table className="table table-bordered bg-white">
          <thead>
            <tr>
              <th>시작</th>
              <th>종료</th>
              <th>상태</th>
              <th>예약</th>
            </tr>
          </thead>
          <tbody>
            {slots.map((s) => (
              <tr key={s.id}>
                <td>{String(s.startAt).replace('T', ' ')}</td>
                <td>{String(s.endAt).replace('T', ' ')}</td>
                <td>{s.reserved ? '예약됨' : '가능'}</td>
                <td>
                  <button
                    className="btn btn-sm btn-primary"
                    disabled={s.reserved || !patientName}
                    onClick={async () => {
                      try {
                        const created = await fetchJson(API_RESERVATIONS, {
                          method: 'POST',
                          body: JSON.stringify({ slotId: s.id, patientName, patientPhone }),
                        })
                        navigate('/reservations', { replace: true, state: { msg: `예약 완료 (id=${created.id})` } })
                      } catch (e) {
                        setErr(e.message)
                      }
                    }}
                  >
                    예약
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </>
  )
}

function ReservationsPage() {
  const location = useLocation()
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    setLoading(true)
    setError(null)
    fetchJson(API_RESERVATIONS)
      .then(setItems)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false))
  }, [location.key])

  if (loading) return <p className="text-muted">불러오는 중…</p>
  if (error) return <div className="alert alert-danger">{error}</div>

  return (
    <>
      <h1 className="h3 mb-3">예약 목록</h1>
      <table className="table table-bordered bg-white">
        <thead>
          <tr>
            <th>ID</th>
            <th>Slot</th>
            <th>이름</th>
            <th>상태</th>
            <th>취소</th>
          </tr>
        </thead>
        <tbody>
          {items.map((r) => (
            <tr key={r.id}>
              <td>{r.id}</td>
              <td>{r.slotId}</td>
              <td>{r.patientName}</td>
              <td>{r.status}</td>
              <td>
                <button
                  className="btn btn-sm btn-outline-danger"
                  disabled={r.status !== 'BOOKED'}
                  onClick={async () => {
                    await fetchJson(`${API_RESERVATIONS}/cancel`, {
                      method: 'POST',
                      body: JSON.stringify({ reservationId: r.id }),
                    })
                    // 목록 재조회
                    fetchJson(API_RESERVATIONS).then(setItems)
                  }}
                >
                  취소
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  )
}

export default function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<DoctorsPage />} />
        <Route path="/doctors" element={<DoctorsPage />} />
        <Route path="/doctors/:doctorId/slots" element={<SlotsPage />} />
        <Route path="/reservations" element={<ReservationsPage />} />
      </Routes>
    </Layout>
  )
}
