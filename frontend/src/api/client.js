// Same-origin fetch wrapper. Turns a Problem Details body into an Error with `type` and `detail`
// (api-contracts 1.1). The 401 redirect to /login arrives with ticket 02.
export async function api(path, options = {}) {
  const res = await fetch(`/api${path}`, { credentials: 'same-origin', ...options })
  if (res.ok) return res.status === 204 ? null : res.json()
  const problem = await res.json().catch(() => ({}))
  const err = new Error(problem.detail || 'Something went wrong. Please try again.')
  err.type = problem.type || 'server-error'
  err.status = res.status
  throw err
}
