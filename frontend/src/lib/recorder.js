// Recorder timing for a voice answer: up to 3 minutes (api-contracts 5, transcribe).
export const MAX_SECONDS = 180

export const elapsedSeconds = (startMs, nowMs) => Math.max(0, Math.floor((nowMs - startMs) / 1000))
export const shouldStop = (seconds) => seconds >= MAX_SECONDS
export const clock = (seconds) => `${String(Math.floor(seconds / 60)).padStart(2, '0')}:${String(seconds % 60).padStart(2, '0')}`
