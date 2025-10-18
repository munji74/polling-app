import { useEffect, useState } from 'react'
import api from '../api/client'
import PollCard from '../components/PollCard'


export default function Home() {
const [polls, setPolls] = useState([])
const [loading, setLoading] = useState(true)
const [error, setError] = useState(null)


useEffect(() => {
async function load() {
try {
const { data } = await api.get('/api/polls')
setPolls(data)
} catch (e) {
setError('Failed to load polls')
} finally {
setLoading(false)
}
}
load()
}, [])


if (loading) return <div>Loading polls...</div>
if (error) return <div className="text-red-600">{error}</div>


return (
<div className="grid gap-4">
{polls.map((p) => <PollCard key={p.id} poll={p} />)}
{polls.length === 0 && <div>No polls yet.</div>}
</div>
)
}