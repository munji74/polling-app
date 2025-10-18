import axios from 'axios'
import { getToken } from '../utils/storage'


const api = axios.create({
baseURL: import.meta.env.VITE_API_BASE || 'http://localhost:8080',
})


// Attach JWT if available
api.interceptors.request.use((config) => {
const token = getToken()
if (token) config.headers.Authorization = `Bearer ${token}`
return config
})


// Basic error handling (optional)
api.interceptors.response.use(
(res) => res,
(err) => {
// If unauthorized, you can redirect to /login here if desired
return Promise.reject(err)
}
)


export default api