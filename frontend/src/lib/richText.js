// Splits question text into plain, inline code (`like this`) and fenced code blocks (``` on its own lines) so code shows in monospace.
// Text without a backtick comes back as one plain part, so ordinary questions look exactly as before.
export function parts(text) {
  const out = []
  const fence = /```[^\n`]*\n([\s\S]*?)\n?```/g
  let last = 0, m
  while ((m = fence.exec(text)) !== null) {
    if (m.index > last) out.push(...inline(text.slice(last, m.index)))
    out.push({ kind: 'block', text: m[1] })
    last = fence.lastIndex
  }
  if (last < text.length) out.push(...inline(text.slice(last)))
  return out
}

function inline(text) {
  return text.split(/(`[^`\n]+`)/g).filter((p) => p !== '').map((p) => (p.length > 2 && p.startsWith('`') && p.endsWith('`') ? { kind: 'code', text: p.slice(1, -1) } : { kind: 'text', text: p }))
}
