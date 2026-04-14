import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { createPost } from '../api'
import { useAuth } from '../context/AuthContext'

export default function PostWrite() {
  const { user, loading } = useAuth()
  const nav = useNavigate()
  const [title, setTitle] = useState('')
  const [content, setContent] = useState('')
  const [files, setFiles] = useState(null)
  const [err, setErr] = useState('')

  if (!loading && !user) {
    return (
      <p>
        <Link to="/login">로그인</Link>이 필요합니다.
      </p>
    )
  }

  async function onSubmit(e) {
    e.preventDefault()
    setErr('')
    const list = files ? Array.from(files) : []
    const res = await createPost({ title, content, files: list })
    if (res.ok) {
      const data = await res.json()
      nav(`/posts/${data.id}`)
    } else {
      const j = await res.json().catch(() => ({}))
      setErr(j.message || '등록 실패')
    }
  }

  return (
    <div>
      <h1>글쓰기</h1>
      {err && <p className="err">{err}</p>}
      <form onSubmit={onSubmit}>
        <label>
          제목
          <input value={title} onChange={(e) => setTitle(e.target.value)} required />
        </label>
        <label>
          내용
          <textarea value={content} onChange={(e) => setContent(e.target.value)} required rows={12} />
        </label>
        <label>
          이미지 (선택, 여러 장)
          <input type="file" accept="image/*" multiple onChange={(e) => setFiles(e.target.files)} />
        </label>
        <button type="submit">등록</button>
      </form>
      <p>
        <Link to="/posts">목록</Link>
      </p>
    </div>
  )
}
