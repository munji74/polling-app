import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const onLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <header className="bg-white border-b">
      <div className="max-w-4xl mx-auto p-4 flex items-center justify-between">
        <Link to="/" className="font-semibold">Polls</Link>
        <nav className="flex items-center gap-3">
          <Link to="/">Home</Link>
          {user ? (
            <>
              <Link to="/create" className="px-3 py-1 rounded bg-gray-900 text-white">Create Poll</Link>
              <Link to="/my-polls">My Polls</Link>
              <button onClick={onLogout} className="px-3 py-1 rounded border">Logout</button>
            </>
          ) : (
            <>
              <Link to="/login">Login</Link>
              <Link to="/register" className="px-3 py-1 rounded bg-gray-900 text-white">Sign up</Link>
            </>
          )}
        </nav>
      </div>
    </header>
  )
}
