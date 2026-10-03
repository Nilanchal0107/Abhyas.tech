import { afterEach, expect, test, vi } from 'vitest'
import { api, onExpired } from './client.js'

const reply = (status, body) => vi.stubGlobal('fetch', vi.fn(async () => new Response(JSON.stringify(body), { status })))
afterEach(() => vi.unstubAllGlobals())

test('a 401 from a normal endpoint calls the expired handler', async () => {
  const expired = vi.fn()
  onExpired(expired)
  reply(401, { type: 'unauthorized', detail: 'Please log in.' })
  await expect(api('/resumes')).rejects.toMatchObject({ status: 401, type: 'unauthorized' })
  expect(expired).toHaveBeenCalledOnce()
})

test('a 401 from login or signup is a form error, not a redirect', async () => {
  const expired = vi.fn()
  onExpired(expired)
  reply(401, { type: 'unauthorized', detail: 'That user ID or password is not right.' })
  await expect(api('/auth/login', { method: 'POST' })).rejects.toThrow('That user ID or password is not right.')
  expect(expired).not.toHaveBeenCalled()
})

test('validation errors are kept for inline messages', async () => {
  reply(400, { type: 'validation', detail: 'x', errors: [{ field: 'password', message: 'Use at least 8 characters.' }] })
  await expect(api('/auth/signup', { method: 'POST' })).rejects.toMatchObject({
    errors: [{ field: 'password', message: 'Use at least 8 characters.' }],
  })
})
