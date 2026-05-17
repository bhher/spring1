import axios from 'axios'

/** `/api/doits` 기준 axios 인스턴스 */
export const doitsApi = axios.create({
  baseURL: '/api/doits',
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
})

export function axiosErrorMessage(err) {
  if (!axios.isAxiosError(err)) {
    return err instanceof Error ? err.message : String(err)
  }
  const data = err.response?.data
  if (typeof data === 'string' && data.trim()) return data
  return err.response?.statusText || err.message
}
