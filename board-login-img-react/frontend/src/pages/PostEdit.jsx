import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { fetchPost, updatePost } from '../api'
import { useAuth } from '../context/AuthContext'

export default function PostEdit() {
  const { id } = useParams()
  const nav = useNavigate()
  const { user, loading } = useAuth()
  const [title, setTitle] = useState('')
  const [content, setContent] = useState('')
  const [files, setFiles] = useState(null)
  const [err, setErr] = useState('')
  const [initial, setInitial] = useState(null)

  useEffect(() => {
    let cancelled = false
    ;(async () => {
      const res = await fetchPost(id)
      if (!res.ok) {
        setErr('글을 불러올 수 없습니다.')
        return
      }
      const data = await res.json()
      if (cancelled) return
      if (!data.canEdit) {
        setErr('수정 권한이 없습니다.')
        return
      }
      setInitial(data)
      setTitle(data.title)
      setContent(data.content)
    })()
    return () => {
      cancelled = true
    }
  }, [id])

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
    const res = await updatePost(id, { title, content, files: list })
    if (res.ok) {
      nav(`/posts/${id}`)
    } else {
      const j = await res.json().catch(() => ({}))
      setErr(j.message || '저장 실패')
    }
  }

  if (err && !initial) {
    return <p className="err">{err}</p>
  }
  if (!initial) {
    return <p>불러오는 중…</p>
  }

  return (
    <div>
      <h1>수정</h1>
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
          이미지 추가 (선택)
          <input type="file" accept="image/*" multiple onChange={(e) => setFiles(e.target.files)} />
        </label>
        <p className="hint">기존 이미지는 유지되며, 선택한 파일이 추가됩니다.</p>
        <button type="submit">저장</button>
      </form>
      <p>
        <Link to={`/posts/${id}`}>글 보기</Link> · <Link to="/posts">목록</Link>
      </p>
    </div>
  )
}
