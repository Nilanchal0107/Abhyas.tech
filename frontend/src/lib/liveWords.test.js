// Run: node --test frontend/src/lib/liveWords.test.js
import test from 'node:test'
import assert from 'node:assert/strict'
import { fillerCount, pace, wordCount } from './liveWords.js'

test('counts words like VoiceStats.java', () => {
  assert.equal(wordCount("I didn't use Spring-Boot, 2 times"), 6)
  assert.equal(wordCount(''), 0)
})

test('counts whole-token fillers only', () => {
  assert.equal(fillerCount('Um, I mean, it was uhh fine, you know'), 4)
  assert.equal(fillerCount('an umbrella and an error'), 0)
})

test('pace is words per minute and 0 before any time has passed', () => {
  assert.equal(pace('one two three four five six', 3), 120)
  assert.equal(pace('one two', 0), 0)
})
