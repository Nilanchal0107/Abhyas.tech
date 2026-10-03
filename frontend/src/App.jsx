import { useEffect, useState } from 'react'
import { api } from './api/client.js'

const NAV = ['Practice', 'Resumes', 'Stories', 'History', 'Account']

// W0 page shell. Nav links stay inert until their screens exist.
export default function App() {
  const [me, setMe] = useState({ state: 'loading' })

  useEffect(() => {
    api('/me')
      .then((data) => setMe({ state: 'in', data }))
      .catch((err) => setMe({ state: err.type === 'unauthorized' ? 'out' : 'error', err }))
  }, [])

  return (
    <>
      <a className="skip" href="#content">Skip to content</a>
      <header className="topbar">
        <strong>Abhyas.tech</strong>
        <nav aria-label="Main">
          {NAV.map((label, i) => (
            <span key={label} className="nav-item" aria-current={i === 0 ? 'page' : undefined}>{label}</span>
          ))}
        </nav>
      </header>
      <main id="content" className="column">
        <h1>Practice</h1>
        {me.state === 'loading' && <p>Checking who is signed in…</p>}
        {me.state === 'out' && <p>Not signed in. Login arrives in the next step.</p>}
        {me.state === 'in' && <p>Signed in.</p>}
        {me.state === 'error' && <p role="alert">! Error: {me.err.detail ?? me.err.message}</p>}
      </main>
    </>
  )
}
