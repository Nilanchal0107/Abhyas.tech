import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router'
import { api, startSession } from './api/client.js'
import { Alert, Loading, Spinner, Steps } from './ui.jsx'
import { ArrowLeft, ArrowRight, CheckCircle, WarningCircle } from '@phosphor-icons/react'

// W7: which skills of the target role the resume proves. Each request recomputes and stores the check.
// first: step 3 of the first-time path. onPoint(id): show the resume point. onFix: the "add some" link back.
export default function RoleSkills({ id, fileName, first, onBack, onFix, fixLabel, onPoint }) {
  const [check, setCheck] = useState(null)
  const [error, setError] = useState('')
  const [preparing, setPreparing] = useState(false)
  const [startError, setStartError] = useState('')
  const navigate = useNavigate()

  useEffect(() => {
    api(`/resumes/${id}/role-skills-check`).then(setCheck).catch((e) => setError(e.message))
  }, [id])

  if (error) return <><button type="button" onClick={onBack}><ArrowLeft size={18} aria-hidden="true" />Back to resumes</button><Alert>{error}</Alert></>
  if (!check) return <Loading>Checking your skills…</Loading>

  async function start() {
    setStartError('')
    setPreparing(true)
    try {
      navigate(`/practice/${await startSession(id)}`)
    } catch (e) {
      setStartError(e.message)
      setPreparing(false)
    }
  }

  const { withEvidence: yes, withoutEvidence: no } = check
  return (
    <>
      {first && <Steps current={3} />}
      <div className="page-head">
        <span className="eyebrow">Role skills check</span>
        <h1>{check.role.name}{fileName ? ` · ${fileName}` : ''}</h1>
      </div>
      {yes.length === 0 ? (
        <div className="notice">
          <WarningCircle size={20} aria-hidden="true" />
          <div>
            <p>We found no skills on this resume. Go back and add some.</p>
            <button type="button" className="small" onClick={onFix}>{fixLabel}</button>
          </div>
        </div>
      ) : (
        <>
          <h2 className="group-title yes"><CheckCircle size={26} weight="fill" aria-hidden="true" />Skills with evidence on your resume ({yes.length})</h2>
          <ul className="skill-grid yes">
            {yes.map((s) => (
              <li key={s.skillId}>
                <strong>{s.skill}</strong>
                <span className="proof">
                  {s.resumePoints.map((p) => (onPoint ? <button key={p.id} type="button" onClick={() => onPoint(p.id)}>{p.text}</button> : <span key={p.id} className="muted">{p.text}</span>))}
                </span>
              </li>
            ))}
          </ul>
        </>
      )}
      {no.length > 0 && (
        <>
          <h2 className="group-title no"><WarningCircle size={26} weight="fill" aria-hidden="true" />Skills with no evidence yet ({no.length})</h2>
          <ul className="skill-grid no">{no.map((s) => <li key={s.skillId}><strong>{s.skill}</strong></li>)}</ul>
        </>
      )}
      <div className="actions">
        <button type="button" className="primary" onClick={start} disabled={preparing}>{first ? 'Start your first practice session' : 'Start practice session'}<ArrowRight size={18} aria-hidden="true" /></button>
        <button type="button" onClick={onBack}><ArrowLeft size={18} aria-hidden="true" />Back to resumes</button>
      </div>
      <p className="loading" aria-live="polite">{preparing && <><Spinner />Preparing your questions…</>}</p>
      <Alert>{startError}</Alert>
    </>
  )
}
