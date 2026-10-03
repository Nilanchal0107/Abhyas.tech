import { useEffect, useId, useState } from 'react'
import { api } from './api/client.js'

// W4 target role field: a combobox over GET /roles. Arrow keys move, Enter picks, Escape closes.
export default function RoleSearch({ onPick }) {
  const [text, setText] = useState('')
  const [picked, setPicked] = useState(null)
  const [roles, setRoles] = useState(null) // null = no search yet
  const [error, setError] = useState(null)
  const [active, setActive] = useState(-1)
  const [open, setOpen] = useState(false)
  const id = useId()

  useEffect(() => {
    const q = text.trim()
    if (!q || picked) { setRoles(null); setError(null); return }
    let stale = false
    const t = setTimeout(() => {
      api(`/roles?q=${encodeURIComponent(q.slice(0, 100))}`)
        .then((d) => { if (!stale) { setRoles(d.roles); setError(null); setActive(-1); setOpen(true) } })
        .catch((e) => { if (!stale) setError(e.message) })
    }, 250)
    return () => { stale = true; clearTimeout(t) }
  }, [text, picked])

  function pick(role) {
    setPicked(role)
    setText(role.name)
    setOpen(false)
    onPick(role)
  }

  function onKeyDown(e) {
    if (!open || !roles?.length) return
    if (e.key === 'ArrowDown') { e.preventDefault(); setActive((i) => (i + 1) % roles.length) }
    else if (e.key === 'ArrowUp') { e.preventDefault(); setActive((i) => (i <= 0 ? roles.length - 1 : i - 1)) }
    else if (e.key === 'Enter' && active >= 0) { e.preventDefault(); pick(roles[active]) }
    else if (e.key === 'Escape') setOpen(false)
  }

  const showList = open && roles?.length > 0
  return (
    <div className="role-search">
      <label htmlFor={id}>Target role</label>
      <input id={id} type="text" role="combobox" autoComplete="off" maxLength={100}
        placeholder={'Search roles, e.g. "SDE" or "Backend"'}
        aria-expanded={showList} aria-controls={`${id}-list`} aria-autocomplete="list"
        aria-activedescendant={showList && active >= 0 ? `${id}-${active}` : undefined}
        value={text} onKeyDown={onKeyDown}
        onChange={(e) => { setText(e.target.value); if (picked) { setPicked(null); onPick(null) } }} />
      {showList && (
        <ul id={`${id}-list`} role="listbox" aria-label="Matching roles">
          {roles.map((r, i) => (
            <li key={r.id} id={`${id}-${i}`} role="option" aria-selected={i === active}
              onMouseDown={(e) => { e.preventDefault(); pick(r) }}>
              {r.name}{r.matchedOn === 'alias' ? ` (${r.matchedAlias})` : ''}{r.isOwn ? ' · your role' : ''}
            </li>
          ))}
        </ul>
      )}
      <p aria-live="polite">{roles && !roles.length && !picked ? 'No role found.' : ''}</p>
      {roles && !roles.length && !picked && (
        <p>Can't find your role? <button type="button" disabled title="Coming in the next step">Add a missing role</button></p>
      )}
      {error && <p role="alert">! Error: {error}</p>}
    </div>
  )
}
