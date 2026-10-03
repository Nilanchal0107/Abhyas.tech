import { useId, useState } from 'react'
import { postJson } from './api/client.js'
import { limitErrors, pointCount } from './lib/resume.js'

// W6: step 2 of 3. Editable, collapsible form for the parsed resume. Saving creates the resume and its resume points.
const blank = (s) => (s.trim() ? s : null)

function Section({ title, empty, defaultOpen = true, children }) {
  const [open, setOpen] = useState(empty || defaultOpen)
  const id = useId()
  return (
    <section>
      <h2>
        <button type="button" aria-expanded={open} aria-controls={id} onClick={() => setOpen(!open)}>
          {open ? '▾' : '▸'} {title}
        </button>
        {empty && <span> ! Nothing found</span>}
      </h2>
      <div id={id} hidden={!open}>{children}</div>
    </section>
  )
}

function Field({ label, value, onChange, type = 'text', note }) {
  const id = useId()
  return (
    <div className="field">
      <label htmlFor={id}>{label}</label>
      <input id={id} type={type} value={value ?? ''} onChange={(e) => onChange(blank(e.target.value))} />
      {note && <small>{note}</small>}
    </div>
  )
}

function Bullets({ bullets, onChange }) {
  return (
    <>
      {bullets.map((b, i) => (
        <div className="field" key={i}>
          <label htmlFor={`b${i}-${bullets.length}`} className="visually-hidden">Bullet {i + 1}</label>
          <textarea id={`b${i}-${bullets.length}`} rows={2} value={b}
            onChange={(e) => onChange(bullets.map((x, j) => (j === i ? e.target.value : x)))} />
          <button type="button" onClick={() => onChange(bullets.filter((_, j) => j !== i))}>Remove</button>
        </div>
      ))}
      <button type="button" onClick={() => onChange([...bullets, ''])}>Add a bullet</button>
    </>
  )
}

export default function Review({ initial, byAi, unavailable, fileName, roleId, interviewDate, parseSource, onBack, onSaved }) {
  const [p, setP] = useState(initial)
  const [skill, setSkill] = useState('')
  const [saving, setSaving] = useState(false)
  const [problems, setProblems] = useState([])

  const set = (key, value) => setP((r) => ({ ...r, [key]: value }))
  const profile = (key) => (value) => set('profile', { ...p.profile, [key]: value })
  const setAt = (key, i, patch) => set(key, p[key].map((x, j) => (j === i ? { ...x, ...patch } : x)))
  const removeAt = (key, i) => set(key, p[key].filter((_, j) => j !== i))

  function addSkill() {
    const s = skill.trim()
    if (s && !p.skills.some((x) => x.toLowerCase() === s.toLowerCase())) set('skills', [...p.skills, s])
    setSkill('')
  }

  async function save() {
    // blank bullets are dropped, as the server does
    const clean = { ...p }
    for (const k of ['experience', 'projects']) clean[k] = p[k].map((e) => ({ ...e, bullets: e.bullets.map((b) => b.trim()).filter(Boolean) }))
    const found = limitErrors(clean)
    setProblems(found)
    if (found.length) return
    setSaving(true)
    try {
      const { resume } = await postJson('/resumes', { fileName, targetRoleId: roleId, interviewDate, parseSource, parsed: clean })
      onSaved(resume)
    } catch (e) {
      setProblems([e.message, ...e.errors.map((x) => x.message)])
    } finally {
      setSaving(false)
    }
  }

  return (
    <>
      <h1>Review</h1>
      <p>Step 2 of 3: Review</p>
      {byAi && <p>! Some parts were read by an AI assistant. Please check them.</p>}
      {unavailable && <p>! We could not read this resume automatically. Please fill in the form yourself.</p>}

      <Section title="Contact" defaultOpen>
        <Field label="Name" value={p.profile.name} onChange={profile('name')} />
        <Field label="Phone" value={p.profile.phone} onChange={profile('phone')} />
        <Field label="Email" value={p.profile.email} onChange={profile('email')} />
        <Field label="Location" value={p.profile.location} onChange={profile('location')} />
        <p>Phone and email are never sent to AI.</p>
      </Section>

      <Section title="Education" empty={!p.education.length} defaultOpen={false}>
        {p.education.map((e, i) => (
          <fieldset key={i}>
            <legend>Education {i + 1}</legend>
            <Field label="School" value={e.school} onChange={(v) => setAt('education', i, { school: v })} />
            <Field label="Degree" value={e.degree} onChange={(v) => setAt('education', i, { degree: v })} />
            <Field label="Date" value={e.date} onChange={(v) => setAt('education', i, { date: v })} />
            <button type="button" onClick={() => removeAt('education', i)}>Remove</button>
          </fieldset>
        ))}
        <button type="button" onClick={() => set('education', [...p.education, { school: null, degree: null, date: null, details: [] }])}>Add a school</button>
      </Section>

      <Section title="Experience" empty={!p.experience.length}>
        {p.experience.map((e, i) => (
          <fieldset key={i}>
            <legend>Job {i + 1}</legend>
            <Field label="Company" value={e.company} onChange={(v) => setAt('experience', i, { company: v })} />
            <Field label="Title" value={e.title} onChange={(v) => setAt('experience', i, { title: v })} />
            <Field label="Date" value={e.date} onChange={(v) => setAt('experience', i, { date: v })} />
            <Bullets bullets={e.bullets} onChange={(b) => setAt('experience', i, { bullets: b })} />
            <button type="button" onClick={() => removeAt('experience', i)}>Remove this job</button>
          </fieldset>
        ))}
        <button type="button" onClick={() => set('experience', [...p.experience, { company: null, title: null, date: null, bullets: [] }])}>Add a job</button>
      </Section>

      <Section title="Projects" empty={!p.projects.length}>
        {p.projects.map((e, i) => (
          <fieldset key={i}>
            <legend>Project {i + 1}</legend>
            <Field label="Name" value={e.name} onChange={(v) => setAt('projects', i, { name: v })} />
            <Field label="Date" value={e.date} onChange={(v) => setAt('projects', i, { date: v })} />
            <Bullets bullets={e.bullets} onChange={(b) => setAt('projects', i, { bullets: b })} />
            <button type="button" onClick={() => removeAt('projects', i)}>Remove this project</button>
          </fieldset>
        ))}
        <button type="button" onClick={() => set('projects', [...p.projects, { name: null, date: null, bullets: [] }])}>Add a project</button>
      </Section>

      <Section title="Skills" empty={!p.skills.length}>
        <ul>
          {p.skills.map((s) => (
            <li key={s}>{s} <button type="button" aria-label={`Remove ${s}`} onClick={() => set('skills', p.skills.filter((x) => x !== s))}>Remove</button></li>
          ))}
        </ul>
        <label htmlFor="new-skill">Add a skill</label>{' '}
        <input id="new-skill" value={skill} onChange={(e) => setSkill(e.target.value)}
          onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); addSkill() } }} />{' '}
        <button type="button" onClick={addSkill}>Add</button>
      </Section>

      <p>{pointCount(p)} resume points</p>
      {problems.length > 0 && (
        <div className="error" role="alert">
          {problems.map((m) => <p key={m}>! Error: {m}</p>)}
        </div>
      )}
      <p aria-live="polite">{saving ? 'Preparing your questions...' : ''}</p>
      <button type="button" onClick={onBack} disabled={saving}>Back</button>{' '}
      <button type="button" onClick={save} disabled={saving}>Save and continue</button>
    </>
  )
}
