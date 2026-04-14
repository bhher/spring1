import { Link } from 'react-router-dom'

export default function Home() {
  return (
    <div>
      <h1>이미지 게시판 (React)</h1>
      <p>
        <Link to="/posts">게시판으로</Link>
      </p>
    </div>
  )
}
