// The spoken interviewer (interview mode): the browser reads the question and the follow-up aloud. Nothing is sent anywhere.
export const canSpeak = typeof window !== 'undefined' && 'speechSynthesis' in window && typeof SpeechSynthesisUtterance !== 'undefined'

const KEY = 'abhyas.interviewer-muted'
export const isMuted = () => { try { return localStorage.getItem(KEY) === '1' } catch { return false } }
export const rememberMuted = (muted) => { try { localStorage.setItem(KEY, muted ? '1' : '0') } catch { /* storage blocked: the choice lasts until reload */ } }

// Reads `text` aloud and calls onDone once, when the reading ends, fails or is blocked (autoplay rules), so the user is never stuck waiting.
// Returns a function that stops the reading without calling onDone.
export function speak(text, onDone) {
  window.speechSynthesis.cancel()
  const u = new SpeechSynthesisUtterance(text)
  u.lang = 'en-US'
  let over = false
  const finish = () => { if (!over) { over = true; clearTimeout(guard); onDone() } }
  const guard = setTimeout(finish, 5000 + text.length * 120) // some browsers never fire `end`
  u.onend = finish
  u.onerror = finish
  window.speechSynthesis.speak(u)
  return () => { over = true; clearTimeout(guard); window.speechSynthesis.cancel() }
}
