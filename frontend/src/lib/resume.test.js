import { describe, expect, it } from 'vitest'
import { checkFile, limitErrors, looksIncomplete, lowQuality, notAResume, toParsedResume } from './resume.js'
import { labelFor } from './pdfLinks.js'

const raw = (over = {}) => ({
  profile: { name: 'Asha Rao', email: 'a@x.com', phone: '', url: 'github.com/asha', summary: '', location: 'Pune' },
  workExperiences: [{ company: 'Acme', jobTitle: 'Dev', date: '2024', descriptions: ['one', 'two'] }],
  educations: [], projects: [],
  skills: { featuredSkills: [{ skill: 'Java', rating: 4 }, { skill: '', rating: 4 }], descriptions: [] },
  ...over,
})

describe('toParsedResume', () => {
  it('maps fields, uses null for missing and drops empty skills', () => {
    const p = toParsedResume(raw())
    expect(p.profile).toMatchObject({ name: 'Asha Rao', phone: null, summary: null, links: [{ label: null, url: 'github.com/asha' }] })
    expect(p.experience[0]).toEqual({ company: 'Acme', title: 'Dev', date: '2024', bullets: ['one', 'two'] })
    expect(p.skills).toEqual(['Java'])
  })
})

describe('looksIncomplete', () => {
  it('is false for a full resume', () => expect(looksIncomplete(toParsedResume(raw()))).toBe(false))
  it('is true with no name', () =>
    expect(looksIncomplete(toParsedResume(raw({ profile: { name: '' } })))).toBe(true))
  it('is true with neither experience nor projects', () =>
    expect(looksIncomplete(toParsedResume(raw({ workExperiences: [] })))).toBe(true))
  it('is true with fewer than 3 resume points', () =>
    expect(looksIncomplete(toParsedResume(raw({ skills: { featuredSkills: [], descriptions: [] } })))).toBe(true))
})

describe('notAResume', () => {
  const empty = { profile: { name: null }, workExperiences: [], educations: [], projects: [], skills: {} }
  it('is true when nothing resume-like was found', () => expect(notAResume(toParsedResume(empty))).toBe(true))
  it('is false for a badly read resume that still has an email', () =>
    expect(notAResume(toParsedResume({ ...empty, profile: { email: 'a@x.com' } }))).toBe(false))
  it('is false for a full resume', () => expect(notAResume(toParsedResume(raw()))).toBe(false))
})

describe('checkFile', () => {
  const f = (content, size) => {
    const b = new Blob([content])
    return { size: size ?? b.size, slice: (s, e) => b.slice(s, e) }
  }
  it('accepts a PDF header', async () => expect(await checkFile(f('%PDF-1.7 ...'))).toBeNull())
  it('refuses a non-PDF', async () => expect(await checkFile(f('hello world'))).toMatch(/PDF/))
  it('refuses over 1 MB', async () => expect(await checkFile(f('%PDF-', 1024 * 1024 + 1))).toMatch(/1 MB/))
})

// The real resume from the manual check: what OpenResume returned, shortened to the fields that were wrong.
describe('toParsedResume fixes', () => {
  const tech = 'Python, Flask, Groq (Whisper & Llama 3.3), HTML/CSS/JavaScript'
  const degree = 'Bachelor of Engineering (B.E.) in Information Technology | Expected Graduation: 2029'
  const parsed = toParsedResume(
    {
      profile: { name: 'N J', email: 'n@x.com' },
      educations: [{ school: '', degree, date: degree, descriptions: ['A. P. Shah Institute of Technology— Mumbai University', degree, 'CGPA: 8.29 / 10'] }],
      projects: [
        { project: '', date: '', descriptions: ['GitHub ' + tech, 'Built a trainer.'] },
        { project: 'React, Cloud-hosted Backend', date: 'FinHabits — Google Solution Challenge 2026', descriptions: ['Built an app.'] },
      ],
      skills: { featuredSkills: [], descriptions: ['Languages: Java, C, Python', 'Tools: Git, java'] },
    },
    [{ label: 'GitHub', url: 'https://github.com/x' }],
  )
  it('splits skill lines into single skills without repeats', () =>
    expect(parsed.skills).toEqual(['Java', 'C', 'Python', 'Git']))
  it('adds links from the PDF', () => expect(parsed.profile.links).toEqual([{ label: 'GitHub', url: 'https://github.com/x' }]))
  it('fixes education', () =>
    expect(parsed.education[0]).toEqual({
      school: 'A. P. Shah Institute of Technology— Mumbai University',
      degree: 'Bachelor of Engineering (B.E.) in Information Technology',
      date: '2029', details: ['CGPA: 8.29 / 10'],
    }))
  it('drops tech lines and swaps a swapped title', () => {
    expect(parsed.projects[0]).toEqual({ name: null, date: null, bullets: ['Built a trainer.'] })
    expect(parsed.projects[1]).toEqual({ name: 'FinHabits — Google Solution Challenge 2026', date: null, bullets: ['Built an app.'] })
  })
  it('flags an unnamed project as low quality', () => expect(lowQuality(parsed)).toBe(true))
})

describe('labelFor', () => {
  it('names well-known sites and falls back to the host', () => {
    expect(labelFor('https://www.linkedin.com/in/x')).toBe('LinkedIn')
    expect(labelFor('https://nilanchaljena.me/')).toBe('nilanchaljena.me')
  })
})

describe('limitErrors', () => {
  const resume = (bullets, skills = []) => ({ experience: [{ bullets }], projects: [], skills })
  it('accepts the limits exactly and flags one past them', () => {
    expect(limitErrors(resume(Array(50).fill('b'), ['Java']))).toEqual([])
    expect(limitErrors(resume(Array(51).fill('b')))[0]).toMatch(/at most 50 bullets/)
    expect(limitErrors(resume(['x'.repeat(500)]))).toEqual([])
    expect(limitErrors(resume(['x'.repeat(501)]))[0]).toMatch(/over 500/)
  })
  it('flags more than 200 resume points and 100 skills', () => {
    const many = { experience: [1, 2, 3, 4].map(() => ({ bullets: Array(50).fill('b') })), projects: [], skills: ['Java'] }
    expect(limitErrors(many).join()).toMatch(/200 resume points/)
    expect(limitErrors(resume([], Array(101).fill('s'))).join()).toMatch(/100 skills/)
  })
})
