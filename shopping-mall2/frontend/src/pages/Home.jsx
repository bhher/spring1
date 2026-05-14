import { Link } from 'react-router-dom'

export default function Home() {
  return (
    <div>
      <h1 className="h2 mb-3">쇼핑몰 (React)</h1>
      <p className="text-muted">백엔드는 Spring Boot REST + 세션 로그인입니다.</p>
      <Link className="btn btn-primary" to="/products">
        상품 보러가기
      </Link>
    </div>
  )
}
