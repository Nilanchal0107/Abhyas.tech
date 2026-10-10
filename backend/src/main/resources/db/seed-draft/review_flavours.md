# Role-flavoured bank review (design, SQL and endpoint, frontend)

Check every question. Correct options are **bold**. Fix it in `bank_flavours.py`, run `python gen_flavours.py` again,
then move `V13__flavoured_bank.sql` to `db/migration/` (after `V12`).

## Backend / design: System Design

- [ ] **Design, medium:** Design a URL shortener like bit.ly. Walk me through how you would build it.
  - Key fact: Requirements: create a short link, redirect to the long URL, maybe expiry and custom aliases, with far more reads than writes
  - Key fact: API: a POST that creates a link and returns a short code, and a GET on the code that redirects with a 301 or 302
  - Key fact: Data model: a table that maps the short code to the long URL with owner and dates, with a unique index on the code
  - Key fact: Code generation: a counter encoded in base62, or random codes checked for collisions
  - Key fact: Scale: a cache in front of the database for hot links, and replicas or sharding by code
  - Key fact: Trade-offs: 301 is cacheable but hides click counts, 302 allows analytics; counter versus random codes

- [ ] **Design, medium:** Design a rate limiter for a public API.
  - Key fact: Requirements: who is limited (user, API key or IP), the rule (for example 100 requests a minute), and what happens on a breach
  - Key fact: API: a check that returns allow or reject, with a 429 status and a Retry-After header for rejected calls
  - Key fact: Data model: a counter or token bucket per key, held in a fast store such as Redis with an expiry
  - Key fact: Algorithms: fixed window, sliding window and token bucket, and how each handles bursts
  - Key fact: Scale: a shared store for many servers, atomic increments, and sharding keys
  - Key fact: Trade-offs: accuracy against memory and latency, and failing open or closed when the store is down

- [ ] **Design, medium:** Design a notification service that sends email, SMS and push messages.
  - Key fact: Requirements: channels, templates, user preferences, and the number of messages per second
  - Key fact: API: an endpoint that accepts a notification request and returns an id, and a call to look up its status
  - Key fact: Data model: notification, template, user preference and delivery attempt records
  - Key fact: A message queue with workers per channel, so sending is asynchronous and can be retried
  - Key fact: Scale and reliability: retries with backoff, idempotency keys against duplicates, and limits per provider
  - Key fact: Trade-offs: at-least-once against at-most-once delivery, and speed against cost

- [ ] **Design, hard:** Design a news feed for a social app where users follow other users.
  - Key fact: Requirements: posting, following, and a feed of recent posts from the people you follow
  - Key fact: API: create a post, follow and unfollow, and get the feed with cursor pagination
  - Key fact: Data model: users, follow edges, posts, and optionally a precomputed feed per user
  - Key fact: Fan-out on write precomputes feeds when someone posts; fan-out on read builds the feed when it is asked for
  - Key fact: Scale: cache hot feeds, and treat users with millions of followers differently
  - Key fact: Trade-offs: write cost against read latency, and freshness against cost

- [ ] **Design, hard:** Design a file storage service like a simple Dropbox, where users upload, download and share files.
  - Key fact: Requirements: upload, download, sharing, large files and size limits
  - Key fact: API: upload (in chunks or through a signed URL), download, and creating a share link
  - Key fact: Data model: metadata in a database (owner, name, size, version, permissions) and the file bytes in object storage
  - Key fact: Chunked and resumable uploads, with checksums to check integrity
  - Key fact: Scale: object storage, a CDN for downloads, and replication
  - Key fact: Trade-offs: whole files against chunks with deduplication, and strong against eventual consistency for metadata

- [ ] **Design, hard:** Design a chat application that supports one-to-one messages and group chats.
  - Key fact: Requirements: real-time send and receive, message history, delivery status and groups
  - Key fact: API: a WebSocket connection for live messages and a REST call for history
  - Key fact: Data model: users, conversations, members, and messages with an ordering id
  - Key fact: Real-time delivery through WebSockets and a message broker between servers
  - Key fact: Scale: shard conversations, store messages for offline users and send push notifications
  - Key fact: Trade-offs: ordering and delivery guarantees, and the cost of fan-out in large groups

## Backend / design: Object-Oriented Programming

- [ ] **Design, medium:** Design the classes for a parking lot system.
  - Key fact: Requirements: vehicle types, spots of different sizes, entry and exit, tickets and fees
  - Key fact: Main classes: ParkingLot, Level, Spot, Vehicle with subtypes, Ticket and a fee calculator
  - Key fact: Operations such as park(vehicle) returning a ticket and exit(ticket) returning the fee
  - Key fact: Relationships: a lot has levels, a level has spots, and a ticket links a vehicle, a spot and a start time
  - Key fact: Extensibility: a strategy for choosing a spot and another for pricing, so new rules do not change old classes
  - Key fact: Trade-offs: two cars taking the same spot (concurrency), and simple lists against indexed lookups

## Backend / design: Data Structures

- [ ] **Design, medium:** Design an LRU cache that supports get and put in constant time.
  - Key fact: Requirements: a fixed capacity, evict the least recently used entry, and get and put in O(1)
  - Key fact: Interface: get(key) and put(key, value)
  - Key fact: Data structure: a hash map from key to node, plus a doubly linked list ordered by recent use
  - Key fact: On get or put move the node to the front; on overflow remove the tail node and its map entry
  - Key fact: Concurrency: a lock or a concurrent structure, and what thread safety costs
  - Key fact: Trade-offs: the memory of the extra links, and other policies such as LFU or a time to live

## Backend / design: Object-Oriented Programming

- [ ] **Design, medium:** Design a library management system where members borrow and return books.
  - Key fact: Requirements: search books, borrow and return, due dates, fines, and members with limits
  - Key fact: Main classes: Book, BookCopy, Member, Loan, Library and a fine policy
  - Key fact: Operations such as borrow(member, copy), return(loan) and search(title)
  - Key fact: Relationships: a book has many copies, a loan links a member and a copy with a due date
  - Key fact: Extensibility: fine rules and notification channels can change without editing the loan code
  - Key fact: Trade-offs: reservations and waiting lists, and keeping the copy count consistent under concurrent borrowing

## Backend / design: Design Patterns

- [ ] **Design, hard:** Design a vending machine that accepts coins, lets the user pick a product and gives change.
  - Key fact: Requirements: products with prices and stock, coins, selection, change, and refunds
  - Key fact: Main classes: VendingMachine, Product, Inventory, Coin or Money and a state for each step
  - Key fact: Operations such as insertCoin(coin), selectProduct(code), dispense() and refund()
  - Key fact: The State pattern: idle, has money, dispensing and out of service, each with its own behaviour
  - Key fact: Extensibility: new coin types or payment methods through an interface
  - Key fact: Trade-offs: a state machine against flags and if statements, and what happens when change cannot be given

- [ ] **Design, hard:** Design a logging library with several levels and several outputs, such as console and file.
  - Key fact: Requirements: levels (debug, info, warn, error), several destinations, formatting and filtering by level
  - Key fact: Main classes: Logger, Level, Appender or Handler, and Formatter
  - Key fact: Operations such as log(level, message) and addAppender(appender)
  - Key fact: Patterns: Observer or a list of appenders that each receive the message, and Strategy for formatting
  - Key fact: Extensibility: a new destination is a new class behind an interface, with no change to Logger
  - Key fact: Trade-offs: synchronous against asynchronous writing, and the cost of formatting a message that is filtered out

## Backend / design: Object-Oriented Programming

- [ ] **Design, hard:** Design an elevator system for a building with several elevators.
  - Key fact: Requirements: floors, several elevators, call buttons on floors and inside, and a fair choice of elevator
  - Key fact: Main classes: Elevator, Floor, Request, Controller or Dispatcher and a Direction
  - Key fact: Operations such as requestElevator(floor, direction) and pressFloor(elevator, floor)
  - Key fact: Relationships: the controller owns the elevators and assigns each request to one of them
  - Key fact: Extensibility: the scheduling strategy (nearest car, direction first) can be swapped
  - Key fact: Trade-offs: simple nearest-car scheduling against throughput, and the state of each elevator (moving, idle, door open)

## Frontend / design: State Management

- [ ] **Design, medium:** Design a typeahead search box that shows suggestions while the user types.
  - Key fact: Requirements: suggestions after a few characters, keyboard navigation, and screen reader support
  - Key fact: Component API: props such as onSelect and a function that fetches suggestions, plus a debounce delay
  - Key fact: State model: query, results, the highlighted index, loading and error
  - Key fact: Debounce the input, cancel or ignore stale responses, and cache recent queries
  - Key fact: Accessibility: the combobox role, aria-activedescendant and arrow keys with Enter and Escape
  - Key fact: Trade-offs: filtering on the client against the server, and the debounce delay against responsiveness

## Frontend / design: Web Performance

- [ ] **Design, medium:** Design an infinite scrolling feed that stays fast with thousands of items.
  - Key fact: Requirements: load more as the user scrolls, keep scroll position, and show loading and error states
  - Key fact: Component API: a function that loads a page by cursor, and a render function for one item
  - Key fact: State model: the list of items, the next cursor, loading and an end-of-list flag
  - Key fact: Use an IntersectionObserver on a sentinel element instead of scroll events, and virtualise long lists
  - Key fact: Performance: stable keys, memoised items and image lazy loading
  - Key fact: Trade-offs: infinite scroll against pagination for accessibility and the footer, and memory against smooth scrolling

## Frontend / design: State Management

- [ ] **Design, hard:** Design a reusable data table component with sorting, filtering and pagination.
  - Key fact: Requirements: sortable columns, filters, pages, row selection and large data sets
  - Key fact: Component API: columns described as data, the rows, and callbacks for sort, filter and page change
  - Key fact: State model: sort key and direction, filters, page and page size, either controlled by the parent or held inside
  - Key fact: Client side processing for small data and server side processing for large data
  - Key fact: Accessibility: a real table element, sortable headers announced with aria-sort, and keyboard use
  - Key fact: Trade-offs: controlled against uncontrolled state, and a simple API against flexibility

- [ ] **Design, hard:** Design the front end of a multi-step checkout form with validation.
  - Key fact: Requirements: steps, validation on each step, going back without losing data, and a final review
  - Key fact: Component API: a list of steps, each with its fields and validation rules, and an onSubmit callback
  - Key fact: State model: the form values of all steps in one place, the current step, errors and a submitting flag
  - Key fact: Validate on blur and again on Next, show errors next to the field, and keep focus management sensible
  - Key fact: Persist progress (for example in session storage) and protect against double submit
  - Key fact: Trade-offs: one form state against a state per step, and a form library against hand written state

## Backend / code: SQL

- [ ] **Brief, easy:** Tables: customers(id, name, city) and orders(id, customer_id, total, created_at).
  
  Write a query that returns each customer's name and the total value of their orders, highest first, including customers who have no orders.
  - Key fact: Uses a LEFT JOIN from customers to orders so customers without orders stay in the result
  - Key fact: Joins on orders.customer_id = customers.id
  - Key fact: Adds up the order values with SUM and groups by the customer
  - Key fact: Shows 0 (for example with COALESCE) or NULL for customers with no orders
  - Key fact: Sorts the result by the total in descending order

- [ ] **Brief, medium:** Table: employees(id, name, salary, department_id).
  
  Write a query that returns the second highest distinct salary.
  - Key fact: Looks only at distinct salary values, so ties for the top salary are not counted twice
  - Key fact: Skips the highest salary, for example with a subquery on MAX(salary), DENSE_RANK() = 2, or LIMIT 1 OFFSET 1 on distinct values
  - Key fact: Returns the salary value itself
  - Key fact: Returns NULL or no row when there is no second salary
  - Key fact: Uses valid syntax for the database it names

- [ ] **Brief, easy:** Table: users(id, email).
  
  Write a query that finds every email that appears more than once, with how many times it appears.
  - Key fact: Groups the rows by email with GROUP BY
  - Key fact: Filters the groups with HAVING COUNT(*) > 1, not with WHERE
  - Key fact: Selects the email and the count
  - Key fact: Mentions that email comparison may need LOWER() or trimming to catch near duplicates
  - Key fact: Uses COUNT(*) or COUNT(id) correctly

- [ ] **Brief, medium:** Table: employees(id, name, salary, department_id).
  
  Write a query that lists the employees who earn more than the average salary of their own department.
  - Key fact: Computes an average per department, with a subquery, a join to a grouped query, or AVG() OVER (PARTITION BY department_id)
  - Key fact: Compares each employee's salary with the average of the same department, not the company average
  - Key fact: Returns the employee columns (such as name and salary)
  - Key fact: Uses a join condition or window function correctly so each employee appears once
  - Key fact: Mentions that the comparison is strictly greater than

- [ ] **Brief, hard:** Table: sales(id, category, product, units).
  
  Write a query that returns the top 3 products by units sold in each category.
  - Key fact: Ranks products inside each category with ROW_NUMBER, RANK or DENSE_RANK and PARTITION BY category
  - Key fact: Orders the ranking by units in descending order
  - Key fact: Filters the rank to 3 or less in an outer query or CTE, because window functions cannot be used in WHERE directly
  - Key fact: Returns category, product and units
  - Key fact: Explains how ties are treated (RANK and DENSE_RANK keep ties, ROW_NUMBER does not)

- [ ] **Brief, medium:** Table: users(id, email).
  
  Write a statement that deletes duplicate rows with the same email and keeps the row with the smallest id.
  - Key fact: Finds the row to keep for each email with MIN(id) grouped by email
  - Key fact: Deletes only the other rows, for example with a self join, DELETE ... WHERE id NOT IN (SELECT MIN(id) ...), or ROW_NUMBER
  - Key fact: Does not delete every row of a duplicated email
  - Key fact: Knows that some databases (MySQL) need the subquery wrapped or a join, because you cannot select from the table you delete from directly
  - Key fact: Suggests running the select first or using a transaction before deleting

- [ ] **Brief, hard:** Tables: users(id, name) and orders(id, user_id, created_at).
  
  Write a query that returns the users who ordered in January 2026 but did not order in February 2026.
  - Key fact: Filters orders to the January 2026 date range with a clean range (created_at >= '2026-01-01' AND created_at < '2026-02-01')
  - Key fact: Excludes users who have an order in February 2026, with NOT EXISTS, NOT IN or a LEFT JOIN with IS NULL
  - Key fact: Returns each user once, using DISTINCT or EXISTS
  - Key fact: Joins orders to users on orders.user_id = users.id
  - Key fact: Avoids functions on created_at that stop an index being used, or explains the choice

## Backend / code: REST APIs

- [ ] **Brief, easy:** Design the endpoints of a todo list API: create, list, update and delete a task. For each give the method, the path, the main status codes and the request body.
  - Key fact: POST /tasks creates a task and returns 201 with the new task
  - Key fact: GET /tasks lists tasks, with pagination and an optional filter
  - Key fact: PUT or PATCH /tasks/{id} updates a task and returns 200, or 404 when it does not exist
  - Key fact: DELETE /tasks/{id} returns 204
  - Key fact: Validates the body (400 or 422 with a clear error) and requires authentication (401 or 403)

- [ ] **Brief, hard:** Design an endpoint that transfers money between two accounts. Give the method, the path, the request and response, and the failure cases.
  - Key fact: POST /transfers with the source account, the target account, the amount and an idempotency key
  - Key fact: Runs in one database transaction, so both balances change or neither does
  - Key fact: Validates the amount and the accounts, and fails with 422 or 409 when the balance is too low
  - Key fact: The idempotency key makes a retried request return the first result instead of moving money twice
  - Key fact: Checks that the caller owns the source account (401 or 403) and returns 201 with the transfer id

- [ ] **Brief, medium:** Design a paginated endpoint that lists products with a category filter and sorting.
  - Key fact: GET /products with limit and a page number or a cursor as query parameters
  - Key fact: Returns the items with the next cursor or the total count in the response
  - Key fact: Filters and sorting through query parameters, with the sortable fields limited to an allowed list
  - Key fact: Uses a stable order (sort on a unique column as a tie-breaker) so pages do not repeat or skip items
  - Key fact: Caps the limit and sets sensible defaults, and mentions keyset against offset pagination

## Backend / code: Authentication

- [ ] **Brief, hard:** Design the login and token refresh endpoints for a web app.
  - Key fact: POST /auth/login takes the credentials and returns 200 with a token or sets a cookie; wrong credentials return 401 without saying which part was wrong
  - Key fact: Passwords are stored hashed with a slow algorithm such as bcrypt, never as plain text
  - Key fact: A short-lived access token plus a longer-lived refresh token, with POST /auth/refresh to get a new access token
  - Key fact: Rate limiting or lockout on repeated failed logins
  - Key fact: Logout invalidates the refresh token, and everything runs over HTTPS

## Backend / code: REST APIs

- [ ] **Brief, medium:** Design an endpoint for uploading a profile picture.
  - Key fact: POST or PUT /users/{id}/avatar with multipart/form-data
  - Key fact: Validates the file type and the size, and answers 400, 413 or 415 when they are wrong
  - Key fact: Stores the file in object storage (not in the database) and saves its URL
  - Key fact: Only the owner of the profile (or an admin) may upload, otherwise 401 or 403
  - Key fact: Returns 200 or 201 with the new picture URL

- [ ] **Brief, medium:** Design the endpoints to follow and unfollow another user, and to list a user's followers.
  - Key fact: PUT /users/{id}/follow (or POST) to follow and DELETE on the same path to unfollow, both returning 204
  - Key fact: Following twice does not create a duplicate, so the call is idempotent
  - Key fact: Unknown user gives 404, and following yourself gives 400 or 409
  - Key fact: GET /users/{id}/followers returns a paginated list
  - Key fact: All of them require authentication

## Frontend / standard: JavaScript

- [ ] **MC, easy:** What does this code log?
  ```js
  console.log(typeof null, typeof undefined, typeof [])
  ```
  - A. **object undefined object**
  - B. null undefined array
  - C. object undefined array
  - D. null object object

- [ ] **MC, medium:** What does this code log?
  ```js
  for (var i = 0; i < 3; i++) setTimeout(() => console.log(i), 0)
  ```
  - A. 0 1 2
  - B. **3 3 3**
  - C. 2 2 2
  - D. Nothing

- [ ] **MC, medium:** What does this code log?
  ```js
  for (let i = 0; i < 3; i++) setTimeout(() => console.log(i), 0)
  ```
  - A. **0 1 2**
  - B. 3 3 3
  - C. undefined undefined undefined
  - D. Nothing

- [ ] **MC, medium:** What does this code log?
  ```js
  console.log(0.1 + 0.2 === 0.3, '5' + 3, '5' - 3)
  ```
  - A. true 53 2
  - B. **false 53 2**
  - C. false 8 2
  - D. true 8 53

- [ ] **MC, hard:** Which statements about closures are true?
  - A. **A closure keeps access to the variables of the function it was created in, even after that function has returned**
  - B. A closure copies the values of the outer variables at the moment it is created
  - C. **A function that returns an inner function which increments a private counter is a closure**
  - D. Closures only work with var

- [ ] **Blank, easy:** An async function always returns a ________.
  Accepted: Promise

## Frontend / standard: CSS

- [ ] **MC, easy:** Which CSS centers a child both horizontally and vertically inside a flex container?
  - A. **`display: flex; justify-content: center; align-items: center;`**
  - B. `display: block; margin: auto;`
  - C. `display: flex; text-align: center;`
  - D. `position: center;`

- [ ] **MC, easy:** In a flex container with `flex-direction: row`, which property aligns the items along the cross axis (vertically)?
  - A. `justify-content`
  - B. **`align-items`**
  - C. `flex-wrap`
  - D. `gap`

- [ ] **MC, medium:** Which statements about CSS Grid are true?
  - A. **`grid-template-columns: repeat(3, 1fr)` creates three equal columns**
  - B. Grid can only lay out items in one dimension
  - C. **`gap` sets the spacing between rows and columns**
  - D. Grid items must use `position: absolute`

- [ ] **MC, medium:** What does `box-sizing: border-box` change?
  - A. **The width and height include the padding and the border**
  - B. Margins stop collapsing
  - C. The element becomes a flex container
  - D. The border is removed

- [ ] **MC, hard:** Which values of `position` take an element out of the normal document flow?
  - A. **`absolute`**
  - B. **`fixed`**
  - C. `relative`
  - D. `static`

- [ ] **Blank, easy:** In the CSS box model, the space between the content and the border is called ________.
  Accepted: padding

## Frontend / standard: React

- [ ] **MC, medium:** What is the problem with this effect?
  ```jsx
  useEffect(() => {
    fetchUser(id).then(setUser)
  }, [])
  ```
  - A. **The effect does not run again when `id` changes, because `id` is missing from the dependency array**
  - B. `setUser` cannot be called inside an effect
  - C. `useEffect` cannot call code that returns a Promise
  - D. The effect runs before the first render

- [ ] **MC, medium:** A button calls `add` once. What is `n` after the next render?
  ```jsx
  const [n, setN] = useState(0)
  const add = () => { setN(n + 1); setN(n + 1) }
  ```
  - A. 0
  - B. **1**
  - C. 2
  - D. 3

- [ ] **MC, medium:** Which statements about hooks are true?
  - A. **Hooks must be called at the top level of a component, not inside conditions or loops**
  - B. Hooks can be called from any regular JavaScript function
  - C. **A custom hook is a function whose name starts with `use` and which may call other hooks**
  - D. A state setter changes the state variable immediately in the same render

- [ ] **MC, hard:** Why does this effect run in an endless loop?
  ```jsx
  const [items, setItems] = useState([])
  useEffect(() => { setItems([...items, 1]) }, [items])
  ```
  - A. **It updates state that is in its own dependency list, so every update triggers it again**
  - B. `useEffect` always runs in a loop
  - C. State setters are asynchronous
  - D. Arrays cannot be stored in state

- [ ] **MC, easy:** Which is the correct way to update one property of an object held in state?
  - A. `state.name = 'Ada'; setState(state)`
  - B. **`setState({ ...state, name: 'Ada' })`**
  - C. `setState(state.name = 'Ada')`
  - D. `state.setName('Ada')`

- [ ] **Blank, easy:** To run cleanup code when a component unmounts, return a ________ from useEffect.
  Accepted: function, cleanup function

- [ ] **Blank, easy:** A list rendered with map needs a stable ________ prop on each item so React can track it.
  Accepted: key

- [ ] **Blank, medium:** useMemo caches a computed ________, while useCallback caches a function.
  Accepted: value, result

- [ ] **Brief, medium:** Explain what the dependency array of useEffect does, and one mistake people make with it.
  - Key fact: it lists the values the effect uses, and React runs the effect again after a render where one of them changed
  - Key fact: an empty array runs the effect once after the first render, and leaving out a value used inside gives stale values
  - Key fact: a new object or function created on every render in the array makes the effect run every time, and the cleanup runs before the next run and on unmount

- [ ] **Brief, hard:** What is a stale closure in a React component, and how do you avoid it?
  - Key fact: a function created in an earlier render keeps the old values of props or state it closed over
  - Key fact: it shows up in timers, event handlers and effects that run later with outdated values
  - Key fact: avoid it with correct dependency arrays, functional updates such as setN(n => n + 1), or a ref for the latest value

## Frontend / standard: JavaScript

- [ ] **Brief, medium:** Explain the JavaScript event loop and why a long-running loop freezes the page.
  - Key fact: JavaScript runs on one thread, with a call stack for the code that is running now
  - Key fact: finished timers, events and promise callbacks wait in queues (microtasks first) until the stack is empty
  - Key fact: a long synchronous loop keeps the stack busy, so no click, timer or rendering can run until it ends

