import { useCallback, useEffect, useState } from 'react'
import { Link, Navigate, NavLink, Outlet, Route, Routes, useLocation, useNavigate, useParams, useSearchParams } from 'react-router'
import { api, onExpired, postJson } from './api/client.js'
import Account, { ConfirmDelete } from './Account.jsx'
import Login from './Login.jsx'
import Practice from './Practice.jsx'
import Report from './Report.jsx'
import ResumeList from './ResumeList.jsx'
import Upload from './Upload.jsx'
import History from './History.jsx'
import Home from './Home.jsx'
import Landing from './Landing.jsx'
import { StoryForm, StoryList } from './Stories.jsx'
import { Open } from './ResumeList.jsx'
import RoleSkills from './RoleSkills.jsx'
import Progress from './Progress.jsx'
import { Brand, Alert, Loading } from './ui.jsx'
import { BookOpenText, ClockCounterClockwise, FileText, Pencil, SignOut, UserCircle } from '@phosphor-icons/react'

const NAV = [['Practice', '/practice', Pencil], ['Resumes', '/resumes', FileText], ['Stories', '/stories', BookOpenText], ['History', '/history', ClockCounterClockwise], ['Account', '/account', UserCircle]]

// Only an in-app path may be returned to (never another site).
const safeNext = (n) => (n && n.startsWith('/') && !n.startsWith('//') && !n.startsWith('/login') ? n : '/practice')

// W0 page shell: top bar plus the routed screen.
function Shell({ onLogOut }) {
  const [asking, setAsking] = useState(false) // inline "are you sure", like delete (UI-UX 6.3)
  return (
    <>
      <a className="skip" href="#content">Skip to content</a>
      <header className="topbar">
        <Link to="/" className="brand" aria-label="Abhyas.tech home"><Brand /></Link>
        <nav aria-label="Main">
          {NAV.map(([label, to, Icon]) => <NavLink key={label} to={to} className="nav-item"><Icon size={20} aria-hidden="true" />{label}</NavLink>)}
        </nav>
        {asking ? (
          <span role="group" aria-label="Confirm log out">
            Log out of Abhyas.tech?
            <button type="button" className="small" onClick={onLogOut}>Yes, log out</button>
            <button type="button" className="small" autoFocus onClick={() => setAsking(false)}>Cancel</button>
          </span>
        ) : (
          <button type="button" className="small" onClick={() => setAsking(true)}><SignOut size={18} aria-hidden="true" />Log out</button>
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

  if (me.state === 'loading') return <main id="content" className="column"><Loading>Checking who is signed in…</Loading></main>
  if (me.state === 'error') return <main id="content" className="column"><Alert>{me.err.detail ?? me.err.message}</Alert></main>

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
        <Route path="resumes/:id" element={<OpenRoute />} />
        <Route path="resumes/:id/role-skills" element={<RoleSkillsRoute />} />
        <Route path="resumes/:id/progress" element={<ProgressRoute />} />
        <Route path="stories" element={<StoriesRoute />} />
        <Route path="stories/new" element={<StoryFormRoute prefill={storyPrefill} onPrefillUsed={clearPrefill} />} />
        <Route path="stories/:id/edit" element={<StoryFormRoute />} />
        <Route path="history" element={<HistoryRoute />} />
        <Route path="account" element={<AccountRoute user={me.user} />} />
        <Route path="account/delete" element={<DeleteRoute onDeleted={deleted} />} />
        <Route path="sessions/:id/report" element={<ReportRoute onSaveStory={(prefill) => { setStoryPrefill(prefill); navigate('/stories/new') }} />} />
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
  return <ResumeList onUpload={() => navigate('/resumes/new')} onPractice={useGoPractice()} onOpen={(id) => navigate(`/resumes/${id}`)}
    onCheck={(id, fileName) => navigate(`/resumes/${id}/role-skills`, { state: { fileName } })} />
}

function OpenRoute() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [params] = useSearchParams()
  // `point` comes from the role skills check: scroll to that resume point
  return <Open key={id} id={id} pointId={Number(params.get('point')) || null} onBack={() => navigate('/resumes')} onChanged={() => {}} />
}

function RoleSkillsRoute() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { state } = useLocation()
  return <RoleSkills key={id} id={id} fileName={state?.fileName} onBack={() => navigate('/resumes')} fixLabel="Upload a new resume"
    onFix={() => navigate('/resumes/new')} onPoint={(p) => navigate(`/resumes/${id}?point=${p}`)} />
}

function ProgressRoute() {
  const { id } = useParams()
  return <Progress key={id} id={id} />
}

function StoriesRoute() {
  const navigate = useNavigate()
  return <StoryList onNew={() => navigate('/stories/new')} onEdit={(id) => navigate(`/stories/${id}/edit`)} />
}

// "Save as story" in a report arrives with a prefill; keep the first one for this form, then clear it from App.
function StoryFormRoute({ prefill, onPrefillUsed }) {
  const { id } = useParams()
  const navigate = useNavigate()
  const [first] = useState(prefill)
  useEffect(() => { if (prefill) onPrefillUsed() }, [prefill, onPrefillUsed])
  return <StoryForm key={id ?? 'new'} id={id} prefill={first} onClose={() => navigate('/stories')} />
}

function AccountRoute({ user }) {
  const navigate = useNavigate()
  return <Account user={user} onDelete={() => navigate('/account/delete')} />
}

function DeleteRoute({ onDeleted }) {
  const navigate = useNavigate()
  return <ConfirmDelete onCancel={() => navigate('/account')} onDeleted={onDeleted} />
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
  return <main id="content" className="column narrow"><Practice id={id} onExit={exit} onSubmitted={(sid) => navigate(`/sessions/${sid}/report`, { replace: true })} /></main>
}
