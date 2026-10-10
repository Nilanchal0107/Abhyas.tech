import { useEffect, useRef, useState } from 'react'
import { api } from './api/client.js'
import { clock, elapsedSeconds, shouldStop } from './lib/recorder.js'
import { SpeechRecognition, fillerCount, listen, pace } from './lib/liveWords.js'
import { ArrowCounterClockwise, Keyboard, Microphone, Stop } from '@phosphor-icons/react'
import { Alert, Loading } from './ui.jsx'

// The browser's own speech recognition (Chrome, Edge) shows live words while recording, and gives the transcript when Whisper is down.
// Chrome sends that audio to Google; we still store none. Without it (Firefox) nothing live is shown.
let whisperDown = false // once a transcription failed, the next voice questions go straight to the browser

// W8 8d and 8e. value: null, { mode: 'voice', transcript, stats } or { mode: 'typed', text }.
// The recording is sent for transcription and then dropped: only the transcript and numbers are kept (trd 7.3).
export default function VoiceAnswer({ sessionId, position, value, onChange, onBusy, disabled = false }) {
  const [phase, setPhase] = useState('idle') // idle | recording | transcribing
  const [seconds, setSeconds] = useState(0)
  const [problem, setProblem] = useState('')
  const [live, setLive] = useState({ done: '', interim: '' }) // live words, a preview only: Whisper's transcript is what is graded
  const mr = useRef(null)
  const timer = useRef(null)
  const captions = useRef(null)

  useEffect(() => { onBusy?.(phase !== 'idle') }, [phase]) // the interview timer waits for a recording to finish

  useEffect(() => () => { captions.current?.abort(); clearInterval(timer.current); if (mr.current?.state === 'recording') { mr.current.onstop = null; mr.current.stop() } mr.current?.abort?.() }, [])

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
    const t0 = Date.now()
    let session
    try {
      session = listen((done, interim) => setLive({ done, interim }), ({ text, failed }) => {
        clearInterval(timer.current)
        mr.current = null
        if (failed) return toTyped("We couldn't use your microphone.")
        if (!text) return toTyped("We couldn't hear your answer.")
        setPhase('idle')
        onChange({ mode: 'voice', transcript: text, stats: { durationSeconds: elapsedSeconds(t0, Date.now()) } })
      })
    } catch { return toTyped("We couldn't use your microphone.") }
    mr.current = session
    setLive({ done: '', interim: '' })
    setSeconds(0); setPhase('recording')
    timer.current = setInterval(() => {
      const s = elapsedSeconds(t0, Date.now())
      setSeconds(s)
      if (shouldStop(s)) session.stop()
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
        captions.current?.stop(); captions.current = null
        stream.getTracks().forEach((t) => t.stop())
        upload(new Blob(chunks, { type: 'audio/webm' }))
      }
      const t0 = Date.now()
      rec.start()
      setLive({ done: '', interim: '' })
      if (SpeechRecognition) { // never lets a problem with the live words touch the recording
        try { captions.current = listen((done, interim) => setLive({ done, interim })) } catch { captions.current = null }
      }
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
        {SpeechRecognition && <LiveWords live={live} seconds={seconds} />}
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
        <button type="button" disabled={disabled} onClick={() => { onChange(null); start() }}><ArrowCounterClockwise size={18} aria-hidden="true" />Re-record</button>
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
        <button type="button" className="primary" disabled={disabled} onClick={start}><Microphone size={18} weight="fill" aria-hidden="true" />Start recording</button>
        <span className="or">or</span>
        <button type="button" onClick={() => onChange({ mode: 'typed', text: '' })}><Keyboard size={18} aria-hidden="true" />Type your answer instead</button>
      </div>
    </div>
  )
}

// Live words, announced to screen readers only as each sentence is finished; the grey tail is still being recognised.
function LiveWords({ live, seconds }) {
  const text = `${live.done}${live.interim}`
  return (
    <div className="live-words">
      <p className="muted" style={{ margin: 0 }}>Live words (a preview: your recording is written out again when you stop)</p>
      <p aria-live="polite" style={{ minHeight: '3em', margin: '4px 0' }}>{live.done}<span aria-hidden="true" className="interim">{live.interim}</span></p>
      {seconds >= 5 && text.trim() && <p className="muted" style={{ margin: 0 }}>Pace {pace(text, seconds)} words a minute · {fillerCount(text)} filler words so far</p>}
    </div>
  )
}
