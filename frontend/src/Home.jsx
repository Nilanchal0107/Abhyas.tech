import { useEffect, useId, useState } from 'react'
import { api, postJson } from './api/client.js'
import { day } from './lib/sessions.js'

// W2 Home, top to bottom: the unfinished session, the resume selector with Start, the latest completed session.
export default function Home({ user, onPractice, onReport, onUpload }) {
  const [data, setData] = useState(null)
  const [error, setError] = useState('')
  const [resumeId, setResumeId] = useState('')
  const [preparing, setPreparing] = useState(false)
  const selectId = useId()

  async function load() {
    try {
      const [open, resumes, done] = await Promise.all([
        api('/sessions?status=in_progress&limit=1'), api('/resumes'), api('/sessions?status=completed&limit=1'),
      ])
      const latest = done.sessions[0] ? (await api(`/sessions/${done.sessions[0].id}`)).session : null
      setData({ open: open.sessions[0] ?? null, resumes: resumes.resumes, latest })
      setResumeId((id) => id || String(resumes.resumes[0]?.id ?? ''))
    } catch (e) { setError(e.message) }
  }
  useEffect(() => { load() }, [])

  async function start() {
    setPreparing(true); setError('')
    try { onPractice((await postJson('/sessions', { resumeId: Number(resumeId) })).session.id) }
    catch (e) { if (e.type === 'session-open') onPractice(e.problem.sessionId); else setError(e.message) }
    finally { setPreparing(false) }
  }

  async function leave() {
    setError('')
    try { await postJson(`/sessions/${data.open.id}/abandon`); await load() } catch (e) { setError(e.message) }
  }

  const open = data?.open
  const latest = data?.latest
  return (
    <>
      <h1>Practice</h1>
      <p className="muted">Signed in as {user.loginId}.</p>
      {error && <p role="alert">! Error: {error}</p>}
      {!data && !error && <p>Loading…</p>}

      {data && data.resumes.length === 0 && (
        <section aria-label="Upload your resume">
          <h2>Welcome. Let's start with your resume.</h2>
          <p>We turn it into questions about the things you will be asked to defend.</p>
          <button type="button" className="primary" onClick={onUpload}>Upload your resume</button>
        </section>
      )}

      {open && (
        <section aria-label="Continue your practice session">
          <h2>! Continue your practice session</h2>
          <p>Started {day(open.startedAt)} · {open.answeredCount} of {open.questionCount} answered · {open.resume.fileName}</p>
          <div className="actions">
            <button type="button" className="primary" onClick={() => onPractice(open.id)}>Continue</button>
            <button type="button" onClick={leave}>Leave session</button>
          </div>
        </section>
      )}

      {data && data.resumes.length > 0 && !open && (
        <section aria-label="Start a practice session">
          <h2>Start a practice session</h2>
          <div className="actions">
            <label htmlFor={selectId}>Resume</label>
            <select id={selectId} value={resumeId} onChange={(e) => setResumeId(e.target.value)}>
              {data.resumes.map((r) => <option key={r.id} value={r.id}>{r.fileName} ({r.targetRole.name})</option>)}
            </select>
            <button type="button" className="primary" disabled={preparing} onClick={start}>Start practice session</button>
          </div>
          {preparing && <p aria-live="polite">Preparing your questions…</p>}
        </section>
      )}

      {data && (
        <section aria-label="Latest practice session">
          {!latest ? <p>No practice sessions yet.</p> : (
            <>
              <h2>Latest practice session ({day(latest.completedAt)})</h2>
              {latest.totalMarks == null ? <p aria-live="polite">Marking your answers…</p> : (
                <>
                  <div className="stats">
                    <div className="stat"><strong>{latest.totalMarks} / {latest.maxMarks}</strong><span>Total</span></div>
                    <div className="stat"><strong>{latest.masteredCount} of {latest.attempts.length}</strong><span>Mastered</span></div>
                  </div>
                  {latest.weakestPoints?.length > 0 && (
                    <>
                      <p>Weakest resume points:</p>
                      <ul>{latest.weakestPoints.map((p) => <li key={p.id}>{p.text}</li>)}</ul>
                    </>
                  )}
                </>
              )}
              <div className="actions"><button type="button" onClick={() => onReport(latest.id)}>Open report</button></div>
            </>
          )}
        </section>
      )}
    </>
  )
}
