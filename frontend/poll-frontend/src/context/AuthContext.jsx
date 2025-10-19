import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import api from '../api/client'
import { getToken, setToken, clearToken } from '../utils/storage'

const AuthContext = createContext(null)

function authHeader() {
  const t = getToken()
  return t ? { Authorization: `Bearer ${t}` } : {}
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    async function fetchProfile() {
      const token = getToken()
      if (!token) return setLoading(false)
      try {
        // 🔐 Force the header explicitly here
        const { data } = await api.get('/api/auth/me', { headers: authHeader() })
        setUser(data)
      } catch (e) {
        clearToken()
        setUser(null)
      } finally {
        setLoading(false)
      }
    }
    fetchProfile()
  }, [])

  const login = async (email, password) => {
    const res = await api.post('/api/auth/login', { email, password })
    const token = res?.data?.accessToken
    if (!token) throw new Error('Login succeeded but no token returned')

    setToken(token)

    // 🔐 Force the header on this call too
    const me = await api.get('/api/auth/me', { headers: authHeader() })
    setUser(me.data)
  }

  const register = async (name, email, password) => {
    const res = await api.post('/api/auth/register', { name, email, password })
    const token = res?.data?.accessToken || res?.data?.token || res?.data?.jwt || null
    if (token) {
      setToken(token)
      const me = await api.get('/api/auth/me', { headers: authHeader() })
      setUser(me.data)
    }
  }

  const logout = () => {
    clearToken()
    setUser(null)
  }

  const value = useMemo(() => ({ user, login, register, logout, loading }), [user, loading])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)
