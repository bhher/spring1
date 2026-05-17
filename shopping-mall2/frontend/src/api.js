function readCookie(name) {
  const m = document.cookie.match(new RegExp('(?:^|; )' + name.replace(/([.$?*|{}()[\]\\/+^])/g, '\\$1') + '=([^;]*)'))
  return m ? decodeURIComponent(m[1]) : ''
}

export function xsrfHeader() {
  const token = readCookie('XSRF-TOKEN')
  return token ? { 'X-XSRF-TOKEN': token } : {}
}

export async function apiFetch(url, options = {}) {
  const headers = { ...xsrfHeader(), ...options.headers }
  if (options.body && !(options.body instanceof FormData) && typeof options.body === 'object') {
    headers['Content-Type'] = 'application/json'
  }
  const res = await fetch(url, {
    ...options,
    credentials: 'include',
    headers,
  })
  return res
}

export async function fetchCsrfMeta() {
  const res = await apiFetch('/api/auth/csrf')
  if (!res.ok) throw new Error('CSRF 로드 실패')
  return res.json()
}
