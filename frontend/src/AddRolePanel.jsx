import { useEffect, useRef, useState } from 'react'
import { postJson } from './api/client.js'

// W5 inline panel: AI-drafted skills (editable), save a private role. onSaved(role) fills the role field.
export default function AddRolePanel({ name, onSaved, onClose }) {
  const [skills, setSkills] = useState([])
  const [drafting, setDrafting] = useState(true)
  const [newSkill, setNewSkill] = useState('')
  const [skillNote, setSkillNote] = useState(null)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState(null)
  const [existing, setExisting] = useState(null) // role-exists: { id, name }
  const heading = useRef(null)

  useEffect(() => { heading.current?.focus() }, [])

  useEffect(() => {
    let stale = false
    postJson('/roles/draft-skills', { name })
      .then((d) => { if (!stale) setSkills(d.skills) })
      .catch(() => {}) // the user types their own; nothing tells them why
      .finally(() => { if (!stale) setDrafting(false) })
    return () => { stale = true }
  }, [name])

  function addSkill() {
    const s = newSkill.trim()
    if (!s) return
    if (skills.some((x) => x.toLowerCase() === s.toLowerCase())) { setSkillNote(`"${s}" is already in the list.`); return }
    setSkills([...skills, s])
    setSkillNote(null)
    setNewSkill('')
  }

  async function save() {
    setSaving(true); setError(null); setExisting(null)
    try {
      const { role } = await postJson('/roles', { name, aliases: [], skills })
      onSaved({ id: role.id, name: role.name, category: null, isOwn: true })
    } catch (e) {
      if (e.type === 'role-exists') setExisting({ id: e.problem.existingRoleId, name: e.problem.existingRoleName })
      else setError(e.errors?.[0]?.message ?? e.message)
    } finally { setSaving(false) }
  }

  return (
    <section className="add-role" aria-labelledby="add-role-h">
      <h3 id="add-role-h" ref={heading} tabIndex={-1}>Add "{name}" as your own role</h3>
      <p>(visible only to you)</p>
      <p>Skills (suggested by AI, edit before saving)</p>
      <p aria-live="polite">{drafting ? 'Drafting skills...' : ''}</p>
      <ul>
        {skills.map((s) => (
          <li key={s}>{s} <button type="button" aria-label={`Remove ${s}`} onClick={() => setSkills(skills.filter((x) => x !== s))}>x</button></li>
        ))}
      </ul>
      <label htmlFor="add-skill">Add a skill</label>
      <input id="add-skill" type="text" maxLength={100} value={newSkill} disabled={skills.length >= 30}
        onChange={(e) => { setNewSkill(e.target.value); setSkillNote(null) }}
        onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); addSkill() } }} />
      <button type="button" onClick={addSkill} disabled={skills.length >= 30}>Add</button>
      <p aria-live="polite">{skillNote}</p>
      {existing && (
        <p role="alert">A similar role exists: {existing.name}.{' '}
          <button type="button" onClick={() => onSaved({ id: existing.id, name: existing.name, category: null, isOwn: false })}>Use it instead</button>
        </p>
      )}
      {error && <p className="error" role="alert">! Error: {error}</p>}
      <button type="button" onClick={save} disabled={saving || drafting}>Save role</button>
      <button type="button" onClick={onClose}>Cancel</button>
    </section>
  )
}
