import { createContext, useCallback, useContext, useEffect, useState } from 'react'
import { fetchMe, login as apiLogin, logout as apiLogout } from '../api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  const refresh = useCallback(async () => {
    const res = await fetchMe()
    if (res.ok) {
      const data = await res.json()
      setUser(data)
    } else {
      setUser(null)
    }
    setLoading(false)
  }, [])

  useEffect(() => {
    refresh()
  }, [refresh])

  const login = async (username, password) => {
    const res = await apiLogin(username, password)
    if (res.ok) {
      const data = await res.json()
      setUser(data)
      return { ok: true }
    }
    return { ok: false }
  }

  const logout = async () => {
    await apiLogout()
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, logout, refresh }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
