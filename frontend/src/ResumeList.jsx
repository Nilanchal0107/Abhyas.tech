import { useEffect, useState } from 'react'
import { Link } from 'react-router'
import { api, startSession } from './api/client.js'
import { Alert, Loading, Spinner } from './ui.jsx'
import { ArrowLeft, Calendar, ChartLineUp, CheckCircle, FileText, FolderOpen, Plus, Target, Trash, ListMagnifyingGlass, Pencil } from '@phosphor-icons/react'

const day = (iso) => new Date(iso).toLocaleDateString(undefined, { day: 'numeric', month: 'short' })

// W3 open: the saved resume, read-only.
export function Open({ id, pointId, onBack }) {
  const [resume, setResume] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api(`/resumes/${id}`).then(({ resume: r }) => { setResume(r) }).catch((e) => setError(e.message))
  }, [id])

  // coming from the role skills check: bring the chosen resume point into view
  useEffect(() => {
    if (resume && pointId) document.getElementById(`point-${pointId}`)?.scrollIntoView({ block: 'center' })
  }, [resume, pointId])

  if (error && !resume) return <><button type="button" onClick={onBack}><ArrowLeft size={18} aria-hidden="true" />Back to resumes</button><Alert>{error}</Alert></>
  if (!resume) return <Loading />

  const label = { experience_bullet: 'Experience', project_bullet: 'Project', skill: 'Skill' }
  return (
    <>
      <button type="button" className="small quiet" onClick={onBack}><ArrowLeft size={18} aria-hidden="true" />Back to resumes</button>
      <div className="page-head" style={{ marginTop: 8 }}>
        <span className="eyebrow">Resume</span>
        <h1>{resume.fileName}</h1>
        <p>Target role: {resume.targetRole.name} · Saved resumes are read-only.</p>
      </div>
      <h2>{resume.parsed.profile?.name ?? 'Your resume'}</h2>
      <h3 style={{ marginTop: 0 }}>Resume points ({resume.points.length})</h3>
      {resume.points.length === 0 ? <p>Nothing found.</p> : (
        <ol className="points sheet" style={{ padding: '0 24px' }}>{resume.points.map((p) => <li key={p.id} id={`point-${p.id}`} className={p.id === pointId ? 'picked' : undefined}><span><span className="kind">{label[p.kind]}</span>{p.text}</span></li>)}</ol>
      )}
    </>
  )
}

// W3: list, delete. Open (W3) and the role skills check (W7) are their own routes. Practice starts a session (W8).
export default function ResumeList({ onUpload, onPractice, onOpen, onCheck }) {
  const [resumes, setResumes] = useState(null)
  const [error, setError] = useState('')
  const [confirm, setConfirm] = useState(null) // id waiting for inline confirmation
  const [rowError, setRowError] = useState({})
  const [preparing, setPreparing] = useState(null) // resume id whose questions are being prepared

  const load = () => api('/resumes').then((r) => setResumes(r.resumes)).catch((e) => setError(e.message))
  useEffect(() => { load() }, [])

  async function start(id) {
    setRowError({ [id]: '' })
    setPreparing(id)
    try {
      onPractice(await startSession(id))
    } catch (e) {
      setRowError({ [id]: e.message })
    } finally { setPreparing(null) }
  }

  async function remove(id) {
    try {
      await api(`/resumes/${id}`, { method: 'DELETE' })
      setConfirm(null)
      load()
    } catch (e) { setRowError({ [id]: e.message }) }
  }

  if (error) return <Alert>{error}</Alert>
  if (!resumes) return <Loading />

  return (
    <>
      <div className="head-row">
        <div className="page-head">
          <span className="eyebrow">Library</span>
          <h1>Your resumes</h1>
        </div>
        <button type="button" className="primary" onClick={onUpload}><Plus size={18} weight="bold" aria-hidden="true" />Upload a resume</button>
      </div>
      {resumes.length === 0 && <p className="muted">No resumes yet. Upload one to get your first questions.</p>}
      {resumes.map((r) => (
        <section key={r.id} aria-label={r.fileName}>
          <div className="sheet-head">
            <FileText size={32} className="file-icon" aria-hidden="true" />
            <div>
              <h2>{r.fileName}</h2>
              <p className="meta">
                <span><Target size={16} aria-hidden="true" />Target role: {r.targetRole.name}</span>
                <span><Calendar size={16} aria-hidden="true" />Uploaded {day(r.createdAt)}</span>
                <span><CheckCircle size={16} aria-hidden="true" />Mastered questions: {r.masteredCount}</span>
              </p>
            </div>
          </div>
          <div className="actions">
            <button type="button" className="primary" onClick={() => start(r.id)}><Pencil size={18} aria-hidden="true" />Practice with this resume</button>
            <button type="button" onClick={() => onCheck(r.id, r.fileName)}><ListMagnifyingGlass size={18} aria-hidden="true" />Role skills check</button>
            <Link to={`/resumes/${r.id}/progress`} className="btn"><ChartLineUp size={18} aria-hidden="true" />Progress</Link>
            <button type="button" onClick={() => onOpen(r.id)}><FolderOpen size={18} aria-hidden="true" />Open</button>
            <button type="button" className="danger" onClick={() => setConfirm(r.id)}><Trash size={18} aria-hidden="true" />Delete</button>
          </div>
          {confirm === r.id && (
            <div role="alert" className="alert" style={{ flexWrap: 'wrap', alignItems: 'center' }}>
              <span>Delete this resume and its questions and scores?</span>
              <button type="button" className="danger small" onClick={() => remove(r.id)}>Delete</button>
              <button type="button" className="small" onClick={() => setConfirm(null)}>Cancel</button>
            </div>
          )}
          <p className="loading" aria-live="polite">{preparing === r.id && <><Spinner />Preparing your questions…</>}</p>
          <Alert>{rowError[r.id]}</Alert>
        </section>
      ))}
    </>
  )
}
