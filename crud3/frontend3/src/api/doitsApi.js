import axios from 'axios'

const api = axios.create({
  baseURL: '/api/doits',
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
})

function errorMessage(err) {
  if (!axios.isAxiosError(err)) {
    return err instanceof Error ? err.message : String(err)
  }
  const data = err.response?.data
  if (typeof data === 'string' && data.trim()) return data
  return err.response?.statusText || err.message
}

export async function fetchDoitList() {
  try {
    const res = await api.get('')
    return res.data
  } catch (e) {
    throw new Error(errorMessage(e))
  }
}

export async function fetchDoit(num) {
  try {
    const res = await api.get(`/${num}`)
    return res.data
  } catch (e) {
    if (axios.isAxiosError(e) && e.response?.status === 404) return null
    throw new Error(errorMessage(e))
  }
}

export async function createDoit({ title, content }) {
  try {
    const res = await api.post('', { title, content })
    return res.data
  } catch (e) {
    throw new Error(errorMessage(e))
  }
}

export async function updateDoit(num, { title, content }) {
  try {
    const res = await api.put(`/${num}`, { title, content })
    return res.data
  } catch (e) {
    throw new Error(errorMessage(e))
  }
}

/** 삭제 결과는 호출부에서 status로 판단합니다. */
export async function deleteDoit(num) {
  try {
    return await api.delete(`/${num}`)
  } catch (e) {
    throw new Error(errorMessage(e))
  }
}
