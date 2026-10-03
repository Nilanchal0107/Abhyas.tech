import { describe, expect, it } from 'vitest'
import { clock, elapsedSeconds, MAX_SECONDS, shouldStop } from './recorder.js'

describe('recorder timing', () => {
  it('counts whole seconds and never goes negative', () => {
    expect(elapsedSeconds(1000, 1999)).toBe(0)
    expect(elapsedSeconds(1000, 43_500)).toBe(42)
    expect(elapsedSeconds(5000, 1000)).toBe(0)
  })

  it('stops by itself at 3 minutes', () => {
    expect(MAX_SECONDS).toBe(180)
    expect(shouldStop(179)).toBe(false)
    expect(shouldStop(180)).toBe(true)
    expect(shouldStop(181)).toBe(true)
  })

  it('shows minutes and seconds', () => {
    expect(clock(0)).toBe('00:00')
    expect(clock(42)).toBe('00:42')
    expect(clock(180)).toBe('03:00')
  })
})
