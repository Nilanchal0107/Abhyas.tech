import { useEffect, useId, useState } from 'react'
import { api, startSession } from './api/client.js'

const day = (iso) => new Date(iso).toLocaleDateString(undefined, { day: 'numeric', month: 'short' })

// W3 open: the saved resume read-only, except its interview date (PATCH accepts only that).
export function Open({ id, pointId, onBack, onChanged }) {
  const [resume, setResume] = useState(null)
  const [date, setDate] = useState('')
  const [msg, setMsg] = useState('')
  const [error, setError] = useState('')
  const dateId = useId()

  useEffect(() => {
    api(`/resumes/${id}`).then(({ resume: r }) => { setResume(r); setDate(r.interviewDate ?? '') }).catch((e) => setError(e.message))
  }, [id])

  // coming from the role skills check: bring the chosen resume point into view
  useEffect(() => {
    if (resume && pointId) document.getElementById(`point-${pointId}`)?.scrollIntoView({ block: 'center' })
  }, [resume, pointId])

  async function saveDate() {
    setMsg(''); setError('')
    try {
      const { resume: r } = await api(`/resumes/${id}`, {
        method: 'PATCH', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ interviewDate: date || null }),
      })
      setResume((old) => ({ ...old, ...r }))
      setMsg('Interview date saved.')
      onChanged()
    } catch (e) { setError(e.errors?.[0]?.message ?? e.message) }
  }

  if (error && !resume) return <><button type="button" onClick={onBack}>Back to resumes</button><p role="alert">! Error: {error}</p></>
  if (!resume) return <p>Loading…</p>

  const label = { experience_bullet: 'Experience', project_bullet: 'Project', skill: 'Skill' }
  return (
    <>
      <button type="button" onClick={onBack}>Back to resumes</button>
      <h1>{resume.fileName}</h1>
      <p>Target role: {resume.targetRole.name} · Saved resumes are read-only, except the interview date.</p>
      <div>
        <label htmlFor={dateId}>Interview date (optional)</label>
        <input id={dateId} type="date" value={date} onChange={(e) => setDate(e.target.value)} />
        <button type="button" onClick={saveDate}>Save date</button>
        <p aria-live="polite">{msg}</p>
        <p className="error" role="alert">{error ? `! Error: ${error}` : ''}</p>
      </div>
      <h2>{resume.parsed.profile?.name ?? 'Your resume'}</h2>
      <h2>Resume points ({resume.points.length})</h2>
      {resume.points.length === 0 ? <p>Nothing found.</p> : (
        <ol>{resume.points.map((p) => <li key={p.id} id={`point-${p.id}`} style={p.id === pointId ? { background: 'Mark' } : undefined}><strong>{label[p.kind]}:</strong> {p.text}</li>)}</ol>
      )}
    </>
  )
}

// W3: list, delete. Open (W3) and the role skills check (W7) are their own routes. Practice starts a session (W8).
export default function ResumeList({ onUpload, onPractice, onOpen, onCheck }) {
  const [resumes, setResumes] = useState(null)
  const [error, setError] = useState('')
  const [confirm, setConfirm] = useState(null) // id waiting for inline confirmation
  const [rowError, setRowError] = useState({})
  const [preparing, setPreparing] = useState(null) // resume id whose questions are being prepared

  const load = () => api('/resumes').then((r) => setResumes(r.resumes)).catch((e) => setError(e.message))
  useEffect(() => { load() }, [])

  async function start(id) {
    setRowError({ [id]: '' })
    setPreparing(id)
    try {
      onPractice(await startSession(id))
    } catch (e) {
      setRowError({ [id]: e.message })
    } finally { setPreparing(null) }
  }

  async function remove(id) {
    try {
      await api(`/resumes/${id}`, { method: 'DELETE' })
      setConfirm(null)
      load()
    } catch (e) { setRowError({ [id]: e.message }) }
  }

  if (error) return <p role="alert">! Error: {error}</p>
  if (!resumes) return <p>Loading…</p>

  return (
    <>
      <h1>Your resumes</h1>
      <p><button type="button" className="primary" onClick={onUpload}>Upload a resume</button></p>
      {resumes.length === 0 && <p>No resumes yet. Upload one to get your first questions.</p>}
      {resumes.map((r) => (
        <section key={r.id} aria-label={r.fileName}>
          <h2>{r.fileName}</h2>
          <p className="muted">Target role: {r.targetRole.name} · Uploaded {day(r.createdAt)} · Mastered questions: {r.masteredCount}</p>
          <div className="actions">
            <button type="button" className="primary" onClick={() => start(r.id)}>Practice with this resume</button>
            <button type="button" onClick={() => onCheck(r.id, r.fileName)}>Role skills check</button>
            <button type="button" onClick={() => onOpen(r.id)}>Open</button>
            <button type="button" className="danger" onClick={() => setConfirm(r.id)}>Delete</button>
          </div>
          {confirm === r.id && (
            <p role="alert">
              Delete this resume and its questions and scores?{' '}
              <button type="button" className="danger" onClick={() => remove(r.id)}>Delete</button>{' '}
              <button type="button" onClick={() => setConfirm(null)}>Cancel</button>
            </p>
          )}
          {preparing === r.id && <p aria-live="polite">Preparing your questions…</p>}
          {rowError[r.id] && <p role="alert">! Error: {rowError[r.id]}</p>}
        </section>
      ))}
    </>
  )
}
