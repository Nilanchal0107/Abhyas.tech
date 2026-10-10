// Run: node --test frontend/src/lib/richText.test.js
import test from 'node:test'
import assert from 'node:assert/strict'
import { parts } from './richText.js'

test('plain text is one part', () => {
  assert.deepEqual(parts('Why use Redis here?'), [{ kind: 'text', text: 'Why use Redis here?' }])
})

test('inline code and a fenced block are split out, whitespace kept', () => {
  assert.deepEqual(parts('What does `map` return?\n```js\nconst a = [1]\n  .map(x => x)\n```\nExplain.'), [
    { kind: 'text', text: 'What does ' }, { kind: 'code', text: 'map' }, { kind: 'text', text: ' return?\n' },
    { kind: 'block', text: 'const a = [1]\n  .map(x => x)' }, { kind: 'text', text: '\nExplain.' },
  ])
})

test('an unclosed backtick stays text', () => {
  assert.deepEqual(parts('use ` alone'), [{ kind: 'text', text: 'use ` alone' }])
})
