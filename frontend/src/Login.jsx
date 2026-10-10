import { useId, useState } from 'react'
import { Link, useSearchParams } from 'react-router'
import { postJson } from './api/client.js'
import { XCircle, Eye, EyeSlash } from '@phosphor-icons/react'
import { Alert, Brand } from './ui.jsx'

// W1: log in / sign up. Errors show inline under the field or button that failed (UI-UX 7.1).
export default function Login({ notice, onDone }) {
  const [mode, setMode] = useState(useSearchParams()[0].get('mode') === 'signup' ? 'signup' : 'login')
  const [loginId, setLoginId] = useState('')
  const [password, setPassword] = useState('')
  const [confirm, setConfirm] = useState('')
  const [fieldErrors, setFieldErrors] = useState({})
  const [formError, setFormError] = useState(null)
  const [busy, setBusy] = useState(false)
  const [show, setShow] = useState(false)
  const id = useId()
  const signup = mode === 'signup'

  function switchTo(next) {
    setMode(next)
    setFieldErrors({})
    setFormError(null)
  }

  async function submit(e) {
    e.preventDefault()
    setFormError(null)
    if (signup && password !== confirm) {
      setFieldErrors({ confirm: 'The passwords do not match.' })
      return
    }
    setFieldErrors({})
    setBusy(true)
    try {
      const data = await postJson(signup ? '/auth/signup' : '/auth/login', { loginId, password })
      onDone(data.user)
    } catch (err) {
      if (err.errors?.length) {
        setFieldErrors(Object.fromEntries(err.errors.map((x) => [x.field, x.message])))
      } else if (err.type === 'login-taken') {
        setFieldErrors({ loginId: err.message })
      } else {
        setFormError(err.message)
      }
      setBusy(false)
    }
  }

  const field = (key, label, value, set, props) => (
    <div className="field">
      <label htmlFor={`${id}-${key}`}>{label}</label>
      <input
        id={`${id}-${key}`}
        value={value}
        onChange={(e) => set(e.target.value)}
        aria-invalid={fieldErrors[key] ? true : undefined}
        aria-describedby={fieldErrors[key] ? `${id}-${key}-err` : undefined}
        required
        {...props}
      />
      {fieldErrors[key] && <p id={`${id}-${key}-err`} className="error"><XCircle size={18} weight="fill" aria-hidden="true" />{fieldErrors[key]}</p>}
    </div>
  )

  return (
    <div className="login-page">
    <main id="content" className="login">
      <h1><Link to="/" className="brand" aria-label="Abhyas.tech home"><Brand /></Link></h1>
      <p className="tagline">{signup ? 'Make an account and bring your resume.' : 'Welcome back. Pick up where you left off.'}</p>
      {notice && <p role="status" className="notice">{notice}</p>}
      <div className="switch" role="group" aria-label="Log in or sign up">
        <button type="button" aria-pressed={!signup} onClick={() => switchTo('login')}>Log in</button>
        <button type="button" aria-pressed={signup} onClick={() => switchTo('signup')}>Sign up</button>
      </div>
      <form onSubmit={submit} noValidate>
        {field('loginId', 'User ID', loginId, setLoginId, { autoComplete: 'username', autoCapitalize: 'none', spellCheck: false })}
        {field('password', 'Password', password, setPassword, {
          type: show ? 'text' : 'password',
          autoComplete: signup ? 'new-password' : 'current-password',
        })}
        {signup && field('confirm', 'Confirm password', confirm, setConfirm, { type: show ? 'text' : 'password', autoComplete: 'new-password' })}
        <div className="field pw-row">
          <label>
            <input type="checkbox" checked={show} onChange={(e) => setShow(e.target.checked)} />
            {show ? <EyeSlash size={20} aria-hidden="true" /> : <Eye size={20} aria-hidden="true" />} Show password
          </label>
        </div>
        <Alert>{formError}</Alert>
        <button type="submit" disabled={busy}>{signup ? 'Create account' : 'Log in'}</button>
      </form>
    </main>
    </div>
  )
}
