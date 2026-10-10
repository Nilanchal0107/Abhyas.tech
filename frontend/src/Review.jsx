import { useId, useState } from 'react'
import { postJson } from './api/client.js'
import { limitErrors, pointCount } from './lib/resume.js'
import { Alert, Chip, Spinner, Steps } from './ui.jsx'
import { ArrowLeft, ArrowRight, CaretDown, Info, Plus, Trash, Warning, X } from '@phosphor-icons/react'

// W6: step 2 of 3. Editable, collapsible form for the parsed resume. Saving creates the resume and its resume points.
const blank = (s) => (s.trim() ? s : null)

function Section({ title, empty, defaultOpen = true, children }) {
  const [open, setOpen] = useState(empty || defaultOpen)
  const id = useId()
  return (
    <section className="form-section">
      <h2>
        <button type="button" className="section-btn" aria-expanded={open} aria-controls={id} onClick={() => setOpen(!open)}>
          {title}
          {empty && <Chip kind="warning" Icon={Warning}>Nothing found</Chip>}
          <CaretDown size={20} weight="bold" className="caret" aria-hidden="true" />
        </button>
      </h2>
      <div id={id} className="body" hidden={!open}>{children}</div>
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
        <div className="bullet-row" key={i}>
          <label htmlFor={`b${i}-${bullets.length}`} className="visually-hidden">Bullet {i + 1}</label>
          <textarea id={`b${i}-${bullets.length}`} rows={2} value={b}
            onChange={(e) => onChange(bullets.map((x, j) => (j === i ? e.target.value : x)))} />
          <button type="button" className="small" onClick={() => onChange(bullets.filter((_, j) => j !== i))}><Trash size={16} aria-hidden="true" />Remove</button>
        </div>
      ))}
      <button type="button" className="small" onClick={() => onChange([...bullets, ''])}><Plus size={16} weight="bold" aria-hidden="true" />Add a bullet</button>
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

  const add = (label, on) => <button type="button" className="small" onClick={on}><Plus size={16} weight="bold" aria-hidden="true" />{label}</button>
  const remove = (label, on) => <button type="button" className="small danger" onClick={on}><Trash size={16} aria-hidden="true" />{label}</button>

  return (
    <>
      <div className="page-head">
        <span className="eyebrow">Check what we read</span>
        <h1>Review</h1>
      </div>
      <Steps current={2} />
      {byAi && <p className="notice"><Info size={20} aria-hidden="true" />Some parts were read by an AI assistant. Please check them.</p>}
      {unavailable && <p className="notice"><Warning size={20} aria-hidden="true" />Parts of this resume may be missing or in the wrong box. Please check the form and fix them.</p>}

      <Section title="Contact" defaultOpen>
        <div className="field-row">
          <Field label="Name" value={p.profile.name} onChange={profile('name')} />
          <Field label="Location" value={p.profile.location} onChange={profile('location')} />
          <Field label="Phone" value={p.profile.phone} onChange={profile('phone')} />
          <Field label="Email" value={p.profile.email} onChange={profile('email')} />
        </div>
        <p className="muted">Phone and email are never sent to AI.</p>
      </Section>

      <Section title="Education" empty={!p.education.length} defaultOpen={false}>
        {p.education.map((e, i) => (
          <fieldset key={i}>
            <legend>Education {i + 1}</legend>
            <Field label="School" value={e.school} onChange={(v) => setAt('education', i, { school: v })} />
            <div className="field-row">
              <Field label="Degree" value={e.degree} onChange={(v) => setAt('education', i, { degree: v })} />
              <Field label="Date" value={e.date} onChange={(v) => setAt('education', i, { date: v })} />
            </div>
            {remove('Remove', () => removeAt('education', i))}
          </fieldset>
        ))}
        {add('Add a school', () => set('education', [...p.education, { school: null, degree: null, date: null, details: [] }]))}
      </Section>

      <Section title="Experience" empty={!p.experience.length}>
        {p.experience.map((e, i) => (
          <fieldset key={i}>
            <legend>Job {i + 1}</legend>
            <div className="field-row">
              <Field label="Company" value={e.company} onChange={(v) => setAt('experience', i, { company: v })} />
              <Field label="Title" value={e.title} onChange={(v) => setAt('experience', i, { title: v })} />
              <Field label="Date" value={e.date} onChange={(v) => setAt('experience', i, { date: v })} />
            </div>
            <Bullets bullets={e.bullets} onChange={(b) => setAt('experience', i, { bullets: b })} />
            <div className="actions">{remove('Remove this job', () => removeAt('experience', i))}</div>
          </fieldset>
        ))}
        {add('Add a job', () => set('experience', [...p.experience, { company: null, title: null, date: null, bullets: [] }]))}
      </Section>

      <Section title="Projects" empty={!p.projects.length}>
        {p.projects.map((e, i) => (
          <fieldset key={i}>
            <legend>Project {i + 1}</legend>
            <div className="field-row">
              <Field label="Name" value={e.name} onChange={(v) => setAt('projects', i, { name: v })} />
              <Field label="Date" value={e.date} onChange={(v) => setAt('projects', i, { date: v })} />
            </div>
            <Bullets bullets={e.bullets} onChange={(b) => setAt('projects', i, { bullets: b })} />
            <div className="actions">{remove('Remove this project', () => removeAt('projects', i))}</div>
          </fieldset>
        ))}
        {add('Add a project', () => set('projects', [...p.projects, { name: null, date: null, bullets: [] }]))}
      </Section>

      <Section title="Skills" empty={!p.skills.length}>
        <ul className="skill-list">
          {p.skills.map((s) => (
            <li key={s}>{s} <button type="button" aria-label={`Remove ${s}`} onClick={() => set('skills', p.skills.filter((x) => x !== s))}><X size={14} weight="bold" aria-hidden="true" /></button></li>
          ))}
        </ul>
        <div className="add-row">
          <div className="field">
            <label htmlFor="new-skill">Add a skill</label>
            <input id="new-skill" value={skill} onChange={(e) => setSkill(e.target.value)}
              onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); addSkill() } }} />
          </div>
          <button type="button" onClick={addSkill}><Plus size={18} weight="bold" aria-hidden="true" />Add</button>
        </div>
      </Section>

      <p className="muted" style={{ marginTop: 16 }}>{pointCount(p)} resume points</p>
      {problems.length > 0 && (
        <div className="alert" role="alert" style={{ flexDirection: 'column' }}>
          {problems.map((m) => <p key={m} style={{ margin: 0 }}><strong>Error:</strong> {m}</p>)}
        </div>
      )}
      <p className="loading" aria-live="polite">{saving && <><Spinner />Preparing your questions...</>}</p>
      <div className="sticky-actions">
        <button type="button" onClick={onBack} disabled={saving}><ArrowLeft size={18} aria-hidden="true" />Back</button>
        <button type="button" className="primary" onClick={save} disabled={saving}>Save and continue<ArrowRight size={18} aria-hidden="true" /></button>
      </div>
    </>
  )
}
