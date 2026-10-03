import { useId, useState } from 'react'
import { api } from './api/client.js'

// W12a: account details and the danger zone. W12b: delete confirmation (own page).
export default function Account({ user, onDeleted }) {
  const [confirming, setConfirming] = useState(false)
  if (confirming) return <ConfirmDelete onCancel={() => setConfirming(false)} onDeleted={onDeleted} />
  return (
    <>
      <h1>Account</h1>
      <p>User ID: {user.loginId}</p>
      <h2>Danger zone</h2>
      <section className="danger">
        <h3>Delete my account</h3>
        <p>This permanently deletes all your data.</p>
        <button type="button" onClick={() => setConfirming(true)}>Delete my account ...</button>
      </section>
    </>
  )
}

function ConfirmDelete({ onCancel, onDeleted }) {
  const [password, setPassword] = useState('')
  const [word, setWord] = useState('')
  const [error, setError] = useState(null)
  const [busy, setBusy] = useState(false)
  const id = useId()

  async function submit(e) {
    e.preventDefault()
    setError(null)
    setBusy(true)
    try {
      await api('/me', { method: 'DELETE', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ password }) })
      onDeleted()
    } catch (err) {
      setError(err.message)
      setBusy(false)
    }
  }

  return (
    <>
      <h1>Delete your account</h1>
      <p>This will permanently delete:</p>
      <ul>
        <li>your resumes and resume points</li>
        <li>key facts, questions, answers, transcripts and scores</li>
        <li>your stories and role skills check results</li>
        <li>roles you added</li>
      </ul>
      <p>The shared starter roles are not affected.</p>
      <form onSubmit={submit} noValidate>
        <div className="field">
          <label htmlFor={`${id}-pw`}>Password</label>
          <input id={`${id}-pw`} type="password" autoComplete="current-password" value={password}
            onChange={(e) => setPassword(e.target.value)}
            aria-invalid={error ? true : undefined} aria-describedby={error ? `${id}-err` : undefined} />
          {error && <p id={`${id}-err`} role="alert" className="error">✗ {error}</p>}
        </div>
        <div className="field">
          <label htmlFor={`${id}-word`}>Type DELETE</label>
          <input id={`${id}-word`} value={word} onChange={(e) => setWord(e.target.value)} autoComplete="off" spellCheck={false} />
        </div>
        <button type="submit" disabled={busy || !password || word !== 'DELETE'}>Delete my account</button>{' '}
        <button type="button" onClick={onCancel}>Cancel</button>
      </form>
    </>
  )
}
