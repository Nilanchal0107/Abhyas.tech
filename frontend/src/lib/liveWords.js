// Live words while recording (FR-15 preview). The counts mirror VoiceStats.java so the preview and the saved numbers agree on what a word and a filler are.
// The text comes from the browser's speech recognition and is only a preview: after the recording stops, Whisper's transcript is the one that is graded.
export const SpeechRecognition = typeof window === 'undefined' ? undefined : window.SpeechRecognition || window.webkitSpeechRecognition

const WORD = /[\p{L}\p{N}][\p{L}\p{N}'’-]*/gu
const FILLER = /(?<![\p{L}\p{N}])(?:um+|uh+|uhm+|er+m?|ah+|hmm+|you know|i mean)(?![\p{L}\p{N}])/giu

export const wordCount = (text) => (text.match(WORD) ?? []).length
export const fillerCount = (text) => (text.match(FILLER) ?? []).length
export const pace = (text, seconds) => (seconds <= 0 ? 0 : Math.round((wordCount(text) * 60) / seconds))

// Starts recognition that keeps running through pauses (Chrome ends it after silence) until stop() is called.
// onChange(finalText, interimText) fires on every result; onEnd({ text, failed }) fires once, after stop() or a real error. Throws if it cannot start.
export function listen(onChange, onEnd) {
  const rec = new SpeechRecognition()
  rec.lang = 'en-US'; rec.continuous = true; rec.interimResults = true
  let done = '', stopped = false, failed = false
  rec.onresult = (e) => {
    let interim = ''
    for (let i = e.resultIndex; i < e.results.length; i++) {
      const t = e.results[i][0].transcript
      if (e.results[i].isFinal) done += `${t} `; else interim += t
    }
    onChange(done, interim)
  }
  rec.onerror = (e) => { if (e.error !== 'no-speech' && e.error !== 'aborted') failed = true }
  rec.onend = () => {
    if (!stopped && !failed) { try { rec.start(); return } catch { /* cannot restart: end below */ } }
    onEnd?.({ text: done.trim(), failed })
  }
  rec.start()
  return {
    stop() { stopped = true; try { rec.stop() } catch { /* already stopped */ } },
    abort() { stopped = true; rec.onend = null; try { rec.abort() } catch { /* already stopped */ } }, // for leaving the screen: no callback fires
  }
}
