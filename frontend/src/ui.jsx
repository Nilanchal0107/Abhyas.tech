import { Fragment } from 'react'
import { Check, CircleNotch, WarningCircle } from '@phosphor-icons/react'
import { parts } from './lib/richText.js'

// The brand mark: a ruled line and a red-pen tick on an ink tile.
export function Logo({ size = 28 }) {
  return (
    <svg width={size} height={size} viewBox="0 0 32 32" aria-hidden="true">
      <rect x="1" y="1" width="30" height="30" rx="7" fill="var(--ink)" />
      <path d="M8 9.5h11M8 15h6" stroke="var(--on-ink)" strokeWidth="2" strokeLinecap="round" opacity="0.6" />
      <path d="M12.5 21.5l4.5 4.5 9-12.5" fill="none" stroke="var(--pen)" strokeWidth="3.4" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  )
}

export function Brand() {
  return <><Logo /><span>Abhyas<em>.tech</em></span></>
}

// Error box: an icon plus the word "Error", never color alone (UI-UX 7.1, 8). With no children it stays mounted and empty,
// so a screen reader announces the message when it appears.
export function Alert({ children, ...rest }) {
  if (!children) return <p role="alert" className="alert" {...rest} />
  return (
    <p role="alert" className="alert" {...rest}>
      <WarningCircle size={20} weight="fill" aria-hidden="true" />
      <span><strong>Error:</strong> {children}</span>
    </p>
  )
}

export const Spinner = ({ size = 20 }) => <CircleNotch className="spin" size={size} aria-hidden="true" />

// Spinner plus words (never color alone) that keeps the layout stable.
export function Loading({ children = 'Loading…' }) {
  return <p className="loading" role="status"><Spinner />{children}</p>
}

// A status is always an icon plus a word.
export function Chip({ kind = 'neutral', Icon, children }) {
  return <span className={`chip ${kind}`}>{Icon && <Icon size={16} weight="bold" aria-hidden="true" />}{children}</span>
}

// Step indicator for the first-time path: Upload, Review, Role skills check (UI-UX 6.4).
const STEP_NAMES = ['Upload', 'Review', 'Role skills check']
export function Steps({ current }) {
  return (
    <ol className="steps" aria-label="Progress">
      {STEP_NAMES.map((name, i) => (
        <li key={name} className={i + 1 < current ? 'done' : undefined} aria-current={i + 1 === current ? 'step' : undefined}>
          <span className="n" aria-hidden="true">{i + 1 < current ? <Check size={14} weight="bold" /> : i + 1}</span>
          {i + 1 === current ? `Step ${current} of 3: ${name}` : name}
        </li>
      ))}
    </ol>
  )
}

// Question and option text with code in monospace: `inline code` and ``` fenced blocks ```. Text without backticks renders as before.
// `inline` (options, text beside a blank) never makes a block: a fenced block becomes pre-wrapped inline code there.
export function Rich({ text, inline = false }) {
  return parts(text ?? '').map((p, i) => {
    if (p.kind === 'text') return <Fragment key={i}>{p.text}</Fragment>
    if (p.kind === 'code' || inline) return <code key={i} className={p.kind === 'block' ? 'code-wrap' : undefined}>{p.text}</code>
    return <pre key={i} tabIndex={0} aria-label="Code"><code>{p.text}</code></pre>
  })
}
