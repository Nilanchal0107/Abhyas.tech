import { useEffect, useRef, useState } from 'react'
import { api } from './api/client.js'
import { clock, elapsedSeconds, shouldStop } from './lib/recorder.js'

// W8 8d and 8e. value: null, { mode: 'voice', transcript, stats } or { mode: 'typed', text }.
// The recording is sent for transcription and then dropped: only the transcript and numbers are kept (trd 7.3).
export default function VoiceAnswer({ sessionId, position, value, onChange }) {
  const [phase, setPhase] = useState('idle') // idle | recording | transcribing
  const [seconds, setSeconds] = useState(0)
  const [problem, setProblem] = useState('')
  const mr = useRef(null)
  const timer = useRef(null)

  useEffect(() => () => { clearInterval(timer.current); if (mr.current?.state === 'recording') { mr.current.onstop = null; mr.current.stop() } }, [])

  const toTyped = (message) => { setProblem(message); setPhase('idle'); onChange({ mode: 'typed', text: '' }) }

  async function upload(blob) {
    setPhase('transcribing')
    const body = new FormData()
    body.append('audio', blob, 'answer.webm')
    try {
      const r = await api(`/sessions/${sessionId}/attempts/${position}/transcribe`, { method: 'POST', body })
      setPhase('idle')
      onChange({ mode: 'voice', transcript: r.transcript, stats: r.stats })
    } catch (e) {
      if (e.type === 'session-closed') return
      toTyped("We couldn't turn your recording into text.")
    }
  }

  async function start() {
    setProblem('')
    let stream
    try {
      stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    } catch { return toTyped("We couldn't use your microphone.") }
    try {
      const chunks = []
      const rec = new MediaRecorder(stream, { mimeType: 'audio/webm;codecs=opus' })
      mr.current = rec
      rec.ondataavailable = (e) => chunks.push(e.data)
      rec.onstop = () => {
        clearInterval(timer.current)
        stream.getTracks().forEach((t) => t.stop())
        upload(new Blob(chunks, { type: 'audio/webm' }))
      }
      const t0 = Date.now()
      rec.start()
      setSeconds(0); setPhase('recording')
      timer.current = setInterval(() => {
        const s = elapsedSeconds(t0, Date.now())
        setSeconds(s)
        if (shouldStop(s)) rec.stop() // stops by itself at 3 minutes
      }, 250)
    } catch {
      stream.getTracks().forEach((t) => t.stop())
      toTyped("We couldn't use your microphone.")
    }
  }

  if (phase === 'recording') {
    return (
      <div>
        <p role="status"><span aria-hidden="true" style={{ color: 'red' }}>●</span> Recording</p>
        <p>Time: <span>{clock(seconds)}</span> of 03:00</p>
        <button type="button" onClick={() => mr.current.stop()}>■ Stop</button>
      </div>
    )
  }
  if (phase === 'transcribing') return <p role="status">Transcribing…</p>

  if (value?.mode === 'voice') {
    return (
      <div>
        <h3>Your answer (transcribed)</h3>
        <blockquote>{value.transcript}</blockquote>
        <p>Press Next to submit this answer.</p>
        <button type="button" onClick={() => { onChange(null); start() }}>Re-record</button>
      </div>
    )
  }
  if (value?.mode === 'typed') {
    const id = `voice-typed-${position}`
    return (
      <div>
        {problem && <p role="alert">✗ {problem} Type your answer instead.</p>}
        <label htmlFor={id}>Type your answer</label>
        <textarea id={id} rows={6} maxLength={5000} style={{ width: '100%' }} value={value.text} onChange={(e) => onChange({ mode: 'typed', text: e.target.value })} />
        <p>Graded like a brief answer (5 marks), no voice stats.</p>
        <button type="button" onClick={() => { onChange(null); setProblem('') }}>Record instead</button>
      </div>
    )
  }
  return (
    <div>
      <button type="button" onClick={start}><span aria-hidden="true">● </span>Start recording</button>{' '}
      or <button type="button" onClick={() => onChange({ mode: 'typed', text: '' })}>Type your answer instead</button>
    </div>
  )
}
