/** @param {string} path 상대 경로 (예: /api/posts) */
export async function apiFetch(path, options = {}) {
  const isJsonBody = options.body && !(options.body instanceof FormData)
  const headers = { ...options.headers }
  if (isJsonBody) {
    headers['Content-Type'] = 'application/json'
  }
  const res = await fetch(path, {
    ...options,
    credentials: 'include',
    headers,
  })
  return res
}

export async function login(username, password) {
  const res = await apiFetch('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  })
  return res
}

export async function logout() {
  return apiFetch('/api/auth/logout', { method: 'POST' })
}

export async function register(body) {
  return apiFetch('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify(body),
  })
}

export async function fetchMe() {
  return apiFetch('/api/auth/me')
}

export async function fetchPosts() {
  return apiFetch('/api/posts')
}

export async function fetchPost(id) {
  return apiFetch(`/api/posts/${id}`)
}

export async function createPost({ title, content, files }) {
  const fd = new FormData()
  fd.append('title', title)
  fd.append('content', content)
  if (files?.length) {
    for (const f of files) {
      fd.append('files', f)
    }
  }
  return apiFetch('/api/posts', { method: 'POST', body: fd })
}

export async function updatePost(id, { title, content, files }) {
  const fd = new FormData()
  fd.append('title', title)
  fd.append('content', content)
  if (files?.length) {
    for (const f of files) {
      fd.append('files', f)
    }
  }
  return apiFetch(`/api/posts/${id}/update`, { method: 'POST', body: fd })
}

export async function deletePost(id) {
  return apiFetch(`/api/posts/${id}`, { method: 'DELETE' })
}
