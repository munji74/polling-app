import axios from 'axios'
import { getToken } from '../utils/storage'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || 'http://localhost:8080',
})

// Ensure JSON posts
api.defaults.headers.post['Content-Type'] = 'application/json'

api.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    // Make sure we don't lose existing headers
    config.headers = {
      ...(config.headers || {}),
      Authorization: `Bearer ${token}`,
    }
  }
  return config
})

api.interceptors.response.use(
  (res) => res,
  (err) => Promise.reject(err)
)

export default api
