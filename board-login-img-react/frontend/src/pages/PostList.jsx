import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { fetchPosts } from '../api'

export default function PostList() {
  const [posts, setPosts] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    let cancelled = false
    ;(async () => {
      const res = await fetchPosts()
      if (!res.ok) {
        setError('목록을 불러오지 못했습니다.')
        return
      }
      const data = await res.json()
      if (!cancelled) setPosts(data)
    })()
    return () => {
      cancelled = true
    }
  }, [])

  return (
    <div>
      <h1>게시판</h1>
      {error && <p className="err">{error}</p>}
      {posts.length === 0 && !error && <p className="muted">글이 없습니다.</p>}
      <table className="tbl">
        <thead>
          <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
          </tr>
        </thead>
        <tbody>
          {posts.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>
                <Link to={`/posts/${p.id}`}>{p.title}</Link>
              </td>
              <td>{p.authorName}</td>
              <td>{p.createdAt?.replace('T', ' ').slice(0, 16)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
