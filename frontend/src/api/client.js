// Same-origin fetch wrapper. Turns a Problem Details body into an Error with `type`, `detail`, and for
// validation `errors` (api-contracts 1.1). A 401 from anywhere except the login and signup forms means the
// login expired: the handler set with onExpired sends the user to /login and back.
let expiredHandler = () => {}
export function onExpired(fn) { expiredHandler = fn }

const FORM_PATHS = ['/auth/login', '/auth/signup']

export async function api(path, options = {}) {
  const res = await fetch(`/api${path}`, { credentials: 'same-origin', ...options })
  if (res.ok) return res.status === 204 ? null : res.json()
  const problem = await res.json().catch(() => ({}))
  const err = new Error(problem.detail || 'Something went wrong. Please try again.')
  err.type = problem.type || 'server-error'
  err.status = res.status
  err.errors = problem.errors ?? []
  if (res.status === 401 && !FORM_PATHS.includes(path)) expiredHandler()
  throw err
}

export const postJson = (path, body) =>
  api(path, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) })
