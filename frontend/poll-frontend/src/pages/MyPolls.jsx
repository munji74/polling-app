import { useEffect, useState, useMemo } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/client'

export default function MyPolls() {
  const [polls, setPolls] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  // Helper: try multiple endpoints until one works
  async function fetchMyPolls() {
    const endpoints = [
      '/api/polls/mine',
      '/api/users/me/polls',
      '/api/polls?createdBy=me',
    ]
    let lastErr
    for (const ep of endpoints) {
      try {
        const { data } = await api.get(ep)
        return Array.isArray(data) ? data : (data?.items ?? data ?? [])
      } catch (e) {
        lastErr = e
      }
    }
    throw lastErr
  }

  useEffect(() => {
    (async () => {
      try {
        const res = await fetchMyPolls()
        setPolls(res)
      } catch (_) {
        setError('Failed to load your polls')
      } finally {
        setLoading(false)
      }
    })()
  }, [])

  const active = useMemo(
    () => polls.filter(p => !p.expiresAt || new Date(p.expiresAt) >= new Date()),
    [polls]
  )
  const expired = useMemo(
    () => polls.filter(p => p.expiresAt && new Date(p.expiresAt) < new Date()),
    [polls]
  )

  if (loading) return <div>Loading your polls...</div>
  if (error) return <div className="text-red-600">{error}</div>

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold">My Polls</h1>
        <Link to="/create" className="px-3 py-2 rounded bg-gray-900 text-white">
          New Poll
        </Link>
      </div>

      <section>
        <h2 className="text-sm font-medium text-gray-600 mb-2">Active</h2>
        <div className="grid gap-4">
          {active.length ? (
            active.map(p => (
              <MyPollRow key={p.id} poll={p} />
            ))
          ) : (
            <div className="text-sm text-gray-500">No active polls.</div>
          )}
        </div>
      </section>

      <section>
        <h2 className="text-sm font-medium text-gray-600 mb-2">Expired</h2>
        <div className="grid gap-4">
          {expired.length ? (
            expired.map(p => (
              <MyPollRow key={p.id} poll={p} />
            ))
          ) : (
            <div className="text-sm text-gray-500">No expired polls.</div>
          )}
        </div>
      </section>
    </div>
  )
}

function MyPollRow({ poll }) {
  const totalVotes = (poll.options || []).reduce((s, o) => s + (o.votes || 0), 0)

  return (
    <div className="p-4 bg-white rounded-lg border">
      <div className="flex items-center justify-between">
        <div>
          <h3 className="text-base font-semibold">{poll.question}</h3>
          <p className="text-xs text-gray-500 mt-1">Total votes: {totalVotes}</p>
          {poll.expiresAt && (
            <p className="text-xs text-gray-500">
              Expires: {new Date(poll.expiresAt).toLocaleString()}
            </p>
          )}
        </div>
        <div className="flex items-center gap-2">
          <Link
            to={`/polls/${poll.id}`}
            className="text-blue-600 hover:underline text-sm"
          >
            View
          </Link>
          {/* Optional: uncomment if your backend supports deletion
          <button
            onClick={() => handleDelete(poll.id)}
            className="text-sm px-2 py-1 border rounded"
          >
            Delete
          </button>
          */}
        </div>
      </div>
    </div>
  )
}

/* Example delete handler if your API supports it:
async function handleDelete(id) {
  if (!confirm('Delete this poll?')) return
  await api.delete(`/api/polls/${id}`)
  // refetch or optimistically update state
}
*/
