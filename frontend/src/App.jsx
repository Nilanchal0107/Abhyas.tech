import { useCallback, useEffect, useState } from 'react'
import { api, onExpired, postJson } from './api/client.js'
import Account from './Account.jsx'
import Login from './Login.jsx'
import Practice from './Practice.jsx'
import Report from './Report.jsx'
import ResumeList from './ResumeList.jsx'
import Upload from './Upload.jsx'
import History from './History.jsx'
import Home from './Home.jsx'
import Stories from './Stories.jsx'

const NAV = ['Practice', 'Resumes', 'Stories', 'History', 'Account']

// Only an in-app path may be returned to (never another site).
const safeNext = (n) => (n && n.startsWith('/') && !n.startsWith('//') && !n.startsWith('/login') ? n : '/')

// W0 page shell.
export default function App() {
  const [me, setMe] = useState({ state: 'loading' })
  const [view, setView] = useState('Practice')
  const [uploading, setUploading] = useState(false)
  const [practicing, setPracticing] = useState(null) // session id: the practice screen has no top bar (W8)
  const [reporting, setReporting] = useState(null) // session id of the report being shown (W9)
  const [storyPrefill, setStoryPrefill] = useState(null) // { action, sourceAttemptId } from "Save as story" in a report
  const clearPrefill = useCallback(() => setStoryPrefill(null), [])

  // An expired login (401 from any endpoint) goes to /login and remembers where the user was.
  useEffect(() => {
    onExpired(() => {
      const here = location.pathname + location.search
      if (!location.pathname.startsWith('/login')) history.replaceState(null, '', `/login?next=${encodeURIComponent(here)}`)
      setMe({ state: 'out', notice: 'Please log in to continue.' })
    })
  }, [])

  useEffect(() => {
    api('/me')
      .then((user) => setMe({ state: 'in', user }))
      .catch((err) => {
        if (err.type === 'unauthorized') return // the expired handler already moved us to /login, quietly
        setMe({ state: 'error', err })
      })
  }, [])

  function signedIn(user) {
    history.replaceState(null, '', safeNext(new URLSearchParams(location.search).get('next')))
    setMe({ state: 'in', user })
  }

  async function logOut() {
    await postJson('/auth/logout').catch(() => {}) // repeating logout is fine; the cookie is cleared either way
    history.replaceState(null, '', '/login')
    setMe({ state: 'out' })
  }

  function deleted() {
    history.replaceState(null, '', '/login')
    setView('Practice')
    setMe({ state: 'out', notice: 'Your account was deleted.' })
  }

  if (me.state === 'out') return <Login notice={me.notice} onDone={signedIn} />
  if (me.state === 'in' && practicing) {
    return <main id="content" className="column"><Practice id={practicing} onExit={() => setPracticing(null)} onSubmitted={(id) => { setPracticing(null); setReporting(id) }} /></main>
  }

  return (
    <>
      <a className="skip" href="#content">Skip to content</a>
      {me.state === 'in' && (
        <header className="topbar">
          <strong>Abhyas.tech</strong>
          <nav aria-label="Main">
            {NAV.map((label) => (
              <button key={label} type="button" className="nav-item" aria-current={view === label ? 'page' : undefined}
                onClick={() => { setView(label); setUploading(false); setReporting(null) }}>{label}</button>
            ))}
          </nav>
          <button type="button" onClick={logOut}>Log out</button>
        </header>
      )}
      <main id="content" className="column">
        {me.state === 'in' && reporting && (
          <Report id={reporting} onBack={() => { setReporting(null); setView('Practice') }}
            onSaveStory={(prefill) => { setStoryPrefill(prefill); setReporting(null); setView('Stories') }} />
        )}
        {me.state === 'in' && !reporting && view === 'Account' && <Account user={me.user} onDeleted={deleted} />}
        {me.state === 'in' && !reporting && view === 'Resumes' && (uploading ? <Upload /> : <ResumeList onUpload={() => setUploading(true)} onPractice={setPracticing} />)}
        {me.state === 'in' && !reporting && view === 'Stories' && <Stories prefill={storyPrefill} onPrefillUsed={clearPrefill} />}
        {me.state === 'in' && !reporting && view === 'History' && <History onReport={setReporting} onContinue={setPracticing} />}
        {me.state === 'in' && !reporting && view === 'Practice' && (
          <Home user={me.user} onPractice={setPracticing} onReport={setReporting} onUpload={() => { setView('Resumes'); setUploading(true) }} />
        )}
        {me.state === 'loading' && <p>Checking who is signed in…</p>}
        {me.state === 'error' && <p role="alert">! Error: {me.err.detail ?? me.err.message}</p>}
      </main>
    </>
  )
}
