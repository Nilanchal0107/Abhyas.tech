import { useCallback, useEffect, useState } from 'react'
import { Link, Navigate, NavLink, Outlet, Route, Routes, useLocation, useNavigate, useParams, useSearchParams } from 'react-router'
import { api, onExpired, postJson } from './api/client.js'
import Account from './Account.jsx'
import Login from './Login.jsx'
import Practice from './Practice.jsx'
import Report from './Report.jsx'
import ResumeList from './ResumeList.jsx'
import Upload from './Upload.jsx'
import History from './History.jsx'
import Home from './Home.jsx'
import Landing from './Landing.jsx'
import Stories from './Stories.jsx'

const NAV = [['Practice', '/practice'], ['Resumes', '/resumes'], ['Stories', '/stories'], ['History', '/history'], ['Account', '/account']]

// Only an in-app path may be returned to (never another site).
const safeNext = (n) => (n && n.startsWith('/') && !n.startsWith('//') && !n.startsWith('/login') ? n : '/practice')

// W0 page shell: top bar plus the routed screen.
function Shell({ onLogOut }) {
  const [asking, setAsking] = useState(false) // inline "are you sure", like delete (UI-UX 6.3)
  return (
    <>
      <a className="skip" href="#content">Skip to content</a>
      <header className="topbar">
        <Link to="/" className="brand"><strong>Abhyas.tech</strong></Link>
        <nav aria-label="Main">
          {NAV.map(([label, to]) => <NavLink key={label} to={to} className="nav-item">{label}</NavLink>)}
        </nav>
        {asking ? (
          <span role="group" aria-label="Confirm log out">
            Log out of Abhyas.tech?{' '}
            <button type="button" onClick={onLogOut}>Yes, log out</button>{' '}
            <button type="button" autoFocus onClick={() => setAsking(false)}>Cancel</button>
          </span>
        ) : (
          <button type="button" onClick={() => setAsking(true)}>Log out</button>
        )}
      </header>
      <main id="content" className="column"><Outlet /></main>
    </>
  )
}

// A signed-out visitor goes to /login and comes back to the page they asked for.
function ToLogin() {
  const { pathname, search } = useLocation()
  return <Navigate to={`/login?next=${encodeURIComponent(pathname + search)}`} replace />
}

export default function App() {
  const [me, setMe] = useState({ state: 'loading' })
  const [storyPrefill, setStoryPrefill] = useState(null) // { action, sourceAttemptId } from "Save as story" in a report
  const clearPrefill = useCallback(() => setStoryPrefill(null), [])
  const navigate = useNavigate()
  const [params] = useSearchParams()

  // An expired login (401 from any endpoint) shows the login form; ToLogin remembers where the user was.
  useEffect(() => {
    onExpired(() => setMe({ state: 'out', notice: 'Please log in to continue.' }))
  }, [])

  useEffect(() => {
    api('/me')
      .then((user) => setMe({ state: 'in', user }))
      .catch((err) => {
        if (err.type === 'unauthorized') return // the expired handler already set the signed-out state
        setMe({ state: 'error', err })
      })
  }, [])

  async function logOut() {
    await postJson('/auth/logout').catch(() => {}) // repeating logout is fine; the cookie is cleared either way
    setMe({ state: 'out' })
    navigate('/login', { replace: true })
  }

  function deleted() {
    setMe({ state: 'out', notice: 'Your account was deleted.' })
    navigate('/login', { replace: true })
  }

  if (me.state === 'loading') return <main id="content" className="column"><p>Checking who is signed in…</p></main>
  if (me.state === 'error') return <main id="content" className="column"><p role="alert">! Error: {me.err.detail ?? me.err.message}</p></main>

  if (me.state === 'out') {
    return (
      <Routes>
        <Route path="/" element={<Landing signedIn={false} />} />
        <Route path="/login" element={<Login notice={me.notice} onDone={(user) => { setMe({ state: 'in', user }); navigate(safeNext(params.get('next')), { replace: true }) }} />} />
        <Route path="*" element={<ToLogin />} />
      </Routes>
    )
  }

  // The practice screen has no top bar (W8), so it sits outside the Shell.
  return (
    <Routes>
      <Route path="/" element={<Landing signedIn />} />
      <Route path="/login" element={<Navigate to={safeNext(params.get('next'))} replace />} />
      <Route path="/practice/:id" element={<PracticeRoute />} />
      <Route element={<Shell onLogOut={logOut} />}>
        <Route path="practice" element={<HomeRoute user={me.user} />} />
        <Route path="resumes" element={<ResumesRoute />} />
        <Route path="resumes/new" element={<Upload />} />
        <Route path="stories" element={<Stories prefill={storyPrefill} onPrefillUsed={clearPrefill} />} />
        <Route path="history" element={<HistoryRoute />} />
        <Route path="account" element={<Account user={me.user} onDeleted={deleted} />} />
        <Route path="sessions/:id/report" element={<ReportRoute onSaveStory={(prefill) => { setStoryPrefill(prefill); navigate('/stories') }} />} />
      </Route>
      <Route path="*" element={<Navigate to="/practice" replace />} />
    </Routes>
  )
}

function useGoPractice() {
  const navigate = useNavigate()
  return (id) => navigate(`/practice/${id}`)
}

function HomeRoute({ user }) {
  const navigate = useNavigate()
  return <Home user={user} onPractice={useGoPractice()} onReport={(id) => navigate(`/sessions/${id}/report`)} onUpload={() => navigate('/resumes/new')} />
}

function ResumesRoute() {
  const navigate = useNavigate()
  return <ResumeList onUpload={() => navigate('/resumes/new')} onPractice={useGoPractice()} />
}

function HistoryRoute() {
  const navigate = useNavigate()
  return <History onReport={(id) => navigate(`/sessions/${id}/report`)} onContinue={useGoPractice()} />
}

function ReportRoute({ onSaveStory }) {
  const navigate = useNavigate()
  return <Report id={useParams().id} onBack={() => navigate('/practice')} onSaveStory={onSaveStory} />
}

function PracticeRoute() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { key } = useLocation()
  // Leaving goes back to where the user came from; a pasted link has no earlier page in the app, so go to the practice screen.
  const exit = () => (key === 'default' ? navigate('/practice', { replace: true }) : navigate(-1))
  return <main id="content" className="column"><Practice id={id} onExit={exit} onSubmitted={(sid) => navigate(`/sessions/${sid}/report`, { replace: true })} /></main>
}
