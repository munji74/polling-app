import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Register() {
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [passwordConfirm, setPasswordConfirm] = useState('')
  const [error, setError] = useState(null)
  const [fieldErrors, setFieldErrors] = useState({})
  const [submitting, setSubmitting] = useState(false)
  const { register } = useAuth()
  const navigate = useNavigate()

  const onSubmit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    setError(null)
    setFieldErrors({})
    try {
      await register({ name, email, password, passwordConfirm })
      navigate('/', { replace: true })
    } catch (err) {
      // register() throws either a string or returns a map for field errors
      if (typeof err === 'object' && err !== null) {
        setFieldErrors(err)
      } else {
        setError(String(err || 'Registration failed'))
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="max-w-md mx-auto bg-white border rounded-lg p-6">
      <h1 className="text-xl font-semibold mb-4">Create account</h1>
      <form onSubmit={onSubmit} className="space-y-3">
        <div>
          <label className="block text-sm mb-1">Name</label>
          <input
            className="w-full border rounded px-3 py-2"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
          {fieldErrors.name && <p className="text-red-600 text-sm">{fieldErrors.name}</p>}
        </div>

        <div>
          <label className="block text-sm mb-1">Email</label>
          <input
            className="w-full border rounded px-3 py-2"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          {fieldErrors.email && <p className="text-red-600 text-sm">{fieldErrors.email}</p>}
        </div>

        <div>
          <label className="block text-sm mb-1">Password</label>
          <input
            type="password"
            className="w-full border rounded px-3 py-2"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          {fieldErrors.password && <p className="text-red-600 text-sm">{fieldErrors.password}</p>}
        </div>

        <div>
          <label className="block text-sm mb-1">Confirm password</label>
          <input
            type="password"
            className="w-full border rounded px-3 py-2"
            value={passwordConfirm}
            onChange={(e) => setPasswordConfirm(e.target.value)}
          />
          {fieldErrors.passwordConfirm && (
            <p className="text-red-600 text-sm">{fieldErrors.passwordConfirm}</p>
          )}
        </div>

        {error && <div className="text-red-600 text-sm">{error}</div>}

        <button disabled={submitting} className="w-full bg-gray-900 text-white rounded py-2">
          {submitting ? 'Creating…' : 'Sign up'}
        </button>

        <p className="text-sm text-center">
          Already have an account?{' '}
          <Link className="text-blue-600" to="/login">
            Log in
          </Link>
        </p>
      </form>
    </div>
  )
}
