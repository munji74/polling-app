import { useState } from 'react'
import api from '../api/client'
import { useNavigate } from 'react-router-dom'

export default function CreatePoll() {
  const [question, setQuestion] = useState('')
  const [options, setOptions] = useState(['', ''])
  const [expiresAt, setExpiresAt] = useState('')
  const [error, setError] = useState(null)
  const [saving, setSaving] = useState(false)
  const navigate = useNavigate()

  const updateOption = (i, val) => {
    setOptions((prev) => prev.map((o, idx) => (idx === i ? val : o)))
  }

  const addOption = () => setOptions((prev) => [...prev, ''])
  const removeOption = (i) =>
    setOptions((prev) => prev.filter((_, idx) => idx !== i))

  const onSubmit = async (e) => {
    e.preventDefault()
    setSaving(true)
    setError(null)
    try {
      const body = { question, options: options.filter(Boolean) }
      if (expiresAt) body.expiresAt = new Date(expiresAt).toISOString()

      const { data, headers } = await api.post('/api/polls', body)

      // Prefer Location header if present, else fallback to response body id
      const location = headers['location'] || `/api/polls/${data.id}`
      const id = location.split('/').pop()
      navigate(`/polls/${id}`)
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to create poll')
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="max-w-xl mx-auto bg-white border rounded-lg p-6">
      <h1 className="text-xl font-semibold mb-4">Create a Poll</h1>

      <form onSubmit={onSubmit} className="space-y-4">
        <div>
          <label className="block text-sm mb-1">Question</label>
          <input
            className="w-full border rounded px-3 py-2"
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
            placeholder="What should we build next?"
            required
          />
        </div>

        <div>
          <label className="block text-sm mb-1">Options</label>
          <div className="space-y-2">
            {options.map((opt, i) => (
              <div key={i} className="flex gap-2">
                <input
                  className="flex-1 border rounded px-3 py-2"
                  value={opt}
                  onChange={(e) => updateOption(i, e.target.value)}
                  placeholder={`Option ${i + 1}`}
                  required
                />
                {options.length > 2 && (
                  <button
                    type="button"
                    className="px-3 py-2 border rounded"
                    onClick={() => removeOption(i)}
                  >
                    Remove
                  </button>
                )}
              </div>
            ))}
            <button
              type="button"
              className="px-3 py-2 border rounded"
              onClick={addOption}
            >
              Add option
            </button>
          </div>
        </div>

        <div>
          <label className="block text-sm mb-1">Expires At (optional)</label>
          <input
            type="datetime-local"
            className="w-full border rounded px-3 py-2"
            value={expiresAt}
            onChange={(e) => setExpiresAt(e.target.value)}
          />
        </div>

        {error && <div className="text-red-600 text-sm">{error}</div>}

        <button
          disabled={saving}
          className="px-4 py-2 rounded bg-gray-900 text-white disabled:opacity-50"
        >
          {saving ? 'Creating…' : 'Create Poll'}
        </button>
      </form>
    </div>
  )
}
