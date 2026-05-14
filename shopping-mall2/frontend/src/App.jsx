import { Link, NavLink, Route, Routes, useNavigate } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { apiFetch } from './api.js'
import Home from './pages/Home.jsx'
import ProductList from './pages/ProductList.jsx'
import ProductDetail from './pages/ProductDetail.jsx'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import Cart from './pages/Cart.jsx'
import MyOrders from './pages/MyOrders.jsx'
import OrderDetail from './pages/OrderDetail.jsx'
import AdminProductList from './pages/AdminProductList.jsx'
import AdminProductForm from './pages/AdminProductForm.jsx'

function NavBar() {
  const [me, setMe] = useState(null)
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  const loadMe = async () => {
    setLoading(true)
    try {
      const res = await apiFetch('/api/auth/me')
      if (res.ok) setMe(await res.json())
      else setMe(null)
    } catch {
      setMe(null)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadMe()
  }, [])

  const logout = async () => {
    await apiFetch('/api/auth/logout', { method: 'POST' })
    setMe(null)
    navigate('/')
  }

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
      <div className="container">
        <Link className="navbar-brand" to="/">
          shopping-mall2
        </Link>
        <div className="navbar-nav flex-row gap-2 flex-wrap align-items-center">
          <NavLink className="nav-link" to="/products">
            상품
          </NavLink>
          {me && (
            <NavLink className="nav-link" to="/cart">
              장바구니
            </NavLink>
          )}
          {me && (
            <NavLink className="nav-link" to="/mypage/orders">
              내 주문
            </NavLink>
          )}
          {me?.role === 'ADMIN' && (
            <NavLink className="nav-link" to="/admin/products">
              관리자
            </NavLink>
          )}
          {!loading && !me && (
            <>
              <NavLink className="nav-link" to="/login">
                로그인
              </NavLink>
              <NavLink className="nav-link" to="/register">
                회원가입
              </NavLink>
            </>
          )}
          {me && (
            <>
              <span className="navbar-text text-white-50 small ms-2">{me.email}</span>
              <button type="button" className="btn btn-sm btn-outline-light ms-2" onClick={logout}>
                로그아웃
              </button>
            </>
          )}
        </div>
      </div>
    </nav>
  )
}

export default function App() {
  return (
    <div className="d-flex flex-column min-vh-100">
      <NavBar />
      <main className="container flex-grow-1 pb-5">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/products" element={<ProductList />} />
          <Route path="/products/:id" element={<ProductDetail />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/cart" element={<Cart />} />
          <Route path="/mypage/orders" element={<MyOrders />} />
          <Route path="/mypage/orders/:id" element={<OrderDetail />} />
          <Route path="/admin/products" element={<AdminProductList />} />
          <Route path="/admin/products/new" element={<AdminProductForm />} />
          <Route path="/admin/products/:id/edit" element={<AdminProductForm />} />
        </Routes>
      </main>
    </div>
  )
}
