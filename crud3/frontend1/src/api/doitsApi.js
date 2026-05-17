/** 게시글 REST API — 각 함수에서 `fetch`만 사용합니다. */

const BASE = '/api/doits'

const jsonHeaders = {
  'Content-Type': 'application/json',
  Accept: 'application/json',
}

export async function fetchDoitList() {
  const res = await fetch(BASE, { headers: jsonHeaders })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || res.statusText)
  }
  return res.json()
}

export async function fetchDoit(num) {
  const res = await fetch(`${BASE}/${num}`, { headers: { Accept: 'application/json' } })
  if (res.status === 404) return null
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || res.statusText)
  }
  return res.json()
}

export async function createDoit({ title, content }) {
  const res = await fetch(BASE, {
    method: 'POST',
    headers: jsonHeaders,
    body: JSON.stringify({ title, content }),
  })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || res.statusText)
  }
  return res.json()
}

export async function updateDoit(num, { title, content }) {
  const res = await fetch(`${BASE}/${num}`, {
    method: 'PUT',
    headers: jsonHeaders,
    body: JSON.stringify({ title, content }),
  })
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || res.statusText)
  }
  return res.json()
}

/** 삭제 결과는 호출부에서 `res.status` 등으로 판단합니다. */
export function deleteDoit(num) {
  return fetch(`${BASE}/${num}`, { method: 'DELETE' })
}
