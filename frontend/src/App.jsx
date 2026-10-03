import { useEffect, useState } from 'react'
import { api, onExpired, postJson } from './api/client.js'
import Account from './Account.jsx'
import Login from './Login.jsx'
import RoleSearch from './RoleSearch.jsx'

const NAV = ['Practice', 'Resumes', 'Stories', 'History', 'Account']

// Only an in-app path may be returned to (never another site).
const safeNext = (n) => (n && n.startsWith('/') && !n.startsWith('//') && !n.startsWith('/login') ? n : '/')

// W0 page shell. Nav links stay inert until their screens exist.
export default function App() {
  const [me, setMe] = useState({ state: 'loading' })
  const [view, setView] = useState('Practice')

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

  return (
    <>
      <a className="skip" href="#content">Skip to content</a>
      {me.state === 'in' && (
        <header className="topbar">
          <strong>Abhyas.tech</strong>
          <nav aria-label="Main">
            {NAV.map((label) =>
              label === 'Account' || label === 'Resumes' ? (
                <button key={label} type="button" className="nav-item" aria-current={view === label ? 'page' : undefined}
                  onClick={() => setView(label)}>{label}</button>
              ) : (
                <span key={label} className="nav-item" aria-current={view === label ? 'page' : undefined}>{label}</span>
              ),
            )}
          </nav>
          <button type="button" onClick={logOut}>Log out</button>
        </header>
      )}
      <main id="content" className="column">
        {me.state === 'in' && view === 'Account' && <Account user={me.user} onDeleted={deleted} />}
        {me.state === 'in' && view === 'Resumes' && (
          <>
            <h1>Upload</h1>
            {/* ticket 07 adds the file field and the rest of W4 around the role field */}
            <RoleSearch onPick={() => {}} />
          </>
        )}
        {me.state === 'in' && view !== 'Account' && view !== 'Resumes' && (
          <>
            <h1>Practice</h1>
            <p>Signed in as {me.user.loginId}.</p>
          </>
        )}
        {me.state === 'loading' && <p>Checking who is signed in…</p>}
        {me.state === 'error' && <p role="alert">! Error: {me.err.detail ?? me.err.message}</p>}
      </main>
    </>
  )
}
