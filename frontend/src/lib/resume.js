// Turns OpenResume's parse result into ParsedResume (api-contracts 2.1) and decides "looks incomplete" (trd 7.1).
const text = (s) => (typeof s === 'string' && s.trim() ? s.trim() : null)
const list = (a) => (a ?? []).map(text).filter(Boolean)

// A line of technologies ("Python, Flask, Groq") that OpenResume puts in a project's name, date or bullets.
const techLine = (s) => !!s && s.length < 100 && (s.match(/,/g) ?? []).length >= 1 && !/[.:]$/.test(s)
const YEAR = /\b(19|20)\d{2}(\s*[-–]\s*((19|20)\d{2}|present|now))?/i
// A whole string that is only a date or date range: "2024", "Jan 2023 - Mar 2024", "2022 - now".
const DATE_ONLY = /^([a-z]{3,9}\.? )?\d{4}( ?[-–] ?(([a-z]{3,9}\.? )?\d{4}|present|now))?$/i
const INSTITUTION = /university|institute|college|school|academy/i

// "Languages: Java, C" -> ["Java", "C"]; no repeats (case-insensitive).
function splitSkills(lines) {
  const seen = new Set()
  return lines
    .flatMap((l) => l.replace(/^[^:,]{1,40}:\s*/, '').split(/[,;]/))
    .map(text).filter(Boolean)
    .filter((s) => !seen.has(s.toLowerCase()) && seen.add(s.toLowerCase()))
}

function fixEducation(e) {
  let { school, degree, date, details } = e
  if (!school && INSTITUTION.test(details[0] ?? '')) [school, ...details] = details
  if (degree && degree === date) {
    date = degree.match(YEAR)?.[0] ?? null
    degree = text(degree.split('|')[0])
  }
  return { school, degree, date, details: details.filter((d) => d !== e.degree) }
}

function fixProject(p) {
  let { name, date } = p
  if (techLine(name) && date && !DATE_ONLY.test(date)) { name = date; date = null } // title and tech line were swapped
  else if (techLine(name)) name = null
  return { name, date: techLine(date) ? null : date, bullets: p.bullets.filter((b) => !techLine(b)) }
}

// links: [{ label, url }] read from the PDF's link annotations (lib/pdfLinks.js), merged in by the caller.
export function toParsedResume(r, links = []) {
  const url = text(r.profile?.url)
  return {
    profile: {
      name: text(r.profile?.name),
      email: text(r.profile?.email),
      phone: text(r.profile?.phone),
      location: text(r.profile?.location),
      links: [...(url ? [{ label: null, url }] : []), ...links],
      summary: text(r.profile?.summary),
    },
    education: (r.educations ?? []).map((e) =>
      fixEducation({ school: text(e.school), degree: text(e.degree), date: text(e.date), details: list(e.descriptions) })),
    experience: (r.workExperiences ?? [])
      .map((w) => ({ company: text(w.company), title: text(w.jobTitle), date: text(w.date), bullets: list(w.descriptions) }))
      .filter((w) => w.company || w.title || w.bullets.length),
    projects: (r.projects ?? [])
      .map((p) => fixProject({ name: text(p.project), date: text(p.date), bullets: list(p.descriptions) }))
      .filter((p) => p.name || p.bullets.length),
    skills: splitSkills([...list((r.skills?.featuredSkills ?? []).map((s) => s.skill)), ...list(r.skills?.descriptions)]),
  }
}

// Resume points: experience bullets, project bullets and skills.
export const pointCount = (p) =>
  p.experience.concat(p.projects).reduce((n, x) => n + x.bullets.length, 0) + p.skills.length

// No name, or neither experience nor projects, or fewer than 3 resume points.
export const looksIncomplete = (p) =>
  !p.profile.name || (!p.experience.length && !p.projects.length) || pointCount(p) < 3

// Parsed, but still wrong in a way the browser parse cannot fix: an education entry with no school, or an
// unnamed project. The fallback parse should run too (trd 7.1 step 2).
export const lowQuality = (p) => p.education.some((e) => !e.school) || p.projects.some((x) => !x.name)

// A PDF that is not a resume: no email or phone, and no experience, projects, education or skills at all.
// (A real resume the parser read badly still has some of these and goes on to the fallback.)
export const notAResume = (p) =>
  !p.profile.email && !p.profile.phone &&
  !p.experience.length && !p.projects.length && !p.education.length && !p.skills.length

// File checks done before parsing: %PDF header and at most 1 MB. Returns a message, or null if fine.
export async function checkFile(file) {
  if (file.size > 1024 * 1024) return 'This file is over 1 MB. Please choose a smaller PDF.'
  const head = new TextDecoder().decode(await file.slice(0, 5).arrayBuffer())
  return head === '%PDF-' ? null : 'Please choose a PDF file.'
}

// Limits from api-contracts 1.2, checked before saving. Returns inline messages (empty when fine).
export function limitErrors(p) {
  const out = []
  for (const [label, list] of [['experience', p.experience], ['project', p.projects]]) {
    list.forEach((e, i) => {
      if (e.bullets.length > 50) out.push(`Use at most 50 bullets in ${label} ${i + 1}.`)
      e.bullets.forEach((b, j) => b.length > 500 && out.push(`Bullet ${j + 1} of ${label} ${i + 1} is over 500 characters.`))
    })
  }
  if (p.skills.length > 100) out.push('Use at most 100 skills.')
  p.skills.forEach((s) => s.length > 500 && out.push(`The skill "${s.slice(0, 30)}..." is over 500 characters.`))
  if (pointCount(p) > 200) out.push('Use at most 200 resume points in total (bullets and skills).')
  return out
}
