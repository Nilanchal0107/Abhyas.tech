import { useEffect, useState } from 'react'
import { api, download } from './api/client.js'
import { day, modeLabel, statusLabel, total } from './lib/sessions.js'
import { Alert, Chip, Loading } from './ui.jsx'
import { ArrowRight, CheckCircle, DownloadSimple, HourglassMedium, XCircle } from '@phosphor-icons/react'

const STATUS_ICON = { success: CheckCircle, warning: HourglassMedium, error: XCircle }

// W10: every practice session, newest first. A completed row opens its report and can be exported; an unfinished one can be continued.
export default function History({ onReport, onContinue }) {
  const [sessions, setSessions] = useState(null)
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(null)

  useEffect(() => { api('/sessions').then((r) => setSessions(r.sessions)).catch((e) => setError(e.message)) }, [])

  async function exportJson(s) {
    setBusy(s.id); setError('')
    try { await download(`/sessions/${s.id}/export`) } catch (e) { setError(e.message) } finally { setBusy(null) }
  }

  return (
    <>
      <div className="page-head">
        <span className="eyebrow">Record</span>
        <h1>History</h1>
      </div>
      <Alert>{error}</Alert>
      {!sessions && !error && <Loading />}
      {sessions?.length === 0 && <p className="muted">No practice sessions yet. Start one from Home.</p>}
      {sessions?.length > 0 && (
        <div className="table-scroll">
          <table>
            <thead>
              <tr><th scope="col">Date</th><th scope="col">Resume</th><th scope="col">Role</th><th scope="col">Mode</th><th scope="col">Status</th><th scope="col">Total</th><th scope="col" className="sticky-end">Action</th></tr>
            </thead>
            <tbody>
              {sessions.map((s) => {
                const done = s.status === 'completed'
                return (
                  <tr key={s.id} onClick={done ? () => onReport(s.id) : undefined} style={done ? { cursor: 'pointer' } : undefined}>
                    <th scope="row">{done ? <button type="button" className="small" onClick={() => onReport(s.id)} aria-label={`Open report of ${day(s.startedAt)}`}>{day(s.startedAt)}</button> : day(s.startedAt)}</th>
                    <td>{s.resume.fileName}</td>
                    <td>{s.role.name}</td>
                    <td>{modeLabel(s)}</td>
                    <td>{(() => { const st = statusLabel(s); return <Chip kind={st.kind} Icon={STATUS_ICON[st.kind]}>{st.label}</Chip> })()}</td>
                    <td className="num">{total(s)}</td>
                    <td className="sticky-end">
                      {done && <button type="button" className="small" disabled={busy === s.id} onClick={(e) => { e.stopPropagation(); exportJson(s) }}><DownloadSimple size={18} aria-hidden="true" />Export JSON</button>}
                      {s.status === 'in_progress' && <button type="button" className="primary small" onClick={() => onContinue(s.id)}>Continue<ArrowRight size={16} aria-hidden="true" /></button>}
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      )}
    </>
  )
}
