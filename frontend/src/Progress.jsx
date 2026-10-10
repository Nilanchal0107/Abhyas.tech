import { useEffect, useState } from 'react'
import { Link } from 'react-router'
import { api } from './api/client.js'
import { Alert, Loading } from './ui.jsx'
import { ArrowLeft } from '@phosphor-icons/react'

const REASON = { 'low-marks': 'Low marks so far', gap: 'Likely to be asked for this role' }

// "Guide me": the weakest skills over all completed sessions of one resume, and a short study-next list (ticket 14).
export default function Progress({ id }) {
  const [data, setData] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api(`/resumes/${id}/progress`).then(setData).catch((e) => setError(e.message))
  }, [id])

  if (error) return <Alert>{error}</Alert>
  if (!data) return <Loading>Loading your progress…</Loading>
  return (
    <>
      <Link to="/resumes" className="btn"><ArrowLeft size={18} aria-hidden="true" />Back to resumes</Link>
      <div className="page-head" style={{ marginTop: 16 }}>
        <span className="eyebrow">Guide me</span>
        <h1>Your progress</h1>
      </div>
      {data.skills.length === 0 ? (
        <p>No marked skills yet. Finish a practice session and your weakest skills show up here.</p>
      ) : (
        <>
          <h2>Study next</h2>
          <ul className="skill-grid no">{data.studyNext.map((s) => <li key={s.skillId}><strong>{s.skill}</strong><span className="muted">{REASON[s.reason]}</span></li>)}</ul>
          <h2>Skills, weakest first ({data.sessionCount} completed {data.sessionCount === 1 ? 'session' : 'sessions'})</h2>
          <ul className="skill-grid">
            {data.skills.map((s) => (
              <li key={s.skillId}>
                <strong>{s.skill}</strong>
                <span className="num">{s.percent}% ({s.marks} of {s.maxMarks} marks)</span>
                <span className="meter" aria-hidden="true" style={{ flex: '1 1 120px' }}><i style={{ width: `${s.percent}%` }} /></span>
                <span className="muted proof">over time: {s.bySession.map((x) => `${x.percent}%`).join(' → ')}</span>
              </li>
            ))}
          </ul>
        </>
      )}
    </>
  )
}
