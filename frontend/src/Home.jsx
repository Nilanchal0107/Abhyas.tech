import { useEffect, useId, useState } from 'react'
import { ArrowRight, ChartBar, FileArrowUp, ClockCountdown, ListChecks, Microphone } from '@phosphor-icons/react'
import { api, postJson } from './api/client.js'
import { day, modeLabel } from './lib/sessions.js'
import { Alert, Loading, Spinner } from './ui.jsx'

// W2 Home, top to bottom: the unfinished session, the resume selector with Start, the latest completed session.
export default function Home({ user, onPractice, onReport, onUpload }) {
  const [data, setData] = useState(null)
  const [error, setError] = useState('')
  const [resumeId, setResumeId] = useState('')
  const [preparing, setPreparing] = useState(false)
  const [mode, setMode] = useState('quiz')
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
    try { onPractice((await postJson('/sessions', { resumeId: Number(resumeId), mode })).session.id) }
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
      <div className="page-head">
        <span className="eyebrow">Home</span>
        <h1>Practice</h1>
        <p>Signed in as {user.loginId}.</p>
      </div>
      <Alert>{error}</Alert>
      {!data && !error && <Loading />}

      {data && data.resumes.length === 0 && (
        <section aria-label="Upload your resume" className="accent">
          <div className="sheet-head">
            <FileArrowUp size={32} aria-hidden="true" />
            <div>
              <h2>Welcome. Let's start with your resume.</h2>
              <p>We turn it into questions about the things you will be asked to defend.</p>
            </div>
          </div>
          <div className="actions"><button type="button" className="primary" onClick={onUpload}>Upload your resume<ArrowRight size={18} aria-hidden="true" /></button></div>
        </section>
      )}

      {open && (
        <section aria-label="Continue your practice session" className="accent">
          <div className="sheet-head">
            <ClockCountdown size={32} aria-hidden="true" />
            <div>
              <h2>Continue your practice session</h2>
              <p className="muted">{modeLabel(open)} · Started {day(open.startedAt)} · {open.answeredCount} of {open.questionCount} answered · {open.resume.fileName}</p>
            </div>
          </div>
          <div className="actions">
            <button type="button" className="primary" onClick={() => onPractice(open.id)}>Continue<ArrowRight size={18} aria-hidden="true" /></button>
            <button type="button" onClick={leave}>Leave session</button>
          </div>
        </section>
      )}

      {data && data.resumes.length > 0 && !open && (
        <section aria-label="Start a practice session">
          <h2>Start a practice session</h2>
          <fieldset className="plain">
            <legend>Mode</legend>
            <div className="choice-grid">
              <label className="choice">
                <input type="radio" name="mode" checked={mode === 'quiz'} onChange={() => setMode('quiz')} />
                <span><strong><ListChecks size={18} aria-hidden="true" style={{ verticalAlign: '-3px' }} /> Quiz</strong>19 questions, 80 marks</span>
              </label>
              <label className="choice">
                <input type="radio" name="mode" checked={mode === 'interview'} onChange={() => setMode('interview')} />
                <span><strong><Microphone size={18} aria-hidden="true" style={{ verticalAlign: '-3px' }} /> Interview</strong>6 spoken questions with a follow-up each, 3 minutes per answer</span>
              </label>
            </div>
          </fieldset>
          <div className="field">
            <label htmlFor={selectId}>Resume</label>
            <select id={selectId} value={resumeId} onChange={(e) => setResumeId(e.target.value)}>
              {data.resumes.map((r) => <option key={r.id} value={r.id}>{r.fileName} ({r.targetRole.name})</option>)}
            </select>
          </div>
          <div className="actions">
            <button type="button" className="primary" disabled={preparing} onClick={start}>{mode === 'interview' ? 'Start interview' : 'Start practice session'}<ArrowRight size={18} aria-hidden="true" /></button>
          </div>
          <p className="loading" aria-live="polite">{preparing && <><Spinner />Preparing your questions…</>}</p>
        </section>
      )}

      {data && (
        <section aria-label="Latest practice session">
          {!latest ? <p className="muted" style={{ margin: 0 }}>No practice sessions yet.</p> : (
            <>
              <h2>Latest practice session ({day(latest.completedAt)})</h2>
              {latest.totalMarks == null ? <p className="loading" aria-live="polite"><Spinner />Marking your answers…</p> : (
                <>
                  <div className="stats">
                    <div className="stat"><strong><span className="mark ok" style={{ fontSize: 'inherit' }}>{latest.totalMarks} / {latest.maxMarks}</span></strong><span>Total</span></div>
                    <div className="stat"><strong>{latest.masteredCount} of {latest.attempts.length}</strong><span>Mastered</span></div>
                  </div>
                  {latest.weakestPoints?.length > 0 && (
                    <>
                      <h4>Weakest resume points</h4>
                      <ul>{latest.weakestPoints.map((p) => <li key={p.id}>{p.text}</li>)}</ul>
                    </>
                  )}
                </>
              )}
              <div className="actions"><button type="button" onClick={() => onReport(latest.id)}><ChartBar size={18} aria-hidden="true" />Open report</button></div>
            </>
          )}
        </section>
      )}
    </>
  )
}
