import { useEffect, useRef, useState } from 'react'
import { api } from './api/client.js'
import { clock, elapsedSeconds, shouldStop } from './lib/recorder.js'
import { ArrowCounterClockwise, Keyboard, Microphone, Stop } from '@phosphor-icons/react'
import { Alert, Loading } from './ui.jsx'

// The browser's own speech recognition (Chrome, Edge), used when Whisper is down. Chrome sends that audio to Google; we still store none.
const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
let whisperDown = false // once a transcription failed, the next voice questions go straight to the browser

// W8 8d and 8e. value: null, { mode: 'voice', transcript, stats } or { mode: 'typed', text }.
// The recording is sent for transcription and then dropped: only the transcript and numbers are kept (trd 7.3).
export default function VoiceAnswer({ sessionId, position, value, onChange, onBusy }) {
  const [phase, setPhase] = useState('idle') // idle | recording | transcribing
  const [seconds, setSeconds] = useState(0)
  const [problem, setProblem] = useState('')
  const mr = useRef(null)
  const timer = useRef(null)

  useEffect(() => { onBusy?.(phase !== 'idle') }, [phase]) // the interview timer waits for a recording to finish

  useEffect(() => () => { clearInterval(timer.current); if (mr.current?.state === 'recording') { mr.current.onstop = null; mr.current.stop() } mr.current?.abort?.() }, [])

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
      if (SpeechRecognition) { // Whisper is down: speak again, and the browser writes the text
        whisperDown = true
        setProblem("We couldn't turn your recording into text. Speak your answer again: your browser will write it down.")
        return setPhase('idle')
      }
      toTyped("We couldn't turn your recording into text.")
    }
  }

  // Web Speech: the transcript comes from the browser, and only the length we measured is sent as voice stats.
  function startBrowser() {
    const rec = new SpeechRecognition()
    rec.lang = 'en-US'; rec.continuous = true; rec.interimResults = false
    let text = '', failed = false
    const t0 = Date.now()
    rec.onresult = (e) => { for (let i = e.resultIndex; i < e.results.length; i++) if (e.results[i].isFinal) text += `${e.results[i][0].transcript} ` }
    rec.onerror = () => { failed = true }
    rec.onend = () => {
      clearInterval(timer.current)
      mr.current = null
      if (failed) return toTyped("We couldn't use your microphone.")
      if (!text.trim()) return toTyped("We couldn't hear your answer.")
      setPhase('idle')
      onChange({ mode: 'voice', transcript: text.trim(), stats: { durationSeconds: elapsedSeconds(t0, Date.now()) } })
    }
    try { rec.start() } catch { return toTyped("We couldn't use your microphone.") }
    mr.current = rec
    setSeconds(0); setPhase('recording')
    timer.current = setInterval(() => {
      const s = elapsedSeconds(t0, Date.now())
      setSeconds(s)
      if (shouldStop(s)) rec.stop()
    }, 250)
  }

  async function start() {
    setProblem('')
    if (whisperDown && SpeechRecognition) return startBrowser()
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
      <div className="voice-box recording">
        <div className="rec-line">
          <p role="status" className="row" style={{ margin: 0 }}><span className="dot-rec" aria-hidden="true" /> Recording</p>
          <p style={{ margin: 0 }}>Time: <span className="rec-time">{clock(seconds)}</span> of 03:00</p>
        </div>
        <button type="button" className="danger" onClick={() => mr.current.stop()}><Stop size={18} weight="fill" aria-hidden="true" />Stop</button>
      </div>
    )
  }
  if (phase === 'transcribing') return <Loading>Transcribing…</Loading>

  if (value?.mode === 'voice') {
    return (
      <div className="voice-box">
        <h3 style={{ marginTop: 0 }}>Your answer (transcribed)</h3>
        <blockquote>{value.transcript}</blockquote>
        <p className="muted">Press Next to submit this answer.</p>
        <button type="button" onClick={() => { onChange(null); start() }}><ArrowCounterClockwise size={18} aria-hidden="true" />Re-record</button>
      </div>
    )
  }
  if (value?.mode === 'typed') {
    const id = `voice-typed-${position}`
    return (
      <div>
        {problem && <Alert>{problem} Type your answer instead.</Alert>}
        <label htmlFor={id} style={{ fontWeight: 600, display: 'block', marginBottom: 4 }}>Type your answer</label>
        <textarea id={id} rows={6} maxLength={5000} value={value.text} onChange={(e) => onChange({ mode: 'typed', text: e.target.value })} />
        <p className="muted" style={{ marginTop: 8 }}>Graded like a brief answer (5 marks), no voice stats.</p>
        <button type="button" onClick={() => { onChange(null); setProblem('') }}><Microphone size={18} aria-hidden="true" />Record instead</button>
      </div>
    )
  }
  return (
    <div className="voice-box">
      {problem && <Alert>{problem}</Alert>}
      <div className="actions" style={{ marginTop: 0 }}>
        <button type="button" className="primary" onClick={start}><Microphone size={18} weight="fill" aria-hidden="true" />Start recording</button>
        <span className="or">or</span>
        <button type="button" onClick={() => onChange({ mode: 'typed', text: '' })}><Keyboard size={18} aria-hidden="true" />Type your answer instead</button>
      </div>
    </div>
  )
}
