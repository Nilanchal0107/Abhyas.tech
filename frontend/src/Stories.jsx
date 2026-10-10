import { useEffect, useState } from 'react'
import { api } from './api/client.js'
import { Alert, Chip, Loading } from './ui.jsx'
import { ArrowLeft, Pencil, Plus, Tag, Trash, X } from '@phosphor-icons/react'

const BOXES = [['situation', 'Situation'], ['task', 'Task'], ['action', 'Action'], ['result', 'Result'], ['lesson', 'Lesson learned']]

// W11a (route /stories). onNew() and onEdit(id) open the form page.
export function StoryList({ onNew, onEdit }) {
  const [data, setData] = useState(null)
  const [tag, setTag] = useState(null)
  const [error, setError] = useState('')
  const [confirming, setConfirming] = useState(null)

  async function load(t) {
    try { setData(await api(`/stories${t ? `?tag=${encodeURIComponent(t)}` : ''}`)); setError('') } catch (e) { setError(e.message) }
  }
  useEffect(() => { load(tag) }, [tag])

  async function remove(id) {
    try {
      await api(`/stories/${id}`, { method: 'DELETE' })
      setConfirming(null)
      if (tag && !(await api('/stories')).tags.includes(tag)) setTag(null) // the last story with this tag is gone
      else await load(tag)
    } catch (e) { setError(e.message) }
  }

  return (
    <>
      <div className="head-row">
        <div className="page-head">
          <span className="eyebrow">STAR stories</span>
          <h1>Story bank</h1>
        </div>
        <button type="button" className="primary" onClick={onNew}><Plus size={18} weight="bold" aria-hidden="true" />New story</button>
      </div>
      <Alert>{error}</Alert>
      {!data && !error && <Loading />}
      {data && (
        <>
          {data.tags.length > 0 && (
            <div role="group" aria-label="Filter by tag" className="actions" style={{ marginTop: 0 }}>
              <button type="button" className="small" aria-pressed={tag === null} onClick={() => setTag(null)}>All</button>
              {data.tags.map((t) => <button key={t} type="button" className="small" aria-pressed={tag === t} onClick={() => setTag(t)}>{t}</button>)}
            </div>
          )}
          {data.stories.length === 0 && (
            <p className="muted">{tag ? `No stories with the tag ${tag}.` : 'No stories yet. Save one from a behavioral answer, or write one now.'}</p>
          )}
          {data.stories.map((s) => (
            <article key={s.id} aria-label={s.title} className="sheet">
              <h2 style={{ marginTop: 0 }}>{s.title}</h2>
              {s.tags.length > 0 && <p className="actions" style={{ margin: '0 0 8px' }}>{s.tags.map((t) => <Chip key={t} kind="neutral" Icon={Tag}>{t}</Chip>)}</p>}
              {s.preview && <p>{s.preview}</p>}
              {confirming === s.id ? (
                <div role="alert" className="alert" style={{ flexWrap: 'wrap', alignItems: 'center' }}>
                  <span>Delete this story?</span>
                  <button type="button" className="danger small" onClick={() => remove(s.id)}>Yes, delete</button>
                  <button type="button" className="small" onClick={() => setConfirming(null)}>Cancel</button>
                </div>
              ) : (
                <div className="actions" style={{ marginTop: 8 }}>
                  <button type="button" className="small" onClick={() => onEdit(s.id)}><Pencil size={16} aria-hidden="true" />Edit</button>
                  <button type="button" className="small danger" onClick={() => setConfirming(s.id)}><Trash size={16} aria-hidden="true" />Delete</button>
                </div>
              )}
            </article>
          ))}
        </>
      )}
    </>
  )
}

// W11b (routes /stories/new and /stories/:id/edit). `prefill` is { action, sourceAttemptId } from "Save as story" in the report.
export function StoryForm({ id, prefill, onClose }) {
  const [v, setV] = useState({ title: '', situation: '', task: '', action: prefill?.action ?? '', result: '', lesson: '' })
  const [tags, setTags] = useState([])
  const [newTag, setNewTag] = useState('')
  const [picked, setPicked] = useState([])
  const [resumes, setResumes] = useState([]) // [{ id, fileName, points }]
  const [loaded, setLoaded] = useState(!id)
  const [errors, setErrors] = useState({})
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    (async () => {
      try {
        const list = (await api('/resumes')).resumes
        setResumes(await Promise.all(list.map(async (r) => ({ id: r.id, fileName: r.fileName, points: (await api(`/resumes/${r.id}`)).resume.points }))))
        if (id) {
          const { story } = await api(`/stories/${id}`)
          setV(Object.fromEntries(['title', ...BOXES.map(([k]) => k)].map((k) => [k, story[k] ?? ''])))
          setTags(story.tags)
          setPicked(story.resumePoints.map((p) => p.id))
          setLoaded(true)
        }
      } catch (e) { setError(e.message) }
    })()
  }, [id])

  function addTag() {
    const t = newTag.trim()
    if (t && !tags.includes(t)) setTags([...tags, t])
    setNewTag('')
  }

  async function save(e) {
    e.preventDefault()
    setSaving(true); setErrors({}); setError('')
    const body = { ...Object.fromEntries(Object.entries(v).map(([k, x]) => [k, x.trim() || null])), tags, resumePointIds: picked }
    if (!id && prefill?.sourceAttemptId) body.sourceAttemptId = prefill.sourceAttemptId
    try {
      await api(id ? `/stories/${id}` : '/stories', { method: id ? 'PUT' : 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) })
      onClose()
    } catch (err) {
      if (err.errors.length) setErrors(Object.fromEntries(err.errors.map((x) => [x.field, x.message])))
      else setError(err.message)
    } finally { setSaving(false) }
  }

  if (!loaded) return error ? <><Alert>{error}</Alert><button type="button" onClick={onClose}><ArrowLeft size={18} aria-hidden="true" />Back to Story bank</button></> : <Loading />

  const field = (key, label, multiline) => (
    <div className="field" key={key}>
      <label htmlFor={`story-${key}`}>{label}</label>
      {multiline
        ? <textarea id={`story-${key}`} rows={4} value={v[key]} aria-invalid={!!errors[key]} onChange={(e) => setV({ ...v, [key]: e.target.value })} />
        : <input id={`story-${key}`} value={v[key]} aria-invalid={!!errors[key]} onChange={(e) => setV({ ...v, [key]: e.target.value })} />}
      <Alert>{errors[key]}</Alert>
    </div>
  )

  return (
    <form onSubmit={save} noValidate>
      <div className="page-head">
        <span className="eyebrow">STAR story</span>
        <h1>{id ? 'Edit story' : 'New story'}</h1>
      </div>
      {field('title', 'Title')}
      {BOXES.map(([k, label]) => field(k, label, true))}

      <div className="field">
        <p id="tags-label" style={{ fontWeight: 600, marginBottom: 4 }}>Tags</p>
        <ul aria-labelledby="tags-label" className="chips">
          {tags.map((t) => <li key={t}>{t} <button type="button" aria-label={`Remove tag ${t}`} onClick={() => setTags(tags.filter((x) => x !== t))}><X size={14} weight="bold" aria-hidden="true" /></button></li>)}
        </ul>
        <div className="add-row">
          <div className="field">
            <label htmlFor="new-tag">Add a tag</label>
            <input id="new-tag" value={newTag} onChange={(e) => setNewTag(e.target.value)} onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); addTag() } }} />
          </div>
          <button type="button" onClick={addTag}><Plus size={18} weight="bold" aria-hidden="true" />Add tag</button>
        </div>
        <Alert>{errors.tags}</Alert>
      </div>

      <fieldset>
        <legend>Linked resume points (optional)</legend>
        {resumes.every((r) => r.points.length === 0) && <p>No resume points yet.</p>}
        {resumes.filter((r) => r.points.length > 0).map((r) => (
          <fieldset key={r.id}>
            <legend>{r.fileName}</legend>
            {r.points.map((p) => (
              <label key={p.id} className="choice">
                <input type="checkbox" checked={picked.includes(p.id)} onChange={(e) => setPicked(e.target.checked ? [...picked, p.id] : picked.filter((x) => x !== p.id))} />
                <span>{p.text}</span>
              </label>
            ))}
          </fieldset>
        ))}
        <Alert>{errors.resumePointIds}</Alert>
      </fieldset>

      <Alert>{error}</Alert>
      <div className="actions">
        <button type="submit" disabled={saving}>Save story</button>
        <button type="button" onClick={onClose}>Cancel</button>
      </div>
    </form>
  )
}
