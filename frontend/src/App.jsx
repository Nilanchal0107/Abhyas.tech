import { useEffect, useState } from 'react'
import { api, onExpired, postJson } from './api/client.js'
import Login from './Login.jsx'

const NAV = ['Practice', 'Resumes', 'Stories', 'History', 'Account']

// Only an in-app path may be returned to (never another site).
const safeNext = (n) => (n && n.startsWith('/') && !n.startsWith('//') && !n.startsWith('/login') ? n : '/')

// W0 page shell. Nav links stay inert until their screens exist.
export default function App() {
  const [me, setMe] = useState({ state: 'loading' })

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

  if (me.state === 'out') return <Login notice={me.notice} onDone={signedIn} />

  return (
    <>
      <a className="skip" href="#content">Skip to content</a>
      {me.state === 'in' && (
        <header className="topbar">
          <strong>Abhyas.tech</strong>
          <nav aria-label="Main">
            {NAV.map((label, i) => (
              <span key={label} className="nav-item" aria-current={i === 0 ? 'page' : undefined}>{label}</span>
            ))}
          </nav>
          <button type="button" onClick={logOut}>Log out</button>
        </header>
      )}
      <main id="content" className="column">
        <h1>Practice</h1>
        {me.state === 'loading' && <p>Checking who is signed in…</p>}
        {me.state === 'in' && <p>Signed in as {me.user.loginId}.</p>}
        {me.state === 'error' && <p role="alert">! Error: {me.err.detail ?? me.err.message}</p>}
      </main>
    </>
  )
}
