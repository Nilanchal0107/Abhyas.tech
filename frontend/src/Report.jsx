import { useEffect, useState } from 'react'
import { api, download } from './api/client.js'
import { Alert, Chip, Loading, Rich, Spinner } from './ui.jsx'
import { ArrowLeft, CaretDown, CheckCircle, DownloadSimple, Info, Warning, XCircle } from '@phosphor-icons/react'

const PARTS = { multiple_correct: 'Multiple correct', fill_blank: 'Fill in the blank', brief: 'Brief answers', voice: 'Voice answers' }
const PART_ONE = { multiple_correct: 'Multiple correct', fill_blank: 'Fill in the blank', brief: 'Brief answer', voice: 'Voice answer' }
const BLANK = '________'
const fmt = (n) => (Number.isInteger(n) ? String(n) : n.toFixed(1))

// W9: the session report. Polls every 2.5 seconds until grading is complete, so rows fill in gradually.
// A keyword-graded answer says so and can be regraded with AI (skill-bank D9); a regrade restarts the polling.
export default function Report({ id, onBack }) {
  const [session, setSession] = useState(null)
  const [error, setError] = useState('')
  const [open, setOpen] = useState({}) // position -> true/false; unset means "open unless mastered"
  const [exportError, setExportError] = useState('')
  const [reload, setReload] = useState(0)
  const [regrading, setRegrading] = useState({}) // position -> 'busy' or an error message

  async function regrade(position) {
    setRegrading((r) => ({ ...r, [position]: 'busy' }))
    try {
      const { session: s } = await api(`/sessions/${id}/attempts/${position}/regrade`, { method: 'POST' })
      setSession(s)
      setRegrading((r) => ({ ...r, [position]: undefined }))
      setReload((n) => n + 1)
    } catch (e) {
      setRegrading((r) => ({ ...r, [position]: e.message }))
    }
  }

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
  }, [id, reload])

  if (error && !session) return <><Alert>{error}</Alert><button type="button" onClick={onBack}><ArrowLeft size={18} aria-hidden="true" />Back to Home</button></>
  if (!session) return <Loading />
  if (session.status !== 'completed') return <><p>This session has no report.</p><button type="button" onClick={onBack}><ArrowLeft size={18} aria-hidden="true" />Back to Home</button></>

  const marked = session.totalMarks != null
  const pct = marked && session.maxMarks ? Math.round((100 * session.totalMarks) / session.maxMarks) : null
  const parts = [...new Set(session.attempts.map((a) => a.question.type))]
  const date = session.completedAt ? new Date(session.completedAt).toLocaleDateString(undefined, { day: 'numeric', month: 'short' }) : ''

  return (
    <>
      <div className="page-head">
        <span className="eyebrow">Report</span>
        <h1>{session.mode === 'interview' ? 'Interview report' : 'Practice session report'}</h1>
        <p>{date} · {session.role.name} · {session.resume.fileName}</p>
      </div>
      {!session.gradingComplete && <p className="loading" aria-live="polite"><Spinner />{marked ? 'Writing your feedback…' : 'Marking your answers…'}</p>}
      {session.report?.headline && <p className="headline"><span className="hl">{session.report.headline}</span></p>}

      {marked && (
        <>
          <div className="report-cards">
            <section aria-label="Total"><h2>Total</h2><p className="stat"><strong><span className="mark big">{fmt(session.totalMarks)} / {session.maxMarks}</span></strong><span style={{ marginTop: 8 }}>{pct}%</span></p></section>
            <section aria-label="Mastered"><h2>Mastered</h2><p className="stat"><strong>{session.masteredCount} of {session.attempts.length}</strong><span>questions</span></p></section>
            <section aria-label="Weakest resume points">
              <h2>Weakest resume points</h2>
              {session.weakestPoints.length === 0 ? <p style={{ margin: 0 }}>None.</p> : <ul>{session.weakestPoints.map((p) => <li key={p.id}>{p.text}</li>)}</ul>}
            </section>
          </div>

          <h2>Marks by part</h2>
          <div className="table-scroll">
            <table>
              <thead><tr><th scope="col">Part</th><th scope="col">Marks</th><th scope="col">Out of</th><th scope="col"><span className="visually-hidden">Share of marks</span></th></tr></thead>
              <tbody>
                {session.marksByPart.map((p) => (
                  <tr key={p.type}>
                    <th scope="row">{PARTS[p.type]}</th><td className="num">{fmt(p.marks)}</td><td className="num">{fmt(p.maxMarks)}</td>
                    <td style={{ width: '30%' }}><span className="meter" aria-hidden="true"><i style={{ width: `${p.maxMarks ? (100 * p.marks) / p.maxMarks : 0}%` }} /></span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {session.report?.summary && <p style={{ marginTop: 16 }}>{session.report.summary}</p>}
        </>
      )}

      {parts.map((type, n) => (
        <section key={type} aria-label={PARTS[type]} className="part-block">
          <h2>Part {n + 1}: {PARTS[type]}</h2>
          {session.attempts.filter((a) => a.question.type === type).map((a) => {
            const isOpen = open[a.position] ?? (a.mastered !== true)
            const status = a.score == null ? <Chip><Spinner size={16} />Marking…</Chip>
              : a.mastered ? <Chip kind="success" Icon={CheckCircle}>Mastered</Chip> : <Chip kind="warning" Icon={Warning}>Needs work</Chip>
            return (
              <div key={a.position} className="qrow">
                <h3>
                  <button type="button" aria-expanded={isOpen} aria-controls={`row-${a.position}`} onClick={() => setOpen((o) => ({ ...o, [a.position]: !isOpen }))}>
                    <CaretDown size={18} weight="bold" className="caret" aria-hidden="true" />
                    <span className="q-no">Q{a.position}</span>
                    <span className="q-kind">{PART_ONE[type]}</span>
                    <span className="q-score">{a.score == null ? '…' : `${fmt(a.score)}/${fmt(a.maxMarks)}`}</span>
                    {status}
                  </button>
                </h3>
                <div id={`row-${a.position}`} className="qbody" hidden={!isOpen}>
                  <Detail a={a} canRegrade={session.gradingComplete} regrading={regrading[a.position]} onRegrade={() => regrade(a.position)} />
                </div>
              </div>
            )
          })}
        </section>
      ))}

      <Alert>{exportError}</Alert>
      <div className="actions">
        <button type="button" className="primary" onClick={onBack}><ArrowLeft size={18} aria-hidden="true" />Back to Home</button>
        <button type="button" disabled={!session.gradingComplete} onClick={() => download(`/sessions/${id}/export`).then(() => setExportError(''), (e) => setExportError(e.message))}><DownloadSimple size={18} aria-hidden="true" />Export JSON</button>
      </div>
    </>
  )
}

function Detail({ a, canRegrade, regrading, onRegrade }) {
  const q = a.question
  const ans = a.answer
  return (
    <>
      <div className="qtext"><Rich text={q.type === 'fill_blank' ? q.text.split(BLANK).reduce((t, part, i) => (i === 0 ? part : `${t}[blank ${i}]${part}`), '') : q.text} /></div>
      {q.type === 'multiple_correct' && <Options a={a} />}
      {q.type === 'fill_blank' && <Blanks a={a} />}
      {(q.type === 'brief' || q.type === 'voice') && <Written a={a} />}
      {a.gradedWithoutAi && (
        <div className="notice">
          <Info size={20} aria-hidden="true" />
          <div>
            <p style={{ margin: '0 0 8px' }}>Graded without AI: keyword matching gave this mark, so it may be too strict, and it does not count toward mastery.</p>
            <button type="button" className="small" disabled={!canRegrade || regrading === 'busy'} onClick={onRegrade}>{regrading === 'busy' ? 'Regrading…' : 'Regrade with AI'}</button>
            {regrading && regrading !== 'busy' && <Alert>{regrading}</Alert>}
          </div>
        </div>
      )}
      {!ans && <p className="muted">No answer.</p>}
    </>
  )
}

function Options({ a }) {
  const picked = a.answer?.selectedOptions ?? []
  return (
    <ul className="options">
      {a.question.options.map((o) => {
        const right = a.correctOptions?.includes(o.key)
        const mine = picked.includes(o.key)
        return (
          <li key={o.key} className={right ? 'correct' : mine ? 'wrong' : ''}>
            <span className="key">{o.key}.</span> <Rich inline text={o.text} />
            <span className="tag">
              {right && <Chip kind="success" Icon={CheckCircle}>Correct</Chip>}{' '}
              {mine && <Chip kind={right ? 'neutral' : 'error'} Icon={right ? null : XCircle}>Your choice</Chip>}
            </span>
          </li>
        )
      })}
    </ul>
  )
}

function Blanks({ a }) {
  const given = a.answer?.blankAnswers ?? []
  return (
    <ul className="check-list">
      {(a.acceptedAnswers ?? []).map((words, i) => {
        const ok = a.blankResults?.[i]?.correct
        return (
          <li key={i} className={ok ? 'yes' : 'no'}>
            {ok ? <CheckCircle size={20} weight="fill" aria-label="Correct" /> : <XCircle size={20} weight="fill" aria-label="Wrong" />}
            <span>Blank {i + 1}: your answer “{given[i] ?? ''}” · accepted: {words.join(', ')}</span>
          </li>
        )
      })}
    </ul>
  )
}

function Written({ a }) {
  const ans = a.answer
  const voice = a.question.type === 'voice'
  const stats = ans?.voiceStats
  return (
    <>
      {ans && <div className={`answer-box${a.question.format === 'code' ? ' code' : ''}`}><b>{voice && ans.answeredVia === 'voice' ? 'Transcript' : 'Your answer'}</b>“{ans.transcript ?? ans.text}”</div>}
      {a.followUp && <div className="answer-box"><b>Follow-up</b>{a.followUp.text}<br />{a.followUp.answered ? <>Your reply: “{a.followUp.answer}”</> : 'No reply.'}</div>}
      {voice && ans && (stats
        ? <p className="meta"><span>Delivery:</span><span>{stats.wordsPerMinute} words/min</span><span>{stats.fillerCount} filler words</span><span>{stats.wordCount} words</span><span>{stats.durationSeconds} s</span></p>
        : <p className="muted">typed, no delivery stats</p>)}
      {a.feedback?.deliveryComment && <p>{a.feedback.deliveryComment}</p>}
      {a.checklist && (
        <>
          <h4>Checklist</h4>
          <ul className="check-list">
            {a.checklist.map((c) => (
              <li key={c.item} className={c.passed ? 'yes' : 'no'}>
                {c.passed ? <CheckCircle size={20} weight="fill" aria-label="Done" /> : <XCircle size={20} weight="fill" aria-label="Missing" />}
                <span>{c.label}{c.passed && c.quote ? <q>{c.quote}</q> : null}</span>
              </li>
            ))}
          </ul>
        </>
      )}
      {a.keyFacts?.length > 0 && (
        <>
          <h4>Key facts (read-only)</h4>
          <ul className="facts">{a.keyFacts.map((k, i) => <li key={i}>{k}</li>)}</ul>
        </>
      )}
      {a.feedback?.status === 'pending' && <p className="loading" aria-live="polite"><Spinner />Feedback is being written…</p>}
      {a.feedback?.text && <div className="feedback"><p><strong>Feedback:</strong> {a.feedback.text}</p>{a.feedback.whatToFix && <p><strong>What to fix:</strong> {a.feedback.whatToFix}</p>}</div>}
    </>
  )
}
