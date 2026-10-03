import { describe, expect, it } from 'vitest'
import { statusLabel, total } from './sessions.js'

describe('statusLabel', () => {
  it('pairs an icon with a word, and says when the voice part was skipped', () => {
    expect(statusLabel({ status: 'completed', voiceSkipped: false })).toBe('✓ Completed')
    expect(statusLabel({ status: 'completed', voiceSkipped: true })).toBe('✓ Completed, voice part skipped')
    expect(statusLabel({ status: 'in_progress' })).toBe('! In progress')
    expect(statusLabel({ status: 'abandoned' })).toBe('✗ Abandoned')
  })
})

describe('total', () => {
  it('shows marks out of the maximum, or a dash without marks', () => {
    expect(total({ totalMarks: 61.5, maxMarks: 80 })).toBe('61.5 / 80')
    expect(total({ totalMarks: 44, maxMarks: 50 })).toBe('44 / 50')
    expect(total({ totalMarks: null, maxMarks: 80 })).toBe('-')
  })
})
