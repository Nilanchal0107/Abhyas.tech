import * as pdfjs from 'pdfjs-dist'
import workerSrc from 'pdfjs-dist/build/pdf.worker.min.js?url'

// Link annotations hold the real URLs behind words like "GitHub" (the text layer has only the word).
// Returns [{ label, url }] without repeats; mailto: and tel: are left out (email and phone are read as text).
export async function readLinks(fileUrl) {
  pdfjs.GlobalWorkerOptions.workerSrc = workerSrc
  const pdf = await pdfjs.getDocument(fileUrl).promise
  const links = new Map()
  for (let i = 1; i <= pdf.numPages; i++) {
    for (const a of await (await pdf.getPage(i)).getAnnotations()) {
      if (a.subtype === 'Link' && /^https?:/i.test(a.url ?? '')) links.set(a.url, { label: labelFor(a.url), url: a.url })
    }
  }
  return [...links.values()]
}

export function labelFor(url) {
  const host = new URL(url).hostname.replace(/^www\./, '')
  if (host.includes('github')) return 'GitHub'
  if (host.includes('linkedin')) return 'LinkedIn'
  return host
}
