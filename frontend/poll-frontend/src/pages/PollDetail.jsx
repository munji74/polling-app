import { useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router-dom';
import api from '../api/client';
import OptionBar from '../components/OptionBar';
import { useAuth } from '../context/AuthContext';

export default function PollDetail() {
  const { id } = useParams();
  const [poll, setPoll] = useState(null);
  const [selected, setSelected] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { user } = useAuth();

  const totalVotes = useMemo(
    () => (poll?.totalVotes ?? (poll?.options?.reduce((s, o) => s + (o.votes || 0), 0) || 0)),
    [poll]
  );

  const expired = useMemo(
    () => (poll?.expiresAt ? new Date(poll.expiresAt) < new Date() : false),
    [poll]
  );

  useEffect(() => {
    async function load() {
      try {
        const { data } = await api.get(`/api/polls/${id}`);
        setPoll(data);
        if (data?.hasVoted && data?.userOptionId) {
          setSelected(data.userOptionId); // preselect user's choice
        }
      } catch {
        setError('Failed to load poll');
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [id]);

  const submitVote = async () => {
    if (!selected) return;
    try {
      await api.post(`/api/polls/${id}/votes`, { optionId: selected });
      const { data } = await api.get(`/api/polls/${id}`);
      setPoll(data);
      // Optionally show a toast: "Vote recorded"
    } catch (err) {
      if (err?.response?.status === 409) {
        // User already voted: lock UI and show friendly state
        setPoll(p => p ? { ...p, hasVoted: true, userOptionId: p.userOptionId ?? selected } : p);
        // Optionally show a toast: "You already voted in this poll."
      } else if (err?.response?.status === 409 || err?.response?.status === 403) {
        // expired or forbidden, rely on backend message if you surface it
      } else {
        setError('Failed to submit vote');
      }
    }
  };

  if (loading) return <div>Loading poll...</div>;
  if (error) return <div className="text-red-600">{error}</div>;
  if (!poll) return null;

  const alreadyVoted = !!poll.hasVoted;

  return (
    <div className="bg-white border rounded-lg p-6">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold">{poll.question}</h1>
        {expired && (
          <span className="text-xs px-2 py-1 bg-red-100 text-red-700 rounded">
            Expired
          </span>
        )}
      </div>

      <p className="text-sm text-gray-500 mb-4">Total votes: {totalVotes}</p>

      <div className="space-y-4">
        {poll.options?.map((opt) => (
          <label key={opt.id} className="block p-3 border rounded">
            <div className="flex items-center gap-2">
              <input
                type="radio"
                name="option"
                value={opt.id}
                checked={selected === opt.id}
                onChange={() => setSelected(opt.id)}
                disabled={expired || !user || alreadyVoted}
              />
              <span>{opt.text}</span>
            </div>
            <div className="mt-2">
              <OptionBar label="" value={opt.votes || 0} total={totalVotes} />
            </div>
          </label>
        ))}
      </div>

      <div className="mt-4 flex gap-2">
        <button
          disabled={!user || expired || !selected || alreadyVoted}
          onClick={submitVote}
          className="px-4 py-2 rounded bg-gray-900 text-white disabled:opacity-50"
        >
          {alreadyVoted ? 'Already voted' : 'Vote'}
        </button>
        {!user && <span className="text-sm text-gray-500">Login to vote</span>}
      </div>
    </div>
  );
}
