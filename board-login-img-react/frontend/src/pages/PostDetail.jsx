import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { deletePost, fetchPost } from '../api'
import { useAuth } from '../context/AuthContext'

export default function PostDetail() {
  const { id } = useParams()
  const nav = useNavigate()
  const { user } = useAuth()
  const [post, setPost] = useState(null)
  const [err, setErr] = useState('')

  useEffect(() => {
    let cancelled = false
    ;(async () => {
      const res = await fetchPost(id)
      if (!res.ok) {
        setErr('글을 찾을 수 없습니다.')
        return
      }
      const data = await res.json()
      if (!cancelled) setPost(data)
    })()
    return () => {
      cancelled = true
    }
  }, [id])

  async function onDelete() {
    if (!window.confirm('삭제할까요?')) return
    const res = await deletePost(id)
    if (res.ok) {
      nav('/posts')
    } else {
      const j = await res.json().catch(() => ({}))
      setErr(j.message || '삭제 실패')
    }
  }

  if (err && !post) {
    return <p className="err">{err}</p>
  }
  if (!post) {
    return <p>불러오는 중…</p>
  }

  return (
    <article>
      <h1>{post.title}</h1>
      <p className="meta">
        {post.authorName} · {post.createdAt?.replace('T', ' ').slice(0, 16)}
      </p>
      <pre className="body">{post.content}</pre>
      {post.images?.length > 0 && (
        <div className="gallery">
          {post.images.map((img) => (
            <figure key={img.id}>
              <figcaption>{img.originalName}</figcaption>
              <img src={img.url} alt="" width={280} />
              <img src={img.thumbnailUrl} alt="썸네일" width={100} />
            </figure>
          ))}
        </div>
      )}
      {err && <p className="err">{err}</p>}
      {user && post.canEdit && (
        <p className="actions">
          <Link to={`/posts/${id}/edit`}>수정</Link>
          {' · '}
          <button type="button" className="linkish" onClick={onDelete}>
            삭제
          </button>
        </p>
      )}
      <p>
        <Link to="/posts">목록</Link>
      </p>
    </article>
  )
}
