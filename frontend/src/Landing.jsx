import { useEffect, useState } from 'react'
import { Link } from 'react-router'
import { ArrowsClockwise, Check, FileText, ListChecks, Microphone, Plus, ShieldCheck, Target, X } from '@phosphor-icons/react'
import './styles/landing.css'

// Fill these in when the links exist. An empty string hides that link.
const TUTORIAL_URL = ''
const GITHUB_URL = ''

const BENEFITS = [
  [FileText, 'Questions from your own resume', 'Every question is built from one of your resume points, so you practice what an interviewer will ask you.'],
  [ListChecks, 'Marks with proof', 'Brief and voice answers are marked on a checklist, and every tick quotes the sentence that earned it.'],
  [ArrowsClockwise, 'Weak questions come back', 'A question stays in your sessions until you reach its mastery mark. Mastered ones rest for a month, then return.'],
  [Microphone, 'Voice answers with delivery stats', 'Record an answer and see your speaking pace and filler word count. No microphone? Type the answer instead.'],
  [Target, 'Role skills check', 'See which skills of your target role your resume has no evidence for, before the interview does.'],
]

const STEPS = [
  ['Upload your resume', 'Pick a PDF and your target role. Check the details we read, then save.'],
  ['Answer 19 questions', 'A session is worth 80 marks: multiple correct, fill in the blank, brief typed answers and voice answers.'],
  ['Fix what you missed', 'The report lists the key facts you missed. Weak questions return in your next session until mastered.'],
]

const FAQ = [
  ['What do I need to start?', 'A PDF resume and a target role. A microphone is optional, because you can type any voice answer.'],
  ['What kinds of questions will I get?', 'Multiple correct, fill in the blank, brief typed answers and voice answers, all built from your resume points and your target role skills.'],
  ['How are my answers marked?', 'Multiple correct answers are marked by the app. Brief and voice answers are marked on a yes or no checklist, and each tick shows the sentence from your answer that earned it.'],
  ['When does a question stop coming back?', 'When you reach its mastery mark. A mastered question rests for at least one month before it can return.'],
  ['What happens to my resume and recordings?', 'Only you can see them. The original PDF and your voice recordings are never stored, and your phone number and email are never sent to an AI service.'],
  ['Can I use a session in other tools?', 'Yes. Export JSON downloads one completed session with your answers, marks and feedback. Your phone number and email are removed.'],
  ['What is the story bank?', 'A place to keep your STAR stories (Situation, Task, Action, Result) ready for behavioral questions.'],
  ['Does it cost anything?', 'Creating an account is free. Abhyas.tech is open source under the AGPL 3.0 license.'],
]

const TAGLINE = 'An interviewer picks one line of your resume and keeps asking. Practice that moment here, until every answer comes out clear.'

// W13: public landing page. Shown to everyone at "/", signed in or not.
export default function Landing({ signedIn }) {
  const [menu, setMenu] = useState(false)

  // Scroll reveals and the tagline words use IntersectionObserver, never a scroll listener.
  useEffect(() => {
    const reveal = new IntersectionObserver((entries) => {
      entries.forEach((e) => { if (e.isIntersecting) { e.target.classList.add('in'); reveal.unobserve(e.target) } })
    }, { threshold: 0.15 })
    document.querySelectorAll('[data-reveal]').forEach((el) => reveal.observe(el))

    // A word lights up once it crosses the trigger line (60% down the viewport) and stays lit above it.
    const words = new IntersectionObserver((entries) => {
      entries.forEach((e) => e.target.classList.toggle('on', e.isIntersecting || e.boundingClientRect.top < 0))
    }, { rootMargin: '0px 0px -40% 0px' })
    document.querySelectorAll('.lp-word').forEach((el) => words.observe(el))
    return () => { reveal.disconnect(); words.disconnect() }
  }, [])

  useEffect(() => {
    if (!menu) return
    const onKey = (e) => { if (e.key === 'Escape') setMenu(false) }
    document.addEventListener('keydown', onKey)
    return () => document.removeEventListener('keydown', onKey)
  }, [menu])

  const cta = signedIn
    ? <Link to="/practice" className="lp-btn lp-primary">Go to practice</Link>
    : <Link to="/login?mode=signup" className="lp-btn lp-primary">Create a free account</Link>

  const navLinks = signedIn
    ? <Link to="/practice" className="lp-btn lp-primary">Go to practice</Link>
    : <><Link to="/login" className="lp-btn">Log in</Link><Link to="/login?mode=signup" className="lp-btn lp-primary">Sign up</Link></>

  return (
    <div className="lp">
      <a className="lp-skip" href="#content">Skip to content</a>

      <header className="lp-nav">
        <Link to="/" className="lp-brand">Abhyas.tech</Link>
        <nav aria-label="Account" className="lp-nav-links">{navLinks}</nav>
        <button type="button" className="lp-burger" aria-expanded={menu} aria-controls="lp-menu" aria-label={menu ? 'Close menu' : 'Open menu'} onClick={() => setMenu(!menu)}>
          <span /><span />
        </button>
      </header>
      <div id="lp-menu" className={`lp-menu${menu ? ' open' : ''}`} onClick={() => setMenu(false)}>
        <nav aria-label="Account menu" tabIndex={-1}>{navLinks}</nav>
      </div>

      <main id="content">
        <section className="lp-hero lp-wrap">
          <h1 className="lp-h1" data-reveal>Defend every line<br />of your resume.</h1>
          <p className="lp-lead" data-reveal style={{ '--d': '100ms' }}>
            Abhyas.tech turns your resume into interview questions, marks every answer against your own resume points,
            and brings the weak ones back until you can explain them.
          </p>
          <div data-reveal style={{ '--d': '200ms' }}>{cta}</div>
          <p className="lp-proof" data-reveal style={{ '--d': '300ms' }}>
            <ShieldCheck size={20} aria-hidden="true" /> No PDF or voice recording is ever stored. Delete your account and data anytime.
          </p>

          <figure className="lp-mock" data-reveal style={{ '--d': '400ms' }} aria-label="Example of a marked answer">
            <div className="lp-mock-in">
              <p className="lp-mock-tag">Example question</p>
              <p className="lp-mock-q">Your resume says you cut API response time from 840 ms to 210 ms. What did you change first?</p>
            </div>
            <div className="lp-mock-in">
              <p className="lp-mock-tag">Marked 4 of 5</p>
              <ul className="lp-mock-list">
                <li><Check size={16} aria-hidden="true" /> <span>Named the slow query<small>"I traced it to an unindexed join on orders."</small></span></li>
                <li><Check size={16} aria-hidden="true" /> <span>Gave the before and after<small>"It dropped from 840 ms to 210 ms."</small></span></li>
                <li><X size={16} aria-hidden="true" /> <span>Missed: how you measured it</span></li>
              </ul>
            </div>
          </figure>
        </section>

        <section className="lp-wrap lp-section" aria-labelledby="problem">
          <h2 id="problem" className="lp-h2" data-reveal>Question lists do not know your resume</h2>
          <p className="lp-body" data-reveal style={{ '--d': '100ms' }}>
            Most practice tools hand you the same generic questions and judge how you sound. Interviewers do the opposite:
            they pick a line you wrote and ask you to defend it. Abhyas.tech asks those questions first, then marks whether your answer matches what you actually did.
          </p>
        </section>

        <section className="lp-wrap lp-section" aria-labelledby="benefits">
          <h2 id="benefits" className="lp-h2" data-reveal>What you get</h2>
          <ul className="lp-grid">
            {BENEFITS.map(([Icon, title, text], i) => (
              <li key={title} className="lp-card" data-reveal style={{ '--d': `${(i % 3) * 100}ms` }}>
                <span className="lp-icon"><Icon size={24} aria-hidden="true" /></span>
                <h3 className="lp-h3">{title}</h3>
                <p>{text}</p>
              </li>
            ))}
          </ul>
        </section>

        <section className="lp-tagline lp-wrap" aria-label="Why practice">
          <p className="lp-tag-text">
            {TAGLINE.split(' ').map((w, i) => <span key={i} className="lp-word">{w} </span>)}
          </p>
        </section>

        <section className="lp-wrap lp-section" aria-labelledby="how">
          <h2 id="how" className="lp-h2" data-reveal>How it works</h2>
          <ol className="lp-grid lp-steps">
            {STEPS.map(([title, text], i) => (
              <li key={title} className="lp-card" data-reveal style={{ '--d': `${i * 100}ms` }}>
                <span className="lp-num" aria-hidden="true">{i + 1}</span>
                <h3 className="lp-h3">{title}</h3>
                <p>{text}</p>
              </li>
            ))}
          </ol>
        </section>

        <section className="lp-wrap lp-section" aria-labelledby="privacy">
          <div className="lp-card lp-privacy" data-reveal>
            <h2 id="privacy" className="lp-h2">Your data stays yours</h2>
            <p className="lp-body">
              Only you can see your resumes and answers. The original PDF and your voice recordings are never stored.
              Your phone number and email are never sent to an AI service, and you can delete your account and all its data at any time.
            </p>
          </div>
        </section>

        <section className="lp-wrap lp-section lp-faq" aria-labelledby="faq">
          <h2 id="faq" className="lp-h2" data-reveal>Questions</h2>
          {FAQ.map(([q, a]) => (
            <details key={q} className="lp-card" data-reveal>
              <summary>{q}<Plus size={20} aria-hidden="true" /></summary>
              <p>{a}</p>
            </details>
          ))}
        </section>

        <section className="lp-wrap lp-section lp-final" aria-labelledby="final">
          <h2 id="final" className="lp-h2" data-reveal>Find the weak spots before the interviewer does.</h2>
          <div data-reveal style={{ '--d': '100ms' }}>{cta}</div>
          <p className="lp-proof" data-reveal style={{ '--d': '200ms' }}>Free account. Delete everything anytime.</p>
        </section>
      </main>

      <footer className="lp-footer lp-wrap">
        <p>Abhyas.tech is open source under the AGPL 3.0 license.</p>
        {(TUTORIAL_URL || GITHUB_URL) && (
          <p className="lp-links">
            {TUTORIAL_URL && <a href={TUTORIAL_URL} target="_blank" rel="noreferrer">Watch the tutorial on YouTube</a>}
            {GITHUB_URL && <a href={GITHUB_URL} target="_blank" rel="noreferrer">View on GitHub</a>}
          </p>
        )}
      </footer>
    </div>
  )
}
