import { useEffect, useState } from 'react'
import { api, download } from './api/client.js'

const PARTS = { multiple_correct: 'Multiple correct', fill_blank: 'Fill in the blank', brief: 'Brief answers', voice: 'Voice answers' }
const PART_ONE = { multiple_correct: 'Multiple correct', fill_blank: 'Fill in the blank', brief: 'Brief answer', voice: 'Voice answer' }
const BLANK = '________'
const fmt = (n) => (Number.isInteger(n) ? String(n) : n.toFixed(1))

// W9: the session report. Polls every 2.5 seconds until grading is complete, so rows fill in gradually.
// Nothing here says whether AI wrote the marks or the text (FR-20).
export default function Report({ id, onBack, onSaveStory }) {
  const [session, setSession] = useState(null)
  const [error, setError] = useState('')
  const [open, setOpen] = useState({}) // position -> true/false; unset means "open unless mastered"
  const [exportError, setExportError] = useState('')

  useEffect(() => {
    let timer
    let stopped = false
    async function load() {
      try {
        const { session: s } = await api(`/sessions/${id}`)
        if (stopped) return
        setSession(s)
        if (s.status === 'completed' && !s.gradingComplete) timer = setTimeout(load, 2500)
      } catch (e) {
        if (!stopped) setError(e.message)
      }
    }
    load()
    return () => { stopped = true; clearTimeout(timer) }
  }, [id])

  if (error && !session) return <><p role="alert">! Error: {error}</p><button type="button" onClick={onBack}>Back to Home</button></>
  if (!session) return <p>Loading…</p>
  if (session.status !== 'completed') return <><p>This session has no report.</p><button type="button" onClick={onBack}>Back to Home</button></>

  const marked = session.totalMarks != null
  const pct = marked && session.maxMarks ? Math.round((100 * session.totalMarks) / session.maxMarks) : null
  const parts = [...new Set(session.attempts.map((a) => a.question.type))]
  const date = session.completedAt ? new Date(session.completedAt).toLocaleDateString(undefined, { day: 'numeric', month: 'short' }) : ''

  return (
    <>
      <h1>Practice session report</h1>
      <p>{date} · {session.role.name} · {session.resume.fileName}</p>
      {!session.gradingComplete && <p aria-live="polite">{marked ? 'Writing your feedback…' : 'Marking your answers…'}</p>}
      {session.report?.headline && <p><strong>{session.report.headline}</strong></p>}

      {marked && (
        <>
          <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
            <section aria-label="Total"><h2>Total</h2><p>{fmt(session.totalMarks)} / {session.maxMarks}</p><p>{pct}%</p></section>
            <section aria-label="Mastered"><h2>Mastered</h2><p>{session.masteredCount} of {session.attempts.length}</p></section>
            <section aria-label="Weakest resume points">
              <h2>Weakest resume points</h2>
              {session.weakestPoints.length === 0 ? <p>None.</p> : <ul>{session.weakestPoints.map((p) => <li key={p.id}>{p.text}</li>)}</ul>}
            </section>
          </div>

          <h2>Marks by part</h2>
          <table>
            <thead><tr><th scope="col">Part</th><th scope="col">Marks</th><th scope="col">Out of</th></tr></thead>
            <tbody>
              {session.marksByPart.map((p) => <tr key={p.type}><th scope="row">{PARTS[p.type]}</th><td>{fmt(p.marks)}</td><td>{fmt(p.maxMarks)}</td></tr>)}
            </tbody>
          </table>
          {session.report?.summary && <p>{session.report.summary}</p>}
        </>
      )}

      {parts.map((type, n) => (
        <section key={type} aria-label={PARTS[type]}>
          <h2>Part {n + 1}: {PARTS[type]}</h2>
          {session.attempts.filter((a) => a.question.type === type).map((a) => {
            const isOpen = open[a.position] ?? (a.mastered !== true)
            const status = a.score == null ? 'Marking…' : a.mastered ? '✓ Mastered' : '! Needs work'
            return (
              <div key={a.position}>
                <h3>
                  <button type="button" aria-expanded={isOpen} aria-controls={`row-${a.position}`} onClick={() => setOpen((o) => ({ ...o, [a.position]: !isOpen }))}>
                    {isOpen ? '▾' : '▸'} Q{a.position} · {PART_ONE[type]} · {a.score == null ? '…' : `${fmt(a.score)}/${fmt(a.maxMarks)}`} · {status}
                  </button>
                </h3>
                <div id={`row-${a.position}`} hidden={!isOpen}><Detail a={a} onSaveStory={onSaveStory} /></div>
              </div>
            )
          })}
        </section>
      ))}

      {exportError && <p role="alert">! Error: {exportError}</p>}
      <p>
        <button type="button" onClick={onBack}>Back to Home</button>{' '}
        <button type="button" disabled={!session.gradingComplete} onClick={() => download(`/sessions/${id}/export`).then(() => setExportError(''), (e) => setExportError(e.message))}>Export JSON</button>
      </p>
    </>
  )
}

function Detail({ a, onSaveStory }) {
  const q = a.question
  const ans = a.answer
  return (
    <>
      <p>{q.type === 'fill_blank' ? q.text.split(BLANK).reduce((t, part, i) => (i === 0 ? part : `${t}[blank ${i}]${part}`), '') : q.text}</p>
      {q.type === 'multiple_correct' && <Options a={a} />}
      {q.type === 'fill_blank' && <Blanks a={a} />}
      {(q.type === 'brief' || q.type === 'voice') && <Written a={a} />}
      {!ans && <p>No answer.</p>}
      {q.isBehavioral && (ans?.transcript ?? ans?.text) && (
        <p><button type="button" onClick={() => onSaveStory({ action: ans.transcript ?? ans.text, sourceAttemptId: a.id })}>Save as story</button></p>
      )}
    </>
  )
}

function Options({ a }) {
  const picked = a.answer?.selectedOptions ?? []
  return (
    <ul>
      {a.question.options.map((o) => (
        <li key={o.key}>
          {o.key}. {o.text}
          {a.correctOptions?.includes(o.key) ? ' — correct' : ''}{picked.includes(o.key) ? ' — your choice' : ''}
        </li>
      ))}
    </ul>
  )
}

function Blanks({ a }) {
  const given = a.answer?.blankAnswers ?? []
  return (
    <ul>
      {(a.acceptedAnswers ?? []).map((words, i) => (
        <li key={i}>
          Blank {i + 1}: {a.blankResults?.[i]?.correct ? '✓' : '✗'} your answer “{given[i] ?? ''}” · accepted: {words.join(', ')}
        </li>
      ))}
    </ul>
  )
}

function Written({ a }) {
  const ans = a.answer
  const voice = a.question.type === 'voice'
  const stats = ans?.voiceStats
  return (
    <>
      {ans && <p>{voice && ans.answeredVia === 'voice' ? 'Transcript' : 'Your answer'}: “{ans.transcript ?? ans.text}”</p>}
      {voice && ans && (stats
        ? <p>Delivery: {stats.wordsPerMinute} words/min · {stats.fillerCount} filler words · {stats.wordCount} words · {stats.durationSeconds} s</p>
        : <p>typed, no delivery stats</p>)}
      {a.feedback?.deliveryComment && <p>{a.feedback.deliveryComment}</p>}
      {a.checklist && (
        <>
          <h4>Checklist</h4>
          <ul>
            {a.checklist.map((c) => (
              <li key={c.item}>{c.passed ? '✓' : '✗'} {c.label}{c.passed && c.quote ? <> — “{c.quote}”</> : null}</li>
            ))}
          </ul>
        </>
      )}
      {a.keyFacts?.length > 0 && (
        <>
          <h4>Key facts (read-only)</h4>
          <ul>{a.keyFacts.map((k, i) => <li key={i}>{k}</li>)}</ul>
        </>
      )}
      {a.feedback?.status === 'pending' && <p aria-live="polite">Feedback is being written…</p>}
      {a.feedback?.text && <p>Feedback: {a.feedback.text}{a.feedback.whatToFix ? ` What to fix: ${a.feedback.whatToFix}` : ''}</p>}
    </>
  )
}
