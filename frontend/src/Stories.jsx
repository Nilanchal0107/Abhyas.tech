import { useEffect, useState } from 'react'
import { api } from './api/client.js'

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
      <h1>Story bank</h1>
      <button type="button" className="primary" onClick={onNew}>New story</button>
      {error && <p role="alert">! Error: {error}</p>}
      {!data && !error && <p>Loading…</p>}
      {data && (
        <>
          {data.tags.length > 0 && (
            <div role="group" aria-label="Filter by tag">
              <button type="button" aria-pressed={tag === null} onClick={() => setTag(null)}>All</button>{' '}
              {data.tags.map((t) => <span key={t}><button type="button" aria-pressed={tag === t} onClick={() => setTag(t)}>{t}</button>{' '}</span>)}
            </div>
          )}
          {data.stories.length === 0 && (
            <p>{tag ? `No stories with the tag ${tag}.` : 'No stories yet. Save one from a behavioral answer, or write one now.'}</p>
          )}
          {data.stories.map((s) => (
            <article key={s.id} aria-label={s.title} className="card">
              <h2>{s.title}</h2>
              {s.tags.length > 0 && <p>{s.tags.join(' · ')}</p>}
              {s.preview && <p>{s.preview}</p>}
              {confirming === s.id ? (
                <p role="alert">Delete this story? <button type="button" className="danger" onClick={() => remove(s.id)}>Yes, delete</button> <button type="button" onClick={() => setConfirming(null)}>Cancel</button></p>
              ) : (
                <p><button type="button" onClick={() => onEdit(s.id)}>Edit</button> <button type="button" className="danger" onClick={() => setConfirming(s.id)}>Delete</button></p>
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

  if (!loaded) return error ? <><p role="alert">! Error: {error}</p><button type="button" onClick={onClose}>Back to Story bank</button></> : <p>Loading…</p>

  const field = (key, label, multiline) => (
    <div className="field" key={key}>
      <label>{label}
        {multiline
          ? <textarea rows={4} value={v[key]} aria-invalid={!!errors[key]} onChange={(e) => setV({ ...v, [key]: e.target.value })} />
          : <input value={v[key]} aria-invalid={!!errors[key]} onChange={(e) => setV({ ...v, [key]: e.target.value })} />}
      </label>
      {errors[key] && <p className="error" role="alert">! Error: {errors[key]}</p>}
    </div>
  )

  return (
    <form onSubmit={save} noValidate>
      <h1>{id ? 'Edit story' : 'New story'}</h1>
      {field('title', 'Title')}
      {BOXES.map(([k, label]) => field(k, label, true))}

      <div className="field">
        <p id="tags-label">Tags</p>
        <ul aria-labelledby="tags-label" className="chips">
          {tags.map((t) => <li key={t}>{t} <button type="button" aria-label={`Remove tag ${t}`} onClick={() => setTags(tags.filter((x) => x !== t))}>✕</button></li>)}
        </ul>
        <label>Add a tag
          <input value={newTag} onChange={(e) => setNewTag(e.target.value)} onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); addTag() } }} />
        </label>{' '}
        <button type="button" onClick={addTag}>Add tag</button>
        {errors.tags && <p className="error" role="alert">! Error: {errors.tags}</p>}
      </div>

      <fieldset>
        <legend>Linked resume points (optional)</legend>
        {resumes.every((r) => r.points.length === 0) && <p>No resume points yet.</p>}
        {resumes.filter((r) => r.points.length > 0).map((r) => (
          <fieldset key={r.id}>
            <legend>{r.fileName}</legend>
            {r.points.map((p) => (
              <div key={p.id}>
                <label>
                  <input type="checkbox" checked={picked.includes(p.id)} onChange={(e) => setPicked(e.target.checked ? [...picked, p.id] : picked.filter((x) => x !== p.id))} /> {p.text}
                </label>
              </div>
            ))}
          </fieldset>
        ))}
        {errors.resumePointIds && <p className="error" role="alert">! Error: {errors.resumePointIds}</p>}
      </fieldset>

      {error && <p className="error" role="alert">! Error: {error}</p>}
      <p><button type="submit" disabled={saving}>Save story</button> <button type="button" onClick={onClose}>Cancel</button></p>
    </form>
  )
}
