import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import api from '../api/client'
import { getToken, setToken, clearToken } from '../utils/storage'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = getToken()
    async function fetchProfile() {
      if (!token) return setLoading(false)
      try {
        const { data } = await api.get('/api/auth/me')
        setUser(data)
      } catch {
        clearToken()
        setUser(null)
      } finally {
        setLoading(false)
      }
    }
    fetchProfile()
  }, [])

  const login = async (email, password) => {
    const { data } = await api.post('/api/auth/login', { email, password })
    setToken(data.token)
    const me = await api.get('/api/auth/me')
    setUser(me.data)
  }

  const register = async (name, email, password) => {
    const { data } = await api.post('/api/auth/register', { name, email, password })
    setToken(data.token)
    const me = await api.get('/api/auth/me')
    setUser(me.data)
  }

  const logout = () => {
    clearToken()
    setUser(null)
  }

  const value = useMemo(() => ({ user, login, register, logout, loading }), [user, loading])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)
