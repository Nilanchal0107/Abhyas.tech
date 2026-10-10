// Status shared by Home and History (W2, W10). The screen pairs `kind` with an icon, so a status is an icon plus a word (UI-UX).
export function statusLabel(s) {
  if (s.status === 'completed') return { kind: 'success', label: s.voiceSkipped ? 'Completed, voice part skipped' : 'Completed' }
  if (s.status === 'in_progress') return { kind: 'warning', label: 'In progress' }
  return { kind: 'error', label: 'Abandoned' }
}

export const day = (iso) => (iso ? new Date(iso).toLocaleDateString(undefined, { day: 'numeric', month: 'short' }) : '')

const fmt = (n) => (Number.isInteger(n) ? String(n) : n.toFixed(1))

// "61 / 80", or "-" while the session has no marks (unfinished, abandoned or still being marked)
export const total = (s) => (s.totalMarks == null ? '-' : `${fmt(s.totalMarks)} / ${s.maxMarks}`)

// The body of a saved voice answer (api-contracts 5): recorded, typed, or null to clear it. v is { mode: 'voice', transcript, stats } or { mode: 'typed', text }.
export const voiceBody = (v) => !v ? null
  : v.mode === 'voice' ? { text: null, transcript: v.transcript, voiceStats: v.stats, answeredVia: 'voice' }
  : v.text.trim() ? { text: v.text, answeredVia: 'typed' } : null

// The first question an interview still needs something from: unanswered, or answered with a follow-up not yet answered. -1 when none.
export const firstOpen = (attempts) => attempts.findIndex((a) => !a.answer || (a.followUp && !a.followUp.answered))

export const modeLabel = (s) => (s.mode === 'interview' ? 'Interview' : 'Quiz')
