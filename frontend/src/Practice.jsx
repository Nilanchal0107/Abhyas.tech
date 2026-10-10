import { useEffect, useRef, useState } from 'react'
import { api } from './api/client.js'
import VoiceAnswer from './VoiceAnswer.jsx'
import Interview from './Interview.jsx'
import { voiceBody } from './lib/sessions.js'
import { Alert, Chip, Loading } from './ui.jsx'
import { ArrowLeft, ArrowRight, Check, Compass, SignOut } from '@phosphor-icons/react'

const PARTS = { multiple_correct: 'Multiple correct', fill_blank: 'Fill in the blank', brief: 'Brief answer', voice: 'Voice answer' }
const BLANK = '________'
const filled = (v) => v.some((x) => x.trim() !== '')
const voiceFilled = (v) => v && (v.mode === 'voice' || v.text.trim() !== '')
const words = (t) => (t.trim() ? t.trim().split(/\s+/).length : 0)

// "________" in a fill-in-the-blank question becomes a drawn line, read out as "[blank 1]".
const BlankText = ({ text }) => text.split(BLANK).flatMap((part, i) => (i === 0 ? [part] : [
  <span key={i} className="blank"><span className="visually-hidden">[blank {i}]</span></span>, part]))

// Question number, kind, and marks at the top of a question card.
function QTop({ n, type, marks, behavioral }) {
  return (
    <span className="qtop">
      <span className="qnum">Question {n}</span>
      <Chip>{PARTS[type]}</Chip>
      {behavioral != null && <Chip kind="neutral">{behavioral ? 'Behavioral' : 'Technical'}</Chip>}
      <span className="mark">{marks} marks</span>
    </span>
  )
}

// W8 frame and 8a to 8e: one question at a time. An answer is saved when the user moves on, so closing the tab keeps progress.
// The browser never receives the correct options while the session is in progress.
export default function Practice({ id, onExit, onSubmitted }) {
  const [session, setSession] = useState(null)
  const [picked, setPicked] = useState({}) // position -> selected option keys, blank answers or brief text
  const [voice, setVoice] = useState({}) // position -> { mode: 'voice', transcript, stats } or { mode: 'typed', text }
  const [index, setIndex] = useState(0)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const leaveRef = useRef(null)

  useEffect(() => {
    api(`/sessions/${id}`).then(({ session: s }) => {
      if (s.status !== 'in_progress') return onExit()
      setSession(s)
      setVoice(Object.fromEntries(s.attempts.filter((a) => a.answer && a.question.type === 'voice').map((a) => [a.position,
        a.answer.answeredVia === 'voice' ? { mode: 'voice', transcript: a.answer.transcript, stats: a.answer.voiceStats } : { mode: 'typed', text: a.answer.text ?? '' }])))
      setPicked(Object.fromEntries(s.attempts.filter((a) => a.answer && a.question.type !== 'voice').map((a) => [a.position, a.answer.selectedOptions ?? a.answer.blankAnswers ?? (a.answer.text != null ? [a.answer.text] : [])])))
      const firstOpen = s.attempts.findIndex((a) => !a.answer)
      setIndex(firstOpen < 0 ? 0 : firstOpen)
    }).catch((e) => setError(e.message))
  }, [id])

  if (error && !session) return <><Alert>{error}</Alert><button type="button" onClick={onExit}><ArrowLeft size={18} aria-hidden="true" />Back</button></>
  if (!session) return <Loading />
  if (session.mode === 'interview') return <Interview session={session} onExit={onExit} onSubmitted={onSubmitted} />

  const total = session.attempts.length
  const a = session.attempts[index]
  const q = a.question
  const blank = q.type === 'fill_blank'
  const brief = q.type === 'brief'
  const isVoice = q.type === 'voice'
  const v = voice[a.position] ?? null
  const selected = picked[a.position] ?? (blank ? Array(q.blankCount).fill('') : brief ? [''] : [])
  const answered = Object.values(picked).filter(filled).length + Object.values(voice).filter(voiceFilled).length
  const parts = [...new Set(session.attempts.map((x) => x.question.type))]
  const last = index === total - 1

  function toggle(key) {
    setPicked((p) => {
      const cur = p[a.position] ?? []
      return { ...p, [a.position]: cur.includes(key) ? cur.filter((k) => k !== key) : [...cur, key] }
    })
  }

  const type = (i, v) => setPicked((p) => {
    const cur = [...(p[a.position] ?? Array(q.blankCount ?? 1).fill(''))]
    cur[i] = v
    return { ...p, [a.position]: cur }
  })

  // Saves the current answer (or clears it), then runs `then`. Skipping counts as zero.
  async function save(then) {
    setBusy(true); setError('')
    try {
      await api(`/sessions/${id}/attempts/${a.position}`, {
        method: 'PUT', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ answer: isVoice ? voiceBody(v) : !filled(selected) ? null : blank ? { blankAnswers: selected } : brief ? { text: selected[0] } : { selectedOptions: selected } }),
      })
      then()
    } catch (e) {
      if (e.type === 'session-closed') return onExit()
      setError(e.errors?.[0]?.message ?? e.message)
    } finally { setBusy(false) }
  }

  // The last answer is already saved; submitting completes the session and opens the report while grading continues (api-contracts 6).
  async function submit() {
    try {
      await api(`/sessions/${id}/submit`, { method: 'POST' })
      onSubmitted(id)
    } catch (e) {
      if (e.type === 'session-closed') return onExit()
      setError(e.message)
    }
  }

  async function leave() {
    try {
      await api(`/sessions/${id}/abandon`, { method: 'POST' })
      onExit()
    } catch (e) {
      leaveRef.current?.close()
      setError(e.message)
    }
  }

  return (
    <>
      <div className="session-head">
        <p className="part">Part {parts.indexOf(q.type) + 1} of {parts.length}: {PARTS[q.type]} <span>· Question {index + 1} of {total}</span></p>
        <button type="button" className="small" onClick={() => leaveRef.current?.showModal()}><SignOut size={18} aria-hidden="true" />Leave session</button>
      </div>
      <div className="row">
        <div role="progressbar" aria-label="Questions answered" aria-valuemin={0} aria-valuemax={total} aria-valuenow={answered}
          aria-valuetext={`${answered} of ${total} answered`} className="progress">
          <div style={{ width: `${(answered / total) * 100}%` }} />
        </div>
        <span className="muted num">{answered} of {total} answered</span>
      </div>

      {isVoice ? (
        <div className="qcard" key={a.position}>
          <QTop n={index + 1} type={q.type} behavioral={q.isBehavioral} marks={v?.mode === 'typed' ? 5 : a.maxMarks} />
          {q.isGapSkill && <p className="hint"><Compass size={18} aria-hidden="true" />Likely to be asked for this role</p>}
          <p className="qtext-lg">{q.text}</p>
          <VoiceAnswer sessionId={id} position={a.position} value={v} onChange={(x) => setVoice((o) => ({ ...o, [a.position]: x }))} />
        </div>
      ) : brief ? (
        <div className="qcard" key={a.position}>
          <QTop n={index + 1} type={q.type} behavioral={q.isBehavioral} marks={a.maxMarks} />
          {q.isGapSkill && <p className="hint"><Compass size={18} aria-hidden="true" />Likely to be asked for this role</p>}
          {q.isBehavioral && <p className="hint">Tip: <span className="hl">Situation, Task, Action, Result.</span></p>}
          <label htmlFor="brief-answer" className="qtext-lg">{q.text}</label>
          <textarea id="brief-answer" rows={7} maxLength={5000} value={selected[0]} onChange={(e) => type(0, e.target.value)} />
          <p className="wordcount" aria-live="polite">{words(selected[0])} {words(selected[0]) === 1 ? 'word' : 'words'}</p>
        </div>
      ) : blank ? (
        <div className="qcard" key={a.position}>
          <fieldset>
            <legend><QTop n={index + 1} type={q.type} marks={a.maxMarks} /></legend>
            {q.isGapSkill && <p className="hint"><Compass size={18} aria-hidden="true" />Likely to be asked for this role</p>}
            <p className="qtext-lg"><BlankText text={q.text} /></p>
            {selected.map((v, i) => (
              <div key={i} className="blank-field">
                <label htmlFor={`blank-${i}`}>{q.blankCount > 1 ? `Blank ${i + 1}` : 'Your answer'}</label>
                <input id={`blank-${i}`} type="text" value={v} maxLength={200} onChange={(e) => type(i, e.target.value)} />
              </div>
            ))}
          </fieldset>
        </div>
      ) : (
        <div className="qcard" key={a.position}>
          <fieldset>
            <legend><QTop n={index + 1} type={q.type} marks={a.maxMarks} /></legend>
            {q.isGapSkill && <p className="hint"><Compass size={18} aria-hidden="true" />Likely to be asked for this role</p>}
            <p className="hint">Select every correct answer.</p>
            <p className="qtext-lg">{q.text}</p>
            {q.options.map((o) => (
              <label key={o.key} className="choice">
                <input type="checkbox" checked={selected.includes(o.key)} onChange={() => toggle(o.key)} />
                <span><span className="key">{o.key}.</span> {o.text}</span>
              </label>
            ))}
          </fieldset>
        </div>
      )}

      <Alert>{error}</Alert>
      <div className="actions">
        <button type="button" disabled={busy || index === 0} onClick={() => save(() => setIndex(index - 1))}><ArrowLeft size={18} aria-hidden="true" />Back</button>
        <span className="spacer" />
        {last
          ? <button type="button" className="primary" disabled={busy} onClick={() => save(submit)}><Check size={18} weight="bold" aria-hidden="true" />Submit practice session</button>
          : <button type="button" className="primary" disabled={busy} onClick={() => save(() => setIndex(index + 1))}>Next<ArrowRight size={18} aria-hidden="true" /></button>}
      </div>

      {/* a native modal dialog traps focus and gives it back to the Leave button when it closes */}
      <dialog ref={leaveRef} aria-labelledby="leave-title">
        <h2 id="leave-title">Leave this session?</h2>
        <p>Your session will be marked as abandoned. You can start a new one any time.</p>
        <div className="actions">
          <button type="button" className="danger" onClick={leave}>Leave session</button>
          <button type="button" autoFocus onClick={() => leaveRef.current.close()}>Keep practicing</button>
        </div>
      </dialog>
    </>
  )
}
