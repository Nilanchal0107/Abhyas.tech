import { Link } from 'react-router'

// Fill these in when the links exist. An empty string hides that link.
const TUTORIAL_URL = ''
const GITHUB_URL = ''

const STEPS = [
  ['Upload your resume', 'Pick your target role. Abhyas.tech reads your resume points and checks them against the role skills.'],
  ['Practice session', '19 questions worth 80 marks, built from your own resume: multiple-correct, fill-in-the-blank, brief answers and voice answers.'],
  ['Get feedback', 'Every answer is marked, and the session report shows the key facts you missed.'],
  ['Repeat until mastered', 'Weak questions come back. Mastered ones rest for a month.'],
]

// W13: public landing page. Shown to everyone at "/", signed in or not.
export default function Landing({ signedIn }) {
  const links = (
    <>
      {TUTORIAL_URL && <a href={TUTORIAL_URL} target="_blank" rel="noreferrer">Watch the tutorial (YouTube)</a>}
      {GITHUB_URL && <a href={GITHUB_URL} target="_blank" rel="noreferrer">View on GitHub</a>}
    </>
  )
  return (
    <>
      <a className="skip" href="#content">Skip to content</a>
      <header className="topbar">
        <Link to="/" className="brand"><strong>Abhyas.tech</strong></Link>
        <nav aria-label="Account" className="landing-actions">
          {signedIn
            ? <Link to="/practice" className="button primary">Go to practice</Link>
            : <><Link to="/login" className="button">Log in</Link><Link to="/login?mode=signup" className="button primary">Sign up</Link></>}
        </nav>
      </header>
      <main id="content" className="column landing">
        <section>
          <h1>Defend every line of your resume.</h1>
          <p className="lead">
            Abhyas.tech turns your resume into interview questions, marks your answers, and repeats the weak ones
            until you can explain every resume point with confidence.
          </p>
          <p>
            {signedIn
              ? <Link to="/practice" className="button primary">Go to practice</Link>
              : <Link to="/login?mode=signup" className="button primary">Create a free account</Link>}
          </p>
        </section>

        <section aria-labelledby="how">
          <h2 id="how">How it works</h2>
          <ol className="steps">
            {STEPS.map(([title, text]) => <li key={title}><h3>{title}</h3><p>{text}</p></li>)}
          </ol>
        </section>

        <section aria-labelledby="more">
          <h2 id="more">Also in the box</h2>
          <ul>
            <li><strong>Role skills check:</strong> see which skills of your target role your resume has no evidence for.</li>
            <li><strong>Story bank:</strong> keep your STAR stories ready for behavioral questions.</li>
            <li><strong>Export JSON:</strong> download a practice session with your answers and feedback.</li>
          </ul>
        </section>

        <section aria-labelledby="privacy">
          <h2 id="privacy">Your data stays yours</h2>
          <p>Only you can see your resumes and answers. The original PDF and your voice recordings are never stored, and you can delete your account and all its data at any time.</p>
        </section>

        {(TUTORIAL_URL || GITHUB_URL) && (
          <section aria-labelledby="learn">
            <h2 id="learn">See it in action</h2>
            <p className="landing-links">{links}</p>
          </section>
        )}
      </main>
      <footer className="landing-footer">Abhyas.tech is open source under the AGPL-3.0 license.</footer>
    </>
  )
}
