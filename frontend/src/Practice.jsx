import { useEffect, useRef, useState } from 'react'
import { api } from './api/client.js'
import VoiceAnswer from './VoiceAnswer.jsx'

const PARTS = { multiple_correct: 'Multiple correct', fill_blank: 'Fill in the blank', brief: 'Brief answer', voice: 'Voice answer' }
const BLANK = '________'
const filled = (v) => v.some((x) => x.trim() !== '')
const voiceFilled = (v) => v && (v.mode === 'voice' || v.text.trim() !== '')
const words = (t) => (t.trim() ? t.trim().split(/\s+/).length : 0)

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

  if (error && !session) return <><p role="alert">! Error: {error}</p><button type="button" onClick={onExit}>Back</button></>
  if (!session) return <p>Loading…</p>

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

  const voiceBody = (v) => !v ? null
    : v.mode === 'voice' ? { text: null, transcript: v.transcript, voiceStats: v.stats, answeredVia: 'voice' }
    : v.text.trim() ? { text: v.text, answeredVia: 'typed' } : null

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
      <p>Part {parts.indexOf(q.type) + 1} of {parts.length}: {PARTS[q.type]} · Question {index + 1} of {total}</p>
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <div role="progressbar" aria-label="Questions answered" aria-valuemin={0} aria-valuemax={total} aria-valuenow={answered}
          aria-valuetext={`${answered} of ${total} answered`} style={{ flex: 1, height: 10, background: 'GrayText', opacity: 0.3 }}>
          <div style={{ width: `${(answered / total) * 100}%`, height: '100%', background: 'Highlight' }} />
        </div>
        <span>{answered} of {total} answered</span>
        <button type="button" onClick={() => leaveRef.current?.showModal()}>Leave session</button>
      </div>

      {isVoice ? (
        <div key={a.position}>
          <p>Question {index + 1} · {PARTS[q.type]} · {q.isBehavioral ? 'Behavioral' : 'Technical'} · {v?.mode === 'typed' ? 5 : a.maxMarks} marks</p>
          <p>{q.text}</p>
          <VoiceAnswer sessionId={id} position={a.position} value={v} onChange={(x) => setVoice((o) => ({ ...o, [a.position]: x }))} />
        </div>
      ) : brief ? (
        <div key={a.position}>
          <p>Question {index + 1} · {PARTS[q.type]} · {q.isBehavioral ? 'Behavioral' : 'Technical'} · {a.maxMarks} marks</p>
          {q.isBehavioral && <p>Tip: Situation, Task, Action, Result.</p>}
          <label htmlFor="brief-answer">{q.text}</label>
          <textarea id="brief-answer" rows={6} maxLength={5000} style={{ width: '100%' }} value={selected[0]} onChange={(e) => type(0, e.target.value)} />
          <p aria-live="polite">{words(selected[0])} {words(selected[0]) === 1 ? 'word' : 'words'}</p>
        </div>
      ) : blank ? (
        <fieldset key={a.position}>
          <legend>Question {index + 1} · {PARTS[q.type]} · {a.maxMarks} marks</legend>
          <p>{q.text.split(BLANK).reduce((t, part, i) => (i === 0 ? part : `${t}[blank ${i}]${part}`), '')}</p>
          {selected.map((v, i) => (
            <div key={i}>
              <label>{q.blankCount > 1 ? `Blank ${i + 1}` : 'Your answer'}{' '}
                <input type="text" value={v} maxLength={200} onChange={(e) => type(i, e.target.value)} />
              </label>
            </div>
          ))}
        </fieldset>
      ) : (
        <fieldset key={a.position}>
          <legend>Question {index + 1} · {PARTS[q.type]} · {a.maxMarks} marks</legend>
          <p>Select every correct answer.</p>
          <p>{q.text}</p>
          {q.options.map((o) => (
            <div key={o.key}>
              <label>
                <input type="checkbox" checked={selected.includes(o.key)} onChange={() => toggle(o.key)} /> {o.key}. {o.text}
              </label>
            </div>
          ))}
        </fieldset>
      )}

      <p className="error" role="alert">{error ? `! Error: ${error}` : ''}</p>
      <button type="button" disabled={busy || index === 0} onClick={() => save(() => setIndex(index - 1))}>Back</button>{' '}
      {last
        ? <button type="button" disabled={busy} onClick={() => save(submit)}>Submit practice session</button>
        : <button type="button" disabled={busy} onClick={() => save(() => setIndex(index + 1))}>Next</button>}

      {/* a native modal dialog traps focus and gives it back to the Leave button when it closes */}
      <dialog ref={leaveRef} aria-labelledby="leave-title">
        <h2 id="leave-title">Leave this session?</h2>
        <p>Your session will be marked as abandoned. You can start a new one any time.</p>
        <button type="button" onClick={leave}>Leave session</button>{' '}
        <button type="button" autoFocus onClick={() => leaveRef.current.close()}>Keep practicing</button>
      </dialog>
    </>
  )
}
