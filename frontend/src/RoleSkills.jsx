import { useEffect, useState } from 'react'
import { api } from './api/client.js'

// W7: which skills of the target role the resume proves. Each request recomputes and stores the check.
// first: step 3 of the first-time path. onPoint(id): show the resume point. onFix: the "add some" link back.
export default function RoleSkills({ id, fileName, first, onBack, onFix, fixLabel, onPoint }) {
  const [check, setCheck] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api(`/resumes/${id}/role-skills-check`).then(setCheck).catch((e) => setError(e.message))
  }, [id])

  if (error) return <><button type="button" onClick={onBack}>Back to resumes</button><p role="alert">! Error: {error}</p></>
  if (!check) return <p>Checking your skills…</p>

  const { withEvidence: yes, withoutEvidence: no } = check
  return (
    <>
      <h1>Role skills check: {check.role.name}{fileName ? ` · ${fileName}` : ''}</h1>
      {yes.length === 0 ? (
        <p>
          We found no skills on this resume. Go back and add some.{' '}
          <button type="button" onClick={onFix}>{fixLabel}</button>
        </p>
      ) : (
        <>
          <h2>✓ Skills with evidence on your resume ({yes.length})</h2>
          <ul>
            {yes.map((s) => (
              <li key={s.skillId}>
                <strong>{s.skill}</strong>
                {s.resumePoints.map((p) => (
                  <span key={p.id}> {onPoint ? <button type="button" onClick={() => onPoint(p.id)}>{p.text}</button> : p.text}</span>
                ))}
              </li>
            ))}
          </ul>
        </>
      )}
      {no.length > 0 && (
        <>
          <h2>! Skills with no evidence yet ({no.length})</h2>
          <ul>{no.map((s) => <li key={s.skillId}>{s.skill}</li>)}</ul>
        </>
      )}
      <button type="button" disabled title="Coming soon">{first ? 'Start your first practice session' : 'Start practice session'}</button>{' '}
      <button type="button" onClick={onBack}>Back to resumes</button>
    </>
  )
}
