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
        const { data } = await api.get('/api/auth/me', { headers: authHeader() })
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
    const res = await api.post('/api/auth/login', { email, password })
    const token = res?.data?.accessToken
    if (!token) throw new Error('Login succeeded but no token returned')

    setToken(token)
    const me = await api.get('/api/auth/me', { headers: authHeader() })
    setUser(me.data)
  }

  /**
   * Register user.
   * - Sends passwordConfirm
   * - Surfaces 409 (email in use) and 400 (field validation map) nicely
   * - If backend returns a token, auto-signs in
   */
  const register = async ({ name, email, password, passwordConfirm }) => {
    try {
      const res = await api.post('/api/auth/register', {
        name,
        email,
        password,
        passwordConfirm,
      })

      const token =
        res?.data?.accessToken || res?.data?.token || res?.data?.jwt || null
      if (token) {
        setToken(token)
        const me = await api.get('/api/auth/me', { headers: authHeader() })
        setUser(me.data)
      }
      return true
    } catch (err) {
      const status = err?.response?.status
      const data = err?.response?.data

      // Email already used (AuthController returns 409)
      if (status === 409) {
        // prefer structured map if backend sends it; otherwise send a simple map
        const map = typeof data === 'object' && data ? data : { email: 'Email already in use' }
        throw map
      }

      // Validation failed (RestExceptionHandler returns a { field: message } map)
      if (status === 400 && typeof data === 'object' && data) {
        throw data
      }

      // Fallback readable message
      throw (data?.message || data?.error || 'Registration failed')
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
