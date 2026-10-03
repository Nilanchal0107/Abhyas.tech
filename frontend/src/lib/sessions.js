// Labels shared by Home and History (W2, W10). A status is always an icon plus a word (UI-UX).
export function statusLabel(s) {
  if (s.status === 'completed') return s.voiceSkipped ? '✓ Completed, voice part skipped' : '✓ Completed'
  if (s.status === 'in_progress') return '! In progress'
  return '✗ Abandoned'
}

export const day = (iso) => (iso ? new Date(iso).toLocaleDateString(undefined, { day: 'numeric', month: 'short' }) : '')

const fmt = (n) => (Number.isInteger(n) ? String(n) : n.toFixed(1))

// "61 / 80", or "-" while the session has no marks (unfinished, abandoned or still being marked)
export const total = (s) => (s.totalMarks == null ? '-' : `${fmt(s.totalMarks)} / ${s.maxMarks}`)
