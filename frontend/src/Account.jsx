import { useId, useState } from 'react'
import { api } from './api/client.js'
import { Trash, XCircle } from '@phosphor-icons/react'

// W12a: account details and the danger zone. W12b: delete confirmation (own page).
export default function Account({ user, onDelete }) {
  return (
    <>
      <div className="page-head">
        <span className="eyebrow">You</span>
        <h1>Account</h1>
        <p>User ID: {user.loginId}</p>
      </div>
      <h2>Danger zone</h2>
      <section className="danger-zone">
        <h3>Delete my account</h3>
        <p>This permanently deletes all your data.</p>
        <button type="button" className="danger" onClick={onDelete}><Trash size={18} aria-hidden="true" />Delete my account ...</button>
      </section>
    </>
  )
}

export function ConfirmDelete({ onCancel, onDeleted }) {
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
      <div className="page-head">
        <span className="eyebrow">Danger zone</span>
        <h1>Delete your account</h1>
      </div>
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
          {error && <p id={`${id}-err`} role="alert" className="error"><XCircle size={18} weight="fill" aria-hidden="true" />{error}</p>}
        </div>
        <div className="field">
          <label htmlFor={`${id}-word`}>Type DELETE</label>
          <input id={`${id}-word`} value={word} onChange={(e) => setWord(e.target.value)} autoComplete="off" spellCheck={false} />
        </div>
        <div className="actions">
          <button type="submit" className="danger" disabled={busy || !password || word !== 'DELETE'}>Delete my account</button>
          <button type="button" onClick={onCancel}>Cancel</button>
        </div>
      </form>
    </>
  )
}
