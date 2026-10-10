# Abhyas.tech Study Guide: From "What Is the Web?" to System Design

## Before we start: how to use this guide

### Why is this guide written as questions?

When you explain something in an interview, you do not recite a definition.

You tell a small story: here was a problem, here is why it hurts, here is the idea that fixes it.

So every topic here follows the same path.

First we feel the pain of living without the concept.

Then we ask: what is the real root cause of the pain?

Then we ask: so how can we solve this?

Only after that do we meet the concept, and we see why it has the shape it has.

If you learn it this way, you will never need to memorise a definition, because you could have invented the concept yourself.

> **Pause and think.** The test of real understanding: could you invent this idea if nobody had told you about it?

### What are the three voices in this guide?

Each big topic ends with up to three boxes.

- **Explain it to a stranger.** Simple words, one picture in the head, no jargon. Use this with a non-technical person, or in the first 30 seconds of an interview answer.
- **Explain it to an expert.** The same idea with the precise terms, the trade-offs and the "why not the other option". Use this when the interviewer leans forward.
- **In Abhyas.tech.** Where the idea lives in your own project, so every answer ends with "and here is how I used it".

The skill you are training is switching between the first two voices on demand.

### What is Abhyas.tech, in one breath?

Abhyas means "practice".

Abhyas.tech is a coach that reads your resume, turns every line of it into interview questions, marks your answers, and keeps bringing back the questions you are weak at until you can defend them.

The flow is: upload a PDF, check what we read, answer 19 questions, get a marked report, repeat the weak ones.

The tech: React in the browser, Spring Boot (Java 21) on the server, MySQL 8 for data, and AI models (Groq and Google AI Studio) that help read, grade and explain.

### Where is the map of the whole system?

Here it is.

Do not try to understand every box now.

Come back to this picture after every chapter, and each time more of it will light up.

**[Diagram: Architecture](architecture.html).** Browser on the left, API in the middle, database below, AI providers on the right. The dashed line is background work.

> **In Abhyas.tech.** Three rules shaped this whole picture. The browser never calls an AI provider (keys stay on the server). Every database query is scoped to the signed-in user. Grading runs in the background so the user never waits.

### Which diagram answers which question?

| Diagram | The question it answers | Chapter |
|---|---|---|
| Architecture | What are the big parts, and who talks to whom? | 6, 7, 15 |
| Database | What do we store, and how are the tables related? | 8 |
| Data flow | How does a PDF become a report? | 10 |
| Sequence | Who says what, and in which order, when I press Submit? | 12 |
| Workflow | What does one practice session look like for the user and the backend? | 13 |
| Lifecycle | How does a question go from new to mastered, and why does it come back? | 13 |

## What is the web?

### Let us start with the simplest problem: two computers that need to talk

Imagine you are in Delhi and your friend is in Pune, and each of you has a computer.

You want to send a photo from your computer to theirs.

What do you need?

First, a physical path between the two machines. Cables, fibre, Wi-Fi, mobile towers. Together they form a **network**.

Now join millions of networks together and you get the **internet**: a giant road system that any computer can drive on.

Okay, but a road system alone is not enough.

A road tells you nothing about where a particular house is.

> **The root cause.** The first root problem: a computer needs an address, otherwise a message has nowhere to go.

### How does a message find the right computer?

So how can we solve the address problem?

Give every computer on the road system a unique number, like a house number.

That number is the **IP address**, for example `142.250.77.46`.

The message is cut into small pieces called **packets**, each stamped with the destination IP, and routers along the way pass each packet toward that house.

> **Think of it like this.** Sending a big parcel as many small envelopes, each with the address written on it. Different envelopes can take different roads and the receiver puts them back in order.

But can you remember `142.250.77.46`?

Of course not, and nobody wants to.

> **The root cause.** The second root problem: humans remember names, computers use numbers.

So we need a phone book that turns names into numbers.

That phone book is **DNS** (Domain Name System).

You type `abhyas.tech`, your computer asks DNS "what is the number for this name?", gets the IP address, and now it knows where to send packets.

### Now two computers can reach each other. Is that the web?

Not yet.

The internet is the road.

The **web** is what we put on the road: pages and data that live on some computers and are asked for by other computers.

Think about what has to happen when you want to read a page.

One computer has the page.

Another computer wants it.

So the first needs to wait and serve, and the second needs to ask and show.

That gives us two roles.

- The **server**: a computer that waits for requests and answers them. It is always on.
- The **client**: a computer (or a program on it) that sends a request and uses the answer. Your browser is a client.

> **Think of it like this.** A restaurant. The kitchen (server) is always open and prepares what is ordered. You (client) sit down, order, and eat. You never walk into the kitchen.

### But how do they understand each other? Why do we need rules?

Say the client sends "gimme the page".

The server receives a pile of bytes.

Which part is the page name? Is "gimme" a command? How does the server say "not found" in a way the client will understand?

> **The root cause.** The third root problem: two programs written by different people need a shared agreement on how to ask and how to answer.

An agreement like that is called a **protocol**.

The protocol of the web is **HTTP** (HyperText Transfer Protocol).

It says: a request looks like this, a response looks like that.

We will open HTTP up in the next chapter.

### What do we actually get back? HTML, CSS and JavaScript

The server sends back text in three flavours, and the browser knows what to do with each.

- **HTML**: the skeleton. "Here is a heading, here is a button, here is a paragraph."
- **CSS**: the clothes. "Make the heading serif, the button black."
- **JavaScript**: the muscles. "When the button is clicked, do this."

> **Think of it like this.** A house. HTML is the walls and rooms, CSS is the paint and furniture, JavaScript is the electricity that makes the lights turn on.

### What is a URL, and what is HTTPS?

A **URL** is the full address of one thing on the web.

Take `https://abhyas.tech/api/resumes?limit=5`.

- `https` is the protocol (how to talk).
- `abhyas.tech` is the host (which computer, found through DNS).
- `/api/resumes` is the path (which thing on that computer).
- `?limit=5` is the query (extra details for the request).

Now a worry: the road is public.

Anyone along the way could read your packets, including your password.

> **The root cause.** The root problem: a public road means anyone can eavesdrop or tamper.

So we scramble the message so only the two ends can read it.

That scrambling layer is **TLS**, and HTTP over TLS is **HTTPS**.

The padlock in the browser means: the conversation is private, and you are really talking to who you think you are talking to.

> **Explain it to a stranger.** The web is computers asking other computers for pages and data. Your browser is the asker, and a server is the answerer. They follow a shared rulebook called HTTP so they always understand each other, and HTTPS keeps the conversation private.
> **Explain it to an expert.** The web is an application-layer, request-response system on top of TCP/IP. Names resolve through DNS to IPs, a TCP connection is opened (with a TLS handshake for HTTPS), and the client sends an HTTP request that the server answers with a representation of a resource. Everything else, from cookies to REST, is a convention on top of that exchange.
> **In Abhyas.tech.** In development, the browser talks to the Vite dev server on port 5173, and that server quietly forwards anything under `/api` to Spring Boot on port 8080. You will see why in chapter 6.

## What is HTTP, and why do cookies exist?

### What does a request and a response really look like?

Let us look at the actual text, because HTTP is just text with a shape.

A request:

```
GET /api/resumes HTTP/1.1
Host: abhyas.tech
Cookie: rdt_session=eyJhbGciOi...
Accept: application/json
```

A response:

```
HTTP/1.1 200 OK
Content-Type: application/json

{"resumes":[{"id":1,"fileName":"Paras_Resume.pdf"}]}
```

A request has four parts: a **method** (what to do), a **path** (to what), **headers** (extra facts about the request) and an optional **body** (data being sent).

A response has a **status code** (how it went), headers, and a body.

### Why are there different methods like GET and POST?

Imagine there was only one verb, "do".

"do /resumes/5" could mean read it, delete it, or replace it.

> **The root cause.** The root problem: without a verb, neither humans, browsers, caches nor security tools can tell whether a request is safe.

So HTTP names the intention in the method.

| Method | Meaning | Safe to repeat? | Abhyas example |
|---|---|---|---|
| GET | read something | yes, changes nothing | `GET /api/sessions/7` |
| POST | create something or start an action | no | `POST /api/sessions` starts a session |
| PUT | replace something | yes, same result every time | `PUT /api/sessions/7/attempts/3` saves one answer |
| PATCH | change part of something | usually | `PATCH /api/resumes/1` changes only the interview date |
| DELETE | remove something | yes | `DELETE /api/resumes/1` |

The "safe to repeat" column has a name: **idempotent**.

Why does it matter? Networks fail.

If a PUT times out, the browser can simply send it again, because saving the same answer twice leaves the same result.

You could not do that with "charge the card" unless you designed it to be repeat-safe.

### What are status codes, and why are they numbers?

The client needs a quick, machine-readable verdict before it even reads the body.

So responses start with a three-digit code grouped by the first digit.

- **2xx** means success. `200 OK`, `201 Created`, `204 No Content`.
- **3xx** means look elsewhere (redirects).
- **4xx** means the client made a mistake. `400` bad data, `401` not logged in, `403` not allowed, `404` not found, `409` conflict, `429` too many requests.
- **5xx** means the server made a mistake. `500`, `503` unavailable.

> **In Abhyas.tech.** Abhyas answers `409` when you try to start a second practice session while one is open, and `404` (not `403`) when you ask for another user's resume. Why 404? Because "forbidden" would confirm that the resume exists. "Not found" tells an attacker nothing.

### Here is a strange fact: HTTP forgets you instantly. Why is that a problem?

HTTP is **stateless**.

Each request is handled as if it were the first one ever.

The server does not remember that you logged in one second ago.

Why was it designed like that?

Because a server that remembers nothing is easy to copy and scale.

Any machine can answer any request, because no machine holds a secret memory about you.

But look at what that does to a login.

- You send your password. The server says "welcome".
- You click "My resumes". The server says "who are you?"

Without memory, you would have to send your password on every single click.

> **The root cause.** The root problem: a stateless protocol, but a login needs to be remembered across many requests.

### So how can we solve this? Enter cookies

What if the server hands you a small note after login, and the browser automatically attaches that note to every later request?

The server then only has to look at the note.

That note is a **cookie**.

Mechanically: the server replies with a header `Set-Cookie: name=value`, the browser stores it, and for every future request to that site it adds `Cookie: name=value`.

> **Think of it like this.** A wristband at a concert. You show your ID once at the gate and get a wristband. After that, every door only checks the wristband, never your ID again.

But a wristband can be stolen or forged.

What goes inside the note, and how do we stop someone from writing their own?

That is exactly the question JWT answers in chapter 9.

> **Explain it to a stranger.** HTTP forgets who you are between clicks. So after you log in, the server gives your browser a small stamped note called a cookie, and your browser shows it on every later request, like a wristband at a concert.
> **Explain it to an expert.** HTTP is stateless by design, which makes servers horizontally scalable. Session identity is therefore carried by the client in each request, most commonly in a cookie set via `Set-Cookie`. The cookie holds either an opaque session ID looked up server-side, or a self-contained signed token such as a JWT.
> **In Abhyas.tech.** Abhyas stores the login in a cookie named `rdt_session`. It is `HttpOnly`, `SameSite=Strict`, lives 8 hours and holds a JWT.

## What is an API?

### Imagine there is no API. How would the browser get the user's resumes?

Suppose the page and the data live together.

The server builds a full HTML page with your resumes baked into it, and sends it.

Now you want a mobile app too.

It does not want HTML. It wants only the data, so it can draw its own screen.

You also want to change one number on the screen without reloading the entire page.

- Every new screen means the server must produce a new HTML page.
- Every new kind of client (phone, watch, another service) needs a separate backend.
- Designers and data people are stuck in the same code.

> **The root cause.** The root problem: the data and the way it is displayed are glued together.

### So how can we solve this?

Cut the glue.

Let the server only expose **data and actions**, in a neutral format.

Any client can then ask for exactly what it needs and draw it however it likes.

That doorway between "something that wants data or actions" and "something that has them" is an **API** (Application Programming Interface).

> **Think of it like this.** A restaurant menu and a waiter. You do not walk into the kitchen and cook. You read the menu (what you may ask for), tell the waiter your order, and a plate comes back. The menu is the API: it lists what is allowed and in which form to ask.

In web projects, "API" almost always means a **web API**: a set of URLs that accept HTTP requests and return data.

### What is REST, and why do URLs look like `/resumes/5`?

Once you have many URLs, you need a naming style, otherwise it becomes chaos: `/getResume`, `/fetchAllResumes`, `/deleteResumeNow`.

**REST** is a simple habit: URLs name **things** (nouns), HTTP methods are the **verbs**.

- `GET /resumes` means list the resumes.
- `GET /resumes/5` means read resume 5.
- `DELETE /resumes/5` means delete resume 5.

Why is that good? Because a stranger can guess your API.

The thing is in the URL, the action is in the method, and the result is in the status code.

### What is JSON, and why do APIs send it?

The response body must be something both a Java server and a JavaScript browser can read.

**JSON** is plain text shaped like `{"name": "Paras", "marks": 61}`.

It is readable by humans, tiny, and every language can turn it into its own objects.

> **The root cause.** Why JSON and not HTML or a binary format? JSON carries only data (no layout), and any language can parse it.

### How does the server say "something went wrong" in a useful way?

Returning `500 error` is lazy.

The UI wants to show a friendly sentence and decide what to do next (retry? show a field error?).

Abhyas follows a standard called **Problem Details** (RFC 7807): every error body has a stable `type` code, a `status`, and a human `detail`.

```
{ "type": "session-open", "status": 409,
  "detail": "You already have a practice session in progress." }
```

The frontend can then branch on `type === 'session-open'` and simply continue that session instead of showing an error.

That is not magic; it is a contract.

### What is an "API contract", and why did you write one?

Two teams (or two halves of one project) build in parallel.

If the frontend expects `fileName` and the backend sends `file_name`, nothing works.

So you write down the exact shape of every request and response before coding.

> **In Abhyas.tech.** `api-contracts.md` is that agreement for Abhyas.tech: every endpoint, request, response, error code and limit. Rules from it: another user's resource is always 404. A session in progress never reveals correct answers to the browser. Signup also logs you in.
> **Explain it to a stranger.** An API is a menu for software. It lists what you can ask a program to do and what shape the answer will come in, so many different apps can use the same program without knowing how it works inside.
> **Explain it to an expert.** A web API exposes resources over HTTP with a uniform interface: nouns in the URL, verbs as methods, representations (JSON) in the body, and standard status codes. Decoupling data from presentation lets multiple clients share one backend. A written contract with stable error types gives the frontend something to branch on, and it makes the two halves independently testable.

## How else can a browser get data?

### First, how does it normally happen?

The browser asks, the server answers, and the conversation is over.

This is called **request-response**.

In JavaScript, that is the `fetch()` function. Abhyas wraps it in `src/api/client.js` so every call sends the cookie, parses JSON, and turns Problem Details into a proper error.

But request-response has a hidden weakness.

### What if the data changes after I asked?

Think about the report page.

You press Submit, and the server needs about two minutes to grade 19 answers and write feedback.

The server cannot tap the browser on the shoulder, because in plain HTTP **only the client can start a conversation**.

> **The root cause.** The root problem: the server has news, but it has no way to speak first.

So how can we solve this?

There are several answers, and each costs something different.

### Option 1: Polling. "Are we there yet?"

The browser simply asks again and again, on a timer.

- Easy. It is just request-response in a loop. Works everywhere.
- Wasteful. Most answers are "no change yet".
- Slow by up to one interval.

> **Think of it like this.** A child in the back seat asking "are we there yet?" every few minutes.

### Option 2: Long polling. "Tell me when you have something"

The browser asks, and the server deliberately **holds the request open** until it has news (or a timeout), then answers. The browser immediately asks again.

- Fewer empty answers, near-instant updates.
- Many open connections sitting on the server.

### Option 3: Server-Sent Events (SSE). A one-way radio

The browser opens one long connection, and the server keeps pushing small messages down it whenever it likes.

- Simple and built into browsers. Automatic reconnect.
- One direction only (server to browser).

### Option 4: WebSockets. A phone call

After a handshake, the connection upgrades to a two-way channel. Either side can talk at any time.

- Perfect for chat, multiplayer games, live cursors.
- Heavier to run and scale, because every user holds a live connection.

### Option 5: Webhooks. "Call me when it is done"

This is server-to-server, not for browsers. You give another service a URL, and it sends an HTTP request to you when something happens (payment succeeded, code pushed).

### And what about asking for data in different shapes?

So far we changed **when** data arrives. Another problem is **how much** we ask for.

- **REST** gives fixed shapes. Sometimes you receive too much (over-fetching) or need three calls to build a screen (under-fetching).
- **GraphQL** lets the client describe the exact fields it wants in one query. Great for complex screens. Costs: a more complex server, harder caching.
- **gRPC** uses a compact binary format with a strict schema. Great and fast between internal services. Browsers cannot use it directly.
- **Server-side rendering (SSR)** builds the HTML on the server, so the first screen arrives ready. Good for SEO and first load speed.

### Compare them in one table

| Way | Who starts? | Best for | Cost |
|---|---|---|---|
| Request-response (fetch) | client | almost everything | no live updates |
| Polling | client, repeatedly | "tell me when a job finishes", simple dashboards | wasted calls, small delay |
| Long polling | client, held open | near real-time without new tech | many open requests |
| SSE | server pushes | live feeds, progress bars | one direction |
| WebSocket | both | chat, games, collaboration | stateful, harder to scale |
| Webhook | another server | server-to-server events | needs a public URL |
| GraphQL | client | complex screens, many clients | server complexity |

### Why did Abhyas choose polling for the report?

Think like an engineer, not a fan of any technology.

- The user just needs "rows fill in over the next minute or two". That is slow-moving, one-direction news.
- Polling needs nothing new on the server, works on localhost, and survives restarts: the page simply asks again and gets the latest state from the database.
- A WebSocket would add a stateful connection, reconnect logic and more testing, for a benefit nobody would notice.

> **The root cause.** The cost of a technology is not only writing it. It is also running, debugging and scaling it. Pick the cheapest tool that makes the user happy.
> **In Abhyas.tech.** The report page calls `GET /api/sessions/{id}` about every 2.5 seconds, but only while something is still pending. When all rows are ready, the polling stops. If you reload the browser mid-grading, nothing is lost, because the truth lives in MySQL, not in the connection.
> **Explain it to a stranger.** Normally the browser asks and the server answers. But when the answer takes minutes, the browser has to keep checking back, like a child asking "are we there yet?" Smarter tricks exist where the server pushes news instead, but for a slow, one-way update the simple checking is enough.
> **Explain it to an expert.** HTTP is client-initiated, so server-to-client updates need a workaround: polling, long polling, SSE (unidirectional over HTTP), or WebSockets (full duplex after an upgrade). I picked short polling for the report because grading is a minutes-long job with unidirectional updates, the state is durable in the database, and polling is stateless, trivially load-balanced and restart-safe. If we needed sub-second updates to thousands of users I would move to SSE first, because it is cheaper than WebSockets when traffic is one-way.

## Frontend: why React, why Vite, and what is that proxy?

### What did people do before React?

They wrote HTML, then used JavaScript to **find an element and change it by hand**.

"Find the span with id `score`, set its text to 61. Find the list, add a new `li`. Remember to remove the old one."

For a small page that is fine.

For a real app, there are hundreds of such manual edits, and they must stay consistent with each other.

- Change the data in one place and forget to update the screen in another, and now the page lies to the user.
- Nobody knows which code touched which element.

> **The root cause.** The root problem: we keep the **data** and the **screen** in two places and must keep them in sync by hand.

### So how can we solve this?

What if we only describe **what the screen should look like for the current data**, and let a tool do the editing?

That is the idea behind **React**.

You write small functions called **components**, which take data and return a description of the screen.

When the data (the **state**) changes, React runs the function again, compares the new description with the old one, and updates only the parts of the real page that differ.

> **Think of it like this.** A smart painter. You hand them a photo of how the wall should look now. They compare it to the wall and only repaint the patches that changed.

```
const [answers, setAnswers] = useState({})   // the data
// change the data -> React redraws exactly what depends on it
setAnswers({ ...answers, 3: 'my answer' })
```

> **In Abhyas.tech.** The Practice screen holds the current question index and the answers in state. Press Next, the index changes, and React redraws the question card. You never touch the DOM by hand.

### What is Vite, and why do we need a build tool at all?

Browsers speak plain JavaScript, but we write modern code split into hundreds of files, using JSX (HTML-looking syntax) that browsers cannot read.

Someone has to translate, bundle and optimise.

**Vite** does that, and during development it serves your files instantly and refreshes the page when you save.

`npm run build` produces the small, fast files you would ship.

### What is that "proxy" in the architecture, and what is CORS?

Here is a rule that surprises beginners.

A page loaded from `localhost:5173` is **not allowed** to read answers from `localhost:8080`, because the port differs.

Why would browsers be so strict?

> **The root cause.** Imagine you are logged into your bank in one tab, and a random site in another tab could silently call the bank's API using your cookies and read the result. That would be a disaster.

So the browser enforces the **same-origin policy**: a page can freely talk to its own origin (protocol + host + port), and talking to others requires the other server's permission, given through **CORS** headers.

We could configure CORS on Spring Boot. But there is a cleaner trick.

What if the browser only ever talks to **one** origin?

In development, the Vite server on 5173 receives every request. Pages and scripts it serves itself, and anything starting with `/api` it forwards to Spring on 8080.

From the browser's point of view there is **one origin**, so there is no cross-origin request and no CORS to configure.

This single decision gives a free bonus: because everything is same-origin, the login cookie can use `SameSite=Strict`, a strong protection against forged requests (chapter 9).

> **Explain it to a stranger.** The browser refuses to let a page from one address read data from another address, to protect you. We avoid the problem by making the browser talk to a single address, which quietly passes the data requests on to the real server behind it.
> **Explain it to an expert.** Under the same-origin policy a cross-origin fetch needs explicit CORS opt-in. In dev, Vite's proxy forwards `/api` to Spring Boot so the browser sees one origin: no CORS, and `SameSite=Strict` cookies work. In production the same effect comes from serving the static build and the API behind one reverse proxy.

### How is the frontend organised, and where does it keep its data?

- **Routes** (React Router) map URLs to screens: `/practice`, `/resumes`, `/history`, `/sessions/:id/report`.
- A small `fetch` wrapper centralises the cookie, JSON handling and the 401 rule: if any call returns "not logged in", the app sends you to login and brings you back.
- The practice screen has **no top bar** on purpose: you can not wander away by accident, you must press "Leave session".
- Accessibility is built in: every input has a label, errors have an icon plus the word "Error" (never color alone), progress bars are announced to screen readers.

> **Watch out.** A small honesty note for interviews. The TRD planned TanStack Query for data fetching, but the current code uses plain `fetch` with `useEffect` and a `setTimeout` loop for polling, and TanStack Query is not in `package.json`. Say what the code does, and if asked, say you would adopt TanStack Query to get caching and retries for free.

## Backend: what does the server actually do?

### Why do we need a backend at all? Could the browser do everything?

Try it in your head.

Put the whole app in the browser: parse the PDF, store resumes in the browser, grade answers, call the AI.

- **Secrets leak.** To call an AI provider the browser needs an API key. Anything in the browser can be read by the user. Anyone could steal the key and run up your bill.
- **No shared truth.** Data stored in one browser does not exist on another laptop. Two users cannot be kept apart.
- **Cheating.** If grading runs in the browser, a user can change the code and give themselves full marks.

> **The root cause.** The root problem: the browser is the user's computer. It cannot be trusted with secrets, shared data or rules.

So we need a computer **we** control, which holds the secrets, owns the data, and enforces the rules.

That is the backend.

### What does Spring Boot do for us?

A server program has to do boring things: listen on a port, parse HTTP, turn JSON into objects, validate input, talk to a database, handle errors, manage threads.

Writing all that by hand would take weeks.

**Spring Boot** is a Java framework that provides these ready-made, so you write only the interesting part: "when someone calls `POST /api/sessions`, do this".

You mark a class and a method, and Spring wires the URL to your code.

### Why is the code split into controllers, services and repositories?

Imagine one giant method that reads the HTTP request, checks the login, runs the rules, writes SQL and formats the response.

Now change the database. Everything breaks.

Now test the rules. You cannot, without starting a web server.

> **The root cause.** The root problem: when one piece of code has several jobs, any change can break any job.

So we give each layer exactly one job.

| Layer | One job | Abhyas example |
|---|---|---|
| Controller | translate HTTP to a method call and back | `SessionController` |
| Service | the real rules of the app | `SessionService`, `GradingService` |
| Repository | talk to the database | JPA repositories |

> **Think of it like this.** A restaurant again. The waiter (controller) takes the order and brings the plate. The chef (service) cooks by the recipe. The pantry clerk (repository) fetches the ingredients. The waiter never cooks, and the chef never walks to the table.

Abhyas also groups packages **by feature** (`auth`, `resume`, `session`, `ai`, `story`, `export`) rather than by layer, so everything about sessions lives in one folder.

### How does the server know who is calling?

Every request under `/api` (except signup, login and logout) first passes through an **interceptor**.

It reads the cookie, checks the token, and puts the user's id on the request.

If anything is wrong, it stops the request with `401`.

Then, in every service, the user id is passed into every query, so you only ever see your own rows.

> **In Abhyas.tech.** Helper methods like `requireOwnedResume(userId, id)` throw "not found" for somebody else's data. Controllers never load a resume or a session by id alone. This is the single most important security rule in the code.

### What is a thread, and why does it matter here?

A server handles many requests at the same time, each on its own **thread** (a worker doing one thing).

Now think about grading. It takes a minute or two, because calling an AI is slow.

If grading ran on the request thread, the user's Submit button would hang for two minutes, and the worker would be stuck for all that time.

We will return to this in chapter 12, because it is the heart of the "Submit returns at once" design.

> **Explain it to a stranger.** The backend is the trusted computer in the middle. It keeps the secrets, owns the data and applies the rules, because the user's own browser can never be trusted with those. Inside, the code is split so each part does one job: one takes requests, one holds the rules, one talks to the database.
> **Explain it to an expert.** A stateless Spring Boot REST API with layered, feature-packaged design: controllers handle transport, services own business logic and transactions, JPA repositories own persistence. An interceptor authenticates each request from a signed cookie and injects the user id; every repository call is scoped by it, giving tenant isolation at the query level.

## Database: where does the data live, and why a database?

### Why not just save things in files?

Try saving resumes and answers in text files.

- Two requests write the same file at the same moment. One overwrites the other.
- The power goes out halfway through writing. You have half a file.
- You want "all unmastered questions for user 4, weakest first". You must open every file and loop through it yourself, slowly.
- You want to guarantee that a resume always belongs to a real user. A file has no way to enforce that.

> **The root cause.** The root problems: concurrent writes, crashes mid-write, slow searching, and no way to enforce rules about the data.

So how can we solve these?

Use software built just for storing data safely.

That is a **database**.

It gives you four gifts: fast search (**indexes**), safe changes (**transactions**), enforced rules (**constraints**), and many users at once.

### Why a relational database like MySQL?

Our data is full of relationships.

A user has resumes. A resume has points. A session belongs to a resume. A session has attempts. An attempt points to a question.

A **relational** database stores each kind of thing in a **table** (rows and columns) and connects tables with **keys**.

- **Primary key**: a unique id for each row.
- **Foreign key**: a column that points to a row in another table, such as `attempt.session_id`.

The database refuses to save a foreign key that points to nothing.

> **Think of it like this.** A school register. One sheet lists students, another lists classes, and a third sheet lists which student is in which class using ids, not by rewriting the whole student each time.

Here is the whole picture of Abhyas.tech's tables.

**[Diagram: Database](database.html).** Eleven main tables. Solid arrows are required links, dashed arrows are optional ones.

### How do I read this diagram like a story?

Start at `app_user`, the account. Everything traces back to it.

- A user **uploads** a `resume` (`resume.user_id`) for a target `role`.
- The resume is split into `resume_point` rows, one per project bullet, experience bullet or skill. Some link to a `skill`.
- A user starts a `practice_session` for a resume. The session has many `attempt` rows, one per question slot (19 for a quiz).
- Each attempt points to a `question`, either written for this resume or taken from the shared `bank_question`.
- `bank_performance` remembers, **per user**, how well that user did on each bank question. That is why mastery follows you from one resume to the next.
- `story` rows are your saved STAR stories.

> **The root cause.** A good question to ask about any table: who owns this row? In Abhyas, nearly every answer is "the user", and deleting the user cascades through the tree, so account deletion leaves nothing behind.

### What is a migration, and why Flyway?

The database structure changes as the app grows: a new column today, a new table tomorrow.

If everyone edits their local database by hand, no two machines match.

So the structure is written as numbered SQL files (`V1__schema.sql`, `V2__...`), and **Flyway** runs the ones not yet applied, in order, on startup.

Code and database change together, in version control.

Abhyas sets `ddl-auto=validate`, which means the app **refuses to start** if the Java entities and the real tables disagree.

That turns a silent bug into a loud one, on the first second.

### Why does it mention a "generated column" and a "unique index"?

We want a rule: a user can have only one `in_progress` session at a time.

You could check this in Java. But two requests at the same moment can both check, both see none, and both create one.

> **The root cause.** The root problem: rules enforced only in application code can be raced.

So we make the database enforce it.

A generated column `open_user_id` holds the user id only when the session is `in_progress`, and a **unique index** on it makes a second open session impossible, no matter how many requests arrive.

The same thinking applies to `ON DELETE CASCADE`: the database, not the code, guarantees that deleting an account deletes its rows.

### What is a transaction?

Saving a resume means inserting one `resume` row and many `resume_point` rows.

What if it fails halfway?

You would have a resume with half its points.

A **transaction** groups several changes into one all-or-nothing unit.

Either every change is saved, or none is.

> **Think of it like this.** A bank transfer. Money must leave one account and arrive in the other. It can never leave and then vanish.
> **In Abhyas.tech.** Saving a resume runs in one transaction. Skills are matched to the existing `skill` table by name, and the app never creates new skills from resumes.
> **Explain it to a stranger.** A database is a tidy, super-fast filing cabinet that many people can use at once without messing each other up. It also refuses to keep information that breaks the rules, like an answer that belongs to no one.
> **Explain it to an expert.** MySQL 8 with InnoDB, a normalised schema with foreign keys and cascading deletes. Flyway owns schema evolution and Hibernate runs in `validate` mode. Invariants that must hold under concurrency, like a single open session per user, are enforced with a generated column and a unique index, not with application checks. JSON columns store flexible payloads such as key facts, checklists and the parsed resume. Everything is UTC.

## How does login work? Passwords, cookies and JWT

### What is the very first problem? Storing passwords

The user types a password. We must remember it to check it next time.

The lazy way is to store it as typed.

Now picture a hacker who reads your database for one minute.

They hold every user's password. And since many people reuse passwords, they can now log in to those users' email and bank.

> **The root cause.** The root problem: if we can read the password, so can anyone who steals the database.

So how can we solve this?

What if we store something from which the password **cannot be recovered**, yet can still be **checked**?

That is a **hash**: a one-way scramble.

`hash("hello")` always gives the same output, but no one can turn the output back into "hello".

At login: hash what the user typed, and compare with the stored hash.

### Why BCrypt and not just a normal hash?

Fast hashes (like SHA-256) are a gift to attackers: they can try billions of guesses per second.

Also, two users with the same password would have the same hash, so cracking one cracks both.

**BCrypt** fixes both.

- It adds a random **salt** to every password, so equal passwords give different hashes.
- It is deliberately **slow** and tunable, so each guess costs the attacker real time.

> **Watch out.** Why does Abhyas limit passwords to 72 characters? BCrypt only looks at the first 72 bytes. Allowing longer ones would silently ignore the rest.

### What if someone just keeps guessing?

That is a brute-force attack.

So Abhyas counts failed attempts per login id and blocks login for 5 minutes after 5 failures.

The error message is identical for "no such user" and "wrong password", so an attacker cannot discover which user ids exist.

The counter lives in memory and resets on restart. That is accepted for a laptop app, and a shared store like Redis would be used if it ran on many servers.

### After login, how does the server remember me? Two designs

We saw the problem in chapter 3: HTTP forgets you. We want the browser to carry a proof of login.

There are two classic designs.

**Design A: server-side sessions.**

After login the server creates a record in its memory or database: `session 8f3a... belongs to user 4`.

It gives the browser only the random id `8f3a...`.

On each request the server looks the id up.

- Easy to cancel: delete the record and the user is logged out at once.
- But the server must keep and look up a record for every logged-in user, and with many servers they must all share that store.

**Design B: signed tokens.**

The server puts the facts inside the note itself, "this is user 4, valid until 8 pm", and **signs** it. On each request it only checks the signature.

- No lookup, no stored sessions, any server can verify it.
- But it is hard to cancel before it expires.

### What is a JWT, exactly?

**JWT** (JSON Web Token, say "jot") is the standard format for Design B.

It is three pieces joined by dots: `header.payload.signature`

```
eyJhbGciOiJIUzI1NiJ9 . eyJzdWIiOiI0IiwiZXhwIjoxNzYwMDAwMDAwfQ . 9xK...signature...
   header                       payload (claims)                      signature
```

- **Header**: which algorithm signs it. Abhyas uses HS256.
- **Payload**: the **claims**, which are the facts. Abhyas puts the user id in `sub`, the time it was issued in `iat`, and the expiry in `exp`.
- **Signature**: computed from the header, the payload and a **secret** only the server knows.

Here is the most important thing to understand, and the one interviewers love to test.

> **Watch out.** A JWT is **signed, not encrypted**. Anyone can decode the payload (it is just Base64 text). The signature only proves **nobody changed it**. So never put secrets in a JWT.

### Why can't an attacker just edit the payload?

Suppose the attacker changes `sub` from 4 to 1 to become an admin.

The signature was computed over the original payload.

They do not know the secret, so they cannot compute a matching signature for the new one.

When the server checks, the signature does not match, and the token is rejected.

Think of it as a wax seal on a letter: anyone can read the letter, but nobody can fake the seal.

### What does Abhyas's token code actually do?

Issuing:

```
claims = subject(userId), issuedAt(now), expiresAt(now + 8 hours)
jwt    = sign(header HS256, claims, secret)
```

Verifying a token from the cookie, in order:

1. Parse it. Garbage means "not logged in".
2. Check the algorithm is exactly HS256. (This blocks the old "alg: none" trick, where an attacker claims no signature is needed.)
3. Check the signature with the secret.
4. Check `exp` is in the future.
5. Return the user id from `sub`.

Any failure at all returns "empty", and the interceptor answers `401`.

The secret must be at least 32 bytes, and the app refuses to start without it.

### Where should the token be stored: localStorage or a cookie?

This choice decides how safe the login is.

- **localStorage**: JavaScript can read it. If an attacker injects a script into your page (**XSS**, cross-site scripting), they can read and steal the token.
- **Cookie with `HttpOnly`**: JavaScript cannot read it at all. An injected script cannot steal it.

But cookies bring a different danger.

The browser attaches cookies **automatically**, even if the request was triggered by another site's page. A malicious page could make your browser send "delete my account" with your cookie. That is **CSRF** (cross-site request forgery).

So Abhyas uses a cookie with three flags.

| Flag | What it does | Which attack it stops |
|---|---|---|
| `HttpOnly` | JavaScript cannot read the cookie | stealing the token through XSS |
| `SameSite=Strict` | the browser never sends it on requests that start from another site | CSRF |
| `Secure` | only sent over HTTPS (switched on outside localhost) | eavesdropping |

Because the Vite proxy makes everything one origin (chapter 6), `SameSite=Strict` works without side effects, and Spring's CSRF token machinery is deliberately turned off.

### What is the weakness of JWT, and how does Abhyas live with it?

You cannot easily "cancel" a signed token. If it leaks, it works until `exp`.

Abhyas keeps the risk small: the lifetime is 8 hours, there is no refresh token, and expiry simply sends the user to login and back to where they were.

For a bigger system you would use short-lived access tokens plus refresh tokens, or a revocation list.

### Authentication versus authorization

- **Authentication**: who are you? (the JWT answers this)
- **Authorization**: what may you touch? (the user-scoped queries answer this)

A valid login does not mean you can read every resume. Every query adds `WHERE user_id = me`.

> **Explain it to a stranger.** When you log in, the server hands your browser a stamped, tamper-proof note saying who you are, and it expires in 8 hours. The browser shows it on every click. Nobody can change the note without breaking the stamp, and the note is kept where websites' scripts cannot peek at it.
> **Explain it to an expert.** The password is stored as a salted BCrypt hash, with a per-login-id throttle. A successful login issues an HS256 JWT with `sub`, `iat` and `exp`, carried in an `HttpOnly`, `SameSite=Strict` cookie. A servlet interceptor verifies algorithm, signature and expiry, and sets the user id. JWT is stateless, so it scales horizontally without a session store, at the price of no instant revocation, which I limit with an 8-hour lifetime. HttpOnly addresses XSS token theft, SameSite=Strict addresses CSRF, and the single-origin proxy lets both work together.
> **In Abhyas.tech.** Cookie name `rdt_session`. Library: `nimbus-jose-jwt` (chosen over `jjwt` to avoid a second Jackson version next to Spring Boot). Account deletion needs the password again and deletes the `app_user` row, and the cascade removes everything else.

## How does the resume parser work?

### What is the problem? Why can't we just "read the text" of a PDF?

You look at a resume and see a heading, a job title, a date, three bullets.

It feels like text.

But a PDF is not a document of paragraphs. It is a set of **drawing instructions**: "put the characters `Ex` at position (72, 700) in bold 12 pt, put `perience` at (84, 700)".

A PDF was designed to look identical everywhere, not to be understood by a computer.

There is no tag saying "this is a heading" or "this is a job".

> **The root cause.** The root problem: a PDF stores where ink goes on a page, not what the words mean.

So the parser has to **reconstruct meaning from geometry**: where text sits, how big it is, whether it is bold.

### Where should the parsing happen: the browser or the server?

Think about what you are sending.

A resume has your name, phone, email, history. Very personal.

- If we parse in the **browser**, the PDF never leaves your laptop. No upload, no storage, no waiting for a server.
- If we parse on the **server**, we receive and handle sensitive files.

So how can we get the best of both?

Parse in the browser **first**, and use the server only as a backup when the browser's attempt looks poor.

That is exactly Abhyas's design.

Here is the journey from the PDF to a saved resume, and onwards to a report.

**[Diagram: Data flow](dataflow.html).** Left to right: input, parse, store, practice, output. The scrubbed fallback path only runs when the browser parse is incomplete.

### What are the steps inside the browser parser?

The parser is a copy of an open-source project called **OpenResume**, which is why the whole repo is under the AGPL-3.0 licence.

It is a pipeline of four small steps.

Each step solves one problem and hands a cleaner result to the next.

### Step 1: how do we get the text out? (`read-pdf`)

We use **PDF.js**, the library that Firefox uses to display PDFs.

It returns **text items**: little chunks of text, each with `x`, `y`, `width`, the font name and whether the line ends there (`hasEOL`).

That is our raw material: words with coordinates and fonts.

### Step 2: how do we make lines? (`group-text-items-into-lines`)

We have chunks. A human sees lines.

The parser walks the chunks: when a chunk says "end of line", it closes the current line.

But there is a catch.

Some PDFs break one word into several chunks (because of kerning or the tool that created them), like `Exper` and `ience`.

So the parser measures a **typical character width**, then merges neighbouring chunks if the gap between them is smaller than that width, adding a space only when it should.

> **Think of it like this.** Joining dotted beads into a string. If two beads are touching, they belong to the same word.

### Step 3: how do we find sections like Experience and Education? (`group-lines-into-sections`)

Here comes the key insight.

How do you, as a human, spot a section title without reading?

It sits alone on its line, and it looks louder: **bold** and often **ALL CAPS**.

So the rule is a heuristic (a smart guess):

- A line with a single chunk that is **bold and all uppercase** is a section title. This works for well-made resumes.
- As a weaker backup: a short title (two words at most) that starts with a capital and contains a keyword like experience, education, project or skill.

Every line after a title belongs to that section until the next title.

Lines before the first title are the **profile** (name, email, phone, location).

> **The root cause.** We cannot know the meaning, but we can know the habit: people make headings look different from body text.

### Step 4: how do we pull out the actual fields? (`extract-resume-from-sections`)

Now every section has its lines, and we need fields: name, email, school, degree, company, bullets.

Here the parser uses a **feature-scoring system**.

It is a way to pick the best answer when you cannot be sure.

For each candidate piece of text, you ask yes/no questions, and each "yes" adds or subtracts points.

- "Does it contain an `@`?" gives a big score for being the email.
- "Does it look like a phone number (digits, maybe dashes or brackets)?" gives a big score for being the phone.
- "Is it bold, near the top, all letters, no digits?" gives a score for being the name.
- "Does it contain a digit?" subtracts points from being a name.

The candidate with the **highest total score** wins.

Why is this clever? Because no single rule is reliable, but the **sum** of many weak signals is.

> **Think of it like this.** A detective. Nobody leaves a signed confession, but if the suspect was near the scene, has no alibi and owns the weapon, you are fairly sure.

Bullets are easy: lines that start with a bullet symbol become `descriptions`, and companies, titles and dates are found by the same scoring.

### Then what does Abhyas do with the raw result? (tidy)

OpenResume's output is rough, so Abhyas cleans it before showing it:

- Splits skill lines like "Languages: Java, C" into separate skills.
- Reads the real link URLs from the PDF's hidden link annotations.
- Splits a degree and its date when they came as one string.
- Puts a project title back if it got swapped with its technology line.
- Does not treat technology lines as resume points.

### How does the app decide the parse is bad?

A parser built on habits fails when a resume breaks the habits, for example a two-column layout, where reading left to right across the page mixes both columns into nonsense.

So Abhyas runs a **"looks incomplete"** test. The browser parse is judged poor when any of these is true:

- There is no name.
- There is neither experience nor projects.
- Fewer than 3 resume points in total.
- Low quality: an education entry with no school, or a project with no name.

If the parse is poor, the **fallback** runs.

### What is the fallback, and why does it need the server?

The browser result is not good enough, and rules cannot fix a two-column page.

What can? An AI model, which reads text more like a human.

But we promised the browser never calls an AI provider (keys would leak). So the PDF goes to our server just for this one request:

1. The server checks the file really is a PDF by its first bytes (`%PDF-`) and that it is at most 1 MB.
2. **Apache PDFBox** extracts the text and the hidden links.
3. A **scrubber** removes phone numbers and emails before anything leaves the server. The `[phone removed]` and `[email removed]` placeholders replace them.
4. The scrubbed text goes to **Groq** with temperature 0 and an instruction to return the same JSON shape, with `null` for anything unknown (so it cannot invent facts).
5. The reply is validated, and the PDF is **discarded**. Nothing is stored.

The phone and email are filled back in on our server from the original text, never given to the AI.

What if the AI is unavailable? The app keeps the browser's rough parse and asks the user to fix it.

Before 4 October 2026 it showed an empty form, which made the app unusable without AI. Good engineering means the app still works when its smartest part fails.

### Why is there a Review screen at all?

No parser, rule-based or AI, is perfect.

And everything downstream, every question and every mark, is built from this data.

> **The root cause.** Garbage in, garbage out. So we put a human in the loop at the cheapest possible moment: right after parsing, before anything is saved.

The user edits the fields in one form with collapsible sections.

Empty sections are opened and marked "Nothing found".

Once saved, a resume is **read-only** (except its interview date), so questions and results always match the resume they were built from.

### What happens when the user saves?

In one transaction:

1. Insert the `resume` row.
2. Insert one `resume_point` row per project bullet, experience bullet and skill.
3. Match skills to the shared `skill` table by case-insensitive name. No match means `skill_id` stays empty, and **no skill is ever created from a resume**, so the skills stay clean.

Then, after the save, one Groq call writes about 8 open questions about the projects and experience, each with 3 to 6 **key facts** (true statements used later for grading).

If AI is down, no questions are stored and the practice bank fills in. A retry happens later, at most once per resume every 10 minutes.

### What is the Role skills check?

You said you are aiming for "Backend Developer". Does your resume prove it?

The check takes each skill of the role, with its aliases ("Spring Boot" and "SpringBoot"), and looks for it as a **whole word, ignoring case**, in every resume point.

The output has two lists: skills with evidence (each linked to the resume point that shows it), and skills without. No AI is needed.

> **Explain it to a stranger.** A PDF only remembers where each word is drawn, not what it means. So the app looks at the position and style of the words: a bold, all-capital line alone on its row is probably a heading. If that guesswork fails, a smart assistant reads the text instead, with your phone number and email blacked out first. Then you double check the result.
> **Explain it to an expert.** The pipeline is PDF.js text items, then line grouping by EOL and gap-merging against a typical glyph width, then section grouping by typographic heading heuristics (bold and uppercase, with keyword fallback), then field extraction with a feature-scoring system that sums weak signals. A completeness test routes poor results to a server-side fallback: PDFBox text extraction, PII scrubbing, then an LLM at temperature 0 constrained to our JSON schema, with nulls for unknowns. A review step keeps a human in the loop, and the persisted resume is immutable so derived questions stay consistent.
> **In Abhyas.tech.** Limits and flow: PDF only (header-checked), at most 1 MB, parsed in browser first, fallback via `POST /api/resumes/parse-fallback`. The PDF and any audio are never stored. Known limit of the browser parser: single-column English resumes work best.

## How does the app use AI safely?

### Why use AI at all?

Some jobs rules cannot do well.

- Turn a messy resume into clean questions.
- Judge whether a free-text answer actually says "I added a cache and measured p95 latency".
- Write encouraging, specific feedback.
- Turn speech into text.

An **LLM** (large language model) can read and write language, so it fits these jobs.

But look at what an LLM costs us.

### What problems come with LLMs?

- **They can be wrong with confidence.** An LLM may invent a plausible fact or hand out generous marks.
- **They are not deterministic.** The same input can give different outputs.
- **They can fail.** Timeouts, bad JSON, and rate limits (free tiers say "429, slow down" often).
- **They cost money and time.** Each call is slow.
- **Privacy.** Whatever you send leaves your machine.
- **Secrets.** The API key must never reach users.

> **The root cause.** The root problem: an AI is a powerful but unreliable, slow, limited and external worker. We must wrap it so its mistakes cannot harm the product.

### So how can we solve each problem?

This is where the design of Abhyas becomes interesting. Each risk gets its own guard.

| Risk | Guard | How it works |
|---|---|---|
| Invented marks | **Java adds up the marks** | The AI only ticks yes/no on a checklist. Java counts the ticks. Marks are never taken from the AI. |
| Fake proof | **Evidence rule** | Every "yes" must include a quote. Java checks the quote really appears in the user's answer. If not, the tick flips to "no". |
| Randomness | **Temperature 0 and JSON only** | Ask for the most predictable output in a fixed structure, then validate it. |
| Bad JSON | **Validate and retry once** | If the reply does not parse, retry one time, then fall back. |
| Rate limits | **AiGate** | Stops hammering a provider that said "429", see below. |
| Provider down | **Fallback graders** | Keyword grader and template feedback take over. |
| Privacy | **Scrubber** | Phone and email are removed from every outgoing text. |
| Leaked keys | **Server-only calls** | Only the backend holds keys. The browser never calls a provider. |
| Slow calls | **Background grading** | The user never waits on the AI (chapter 12). |

> **In Abhyas.tech.** The grading rule in one sentence: the AI proposes ticks with quotes, Java checks the quotes and adds the marks.

### What is AiClient?

`AiClient` is the **one** place in the code that talks to AI providers.

That is deliberate: guards written once are guards that cannot be forgotten.

For every call it: removes phone and email, sets temperature 0, asks for JSON only, applies a timeout (20 s, and 40 s for audio), parses and validates the answer, and retries once on a 5xx, a timeout or invalid JSON.

Logs hold timings and error types, **never** the prompt or the reply.

### What is AiGate, and why is it called a circuit breaker?

Say Groq returns `429 Too Many Requests`.

If every one of the next 50 calls tries anyway, each fails slowly, the user waits, and you may be blocked longer.

So how can we solve this?

Remember the failure for a while, and answer "unavailable" **immediately** without calling.

That idea is the **circuit breaker pattern**, the same as the fuse in your house: when something is wrong, the circuit opens so the damage does not spread.

`AiGate` keeps, per provider (`groq-chat`, `groq-whisper`, `gemma`), the time until it is usable again.

- A per-minute limit: wait for the reply's `Retry-After` seconds (15 minutes if it gives none).
- A daily limit: wait until the next UTC midnight.

While unavailable, the app uses its fallbacks. State lives in memory and resets on restart.

### What is the fallback, and why is it silent?

Each AI job has a non-AI twin, so the product keeps working.

| Job | AI version | Fallback |
|---|---|---|
| Project questions | Groq | the practice bank, then generic templates |
| Grade brief and voice answers | Groq checklist | `KeywordGrader`, which ticks items by keyword match and uses the matching sentence as the quote |
| Feedback and report | Gemma | `TemplateFeedback`: ticks, key facts, "what to fix", numbers only |
| Voice to text | Groq Whisper | the browser's Web Speech API, or typing |

The switch has no banner, because the user came to practice, not to manage infrastructure.

But honesty matters, so each attempt stores `graded_by` as `java`, `ai` or `fallback`.

A keyword-graded answer says "Graded without AI... may be too strict... does not count toward mastery", with a **Regrade with AI** button.

### Which AI does what, and why two providers?

- **Groq** is fast: chat for parsing, questions and checklists, and Whisper for speech to text.
- **Google AI Studio (Gemma)** writes the kinder, longer feedback and the report.

Each has its own free-tier limits, and splitting the work spreads the load. Gemma calls are spaced 7 seconds apart for that reason.

### Why is the voice stuff built this way?

The browser records WebM/Opus audio for at most 3 minutes with `MediaRecorder` and uploads it. The server sends it to Whisper, counts words, duration, words per minute and filler words, and returns the transcript.

**Nothing is saved yet.** The audio is held only in memory or a temp file and deleted.

If Whisper fails, the voice question stays and the user speaks again using the browser's own **Web Speech API** (in Chrome, that audio goes to Google, and the privacy note says so). If there is no microphone, the user types the answer, graded like a brief answer for 5 marks.

> **Explain it to a stranger.** The AI is a clever but sometimes sloppy helper. So we never let it do the sums or make promises we cannot check. It must show a quote for every tick, our own code checks the quote, and our code adds up the marks. If the helper is tired or away, a simpler checker steps in.
> **Explain it to an expert.** The LLM is treated as an untrusted component. Output is constrained to JSON and validated, scores are computed deterministically in Java from boolean checklists, and an evidence rule requires each positive item to carry a verbatim quote that Java substring-matches against the answer, which cuts hallucinated credit. A per-provider circuit breaker (`AiGate`) handles 429s and daily quotas, and each AI-backed interface has a deterministic fallback implementation. PII is scrubbed at the single egress point.
> **In Abhyas.tech.** Three interfaces each with an AI and a fallback implementation: questions, grading and feedback. One `AiClient` for every call. Providers' model IDs live in config because they change often.

## What happens when I press Submit? Background work and async design

### What would happen if grading ran inside the Submit request?

Count the work.

- Java marks 5 multiple-correct and 5 fill-in-the-blank answers. Instant.
- Groq grades 9 brief and voice answers, one at a time, about a second or two each.
- Gemma writes feedback for each of them, with a 7-second gap between calls to respect its limit.
- Gemma writes the final report.

That is easily one to two minutes.

If the browser waits for that, the Submit button hangs. A mobile network blip kills the request and the user has no idea whether anything was graded. The server thread is also locked for all that time.

> **The root cause.** The root problem: a slow job is welded to a request that should answer in a blink.

### So how can we solve this?

Split the two things.

1. **Accept** the work and answer at once: "Got it, your session is completed."
2. **Do** the work later, on a separate worker, and let the client see the results when they are ready.

This is the **asynchronous job** pattern, and it is how emails, video uploads and payment processing work everywhere.

> **Think of it like this.** A coat check. You hand over your coat and immediately get a ticket. The attendant hangs the coat in the back. You come back whenever you like, and show the ticket.

Here is the exact conversation, step by step.

**[Diagram: Submit and grade sequence](sequence.html).** Read it top to bottom. The user is answered at step 3, long before the grading finishes.

### How do I read the sequence diagram?

1. The user sends `POST /submit`.
2. The API puts the session in a queue for the worker. (Dashed arrow: it is only a hand-off.)
3. The API replies **at once** with "completed". The user sees the report page with empty rows.
4. The worker, in the background, marks multiple-correct and blanks with plain Java.
5. For each brief and voice answer the worker asks Groq for a checklist with quotes. Groq replies. The worker checks the quotes and saves the score and mastery.
6. For each answer the worker asks Gemma for feedback, waiting 7 seconds between calls. Gemma answers with text, or with a `429`.
7. The worker saves "feedback ready" to the database.
8. Meanwhile the browser keeps asking `GET session` every few seconds and shows whichever rows are ready.

> **The root cause.** The most important design choice: the **database is the shared notebook**. The worker writes progress there, the browser reads progress from there. They never talk to each other directly.

### Why is the worker a single thread?

It looks like a limitation, but it is a decision.

Gemma's free tier allows very few calls per minute. Ten workers would trip the limit immediately.

One worker means calls go out **one at a time**, in order, with a deliberate pause. The code is simple, with no locks and no races.

The trade-off is throughput: two users submitting at the same moment wait in line. For a laptop-sized app that is perfectly fine.

> **Watch out.** An interviewer may ask "what happens if the server restarts mid-grading?" The answer: on startup, every completed session that still has a `pending` status is picked up and finished from where it stopped. That is why progress is stored in the database, not in memory.

### How is each type of answer marked?

- **Multiple-correct (2 marks).** `marks = max(0, 2 × (correct picked − wrong picked) ÷ correct options)`, rounded to the nearest half. Why subtract wrong picks? Otherwise a user could tick every box and get full marks. Examples: both right gives 2, one right plus one wrong gives 0, one right out of two gives 1.
- **Fill in the blank (2 marks).** Trim and ignore case, compare with the accepted wordings. One blank is worth 2, two blanks 1 each. A blank Java marked wrong is re-checked by one Groq call for **meaning**, so "REST controller" and `@RestController` can both pass.
- **Brief (5) and voice (10).** An AI-ticked checklist, the evidence rule, and Java summing the ticks.

### Why not let the AI give a mark out of 5?

Because that number would be a vibe.

Two runs could give 3 and 4 for the same answer, and you could never explain why.

A checklist of yes/no items with a quoted proof is **auditable**: you can point at every tick and say "this sentence earned it".

> **Explain it to a stranger.** Pressing Submit just hands your answers to a worker and tells you "received". The worker marks them in the background, one at a time, and your report page checks back every few seconds to show whatever is ready, like picking up your coat later with a ticket.
> **Explain it to an expert.** Submit is a command that transitions the session to completed and enqueues a grading job on a single-thread executor, returning immediately. The worker is idempotent and resumable, because per-attempt `graded_by`, `feedback_status` and `report_status` live in MySQL and a startup recovery task resumes any pending session. The client polls the resource, which is simple and stateless. The single thread is intentional back-pressure for rate-limited providers.

## How is a practice session built, and why do questions come back?

### What is the problem with a normal question list?

A fixed list of questions gives every user the same quiz forever.

- It does not know your resume.
- It does not know which questions you got wrong.
- Once you see the answer, you may memorise it without learning.

> **The root cause.** The root problem: practice that does not adapt wastes your time on what you already know and ignores what you do not.

### So how can we solve this?

Remember, for every question, how you did, and build each new session from that memory.

Weak questions are asked again first. Strong questions rest and return later.

This is the idea behind **spaced repetition**.

### What does one session look like, from start to report?

**[Diagram: Practice session workflow](workflow.html).** Three lanes: what the user does, what the backend does, and the fallback path that keeps grading alive.

Read it left to right.

1. **Start.** The user picks a resume on Home. The backend **builds 19 slots**, putting the unmastered questions first.
2. **Answer.** One question per screen. Each answer is saved when the user presses Next, so closing the tab loses nothing. There is no feedback during the session.
3. **Submit.** Returns at once (chapter 12).
4. **Grade.** Java first, then the AI. If the AI is down, the lower lane (keywords and templates) takes over, so **AI errors never block the report**.
5. **Report.** The page polls until it is ready.

### Why exactly 19 questions and 80 marks?

It is a fixed shape so sessions are comparable.

| Part | Questions | Marks each | Total |
|---|---|---|---|
| Multiple correct | 5 | 2 | 10 |
| Fill in the blank | 5 | 2 | 10 |
| Brief answer (3 technical, 2 behavioral, 1 about your project) | 6 | 5 | 30 |
| Voice answer (1 behavioral, 2 about your project) | 3 | 10 | 30 |
| **Total** | **19** | | **80** |

If the pool of unmastered and new questions is too small, the missing slots are never left empty. They are filled from the practice bank, then the generic templates, and finally resting questions.

### How does the order of questions get chosen?

For each part the builder takes:

1. **Unmastered** questions first: asked at least once and not mastered. Lowest last score first, then the one asked longest ago. A question that was only keyword-graded counts as 0, so it comes back early for a proper AI grade.
2. Remaining slots are filled randomly from **new** questions (never asked) and **rested** ones (mastered more than a month ago).

Mastered questions less than a month old are skipped, so you spend your time where it counts.

### What does "mastered" mean, and what is the lifecycle of a question?

**[Diagram: Mastery lifecycle](lifecycle.html).** A question moves right along the mastery path, or drops into the weak loop and is picked first next time.

Follow one question.

1. **New**: never asked.
2. **In a session**: you answered it.
3. **Graded**: the score is compared with a threshold for its type. The thresholds: multiple-correct 2 of 2, fill-in-the-blank 2 of 2, brief 4 of 5, voice 8 of 10 (a typed voice answer needs 4 of 5).
4. If it passes, it is **Mastered** and is skipped for a month. Then it is **Rested**, and eligible to return.
5. If it does not pass, it falls into the **weak loop** (Unmastered) and is picked first next time, until you master it.

Why does a mastered question come back at all? Because remembering fades.

A month later you may not defend that bullet any more, and an interviewer will not care that you could last month.

### Why is mastery stored per user, not per resume?

If you upload a second resume that also mentions Java, the bank question "What is lazy loading?" is the **same** question.

You already mastered it, so it should not appear again.

So the memory for bank questions lives in `bank_performance`, keyed by user and question.

Questions written for one specific resume project belong to that resume.

### Why do some answers not count toward mastery?

A fallback (keyword) grade can be too harsh or too generous.

If it could mark a question "mastered", a user might never see it again by luck.

So the rule: multiple-correct and blanks always count (they are marked by exact rules), but brief and voice count **only when graded by AI**. Fallback grades never set mastered.

### What is Interview mode?

The same engine with a different shape: 6 questions, spoken or typed, 3 minutes each, and after every answer the AI writes **one follow-up** from what you just said, like a real interviewer would.

The follow-up is written once and stored, so a page reload shows the same question. The timer lives only in the browser, and the server accepts an answer any time while the session is open.

> **Explain it to a stranger.** The app remembers how you did on every question. Questions you missed come back first, and questions you aced take a month's rest, so you spend your time where you are weak, like a good teacher who keeps returning to the topics you find hard.
> **Explain it to an expert.** Session construction is a deterministic, mastery-aware sampler with part quotas and a layered fallback for pool exhaustion. Mastery state is `bank_performance(user, bank_question)` for shared bank items and on the `question` row for resume-specific items, with a one-month rest window, which is a coarse spaced-repetition schedule. AI-dependent item types only update mastery when graded by the AI, so degraded grading cannot distort the schedule.

## Security, privacy, logging and testing: how do we trust it?

### What are the main ways this app could hurt a user?

Think like an attacker, then like a careless developer.

- Read someone else's resume. (**Authorization** bug.)
- Steal the login. (**XSS**, **CSRF**, weak storage.)
- Guess passwords. (**Brute force**.)
- Leak personal data to a third party. (**Privacy**.)
- Leak a secret key from the repo. (**Secrets**.)
- Put resume text in a log file that many people can read. (**Logging**.)

Each of these has a specific guard.

### What is "defence in depth"?

Never rely on one lock.

| Danger | First lock | Second lock |
|---|---|---|
| Reading another user's data | every query scoped by user id | another user's id returns 404, so even existence is hidden |
| Stolen cookie via XSS | `HttpOnly` | short 8-hour lifetime |
| CSRF | `SameSite=Strict` | one origin, no CORS |
| Password guessing | BCrypt slowness | login throttle and identical errors |
| PII to AI | scrubber on every call | resume points only, never the profile |
| Secret leakage | keys only in environment or a gitignored file | `.env.example` lists names, never values |
| Stored sensitive files | the PDF and audio are never stored | export strips phone and email |
| Data left after deletion | cascade deletes | a final check counts rows for the deleted user |

### What is not allowed in the logs, and why?

Logs are copied, searched, shipped to other tools, and read by many people.

So the rule is strict: logs may contain ids, endpoints, status codes, provider names, durations and error types. They may **never** contain resume text, answers, transcripts, prompts, AI replies, passwords, tokens or keys.

Even debug logging of AI requests is off. A unit test checks that the exception handler and `AiClient` do not put request bodies in messages.

### How do we test it?

| Layer | Tool | What it proves |
|---|---|---|
| Rules | JUnit | marks formula, blank matching, evidence rule, mastery rules, slot quotas |
| Database | Testcontainers with a real MySQL 8 | migrations run, generated columns, the unique open-session index, cascade deletes |
| AI layer | JUnit with a fake provider | bad JSON then retry then fallback, a 429 opens the gate, the scrubber works, no personal text in logs |
| Security | MockMvc | another user gets 404, logged out gets 401, login throttle, export only for completed sessions |
| Frontend | Vitest | the "looks incomplete" test, parse mapping, API error handling |
| Manual | you | a checklist after every build step, plus hand-grading about 20 answers against the AI |

Why a **real** MySQL in tests, not an in-memory fake?

Because the schema uses MySQL-specific features (generated columns, JSON, CHECK constraints). A fake database would pass tests that production would fail.

> **Watch out.** Be honest in interviews about what is not verified. At the time of writing, the V1 SQL had not been run against a real MySQL, and the new redesign had only been compiled, not viewed in a browser. Saying what you have not tested builds more trust than pretending.
> **Explain it to a stranger.** We assume things will go wrong and put several locks on every door. Even if one lock fails, the next one still protects your data, and we only keep what we truly need.
> **Explain it to an expert.** Isolation is enforced at the data-access layer (user-scoped queries plus 404-not-403 to avoid existence oracles), authentication uses an HttpOnly SameSite=Strict JWT cookie behind a same-origin proxy, input is validated at the edge, secrets come from the environment, outbound text is PII-scrubbed at a single egress point, and logs are content-free. Integration tests run against real MySQL via Testcontainers so DB-level invariants are actually exercised.

## System design: putting it all together

### What is system design, really?

Every chapter so far solved a small problem.

System design is the skill of asking, for a whole product: **what parts do we need, how do they talk, where does data live, and what breaks first when more people arrive?**

It is not a list of famous tools. It is a chain of "this hurts, so we add that".

### How should I think about any system design question? Five steps

1. **Clarify what it must do.** Who uses it, and what are the 3 to 5 key actions? (Abhyas: upload a resume, practise, get a marked report.)
2. **Clarify how well it must do it.** How many users? How fast? Can it lose data? Can it be down? These are the **non-functional** needs.
3. **Sketch the simplest thing that works.** Client, server, database.
4. **Find the bottleneck.** Which part is slow, expensive or fragile first?
5. **Fix that bottleneck, and say what it costs.** Then repeat from step 4.

> **The root cause.** Good designs grow in response to measured pain. Starting with Kafka and Kubernetes for 20 users is not engineering, it is decoration.

### What are the basic building blocks, and what problem does each solve?

| Block | The problem it solves |
|---|---|
| **Client** (browser, app) | the human needs an interface |
| **DNS** | humans use names, machines use IPs |
| **Server (API)** | the secrets, rules and shared data must live somewhere we trust |
| **Database** | data must survive crashes and be searchable and consistent |
| **Cache** (Redis, in-memory) | asking the database the same thing again and again is slow and costly |
| **Queue** (RabbitMQ, SQS, Kafka) | slow work must not block the user, and bursts need smoothing |
| **Worker** | something has to take jobs off the queue |
| **Load balancer** | one server cannot take all the traffic, and must not be a single point of failure |
| **CDN** | files (JS, CSS, images) are far from users, so serve copies from nearby |
| **Object storage** (S3) | big files do not belong in the database |
| **Rate limiter** | one user or a bug must not use up everyone's share |
| **Monitoring and logs** | you cannot fix what you cannot see |

### Which of these does Abhyas already have, and which does it skip on purpose?

Look back at the architecture diagram.

**[Diagram: Architecture](architecture.html).** The same picture as chapter 1. Now look at it as a system designer: what is there, and what is missing?

| Block | In Abhyas today | Why this is fine now |
|---|---|---|
| Client | React app | yes |
| Server | one Spring Boot process | one laptop-scale deployment |
| Database | one MySQL | every table is simple and small |
| Queue and worker | an in-process single-thread executor | tiny volume, and the database stores the progress |
| Cache | none (only the JPA first-level cache) | no read is slow yet |
| Load balancer, CDN | none | one server, localhost |
| Rate limiting | login throttle and AiGate, both in memory | resets on restart is accepted |
| Object storage | none | the PDF and audio are deliberately never stored |

### Now the interview twist: "What if it had 1,000,000 users?"

Do not panic and do not list tools. Walk it as a chain of pains.

**Pain 1: one server is not enough.**

Add more copies of the Spring Boot app behind a **load balancer**.

This works easily **because the app is stateless**: the login is a JWT carried by the client, not a session stored in server memory. Any copy can answer any request.

That is the payoff of the JWT decision from chapter 9.

**Pain 2: the in-memory pieces stop working.**

The login throttle and `AiGate` live in each server's memory. With 10 servers, each has its own count, so an attacker gets 10 times more guesses, and one server may hammer a provider that another already knows is limited.

Fix: move that shared state to **Redis**.

**Pain 3: the single-thread executor is a bottleneck and a single point of failure.**

If the server dies, queued grading waits (it is recoverable, but late). And one thread cannot grade thousands of sessions.

Fix: replace the in-process executor with a real **message queue** and a pool of **worker** machines.

The AI providers' rate limits now become the real cap, so workers need a **shared rate limiter** and a priority policy.

The report page can keep polling, or switch to SSE if updates need to be snappier.

**Pain 4: the database gets busy.**

- First **index** the slow queries and look at the query plans.
- Then add **read replicas** for read-heavy pages such as History.
- Cache the shared, rarely changing data: roles, skills, the practice bank.
- Much later, **shard** by user id. This is possible because every row traces back to a user.

**Pain 5: the AI bill and limits.**

Groq and Gemma have free-tier limits and cost money at scale.

- Cache results when the same question and answer repeat.
- Use the keyword grader first for obvious zeroes.
- Batch calls where the provider allows.
- Make the fallback quality good enough to keep users happy during throttling.

**Pain 6: the front door.**

Serve the static React build from a **CDN**, and put the API behind HTTPS with `Secure` cookies.

**Pain 7: seeing what is going on.**

Add metrics (latency, error rate, queue depth, AI success rate, fallback rate), centralised logs without personal content, and alerts.

> **The root cause.** Notice the pattern: each change fixes a named pain and costs something (complexity, money, consistency). Say both halves out loud.

### Which trade-offs should I be ready to discuss?

- **Consistency versus availability.** With one MySQL primary, writes are consistent. Replicas can lag a little, so "read your own write" pages (such as the report right after Submit) should read from the primary.
- **Polling versus push.** Polling is simplest and most robust. Push is snappier and costlier.
- **Stateless JWT versus server sessions.** JWT scales without a store but cannot be revoked instantly. Server sessions are revocable but need a shared store.
- **Synchronous versus asynchronous.** Async removes waiting but adds states (pending, ready) the UI must show.
- **Monolith versus microservices.** One deployable app is easier to build, test and debug. Split only when teams or scaling needs force it. Abhyas is a well-organised **modular monolith**, packaged by feature, so splitting later is possible.
- **Build versus buy.** The parser copies an open-source project (and accepts AGPL) instead of building one from scratch, and PDFBox plus an LLM are used as a safety net.

> **Explain it to a stranger.** System design is deciding which boxes a product needs and how they connect. You start with the smallest working set, and add more boxes only when something becomes too slow, too fragile or too busy.
> **Explain it to an expert.** Abhyas is a stateless modular monolith with a relational store, an in-process async worker for rate-limited provider calls, and durable job state in the database. To scale: horizontal API replicas behind a load balancer (enabled by JWT statelessness), shared state for throttling and gating in Redis, an external queue with worker autoscaling bounded by provider quotas, read replicas and caching for shared reference data, a CDN for static assets, and observability across latency, queue depth and fallback rate. Each step is driven by a measured bottleneck.

## The interview playbook

### How should I answer a question in an interview?

Use this shape. It matches how this guide works.

1. **Say what it is in one sentence.** (The stranger version.)
2. **Say why it exists**: the problem without it.
3. **Say how it works**, in three steps at most.
4. **Say where you used it** in Abhyas.tech.
5. **Say the trade-off or limit**, which shows depth.

Then stop. Let them ask. A short, clear answer that invites a follow-up beats a long lecture.

### What is my 2-minute pitch of the project?

"Abhyas.tech helps people prepare for interviews by defending their own resume.

You upload a PDF, and it is parsed in the browser, so your file never has to leave your laptop. If the parse looks incomplete, a server-side fallback uses PDFBox and an LLM, after removing phone and email.

You check the result, and the app writes questions from your own projects and experience, plus a reviewed bank.

A session has 19 questions worth 80 marks: multiple choice, blanks, written answers and spoken answers.

When you submit, the server answers immediately and grades in the background. The AI only ticks a checklist with quotes, Java verifies the quotes and adds the marks.

Weak questions come back first, and mastered ones rest for a month.

The stack is React, Spring Boot on Java 21 and MySQL, with a JWT in an HttpOnly cookie.

Every design choice follows one idea: the AI is powerful but unreliable, so the app must stay correct and useful without it."

### Which questions are likely, and how do I answer them?

**Q1. What happens when you type a URL and press Enter?**

DNS turns the name into an IP. The browser opens a TCP connection, and for HTTPS a TLS handshake. It sends an HTTP request. The server answers with HTML, and the browser then fetches the CSS and JavaScript the page needs, and runs them.

**Q2. What is the difference between the internet and the web?**

The internet is the network of networks (the roads). The web is a service on top of it (pages and data over HTTP).

**Q3. What is an API and what makes one RESTful?**

It is a contract for software to use software. REST means nouns in URLs, verbs as HTTP methods, JSON bodies, standard status codes, and stateless requests.

**Q4. PUT versus POST versus PATCH?**

POST creates or triggers an action and is not repeat-safe. PUT replaces and is idempotent. PATCH changes part. In Abhyas: `POST /sessions`, `PUT .../attempts/3`, `PATCH /resumes/1`.

**Q5. 401 versus 403 versus 404?**

401 means not authenticated. 403 means authenticated but not allowed. I return 404 for another user's data, so that I do not even confirm it exists.

**Q6. What is idempotency, and why care?**

Repeating the request leaves the same result, which makes retries after a network failure safe.

**Q7. What is JWT and how is it verified?**

A signed token in three parts: header, payload with claims, and signature. The server recomputes the signature using its secret, checks the algorithm, and checks `exp`. It is signed, not encrypted, so it must hold no secrets.

**Q8. Why a cookie and not localStorage?**

`HttpOnly` stops JavaScript, so XSS cannot steal the token. The cost is CSRF risk, handled by `SameSite=Strict` and a single origin.

**Q9. What is the weakness of JWT?**

No instant revocation. Mitigate with a short lifetime, refresh tokens and a deny-list when needed.

**Q10. Why hash passwords with BCrypt?**

One-way, salted so equal passwords differ, and deliberately slow to make guessing expensive.

**Q11. What is XSS? What is CSRF?**

XSS is an attacker running a script on your page, so it can read what JavaScript can read. CSRF is another site making your browser send an authenticated request. HttpOnly helps against the first, SameSite against the second.

**Q12. What is CORS and why does it exist?**

The same-origin policy blocks a page from reading responses of another origin. CORS lets a server allow specific origins. I avoid it by proxying through a single origin.

**Q13. Why React?**

It keeps the screen a function of the data, and updates only what changed, instead of hand-editing the DOM.

**Q14. What is state, and why did the screen re-render?**

State is data that changes over time. When it changes, React runs the component again and applies the difference.

**Q15. How do you get live updates, and why polling?**

Options are polling, long polling, SSE, WebSockets. I chose polling because the update is slow and one-way, the state is durable in the DB, and polling is stateless and restart-safe.

**Q16. Why a relational database?**

The data is relational and I need constraints, transactions and joins.

**Q17. What is a foreign key, an index and a transaction?**

Foreign key: a column that must point to an existing row. Index: a structure that makes lookups fast, with a cost on writes. Transaction: an all-or-nothing group of changes.

**Q18. How do you enforce "one open session per user"?**

In the database, not in code: a generated column plus a unique index, so even simultaneous requests cannot create two.

**Q19. Why Flyway?**

Versioned, repeatable schema changes that run on startup, so every machine has the same schema, and `ddl-auto=validate` fails fast on a mismatch.

**Q20. How does the resume parser work?**

PDF.js gives positioned text items. I group them into lines, split the lines into sections using heading heuristics (bold plus uppercase, keyword fallback), and extract fields with a feature-scoring system. If the result looks incomplete, a server fallback uses PDFBox and an LLM after PII scrubbing. The user reviews before saving.

**Q21. Why parse in the browser first?**

Privacy, cost and speed: the file stays local, and there is no server work for most resumes.

**Q22. How do you stop the LLM from lying about marks?**

It never produces marks. It ticks a checklist, each yes must quote the answer, Java verifies the quote with a substring match, and Java sums the ticks.

**Q23. What if the AI is down or rate-limited?**

`AiGate` is a circuit breaker per provider. The app falls back to keyword grading and template feedback, and labels those answers as graded without AI.

**Q24. Why is Submit asynchronous?**

Grading takes minutes because of AI calls and rate-limit spacing. I accept the work, return at once, run a single-thread worker, store progress in the database and let the page poll.

**Q25. What if the server restarts during grading?**

Progress is in MySQL. On startup any completed session with pending parts is resumed.

**Q26. Why a single-thread worker?**

Gemma's rate limit is tight, so one-at-a-time calls with a 7-second gap are simplest and safe. It trades throughput for simplicity.

**Q27. How does the app decide which questions to ask?**

Unmastered first (lowest score, longest ago), then random from new and rested. Mastered ones rest for a month. Missing slots fall back to the bank, templates and rested questions.

**Q28. Why is mastery per user?**

A bank question is the same question across all your resumes, so what you learned should carry over.

**Q29. How do you protect user data?**

Every query is scoped by user id, other users get 404, PII is scrubbed before AI calls, PDFs and audio are never stored, logs contain no content, and secrets are in the environment.

**Q30. How would you scale it?**

Stateless API replicas behind a load balancer, Redis for shared throttling and gate state, a real queue and worker pool, read replicas and caching for reference data, a CDN, and observability. Each is added only when a measured bottleneck demands it.

**Q31. What would you do differently?**

Honest, concrete answers: adopt TanStack Query for fetching, add refresh tokens or revocation, move throttling to a shared store, run the V1 SQL on a real MySQL early, and add end-to-end tests.

**Q32. What was the hardest design decision?**

Making AI unreliable-by-design: the evidence rule, Java-summed marks and silent fallbacks, so quality did not depend on any one provider.

### What are the follow-up traps, and what should I say?

- "Is a JWT encrypted?" **No. It is signed. Anyone can read the payload.**
- "Can you log a user out immediately?" **Not with a pure JWT. I rely on an 8-hour expiry, and would add a deny-list or sessions if instant logout mattered.**
- "Why not microservices?" **One team, one deployment, simple transactions. A modular monolith keeps the option open.**
- "Why not WebSockets?" **The need is one-way and slow, and polling is cheaper to run.**
- "Is the data safe from the AI companies?" **Phone and email are removed, only resume points are sent, nothing is stored by us. But the provider's free-tier data policy still needs to be checked, and I say so.**
- "Does your system always work?" **It degrades: with no AI, you still get questions, marks and templated feedback.**

### How do I practise switching between stranger and expert?

Pick any chapter, set a timer, and do three rounds.

1. **Ten-year-old round.** Explain it in 30 seconds with one analogy and no jargon.
2. **Colleague round.** Explain it in 90 seconds with the right terms and one trade-off.
3. **Interviewer round.** Explain it in 2 minutes: problem, mechanism, where you used it, limits, and what you would change.

If you get stuck, you have found the exact sentence to re-read.

> **Pause and think.** A final check for every topic: can I say why it exists without using the topic's own name?

## Glossary: quick definitions

| Term | Simple meaning |
|---|---|
| API | A menu of things one program lets another program ask for |
| REST | URLs are things, HTTP methods are actions |
| HTTP / HTTPS | The web's rulebook, and the private version |
| Stateless | The server does not remember you between requests |
| Cookie | A note the browser stores and sends back automatically |
| JWT | A signed, readable token with claims such as user and expiry |
| Hash / salt / BCrypt | One-way scramble / random extra per password / a slow safe password hash |
| XSS / CSRF | A malicious script on your page / a malicious site making your browser send a request |
| CORS / same-origin | Browser rule about reading other sites' responses, and its opt-in |
| Proxy | A middle server that forwards requests |
| Component / state | A reusable piece of screen / data that changes over time |
| Controller / service / repository | Takes requests / holds the rules / talks to the database |
| Transaction | An all-or-nothing group of database changes |
| Foreign key | A column that must point to an existing row |
| Index | A lookup shortcut that speeds reads |
| Migration | A versioned change to the database structure |
| Cascade | Deleting a parent also deletes its children |
| Idempotent | Doing it twice gives the same result as once |
| Polling / SSE / WebSocket | Keep asking / server pushes one way / two-way channel |
| Async job | Work done later, in the background, while the user gets an answer now |
| Thread / executor | A worker doing one thing / a pool or single worker that runs jobs |
| LLM | A large language model that reads and writes text |
| Temperature | How random an LLM's output is (0 is most predictable) |
| Hallucination | A confident but invented answer |
| Circuit breaker | Stop calling a failing service for a while, and answer "unavailable" at once |
| Fallback | A simpler backup that keeps the feature working |
| Evidence rule | A tick counts only if its quote is really in the answer |
| Mastered | A question whose mark reached its threshold, resting for a month |
| Spaced repetition | Reviewing things at growing gaps, weak ones more often |
| Heuristic | A smart rule of thumb that is usually right |
| Feature scoring | Adding up many weak signals to choose the best candidate |
| PII | Personal data such as phone and email |
| Monolith / microservices | One deployable app / many small independent services |
| Load balancer | Spreads requests over several servers |
| Cache | A fast copy of data that is slow to fetch |
| Queue | A waiting line for jobs |
| Horizontal / vertical scaling | More machines / a bigger machine |
