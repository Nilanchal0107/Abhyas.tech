import { useEffect, useRef, useState } from 'react'
import { api } from './api/client.js'
import VoiceAnswer from './VoiceAnswer.jsx'
import { clock, MAX_SECONDS } from './lib/recorder.js'
import { firstOpen, voiceBody } from './lib/sessions.js'
import { Alert, Chip, Spinner } from './ui.jsx'
import { ArrowRight, Check, SignOut, Timer } from '@phosphor-icons/react'

const toValue = (a) => (!a ? null : a.answeredVia === 'voice' ? { mode: 'voice', transcript: a.transcript, stats: a.voiceStats } : { mode: 'typed', text: a.text ?? '' })
const filled = (v) => v && (v.mode === 'voice' || v.text.trim() !== '')

// Interview mode (W8i): one spoken or typed question at a time, then one AI follow-up, forward only. Each answer has a 3 minute timer;
// when it runs out the answer is sent as it is (a recording in progress is allowed to finish first). The browser never gets the key facts.
export default function Interview({ session, onExit, onSubmitted }) {
  const { id, attempts } = session
  const start = Math.max(0, firstOpen(attempts))
  const [index, setIndex] = useState(start)
  const [step, setStep] = useState(attempts[start].answer ? 'follow' : 'main') // main | follow
  const [main, setMain] = useState(toValue(attempts[start].answer))
  const [follow, setFollow] = useState(null)
  const [followText, setFollowText] = useState(attempts[start].followUp?.text ?? null)
  const [left, setLeft] = useState(MAX_SECONDS)
  const [recording, setRecording] = useState(false)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const leaveRef = useRef(null)
  const next = useRef(null)

  const a = attempts[index]
  const last = index === attempts.length - 1

  // the follow-up is written once on the server, so asking again after a reload returns the same text
  useEffect(() => {
    if (step !== 'follow' || followText) return
    setBusy(true)
    api(`/sessions/${id}/attempts/${a.position}/follow-up`, { method: 'POST' })
      .then(({ attempt }) => setFollowText(attempt.followUp.text))
      .catch((e) => { if (e.type === 'session-closed') onExit(); else setError(e.errors?.[0]?.message ?? e.message) })
      .finally(() => setBusy(false))
  }, [step, index])

  // one timer per answer; each step change sets `left` back to the full time in the same update, so a finished timer cannot fire twice
  useEffect(() => {
    const t = setInterval(() => setLeft((s) => Math.max(0, s - 1)), 1000)
    return () => clearInterval(t)
  }, [index, step])
  useEffect(() => { if (left === 0 && !recording && !busy) next.current?.() }, [left, recording, busy])

  const put = (path, method, body) => api(`/sessions/${id}/attempts/${a.position}${path}`, { method, headers: { 'Content-Type': 'application/json' }, body: body && JSON.stringify(body) })

  async function advance() {
    if (last) {
      await api(`/sessions/${id}/submit`, { method: 'POST' })
      return onSubmitted(id)
    }
    const n = attempts[index + 1]
    setLeft(MAX_SECONDS); setIndex(index + 1); setStep(n.answer ? 'follow' : 'main'); setMain(toValue(n.answer)); setFollow(null); setFollowText(n.followUp?.text ?? null)
  }

  async function run(job) {
    setBusy(true); setError('')
    try { await job() }
    catch (e) { if (e.type === 'session-closed') onExit(); else setError(e.errors?.[0]?.message ?? e.message) }
    finally { setBusy(false) }
  }

  // main answer: save it, then the follow-up appears. Nothing said means the question is skipped (zero marks), with no follow-up.
  next.current = () => run(async () => {
    if (step === 'main') {
      await put('', 'PUT', { answer: voiceBody(main) })
      if (!filled(main)) return advance()
      setLeft(MAX_SECONDS); setStep('follow')
    } else {
      await put('/follow-up', 'PUT', { answer: voiceBody(follow) })
      await advance()
    }
  })

  async function leave() {
    try { await api(`/sessions/${id}/abandon`, { method: 'POST' }); onExit() }
    catch (e) { leaveRef.current?.close(); setError(e.message) }
  }

  const value = step === 'main' ? main : follow
  const set = step === 'main' ? setMain : setFollow

  return (
    <>
      <div className="session-head">
        <p className="part">Interview <span>· Question {index + 1} of {attempts.length} · {a.question.isBehavioral ? 'Behavioral' : 'Technical'}{step === 'follow' ? ' · Follow-up' : ''}</span></p>
        <button type="button" className="small" onClick={() => leaveRef.current?.showModal()}><SignOut size={18} aria-hidden="true" />Leave interview</button>
      </div>
      <div className="row">
        <div role="progressbar" aria-label="Questions answered" aria-valuemin={0} aria-valuemax={attempts.length} aria-valuenow={index}
          aria-valuetext={`${index} of ${attempts.length} done`} className="progress">
          <div style={{ width: `${(index / attempts.length) * 100}%` }} />
        </div>
        <span className="muted num">{index} of {attempts.length} done</span>
      </div>
      <p role="timer" aria-label="Time left for this answer" className="timer" style={{ marginTop: 16 }}><Timer size={22} aria-hidden="true" />Time left: <strong>{clock(left)}</strong></p>

      <div className="qcard" key={`${a.position}-${step}`}>
        <p className="qtext-lg">{a.question.text}</p>
        {step === 'follow' && (followText
          ? <p className="feedback"><Chip kind="warning">Follow-up</Chip> {followText}</p>
          : <p className="loading" aria-live="polite"><Spinner />Your interviewer is thinking of a follow-up…</p>)}
        {(step === 'main' || followText) && (
          <VoiceAnswer sessionId={id} position={a.position} value={value} onChange={set} onBusy={setRecording} />
        )}
      </div>

      <Alert>{error}</Alert>
      <div className="actions">
        <span className="spacer" />
        <button type="button" className="primary" disabled={busy || (step === 'follow' && !followText)} onClick={() => next.current()}>
          {step === 'main' ? (filled(main) ? 'Next: follow-up' : (last ? 'Skip and finish' : 'Skip question')) : last ? 'Finish interview' : 'Next question'}
          {step === 'follow' && last ? <Check size={18} weight="bold" aria-hidden="true" /> : <ArrowRight size={18} aria-hidden="true" />}
        </button>
      </div>

      <dialog ref={leaveRef} aria-labelledby="leave-title">
        <h2 id="leave-title">Leave this interview?</h2>
        <p>Your session will be marked as abandoned. You can start a new one any time.</p>
        <div className="actions">
          <button type="button" className="danger" onClick={leave}>Leave interview</button>
          <button type="button" autoFocus onClick={() => leaveRef.current.close()}>Keep going</button>
        </div>
      </dialog>
    </>
  )
}
