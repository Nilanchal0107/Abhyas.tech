import { useId, useState } from 'react'
import { readLinks } from './lib/pdfLinks.js'
import { api } from './api/client.js'
import RoleSearch from './RoleSearch.jsx'
import Review from './Review.jsx'
import { checkFile, looksIncomplete, lowQuality, notAResume, toParsedResume } from './lib/resume.js'

// W4: file + target role + optional interview date. The PDF is parsed in the browser first.
// A parse that looks incomplete is retried on the backend (PDFBox + AI, trd 7.1). The Review form (W6) takes over once there is a result; the upload form stays mounted but hidden so Back keeps the file.
// Sends the PDF to the backend. If AI is unavailable the user gets an empty form and an inline message.
async function fallback(file, browserParsed) {
  const body = new FormData()
  body.append('file', file)
  try {
    return { parsed: (await api('/resumes/parse-fallback', { method: 'POST', body })).resume, byAi: true }
  } catch (e) {
    if (e.type !== 'ai-unavailable') return { parsed: browserParsed, incomplete: true, error: e.message }
    return { parsed: emptyResume, unavailable: true }
  }
}

// a new token remounts the Review form, so a fresh parse replaces what the user was editing
const stamp = (r) => ({ ...r, token: crypto.randomUUID() })

const emptyResume = {
  profile: { name: null, email: null, phone: null, location: null, links: [], summary: null },
  education: [], experience: [], projects: [], skills: [],
}

export default function Upload() {
  const [file, setFile] = useState(null)
  const [role, setRole] = useState(null)
  const [date, setDate] = useState('')
  const [errors, setErrors] = useState({})
  const [reading, setReading] = useState(false)
  const [result, setResult] = useState(null) // { parsed, incomplete, byAi }
  const [saved, setSaved] = useState(null)
  const fileId = useId()
  const dateId = useId()

  async function pickFile(f) {
    setResult(null)
    const msg = f ? await checkFile(f) : null
    setFile(msg ? null : f)
    setErrors((e) => ({ ...e, file: msg, parse: null }))
  }

  async function onContinue() {
    const next = {}
    if (!file) next.file = 'Please choose a PDF file.'
    if (!role) next.role = 'Choose a target role to continue.'
    setErrors(next)
    if (next.file || next.role) return
    setReading(true)
    setResult(null)
    try {
      const { parseResumeFromPdf } = await import('./lib/openresume/index.ts') // keeps pdf.js out of the first load
      const url = URL.createObjectURL(file)
      try {
        const links = await readLinks(url).catch(() => []) // links are a bonus; never block the parse
        const parsed = toParsedResume(await parseResumeFromPdf(url), links)
        if (notAResume(parsed)) setErrors({ parse: 'This PDF does not look like a resume. Please choose your resume.' })
        else if (looksIncomplete(parsed) || lowQuality(parsed)) setResult(stamp(await fallback(file, parsed)))
        else setResult(stamp({ parsed, incomplete: false }))
      } finally {
        URL.revokeObjectURL(url)
      }
    } catch {
      setErrors({ parse: 'We could not read this PDF. Try another file.' })
    } finally {
      setReading(false)
    }
  }

  // "Scan with AI" is always offered once a preview is shown, even when the browser parse looks fine.
  async function scanWithAi() {
    setReading(true)
    try { setResult(stamp(await fallback(file, result.parsed))) } finally { setReading(false) }
  }

  if (saved) {
    return (
      <>
        <h1>Resume saved</h1>
        <p>{saved.fileName} for {saved.targetRole.name} was saved with {saved.pointCount} resume points.</p>
        <button type="button" onClick={() => location.reload()}>Upload another resume</button>
      </>
    )
  }

  const p = result?.parsed
  return (
    <>
      {p && (
        <>
          <button type="button" onClick={scanWithAi} disabled={reading}>Scan with AI</button>
          <p aria-live="polite">{reading ? 'Reading your resume...' : ''}</p>
          {result.error && <p>! This looks incomplete and the extra check failed: {result.error}</p>}
          <Review key={result.token} initial={p} byAi={result.byAi} unavailable={result.unavailable} fileName={file.name}
            roleId={role.id} interviewDate={date || null} parseSource={result.byAi ? 'ai_fallback' : 'openresume'}
            onBack={() => setResult(null)} onSaved={setSaved} />
        </>
      )}
      <div hidden={!!p}>
      <h1>Upload</h1>
      <p>Step 1 of 3: Upload</p>
      <div>
        <label htmlFor={fileId}>Resume (PDF)</label>
        <input id={fileId} type="file" accept="application/pdf" aria-describedby={`${fileId}-err`}
          onChange={(e) => pickFile(e.target.files[0] ?? null)} />
        <p id={`${fileId}-err`} className="error" role="alert">{errors.file ? `! Error: ${errors.file}` : ''}</p>
      </div>
      <RoleSearch onPick={(r) => { setRole(r); setErrors((e) => ({ ...e, role: null })) }} />
      <p className="error" role="alert">{errors.role ? `! Error: ${errors.role}` : ''}</p>
      <div>
        <label htmlFor={dateId}>Interview date (optional)</label>
        <input id={dateId} type="date" value={date} onChange={(e) => setDate(e.target.value)} />
      </div>
      <button type="button" onClick={onContinue} disabled={reading}>Continue</button>
      <p aria-live="polite" style={{ minHeight: '1.5em' }}>{reading ? 'Reading your resume...' : ''}</p>
      <p className="error" role="alert">{errors.parse ? `! Error: ${errors.parse}` : ''}</p>
      </div>
    </>
  )
}
