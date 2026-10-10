# Practice bank review, wave 1: skills of the Software Development and AI roles

Check every answer. Correct options are **bold**. Fix a question in the `bank_*.py` file named in its heading, run `python gen_bank.py`
again, then move `V8__starter_bank.sql` to `db/migration/`.

## JavaScript (bank_fullstack.py)

- [ ] **MC, easy:** Which of these values are falsy in JavaScript?
  - A. **0**
  - B. **The empty string**
  - C. The text 0 (a string holding the digit zero)
  - D. **null**
- [ ] **MC, easy:** Which of these are primitive types in JavaScript?
  - A. **string**
  - B. **number**
  - C. object
  - D. **boolean**
- [ ] **MC, easy:** Which array methods return a new array instead of changing the original?
  - A. **map**
  - B. **filter**
  - C. push
  - D. **slice**
- [ ] **MC, medium:** Which statements about let and const are true?
  - A. **Both are block-scoped**
  - B. **A const variable cannot be reassigned**
  - C. The properties of an object held in a const cannot be changed
  - D. A let variable can be read before its declaration line and gives undefined
- [ ] **MC, medium:** Which statements about == and === are true?
  - A. **=== compares value and type without converting**
  - B. **== converts types before comparing**
  - C. **null == undefined is true**
  - D. NaN === NaN is true
- [ ] **MC, medium:** Which statements about arrow functions are true?
  - A. **They do not have their own this**
  - B. **They cannot be called with new as constructors**
  - C. They have their own arguments object
  - D. **With a one-expression body they return its value without the return keyword**
- [ ] **MC, medium:** Which statements about Promises are true?
  - A. **A Promise is pending, fulfilled or rejected**
  - B. **then returns a new Promise**
  - C. **Promise.all rejects as soon as one of its Promises rejects**
  - D. A fulfilled Promise can later become rejected
- [ ] **MC, hard:** Which statements about the JavaScript event loop are true?
  - A. **Promise callbacks (microtasks) run before the next setTimeout callback**
  - B. A setTimeout of 0 ms runs its callback before the current code finishes
  - C. **Long synchronous code stops other callbacks from running**
  - D. **await pauses only the async function it is in, not the whole thread**
- [ ] **MC, hard:** Which statements about prototypes in JavaScript are true?
  - A. **Objects inherit properties through their prototype chain**
  - B. **class syntax is built on prototypes**
  - C. The prototype of every object is null
  - D. **Object.create(proto) makes an object whose prototype is proto**
- [ ] **MC, hard:** Which statements about this in JavaScript are true?
  - A. **In a method called as obj.f(), this is obj**
  - B. **bind returns a new function with this fixed**
  - C. **In strict mode, this is undefined in a plain function call**
  - D. this always refers to the function itself
- [ ] **Blank, easy:** The ________ keyword declares a block-scoped variable that can be reassigned.  
  Accepted: let
- [ ] **Blank, easy:** The array method ________ makes a new array from the result of calling a function on every element.  
  Accepted: map
- [ ] **Blank, easy:** JSON.________ turns a JSON string into a JavaScript value.  
  Accepted: parse
- [ ] **Blank, medium:** The ________ operator compares two values without converting their types.  
  Accepted: ===, strict equality, triple equals
- [ ] **Blank, medium:** The ________ keyword pauses an async function until a Promise settles.  
  Accepted: await
- [ ] **Blank, medium:** The ________ syntax (three dots) copies the items of an array or the properties of an object into a new one.  
  Accepted: spread, spread operator
- [ ] **Blank, medium:** Promise.________ waits for every Promise and reports each result, fulfilled or rejected.  
  Accepted: allSettled
- [ ] **Blank, hard:** A function that keeps access to the variables of the scope it was created in is called a ________.  
  Accepted: closure
- [ ] **Blank, hard:** Moving declarations to the top of their scope before the code runs is called ________.  
  Accepted: hoisting
- [ ] **Blank, hard:** Delaying a function until the calls to it have stopped for a set time is called ________.  
  Accepted: debouncing, debounce
- [ ] **Brief, easy:** What is the difference between let, const and var?
  - Key fact: var is function-scoped, while let and const are block-scoped
  - Key fact: const cannot be reassigned, but an object it holds can still be changed
  - Key fact: let and const cannot be used before their declaration line, while var gives undefined there
  - Keywords (grading without AI): block, function, scope, reassign, hoisting
- [ ] **Brief, medium:** How do Promises and async/await relate to each other?
  - Key fact: async/await is syntax built on top of Promises
  - Key fact: an async function always returns a Promise
  - Key fact: await pauses the async function until the Promise settles, and errors are caught with try/catch
  - Keywords (grading without AI): promise, async, await, then, catch
- [ ] **Brief, medium:** What is a closure, and when is it useful?
  - Key fact: a closure is a function that keeps access to the variables of the scope it was created in
  - Key fact: it keeps that access even after the outer function has returned
  - Key fact: it is useful for private state, callbacks and functions that build other functions
  - Keywords (grading without AI): closure, scope, outer, private, callback
- [ ] **Brief, hard:** How does the JavaScript event loop handle asynchronous code?
  - Key fact: JavaScript runs one call stack on a single thread
  - Key fact: finished async work queues a callback, and the event loop runs it when the stack is empty
  - Key fact: microtasks such as Promise callbacks run before macrotasks such as setTimeout
  - Keywords (grading without AI): call stack, queue, microtask, macrotask, thread
- [ ] **Brief, hard:** How does prototypal inheritance work in JavaScript?
  - Key fact: each object has a link to a prototype object
  - Key fact: a property the object lacks is looked up along the prototype chain
  - Key fact: class syntax is a cleaner way to set up the same prototypes
  - Keywords (grading without AI): prototype, chain, inherit, class, Object.create

## React (bank_fullstack.py)

- [ ] **MC, easy:** Which statements about React are true?
  - A. **Components can hold state**
  - B. **Props are passed from a parent to a child**
  - C. Hooks may be called inside loops and conditions
  - D. **Changing state triggers a re-render**
- [ ] **MC, easy:** Which statements about JSX are true?
  - A. **It lets you write HTML-like markup in JavaScript**
  - B. **It is compiled to JavaScript function calls**
  - C. Browsers run JSX directly without a build step
  - D. **JavaScript expressions go inside curly braces**
- [ ] **MC, easy:** Which of these are built-in React hooks?
  - A. **useState**
  - B. **useEffect**
  - C. **useContext**
  - D. useFetch
- [ ] **MC, medium:** Why does React need a key prop on the items of a list?
  - A. **To tell which items changed, were added or were removed**
  - B. **To keep each item's state with the right item when the list is reordered**
  - C. To style each item differently
  - D. To make the list render in a fixed order
- [ ] **MC, medium:** Which statements about controlled form inputs are true?
  - A. **The input's value comes from React state**
  - B. **An onChange handler updates that state**
  - C. React cannot read the value of a controlled input
  - D. **The state is the single source of truth for the value**
- [ ] **MC, medium:** Which statements about React Context are true?
  - A. **It passes data down the tree without props at every level**
  - B. **A Provider supplies the value**
  - C. **Components read it with useContext**
  - D. It removes the need for any local state
- [ ] **MC, medium:** Which make a React function component re-render?
  - A. **Its state changes**
  - B. **Its parent re-renders**
  - C. **A context it reads changes**
  - D. A plain local variable inside it is changed
- [ ] **MC, hard:** Which statements about useEffect are true?
  - A. **The effect runs after the component renders**
  - B. **An empty dependency array runs the effect only after the first render**
  - C. **The cleanup function runs before the effect runs again and when the component unmounts**
  - D. Effects run during rendering, before the DOM is updated
- [ ] **MC, hard:** Which statements about React.memo, useCallback and useMemo are true?
  - A. **React.memo skips re-rendering a component when its props have not changed**
  - B. **useCallback keeps the same function between renders until its dependencies change**
  - C. Wrapping every value in useMemo always makes an app faster
  - D. **Passing a new inline function as a prop can defeat React.memo**
- [ ] **MC, hard:** Which statements about reconciliation in React are true?
  - A. **React compares the new element tree with the previous one**
  - B. **Only the changed parts of the real DOM are updated**
  - C. **An element of a different type replaces the whole subtree under it**
  - D. React rebuilds the whole real DOM on every state change
- [ ] **Blank, easy:** The ________ hook adds local state to a function component.  
  Accepted: useState
- [ ] **Blank, easy:** A React component's name must start with an ________ letter so JSX treats it as a component.  
  Accepted: uppercase, upper-case, capital
- [ ] **Blank, easy:** In JSX, the HTML attribute class is written as ________.  
  Accepted: className
- [ ] **Blank, medium:** Data passed from a parent component to a child component is called ________.  
  Accepted: props, properties
- [ ] **Blank, medium:** Moving shared state to the closest common parent of the components that need it is called lifting state ________.  
  Accepted: up
- [ ] **Blank, medium:** The ________ hook points at a DOM element or keeps a value whose change does not re-render the component.  
  Accepted: useRef
- [ ] **Blank, medium:** Passing props through many components that do not use them is called prop ________.  
  Accepted: drilling
- [ ] **Blank, hard:** The ________ hook keeps a computed value between renders and recomputes it only when its dependencies change.  
  Accepted: useMemo
- [ ] **Blank, hard:** The ________ hook manages complex state with a reducer function and dispatched actions.  
  Accepted: useReducer
- [ ] **Blank, hard:** Loading a component's code only when it is first needed, with React.lazy and Suspense, is called code ________.  
  Accepted: splitting
- [ ] **Brief, easy:** What is the difference between props and state in React?
  - Key fact: props are passed in by the parent and are read-only in the child
  - Key fact: state belongs to the component and is changed with its setter
  - Key fact: new props or a state change re-render the component
  - Keywords (grading without AI): props, state, parent, read-only, re-render
- [ ] **Brief, medium:** When would you use useEffect, and what is its dependency array for?
  - Key fact: useEffect runs side effects such as data fetching or subscriptions after render
  - Key fact: the dependency array decides when the effect runs again
  - Key fact: the cleanup function removes subscriptions or timers so they do not leak
  - Keywords (grading without AI): side effect, dependency, cleanup, fetch, render
- [ ] **Brief, medium:** Why should the items of a list in React have a stable key?
  - Key fact: keys let React match items between renders
  - Key fact: a stable key keeps each item's state and DOM with the right item
  - Key fact: the array index as key causes bugs when items are reordered, added or removed
  - Keywords (grading without AI): key, index, reorder, identity, state
- [ ] **Brief, hard:** How would you find and fix a React component that re-renders too often?
  - Key fact: measure first, for example with the React DevTools Profiler
  - Key fact: keep state close to where it is used and avoid new objects or functions in props on every render
  - Key fact: use React.memo, useMemo or useCallback where the profiler shows a real cost
  - Keywords (grading without AI): profiler, memo, useCallback, useMemo, state
- [ ] **Brief, hard:** Compare React Context with a state library such as Redux. When would you choose each?
  - Key fact: Context passes values down the tree and suits data that rarely changes, such as the theme or the signed-in user
  - Key fact: every component that reads a context re-renders when its value changes
  - Key fact: a store such as Redux adds predictable updates, middleware and dev tools for large shared state that changes often
  - Keywords (grading without AI): context, redux, store, re-render, global

## Node.js (bank_fullstack.py)

- [ ] **MC, easy:** Which statements about Node.js are true?
  - A. **It runs JavaScript outside the browser**
  - B. **It is built around an event loop**
  - C. Every JavaScript statement runs on its own thread
  - D. **npm manages its packages**
- [ ] **MC, easy:** Which of these are core Node.js modules?
  - A. **fs**
  - B. **http**
  - C. **path**
  - D. jquery
- [ ] **MC, easy:** Which statements about require and import in Node.js are true?
  - A. **require belongs to CommonJS modules**
  - B. **import belongs to ES modules**
  - C. **Setting type to module in package.json makes .js files ES modules**
  - D. require and import can never be used in the same project
- [ ] **MC, medium:** Which statements about npm are true?
  - A. **package.json lists a project's dependencies**
  - B. **package-lock.json records the exact installed versions**
  - C. npm install --production also installs devDependencies
  - D. **npm run starts scripts defined in package.json**
- [ ] **MC, medium:** Which statements about Node.js streams are true?
  - A. **They handle data piece by piece instead of loading it all into memory**
  - B. **pipe connects a readable stream to a writable stream**
  - C. Streams work only with files
  - D. **HTTP requests and responses are streams**
- [ ] **MC, medium:** Which statements about errors in asynchronous Node.js code are true?
  - A. **A rejected Promise that nobody handles raises an unhandledRejection**
  - B. **try/catch around await catches a rejected Promise**
  - C. **Error-first callbacks get the error as their first argument**
  - D. try/catch around a setTimeout call catches errors thrown inside its callback
- [ ] **MC, medium:** Which are good places for secrets such as API keys in a Node.js app?
  - A. **Environment variables**
  - B. **A .env file that is not committed to Git**
  - C. Constants in the source code
  - D. **A secrets manager**
- [ ] **MC, hard:** Which statements about Express middleware are true?
  - A. **Middleware runs in the order it is registered**
  - B. **A middleware calls next() to hand over to the next one**
  - C. **Error-handling middleware takes four arguments**
  - D. Middleware can only run after the route handler
- [ ] **MC, hard:** Which statements about the Node.js event loop are true?
  - A. **process.nextTick callbacks run before Promise callbacks**
  - B. **File system work can run on the libuv thread pool**
  - C. **A CPU-heavy loop on the main thread delays every other request**
  - D. Each incoming HTTP request gets its own JavaScript thread by default
- [ ] **MC, hard:** Which help a Node.js server use all CPU cores or run CPU-heavy work?
  - A. **The cluster module**
  - B. **Worker threads**
  - C. **Several processes behind a load balancer**
  - D. More await calls inside the CPU-heavy function
- [ ] **Blank, easy:** Node.js runs JavaScript on the ________ engine, the same engine Chrome uses.  
  Accepted: V8
- [ ] **Blank, easy:** The Node.js module ________ reads and writes files.  
  Accepted: fs
- [ ] **Blank, easy:** The command npm ________ puts the packages listed in package.json into node_modules.  
  Accepted: install, i
- [ ] **Blank, medium:** Node.js code that waits for input or output uses a ________ model, so the main thread is not blocked.  
  Accepted: non-blocking, asynchronous, async
- [ ] **Blank, medium:** In Express, req.________ holds route parameters, such as the id in /users/:id.  
  Accepted: params
- [ ] **Blank, medium:** The ________ class lets a Node.js object emit named events that listeners react to.  
  Accepted: EventEmitter
- [ ] **Blank, medium:** In Express, app.________() adds middleware that runs for every request.  
  Accepted: use
- [ ] **Blank, hard:** In Node.js, process.________ reads environment variables such as a database password.  
  Accepted: env
- [ ] **Blank, hard:** Node.js uses the ________ library for its event loop and thread pool.  
  Accepted: libuv
- [ ] **Blank, hard:** To run CPU-heavy JavaScript without blocking the event loop, Node.js offers ________ threads.  
  Accepted: worker
- [ ] **Brief, easy:** What is Node.js, and why does it suit servers that wait a lot on input and output?
  - Key fact: Node.js runs JavaScript outside the browser on the V8 engine
  - Key fact: it uses a single-threaded event loop with non-blocking I/O
  - Key fact: one process can serve many connections while it waits for databases or files
  - Keywords (grading without AI): V8, event loop, non-blocking, I/O, thread
- [ ] **Brief, medium:** What is middleware in Express, and how does it work?
  - Key fact: middleware is a function that gets the request, the response and next
  - Key fact: middleware runs in the order it is registered and calls next to continue
  - Key fact: it is used for logging, parsing bodies, authentication and error handling
  - Keywords (grading without AI): middleware, next, request, order, error
- [ ] **Brief, medium:** How do you handle errors in asynchronous Node.js code?
  - Key fact: with async/await, wrap awaited calls in try/catch
  - Key fact: with Promises, add a catch handler and never leave a rejection unhandled
  - Key fact: in Express, pass the error to next so an error-handling middleware answers
  - Keywords (grading without AI): try, catch, reject, next, unhandled
- [ ] **Brief, hard:** Why can a CPU-heavy task hurt a Node.js server, and how would you fix it?
  - Key fact: JavaScript runs on one main thread, so heavy computation blocks the event loop
  - Key fact: while it is blocked, no other request is handled
  - Key fact: move the work to worker threads, a separate process or a job queue
  - Keywords (grading without AI): block, event loop, worker, queue, CPU
- [ ] **Brief, hard:** When would you use streams in Node.js instead of reading data all at once?
  - Key fact: streams process data in chunks instead of loading it all into memory
  - Key fact: they suit large files, uploads and passing responses through
  - Key fact: pipe handles backpressure, so a fast source does not overwhelm a slow destination
  - Keywords (grading without AI): stream, chunk, memory, pipe, backpressure

## REST APIs (bank_fullstack.py)

- [ ] **MC, easy:** Which HTTP status codes mean a problem with the client's request?
  - A. **400**
  - B. **401**
  - C. **404**
  - D. 500
- [ ] **MC, easy:** Which HTTP methods usually send data in the request body?
  - A. **POST**
  - B. **PUT**
  - C. **PATCH**
  - D. GET
- [ ] **MC, easy:** Which HTTP status codes mean success?
  - A. **200**
  - B. **201**
  - C. **204**
  - D. 302
- [ ] **MC, medium:** Which HTTP methods are idempotent by definition?
  - A. **GET**
  - B. **PUT**
  - C. POST
  - D. **DELETE**
- [ ] **MC, medium:** Which statements about the status codes 401 and 403 are true?
  - A. **401 means the request has no valid authentication**
  - B. **403 means the user is known but not allowed**
  - C. 403 means the server crashed
  - D. **Logging in can fix a 401**
- [ ] **MC, medium:** Which are common ways to version a REST API?
  - A. **A version in the path, such as /v1/orders**
  - B. **A version in a header**
  - C. **A version in a query parameter**
  - D. Changing the meaning of fields without notice
- [ ] **MC, medium:** Which statements about PUT and PATCH are true?
  - A. **PUT replaces the whole resource**
  - B. **PATCH changes only the fields it sends**
  - C. **PUT is idempotent**
  - D. PATCH must always create a new resource
- [ ] **MC, hard:** Which statements about REST API design are true?
  - A. **URLs should name resources, such as /orders/42**
  - B. **PATCH applies a partial update to a resource**
  - C. **Each request should carry everything needed to handle it (statelessness)**
  - D. Every action should be a POST to one /api endpoint
- [ ] **MC, hard:** Which statements about CORS are true?
  - A. **Browsers enforce it, not servers**
  - B. **The server allows other origins with the Access-Control-Allow-Origin header**
  - C. **Some requests trigger a preflight OPTIONS request**
  - D. It stops tools such as curl from calling the API
- [ ] **MC, hard:** Which make a POST request safe to retry?
  - A. **An idempotency key sent by the client**
  - B. **The server storing the result for that key**
  - C. **Returning the stored result when the same key comes again**
  - D. Asking clients never to retry
- [ ] **Blank, easy:** A REST API usually answers with the status code ________ when a resource was created.  
  Accepted: 201, 201 Created
- [ ] **Blank, easy:** The HTTP status code ________ means the requested resource was not found.  
  Accepted: 404, 404 Not Found
- [ ] **Blank, easy:** REST APIs most often send data in the ________ format.  
  Accepted: JSON
- [ ] **Blank, medium:** The ________ header tells the server the format of the request body, such as application/json.  
  Accepted: Content-Type
- [ ] **Blank, medium:** The status code ________ means the request worked and the response has no content.  
  Accepted: 204, 204 No Content
- [ ] **Blank, medium:** The status code ________ tells a client it sent too many requests and should slow down.  
  Accepted: 429, 429 Too Many Requests
- [ ] **Blank, medium:** The ________ header usually carries a bearer token for authentication.  
  Accepted: Authorization
- [ ] **Blank, hard:** Returning a large result in smaller chunks, with limit and offset or a cursor, is called ________.  
  Accepted: pagination, paging
- [ ] **Blank, hard:** Before some cross-origin requests, the browser sends a ________ OPTIONS request to check that they are allowed.  
  Accepted: preflight
- [ ] **Blank, hard:** HATEOAS means responses include ________ to related actions and resources.  
  Accepted: links, hyperlinks
- [ ] **Brief, easy:** What makes an API RESTful?
  - Key fact: resources are named by URLs and acted on with HTTP methods
  - Key fact: each request is stateless and carries everything it needs
  - Key fact: responses use standard status codes and a format such as JSON
  - Keywords (grading without AI): resource, HTTP method, stateless, status code, JSON
- [ ] **Brief, medium:** What is the difference between PUT, PATCH and POST?
  - Key fact: POST creates a resource or starts an action and is not idempotent
  - Key fact: PUT replaces a whole resource and is idempotent
  - Key fact: PATCH changes only some fields of a resource
  - Keywords (grading without AI): POST, PUT, PATCH, idempotent, replace
- [ ] **Brief, medium:** How would you design the error responses of a REST API?
  - Key fact: use the right status code: 4xx for client errors, 5xx for server errors
  - Key fact: return a consistent JSON body with an error code and a readable message
  - Key fact: never leak stack traces or internal details
  - Keywords (grading without AI): status code, 4xx, 5xx, consistent, message
- [ ] **Brief, hard:** How would you add pagination to an endpoint that returns a long list?
  - Key fact: offset and limit is simple but gets slow and unstable on large or changing data
  - Key fact: cursor pagination points at the last item seen and stays fast and stable
  - Key fact: return the next page's link or cursor and cap the page size
  - Keywords (grading without AI): offset, limit, cursor, page size, next
- [ ] **Brief, hard:** How would you change a REST API without breaking existing clients?
  - Key fact: put a version in the path or a header, such as /v1
  - Key fact: add fields instead of removing or changing existing ones, so changes stay backward compatible
  - Key fact: keep the old version running for a while and announce its deprecation
  - Keywords (grading without AI): version, /v1, backward compatible, deprecate, header

## SQL (bank_fullstack.py)

- [ ] **MC, easy:** Which SQL statements change the data in a table?
  - A. **INSERT**
  - B. **UPDATE**
  - C. **DELETE**
  - D. SELECT
- [ ] **MC, easy:** Which of these are SQL aggregate functions?
  - A. **COUNT**
  - B. **SUM**
  - C. **AVG**
  - D. CONCAT
- [ ] **MC, easy:** Which statements about a primary key are true?
  - A. **It identifies each row uniquely**
  - B. **It cannot be NULL**
  - C. A table can have several primary keys
  - D. **It can span more than one column**
- [ ] **MC, medium:** Which statements about SQL joins are true?
  - A. **An INNER JOIN returns only rows that match in both tables**
  - B. **A LEFT JOIN keeps every row of the left table**
  - C. A CROSS JOIN needs an ON condition
  - D. An INNER JOIN keeps the unmatched rows of both tables
- [ ] **MC, medium:** Which statements about indexes are true?
  - A. **They can speed up WHERE and JOIN lookups**
  - B. **They slow inserts and updates down a little**
  - C. Every column should be indexed
  - D. **A unique index stops duplicate values**
- [ ] **MC, medium:** Which statements about NULL in SQL are true?
  - A. **NULL = NULL is not true**
  - B. **IS NULL checks for a missing value**
  - C. **COUNT(column) skips NULL values**
  - D. NULL is the same as an empty string
- [ ] **MC, medium:** Which statements about GROUP BY are true?
  - A. **It groups rows that have the same values in the listed columns**
  - B. **Aggregates such as SUM are computed per group**
  - C. **HAVING can filter on an aggregate**
  - D. WHERE can filter on an aggregate such as SUM(total)
- [ ] **MC, hard:** Which statements about database transactions are true?
  - A. **Atomicity means all statements of a transaction succeed or none do**
  - B. **COMMIT makes the changes of a transaction permanent**
  - C. ROLLBACK undoes changes that were already committed
  - D. **Isolation controls what running transactions see of each other's changes**
- [ ] **MC, hard:** Which statements about transaction isolation problems are true?
  - A. **A dirty read sees changes another transaction has not committed**
  - B. **A non-repeatable read gets different values when it reads the same row twice**
  - C. **A phantom read sees new rows appear in a repeated range query**
  - D. SERIALIZABLE allows dirty reads
- [ ] **MC, hard:** Which can make a query slow even with an index on the column?
  - A. **Wrapping the column in a function in the WHERE clause**
  - B. **A LIKE pattern that starts with a wildcard**
  - C. **Comparing the column with a value of another type that forces a conversion**
  - D. Looking a row up by its primary key
- [ ] **Blank, easy:** The ________ clause filters the rows of a SELECT statement.  
  Accepted: WHERE
- [ ] **Blank, easy:** The ________ BY clause sorts the result of a query.  
  Accepted: ORDER
- [ ] **Blank, easy:** SELECT ________ removes duplicate rows from a result.  
  Accepted: DISTINCT
- [ ] **Blank, medium:** The ________ clause filters groups after GROUP BY has been applied.  
  Accepted: HAVING
- [ ] **Blank, medium:** A ________ key is a column that points at the primary key of another table.  
  Accepted: foreign
- [ ] **Blank, medium:** The ________ statement shows how the database plans to run a query.  
  Accepted: EXPLAIN, EXPLAIN ANALYZE
- [ ] **Blank, medium:** A ________ JOIN returns every row of both tables, matched where possible.  
  Accepted: FULL OUTER, FULL
- [ ] **Blank, hard:** Splitting data into related tables to remove duplication is called ________.  
  Accepted: normalization, normalisation, database normalization
- [ ] **Blank, hard:** A ________ index holds every column a query needs, so the table itself is not read.  
  Accepted: covering
- [ ] **Blank, hard:** A subquery that refers to the current row of the outer query is called a ________ subquery.  
  Accepted: correlated
- [ ] **Brief, easy:** What is the difference between WHERE and HAVING?
  - Key fact: WHERE filters rows before grouping
  - Key fact: HAVING filters groups after GROUP BY
  - Key fact: HAVING can use aggregate functions such as COUNT or SUM, WHERE cannot
  - Keywords (grading without AI): WHERE, HAVING, GROUP BY, aggregate, filter
- [ ] **Brief, medium:** Explain the different types of SQL JOIN.
  - Key fact: INNER JOIN returns only rows that match in both tables
  - Key fact: LEFT JOIN keeps every row of the left table and fills missing matches with NULL
  - Key fact: FULL OUTER JOIN keeps unmatched rows of both sides, and CROSS JOIN pairs every row with every row
  - Keywords (grading without AI): INNER, LEFT, OUTER, CROSS, NULL
- [ ] **Brief, medium:** How do indexes speed up queries, and what do they cost?
  - Key fact: an index is a sorted structure, usually a B-tree, that finds rows without scanning the whole table
  - Key fact: it speeds up WHERE, JOIN and ORDER BY on the indexed columns
  - Key fact: it takes extra space and slows down inserts, updates and deletes
  - Keywords (grading without AI): index, B-tree, scan, write, space
- [ ] **Brief, hard:** What does ACID mean for database transactions?
  - Key fact: Atomicity: all statements of a transaction succeed or none do
  - Key fact: Consistency and Isolation: data stays valid and running transactions do not see each other's partial work
  - Key fact: Durability: committed changes survive a crash
  - Keywords (grading without AI): atomicity, consistency, isolation, durability, commit
- [ ] **Brief, hard:** How would you find and fix a slow SQL query?
  - Key fact: read the query plan with EXPLAIN to spot full scans and bad row estimates
  - Key fact: add or fix indexes on the columns used in WHERE, JOIN and ORDER BY
  - Key fact: rewrite the query to avoid functions on indexed columns, SELECT * and one query per row (N+1)
  - Keywords (grading without AI): EXPLAIN, index, scan, plan, rewrite

## MongoDB (bank_fullstack.py)

- [ ] **MC, easy:** Which statements about MongoDB are true?
  - A. **It stores documents in a JSON-like format**
  - B. **Collections group documents**
  - C. It needs a fixed schema for every collection
  - D. **Documents can contain nested documents**
- [ ] **MC, easy:** Which shell methods insert documents in MongoDB?
  - A. **insertOne**
  - B. **insertMany**
  - C. findOne
  - D. deleteOne
- [ ] **MC, easy:** Which statements about a MongoDB document are true?
  - A. **It is made of field and value pairs**
  - B. **Documents in one collection can have different fields**
  - C. Every document must have exactly the same fields as the others
  - D. **A field can hold an array**
- [ ] **MC, medium:** Which of these are MongoDB query or update operators?
  - A. **$gt**
  - B. **$set**
  - C. **$in**
  - D. $join
- [ ] **MC, medium:** Which statements about embedding and referencing in MongoDB are true?
  - A. **Embedding keeps related data in one document and reads it in one query**
  - B. **Referencing stores the _id of another document**
  - C. Embedding suits data that grows without limit, such as every log entry of a user
  - D. **Referencing suits data shared by many documents**
- [ ] **MC, medium:** Which of these are aggregation pipeline stages?
  - A. **$match**
  - B. **$group**
  - C. **$project**
  - D. $select
- [ ] **MC, medium:** Which statements about MongoDB replica sets are true?
  - A. **They keep copies of the data on several servers**
  - B. **One member is the primary that takes writes**
  - C. **If the primary fails, a secondary can be elected primary**
  - D. Every member takes writes at the same time by default
- [ ] **MC, hard:** Which statements about MongoDB indexes and aggregation are true?
  - A. **The _id field is indexed by default**
  - B. **A $match early in a pipeline cuts the documents later stages handle**
  - C. **$lookup joins documents from another collection**
  - D. An aggregation pipeline can have only one stage
- [ ] **MC, hard:** Which statements about sharding in MongoDB are true?
  - A. **It splits a collection across several servers**
  - B. **A shard key decides which shard holds a document**
  - C. A shard key with few distinct values spreads data evenly
  - D. **mongos routes queries to the right shards**
- [ ] **MC, hard:** Which statements about multi-document transactions in MongoDB are true?
  - A. **They work on replica sets and sharded clusters**
  - B. **A write to a single document is already atomic**
  - C. **They cost more than single-document writes**
  - D. They make schema design unimportant
- [ ] **Blank, easy:** In MongoDB, a group of documents is stored in a ________.  
  Accepted: collection
- [ ] **Blank, easy:** The method db.users.________() returns all documents that match a filter.  
  Accepted: find
- [ ] **Blank, easy:** MongoDB is a ________ database, so it does not store data in SQL tables.  
  Accepted: NoSQL, document, document-oriented
- [ ] **Blank, medium:** Every MongoDB document has a unique ________ field that acts as its primary key.  
  Accepted: _id
- [ ] **Blank, medium:** The update operator ________ adds a value to an array field.  
  Accepted: $push, $addToSet
- [ ] **Blank, medium:** In the aggregation pipeline, the ________ stage groups documents and computes values such as sums.  
  Accepted: $group
- [ ] **Blank, medium:** An index on two or more fields is called a ________ index.  
  Accepted: compound
- [ ] **Blank, hard:** MongoDB stores documents in ________, a binary form of JSON.  
  Accepted: BSON
- [ ] **Blank, hard:** A group of MongoDB servers holding the same data, with one primary, is a replica ________.  
  Accepted: set
- [ ] **Blank, hard:** The ________ key decides how a sharded collection is split across shards.  
  Accepted: shard
- [ ] **Brief, easy:** How is MongoDB different from a relational database such as MySQL?
  - Key fact: MongoDB stores flexible JSON-like documents in collections instead of rows in tables
  - Key fact: documents in one collection do not need the same fields
  - Key fact: related data is often embedded in one document instead of joined across tables
  - Keywords (grading without AI): document, collection, schema, embed, table
- [ ] **Brief, medium:** When would you embed data in a MongoDB document, and when would you reference it?
  - Key fact: embed data that is read together and belongs to one parent, such as the items of an order
  - Key fact: reference data that many documents share or that grows without limit
  - Key fact: a document is limited to 16 MB, so arrays that grow without limit should not be embedded
  - Keywords (grading without AI): embed, reference, 16 MB, array, shared
- [ ] **Brief, medium:** How do indexes work in MongoDB, and how do you check that a query uses one?
  - Key fact: an index keeps field values in order so a query does not scan every document
  - Key fact: a compound index covers several fields, and the order of the fields matters
  - Key fact: explain() shows whether a query used an index (IXSCAN) or scanned the collection (COLLSCAN)
  - Keywords (grading without AI): index, compound, explain, IXSCAN, COLLSCAN
- [ ] **Brief, hard:** Explain the MongoDB aggregation pipeline with an example.
  - Key fact: documents flow through stages, and each stage changes the stream
  - Key fact: common stages are $match to filter, $group to aggregate and $project to shape fields
  - Key fact: a $match early in the pipeline can use indexes and cuts the work of later stages
  - Keywords (grading without AI): pipeline, stage, $match, $group, $project
- [ ] **Brief, hard:** How does MongoDB scale reads and writes?
  - Key fact: replica sets copy data to secondaries for failover and can serve reads
  - Key fact: sharding splits a collection across servers by a shard key to scale writes
  - Key fact: a good shard key has many distinct values and spreads writes evenly
  - Keywords (grading without AI): replica set, shard, shard key, secondary, scale

## HTML (bank_fullstack.py)

- [ ] **MC, easy:** Which of these are semantic HTML elements?
  - A. **nav**
  - B. **article**
  - C. div
  - D. **footer**
- [ ] **MC, easy:** Which of these are HTML tags used to build lists?
  - A. **ul**
  - B. **ol**
  - C. **li**
  - D. list
- [ ] **MC, easy:** Which statements about links in HTML are true?
  - A. **The a tag makes a link**
  - B. **The href attribute holds the destination**
  - C. **target set to _blank opens the link in a new tab**
  - D. The src attribute holds a link's destination
- [ ] **MC, medium:** Which statements about HTML forms are true?
  - A. **Each input should have a label linked to it**
  - B. **The required attribute stops the form from being sent while the field is empty**
  - C. **A button inside a form submits it by default**
  - D. The placeholder attribute fully replaces a label
- [ ] **MC, medium:** Which of these are real input types in HTML?
  - A. **email**
  - B. **date**
  - C. **number**
  - D. phone
- [ ] **MC, medium:** Which statements about headings are true?
  - A. **A page should use headings in order, h1 then h2 and so on**
  - B. **Screen reader users jump between headings**
  - C. A heading level should be chosen for its default size
  - D. **HTML has six heading levels**
- [ ] **MC, medium:** Which statements about the label element are true?
  - A. **Its for attribute matches the id of an input**
  - B. **Clicking the label focuses the input**
  - C. **Screen readers read it as the name of the input**
  - D. It is only decoration
- [ ] **MC, hard:** Which statements about loading scripts are true?
  - A. **A script with defer runs after the HTML is parsed, in document order**
  - B. **A script with async runs as soon as it has downloaded, in no fixed order**
  - C. **A plain script in the head blocks HTML parsing while it loads and runs**
  - D. defer and async work only on inline scripts
- [ ] **MC, hard:** Which statements about data attributes are true?
  - A. **They are written as data- followed by a name**
  - B. **JavaScript reads them through element.dataset**
  - C. They are a good place to keep passwords
  - D. **CSS can select elements by them**
- [ ] **MC, hard:** Which help a page look good in search results and link previews?
  - A. **A descriptive title element**
  - B. **A meta description**
  - C. **Open Graph meta tags**
  - D. Putting all text inside images
- [ ] **Blank, easy:** The ________ attribute of an img tag gives text for screen readers and for images that fail to load.  
  Accepted: alt
- [ ] **Blank, easy:** An HTML page starts with the ________ declaration so the browser uses standards mode.  
  Accepted: <!DOCTYPE html>, DOCTYPE, doctype
- [ ] **Blank, easy:** The ________ tag makes a hyperlink.  
  Accepted: a, <a>, anchor
- [ ] **Blank, medium:** The ________ element holds the main navigation links of a page.  
  Accepted: nav
- [ ] **Blank, medium:** The ________ attribute gives an element a name that must be unique on the page.  
  Accepted: id
- [ ] **Blank, medium:** The ________ element wraps the main content of a page.  
  Accepted: main
- [ ] **Blank, medium:** The ________ attribute of an input shows hint text until the user types.  
  Accepted: placeholder
- [ ] **Blank, hard:** The ________ meta tag with width=device-width makes a page scale correctly on phones.  
  Accepted: viewport
- [ ] **Blank, hard:** Setting the ________ attribute of an img to lazy delays loading until the image is near the viewport.  
  Accepted: loading
- [ ] **Blank, hard:** The ________ attribute of an img lists image files of different widths for the browser to choose from.  
  Accepted: srcset
- [ ] **Brief, easy:** What is semantic HTML, and why does it matter?
  - Key fact: semantic elements such as header, nav, main and article say what the content is
  - Key fact: screen readers and search engines use them to understand the page
  - Key fact: they make the code easier to read than nested div elements
  - Keywords (grading without AI): semantic, nav, article, screen reader, div
- [ ] **Brief, medium:** How do you build an accessible HTML form?
  - Key fact: give every input a label linked with for and id
  - Key fact: use the right input types and attributes such as required
  - Key fact: show errors as text next to the field, not only with colour
  - Keywords (grading without AI): label, for, required, type, error
- [ ] **Brief, medium:** What is the difference between block and inline elements?
  - Key fact: block elements such as div and p start on a new line and take the full width
  - Key fact: inline elements such as span and a sit inside a line and take only the width they need
  - Key fact: width and height do not apply to plain inline elements
  - Keywords (grading without AI): block, inline, div, span, width
- [ ] **Brief, hard:** How do async and defer change the way scripts load?
  - Key fact: a plain script blocks HTML parsing while it downloads and runs
  - Key fact: defer downloads in parallel and runs after parsing, in document order
  - Key fact: async downloads in parallel and runs as soon as it arrives, in any order
  - Keywords (grading without AI): async, defer, parse, block, order
- [ ] **Brief, hard:** How would you make images load fast and look sharp on every screen?
  - Key fact: use srcset and sizes so the browser picks an image of the right size
  - Key fact: compress images and use modern formats such as WebP or AVIF
  - Key fact: lazy-load images below the fold and set width and height to avoid layout shift
  - Keywords (grading without AI): srcset, WebP, lazy, compress, layout shift

## CSS (bank_fullstack.py)

- [ ] **MC, easy:** Which of these are CSS layout methods?
  - A. **Flexbox**
  - B. **Grid**
  - C. **Float**
  - D. Gridflex
- [ ] **MC, easy:** Which are valid ways to write a colour in CSS?
  - A. **#ff0000**
  - B. **rgb(255, 0, 0)**
  - C. **red**
  - D. colour(red)
- [ ] **MC, easy:** Which of these CSS units are relative?
  - A. **em**
  - B. **rem**
  - C. **%**
  - D. px
- [ ] **MC, medium:** Which statements about the CSS box model are true?
  - A. **Padding is the space between the content and the border**
  - B. **Margin is the space outside the border**
  - C. **With box-sizing set to border-box, width includes padding and border**
  - D. Margin is drawn inside the border
- [ ] **MC, medium:** Which statements about position in CSS are true?
  - A. **absolute places an element against its nearest positioned ancestor**
  - B. **fixed places an element against the viewport**
  - C. **sticky acts like relative until a scroll point, then sticks**
  - D. static elements accept top and left offsets
- [ ] **MC, medium:** Which statements about CSS Grid are true?
  - A. **It lays items out in rows and columns**
  - B. **grid-template-columns sets the column tracks**
  - C. **The fr unit shares out the free space**
  - D. Grid works on one axis only
- [ ] **MC, medium:** Which statements about rem and em are true?
  - A. **rem is relative to the root font size**
  - B. **em is relative to the font size of the element or its parent**
  - C. **Nested em sizes can compound**
  - D. rem changes with the parent element's font size
- [ ] **MC, hard:** Which of these beat a single class selector such as .button in specificity?
  - A. **An id selector such as #save**
  - B. **An inline style attribute**
  - C. **Two classes such as .button.primary**
  - D. A type selector such as button
- [ ] **MC, hard:** Which statements about the cascade are true?
  - A. **!important beats normal declarations**
  - B. **With equal specificity, the later rule wins**
  - C. **An inherited value loses to any rule that targets the element itself**
  - D. The order of declarations inside one rule changes its specificity
- [ ] **MC, hard:** Which CSS properties are cheap to animate because they skip layout?
  - A. **transform**
  - B. **opacity**
  - C. width
  - D. top
- [ ] **Blank, easy:** The CSS property ________ sets the space between an element's content and its border.  
  Accepted: padding
- [ ] **Blank, easy:** The CSS property ________ sets the space outside an element's border.  
  Accepted: margin
- [ ] **Blank, easy:** The CSS property ________ sets the colour of the text.  
  Accepted: color
- [ ] **Blank, medium:** Setting display to ________ on a container turns its children into flex items.  
  Accepted: flex, inline-flex
- [ ] **Blank, medium:** The CSS property ________ decides which element is drawn on top when they overlap.  
  Accepted: z-index
- [ ] **Blank, medium:** The pseudo-class ________ styles an element while the mouse pointer is over it.  
  Accepted: :hover, hover
- [ ] **Blank, medium:** In CSS Grid, the ________ unit stands for a fraction of the free space.  
  Accepted: fr
- [ ] **Blank, hard:** In Flexbox, the ________ property places items along the main axis.  
  Accepted: justify-content
- [ ] **Blank, hard:** CSS ________ properties, written as --name, hold values that var() can reuse.  
  Accepted: custom
- [ ] **Blank, hard:** Setting box-sizing to ________ makes width include padding and border.  
  Accepted: border-box
- [ ] **Brief, easy:** Explain the CSS box model.
  - Key fact: every element is a box of content, padding, border and margin
  - Key fact: padding is inside the border, margin is outside it
  - Key fact: box-sizing set to border-box makes width include padding and border
  - Keywords (grading without AI): content, padding, border, margin, box-sizing
- [ ] **Brief, medium:** When would you use Flexbox, and when CSS Grid?
  - Key fact: Flexbox lays items out along one axis, a row or a column
  - Key fact: Grid lays items out in two dimensions, rows and columns together
  - Key fact: Flexbox suits components such as nav bars, Grid suits whole page layouts
  - Keywords (grading without AI): flexbox, grid, axis, row, column
- [ ] **Brief, medium:** How does CSS specificity decide which rule wins?
  - Key fact: inline styles beat ids, ids beat classes, and classes beat element selectors
  - Key fact: with equal specificity, the rule that comes later wins
  - Key fact: !important overrides normal rules and should be used rarely
  - Keywords (grading without AI): specificity, id, class, inline, !important
- [ ] **Brief, hard:** How do you make a layout responsive?
  - Key fact: use fluid widths, Flexbox or Grid instead of fixed pixel widths
  - Key fact: use media queries or container queries to change the layout at breakpoints
  - Key fact: start mobile first and use relative units such as rem and %
  - Keywords (grading without AI): media query, fluid, breakpoint, mobile first, rem
- [ ] **Brief, hard:** How would you keep CSS manageable in a large project?
  - Key fact: use a naming method such as BEM, CSS Modules or utility classes to avoid clashes
  - Key fact: keep shared values such as colours and spacing in custom properties
  - Key fact: keep specificity low and avoid !important and deep nesting
  - Keywords (grading without AI): BEM, modules, custom properties, specificity, naming

## Git (bank_fullstack.py)

- [ ] **MC, easy:** Which Git commands are part of saving a change and sharing it?
  - A. **git add**
  - B. **git commit**
  - C. **git push**
  - D. git blame
- [ ] **MC, easy:** Which statements about git clone are true?
  - A. **It copies a remote repository to your computer**
  - B. **It sets up a remote called origin**
  - C. It creates a new empty repository on GitHub
  - D. **It copies the full history**
- [ ] **MC, easy:** Which Git commands show history or changes?
  - A. **git log**
  - B. **git diff**
  - C. **git show**
  - D. git init
- [ ] **MC, medium:** Which statements about Git branches are true?
  - A. **A branch is a movable pointer to a commit**
  - B. **git merge combines the history of two branches**
  - C. Creating a branch copies every file of the project
  - D. Deleting a branch at once deletes all its commits for good
- [ ] **MC, medium:** Which statements about a merge conflict are true?
  - A. **It happens when both branches changed the same lines**
  - B. **Git marks the conflicting parts in the file**
  - C. **You fix it by editing the file, then adding and committing it**
  - D. Git always picks the newest change on its own
- [ ] **MC, medium:** Which statements about .gitignore are true?
  - A. **It lists files Git should not track**
  - B. **It is a good place to list build output and files holding secrets**
  - C. It removes files that are already tracked
  - D. **Patterns such as *.log are allowed**
- [ ] **MC, medium:** Which statements about git pull are true?
  - A. **It fetches and then merges or rebases**
  - B. **It can cause merge conflicts**
  - C. **git pull --rebase replays your local commits on top**
  - D. It only downloads and never changes your branch
- [ ] **MC, hard:** Which statements about git rebase and git merge are true?
  - A. **Rebase rewrites commits so they sit on top of another branch**
  - B. **Merge keeps the existing history and can add a merge commit**
  - C. **Rebasing commits that others already pulled causes problems for them**
  - D. Merge deletes the commits of the merged branch
- [ ] **MC, hard:** Which Git commands undo changes?
  - A. **git revert makes a new commit that undoes an earlier one**
  - B. **git reset --hard moves the branch and throws away working changes**
  - C. **git restore throws away changes to a file in the working tree**
  - D. git log undoes the last commit
- [ ] **MC, hard:** Which statements about git stash are true?
  - A. **It saves uncommitted changes and cleans the working tree**
  - B. **git stash pop applies the latest stash and removes it**
  - C. Untracked files are stashed by default
  - D. **You can keep several stashes**
- [ ] **Blank, easy:** The command git ________ shows which files are changed, staged or untracked.  
  Accepted: status
- [ ] **Blank, easy:** The command git ________ makes a new empty repository in the current folder.  
  Accepted: init
- [ ] **Blank, easy:** The command git ________ stages changes for the next commit.  
  Accepted: add
- [ ] **Blank, medium:** The command git ________ downloads new commits from a remote without changing your working files.  
  Accepted: fetch
- [ ] **Blank, medium:** The command git ________ puts uncommitted changes aside so you can switch branches.  
  Accepted: stash
- [ ] **Blank, medium:** The default name of the remote a repository was cloned from is ________.  
  Accepted: origin
- [ ] **Blank, medium:** git ________ makes a new commit that undoes an earlier commit without rewriting history.  
  Accepted: revert
- [ ] **Blank, hard:** The command git ________ applies the changes of one chosen commit onto the current branch.  
  Accepted: cherry-pick
- [ ] **Blank, hard:** The command git ________ finds the commit that brought in a bug by binary search.  
  Accepted: bisect
- [ ] **Blank, hard:** git ________ lists where HEAD has pointed, which helps recover lost commits.  
  Accepted: reflog
- [ ] **Brief, easy:** What is the difference between git fetch and git pull?
  - Key fact: git fetch downloads new commits from the remote without changing your branch
  - Key fact: git pull runs fetch and then merges or rebases into your branch
  - Key fact: fetch is safe to run at any time, while pull can cause conflicts
  - Keywords (grading without AI): fetch, pull, merge, remote, conflict
- [ ] **Brief, medium:** How do you resolve a merge conflict?
  - Key fact: Git marks the conflicting lines with markers in the file
  - Key fact: edit the file to keep the right content and remove the markers
  - Key fact: stage the file and commit (or continue the rebase), then run the tests
  - Keywords (grading without AI): conflict, markers, edit, add, commit
- [ ] **Brief, medium:** Describe a branching workflow you would use in a team.
  - Key fact: work on short-lived feature branches made from main
  - Key fact: open a pull request so others review the change and CI runs the tests
  - Key fact: merge into main often and keep main always releasable
  - Keywords (grading without AI): feature branch, pull request, review, main, CI
- [ ] **Brief, hard:** When would you use rebase instead of merge, and what is the risk?
  - Key fact: rebase replays commits on top of another branch and keeps history linear
  - Key fact: merge keeps the real history and adds a merge commit
  - Key fact: never rebase commits that others have already pulled, because rebase rewrites history
  - Keywords (grading without AI): rebase, merge, linear, rewrite, shared
- [ ] **Brief, hard:** How would you undo a bad commit that is already pushed?
  - Key fact: use git revert to add a new commit that undoes it, which keeps shared history safe
  - Key fact: avoid git reset and force push on shared branches
  - Key fact: if the commit held a secret, revoke the secret, because it stays in history
  - Keywords (grading without AI): revert, reset, force push, history, secret

## Docker (bank_fullstack.py)

- [ ] **MC, easy:** Which statements about Docker are true?
  - A. **An image is a read-only template for containers**
  - B. **Containers share the kernel of the host**
  - C. Each container needs its own full operating-system kernel
  - D. **A Dockerfile describes how to build an image**
- [ ] **MC, easy:** Which Docker commands list Docker objects?
  - A. **docker ps**
  - B. **docker images**
  - C. **docker volume ls**
  - D. docker build
- [ ] **MC, easy:** Which statements about a Docker container are true?
  - A. **It is a running instance of an image**
  - B. **It is isolated from other containers**
  - C. It is a full virtual machine with its own kernel
  - D. **It can be stopped and started again**
- [ ] **MC, medium:** Which statements about Docker storage are true?
  - A. **Data in a volume survives when the container is removed**
  - B. **Several containers can share one volume**
  - C. Files written in a container's own layer survive docker rm
  - D. **A bind mount maps a folder of the host into the container**
- [ ] **MC, medium:** Which statements about Dockerfile instructions are true?
  - A. **RUN runs a command while the image is built**
  - B. **CMD sets the default command when a container starts**
  - C. **COPY copies files from the build context into the image**
  - D. EXPOSE publishes the port on the host
- [ ] **MC, medium:** Which statements about Docker networking are true?
  - A. **Containers on the same user-defined network reach each other by name**
  - B. **-p 8080:80 maps host port 8080 to container port 80**
  - C. Containers can never reach the internet
  - D. **Docker Compose puts its services on a shared network**
- [ ] **MC, medium:** Which statements about image layers are true?
  - A. **Instructions such as RUN and COPY add a layer**
  - B. **Unchanged layers are reused from the build cache**
  - C. **Changing an early layer rebuilds the layers after it**
  - D. Every layer is a full copy of the base image
- [ ] **MC, hard:** Which make Docker builds faster or images smaller?
  - A. **Copying the package files and installing dependencies before copying the rest of the code**
  - B. **Multi-stage builds**
  - C. **A .dockerignore file**
  - D. Putting the most often changed instruction at the top of the Dockerfile
- [ ] **MC, hard:** Which are good practices for container security?
  - A. **Run as a non-root user**
  - B. **Use small, trusted base images**
  - C. Bake secrets into the image
  - D. **Scan images for known vulnerabilities**
- [ ] **MC, hard:** Which statements about CMD and ENTRYPOINT are true?
  - A. **ENTRYPOINT sets the program that always runs**
  - B. **CMD gives default arguments that docker run can override**
  - C. **The exec form is written as a JSON array**
  - D. A Dockerfile can contain only one of the two
- [ ] **Blank, easy:** The command docker ________ starts a new container from an image.  
  Accepted: run
- [ ] **Blank, easy:** The command docker ________ builds an image from a Dockerfile.  
  Accepted: build
- [ ] **Blank, easy:** The command docker ________ downloads an image from a registry.  
  Accepted: pull
- [ ] **Blank, medium:** Docker ________ starts several containers described in one YAML file.  
  Accepted: Compose
- [ ] **Blank, medium:** The Dockerfile instruction ________ sets the working folder for the instructions after it.  
  Accepted: WORKDIR
- [ ] **Blank, medium:** A ________ file lists files that should not be sent to the build context.  
  Accepted: .dockerignore, dockerignore
- [ ] **Blank, medium:** The docker run flag ________ maps a host port to a container port.  
  Accepted: -p, --publish
- [ ] **Blank, hard:** The Dockerfile instruction ________ documents the port a container listens on, without publishing it.  
  Accepted: EXPOSE
- [ ] **Blank, hard:** A ________ build uses several FROM stages so the final image holds only what runs in production.  
  Accepted: multi-stage, multistage, multi stage
- [ ] **Blank, hard:** The Dockerfile instruction ________ tells Docker how to check that a container still works.  
  Accepted: HEALTHCHECK
- [ ] **Brief, easy:** What is the difference between a Docker image and a container?
  - Key fact: an image is a read-only template built from a Dockerfile
  - Key fact: a container is a running instance of an image with its own writable layer
  - Key fact: many containers can run from one image
  - Keywords (grading without AI): image, container, template, instance, layer
- [ ] **Brief, medium:** How is a container different from a virtual machine?
  - Key fact: a container shares the host's kernel, while a virtual machine runs its own operating system
  - Key fact: containers start in seconds and use less memory
  - Key fact: virtual machines give stronger isolation
  - Keywords (grading without AI): kernel, virtual machine, isolation, lightweight, start
- [ ] **Brief, medium:** How do you keep data when a container is removed?
  - Key fact: data written in the container's own layer is lost on docker rm
  - Key fact: a named volume keeps data that Docker manages
  - Key fact: a bind mount maps a folder of the host into the container
  - Keywords (grading without AI): volume, bind mount, persist, rm, data
- [ ] **Brief, hard:** How would you make a Docker image smaller and its builds faster?
  - Key fact: use a small base image and a multi-stage build
  - Key fact: copy the dependency files and install them before copying the rest, so the layer cache is reused
  - Key fact: add a .dockerignore and clean package caches in the same RUN step
  - Keywords (grading without AI): multi-stage, cache, layer, .dockerignore, base image
- [ ] **Brief, hard:** How would you run a web app and its database together with Docker Compose?
  - Key fact: define both as services in a compose.yaml file
  - Key fact: the services share a network and reach each other by service name
  - Key fact: use a named volume for the database data and environment variables for settings
  - Keywords (grading without AI): compose, service, network, volume, environment

## Authentication (bank_fullstack.py)

- [ ] **MC, easy:** Which are good practices for storing passwords?
  - A. **Hash them with a slow algorithm such as bcrypt**
  - B. **Add a unique salt to each password**
  - C. Store them as plain text
  - D. **Never write them to logs**
- [ ] **MC, easy:** Which are examples of multi-factor authentication?
  - A. **A password plus a code from an authenticator app**
  - B. **A password plus a hardware security key**
  - C. A password typed twice
  - D. **A password plus a fingerprint**
- [ ] **MC, easy:** Which statements about HTTPS are true?
  - A. **It encrypts traffic between the browser and the server**
  - B. **It stops passwords from being read on the network**
  - C. It hashes passwords on the server
  - D. **It uses TLS**
- [ ] **MC, medium:** Which statements about JWTs are true?
  - A. **The payload is encoded, not encrypted, so anyone can read it**
  - B. **The signature lets the server notice a changed token**
  - C. The server can revoke a JWT at once without storing anything extra
  - D. **It usually carries an expiry time**
- [ ] **MC, medium:** Which statements about sessions and tokens are true?
  - A. **A server session keeps login state on the server, found by a cookie**
  - B. **A JWT carries the login state in the token itself**
  - C. **Server sessions are easy to revoke**
  - D. A JWT can be checked only by looking it up in a database
- [ ] **MC, medium:** Which statements about OAuth 2.0 are true?
  - A. **It lets an app use a user's data at another service without their password**
  - B. **It uses access tokens**
  - C. **Authorization code with PKCE is the recommended flow for browser and mobile apps**
  - D. It is a password hashing algorithm
- [ ] **MC, medium:** Which help stop brute-force login attempts?
  - A. **Rate limiting login requests**
  - B. **Locking or slowing an account after many failures**
  - C. Saying which part of the login was wrong
  - D. **Multi-factor authentication**
- [ ] **MC, hard:** Which help protect a login session kept in a cookie?
  - A. **The HttpOnly flag, so scripts cannot read the cookie**
  - B. **The Secure flag, so it is sent only over HTTPS**
  - C. **The SameSite attribute, against cross-site request forgery**
  - D. Keeping the password in the cookie
- [ ] **MC, hard:** Which statements about XSS and CSRF are true?
  - A. **XSS runs an attacker's script in the victim's page**
  - B. **CSRF makes the victim's browser send a request the victim did not mean to send**
  - C. Any script on the page can read an HttpOnly cookie
  - D. **SameSite cookies help against CSRF**
- [ ] **MC, hard:** Which statements about refresh tokens are true?
  - A. **They get a new short-lived access token**
  - B. **They usually live longer than access tokens**
  - C. **Rotating them on each use helps detect theft**
  - D. They should be sent with every API request
- [ ] **Blank, easy:** Checking who a user is is authentication; checking what they may do is ________.  
  Accepted: authorization, authorisation
- [ ] **Blank, easy:** Proving who you are in two or more ways, such as a password and a code, is called ________ authentication.  
  Accepted: multi-factor, multifactor, two-factor, 2FA, MFA
- [ ] **Blank, easy:** A password should be stored as a ________, never as plain text.  
  Accepted: hash
- [ ] **Blank, medium:** A signed ________ carries claims and is often used for stateless login.  
  Accepted: JWT, JSON Web Token
- [ ] **Blank, medium:** ________ is a slow password-hashing function often used to store passwords.  
  Accepted: bcrypt, Argon2, scrypt
- [ ] **Blank, medium:** OAuth 2.0 gives an app an access ________ that it sends when it calls an API.  
  Accepted: token
- [ ] **Blank, medium:** The cookie flag ________ stops JavaScript from reading the cookie.  
  Accepted: HttpOnly
- [ ] **Blank, hard:** Adding a unique random ________ to each password before hashing defeats precomputed rainbow tables.  
  Accepted: salt
- [ ] **Blank, hard:** ________ adds an identity layer on top of OAuth 2.0 and returns an ID token.  
  Accepted: OpenID Connect, OIDC
- [ ] **Blank, hard:** The OAuth extension ________ protects the authorization code flow in apps that cannot keep a secret.  
  Accepted: PKCE
- [ ] **Brief, easy:** What is the difference between authentication and authorization?
  - Key fact: authentication checks who the user is, for example with a password
  - Key fact: authorization checks what that user may do
  - Key fact: authorization comes after authentication, for example through roles or permissions
  - Keywords (grading without AI): authentication, authorization, identity, permission, role
- [ ] **Brief, medium:** How should a web app store user passwords?
  - Key fact: never store plain text; store a hash
  - Key fact: use a slow, salted algorithm such as bcrypt or Argon2
  - Key fact: compare hashes at login and never write passwords to logs
  - Keywords (grading without AI): hash, salt, bcrypt, Argon2, plain text
- [ ] **Brief, medium:** Compare server sessions with JWT-based authentication.
  - Key fact: a server session keeps login state on the server, and the cookie holds only an ID
  - Key fact: a JWT holds the claims itself and is checked by its signature without a lookup
  - Key fact: sessions are easy to revoke, while JWTs need a short expiry or a deny list
  - Keywords (grading without AI): session, JWT, signature, revoke, stateless
- [ ] **Brief, hard:** How does the OAuth 2.0 authorization code flow work?
  - Key fact: the app sends the user to the authorization server to log in and consent
  - Key fact: the server redirects back to the app with a short-lived code
  - Key fact: the app exchanges the code, with PKCE or its secret, for an access token and uses it to call the API
  - Keywords (grading without AI): authorization code, redirect, consent, access token, PKCE
- [ ] **Brief, hard:** How would you protect a login form against common attacks?
  - Key fact: use HTTPS, hashed passwords and rate limiting against brute force
  - Key fact: use HttpOnly, Secure and SameSite cookies against XSS and CSRF
  - Key fact: offer multi-factor authentication and give the same error for a wrong user name or password
  - Keywords (grading without AI): rate limit, HttpOnly, SameSite, MFA, brute force

## Unit Testing (bank_fullstack.py)

- [ ] **MC, easy:** Which are good qualities of a unit test?
  - A. **It runs fast**
  - B. **It does not depend on other tests**
  - C. It passes only if the tests run in a fixed order
  - D. **It checks one behaviour**
- [ ] **MC, easy:** Which statements about unit tests are true?
  - A. **They test a small piece of code on its own**
  - B. **They run automatically**
  - C. They need a real database
  - D. **They catch regressions**
- [ ] **MC, easy:** Which of these are unit testing frameworks?
  - A. **JUnit**
  - B. **Jest**
  - C. **pytest**
  - D. Webpack
- [ ] **MC, medium:** Which statements about test coverage are true?
  - A. **It measures which lines or branches the tests run**
  - B. 100% coverage proves the code has no bugs
  - C. **It can show parts of the code no test runs**
  - D. **Branch coverage checks both outcomes of a condition**
- [ ] **MC, medium:** Which statements about mocks and stubs are true?
  - A. **A stub returns fixed answers**
  - B. **A mock also checks how it was called**
  - C. **They let you test code without slow or external dependencies**
  - D. Every dependency must always be mocked
- [ ] **MC, medium:** Which statements about the testing pyramid are true?
  - A. **It suggests many unit tests**
  - B. **It suggests few end-to-end tests**
  - C. **Integration tests sit between the two**
  - D. It suggests mostly manual tests
- [ ] **MC, medium:** Which make a test name useful?
  - A. **It says which behaviour is checked**
  - B. **It says the expected result**
  - C. It is just test1
  - D. **It says the condition or input**
- [ ] **MC, hard:** Which are signs of a flaky test?
  - A. **It passes and fails on the same code**
  - B. **It depends on the current time or on random values**
  - C. **It depends on other tests running first**
  - D. It fails every time because of a real bug
- [ ] **MC, hard:** Which statements about testing code that reads the current time are true?
  - A. **Inject a clock so tests can control the time**
  - B. **Reading the system time directly makes tests flaky**
  - C. Tests should sleep until the right time
  - D. **A fixed clock makes results repeatable**
- [ ] **MC, hard:** Which statements about test doubles are true?
  - A. **A fake has a simple working implementation, such as an in-memory store**
  - B. **A spy records the calls made to it**
  - C. **A dummy is passed in but never used**
  - D. A mock is the same thing as an integration test
- [ ] **Blank, easy:** A ________ replaces a real dependency with a controlled fake during a test.  
  Accepted: mock, stub, fake, test double
- [ ] **Blank, easy:** An ________ checks that a value in a test is what you expected.  
  Accepted: assertion, assert
- [ ] **Blank, easy:** A test that checks how several parts work together is called an ________ test.  
  Accepted: integration
- [ ] **Blank, medium:** Arrange, ________, Assert is a common way to structure a unit test.  
  Accepted: Act
- [ ] **Blank, medium:** A bug where a change breaks something that used to work is called a ________.  
  Accepted: regression
- [ ] **Blank, medium:** In JUnit 5, the ________ annotation runs a method before each test.  
  Accepted: @BeforeEach, BeforeEach
- [ ] **Blank, medium:** A test that sometimes passes and sometimes fails on the same code is called ________.  
  Accepted: flaky
- [ ] **Blank, hard:** Writing a failing test first and then the code that makes it pass is called ________.  
  Accepted: TDD, test-driven development, test driven development
- [ ] **Blank, hard:** ________ testing changes the code on purpose to check whether the tests notice.  
  Accepted: mutation
- [ ] **Blank, hard:** Passing dependencies into a class instead of creating them inside it is called dependency ________.  
  Accepted: injection
- [ ] **Brief, easy:** Why write unit tests?
  - Key fact: they catch bugs early, close to where they were made
  - Key fact: they let you change code safely because regressions show up
  - Key fact: they document how the code is meant to behave
  - Keywords (grading without AI): bug, regression, refactor, document, confidence
- [ ] **Brief, medium:** What is the difference between unit, integration and end-to-end tests?
  - Key fact: unit tests check one small piece on its own and run fast
  - Key fact: integration tests check that parts such as the code and a database work together
  - Key fact: end-to-end tests drive the whole app like a user and are slow, so there are few of them
  - Keywords (grading without AI): unit, integration, end-to-end, isolation, pyramid
- [ ] **Brief, medium:** When would you use a mock in a unit test?
  - Key fact: to replace a slow or external dependency such as an HTTP service or a database
  - Key fact: to control what the dependency returns, including errors
  - Key fact: not for simple value objects, because too many mocks make tests fragile
  - Keywords (grading without AI): mock, dependency, external, control, fragile
- [ ] **Brief, hard:** How would you make a flaky test reliable?
  - Key fact: find the cause: time, randomness, order between tests or shared state
  - Key fact: control it with an injected clock, a fixed random seed or fresh data per test
  - Key fact: replace sleeps with waiting for a condition
  - Keywords (grading without AI): flaky, clock, seed, shared state, sleep
- [ ] **Brief, hard:** What is test-driven development, and what are its benefits?
  - Key fact: write a failing test, write the simplest code that passes it, then refactor
  - Key fact: it keeps code testable and designed from the caller's point of view
  - Key fact: it gives a fast feedback loop and a safety net for refactoring
  - Keywords (grading without AI): red, green, refactor, failing test, feedback

## Data Structures (bank_software.py)

- [ ] **MC, easy:** Which operations on a hash table take O(1) time on average?
  - A. **Looking up a value by key**
  - B. **Inserting a key**
  - C. Listing all keys in sorted order
  - D. Finding the smallest key
- [ ] **MC, easy:** Which of these data structures are linear?
  - A. **Array**
  - B. **Linked list**
  - C. **Stack**
  - D. Binary tree
- [ ] **MC, easy:** Which statements about a stack are true?
  - A. **The last item added is the first removed**
  - B. **push adds an item on top**
  - C. pop removes the item at the bottom
  - D. **The call stack of a program is one**
- [ ] **MC, medium:** Which statements about arrays and linked lists are true?
  - A. **An array reaches any element by index in O(1)**
  - B. **Inserting at the head of a linked list is O(1)**
  - C. A linked list reaches any element by index in O(1)
  - D. **An array keeps its elements next to each other in memory**
- [ ] **MC, medium:** Which statements about a binary search tree are true?
  - A. **Keys in the left subtree are smaller than the node**
  - B. **An in-order walk gives the keys in sorted order**
  - C. Search always takes O(1)
  - D. **An unbalanced tree can slow down to O(n)**
- [ ] **MC, medium:** Which statements about a binary heap are true?
  - A. **A min-heap keeps the smallest item at the root**
  - B. **It backs a priority queue**
  - C. **Inserting takes O(log n)**
  - D. Reading it level by level gives the items in sorted order
- [ ] **MC, medium:** Which statements about hash collisions are true?
  - A. **Two keys land in the same bucket**
  - B. **Chaining keeps colliding entries in a list**
  - C. **Open addressing probes for another free slot**
  - D. A good hash function makes collisions impossible
- [ ] **MC, hard:** Which statements about balanced search trees are true?
  - A. **AVL and red-black trees keep their height O(log n)**
  - B. **They rebalance with rotations**
  - C. **A B-tree node holds many keys, which suits disks**
  - D. Balancing makes search O(1)
- [ ] **MC, hard:** Which statements about a trie are true?
  - A. **It stores strings character by character along paths**
  - B. **Prefix search is fast**
  - C. **Lookup time depends on the key's length, not on how many keys there are**
  - D. It always uses less memory than a hash set
- [ ] **MC, hard:** Which statements about graph representations are true?
  - A. **An adjacency list uses O(V + E) space**
  - B. **An adjacency matrix uses O(V^2) space**
  - C. **A matrix checks whether an edge exists in O(1)**
  - D. An adjacency list is always better for dense graphs
- [ ] **Blank, easy:** A ________ removes items in the same order they were added (first in, first out).  
  Accepted: queue
- [ ] **Blank, easy:** A ________ adds and removes items at the same end (last in, first out).  
  Accepted: stack
- [ ] **Blank, easy:** In an array, an element is reached directly by its ________.  
  Accepted: index, position
- [ ] **Blank, medium:** A ________ keeps the smallest or largest item at the top and backs a priority queue.  
  Accepted: heap, binary heap
- [ ] **Blank, medium:** When two keys map to the same bucket of a hash table, it is called a ________.  
  Accepted: collision
- [ ] **Blank, medium:** A tree in which each node has at most two children is a ________ tree.  
  Accepted: binary
- [ ] **Blank, medium:** A graph is made of nodes and the ________ that connect them.  
  Accepted: edges, edge
- [ ] **Blank, hard:** A ________ is a tree of characters that makes prefix lookups fast.  
  Accepted: trie, prefix tree
- [ ] **Blank, hard:** The union-find structure is also called a ________ set.  
  Accepted: disjoint
- [ ] **Blank, hard:** An operation that is usually O(1) but sometimes slow, such as appending to a dynamic array, is called ________ O(1).  
  Accepted: amortized, amortised
- [ ] **Brief, easy:** What is the difference between a stack and a queue?
  - Key fact: a stack is last in, first out: push and pop happen at the same end
  - Key fact: a queue is first in, first out: add at the back, remove at the front
  - Key fact: stacks suit function calls and undo, queues suit tasks in order and breadth-first search
  - Keywords (grading without AI): stack, queue, LIFO, FIFO, order
- [ ] **Brief, medium:** How does a hash table work?
  - Key fact: a hash function turns a key into a bucket index
  - Key fact: collisions are handled by chaining or open addressing
  - Key fact: lookups and inserts are O(1) on average, and the table resizes when it gets too full
  - Keywords (grading without AI): hash function, bucket, collision, O(1), resize
- [ ] **Brief, medium:** When would you use an array, and when a linked list?
  - Key fact: an array gives O(1) access by index and is cache-friendly
  - Key fact: a linked list inserts and removes at a known node in O(1)
  - Key fact: use an array for random access and a linked list when you often add and remove in the middle
  - Keywords (grading without AI): array, linked list, index, insert, memory
- [ ] **Brief, hard:** How does a binary search tree stay fast, and what happens when it is unbalanced?
  - Key fact: left keys are smaller and right keys larger, so each step of a search halves the tree
  - Key fact: a balanced tree gives O(log n) search, insert and delete
  - Key fact: inserting sorted data turns it into a chain with O(n) operations, so self-balancing trees such as AVL or red-black trees rotate nodes
  - Keywords (grading without AI): BST, balanced, O(log n), rotation, red-black
- [ ] **Brief, hard:** How do you choose a data structure for a problem?
  - Key fact: list the operations the problem needs and how often each happens
  - Key fact: compare the time and space cost of those operations across structures
  - Key fact: pick the simplest structure that makes the most frequent operations fast
  - Keywords (grading without AI): operations, time complexity, space, trade-off, frequent

## Algorithms (bank_software.py)

- [ ] **MC, easy:** Which of these sorting algorithms take O(n log n) time in the average case?
  - A. **Merge sort**
  - B. **Quick sort**
  - C. Bubble sort
  - D. Insertion sort
- [ ] **MC, easy:** Which statements about binary search are true?
  - A. **It needs sorted input**
  - B. **It takes O(log n) time**
  - C. It checks every element one by one
  - D. **It halves the search range at each step**
- [ ] **MC, easy:** Which of these take O(n) time?
  - A. **Finding the largest value in an unsorted array**
  - B. **Summing every element of a list**
  - C. Reading an array element by its index
  - D. **Counting how often each word appears in a list, using a hash map**
- [ ] **MC, medium:** Which statements about recursion are true?
  - A. **It needs a base case**
  - B. **Each call uses stack space**
  - C. **Very deep recursion can overflow the stack**
  - D. Recursion is always faster than a loop
- [ ] **MC, medium:** Which statements about breadth-first and depth-first search are true?
  - A. **BFS uses a queue**
  - B. **DFS uses a stack or recursion**
  - C. **BFS finds the shortest path in an unweighted graph**
  - D. DFS always finds the shortest path
- [ ] **MC, medium:** Which statements about quicksort are true?
  - A. **Its average time is O(n log n)**
  - B. **Its worst case is O(n^2)**
  - C. **It can sort in place**
  - D. It is always stable
- [ ] **MC, medium:** Which problems suit the two-pointer technique?
  - A. **Finding a pair with a given sum in a sorted array**
  - B. **Reversing an array in place**
  - C. **Removing duplicates from a sorted array**
  - D. Finding shortest paths in a weighted graph
- [ ] **MC, hard:** Which statements about dynamic programming are true?
  - A. **It solves overlapping subproblems once and reuses the results**
  - B. **It needs optimal substructure**
  - C. **Memoization is the top-down form**
  - D. It always uses less memory than plain recursion
- [ ] **MC, hard:** Which statements about Dijkstra's algorithm are true?
  - A. **It finds shortest paths from one source**
  - B. **It needs non-negative edge weights**
  - C. **It uses a priority queue**
  - D. It works with negative edge weights
- [ ] **MC, hard:** Which statements about greedy algorithms are true?
  - A. **They take the best local choice at each step**
  - B. **They do not always give the best overall answer**
  - C. **They solve activity selection correctly**
  - D. They always beat dynamic programming
- [ ] **Blank, easy:** Binary search needs the input to be ________ and takes O(log n) time.  
  Accepted: sorted, in sorted order
- [ ] **Blank, easy:** Big ________ notation describes how running time grows with the input size.  
  Accepted: O, Oh
- [ ] **Blank, easy:** A function that calls itself is called ________.  
  Accepted: recursive
- [ ] **Blank, medium:** Breadth-first search explores a graph with the help of a ________.  
  Accepted: queue
- [ ] **Blank, medium:** Every recursive function needs a ________ case that stops the recursion.  
  Accepted: base
- [ ] **Blank, medium:** Merge sort works by divide and ________.  
  Accepted: conquer
- [ ] **Blank, medium:** A sort that keeps equal items in their original order is called ________.  
  Accepted: stable
- [ ] **Blank, hard:** Storing the results of calls so they are not computed again is called ________.  
  Accepted: memoization, memoisation
- [ ] **Blank, hard:** Ordering the nodes of a directed acyclic graph so every edge points forward is a ________ sort.  
  Accepted: topological
- [ ] **Blank, hard:** The sliding ________ technique keeps a moving range over an array to avoid recomputing it.  
  Accepted: window
- [ ] **Brief, easy:** What does Big O notation tell you?
  - Key fact: it describes how time or memory grows as the input grows
  - Key fact: it keeps the fastest-growing term and drops constants
  - Key fact: common classes are O(1), O(log n), O(n), O(n log n) and O(n^2)
  - Keywords (grading without AI): Big O, growth, input size, worst case, complexity
- [ ] **Brief, medium:** Compare merge sort and quicksort.
  - Key fact: both take O(n log n) on average
  - Key fact: merge sort is stable and always O(n log n) but needs extra memory
  - Key fact: quicksort sorts in place and is fast in practice but can hit O(n^2) with bad pivots
  - Keywords (grading without AI): merge sort, quicksort, stable, pivot, in place
- [ ] **Brief, medium:** When would you use breadth-first search instead of depth-first search?
  - Key fact: BFS visits nodes level by level using a queue
  - Key fact: BFS finds the shortest path in an unweighted graph
  - Key fact: DFS goes deep first with a stack or recursion and suits cycle detection, topological sort and exploring every path
  - Keywords (grading without AI): BFS, DFS, queue, stack, shortest path
- [ ] **Brief, hard:** Explain dynamic programming with an example.
  - Key fact: it breaks a problem into overlapping subproblems and solves each once
  - Key fact: results are kept with memoization (top-down) or a table (bottom-up)
  - Key fact: for example, Fibonacci or the longest common subsequence drops from exponential to polynomial time
  - Keywords (grading without AI): subproblem, memoization, table, overlapping, optimal substructure
- [ ] **Brief, hard:** How do you approach an algorithm problem you have not seen before?
  - Key fact: clarify the input, the output and the edge cases, and work through a small example
  - Key fact: start with a brute-force solution and state its complexity
  - Key fact: improve it with a fitting data structure or technique, such as hashing, two pointers or dynamic programming, and test it
  - Keywords (grading without AI): edge case, brute force, complexity, optimize, test

## Object-Oriented Programming (bank_software.py)

- [ ] **MC, easy:** Which of these are pillars of object-oriented programming?
  - A. **Encapsulation**
  - B. **Polymorphism**
  - C. Compilation
  - D. **Inheritance**
- [ ] **MC, easy:** Which statements about classes and objects are true?
  - A. **A class is a blueprint**
  - B. **An object is an instance of a class**
  - C. All objects of one class always hold the same field values
  - D. **A class can have methods**
- [ ] **MC, easy:** Which statements about constructors are true?
  - A. **A constructor runs when an object is created**
  - B. **It sets up the object's starting state**
  - C. It must return a value
  - D. **In Java a class can have several constructors with different parameters**
- [ ] **MC, medium:** Which statements about interfaces and abstract classes in Java are true?
  - A. **A class can implement several interfaces**
  - B. **A class can extend only one class**
  - C. **An abstract class can have fields and constructors**
  - D. An interface can be created with new
- [ ] **MC, medium:** Which of these are forms of polymorphism?
  - A. **Method overriding**
  - B. **Method overloading**
  - C. **Calling a method through a parent-type reference and getting the child's version**
  - D. Making a field private
- [ ] **MC, medium:** Which statements about composition and inheritance are true?
  - A. **Composition means an object holds other objects**
  - B. **Inheritance models an is-a relationship**
  - C. **Composition is often more flexible than deep inheritance**
  - D. Inheritance is the only way to reuse code
- [ ] **MC, medium:** Which statements about access modifiers in Java are true?
  - A. **private is visible only inside the class**
  - B. **public is visible everywhere**
  - C. **protected is visible to subclasses**
  - D. private fields can be read by any class in the same package
- [ ] **MC, hard:** Which of these are SOLID principles?
  - A. **Single responsibility**
  - B. **Open/closed**
  - C. **Liskov substitution**
  - D. Global state
- [ ] **MC, hard:** Which statements about the Liskov substitution principle are true?
  - A. **A subclass should work wherever its parent is expected**
  - B. **A subclass that throws on a method its parent supports breaks it**
  - C. **Square extending Rectangle with separate width and height setters is a classic violation**
  - D. It says every class needs an interface
- [ ] **MC, hard:** Which statements about equals and hashCode in Java are true?
  - A. **Equal objects must have equal hash codes**
  - B. **Overriding equals without hashCode breaks HashMap lookups**
  - C. Two objects with the same hash code must be equal
  - D. **The default equals compares references**
- [ ] **Blank, easy:** Hiding the internal state of an object behind public methods is called ________.  
  Accepted: encapsulation
- [ ] **Blank, easy:** An object is an ________ of a class.  
  Accepted: instance
- [ ] **Blank, easy:** A class that takes the fields and methods of a parent class uses ________.  
  Accepted: inheritance
- [ ] **Blank, medium:** Giving a subclass its own version of a parent's method is called method ________.  
  Accepted: overriding
- [ ] **Blank, medium:** Several methods with the same name but different parameters is called method ________.  
  Accepted: overloading
- [ ] **Blank, medium:** A class marked ________ cannot be instantiated and may have methods without a body.  
  Accepted: abstract
- [ ] **Blank, medium:** Building objects out of other objects instead of inheriting from them is called ________.  
  Accepted: composition
- [ ] **Blank, hard:** The S in SOLID stands for single ________.  
  Accepted: responsibility
- [ ] **Blank, hard:** The principle that classes should be open for extension but closed for modification is the ________ principle.  
  Accepted: open/closed, open-closed, open closed
- [ ] **Blank, hard:** Depending on interfaces instead of concrete classes is the dependency ________ principle.  
  Accepted: inversion
- [ ] **Brief, easy:** Explain the four pillars of object-oriented programming.
  - Key fact: encapsulation hides an object's state behind methods
  - Key fact: inheritance lets a class reuse and extend a parent class
  - Key fact: polymorphism lets one interface have many implementations, and abstraction hides details behind a simple interface
  - Keywords (grading without AI): encapsulation, inheritance, polymorphism, abstraction, class
- [ ] **Brief, medium:** What is the difference between an abstract class and an interface?
  - Key fact: an interface defines a contract, and a class can implement many of them
  - Key fact: an abstract class can hold state, constructors and shared code, and a class can extend only one
  - Key fact: use an interface for a capability shared by unrelated classes and an abstract class for a common base
  - Keywords (grading without AI): interface, abstract class, contract, extend, implement
- [ ] **Brief, medium:** Why is composition often preferred over inheritance?
  - Key fact: inheritance ties a subclass tightly to its parent's implementation
  - Key fact: composition builds behaviour from smaller objects that can be swapped
  - Key fact: it avoids deep hierarchies and makes testing and change easier
  - Keywords (grading without AI): composition, inheritance, coupling, flexible, has-a
- [ ] **Brief, hard:** Explain the SOLID principles.
  - Key fact: Single responsibility: a class has one reason to change; Open/closed: extend behaviour without modifying code
  - Key fact: Liskov substitution: a subclass works wherever its parent does; Interface segregation: small, focused interfaces
  - Key fact: Dependency inversion: depend on abstractions, not on concrete classes
  - Keywords (grading without AI): single responsibility, open/closed, Liskov, interface segregation, dependency inversion
- [ ] **Brief, hard:** How do polymorphism and dynamic dispatch work in Java?
  - Key fact: a variable of a parent type can refer to an object of a child type
  - Key fact: the method that runs is chosen at runtime from the object's actual class
  - Key fact: overloading is resolved at compile time from the parameter types, overriding at runtime
  - Keywords (grading without AI): polymorphism, runtime, override, overload, dispatch

## System Design (bank_software.py)

- [ ] **MC, easy:** Which techniques help a web service handle more traffic?
  - A. **Load balancing**
  - B. **Caching**
  - C. Removing all database indexes
  - D. **Horizontal scaling**
- [ ] **MC, easy:** Which statements about a load balancer are true?
  - A. **It spreads requests across servers**
  - B. **It can stop sending traffic to unhealthy servers**
  - C. It stores the application's database
  - D. **It lets you add servers without clients noticing**
- [ ] **MC, easy:** Which statements about caching are true?
  - A. **It keeps often-read data in fast storage**
  - B. **It reduces load on the database**
  - C. **Cached data can become stale**
  - D. It removes the need for a database
- [ ] **MC, medium:** Which statements about vertical and horizontal scaling are true?
  - A. **Vertical scaling adds power to one machine**
  - B. **Horizontal scaling adds more machines**
  - C. **Horizontal scaling usually needs stateless servers**
  - D. Vertical scaling has no upper limit
- [ ] **MC, medium:** Which statements about a CDN are true?
  - A. **It serves static files from servers close to the user**
  - B. **It lowers latency**
  - C. **It reduces load on the origin server**
  - D. It replaces the application server for every request
- [ ] **MC, medium:** Which statements about database read replicas are true?
  - A. **They spread read traffic**
  - B. **They can lag behind the primary**
  - C. **One can take over when the primary fails**
  - D. They split writes across many primaries by default
- [ ] **MC, medium:** Which statements about queues in system design are true?
  - A. **They decouple producers from consumers**
  - B. **They absorb traffic spikes**
  - C. **They let work happen asynchronously**
  - D. They guarantee instant processing
- [ ] **MC, hard:** Which statements about the CAP theorem are true?
  - A. **During a network partition a system must choose consistency or availability**
  - B. **P stands for partition tolerance**
  - C. **Many NoSQL stores favour availability with eventual consistency**
  - D. A distributed system can always have all three at once
- [ ] **MC, hard:** Which statements about sharding are true?
  - A. **It splits data across several databases by a key**
  - B. **A bad shard key creates hot spots**
  - C. **Queries across shards are harder**
  - D. It makes joins across all the data easier
- [ ] **MC, hard:** Which help a system survive failures?
  - A. **Redundancy with no single point of failure**
  - B. **Retries with backoff**
  - C. **Circuit breakers**
  - D. Keeping everything on one server
- [ ] **Blank, easy:** Adding more machines to share the load is called ________ scaling.  
  Accepted: horizontal
- [ ] **Blank, easy:** A ________ balancer spreads incoming requests across several servers.  
  Accepted: load
- [ ] **Blank, easy:** A ________ keeps copies of static files on servers close to users.  
  Accepted: CDN, content delivery network
- [ ] **Blank, medium:** Adding more CPU and memory to one machine is called ________ scaling.  
  Accepted: vertical
- [ ] **Blank, medium:** Splitting a database into parts by a key, each part on its own server, is called ________.  
  Accepted: sharding, partitioning
- [ ] **Blank, medium:** A ________ replica answers read queries with data copied from the primary database.  
  Accepted: read
- [ ] **Blank, medium:** The time a request takes to get an answer is called ________.  
  Accepted: latency
- [ ] **Blank, hard:** In the CAP theorem, the P stands for ________ tolerance.  
  Accepted: partition
- [ ] **Blank, hard:** A model where replicas agree on the same value after some time is called ________ consistency.  
  Accepted: eventual
- [ ] **Blank, hard:** Consistent ________ spreads keys over nodes so adding a node moves only a few keys.  
  Accepted: hashing
- [ ] **Brief, easy:** How would you make a slow web page faster at the system level?
  - Key fact: cache often-read data in memory, for example in Redis
  - Key fact: serve static files through a CDN
  - Key fact: add database indexes and move slow work to background jobs
  - Keywords (grading without AI): cache, CDN, index, background, latency
- [ ] **Brief, medium:** Explain horizontal and vertical scaling, and when to use each.
  - Key fact: vertical scaling adds CPU or memory to one machine; it is simple but has a ceiling
  - Key fact: horizontal scaling adds more machines behind a load balancer and scales further
  - Key fact: horizontal scaling needs stateless servers and shared storage for sessions
  - Keywords (grading without AI): vertical, horizontal, load balancer, stateless, limit
- [ ] **Brief, medium:** What are the trade-offs of adding a cache?
  - Key fact: a cache cuts latency and database load for repeated reads
  - Key fact: cached data can go stale, so it needs expiry or invalidation
  - Key fact: it is one more part that can fail, so the system must still work on a cache miss
  - Keywords (grading without AI): cache, stale, invalidation, TTL, miss
- [ ] **Brief, hard:** How would you design a URL shortener?
  - Key fact: make a short unique key, for example the base62 form of an ID, and store it with the long URL
  - Key fact: redirects are read-heavy, so cache popular keys and use read replicas
  - Key fact: use a key-value store or a database sharded by key, and add rate limiting and expiry
  - Keywords (grading without AI): base62, key, cache, redirect, shard
- [ ] **Brief, hard:** Explain the CAP theorem and how it affects the choice of a database.
  - Key fact: when the network splits, a distributed system must choose consistency or availability
  - Key fact: CP systems refuse some requests to stay consistent, AP systems answer with possibly old data
  - Key fact: choose by need: payments favour consistency, a social feed can accept eventual consistency
  - Keywords (grading without AI): consistency, availability, partition, CP, AP

## Design Patterns (bank_software.py)

- [ ] **MC, easy:** Which of these are creational design patterns?
  - A. **Singleton**
  - B. **Factory Method**
  - C. Observer
  - D. **Builder**
- [ ] **MC, easy:** Which of these are structural design patterns?
  - A. **Adapter**
  - B. **Decorator**
  - C. **Facade**
  - D. Strategy
- [ ] **MC, easy:** Which of these are behavioral design patterns?
  - A. **Observer**
  - B. **Strategy**
  - C. **Command**
  - D. Singleton
- [ ] **MC, medium:** Which statements about the Singleton pattern are true?
  - A. **It makes sure a class has one instance**
  - B. **It gives a global point of access**
  - C. **It can make testing harder**
  - D. It is the best way to share all state
- [ ] **MC, medium:** Which statements about the Strategy pattern are true?
  - A. **It puts each algorithm in its own class behind one interface**
  - B. **The algorithm can be swapped at runtime**
  - C. **It replaces long if-else chains on a type**
  - D. The client must inherit from each strategy
- [ ] **MC, medium:** Which statements about the Builder pattern are true?
  - A. **It builds a complex object step by step**
  - B. **It avoids constructors with many parameters**
  - C. **It can produce immutable objects**
  - D. It makes all instances share one object
- [ ] **MC, medium:** Which statements about the Adapter pattern are true?
  - A. **It converts one interface into the one a client expects**
  - B. **It lets incompatible classes work together**
  - C. **It wraps an existing class**
  - D. It creates objects without naming their class
- [ ] **MC, hard:** Which statements about the Decorator pattern are true?
  - A. **It adds behaviour to an object by wrapping it**
  - B. **Decorators can be stacked**
  - C. **Java IO streams use it**
  - D. It changes the class of the object
- [ ] **MC, hard:** Which statements about dependency injection are true?
  - A. **Dependencies are passed in instead of created inside**
  - B. **It makes classes easier to test**
  - C. **Spring does it through its container**
  - D. It needs a global singleton for every class
- [ ] **MC, hard:** Which statements about the Observer pattern are true?
  - A. **A subject notifies registered observers of changes**
  - B. **It decouples the sender from the receivers**
  - C. **Observers that are never unregistered can leak memory**
  - D. Observers must know each other's concrete classes
- [ ] **Blank, easy:** The ________ pattern lets many objects react when one object changes state.  
  Accepted: observer
- [ ] **Blank, easy:** The ________ pattern makes sure a class has only one instance.  
  Accepted: singleton
- [ ] **Blank, easy:** The ________ pattern gives a simple interface to a complex subsystem.  
  Accepted: facade, façade
- [ ] **Blank, medium:** The ________ pattern converts the interface of a class into one that a client expects.  
  Accepted: adapter
- [ ] **Blank, medium:** The ________ pattern builds a complex object step by step.  
  Accepted: builder
- [ ] **Blank, medium:** The ________ pattern puts interchangeable algorithms behind one interface.  
  Accepted: strategy
- [ ] **Blank, medium:** The ________ method pattern lets subclasses decide which class to create.  
  Accepted: factory
- [ ] **Blank, hard:** The ________ pattern adds behaviour to an object by wrapping it in another object with the same interface.  
  Accepted: decorator
- [ ] **Blank, hard:** The ________ pattern turns a request into an object, so it can be queued or undone.  
  Accepted: command
- [ ] **Blank, hard:** The ________ pattern puts a stand-in in front of another object to control access to it, for example to load it lazily.  
  Accepted: proxy
- [ ] **Brief, easy:** What are design patterns, and why use them?
  - Key fact: they are proven, named solutions to common design problems
  - Key fact: they give developers a shared vocabulary
  - Key fact: they should solve a real problem, not be added for their own sake
  - Keywords (grading without AI): pattern, reusable, vocabulary, creational, structural
- [ ] **Brief, medium:** Explain the Strategy pattern with an example.
  - Key fact: each algorithm sits in its own class behind a common interface
  - Key fact: the client holds a strategy and can swap it at runtime
  - Key fact: for example, different payment or pricing methods replace a long if-else chain
  - Keywords (grading without AI): strategy, interface, swap, runtime, if-else
- [ ] **Brief, medium:** What are the drawbacks of the Singleton pattern?
  - Key fact: it is global state, which hides dependencies
  - Key fact: it makes unit tests harder because it cannot easily be replaced
  - Key fact: lazy creation needs care with threads; dependency injection of one shared instance is often better
  - Keywords (grading without AI): singleton, global, testing, thread, dependency injection
- [ ] **Brief, hard:** How does the Decorator pattern differ from inheritance?
  - Key fact: a decorator wraps an object and adds behaviour at runtime
  - Key fact: decorators can be combined in any order without a subclass for every mix
  - Key fact: inheritance fixes the behaviour at compile time and can lead to an explosion of subclasses
  - Keywords (grading without AI): decorator, wrap, runtime, combine, subclass
- [ ] **Brief, hard:** How does dependency injection relate to the Factory and Strategy patterns?
  - Key fact: dependency injection passes collaborators in from outside instead of creating them
  - Key fact: a container such as Spring acts like a factory that builds and wires objects
  - Key fact: injected interfaces let you swap implementations much like strategies, for example fakes in tests
  - Keywords (grading without AI): dependency injection, factory, container, interface, test

## Debugging (bank_software.py)

- [ ] **MC, easy:** Which actions help you find the cause of a bug?
  - A. **Reproduce it reliably**
  - B. **Read the error message and stack trace**
  - C. Change many things at once
  - D. **Pause the program at a breakpoint**
- [ ] **MC, easy:** What does a stack trace tell you?
  - A. **The type of the exception**
  - B. **The chain of method calls that led to it**
  - C. **The line numbers involved**
  - D. The fix for the bug
- [ ] **MC, easy:** Which are useful debugger features?
  - A. **Breakpoints**
  - B. **Stepping through code line by line**
  - C. **Watching variable values**
  - D. Fixing bugs automatically
- [ ] **MC, medium:** Which statements about logging for debugging are true?
  - A. **Levels such as DEBUG and ERROR control how much detail is written**
  - B. **Logs should never contain passwords**
  - C. **Structured logs with request IDs help trace one request**
  - D. Logging everything at ERROR level is best
- [ ] **MC, medium:** Which steps help narrow down a bug?
  - A. **Make a minimal example that shows it**
  - B. **Binary search over the code or the commits**
  - C. **Check recent changes**
  - D. Rewrite the whole module first
- [ ] **MC, medium:** Which can cause a bug that happens only sometimes?
  - A. **A race condition between threads**
  - B. **Depending on the current time**
  - C. **Shared or uninitialised state**
  - D. A typo in a constant
- [ ] **MC, medium:** Which statements about a NullPointerException are true?
  - A. **It happens when code uses a null reference**
  - B. **The stack trace points to the line**
  - C. **Checking where the value was set helps find the cause**
  - D. It is always a bug in the JVM
- [ ] **MC, hard:** Which help debug a memory leak in a Java service?
  - A. **A heap dump**
  - B. **Watching heap use over time**
  - C. **Looking for collections that only grow**
  - D. Restarting the service more often as the fix
- [ ] **MC, hard:** Which help debug a problem that happens only in production?
  - A. **Logs and metrics from production**
  - B. **Tracing a single request across services**
  - C. **Reproducing it with the same data and configuration**
  - D. Pausing the live server in a debugger
- [ ] **MC, hard:** Which statements about a race condition are true?
  - A. **The result depends on the timing of threads**
  - B. **It may not show up in every run**
  - C. **Locks or atomic operations can prevent it**
  - D. Adding print statements proves there is none
- [ ] **Blank, easy:** A ________ pauses a running program at a chosen line so you can inspect its variables.  
  Accepted: breakpoint
- [ ] **Blank, easy:** The list of method calls printed when an exception happens is the stack ________.  
  Accepted: trace
- [ ] **Blank, easy:** Explaining your code line by line to an object to find a bug is called rubber ________ debugging.  
  Accepted: duck
- [ ] **Blank, medium:** Running one line at a time in a debugger is called ________ through the code.  
  Accepted: stepping
- [ ] **Blank, medium:** A bug that comes back after it was fixed is called a ________.  
  Accepted: regression
- [ ] **Blank, medium:** The log level ________ is for detailed messages that help while developing.  
  Accepted: DEBUG, TRACE
- [ ] **Blank, medium:** The smallest piece of code that still shows a bug is a minimal ________ example.  
  Accepted: reproducible
- [ ] **Blank, hard:** A snapshot of every object in a Java program's memory is called a heap ________.  
  Accepted: dump
- [ ] **Blank, hard:** Two threads waiting forever for each other's locks is a ________.  
  Accepted: deadlock
- [ ] **Blank, hard:** A ________ ID passed through every service lets you follow one request in the logs.  
  Accepted: correlation, trace, request
- [ ] **Brief, easy:** Walk me through how you debug a problem.
  - Key fact: reproduce it reliably and note the exact steps and the error
  - Key fact: read the error message and stack trace and form a guess
  - Key fact: test the guess with a debugger, logs or a smaller example, fix it and add a test
  - Keywords (grading without AI): reproduce, stack trace, hypothesis, debugger, test
- [ ] **Brief, medium:** How do you debug a bug you cannot reproduce on your machine?
  - Key fact: collect logs, metrics and the exact input from where it happened
  - Key fact: compare the environments: data, configuration, versions and timing
  - Key fact: add logging around the suspected code and use it to narrow down the cause
  - Keywords (grading without AI): logs, environment, configuration, timing, reproduce
- [ ] **Brief, medium:** How do you use logs well for debugging?
  - Key fact: use levels such as DEBUG, INFO, WARN and ERROR on purpose
  - Key fact: include context such as a request ID, but never passwords or personal data
  - Key fact: use structured logs so they can be searched and filtered
  - Keywords (grading without AI): log level, context, request ID, structured, sensitive
- [ ] **Brief, hard:** How would you find a memory leak in a long-running service?
  - Key fact: watch memory over time to confirm it keeps growing after garbage collection
  - Key fact: take heap dumps at two points in time and compare which objects grow
  - Key fact: look for caches, collections or listeners that are never cleared, fix them and check again
  - Keywords (grading without AI): heap dump, garbage collection, growth, cache, listener
- [ ] **Brief, hard:** How would you debug an intermittent failure in concurrent code?
  - Key fact: suspect shared mutable state and the timing between threads
  - Key fact: log thread names or take thread dumps to see which thread holds which lock
  - Key fact: fix it with proper synchronization or immutable data, then stress-test to confirm
  - Keywords (grading without AI): race condition, thread, lock, thread dump, synchronization

## Agile (bank_software.py)

- [ ] **MC, easy:** Which of these are Scrum events?
  - A. **Sprint planning**
  - B. **Daily scrum**
  - C. Gantt review
  - D. **Sprint retrospective**
- [ ] **MC, easy:** Which of these are Scrum roles?
  - A. **Product owner**
  - B. **Scrum master**
  - C. **Developers**
  - D. Project sponsor
- [ ] **MC, easy:** Which values come from the Agile Manifesto?
  - A. **Individuals and interactions over processes and tools**
  - B. **Working software over comprehensive documentation**
  - C. **Responding to change over following a plan**
  - D. Contract negotiation over customer collaboration
- [ ] **MC, medium:** Which statements about a user story are true?
  - A. **It describes a need from the user's point of view**
  - B. **It often reads As a ..., I want ..., so that ...**
  - C. **It has acceptance criteria**
  - D. It must describe the database design
- [ ] **MC, medium:** Which statements about Kanban are true?
  - A. **Work is shown on a board in columns**
  - B. **It limits work in progress**
  - C. **It has no fixed-length sprints**
  - D. It requires a Scrum master
- [ ] **MC, medium:** Which statements about the product backlog are true?
  - A. **It is an ordered list of work**
  - B. **The product owner orders it**
  - C. **It changes over time**
  - D. It is fixed at the start of the project
- [ ] **MC, medium:** Which statements about the daily scrum are true?
  - A. **It is short, usually 15 minutes**
  - B. **The team plans the next day of work**
  - C. **It is for the developers**
  - D. It is a status report to managers
- [ ] **MC, hard:** Which statements about story points are true?
  - A. **They estimate relative effort**
  - B. **Teams often use a Fibonacci-like scale**
  - C. **They should not be compared between teams**
  - D. One point equals one hour of work
- [ ] **MC, hard:** Which statements about velocity are true?
  - A. **It is the amount of work a team finishes per sprint**
  - B. **It helps plan future sprints**
  - C. **It should not be used to rank teams**
  - D. Raising it every sprint is the main goal
- [ ] **MC, hard:** Which statements about a definition of done are true?
  - A. **It is a shared checklist for finished work**
  - B. **It can include tests and code review**
  - C. **It keeps quality consistent**
  - D. Each developer has their own
- [ ] **Blank, easy:** In Scrum, a fixed period of work, usually one to four weeks, is called a ________.  
  Accepted: sprint
- [ ] **Blank, easy:** The person who orders the product backlog in Scrum is the product ________.  
  Accepted: owner
- [ ] **Blank, easy:** A short description of a feature from the user's point of view is a user ________.  
  Accepted: story
- [ ] **Blank, medium:** The meeting at the end of a sprint where the team looks at how it worked is the sprint ________.  
  Accepted: retrospective, retro
- [ ] **Blank, medium:** Kanban limits work in ________.  
  Accepted: progress
- [ ] **Blank, medium:** The meeting where the team shows what it built during the sprint is the sprint ________.  
  Accepted: review
- [ ] **Blank, medium:** The conditions a story must meet to be accepted are its acceptance ________.  
  Accepted: criteria
- [ ] **Blank, hard:** The amount of work a team usually finishes in a sprint is its ________.  
  Accepted: velocity
- [ ] **Blank, hard:** A chart of remaining work against time in a sprint is a ________ chart.  
  Accepted: burndown, burn-down
- [ ] **Blank, hard:** Estimating by having everyone show a card at the same moment is called planning ________.  
  Accepted: poker
- [ ] **Brief, easy:** What is Agile, and how is it different from a waterfall approach?
  - Key fact: Agile delivers working software in short iterations and adapts to feedback
  - Key fact: waterfall plans everything up front and goes through fixed phases once
  - Key fact: Agile values working with the customer and responding to change
  - Keywords (grading without AI): iteration, feedback, waterfall, change, collaboration
- [ ] **Brief, medium:** Describe the Scrum events and what each is for.
  - Key fact: sprint planning chooses the work and the goal of the sprint
  - Key fact: the daily scrum is a short daily check that plans the next day
  - Key fact: the sprint review shows the result to stakeholders, and the retrospective improves how the team works
  - Keywords (grading without AI): sprint planning, daily scrum, review, retrospective, goal
- [ ] **Brief, medium:** What makes a good user story?
  - Key fact: it says who wants what and why, from the user's point of view
  - Key fact: it is small enough to finish in one sprint
  - Key fact: it has clear acceptance criteria, so the team knows when it is done
  - Keywords (grading without AI): user story, acceptance criteria, small, value, INVEST
- [ ] **Brief, hard:** How would you estimate work in an Agile team?
  - Key fact: estimate relative effort with story points rather than hours
  - Key fact: use team discussion such as planning poker to bring out different views
  - Key fact: plan with past velocity, and estimate again when you learn more
  - Keywords (grading without AI): story points, planning poker, velocity, relative, uncertainty
- [ ] **Brief, hard:** What do you do when a sprint goal is at risk?
  - Key fact: raise it early, for example in the daily scrum
  - Key fact: work with the product owner to cut or split scope while keeping the goal
  - Key fact: talk about the cause in the retrospective so it does not happen again
  - Keywords (grading without AI): risk, scope, product owner, transparency, retrospective

## Java (bank_software.py)

- [ ] **MC, easy:** Which statements about Java are true?
  - A. **Source code is compiled to bytecode that runs on the JVM**
  - B. **A class can implement several interfaces**
  - C. A class can extend several classes
  - D. **Unused objects are reclaimed by garbage collection**
- [ ] **MC, easy:** Which of these are primitive types in Java?
  - A. **int**
  - B. **boolean**
  - C. **double**
  - D. String
- [ ] **MC, easy:** Which statements about String in Java are true?
  - A. **String objects are immutable**
  - B. **Strings should be compared with equals**
  - C. == compares the text of two strings
  - D. **StringBuilder is better for building a string in a loop**
- [ ] **MC, medium:** Which statements about Java collections are true?
  - A. **ArrayList keeps insertion order**
  - B. **HashSet does not allow duplicates**
  - C. HashMap keeps its keys sorted
  - D. **TreeMap keeps its keys sorted**
- [ ] **MC, medium:** Which statements about checked and unchecked exceptions are true?
  - A. **Checked exceptions must be caught or declared**
  - B. **RuntimeException is unchecked**
  - C. **IOException is checked**
  - D. NullPointerException is checked
- [ ] **MC, medium:** Which statements about Java streams are true?
  - A. **map transforms each element**
  - B. **filter keeps the matching elements**
  - C. **Streams are lazy until a terminal operation runs**
  - D. A stream can be reused after a terminal operation
- [ ] **MC, medium:** Which statements about Optional are true?
  - A. **It shows that a value may be missing**
  - B. **orElse gives a default value**
  - C. **It helps avoid NullPointerException**
  - D. It should be used for every field
- [ ] **MC, hard:** Which statements about JVM memory are true?
  - A. **Objects live on the heap**
  - B. **Each thread has its own stack**
  - C. **The garbage collector frees unreachable objects**
  - D. Local primitive variables live on the heap
- [ ] **MC, hard:** Which statements about concurrency in Java are true?
  - A. **synchronized lets one thread at a time into a block for a given lock**
  - B. **volatile makes a variable's changes visible to other threads**
  - C. **ConcurrentHashMap is safe for concurrent use**
  - D. HashMap is safe for concurrent writes
- [ ] **MC, hard:** Which statements about records in Java are true?
  - A. **They are short immutable data carriers**
  - B. **They get equals, hashCode and toString automatically**
  - C. **Their fields are final**
  - D. They can extend another class
- [ ] **Blank, easy:** In Java, the ________ keyword makes a variable impossible to reassign.  
  Accepted: final
- [ ] **Blank, easy:** Java source code is compiled to ________ that runs on the JVM.  
  Accepted: bytecode, byte code
- [ ] **Blank, easy:** A Java program starts from the public static void ________ method.  
  Accepted: main
- [ ] **Blank, medium:** The ________ block runs after try and catch whether or not an exception was thrown.  
  Accepted: finally
- [ ] **Blank, medium:** The ________ interface is implemented by both ArrayList and LinkedList.  
  Accepted: List
- [ ] **Blank, medium:** A ________ expression such as x -> x * 2 is a short way to write a function in Java.  
  Accepted: lambda
- [ ] **Blank, medium:** The try-with-________ statement closes files and connections automatically.  
  Accepted: resources
- [ ] **Blank, hard:** The ________ keyword makes changes to a variable visible to all threads.  
  Accepted: volatile
- [ ] **Blank, hard:** Java ________ let a class or method work with any type, as in List<String>.  
  Accepted: generics
- [ ] **Blank, hard:** The JVM's ________ compiler turns frequently run bytecode into native machine code while the program runs.  
  Accepted: JIT, just-in-time, just in time
- [ ] **Brief, easy:** What is the difference between the JDK, the JRE and the JVM?
  - Key fact: the JVM runs Java bytecode
  - Key fact: the JRE is the JVM plus the libraries needed to run programs
  - Key fact: the JDK is the JRE plus tools such as the compiler for building programs
  - Keywords (grading without AI): JDK, JRE, JVM, bytecode, compiler
- [ ] **Brief, medium:** How does a HashMap work in Java?
  - Key fact: it stores key-value pairs in buckets chosen from the key's hashCode
  - Key fact: keys in the same bucket are told apart with equals, and long buckets turn into trees
  - Key fact: it resizes when it gets too full, and it is not thread-safe
  - Keywords (grading without AI): hashCode, equals, bucket, resize, thread-safe
- [ ] **Brief, medium:** Explain checked and unchecked exceptions in Java.
  - Key fact: checked exceptions such as IOException must be caught or declared with throws
  - Key fact: unchecked exceptions extend RuntimeException and usually mean a programming error
  - Key fact: use checked exceptions for conditions the caller can recover from, and never swallow exceptions silently
  - Keywords (grading without AI): checked, unchecked, RuntimeException, throws, catch
- [ ] **Brief, hard:** How does garbage collection work in Java?
  - Key fact: the garbage collector frees objects that no live reference can reach
  - Key fact: the heap is split into generations because most objects die young
  - Key fact: collectors such as G1 balance pause times against throughput, and memory can still leak through references kept by mistake
  - Keywords (grading without AI): garbage collector, heap, generation, G1, reachable
- [ ] **Brief, hard:** How do you make code thread-safe in Java?
  - Key fact: avoid shared mutable state and prefer immutable objects
  - Key fact: guard shared state with synchronized, locks or atomic classes
  - Key fact: use concurrent collections and executors instead of managing threads by hand
  - Keywords (grading without AI): immutable, synchronized, atomic, ConcurrentHashMap, executor

## Spring Boot (bank_software.py)

- [ ] **MC, easy:** Which annotations are commonly used in a Spring Boot REST application?
  - A. **@RestController**
  - B. **@Service**
  - C. **@Component**
  - D. @EntryPoint
- [ ] **MC, easy:** Which statements about Spring Boot are true?
  - A. **It auto-configures beans from what is on the classpath**
  - B. **It can run with an embedded web server**
  - C. It needs an external application server to run
  - D. **Starters bundle common dependencies**
- [ ] **MC, easy:** Where can Spring Boot read its configuration from?
  - A. **application.properties**
  - B. **application.yml**
  - C. **Environment variables**
  - D. pom.xml
- [ ] **MC, medium:** Which statements about dependency injection in Spring are true?
  - A. **Constructor injection is recommended**
  - B. **Spring creates beans and wires them together**
  - C. **@Autowired can mark an injection point**
  - D. The developer must create every bean with new
- [ ] **MC, medium:** Which annotations map HTTP requests in Spring MVC?
  - A. **@GetMapping**
  - B. **@PostMapping**
  - C. **@RequestMapping**
  - D. @Entity
- [ ] **MC, medium:** Which statements about Spring profiles are true?
  - A. **They switch configuration per environment**
  - B. **application-dev.yml can hold settings for a dev profile**
  - C. **spring.profiles.active chooses the profile**
  - D. Profiles change the Java version
- [ ] **MC, medium:** Which statements about Spring Data JPA are true?
  - A. **Repository interfaces get their implementation at runtime**
  - B. **Method names such as findByEmail produce queries**
  - C. **@Entity maps a class to a table**
  - D. You must write SQL for every query
- [ ] **MC, hard:** Which statements about @Transactional are true?
  - A. **It runs a method inside a database transaction**
  - B. **A runtime exception rolls it back by default**
  - C. **A call from another method of the same class skips the proxy**
  - D. It works on private methods
- [ ] **MC, hard:** Which statements about Spring bean scopes are true?
  - A. **singleton is the default scope**
  - B. **prototype gives a new instance each time**
  - C. **request scope lives for one HTTP request**
  - D. Singleton beans are created once per thread
- [ ] **MC, hard:** Which can cause the N+1 query problem with JPA?
  - A. **Lazy-loading a collection inside a loop**
  - B. **Loading the parents, then each child list separately**
  - C. **Missing fetch joins**
  - D. Looking up one entity by its primary key
- [ ] **Blank, easy:** The ________ annotation on the main class turns on auto-configuration in Spring Boot.  
  Accepted: @SpringBootApplication, SpringBootApplication
- [ ] **Blank, easy:** Spring Boot ________, such as spring-boot-starter-web, bundle common dependencies.  
  Accepted: starters
- [ ] **Blank, easy:** An object created and managed by the Spring container is called a ________.  
  Accepted: bean
- [ ] **Blank, medium:** The annotation ________ binds a part of the URL, such as the id in /users/{id}, to a method parameter.  
  Accepted: @PathVariable, PathVariable
- [ ] **Blank, medium:** The annotation ________ reads the JSON body of a request into an object.  
  Accepted: @RequestBody, RequestBody
- [ ] **Blank, medium:** The property spring.profiles.________ sets which profile is used.  
  Accepted: active
- [ ] **Blank, medium:** Spring Boot ________ adds endpoints such as /health for monitoring.  
  Accepted: Actuator
- [ ] **Blank, hard:** The ________ annotation runs a method inside a database transaction.  
  Accepted: @Transactional, Transactional
- [ ] **Blank, hard:** Spring implements @Transactional with a ________ that wraps the bean.  
  Accepted: proxy
- [ ] **Blank, hard:** The ________ annotation marks a class whose methods handle exceptions for all controllers.  
  Accepted: @ControllerAdvice, ControllerAdvice, @RestControllerAdvice, RestControllerAdvice
- [ ] **Brief, easy:** What does Spring Boot add on top of the Spring Framework?
  - Key fact: auto-configuration sets up beans based on what is on the classpath
  - Key fact: starters bundle the dependencies for common features
  - Key fact: an embedded server and sensible defaults let the app run with java -jar
  - Keywords (grading without AI): auto-configuration, starter, embedded server, defaults, jar
- [ ] **Brief, medium:** How does dependency injection work in Spring?
  - Key fact: the Spring container creates beans and passes their dependencies in
  - Key fact: constructor injection is preferred because dependencies are explicit and can be final
  - Key fact: it makes code easier to test because dependencies can be replaced
  - Keywords (grading without AI): container, bean, constructor injection, test, @Autowired
- [ ] **Brief, medium:** How would you build a REST endpoint in Spring Boot?
  - Key fact: create a class with @RestController and map methods with @GetMapping or @PostMapping
  - Key fact: read input with @PathVariable, @RequestParam or @RequestBody and validate it
  - Key fact: return objects that become JSON, with the right status codes, and handle errors in a @ControllerAdvice
  - Keywords (grading without AI): @RestController, @GetMapping, @RequestBody, JSON, @ControllerAdvice
- [ ] **Brief, hard:** How does @Transactional work, and what are its pitfalls?
  - Key fact: Spring wraps the bean in a proxy that starts the transaction and commits or rolls it back
  - Key fact: by default only unchecked exceptions cause a rollback
  - Key fact: calls from another method of the same class skip the proxy, and it does not work on private methods
  - Keywords (grading without AI): @Transactional, proxy, rollback, self-invocation, propagation
- [ ] **Brief, hard:** How would you find and fix the N+1 query problem in a Spring Data JPA app?
  - Key fact: turn on SQL logging or use a profiler to spot one query per row
  - Key fact: fetch related data with a fetch join, an entity graph or batch fetching
  - Key fact: return DTO projections when only some fields are needed
  - Keywords (grading without AI): N+1, lazy loading, fetch join, entity graph, DTO

## MySQL (bank_software.py)

- [ ] **MC, easy:** Which statements about indexes in MySQL are true?
  - A. **They can speed up reads**
  - B. **They add work to every write**
  - C. They make every query faster
  - D. **A primary key is indexed**
- [ ] **MC, easy:** Which of these are MySQL data types?
  - A. **VARCHAR**
  - B. **INT**
  - C. **DATETIME**
  - D. ARRAYLIST
- [ ] **MC, easy:** Which statements about AUTO_INCREMENT are true?
  - A. **It gives each new row the next number**
  - B. **It is often used for primary keys**
  - C. Numbers of deleted rows are reused at once
  - D. **A table can have only one AUTO_INCREMENT column**
- [ ] **MC, medium:** Which statements about InnoDB are true?
  - A. **It supports transactions**
  - B. **It supports foreign keys**
  - C. **It uses row-level locking**
  - D. It cannot recover after a crash
- [ ] **MC, medium:** Which statements about EXPLAIN in MySQL are true?
  - A. **It shows how MySQL will run a query**
  - B. **type ALL means a full table scan**
  - C. **The key column shows which index is used**
  - D. It changes the data
- [ ] **MC, medium:** Which statements about character sets in MySQL are true?
  - A. **utf8mb4 stores every Unicode character, including emoji**
  - B. **The old utf8 (utf8mb3) cannot store emoji**
  - C. **The collation decides how text is compared and sorted**
  - D. The character set has no effect on storage
- [ ] **MC, medium:** Which statements about composite indexes are true?
  - A. **The order of the columns matters**
  - B. **A query on the first column alone can use the index**
  - C. **A query on the second column alone usually cannot use it well**
  - D. Column order never matters
- [ ] **MC, hard:** Which statements about MySQL replication are true?
  - A. **The source writes changes to the binary log**
  - B. **Replicas apply those changes**
  - C. **Replicas can lag behind**
  - D. Replication makes backups unnecessary
- [ ] **MC, hard:** Which statements about isolation levels in InnoDB are true?
  - A. **REPEATABLE READ is the default**
  - B. **READ COMMITTED sees only committed data**
  - C. **SERIALIZABLE is the strictest**
  - D. READ UNCOMMITTED is the default
- [ ] **MC, hard:** Which help when a MySQL table becomes very large?
  - A. **Partitioning by a range such as date**
  - B. **Archiving old rows**
  - C. **Indexes that fit the main queries**
  - D. Dropping the primary key
- [ ] **Blank, easy:** The default MySQL storage engine, which supports transactions, is ________.  
  Accepted: InnoDB
- [ ] **Blank, easy:** The MySQL command ________ DATABASES lists all databases.  
  Accepted: SHOW
- [ ] **Blank, easy:** The column option ________ gives each new row the next number automatically.  
  Accepted: AUTO_INCREMENT
- [ ] **Blank, medium:** The character set ________ stores every Unicode character, including emoji.  
  Accepted: utf8mb4
- [ ] **Blank, medium:** In EXPLAIN output, the type ________ means a full table scan.  
  Accepted: ALL
- [ ] **Blank, medium:** The tool ________ makes a logical backup of a MySQL database as SQL statements.  
  Accepted: mysqldump
- [ ] **Blank, medium:** The ________ query log records queries that take longer than a set time.  
  Accepted: slow
- [ ] **Blank, hard:** MySQL replication sends changes from the source through the ________ log.  
  Accepted: binary, bin
- [ ] **Blank, hard:** The default isolation level of InnoDB is REPEATABLE ________.  
  Accepted: READ
- [ ] **Blank, hard:** Splitting one large table into parts by a key such as date, inside the same server, is called ________.  
  Accepted: partitioning
- [ ] **Brief, easy:** Why is InnoDB the usual storage engine in MySQL?
  - Key fact: it supports transactions with commit and rollback
  - Key fact: it supports foreign keys and row-level locking
  - Key fact: it recovers after a crash using its logs
  - Keywords (grading without AI): InnoDB, transaction, foreign key, row-level locking, crash recovery
- [ ] **Brief, medium:** How do you decide which indexes to add to a MySQL table?
  - Key fact: index the columns used in WHERE, JOIN and ORDER BY of the most frequent queries
  - Key fact: in a composite index, put the columns in the order the queries filter by
  - Key fact: check with EXPLAIN, and avoid too many indexes because each one slows writes
  - Keywords (grading without AI): index, composite, EXPLAIN, WHERE, write
- [ ] **Brief, medium:** How would you back up and restore a MySQL database?
  - Key fact: mysqldump makes a logical backup as SQL statements
  - Key fact: for large databases use physical backups or snapshots, plus binary logs for point-in-time recovery
  - Key fact: test restores regularly, because an untested backup may not work
  - Keywords (grading without AI): mysqldump, binary log, point-in-time, restore, snapshot
- [ ] **Brief, hard:** How does MySQL replication work, and what is it used for?
  - Key fact: the source writes changes to the binary log, and replicas read and apply them
  - Key fact: replicas spread read traffic and give a standby for failover
  - Key fact: replication is usually asynchronous, so replicas can lag, and it does not replace backups
  - Keywords (grading without AI): binary log, replica, lag, failover, read scaling
- [ ] **Brief, hard:** How would you find out what is making a MySQL database slow?
  - Key fact: turn on the slow query log and find the worst queries
  - Key fact: run EXPLAIN on them to see scans and missing indexes
  - Key fact: check server metrics such as connections, locks and buffer pool use, then fix queries, indexes or settings
  - Keywords (grading without AI): slow query log, EXPLAIN, index, lock, buffer pool

## Microservices (bank_software.py)

- [ ] **MC, easy:** Which are common challenges of microservices?
  - A. **Network calls between services can fail**
  - B. **Data is spread over several databases**
  - C. Services can never be deployed on their own
  - D. **Tracing one request across services is harder**
- [ ] **MC, easy:** Which statements about microservices are true?
  - A. **Each service does one business job**
  - B. **Services can be deployed on their own**
  - C. **Services talk over the network**
  - D. All services must use the same language
- [ ] **MC, easy:** Which are common ways for microservices to talk to each other?
  - A. **REST over HTTP**
  - B. **gRPC**
  - C. **Messages through a broker**
  - D. Sharing objects in memory
- [ ] **MC, medium:** Which statements about a database per service are true?
  - A. **Each service owns its data**
  - B. **Other services get that data through its API**
  - C. **Services can change their schema on their own**
  - D. All services should write to one shared table
- [ ] **MC, medium:** Which statements about an API gateway are true?
  - A. **It is one entry point for clients**
  - B. **It can handle authentication and rate limiting**
  - C. **It routes requests to services**
  - D. It stores all the business data
- [ ] **MC, medium:** Which statements about service discovery are true?
  - A. **Services find each other's addresses at runtime**
  - B. **A registry such as Eureka or Consul can hold the addresses**
  - C. **Kubernetes gives services DNS names**
  - D. Addresses must be hard-coded
- [ ] **MC, medium:** Which statements about a circuit breaker are true?
  - A. **It stops calls to a failing service for a while**
  - B. **It lets the caller fail fast**
  - C. **It tries the service again after a timeout**
  - D. It makes the failing service faster
- [ ] **MC, hard:** Which statements about the Saga pattern are true?
  - A. **It splits a business transaction into local transactions per service**
  - B. **Compensating actions undo earlier steps on failure**
  - C. **It can be orchestrated or choreographed**
  - D. It gives one ACID transaction across services
- [ ] **MC, hard:** Which help you observe a microservices system?
  - A. **Distributed tracing**
  - B. **Central logging**
  - C. **Metrics for each service**
  - D. Reading each server's console by hand
- [ ] **MC, hard:** Which statements about eventual consistency between services are true?
  - A. **Data in different services can be briefly out of sync**
  - B. **Events spread changes between services**
  - C. **The outbox pattern stops events from being lost**
  - D. Every read everywhere always sees the latest write
- [ ] **Blank, easy:** An API ________ gives clients one entry point to many backend services.  
  Accepted: gateway
- [ ] **Blank, easy:** An application built and deployed as one unit is called a ________.  
  Accepted: monolith
- [ ] **Blank, easy:** Microservices often call each other through ________ APIs over HTTP.  
  Accepted: REST, RESTful
- [ ] **Blank, medium:** A ________ breaker stops calls to a failing service for a while.  
  Accepted: circuit
- [ ] **Blank, medium:** Finding the network address of a service at runtime is called service ________.  
  Accepted: discovery
- [ ] **Blank, medium:** Each microservice should own its ________ and not share it with other services.  
  Accepted: database, data
- [ ] **Blank, medium:** ________ is a fast remote call framework from Google that uses Protocol Buffers.  
  Accepted: gRPC
- [ ] **Blank, hard:** The ________ pattern runs a series of local transactions with compensating actions instead of one distributed transaction.  
  Accepted: saga
- [ ] **Blank, hard:** The transactional ________ pattern saves events in the service's own database before sending them, so none are lost.  
  Accepted: outbox
- [ ] **Blank, hard:** Distributed ________ follows one request across several services.  
  Accepted: tracing
- [ ] **Brief, easy:** What are microservices, and how do they differ from a monolith?
  - Key fact: a monolith is one deployable application, while microservices are small services that each do one job
  - Key fact: each microservice can be built, deployed and scaled on its own
  - Key fact: microservices add network calls, distributed data and more operational work
  - Keywords (grading without AI): monolith, independent, deploy, scale, network
- [ ] **Brief, medium:** How do microservices communicate, and when would you choose each way?
  - Key fact: synchronous calls such as REST or gRPC suit requests that need an answer now
  - Key fact: asynchronous messages through a broker suit events and background work
  - Key fact: asynchronous messaging decouples services but brings eventual consistency
  - Keywords (grading without AI): REST, gRPC, message broker, asynchronous, event
- [ ] **Brief, medium:** How do you handle a slow or failing downstream service?
  - Key fact: set a timeout on every call
  - Key fact: retry with backoff, but only for safe, idempotent calls
  - Key fact: use a circuit breaker and a fallback so the failure does not spread
  - Keywords (grading without AI): timeout, retry, backoff, circuit breaker, fallback
- [ ] **Brief, hard:** How do you keep data consistent across microservices without distributed transactions?
  - Key fact: each service updates its own database in a local transaction
  - Key fact: a saga links the steps with events or an orchestrator and runs compensating actions on failure
  - Key fact: the outbox pattern publishes events reliably, and the system accepts eventual consistency
  - Keywords (grading without AI): saga, compensating, outbox, event, eventual consistency
- [ ] **Brief, hard:** When should a team not use microservices?
  - Key fact: when the team is small or the domain is not yet well understood
  - Key fact: when the app does not need independent scaling or deployment
  - Key fact: a well-structured modular monolith is simpler and can be split later
  - Keywords (grading without AI): monolith, team size, complexity, domain, modular

## Redis (bank_software.py)

- [ ] **MC, easy:** Which of these are Redis data types?
  - A. **String**
  - B. **List**
  - C. **Sorted set**
  - D. Foreign key
- [ ] **MC, easy:** Which statements about Redis are true?
  - A. **It keeps data in memory**
  - B. **It is a key-value store**
  - C. **It is often used as a cache**
  - D. It can only keep data on disk
- [ ] **MC, easy:** Which are common uses of Redis?
  - A. **Caching**
  - B. **Storing sessions**
  - C. **Rate-limiting counters**
  - D. Storing large video files
- [ ] **MC, medium:** Which statements about Redis persistence are true?
  - A. **RDB takes snapshots at intervals**
  - B. **AOF logs every write**
  - C. **Both can be turned on together**
  - D. Redis cannot save data to disk
- [ ] **MC, medium:** Which statements about expiry in Redis are true?
  - A. **EXPIRE sets a time to live on a key**
  - B. **TTL shows the time left**
  - C. **A key without expiry stays until it is deleted or evicted**
  - D. Every key expires after one hour by default
- [ ] **MC, medium:** Which statements about the cache-aside pattern are true?
  - A. **The app reads the cache first**
  - B. **On a miss the app loads from the database and fills the cache**
  - C. **A write updates the database and removes or updates the cached value**
  - D. The cache writes to the database by itself
- [ ] **MC, medium:** Which Redis commands work on sorted sets?
  - A. **ZADD**
  - B. **ZRANGE**
  - C. **ZSCORE**
  - D. LPUSH
- [ ] **MC, hard:** Which statements about Redis eviction policies are true?
  - A. **allkeys-lru removes the least recently used keys**
  - B. **volatile-ttl removes keys that have the shortest time to live**
  - C. **noeviction returns errors when memory is full**
  - D. Every policy removes keys at random
- [ ] **MC, hard:** Which statements about Redis running commands on one thread are true?
  - A. **Commands run one at a time, so each command is atomic**
  - B. **A slow command such as KEYS blocks other clients**
  - C. **Newer versions can use I/O threads for network work**
  - D. Redis can serve only one client connection
- [ ] **MC, hard:** Which statements about Redis Cluster and Sentinel are true?
  - A. **Cluster splits keys across nodes using hash slots**
  - B. **Sentinel watches a primary and fails over to a replica**
  - C. **Cluster has 16384 hash slots**
  - D. Sentinel shards data across nodes
- [ ] **Blank, easy:** Setting a ________ on a Redis key makes it expire after a number of seconds.  
  Accepted: TTL, time to live, expiry
- [ ] **Blank, easy:** The Redis command ________ stores a value under a key.  
  Accepted: SET
- [ ] **Blank, easy:** The Redis command ________ increases a number stored at a key by one.  
  Accepted: INCR
- [ ] **Blank, medium:** A Redis sorted ________ keeps members ordered by score, which suits leaderboards.  
  Accepted: set
- [ ] **Blank, medium:** The Redis persistence mode that logs every write is called ________.  
  Accepted: AOF, append-only file, append only file
- [ ] **Blank, medium:** Reading the cache first and loading from the database on a miss is the cache-________ pattern.  
  Accepted: aside
- [ ] **Blank, medium:** A Redis ________ stores field-value pairs under one key, like a small object.  
  Accepted: hash
- [ ] **Blank, hard:** Many requests hitting the database at once when a popular cache key expires is a cache ________.  
  Accepted: stampede
- [ ] **Blank, hard:** Redis Cluster splits keys into 16384 hash ________.  
  Accepted: slots
- [ ] **Blank, hard:** Redis ________ watches the primary and promotes a replica when it fails.  
  Accepted: Sentinel
- [ ] **Brief, easy:** What is Redis, and what is it used for?
  - Key fact: Redis is an in-memory key-value store, so reads and writes are very fast
  - Key fact: it is used for caching, sessions, counters, rate limiting and queues
  - Key fact: it has data types such as strings, hashes, lists, sets and sorted sets
  - Keywords (grading without AI): in-memory, key-value, cache, session, data types
- [ ] **Brief, medium:** How would you use Redis as a cache in front of a database?
  - Key fact: use cache-aside: read Redis first, and on a miss read the database and store the result
  - Key fact: set a TTL so stale data expires
  - Key fact: on a write, update the database and delete or update the cached key
  - Keywords (grading without AI): cache-aside, TTL, miss, invalidate, database
- [ ] **Brief, medium:** How does Redis keep data across restarts?
  - Key fact: RDB writes snapshots of the data at intervals
  - Key fact: AOF logs every write and replays it on start
  - Key fact: RDB is compact but can lose recent writes, AOF loses less but is larger, and the two can be combined
  - Keywords (grading without AI): RDB, AOF, snapshot, durability, restart
- [ ] **Brief, hard:** How would you build a rate limiter with Redis?
  - Key fact: keep a counter per user and time window with INCR and EXPIRE
  - Key fact: or keep a sorted set of timestamps for a sliding window
  - Key fact: each Redis command is atomic, and a Lua script keeps several steps atomic together
  - Keywords (grading without AI): INCR, EXPIRE, sliding window, atomic, Lua
- [ ] **Brief, hard:** What problems can a cache cause, and how do you prevent them?
  - Key fact: stale data, handled with TTLs and invalidation on writes
  - Key fact: a stampede when a hot key expires, handled with a lock, early refresh or random TTLs
  - Key fact: the cache becoming a single point of failure, handled with replicas and a fallback to the database
  - Keywords (grading without AI): stale, stampede, TTL, invalidation, fallback

## Message Queues (bank_software.py)

- [ ] **MC, easy:** Which benefits does a message queue give?
  - A. **It decouples the producer from the consumer**
  - B. **It smooths out traffic spikes**
  - C. It guarantees zero latency
  - D. **It lets consumers work at their own pace**
- [ ] **MC, easy:** Which of these are message brokers?
  - A. **RabbitMQ**
  - B. **Apache Kafka**
  - C. **Amazon SQS**
  - D. Nginx
- [ ] **MC, easy:** Which statements about producers and consumers are true?
  - A. **A producer sends messages**
  - B. **A consumer receives and processes them**
  - C. The producer and the consumer must run at the same time
  - D. **Several consumers can share the work**
- [ ] **MC, medium:** Which statements about delivery guarantees are true?
  - A. **At-most-once can lose messages**
  - B. **At-least-once can deliver a message twice**
  - C. **Consumers of at-least-once delivery should be idempotent**
  - D. At-least-once never duplicates
- [ ] **MC, medium:** Which statements about acknowledgements are true?
  - A. **A consumer acknowledges a message after processing it**
  - B. **A message that is not acknowledged can be delivered again**
  - C. **Acknowledging before processing can lose work if the consumer crashes**
  - D. Acknowledgements are sent only by producers
- [ ] **MC, medium:** Which statements about queues and publish-subscribe are true?
  - A. **In a queue each message goes to one consumer**
  - B. **In publish-subscribe each subscriber gets a copy**
  - C. **Kafka consumer groups split partitions among their consumers**
  - D. Publish-subscribe sends each message to only one subscriber
- [ ] **MC, medium:** Which statements about a dead letter queue are true?
  - A. **It holds messages that keep failing**
  - B. **It stops one bad message from blocking the rest**
  - C. **Messages in it can be inspected and replayed**
  - D. It deletes failing messages at once
- [ ] **MC, hard:** Which statements about message order are true?
  - A. **Kafka keeps order within a partition**
  - B. **Messages with the same key go to the same partition**
  - C. **Parallel consumers can process messages out of order**
  - D. Kafka keeps order across all partitions
- [ ] **MC, hard:** Which make a consumer safe when a message arrives twice?
  - A. **Storing the IDs of processed messages**
  - B. **Making the operation idempotent**
  - C. **A unique constraint in the database**
  - D. Hoping duplicates are rare
- [ ] **MC, hard:** Which statements about backpressure are true?
  - A. **It is needed when consumers cannot keep up**
  - B. **A queue that keeps growing is a sign of it**
  - C. **Adding consumers or slowing producers helps**
  - D. It cannot happen with a message queue
- [ ] **Blank, easy:** A ________ letter queue holds messages that could not be processed.  
  Accepted: dead
- [ ] **Blank, easy:** The service that sends messages to a queue is called the ________.  
  Accepted: producer, publisher
- [ ] **Blank, easy:** The service that reads messages from a queue is called the ________.  
  Accepted: consumer, subscriber
- [ ] **Blank, medium:** A consumer sends an ________ to tell the broker a message was processed.  
  Accepted: acknowledgement, acknowledgment, ack
- [ ] **Blank, medium:** Sending each message to every subscriber is called publish-________.  
  Accepted: subscribe
- [ ] **Blank, medium:** With at-least-once delivery, consumers must be ________, so a repeated message does no harm.  
  Accepted: idempotent
- [ ] **Blank, medium:** In RabbitMQ, producers send messages to an ________, which routes them to queues.  
  Accepted: exchange
- [ ] **Blank, hard:** Kafka keeps the order of messages only within one ________.  
  Accepted: partition
- [ ] **Blank, hard:** Processing each message exactly one time, as the consumer sees it, is called ________ delivery.  
  Accepted: exactly-once, exactly once
- [ ] **Blank, hard:** When consumers cannot keep up and producers must slow down, the system needs ________.  
  Accepted: backpressure, back pressure
- [ ] **Brief, easy:** Why would you put a message queue between two services?
  - Key fact: it decouples them, so the sender does not wait for the receiver
  - Key fact: it absorbs traffic spikes and lets consumers work at their own pace
  - Key fact: messages are kept while the consumer is down and processed when it is back
  - Keywords (grading without AI): decouple, asynchronous, spike, buffer, reliable
- [ ] **Brief, medium:** Explain at-most-once, at-least-once and exactly-once delivery.
  - Key fact: at-most-once may lose messages but never repeats them
  - Key fact: at-least-once never loses them but may deliver some twice
  - Key fact: exactly-once is hard; in practice it is at-least-once with idempotent consumers
  - Keywords (grading without AI): at-most-once, at-least-once, exactly-once, duplicate, idempotent
- [ ] **Brief, medium:** What happens to a message that keeps failing, and how should you handle it?
  - Key fact: the consumer retries it a few times, ideally with backoff
  - Key fact: after the limit it goes to a dead letter queue so it does not block the others
  - Key fact: someone inspects the dead letter queue, fixes the cause and replays the messages
  - Keywords (grading without AI): retry, backoff, dead letter queue, poison message, replay
- [ ] **Brief, hard:** How would you keep messages in order while still processing them in parallel?
  - Key fact: send related messages with the same key so they land in the same partition
  - Key fact: one consumer handles each partition, so order holds per key
  - Key fact: parallelism comes from having many partitions, while order across different keys is not kept
  - Keywords (grading without AI): partition, key, order, consumer group, parallel
- [ ] **Brief, hard:** Compare RabbitMQ and Kafka.
  - Key fact: RabbitMQ is a broker that routes messages to queues and deletes them once acknowledged
  - Key fact: Kafka is a distributed log that keeps messages for a set time so consumers can replay them
  - Key fact: RabbitMQ suits task queues and flexible routing, Kafka suits high-volume event streams
  - Keywords (grading without AI): RabbitMQ, Kafka, log, retention, routing

## TypeScript (bank_frontend.py)

- [ ] **MC, easy:** Which of these are features of TypeScript?
  - A. **Static types**
  - B. **Interfaces**
  - C. **Compiles to JavaScript**
  - D. Runs in browsers without being compiled
- [ ] **MC, easy:** Which of these are basic TypeScript types?
  - A. **string**
  - B. **number**
  - C. **boolean**
  - D. char
- [ ] **MC, easy:** Which statements about type annotations are true?
  - A. **let age: number = 3 declares a number**
  - B. **TypeScript can often infer types without annotations**
  - C. **Types are checked at compile time**
  - D. Type annotations stay in the JavaScript output
- [ ] **MC, medium:** Which statements about interface and type in TypeScript are true?
  - A. **Both can describe the shape of an object**
  - B. **An interface can be extended with extends**
  - C. **A type alias can describe a union**
  - D. Only a type alias can describe an object
- [ ] **MC, medium:** Which statements about union types are true?
  - A. **string | number accepts either type**
  - B. **Narrowing with typeof picks one member of the union**
  - C. **A union of string literals limits a value to a few allowed strings**
  - D. A union means the value has both types at once
- [ ] **MC, medium:** Which statements about any and unknown are true?
  - A. **any turns off type checking for a value**
  - B. **unknown must be narrowed before it is used**
  - C. **unknown is safer than any**
  - D. any is checked more strictly than unknown
- [ ] **MC, medium:** Which statements about optional properties are true?
  - A. **name?: string marks a property as optional**
  - B. **Its type includes undefined**
  - C. **Optional chaining a?.b stops at null or undefined**
  - D. Optional properties must always be set
- [ ] **MC, hard:** Which statements about generics are true?
  - A. **They let a function work with many types while keeping type safety**
  - B. **Array<T> is a generic type**
  - C. **A constraint such as T extends object limits the allowed types**
  - D. They cannot help type checking because they are removed at compile time
- [ ] **MC, hard:** Which of these are built-in utility types?
  - A. **Partial**
  - B. **Pick**
  - C. **Omit**
  - D. Spread
- [ ] **MC, hard:** Which statements about strict mode in tsconfig are true?
  - A. **strict turns on a group of stricter checks**
  - B. **strictNullChecks makes null and undefined separate types**
  - C. **noImplicitAny reports values whose type falls back to any**
  - D. strict mode changes how the JavaScript behaves at runtime
- [ ] **Blank, easy:** TypeScript is compiled to plain ________ before it runs.  
  Accepted: JavaScript, JS
- [ ] **Blank, easy:** TypeScript files usually end in .________.  
  Accepted: ts, tsx
- [ ] **Blank, easy:** The TypeScript compiler is run with the command ________.  
  Accepted: tsc
- [ ] **Blank, medium:** The ________ keyword describes the shape of an object and can be extended.  
  Accepted: interface
- [ ] **Blank, medium:** The type ________ accepts any value but must be narrowed before you use it.  
  Accepted: unknown
- [ ] **Blank, medium:** A function that never returns, such as one that always throws, has the return type ________.  
  Accepted: never
- [ ] **Blank, medium:** The settings of a TypeScript project live in the file ________.  
  Accepted: tsconfig.json, tsconfig
- [ ] **Blank, hard:** The utility type ________<T> makes every property of T optional.  
  Accepted: Partial
- [ ] **Blank, hard:** A function such as isString(x): x is string, which narrows a type, is called a type ________.  
  Accepted: guard, predicate
- [ ] **Blank, hard:** The keyof operator gives a union of the property ________ of an object type.  
  Accepted: names, keys
- [ ] **Brief, easy:** Why use TypeScript instead of plain JavaScript?
  - Key fact: it adds static types that catch mistakes before the code runs
  - Key fact: editors use the types for autocomplete and safe refactoring
  - Key fact: types document how functions and objects are meant to be used
  - Keywords (grading without AI): static types, compile time, autocomplete, refactor, errors
- [ ] **Brief, medium:** What is the difference between interface and type in TypeScript?
  - Key fact: both can describe the shape of an object
  - Key fact: an interface can be extended, and declaring it again merges it
  - Key fact: a type alias can also name unions, tuples and other complex types
  - Keywords (grading without AI): interface, type alias, extends, union, merge
- [ ] **Brief, medium:** How does type narrowing work in TypeScript?
  - Key fact: a union type can hold several types
  - Key fact: checks such as typeof, instanceof, in or a shared tag field narrow it to one type
  - Key fact: inside the checked branch TypeScript allows only that type's members
  - Keywords (grading without AI): narrowing, typeof, instanceof, union, discriminated
- [ ] **Brief, hard:** Explain generics in TypeScript with an example.
  - Key fact: generics let a function or type work with many types while keeping type safety
  - Key fact: for example, function first<T>(items: T[]): T returns the same type it receives
  - Key fact: constraints such as T extends HasId limit which types are allowed
  - Keywords (grading without AI): generic, T, type parameter, constraint, extends
- [ ] **Brief, hard:** What is the difference between any and unknown, and when would you use each?
  - Key fact: any turns off type checking, so mistakes slip through
  - Key fact: unknown accepts any value but forces you to narrow it before use
  - Key fact: use unknown for untrusted data such as parsed JSON, and avoid any
  - Keywords (grading without AI): any, unknown, narrowing, type safety, JSON

## Responsive Design (bank_frontend.py)

- [ ] **MC, easy:** Which help make a page responsive?
  - A. **Media queries**
  - B. **Widths in percentages**
  - C. Fixed pixel widths for the whole page
  - D. **The viewport meta tag**
- [ ] **MC, easy:** Which statements about mobile-first design are true?
  - A. **The base styles target small screens**
  - B. **min-width media queries add styles for larger screens**
  - C. **It often leads to simpler CSS**
  - D. It means building only a mobile app
- [ ] **MC, easy:** Which CSS units help a layout adapt to the screen?
  - A. **%**
  - B. **vw**
  - C. **rem**
  - D. cm
- [ ] **MC, medium:** Which statements about media queries are true?
  - A. **They apply CSS when a condition such as width is met**
  - B. **min-width suits a mobile-first approach**
  - C. **They can check the screen orientation**
  - D. They change the HTML structure
- [ ] **MC, medium:** Which help images adapt to the screen size?
  - A. **max-width set to 100%**
  - B. **srcset with several sizes**
  - C. **The picture element**
  - D. A fixed width of 1200px
- [ ] **MC, medium:** Which statements about breakpoints are true?
  - A. **They are widths where the layout changes**
  - B. **They are best placed where the content starts to break**
  - C. **Common ones sit near phone, tablet and desktop widths**
  - D. A site must have exactly three
- [ ] **MC, medium:** Which statements about Flexbox and Grid in responsive layouts are true?
  - A. **flex-wrap lets items move to a new line**
  - B. **Grid with repeat, auto-fit and minmax builds columns that adapt**
  - C. **They reduce the need for many media queries**
  - D. They work only on desktop screens
- [ ] **MC, hard:** Which statements about container queries are true?
  - A. **They style an element by the size of its container, not the viewport**
  - B. **The container needs container-type set**
  - C. **They suit reusable components**
  - D. They replace all media queries
- [ ] **MC, hard:** Which statements about the clamp() function are true?
  - A. **clamp(min, preferred, max) keeps a value between two limits**
  - B. **It is used for fluid font sizes**
  - C. **The preferred value often uses vw**
  - D. It works only on colours
- [ ] **MC, hard:** Which help a page work well on touch screens?
  - A. **Tap targets of at least about 44 by 44 pixels**
  - B. **Not hiding key content behind hover**
  - C. **Enough space between links**
  - D. Smaller text so more fits on screen
- [ ] **Blank, easy:** A CSS ________ query applies styles only when the screen meets a condition such as a minimum width.  
  Accepted: media
- [ ] **Blank, easy:** The ________ meta tag tells mobile browsers to use the width of the device.  
  Accepted: viewport
- [ ] **Blank, easy:** Designing for small screens first and adding styles for larger ones is called mobile-________ design.  
  Accepted: first
- [ ] **Blank, medium:** The CSS unit ________ is 1% of the viewport width.  
  Accepted: vw
- [ ] **Blank, medium:** A width at which the layout changes is called a ________.  
  Accepted: breakpoint
- [ ] **Blank, medium:** The HTML element ________ gives different image sources for different screens.  
  Accepted: picture, <picture>
- [ ] **Blank, medium:** Setting max-width to ________ stops an image from growing wider than its container.  
  Accepted: 100%
- [ ] **Blank, hard:** Container ________ style a component by the size of its parent instead of the viewport.  
  Accepted: queries
- [ ] **Blank, hard:** The CSS function ________() keeps a value between a minimum and a maximum, which suits fluid type.  
  Accepted: clamp
- [ ] **Blank, hard:** In Grid, repeat(auto-fit, ________(200px, 1fr)) builds columns that fill the space.  
  Accepted: minmax
- [ ] **Brief, easy:** What is responsive web design?
  - Key fact: the layout adapts to different screen sizes and devices
  - Key fact: it uses fluid layouts, flexible images and media queries
  - Key fact: the same page works on phones, tablets and desktops without a separate site
  - Keywords (grading without AI): screen size, fluid, media query, flexible images, device
- [ ] **Brief, medium:** What does mobile-first mean, and why use it?
  - Key fact: write the base styles for small screens first
  - Key fact: add styles for larger screens with min-width media queries
  - Key fact: it puts the essential content first and keeps the CSS simpler
  - Keywords (grading without AI): mobile first, min-width, media query, small screen, progressive
- [ ] **Brief, medium:** How do you make images responsive?
  - Key fact: set max-width to 100% and height to auto so they scale down
  - Key fact: use srcset and sizes so the browser downloads a fitting size
  - Key fact: use the picture element for different crops or formats
  - Keywords (grading without AI): max-width, srcset, sizes, picture, scale
- [ ] **Brief, hard:** What are container queries, and when are they better than media queries?
  - Key fact: container queries style an element by the size of its container
  - Key fact: media queries look at the size of the whole viewport
  - Key fact: container queries suit reusable components that appear in both wide and narrow places
  - Keywords (grading without AI): container query, media query, component, viewport, container-type
- [ ] **Brief, hard:** How would you test that a site is responsive?
  - Key fact: check it in the browser's device tools at many widths, not only common devices
  - Key fact: test on real phones for touch, speed and the on-screen keyboard
  - Key fact: look for overflow, tiny tap targets and unreadable text, and fix them
  - Keywords (grading without AI): device tools, real device, breakpoint, overflow, touch

## Web Accessibility (bank_frontend.py)

- [ ] **MC, easy:** Which of these improve accessibility?
  - A. **Text alternatives for images**
  - B. **Every control usable with the keyboard**
  - C. Using colour alone to show meaning
  - D. **A visible focus indicator**
- [ ] **MC, easy:** Who benefits from an accessible website?
  - A. **Screen reader users**
  - B. **People who use only a keyboard**
  - C. **People with low vision**
  - D. Nobody; it only helps search engines
- [ ] **MC, easy:** Which statements about alt text are true?
  - A. **It describes the meaning of an image**
  - B. **A decorative image can have an empty alt**
  - C. **Screen readers read it aloud**
  - D. It should always start with the words image of
- [ ] **MC, medium:** Which statements about WCAG are true?
  - A. **It is a set of accessibility guidelines**
  - B. **It has the levels A, AA and AAA**
  - C. **AA is a common target**
  - D. It covers only mobile apps
- [ ] **MC, medium:** Which statements about colour contrast are true?
  - A. **WCAG AA asks for at least 4.5:1 for normal text**
  - B. **Large text needs at least 3:1**
  - C. **Low contrast makes text hard to read**
  - D. Any colour pair is fine if the font is bold
- [ ] **MC, medium:** Which statements about keyboard access are true?
  - A. **Every interactive element must be reachable with Tab**
  - B. **The focus order should follow the visual order**
  - C. **Focus must be visible**
  - D. A div with a click handler works with the keyboard by default
- [ ] **MC, medium:** Which statements about ARIA are true?
  - A. **It adds roles and states for assistive technology**
  - B. **Native HTML elements should be used first**
  - C. **aria-label gives an element an accessible name**
  - D. Adding ARIA always makes a page more accessible
- [ ] **MC, hard:** Which statements about ARIA live regions are true?
  - A. **aria-live makes screen readers announce changes**
  - B. **polite waits until the user is idle**
  - C. **assertive interrupts at once**
  - D. Every static paragraph needs one
- [ ] **MC, hard:** Which are good practice for an accessible modal dialog?
  - A. **Focus moves into the dialog when it opens**
  - B. **Focus stays inside it while it is open**
  - C. **Focus returns to the trigger when it closes**
  - D. The page behind stays reachable with Tab
- [ ] **MC, hard:** Which help screen reader users move around a page?
  - A. **Headings in order**
  - B. **Landmarks such as nav and main**
  - C. **A skip link to the main content**
  - D. Text inside images
- [ ] **Blank, easy:** ARIA stands for Accessible Rich Internet ________.  
  Accepted: Applications
- [ ] **Blank, easy:** The ________ attribute of an img gives text for screen readers.  
  Accepted: alt
- [ ] **Blank, easy:** The best-known accessibility guidelines for the web are called ________.  
  Accepted: WCAG
- [ ] **Blank, medium:** The attribute ________ gives an element an accessible name when there is no visible label.  
  Accepted: aria-label
- [ ] **Blank, medium:** WCAG level ________ asks for a contrast of at least 4.5:1 for normal text.  
  Accepted: AA
- [ ] **Blank, medium:** A ________ link lets keyboard users jump past the navigation to the main content.  
  Accepted: skip
- [ ] **Blank, medium:** The attribute aria-________ tells assistive technology whether a menu or section is open.  
  Accepted: expanded
- [ ] **Blank, hard:** The attribute aria-________ makes screen readers announce changes in an area of the page.  
  Accepted: live
- [ ] **Blank, hard:** Setting the ________ attribute to -1 makes an element focusable by script but not by the Tab key.  
  Accepted: tabindex
- [ ] **Blank, hard:** Keeping keyboard focus inside an open dialog is called a focus ________.  
  Accepted: trap
- [ ] **Brief, easy:** What is web accessibility, and why does it matter?
  - Key fact: it means people with disabilities can perceive, understand, navigate and use the site
  - Key fact: it helps screen reader users, keyboard users and people with low vision or hearing loss
  - Key fact: it is often a legal requirement and makes the site easier for everyone
  - Keywords (grading without AI): disability, screen reader, keyboard, WCAG, usability
- [ ] **Brief, medium:** How do you make a site usable with only a keyboard?
  - Key fact: every interactive element is reachable with Tab and works with Enter or Space
  - Key fact: use native elements such as button and a, which are focusable by default
  - Key fact: keep a visible focus style and a logical focus order
  - Keywords (grading without AI): keyboard, Tab, focus, button, focus order
- [ ] **Brief, medium:** When should you use ARIA, and when not?
  - Key fact: use native HTML elements first, because they have built-in roles and behaviour
  - Key fact: use ARIA to describe custom widgets, states such as aria-expanded, or names such as aria-label
  - Key fact: wrong ARIA is worse than none, so test with a screen reader
  - Keywords (grading without AI): ARIA, native HTML, role, aria-label, screen reader
- [ ] **Brief, hard:** How would you make a custom modal dialog accessible?
  - Key fact: give it role dialog, aria-modal and a label taken from its heading
  - Key fact: move focus into it on open, keep focus inside, and close it with Escape
  - Key fact: return focus to the button that opened it when it closes
  - Keywords (grading without AI): dialog, focus, trap, Escape, aria-modal
- [ ] **Brief, hard:** How would you test a site for accessibility?
  - Key fact: run automated checkers such as axe or Lighthouse to catch common issues
  - Key fact: test by hand with only a keyboard and with a screen reader such as NVDA or VoiceOver
  - Key fact: check contrast, 200% zoom and headings, because tools catch only part of the problems
  - Keywords (grading without AI): axe, Lighthouse, keyboard, screen reader, contrast

## State Management (bank_frontend.py)

- [ ] **MC, easy:** Which problems does a shared state store help with?
  - A. **Many components reading the same data**
  - B. **Predictable updates through defined actions**
  - C. Removing the need for any state
  - D. **Easier debugging of changes over time**
- [ ] **MC, easy:** Which of these are examples of state in a web app?
  - A. **The values typed into a form**
  - B. **The signed-in user**
  - C. **Whether a menu is open**
  - D. The name of the HTML file
- [ ] **MC, easy:** Which statements about local and global state are true?
  - A. **Local state belongs to one component**
  - B. **Global state is shared by many components**
  - C. **Keeping state local when possible keeps code simpler**
  - D. All state should be global
- [ ] **MC, medium:** Which statements about Redux are true?
  - A. **The state lives in one store**
  - B. **Actions describe what happened**
  - C. **Reducers return new state without changing the old one**
  - D. Components change the store directly
- [ ] **MC, medium:** Which statements about immutable updates are true?
  - A. **You create a new object instead of changing the old one**
  - B. **They make change detection cheap**
  - C. **The spread syntax helps copy objects**
  - D. React notices a push into an array that is already in state
- [ ] **MC, medium:** Which statements about server state are true?
  - A. **It is data that lives on the server, such as fetched lists**
  - B. **It needs caching and refetching**
  - C. **Libraries such as TanStack Query or SWR manage it**
  - D. It should always be copied into a global store by hand
- [ ] **MC, medium:** Which statements about derived state are true?
  - A. **It is computed from other state**
  - B. **Storing it separately lets it get out of sync**
  - C. **Computing it during render keeps it correct**
  - D. It must always be kept in its own variable
- [ ] **MC, hard:** Which statements about Redux Toolkit are true?
  - A. **createSlice generates action creators and a reducer**
  - B. **It uses Immer, so reducer code can look like mutation**
  - C. **configureStore sets up good defaults**
  - D. It removes the need for reducers
- [ ] **MC, hard:** Which statements about selectors are true?
  - A. **They read a piece of state from the store**
  - B. **Memoized selectors avoid recomputing derived data**
  - C. **A component re-renders only when its selected value changes**
  - D. They change the state
- [ ] **MC, hard:** Which statements about keeping state in the URL are true?
  - A. **Filters and page numbers in the query string can be shared and bookmarked**
  - B. **The back button works with them**
  - C. **The router can read them**
  - D. It is a good place for passwords
- [ ] **Blank, easy:** In Redux, a ________ is a function that takes the current state and an action and returns the new state.  
  Accepted: reducer
- [ ] **Blank, easy:** In Redux, all application state lives in a single ________.  
  Accepted: store
- [ ] **Blank, easy:** An object such as { type: 'cart/add' } that describes what happened is a Redux ________.  
  Accepted: action
- [ ] **Blank, medium:** In Redux, you send an action to the store by calling ________.  
  Accepted: dispatch
- [ ] **Blank, medium:** State computed from other state instead of stored is called ________ state.  
  Accepted: derived
- [ ] **Blank, medium:** Creating a new object instead of changing the old one is called an ________ update.  
  Accepted: immutable
- [ ] **Blank, medium:** Passing props down through many layers, which a shared store avoids, is called prop ________.  
  Accepted: drilling
- [ ] **Blank, hard:** Redux Toolkit uses the ________ library so reducers can be written as if they changed state in place.  
  Accepted: Immer
- [ ] **Blank, hard:** A function that reads or derives a value from the store is called a ________.  
  Accepted: selector
- [ ] **Blank, hard:** Redux ________, such as thunks, runs code between dispatching an action and the reducer.  
  Accepted: middleware
- [ ] **Brief, easy:** What is application state, and where should it live?
  - Key fact: state is data that can change and affects what the UI shows
  - Key fact: keep state as close as possible to the components that use it
  - Key fact: move it up or into a shared store only when several distant components need it
  - Keywords (grading without AI): state, local, lift, shared, component
- [ ] **Brief, medium:** How does Redux manage state?
  - Key fact: all the state lives in one store
  - Key fact: components dispatch actions that describe what happened
  - Key fact: pure reducers compute the new state, and components subscribe to the parts they need
  - Keywords (grading without AI): store, action, dispatch, reducer, subscribe
- [ ] **Brief, medium:** Why is immutability important in state management?
  - Key fact: change detection can compare references instead of deep values
  - Key fact: old states stay unchanged, which allows undo and time-travel debugging
  - Key fact: changing state in place can stop the UI from updating
  - Keywords (grading without AI): immutable, reference, change detection, re-render, undo
- [ ] **Brief, hard:** How would you handle data fetched from a server in a front-end app?
  - Key fact: treat it as server state, separate from UI state
  - Key fact: use a library such as TanStack Query or SWR for caching, loading and error states, and refetching
  - Key fact: invalidate or refetch after changes so the UI stays in sync with the server
  - Keywords (grading without AI): server state, cache, TanStack Query, refetch, invalidate
- [ ] **Brief, hard:** When would you choose Redux, Context or local state?
  - Key fact: local state for data used by one component or a small subtree
  - Key fact: Context for values that change rarely but many components read, such as the theme or the user
  - Key fact: a store such as Redux for large shared state that changes often and needs clear update rules and dev tools
  - Keywords (grading without AI): local state, Context, Redux, shared, re-render

## Web Performance (bank_frontend.py)

- [ ] **MC, easy:** Which usually improve page load time?
  - A. **Compressing images**
  - B. **Caching static files**
  - C. Loading every script synchronously in the head
  - D. **Minifying JavaScript**
- [ ] **MC, easy:** Which tools measure web page performance?
  - A. **Lighthouse**
  - B. **The Performance panel of Chrome DevTools**
  - C. **WebPageTest**
  - D. Microsoft Paint
- [ ] **MC, easy:** Which statements about browser caching are true?
  - A. **Cache-Control headers say how long the browser may keep a file**
  - B. **A cached file loads without a network request**
  - C. **File names with a content hash allow long caching**
  - D. Caching makes every page slower
- [ ] **MC, medium:** Which of these are Core Web Vitals?
  - A. **Largest Contentful Paint**
  - B. **Interaction to Next Paint**
  - C. **Cumulative Layout Shift**
  - D. Total file count
- [ ] **MC, medium:** Which reduce layout shift?
  - A. **Setting width and height on images**
  - B. **Reserving space for ads and embeds**
  - C. **A fallback font with a similar size to the web font**
  - D. Inserting banners above the content after load
- [ ] **MC, medium:** Which make JavaScript cost less?
  - A. **Code splitting**
  - B. **Tree shaking unused code**
  - C. **Deferring scripts that are not needed at first**
  - D. Bundling every library into one file loaded at start
- [ ] **MC, medium:** Which statements about image formats are true?
  - A. **WebP and AVIF are usually smaller than JPEG**
  - B. **SVG suits icons and logos**
  - C. **Photos saved as PNG are often much bigger than needed**
  - D. GIF is the best format for photos
- [ ] **MC, hard:** Which statements about the critical rendering path are true?
  - A. **CSS blocks rendering until it is loaded**
  - B. **Inlining critical CSS can speed up the first paint**
  - C. **Render-blocking scripts delay the page**
  - D. Fonts never affect rendering
- [ ] **MC, hard:** Which of these are resource hints?
  - A. **preload**
  - B. **preconnect**
  - C. **prefetch**
  - D. prerender-all
- [ ] **MC, hard:** Which help a server deliver pages faster?
  - A. **Compression such as gzip or Brotli**
  - B. **HTTP/2 or HTTP/3**
  - C. **A CDN close to users**
  - D. Turning off caching
- [ ] **Blank, easy:** Loading images only when they are about to scroll into view is called ________ loading.  
  Accepted: lazy
- [ ] **Blank, easy:** Removing spaces and comments from code to make files smaller is called ________.  
  Accepted: minification, minifying
- [ ] **Blank, easy:** Google's built-in tool that scores page performance is ________.  
  Accepted: Lighthouse
- [ ] **Blank, medium:** The Core Web Vital that measures layout jumps is Cumulative Layout ________.  
  Accepted: Shift
- [ ] **Blank, medium:** The Core Web Vital that measures when the main content appears is Largest Contentful ________.  
  Accepted: Paint
- [ ] **Blank, medium:** Splitting a bundle so code loads only when it is needed is called code ________.  
  Accepted: splitting
- [ ] **Blank, medium:** Removing unused exports from a bundle is called tree ________.  
  Accepted: shaking
- [ ] **Blank, hard:** The resource hint rel=________ tells the browser to fetch a file the current page needs early.  
  Accepted: preload
- [ ] **Blank, hard:** ________ is a compression format that usually makes text files smaller than gzip.  
  Accepted: Brotli
- [ ] **Blank, hard:** Time to First ________ measures how long the server takes to send the first part of the response.  
  Accepted: Byte
- [ ] **Brief, easy:** Why does web performance matter?
  - Key fact: slow pages make users leave before the page loads
  - Key fact: speed affects search ranking through the Core Web Vitals
  - Key fact: faster pages use less data and battery on phones
  - Keywords (grading without AI): users, conversion, Core Web Vitals, mobile, speed
- [ ] **Brief, medium:** What are the Core Web Vitals, and what does each measure?
  - Key fact: Largest Contentful Paint measures how fast the main content appears
  - Key fact: Interaction to Next Paint measures how quickly the page responds to input
  - Key fact: Cumulative Layout Shift measures how much the layout jumps while loading
  - Keywords (grading without AI): LCP, INP, CLS, loading, layout shift
- [ ] **Brief, medium:** How would you make images load faster?
  - Key fact: compress them and use modern formats such as WebP or AVIF
  - Key fact: serve the right size with srcset and sizes
  - Key fact: lazy-load images below the fold and serve them through a CDN
  - Keywords (grading without AI): compress, WebP, srcset, lazy, CDN
- [ ] **Brief, hard:** How would you reduce the JavaScript cost of a large single-page app?
  - Key fact: measure the bundle with a bundle analyzer to find large dependencies
  - Key fact: split code by route and load features only when needed
  - Key fact: remove unused code and libraries, and defer scripts that are not needed at first
  - Keywords (grading without AI): bundle analyzer, code splitting, lazy, tree shaking, defer
- [ ] **Brief, hard:** How would you find and fix a slow page load?
  - Key fact: measure with Lighthouse or WebPageTest and read the request waterfall
  - Key fact: find the biggest costs: slow server response, render-blocking files, large images or scripts
  - Key fact: fix the largest first, for example with caching, compression, a CDN or smaller bundles, then measure again
  - Keywords (grading without AI): Lighthouse, waterfall, render-blocking, cache, measure

## Python (bank_data.py)

- [ ] **MC, easy:** Which of these are built-in Python data types?
  - A. **list**
  - B. **dict**
  - C. **tuple**
  - D. vector
- [ ] **MC, easy:** Which statements about Python lists are true?
  - A. **They keep their order**
  - B. **They can be changed after creation**
  - C. **They can hold items of different types**
  - D. They cannot contain duplicates
- [ ] **MC, easy:** Which statements about indentation in Python are true?
  - A. **It defines code blocks**
  - B. **Mixing tabs and spaces causes errors**
  - C. **Four spaces is the usual style**
  - D. It is only for readability and has no effect
- [ ] **MC, medium:** Which statements about dictionaries are true?
  - A. **They map keys to values**
  - B. **Keys must be hashable**
  - C. **Lookups take O(1) on average**
  - D. A list can be used as a key
- [ ] **MC, medium:** Which statements about list comprehensions are true?
  - A. **[x * 2 for x in nums] builds a new list**
  - B. **They can include an if filter**
  - C. **They are often clearer than a loop with append**
  - D. They change the original list
- [ ] **MC, medium:** Which statements about mutable and immutable types are true?
  - A. **Lists are mutable**
  - B. **Tuples are immutable**
  - C. **Strings are immutable**
  - D. Integers are mutable
- [ ] **MC, medium:** Which statements about exceptions in Python are true?
  - A. **try and except handle errors**
  - B. **finally runs whether or not an error happened**
  - C. **raise throws an exception**
  - D. A bare except: is the best practice
- [ ] **MC, hard:** Which statements about generators are true?
  - A. **They produce values lazily with yield**
  - B. **They use little memory for long sequences**
  - C. **They can be iterated only once**
  - D. They store every value in memory first
- [ ] **MC, hard:** Which statements about decorators are true?
  - A. **A decorator wraps a function to add behaviour**
  - B. **@name above a function applies it**
  - C. **functools.wraps keeps the original function's name**
  - D. Decorators can be used only on classes
- [ ] **MC, hard:** Which statements about default argument values are true?
  - A. **They are evaluated once, when the function is defined**
  - B. **A mutable default such as a list is shared between calls**
  - C. **Using None and creating the list inside avoids the problem**
  - D. A new default list is made on every call
- [ ] **Blank, easy:** In Python, the keyword ________ defines a function.  
  Accepted: def
- [ ] **Blank, easy:** The built-in function ________ returns the number of items in a list.  
  Accepted: len
- [ ] **Blank, easy:** Python's package installer is called ________.  
  Accepted: pip
- [ ] **Blank, medium:** A ________ is an ordered, immutable sequence such as (1, 2, 3).  
  Accepted: tuple
- [ ] **Blank, medium:** The keyword ________ makes a function hand back values one at a time as a generator.  
  Accepted: yield
- [ ] **Blank, medium:** A ________ environment keeps a project's packages separate from other projects.  
  Accepted: virtual
- [ ] **Blank, medium:** The with statement uses a context ________ to open and close resources safely.  
  Accepted: manager
- [ ] **Blank, hard:** The ________ in CPython lets only one thread run Python bytecode at a time.  
  Accepted: GIL, global interpreter lock
- [ ] **Blank, hard:** A function that takes a function and returns a new one, applied with @, is a ________.  
  Accepted: decorator
- [ ] **Blank, hard:** The special method ________ sets up a new object's attributes when it is created.  
  Accepted: __init__, init
- [ ] **Brief, easy:** What is the difference between a list and a tuple in Python?
  - Key fact: a list is mutable, so items can be added or changed
  - Key fact: a tuple cannot be changed once created
  - Key fact: tuples can be dictionary keys and suit fixed groups of values, while lists suit collections that change
  - Keywords (grading without AI): list, tuple, mutable, immutable, key
- [ ] **Brief, medium:** How do dictionaries work in Python, and when would you use one?
  - Key fact: a dictionary maps unique, hashable keys to values
  - Key fact: lookups, inserts and deletes take O(1) on average because it is a hash table
  - Key fact: use it to find values by key, count items or group data
  - Keywords (grading without AI): dictionary, key, hashable, O(1), hash table
- [ ] **Brief, medium:** What are list comprehensions and generator expressions?
  - Key fact: a list comprehension builds a list in one expression, such as [x * x for x in nums if x > 0]
  - Key fact: a generator expression uses round brackets and yields items one at a time
  - Key fact: generators save memory for large or endless data because nothing is stored up front
  - Keywords (grading without AI): comprehension, generator, lazy, memory, yield
- [ ] **Brief, hard:** Explain decorators in Python with an example.
  - Key fact: a decorator is a function that takes a function and returns a wrapped version
  - Key fact: @timer above a function is the same as writing func = timer(func)
  - Key fact: they are used for logging, timing, caching and access checks, and functools.wraps keeps the original name
  - Keywords (grading without AI): decorator, wrapper, @, functools.wraps, cache
- [ ] **Brief, hard:** How does Python handle concurrency, and what is the GIL?
  - Key fact: the global interpreter lock lets only one thread run Python bytecode at a time in CPython
  - Key fact: threads still help for I/O-bound work such as network calls, and asyncio suits many concurrent I/O tasks
  - Key fact: for CPU-bound work, use multiprocessing or libraries that release the GIL
  - Keywords (grading without AI): GIL, threading, asyncio, multiprocessing, I/O-bound

## Machine Learning (bank_data.py)

- [ ] **MC, easy:** Which of these are types of machine learning?
  - A. **Supervised learning**
  - B. **Unsupervised learning**
  - C. **Reinforcement learning**
  - D. Compiled learning
- [ ] **MC, easy:** Which of these are supervised learning tasks?
  - A. **Predicting house prices**
  - B. **Classifying emails as spam or not spam**
  - C. **Predicting whether a customer will leave**
  - D. Grouping customers when there are no labels
- [ ] **MC, easy:** Which statements about training and test data are true?
  - A. **The model learns from the training data**
  - B. **The test data checks performance on unseen data**
  - C. **Test data must not be used for training**
  - D. Using the same data for both gives an honest score
- [ ] **MC, medium:** Which statements about overfitting are true?
  - A. **The model fits noise in the training data**
  - B. **Training accuracy is high while test accuracy is low**
  - C. **Regularization helps reduce it**
  - D. More complex models never overfit
- [ ] **MC, medium:** Which of these are classification algorithms?
  - A. **Logistic regression**
  - B. **Decision tree**
  - C. **Random forest**
  - D. K-means
- [ ] **MC, medium:** Which statements about the bias-variance trade-off are true?
  - A. **High-bias models underfit**
  - B. **High-variance models overfit**
  - C. **A good model balances the two**
  - D. Adding features always lowers both
- [ ] **MC, medium:** Which statements about cross-validation are true?
  - A. **K-fold splits the data into k parts and trains k times**
  - B. **It gives a more stable estimate than a single split**
  - C. **It helps choose hyperparameters**
  - D. It trains on the test set
- [ ] **MC, hard:** Which statements about regularization are true?
  - A. **L1 can shrink some weights to exactly zero**
  - B. **L2 shrinks weights toward zero without removing them**
  - C. **A larger penalty makes the model simpler**
  - D. It increases overfitting
- [ ] **MC, hard:** Which statements about ensemble methods are true?
  - A. **A random forest averages many trees trained on random samples**
  - B. **Gradient boosting adds trees that fix earlier errors**
  - C. **Ensembles often beat single models**
  - D. An ensemble always trains faster than one tree
- [ ] **MC, hard:** Which statements about data leakage are true?
  - A. **Information from the test set or the future gets into training**
  - B. **It makes validation scores look too good**
  - C. **Fitting a scaler on all the data before splitting causes it**
  - D. It improves real-world performance
- [ ] **Blank, easy:** Learning from labelled examples is called ________ learning.  
  Accepted: supervised
- [ ] **Blank, easy:** Finding groups in data without labels, such as clustering, is ________ learning.  
  Accepted: unsupervised
- [ ] **Blank, easy:** Predicting a number, such as a price, is a ________ task.  
  Accepted: regression
- [ ] **Blank, medium:** A model that learns the training data too closely and fails on new data is ________.  
  Accepted: overfitting, overfit
- [ ] **Blank, medium:** Settings chosen before training, such as the depth of a tree, are called ________.  
  Accepted: hyperparameters
- [ ] **Blank, medium:** K-________ is a clustering algorithm that groups points around k centres.  
  Accepted: means
- [ ] **Blank, medium:** Splitting the data into k parts and training k times is k-fold cross-________.  
  Accepted: validation
- [ ] **Blank, hard:** Adding a penalty on large weights to reduce overfitting is called ________.  
  Accepted: regularization, regularisation
- [ ] **Blank, hard:** Building trees one after another, each fixing the errors of the last, is gradient ________.  
  Accepted: boosting
- [ ] **Blank, hard:** When information that is not available at prediction time gets into training, it is called data ________.  
  Accepted: leakage
- [ ] **Brief, easy:** What is the difference between supervised and unsupervised learning?
  - Key fact: supervised learning trains on labelled examples to predict a known target
  - Key fact: unsupervised learning finds structure in data without labels, such as clusters
  - Key fact: spam detection is supervised, customer segmentation is unsupervised
  - Keywords (grading without AI): supervised, unsupervised, labels, classification, clustering
- [ ] **Brief, medium:** What is overfitting, and how do you prevent it?
  - Key fact: the model learns noise in the training data and does badly on new data
  - Key fact: it shows as high training accuracy and much lower validation accuracy
  - Key fact: prevent it with more data, simpler models, regularization, cross-validation or early stopping
  - Keywords (grading without AI): overfitting, validation, regularization, cross-validation, simpler model
- [ ] **Brief, medium:** Explain the bias-variance trade-off.
  - Key fact: bias is error from a model that is too simple and underfits
  - Key fact: variance is error from a model that reacts too much to the training data and overfits
  - Key fact: the best model balances the two to keep the total error on new data low
  - Keywords (grading without AI): bias, variance, underfit, overfit, trade-off
- [ ] **Brief, hard:** How would you build a model to predict customer churn?
  - Key fact: define churn and the prediction window, and gather features such as usage, tenure and support tickets
  - Key fact: split the data by time, train a simple baseline, then models such as gradient boosting
  - Key fact: evaluate with precision, recall or AUC because churners are rare, and explain the main drivers
  - Keywords (grading without AI): churn, features, baseline, recall, AUC
- [ ] **Brief, hard:** How do random forests and gradient boosting differ?
  - Key fact: a random forest trains many trees independently on random samples and averages them
  - Key fact: gradient boosting trains trees one after another, each fixing the previous errors
  - Key fact: random forests are robust and easy to tune, while boosting is often more accurate but needs careful tuning
  - Keywords (grading without AI): random forest, boosting, trees, average, tuning

## Feature Engineering (bank_data.py)

- [ ] **MC, easy:** Which of these are examples of feature engineering?
  - A. **Taking the day of the week from a date**
  - B. **Combining height and weight into BMI**
  - C. **Turning categories into numbers**
  - D. Deleting the target column from the test results
- [ ] **MC, easy:** Which are ways to encode a categorical feature?
  - A. **One-hot encoding**
  - B. **Ordinal encoding**
  - C. **Target encoding**
  - D. Image encoding
- [ ] **MC, easy:** Which statements about feature scaling are true?
  - A. **It puts features on a similar range**
  - B. **Standardization gives a mean of 0 and a standard deviation of 1**
  - C. **Min-max scaling maps values to the range 0 to 1**
  - D. Tree-based models always need it
- [ ] **MC, medium:** Which models are sensitive to the scale of features?
  - A. **K-nearest neighbours**
  - B. **Support vector machines**
  - C. **Regularized linear models**
  - D. Decision trees
- [ ] **MC, medium:** Which statements about one-hot encoding are true?
  - A. **It creates one column per category**
  - B. **It suits categories with no order**
  - C. **It can create very many columns for features with many values**
  - D. It gives the categories a numeric order
- [ ] **MC, medium:** Which help with skewed numeric features?
  - A. **A log transform**
  - B. **Binning into ranges**
  - C. **A robust scaler**
  - D. Duplicating the largest values
- [ ] **MC, medium:** Which features can be built from a timestamp?
  - A. **Hour of the day**
  - B. **Day of the week**
  - C. **Whether it is a holiday**
  - D. The model's accuracy
- [ ] **MC, hard:** Which statements about target encoding are true?
  - A. **It replaces a category with the average target for that category**
  - B. **It can leak the target if computed on all the data**
  - C. **Out-of-fold means reduce the leakage**
  - D. It can never overfit
- [ ] **MC, hard:** Which of these are feature selection methods?
  - A. **Removing features with very low variance**
  - B. **Recursive feature elimination**
  - C. **L1 regularization**
  - D. Adding random noise columns as final features
- [ ] **MC, hard:** Which statements about interaction features are true?
  - A. **They combine two features, such as price times quantity**
  - B. **They help linear models capture combined effects**
  - C. **Tree models can find some interactions by themselves**
  - D. They are never useful
- [ ] **Blank, easy:** Turning a category into separate 0 or 1 columns is called ________ encoding.  
  Accepted: one-hot, one hot
- [ ] **Blank, easy:** Putting features on a similar range is called feature ________.  
  Accepted: scaling
- [ ] **Blank, easy:** Grouping a numeric value into ranges, such as age groups, is called ________.  
  Accepted: binning, bucketing, discretization
- [ ] **Blank, medium:** Scaling to a mean of 0 and a standard deviation of 1 is called ________.  
  Accepted: standardization, standardisation
- [ ] **Blank, medium:** A ________ transform reduces the skew of values that span many orders of magnitude.  
  Accepted: log, logarithmic
- [ ] **Blank, medium:** A categorical feature with a very large number of distinct values has high ________.  
  Accepted: cardinality
- [ ] **Blank, medium:** Replacing a category with the average target value for it is ________ encoding.  
  Accepted: target, mean
- [ ] **Blank, hard:** A feature made by multiplying or combining two features is an ________ feature.  
  Accepted: interaction
- [ ] **Blank, hard:** Removing the least important features one at a time and retraining is recursive feature ________.  
  Accepted: elimination
- [ ] **Blank, hard:** Reducing many correlated features to a few new components is often done with ________.  
  Accepted: PCA, principal component analysis
- [ ] **Brief, easy:** What is feature engineering, and why does it matter?
  - Key fact: it turns raw data into inputs that help a model learn
  - Key fact: examples are taking parts of dates, encoding categories and creating ratios
  - Key fact: good features often improve a model more than switching algorithms
  - Keywords (grading without AI): features, raw data, encoding, domain knowledge, model
- [ ] **Brief, medium:** How do you handle categorical features?
  - Key fact: use one-hot encoding for categories with no order and few values
  - Key fact: use ordinal encoding for ordered categories such as small, medium and large
  - Key fact: for features with many values, use target or frequency encoding, taking care to avoid leakage
  - Keywords (grading without AI): one-hot, ordinal, target encoding, cardinality, leakage
- [ ] **Brief, medium:** When and why do you scale features?
  - Key fact: scaling puts features on a similar range
  - Key fact: distance-based and gradient-based models such as KNN, SVM and linear models need it
  - Key fact: fit the scaler on the training data only, then apply it to the test data
  - Keywords (grading without AI): scaling, standardization, KNN, fit, training data
- [ ] **Brief, hard:** How would you create features from a timestamp for a sales model?
  - Key fact: take the hour, day of week, month and whether it is a weekend or holiday
  - Key fact: encode cyclical values such as the hour with sine and cosine
  - Key fact: add lag and rolling features such as last week's sales, computed only from past data
  - Keywords (grading without AI): day of week, holiday, cyclical, lag, rolling
- [ ] **Brief, hard:** How do you decide which features to keep?
  - Key fact: remove features that are constant, duplicated or leak the target
  - Key fact: measure importance with methods such as permutation importance or L1 regularization
  - Key fact: keep the set that gives the best cross-validated score while staying simple
  - Keywords (grading without AI): leakage, importance, permutation, L1, cross-validation

## Model Evaluation (bank_data.py)

- [ ] **MC, easy:** Which of these are metrics for classification?
  - A. **Accuracy**
  - B. **Precision**
  - C. **Recall**
  - D. Mean squared error
- [ ] **MC, easy:** Which of these are metrics for regression?
  - A. **Mean absolute error**
  - B. **Root mean squared error**
  - C. **R-squared**
  - D. F1 score
- [ ] **MC, easy:** Which statements about a confusion matrix are true?
  - A. **It counts true and false positives and negatives**
  - B. **It shows which classes get mixed up**
  - C. **Precision and recall are computed from it**
  - D. It is used for regression
- [ ] **MC, medium:** Which statements about precision and recall are true?
  - A. **Precision is the share of predicted positives that are correct**
  - B. **Recall is the share of actual positives that are found**
  - C. **Raising the threshold usually raises precision and lowers recall**
  - D. They always move together
- [ ] **MC, medium:** Why can accuracy mislead on imbalanced data?
  - A. **A model that predicts only the majority class can score high**
  - B. **It hides how the rare class is handled**
  - C. **Precision, recall or F1 tell more**
  - D. Accuracy is not defined for two classes
- [ ] **MC, medium:** Which statements about the F1 score are true?
  - A. **It is the harmonic mean of precision and recall**
  - B. **It is low if either of them is low**
  - C. **It suits imbalanced classes better than accuracy**
  - D. It is the average of accuracy and recall
- [ ] **MC, medium:** Which statements about ROC AUC are true?
  - A. **It measures how well a model ranks positives above negatives**
  - B. **0.5 is no better than chance**
  - C. **1.0 is perfect ranking**
  - D. It depends on one chosen threshold
- [ ] **MC, hard:** Which statements about precision-recall curves are true?
  - A. **They tell more than ROC curves when positives are rare**
  - B. **They show the trade-off across thresholds**
  - C. **Average precision summarises the curve**
  - D. They ignore the positive class
- [ ] **MC, hard:** Which statements about calibration are true?
  - A. **A calibrated model's 0.8 predictions are right about 80% of the time**
  - B. **It matters when probabilities drive decisions**
  - C. **Platt scaling and isotonic regression can calibrate a model**
  - D. High accuracy guarantees good calibration
- [ ] **MC, hard:** Which are good practices for validating a time series model?
  - A. **Train on the past and test on the future**
  - B. **Use rolling or expanding windows**
  - C. **Avoid shuffling rows randomly across time**
  - D. Mix future rows into training to get more data
- [ ] **Blank, easy:** The share of all predictions that are correct is called ________.  
  Accepted: accuracy
- [ ] **Blank, easy:** A table of true and false positives and negatives is a ________ matrix.  
  Accepted: confusion
- [ ] **Blank, easy:** The share of actual positives that the model finds is called ________.  
  Accepted: recall, sensitivity
- [ ] **Blank, medium:** The share of predicted positives that are actually positive is ________.  
  Accepted: precision
- [ ] **Blank, medium:** The harmonic mean of precision and recall is the ________ score.  
  Accepted: F1
- [ ] **Blank, medium:** An ROC AUC of ________ means the model ranks no better than random guessing.  
  Accepted: 0.5
- [ ] **Blank, medium:** The square root of the mean squared error is abbreviated ________.  
  Accepted: RMSE
- [ ] **Blank, hard:** A model whose predicted probabilities match real frequencies is well ________.  
  Accepted: calibrated
- [ ] **Blank, hard:** Testing a time series model on periods after those it was trained on is called walk-________ validation.  
  Accepted: forward
- [ ] **Blank, hard:** Choosing the probability cut-off that turns scores into class labels is called ________ tuning.  
  Accepted: threshold
- [ ] **Brief, easy:** Why do you split data into training, validation and test sets?
  - Key fact: the model learns from the training set
  - Key fact: the validation set is used to tune choices such as hyperparameters
  - Key fact: the test set is used once at the end to estimate performance on unseen data
  - Keywords (grading without AI): training, validation, test, unseen, hyperparameters
- [ ] **Brief, medium:** Explain precision and recall, and when you would favour each.
  - Key fact: precision is the share of predicted positives that are right, recall is the share of real positives that are found
  - Key fact: favour precision when false alarms are costly, such as blocking good customers as fraud
  - Key fact: favour recall when missing a positive is costly, such as missing a disease
  - Keywords (grading without AI): precision, recall, false positive, false negative, threshold
- [ ] **Brief, medium:** Why is accuracy a poor metric for imbalanced data, and what would you use instead?
  - Key fact: a model that always predicts the majority class can still have high accuracy
  - Key fact: accuracy hides how badly the rare class is handled
  - Key fact: use precision, recall, F1, PR AUC or a confusion matrix instead
  - Keywords (grading without AI): imbalanced, accuracy, F1, PR AUC, confusion matrix
- [ ] **Brief, hard:** How do you choose an evaluation metric for a business problem?
  - Key fact: start from what each kind of error costs the business
  - Key fact: choose a metric that reflects those costs, such as recall for fraud or MAE for forecasts
  - Key fact: set the decision threshold from the costs and check the metric on held-out data
  - Keywords (grading without AI): business cost, metric, threshold, error, held-out
- [ ] **Brief, hard:** How would you evaluate a model that forecasts future values?
  - Key fact: split by time: train on the past and test on later periods
  - Key fact: use rolling or walk-forward validation to test several periods
  - Key fact: compare it with a simple baseline, such as the last value or a seasonal average, using MAE or RMSE
  - Keywords (grading without AI): time split, walk-forward, baseline, MAE, RMSE

## Deep Learning (bank_dataeng.py)

- [ ] **MC, easy:** Which statements about neural networks are true?
  - A. **They are made of layers of connected neurons**
  - B. **They learn their weights from data**
  - C. **Deep networks have many layers**
  - D. They learn without any data
- [ ] **MC, easy:** Which of these are activation functions?
  - A. **ReLU**
  - B. **Sigmoid**
  - C. **Tanh**
  - D. Dropout
- [ ] **MC, easy:** Which tasks does deep learning do well?
  - A. **Image recognition**
  - B. **Speech recognition**
  - C. **Language translation**
  - D. Adding two small integers exactly
- [ ] **MC, medium:** Which statements about backpropagation are true?
  - A. **It computes the gradient of the loss for each weight**
  - B. **It uses the chain rule**
  - C. **An optimizer then updates the weights**
  - D. It runs only at prediction time
- [ ] **MC, medium:** Which statements about the learning rate are true?
  - A. **Too high can make training diverge**
  - B. **Too low makes training slow**
  - C. **A schedule can change it during training**
  - D. It has no effect on training
- [ ] **MC, medium:** Which help reduce overfitting in neural networks?
  - A. **Dropout**
  - B. **Weight decay**
  - C. **Data augmentation**
  - D. Many more epochs on the same data
- [ ] **MC, medium:** Which statements about convolutional neural networks are true?
  - A. **They use filters that slide over the input**
  - B. **They suit images**
  - C. **Pooling reduces the spatial size**
  - D. They cannot process images
- [ ] **MC, hard:** Which statements about the transformer architecture are true?
  - A. **It uses self-attention**
  - B. **It processes the tokens of a sequence in parallel during training**
  - C. **It powers most large language models**
  - D. It requires recurrent layers
- [ ] **MC, hard:** Which statements about vanishing gradients are true?
  - A. **Gradients shrink as they flow back through many layers**
  - B. **Sigmoid activations make it worse**
  - C. **ReLU, residual connections and normalization help**
  - D. It makes deep networks train faster
- [ ] **MC, hard:** Which statements about transfer learning are true?
  - A. **It starts from a model trained on a large dataset**
  - B. **It fine-tunes that model for a new task**
  - C. **It needs less data than training from scratch**
  - D. It always trains every layer from random weights
- [ ] **Blank, easy:** A neural network with many layers is called a ________ network.  
  Accepted: deep
- [ ] **Blank, easy:** The activation function that outputs max(0, x) is ________.  
  Accepted: ReLU
- [ ] **Blank, easy:** One full pass over the training data is called an ________.  
  Accepted: epoch
- [ ] **Blank, medium:** The algorithm that computes gradients layer by layer, from the output back, is ________.  
  Accepted: backpropagation, backprop
- [ ] **Blank, medium:** Randomly switching off neurons during training to reduce overfitting is called ________.  
  Accepted: dropout
- [ ] **Blank, medium:** The size of the step taken when weights are updated is the learning ________.  
  Accepted: rate
- [ ] **Blank, medium:** Networks that slide filters over images are ________ neural networks.  
  Accepted: convolutional
- [ ] **Blank, hard:** The mechanism at the heart of transformers is self-________.  
  Accepted: attention
- [ ] **Blank, hard:** Reusing a pre-trained model for a new task is called ________ learning.  
  Accepted: transfer
- [ ] **Blank, hard:** Connections that add a layer's input to its output, as in ResNet, are ________ connections.  
  Accepted: residual, skip
- [ ] **Brief, easy:** What is a neural network, and how does it learn?
  - Key fact: it is layers of neurons that combine inputs with weights and activation functions
  - Key fact: training compares the predictions with the true values using a loss function
  - Key fact: backpropagation and an optimizer adjust the weights to reduce the loss
  - Keywords (grading without AI): neuron, weights, loss, backpropagation, optimizer
- [ ] **Brief, medium:** What do activation functions do, and why are they needed?
  - Key fact: they add non-linearity, so the network can learn complex patterns
  - Key fact: without them, many layers would act like a single linear layer
  - Key fact: ReLU is common in hidden layers, while sigmoid or softmax at the output give probabilities
  - Keywords (grading without AI): activation, non-linear, ReLU, sigmoid, softmax
- [ ] **Brief, medium:** How do you stop a neural network from overfitting?
  - Key fact: get more data or use data augmentation
  - Key fact: use dropout, weight decay and early stopping
  - Key fact: watch the gap between training loss and validation loss
  - Keywords (grading without AI): augmentation, dropout, weight decay, early stopping, validation
- [ ] **Brief, hard:** Explain how transformers work at a high level.
  - Key fact: text is split into tokens that become embeddings with position information
  - Key fact: self-attention lets each token weigh every other token to build context
  - Key fact: stacked attention and feed-forward layers trained on large data power language models
  - Keywords (grading without AI): token, embedding, self-attention, position, layers
- [ ] **Brief, hard:** When would you use transfer learning, and how?
  - Key fact: when you have little labelled data for a task similar to one a large model was trained on
  - Key fact: start from a pre-trained model, replace the output layer and train on your data
  - Key fact: freeze the early layers first, then fine-tune more layers with a small learning rate
  - Keywords (grading without AI): pre-trained, fine-tune, freeze, small data, learning rate

## PyTorch (bank_dataeng.py)

- [ ] **MC, easy:** Which statements about PyTorch are true?
  - A. **It is a deep learning library for Python**
  - B. **Its main data structure is the tensor**
  - C. **It can run on GPUs**
  - D. It is a database
- [ ] **MC, easy:** Which statements about tensors are true?
  - A. **They are multi-dimensional arrays**
  - B. **They can live on the CPU or the GPU**
  - C. **They support automatic differentiation**
  - D. They can hold only one number
- [ ] **MC, easy:** Which PyTorch modules help build and train models?
  - A. **torch.nn**
  - B. **torch.optim**
  - C. **torch.utils.data**
  - D. torch.html
- [ ] **MC, medium:** Which steps make up a PyTorch training loop?
  - A. **A forward pass that computes predictions**
  - B. **Computing the loss and calling backward**
  - C. **Calling optimizer.step and zeroing the gradients**
  - D. Restarting Python after each step
- [ ] **MC, medium:** Which statements about autograd are true?
  - A. **It records operations to compute gradients**
  - B. **loss.backward() fills .grad for the parameters**
  - C. **requires_grad marks tensors that need gradients**
  - D. It computes gradients only for integer tensors
- [ ] **MC, medium:** Which statements about Dataset and DataLoader are true?
  - A. **A Dataset returns one sample by index**
  - B. **A DataLoader batches and shuffles samples**
  - C. **num_workers loads data in parallel**
  - D. The DataLoader trains the model
- [ ] **MC, medium:** Which statements about model.train() and model.eval() are true?
  - A. **eval turns off dropout**
  - B. **eval makes batch norm use its running statistics**
  - C. **train turns both back on**
  - D. They change the learning rate
- [ ] **MC, hard:** Why call optimizer.zero_grad() at each step?
  - A. **Gradients add up by default**
  - B. **Without it, old gradients mix with new ones**
  - C. **It resets .grad before the next backward**
  - D. It deletes the model
- [ ] **MC, hard:** Which reduce GPU memory use or speed up training?
  - A. **Mixed precision training**
  - B. **Smaller batch sizes**
  - C. **torch.no_grad() during evaluation**
  - D. Keeping every intermediate tensor
- [ ] **MC, hard:** Which statements about saving models in PyTorch are true?
  - A. **Saving the state_dict is recommended**
  - B. **load_state_dict loads weights into a model**
  - C. **The model class must be defined to load a state_dict**
  - D. torch.save cannot save tensors
- [ ] **Blank, easy:** PyTorch's main data structure is the ________.  
  Accepted: tensor
- [ ] **Blank, easy:** In PyTorch, models are built by subclassing nn.________.  
  Accepted: Module
- [ ] **Blank, easy:** A tensor is moved to an NVIDIA GPU with .to('________').  
  Accepted: cuda
- [ ] **Blank, medium:** Calling loss.________() computes the gradients.  
  Accepted: backward
- [ ] **Blank, medium:** optimizer.________() updates the weights using the gradients.  
  Accepted: step
- [ ] **Blank, medium:** The ________ class batches and shuffles samples from a Dataset.  
  Accepted: DataLoader
- [ ] **Blank, medium:** PyTorch's automatic differentiation engine is called ________.  
  Accepted: autograd
- [ ] **Blank, hard:** Because gradients add up by default, you call optimizer.________() at each step.  
  Accepted: zero_grad
- [ ] **Blank, hard:** Wrapping evaluation code in torch.________() stops gradient tracking and saves memory.  
  Accepted: no_grad, inference_mode
- [ ] **Blank, hard:** The recommended way to save weights is to save the model's ________.  
  Accepted: state_dict
- [ ] **Brief, easy:** What is PyTorch, and why is it popular?
  - Key fact: PyTorch is a Python library for building and training neural networks
  - Key fact: it uses tensors that run on CPUs or GPUs and computes gradients automatically
  - Key fact: it feels like normal Python, which makes debugging easy
  - Keywords (grading without AI): PyTorch, tensor, GPU, autograd, Python
- [ ] **Brief, medium:** Walk through a basic PyTorch training loop.
  - Key fact: for each batch, run the forward pass and compute the loss
  - Key fact: call loss.backward() to get the gradients, then optimizer.step() to update the weights
  - Key fact: call optimizer.zero_grad() so gradients do not add up, and track the validation loss
  - Keywords (grading without AI): forward, loss, backward, step, zero_grad
- [ ] **Brief, medium:** What do model.train() and model.eval() change?
  - Key fact: they switch layers that behave differently in training and in inference
  - Key fact: in eval mode dropout is off and batch norm uses its running statistics
  - Key fact: use torch.no_grad() during evaluation to save memory and time
  - Keywords (grading without AI): train, eval, dropout, batch norm, no_grad
- [ ] **Brief, hard:** How would you speed up PyTorch training on a GPU?
  - Key fact: keep the GPU busy with a DataLoader that uses several workers and pinned memory
  - Key fact: use mixed precision with torch.autocast and a gradient scaler
  - Key fact: use larger batches when memory allows, and torch.compile or several GPUs where it helps
  - Keywords (grading without AI): DataLoader, num_workers, mixed precision, batch size, torch.compile
- [ ] **Brief, hard:** How do you save and load a PyTorch model safely for later use?
  - Key fact: save the state_dict, not the whole model object
  - Key fact: to load, create the model class, call load_state_dict, then call eval()
  - Key fact: also save the optimizer state and the epoch to resume training, and record the library versions
  - Keywords (grading without AI): state_dict, load_state_dict, eval, optimizer, checkpoint

## Model Deployment (bank_dataeng.py)

- [ ] **MC, easy:** Which are common ways to serve a model?
  - A. **A REST API**
  - B. **Batch predictions on a schedule**
  - C. **Running on the user's device**
  - D. Emailing the weights to users
- [ ] **MC, easy:** Which statements about batch and online inference are true?
  - A. **Batch inference scores many rows at set times**
  - B. **Online inference answers one request at a time, quickly**
  - C. **Online inference needs low latency**
  - D. Batch inference is always real time
- [ ] **MC, easy:** Which help package a model for deployment?
  - A. **A Docker container**
  - B. **Saving the model file with its version**
  - C. **Listing dependencies with their versions**
  - D. Copying only the training notebook
- [ ] **MC, medium:** Which statements about model serving tools are true?
  - A. **FastAPI can wrap a model in a REST API**
  - B. **TorchServe and TensorFlow Serving serve models**
  - C. **They can batch requests for speed**
  - D. They train the model
- [ ] **MC, medium:** Which statements about latency and throughput are true?
  - A. **Latency is the time for one prediction**
  - B. **Throughput is the number of predictions per second**
  - C. **Batching can raise throughput but add latency**
  - D. They are the same thing
- [ ] **MC, medium:** Which make a model smaller or faster to serve?
  - A. **Quantization**
  - B. **Pruning**
  - C. **Distillation**
  - D. Adding more layers
- [ ] **MC, medium:** Which statements about training-serving skew are true?
  - A. **Features computed differently in training and serving cause it**
  - B. **It lowers accuracy in production**
  - C. **Sharing feature code or a feature store reduces it**
  - D. It cannot happen
- [ ] **MC, hard:** Which statements about canary and shadow deployments are true?
  - A. **A canary sends a small share of traffic to the new model**
  - B. **A shadow model gets real traffic, but its answers are not used**
  - C. **Both reduce the risk of a bad release**
  - D. Both send all traffic to the new model at once
- [ ] **MC, hard:** Which statements about A/B testing models are true?
  - A. **Users are split between the models**
  - B. **A business metric decides the winner**
  - C. **It needs enough traffic to be significant**
  - D. It compares models only on training data
- [ ] **MC, hard:** Which should you monitor for a deployed model?
  - A. **Latency and errors**
  - B. **Drift in the input data**
  - C. **The prediction distribution, and accuracy once labels arrive**
  - D. Only the training loss
- [ ] **Blank, easy:** Using a trained model to make predictions is called ________.  
  Accepted: inference
- [ ] **Blank, easy:** Scoring many records at set times is ________ inference.  
  Accepted: batch
- [ ] **Blank, easy:** A model and its dependencies are often packaged into an image with ________.  
  Accepted: Docker
- [ ] **Blank, medium:** The time it takes to return one prediction is called ________.  
  Accepted: latency
- [ ] **Blank, medium:** Storing model weights with fewer bits, such as int8, is called ________.  
  Accepted: quantization, quantisation
- [ ] **Blank, medium:** Training a small model to copy a large one is called knowledge ________.  
  Accepted: distillation
- [ ] **Blank, medium:** Features computed differently in training and serving cause training-serving ________.  
  Accepted: skew
- [ ] **Blank, hard:** Sending a small share of traffic to a new model first is a ________ release.  
  Accepted: canary
- [ ] **Blank, hard:** Running a new model on real traffic without using its answers is a ________ deployment.  
  Accepted: shadow
- [ ] **Blank, hard:** The open format for moving models between frameworks is ________.  
  Accepted: ONNX
- [ ] **Brief, easy:** What does it mean to deploy a machine learning model?
  - Key fact: make the trained model available to give predictions to real users or systems
  - Key fact: it can be served as an API, run as scheduled batch jobs or run on a device
  - Key fact: it includes packaging, versioning and monitoring, not only training
  - Keywords (grading without AI): deploy, API, batch, package, monitor
- [ ] **Brief, medium:** When would you use batch inference instead of a real-time API?
  - Key fact: batch suits predictions that can be computed ahead, such as nightly recommendations
  - Key fact: a real-time API suits predictions that need fresh input, such as fraud checks at checkout
  - Key fact: batch is cheaper and simpler, while real time needs low latency and high availability
  - Keywords (grading without AI): batch, real time, latency, cost, availability
- [ ] **Brief, medium:** How would you serve a model as a REST API?
  - Key fact: load the model once when the service starts, not on each request
  - Key fact: wrap it in a web framework such as FastAPI and validate the input
  - Key fact: package it in a container with pinned dependencies and add health checks
  - Keywords (grading without AI): FastAPI, load once, validation, container, health check
- [ ] **Brief, hard:** How would you roll out a new model version safely?
  - Key fact: test it offline on recent data and compare it with the current model
  - Key fact: release it as a shadow or a canary to a small share of traffic and watch the metrics
  - Key fact: keep the old version ready for a fast rollback
  - Keywords (grading without AI): offline evaluation, shadow, canary, metrics, rollback
- [ ] **Brief, hard:** How would you make model predictions fast enough for production?
  - Key fact: measure latency first and find the slowest step, often the feature lookup or the model
  - Key fact: make the model lighter with quantization, distillation or a smaller architecture
  - Key fact: batch requests, use a GPU or an optimized runtime such as ONNX Runtime, and cache repeated results
  - Keywords (grading without AI): latency, quantization, distillation, batching, ONNX Runtime

## MLOps (bank_dataeng.py)

- [ ] **MC, easy:** Which are goals of MLOps?
  - A. **Repeatable model training**
  - B. **Reliable deployment**
  - C. **Monitoring models in production**
  - D. Avoiding version control
- [ ] **MC, easy:** Which should be versioned in an ML project?
  - A. **Code**
  - B. **Data**
  - C. **Models**
  - D. Coffee orders
- [ ] **MC, easy:** Which of these are MLOps tools?
  - A. **MLflow**
  - B. **Kubeflow**
  - C. **DVC**
  - D. Photoshop
- [ ] **MC, medium:** Which statements about experiment tracking are true?
  - A. **It records the parameters, metrics and artifacts of each run**
  - B. **It makes results reproducible and comparable**
  - C. **MLflow and Weights and Biases do it**
  - D. It replaces testing
- [ ] **MC, medium:** Which statements about a model registry are true?
  - A. **It stores model versions with metadata**
  - B. **It tracks stages such as staging and production**
  - C. **It supports rolling back to an older version**
  - D. It stores the training data
- [ ] **MC, medium:** Which statements about CI/CD for machine learning are true?
  - A. **It tests code and data before training**
  - B. **It can retrain and validate models automatically**
  - C. **It deploys models that pass the checks**
  - D. It deploys every model without checks
- [ ] **MC, medium:** Which statements about data drift are true?
  - A. **The input data distribution changes over time**
  - B. **It can lower model accuracy**
  - C. **It is detected by comparing new data with the training data**
  - D. It never affects models
- [ ] **MC, hard:** Which statements about concept drift are true?
  - A. **The relationship between the inputs and the target changes**
  - B. **Accuracy drops even when the inputs look the same**
  - C. **Retraining on recent data helps**
  - D. It is the same as a code bug
- [ ] **MC, hard:** Which statements about a feature store are true?
  - A. **It stores features for training and serving**
  - B. **It reduces training-serving skew**
  - C. **It builds point-in-time correct training data**
  - D. It replaces the model
- [ ] **MC, hard:** Which make ML training reproducible?
  - A. **Fixing random seeds**
  - B. **Versioning data and code**
  - C. **Recording library versions**
  - D. Training on a new unrecorded random sample each time
- [ ] **Blank, easy:** Applying DevOps practices to machine learning is called ________.  
  Accepted: MLOps
- [ ] **Blank, easy:** ________ is an open-source tool for tracking experiments and models.  
  Accepted: MLflow
- [ ] **Blank, easy:** A tool such as DVC versions ________ alongside the code.  
  Accepted: data, datasets
- [ ] **Blank, medium:** A central store of model versions and their stages is a model ________.  
  Accepted: registry
- [ ] **Blank, medium:** A change in the distribution of the input data over time is data ________.  
  Accepted: drift
- [ ] **Blank, medium:** Recording the parameters and metrics of each training run is experiment ________.  
  Accepted: tracking
- [ ] **Blank, medium:** Training a model again on newer data is called ________.  
  Accepted: retraining
- [ ] **Blank, hard:** A change in the relationship between the inputs and the target is ________ drift.  
  Accepted: concept
- [ ] **Blank, hard:** A system that serves the same features for training and inference is a feature ________.  
  Accepted: store
- [ ] **Blank, hard:** Training data built only from values known at each past moment is point-in-________ correct.  
  Accepted: time
- [ ] **Brief, easy:** What is MLOps, and why is it needed?
  - Key fact: MLOps applies DevOps practices such as automation and monitoring to machine learning
  - Key fact: it makes training repeatable and deployment reliable
  - Key fact: models degrade as data changes, so they need monitoring and retraining
  - Keywords (grading without AI): MLOps, automation, reproducible, deployment, monitoring
- [ ] **Brief, medium:** Why version data and models, not only code?
  - Key fact: the same code on different data gives a different model
  - Key fact: versioning lets you reproduce, compare and audit any model
  - Key fact: it allows rolling back to a known good model and data set
  - Keywords (grading without AI): version, data, model, reproduce, rollback
- [ ] **Brief, medium:** What is data drift, and how do you detect it?
  - Key fact: data drift is a change in the distribution of the input features over time
  - Key fact: compare live data with training data using statistics such as PSI or a KS test
  - Key fact: alert on large changes and check model accuracy once labels arrive
  - Keywords (grading without AI): data drift, distribution, PSI, monitor, alert
- [ ] **Brief, hard:** How would you set up automatic retraining for a model?
  - Key fact: trigger retraining on a schedule or when drift or accuracy alerts fire
  - Key fact: run a pipeline that validates the data, trains, and evaluates against the current model
  - Key fact: promote the new model through the registry only if it is better, with a way to roll back
  - Keywords (grading without AI): trigger, pipeline, validate, registry, rollback
- [ ] **Brief, hard:** What is a feature store, and what problems does it solve?
  - Key fact: it keeps feature definitions and values in one place for training and serving
  - Key fact: using the same feature logic in both prevents training-serving skew
  - Key fact: it builds point-in-time correct training sets and lets teams reuse features
  - Keywords (grading without AI): feature store, skew, point-in-time, reuse, serving

## LLM APIs (bank_dataeng.py)

- [ ] **MC, easy:** Which of these provide LLM APIs?
  - A. **OpenAI**
  - B. **Anthropic**
  - C. **The Google Gemini API**
  - D. Apache Tomcat
- [ ] **MC, easy:** Which statements about tokens are true?
  - A. **Text is split into tokens before the model reads it**
  - B. **Pricing is usually per token**
  - C. **Both input and output tokens are counted**
  - D. A token is always one whole word
- [ ] **MC, easy:** Which statements about a chat request are true?
  - A. **It sends a list of messages**
  - B. **A system message sets the model's behaviour**
  - C. **The reply comes back as an assistant message**
  - D. The model remembers earlier requests without being sent them
- [ ] **MC, medium:** Which statements about temperature are true?
  - A. **Lower values make answers more predictable**
  - B. **Higher values make answers more varied**
  - C. **0 suits extraction tasks**
  - D. It changes the price per token
- [ ] **MC, medium:** Which statements about the context window are true?
  - A. **It limits how many tokens fit in one request**
  - B. **It counts the prompt and the reply**
  - C. **Long conversations may need trimming or summarizing**
  - D. It is unlimited for every model
- [ ] **MC, medium:** Which help get structured output from an LLM?
  - A. **Asking for JSON that follows a schema**
  - B. **The provider's structured output or tool calling features**
  - C. **Validating the reply in code**
  - D. Trusting the reply without checks
- [ ] **MC, medium:** Which statements about streaming responses are true?
  - A. **Tokens are sent as they are generated**
  - B. **Users see the answer start sooner**
  - C. **The total time stays about the same**
  - D. It makes the model smarter
- [ ] **MC, hard:** What should an app do when an LLM API rate-limits it?
  - A. **Retry with exponential backoff**
  - B. **Respect the Retry-After header**
  - C. **Queue or reduce its requests**
  - D. Retry immediately in a tight loop
- [ ] **MC, hard:** Which statements about tool calling are true?
  - A. **The model can ask the app to run a function with arguments**
  - B. **The app runs the function and sends back the result**
  - C. **The model then uses the result in its answer**
  - D. The model always runs the code itself on the provider's servers
- [ ] **MC, hard:** Which keep an LLM feature safe?
  - A. **Never putting API keys in front-end code**
  - B. **Treating model output as untrusted input**
  - C. **Guarding against prompt injection in user or document text**
  - D. Letting the model run any SQL it writes
- [ ] **Blank, easy:** LLM APIs charge by the number of ________ in the input and the output.  
  Accepted: tokens
- [ ] **Blank, easy:** The message that sets the model's role and rules is the ________ message.  
  Accepted: system
- [ ] **Blank, easy:** An LLM API key should stay on the ________, never in browser code.  
  Accepted: server, backend
- [ ] **Blank, medium:** The setting that controls how random the output is, is called ________.  
  Accepted: temperature
- [ ] **Blank, medium:** The maximum number of tokens a model can take in one request is its context ________.  
  Accepted: window
- [ ] **Blank, medium:** Sending tokens to the client as they are generated is called ________.  
  Accepted: streaming
- [ ] **Blank, medium:** A 429 reply from an LLM API means the request was ________-limited.  
  Accepted: rate
- [ ] **Blank, hard:** Letting the model ask your app to run a function with arguments is called tool ________.  
  Accepted: calling, use
- [ ] **Blank, hard:** Hidden instructions in user or document text that try to override the system prompt are a prompt ________.  
  Accepted: injection
- [ ] **Blank, hard:** Retrying with a wait that doubles after each failure is called exponential ________.  
  Accepted: backoff
- [ ] **Brief, easy:** How does an application use an LLM API?
  - Key fact: the app sends a request with messages, such as a system message and the user's input, to the provider
  - Key fact: the model returns generated text, which the app shows or processes
  - Key fact: the app pays per token and must keep the API key secret on the server
  - Keywords (grading without AI): request, messages, tokens, API key, response
- [ ] **Brief, medium:** How do you get reliable structured output from an LLM?
  - Key fact: ask for a specific JSON shape and give an example or a schema
  - Key fact: use the provider's structured output or tool calling features where they exist
  - Key fact: validate the reply in code and retry or fall back if it is invalid
  - Keywords (grading without AI): JSON, schema, structured output, validate, retry
- [ ] **Brief, medium:** How do you handle rate limits and errors from an LLM API?
  - Key fact: retry temporary errors with exponential backoff and respect Retry-After
  - Key fact: set timeouts and limit the number of concurrent requests
  - Key fact: fall back to a simpler path or a clear message when the API stays unavailable
  - Keywords (grading without AI): rate limit, backoff, Retry-After, timeout, fallback
- [ ] **Brief, hard:** What security risks come with LLM features, and how do you reduce them?
  - Key fact: prompt injection: treat user and document text as data and keep instructions separate
  - Key fact: model output is untrusted, so validate it and never run it directly as code or SQL
  - Key fact: protect API keys and personal data, and limit which tools the model may call
  - Keywords (grading without AI): prompt injection, untrusted output, validation, API key, least privilege
- [ ] **Brief, hard:** How would you keep the cost and latency of LLM calls under control?
  - Key fact: use the smallest model that does the job, with shorter prompts
  - Key fact: cache repeated answers and use prompt caching where the provider offers it
  - Key fact: stream responses, limit output length and monitor token use per feature
  - Keywords (grading without AI): model choice, prompt length, caching, streaming, token usage

## Prompt Engineering (bank_dataeng.py)

- [ ] **MC, easy:** Which make a prompt clearer?
  - A. **Stating the task and the desired output format**
  - B. **Giving the relevant context**
  - C. **Showing an example of a good answer**
  - D. Leaving the task vague
- [ ] **MC, easy:** Which statements about system prompts are true?
  - A. **They set the model's role and rules**
  - B. **They apply to the whole conversation**
  - C. **They are a good place for output format rules**
  - D. Users always see them
- [ ] **MC, easy:** Which statements about few-shot prompting are true?
  - A. **The prompt includes example inputs and outputs**
  - B. **The examples show the expected format**
  - C. **Varied examples help**
  - D. It means sending very few tokens
- [ ] **MC, medium:** Which help reduce hallucinations?
  - A. **Giving the model the source text to answer from**
  - B. **Allowing it to say it does not know**
  - C. **Asking it to quote the source**
  - D. Asking for longer answers
- [ ] **MC, medium:** Which statements about chain-of-thought prompting are true?
  - A. **Asking the model to reason step by step can help on hard tasks**
  - B. **It uses more output tokens**
  - C. **Some models reason internally without being asked**
  - D. It always makes answers shorter
- [ ] **MC, medium:** Which help a model follow a strict output format?
  - A. **Showing the exact format**
  - B. **Delimiters such as XML tags**
  - C. **Validating and retrying in code**
  - D. Hoping it guesses
- [ ] **MC, medium:** Which statements about prompt templates are true?
  - A. **They keep fixed instructions and fill in variables**
  - B. **They keep prompts consistent**
  - C. **They can be versioned like code**
  - D. User input should be mixed freely into the instructions
- [ ] **MC, hard:** Which reduce the risk of prompt injection?
  - A. **Wrapping untrusted text in clear delimiters**
  - B. **Telling the model to treat that text as data**
  - C. **Limiting which tools the model can call**
  - D. Putting secrets in the prompt
- [ ] **MC, hard:** Which statements about evaluating prompts are true?
  - A. **Use a fixed set of test cases**
  - B. **Compare prompt versions on the same cases**
  - C. **Check outputs automatically where possible**
  - D. Judge a prompt by a single example
- [ ] **MC, hard:** Which statements about splitting a task into steps are true?
  - A. **Prompt chaining sends the output of one prompt into the next**
  - B. **Smaller steps are easier to test**
  - C. **Each step can use a different model**
  - D. It always makes the results worse
- [ ] **Blank, easy:** Including a few example inputs and outputs in a prompt is ________ prompting.  
  Accepted: few-shot, few shot
- [ ] **Blank, easy:** Asking with no examples at all is ________ prompting.  
  Accepted: zero-shot, zero shot
- [ ] **Blank, easy:** A model stating false information with confidence is called a ________.  
  Accepted: hallucination
- [ ] **Blank, medium:** Asking the model to reason step by step is chain-of-________ prompting.  
  Accepted: thought
- [ ] **Blank, medium:** Marking parts of a prompt with tags such as <document> uses ________.  
  Accepted: delimiters, tags
- [ ] **Blank, medium:** Feeding the output of one prompt into the next is called prompt ________.  
  Accepted: chaining
- [ ] **Blank, medium:** A reusable prompt with placeholders for the inputs is a prompt ________.  
  Accepted: template
- [ ] **Blank, hard:** Text that tries to override the system prompt is a prompt ________ attack.  
  Accepted: injection
- [ ] **Blank, hard:** Using one model to grade another model's output is called LLM-as-a-________.  
  Accepted: judge
- [ ] **Blank, hard:** Writing the first words of the assistant's reply yourself to steer its format is called ________ the response.  
  Accepted: prefilling, prefill
- [ ] **Brief, easy:** What makes a good prompt?
  - Key fact: it states the task, the audience and the output format clearly
  - Key fact: it gives the context and data the model needs
  - Key fact: it includes an example or constraints when the format matters
  - Keywords (grading without AI): task, context, format, example, constraints
- [ ] **Brief, medium:** How do you reduce hallucinations in LLM answers?
  - Key fact: give the model the source documents and tell it to answer only from them
  - Key fact: let it say it does not know instead of guessing
  - Key fact: ask for quotes or citations and check them
  - Keywords (grading without AI): grounding, source, I do not know, citation, verify
- [ ] **Brief, medium:** When would you use few-shot examples, and what makes them good?
  - Key fact: use them when the format or style is hard to describe in words
  - Key fact: pick varied examples that cover edge cases
  - Key fact: keep them consistent with the instructions, because the model copies them closely
  - Keywords (grading without AI): few-shot, examples, format, edge cases, consistent
- [ ] **Brief, hard:** How would you evaluate and improve a prompt systematically?
  - Key fact: build a test set of realistic inputs with expected outputs or grading rules
  - Key fact: run each prompt version on the whole set and score it automatically where possible
  - Key fact: change one thing at a time and keep the version that scores best
  - Keywords (grading without AI): test set, version, score, LLM-as-a-judge, iterate
- [ ] **Brief, hard:** How do you protect an LLM app from prompt injection?
  - Key fact: keep the instructions in the system prompt and wrap untrusted text in delimiters as data
  - Key fact: limit the tools and data the model can reach, and require approval for risky actions
  - Key fact: validate the output and never let it run directly as code or queries
  - Keywords (grading without AI): prompt injection, delimiters, least privilege, validation, untrusted

## Retrieval-Augmented Generation (bank_dataeng.py)

- [ ] **MC, easy:** Which statements about RAG are true?
  - A. **It retrieves relevant documents and gives them to the model**
  - B. **It helps answer from private or recent data**
  - C. **It can reduce hallucinations**
  - D. It retrains the model for every question
- [ ] **MC, easy:** Which are steps of a RAG pipeline?
  - A. **Splitting documents into chunks**
  - B. **Embedding and indexing the chunks**
  - C. **Retrieving chunks for a query and generating an answer**
  - D. Deleting the documents after indexing
- [ ] **MC, easy:** Which statements about embeddings are true?
  - A. **They turn text into vectors of numbers**
  - B. **Similar meanings get nearby vectors**
  - C. **They make semantic search possible**
  - D. They store the text compressed so it can be read back
- [ ] **MC, medium:** Which statements about chunking are true?
  - A. **Chunks that are too big add noise**
  - B. **Chunks that are too small lose context**
  - C. **Overlap between chunks keeps context across boundaries**
  - D. Chunk size never matters
- [ ] **MC, medium:** Which statements about hybrid search are true?
  - A. **It combines keyword search such as BM25 with vector search**
  - B. **It finds exact terms such as product codes**
  - C. **It often improves retrieval quality**
  - D. It uses only keywords
- [ ] **MC, medium:** Which statements about reranking are true?
  - A. **A reranker scores retrieved chunks against the query more precisely**
  - B. **It runs on a short list of candidates**
  - C. **It improves the order of the results**
  - D. It replaces the retrieval step
- [ ] **MC, medium:** Which help keep a RAG answer grounded?
  - A. **Telling the model to answer only from the context**
  - B. **Asking for citations of the chunks used**
  - C. **Saying I do not know when the context lacks the answer**
  - D. Giving it no retrieved context
- [ ] **MC, hard:** Which of these measure retrieval quality?
  - A. **Recall at k**
  - B. **Mean reciprocal rank**
  - C. **Precision at k**
  - D. Model temperature
- [ ] **MC, hard:** Which statements about evaluating RAG answers are true?
  - A. **Faithfulness checks that the answer is supported by the context**
  - B. **Answer relevance checks that it addresses the question**
  - C. **A test set of questions with expected sources helps**
  - D. Measuring answer length alone is enough
- [ ] **MC, hard:** Which statements about metadata filtering in RAG are true?
  - A. **It limits retrieval to chunks whose fields, such as date or owner, match**
  - B. **It helps enforce access control**
  - C. **It improves precision**
  - D. It always removes the need for embeddings
- [ ] **Blank, easy:** RAG stands for Retrieval-Augmented ________.  
  Accepted: Generation
- [ ] **Blank, easy:** Turning text into vectors that capture meaning produces ________.  
  Accepted: embeddings
- [ ] **Blank, easy:** Splitting documents into smaller pieces for retrieval is called ________.  
  Accepted: chunking
- [ ] **Blank, medium:** Searching by meaning instead of exact words is ________ search.  
  Accepted: semantic, vector
- [ ] **Blank, medium:** Combining keyword search with vector search is ________ search.  
  Accepted: hybrid
- [ ] **Blank, medium:** A model that reorders retrieved chunks by relevance is a ________.  
  Accepted: reranker, re-ranker, cross-encoder
- [ ] **Blank, medium:** Repeating some text between neighbouring chunks is called chunk ________.  
  Accepted: overlap
- [ ] **Blank, hard:** An answer that is fully supported by the retrieved context is ________.  
  Accepted: faithful, grounded
- [ ] **Blank, hard:** The classic keyword ranking function often used in hybrid search is ________.  
  Accepted: BM25
- [ ] **Blank, hard:** Restricting retrieval to chunks with matching fields such as owner or date is metadata ________.  
  Accepted: filtering
- [ ] **Brief, easy:** What is retrieval-augmented generation?
  - Key fact: RAG retrieves relevant pieces of your documents for each question
  - Key fact: it adds them to the prompt so the model answers from that context
  - Key fact: it lets a model use private or recent data without retraining and reduces hallucinations
  - Keywords (grading without AI): retrieve, context, documents, prompt, hallucination
- [ ] **Brief, medium:** How do you choose a chunking strategy for RAG?
  - Key fact: split along natural boundaries such as headings or paragraphs
  - Key fact: pick a size that keeps one idea per chunk, often a few hundred tokens, with some overlap
  - Key fact: test retrieval quality with real questions and adjust
  - Keywords (grading without AI): chunk size, overlap, headings, tokens, evaluate
- [ ] **Brief, medium:** Why use hybrid search and reranking in RAG?
  - Key fact: vector search finds meaning but can miss exact terms such as codes or names
  - Key fact: keyword search such as BM25 finds exact matches, and hybrid search combines the two
  - Key fact: a reranker then orders the best candidates more precisely before they reach the model
  - Keywords (grading without AI): hybrid, BM25, vector, reranker, exact match
- [ ] **Brief, hard:** How would you evaluate a RAG system?
  - Key fact: build a test set of questions with the expected source passages
  - Key fact: measure retrieval with recall at k, and answers with faithfulness and relevance
  - Key fact: inspect failures to see whether retrieval or generation is the weak part
  - Keywords (grading without AI): test set, recall at k, faithfulness, relevance, failure analysis
- [ ] **Brief, hard:** A RAG chatbot gives wrong answers. How would you debug it?
  - Key fact: check whether the right chunks were retrieved for the question
  - Key fact: if not, fix the chunking, the embeddings, hybrid search or the filters
  - Key fact: if they were, fix the prompt so the model uses only the context and cites it
  - Keywords (grading without AI): retrieval, chunks, embeddings, prompt, grounding

## Vector Databases (bank_dataeng.py)

- [ ] **MC, easy:** Which of these are vector databases or vector search tools?
  - A. **Pinecone**
  - B. **Weaviate**
  - C. **pgvector**
  - D. Microsoft Excel
- [ ] **MC, easy:** Which statements about vector databases are true?
  - A. **They store embeddings**
  - B. **They find the vectors most similar to a query vector**
  - C. **They power semantic search and RAG**
  - D. They store only images
- [ ] **MC, easy:** Which are similarity measures for vectors?
  - A. **Cosine similarity**
  - B. **Dot product**
  - C. **Euclidean distance**
  - D. Alphabetical order
- [ ] **MC, medium:** Which statements about approximate nearest neighbour search are true?
  - A. **It trades a little accuracy for much more speed**
  - B. **HNSW is a common index for it**
  - C. **It scales to millions of vectors**
  - D. It always returns the exact nearest vectors
- [ ] **MC, medium:** Which statements about metadata in vector databases are true?
  - A. **Vectors can carry fields such as source or date**
  - B. **Queries can filter on those fields**
  - C. **Filtering helps enforce access control**
  - D. Metadata cannot be stored
- [ ] **MC, medium:** Which statements about embedding dimensions are true?
  - A. **Every vector in an index must have the same dimension**
  - B. **Higher dimensions use more memory**
  - C. **The dimension comes from the embedding model**
  - D. Mixing embedding models in one index is fine
- [ ] **MC, medium:** Which statements about pgvector are true?
  - A. **It adds vector search to PostgreSQL**
  - B. **It keeps vectors next to normal tables**
  - C. **It supports indexes such as HNSW**
  - D. It works only in MongoDB
- [ ] **MC, hard:** Which statements about the HNSW index are true?
  - A. **It is a layered graph of vectors**
  - B. **It gives fast and accurate search**
  - C. **It uses more memory than a flat index**
  - D. It scans every vector for each query
- [ ] **MC, hard:** Which statements about changing the embedding model are true?
  - A. **All stored vectors must be embedded again**
  - B. **Old and new vectors cannot be compared**
  - C. **A new index is usually built alongside the old one**
  - D. Old vectors can safely be mixed with new ones
- [ ] **MC, hard:** Which reduce memory use for large vector indexes?
  - A. **Product quantization**
  - B. **Smaller embedding dimensions**
  - C. **Keeping vectors on disk with a cache**
  - D. Storing every vector twice
- [ ] **Blank, easy:** Vector databases store numeric representations of text called ________.  
  Accepted: embeddings, vectors
- [ ] **Blank, easy:** A common measure of how similar two vectors are is ________ similarity.  
  Accepted: cosine
- [ ] **Blank, easy:** The PostgreSQL extension for vector search is ________.  
  Accepted: pgvector
- [ ] **Blank, medium:** Search that trades a little accuracy for speed is approximate nearest ________ search.  
  Accepted: neighbor, neighbour
- [ ] **Blank, medium:** A popular graph-based vector index is ________.  
  Accepted: HNSW
- [ ] **Blank, medium:** The number of values in each vector is its ________.  
  Accepted: dimension, dimensionality
- [ ] **Blank, medium:** Restricting a vector search by fields such as a user ID is metadata ________.  
  Accepted: filtering
- [ ] **Blank, hard:** Compressing vectors by splitting them into parts and storing short codes is product ________.  
  Accepted: quantization, quantisation
- [ ] **Blank, hard:** Finding the k vectors most similar to a query is a k-nearest ________ search.  
  Accepted: neighbors, neighbours
- [ ] **Blank, hard:** After changing the embedding model, every document must be ________ again.  
  Accepted: embedded, indexed
- [ ] **Brief, easy:** What is a vector database, and what is it used for?
  - Key fact: it stores embeddings, vectors that represent the meaning of text or images
  - Key fact: it finds the vectors most similar to a query vector
  - Key fact: it powers semantic search, recommendations and RAG
  - Keywords (grading without AI): embedding, similarity, semantic search, RAG, vector
- [ ] **Brief, medium:** How does similarity search work?
  - Key fact: text is turned into a vector by an embedding model
  - Key fact: the query is embedded the same way and compared with the stored vectors
  - Key fact: cosine similarity or dot product ranks the closest vectors as the best matches
  - Keywords (grading without AI): embedding, query, cosine, dot product, nearest
- [ ] **Brief, medium:** Should you use a dedicated vector database or pgvector?
  - Key fact: pgvector keeps vectors in PostgreSQL next to your other data, with joins and transactions
  - Key fact: a dedicated vector database scales to very large indexes and offers more search features
  - Key fact: for small to medium data, pgvector is often simpler to run
  - Keywords (grading without AI): pgvector, PostgreSQL, dedicated, scale, simplicity
- [ ] **Brief, hard:** What is approximate nearest neighbour search, and why is it used?
  - Key fact: exact search compares the query with every vector, which is slow for millions of them
  - Key fact: ANN indexes such as HNSW or IVF find almost the closest vectors much faster
  - Key fact: their settings trade recall against speed and memory and should be tuned with tests
  - Keywords (grading without AI): ANN, HNSW, IVF, recall, speed
- [ ] **Brief, hard:** How would you design vector search for many users who must see only their own documents?
  - Key fact: store an owner or tenant ID as metadata on each vector
  - Key fact: filter every query by that ID on the server, never trusting an ID sent by the client
  - Key fact: or use a separate index or namespace per tenant for strong isolation
  - Keywords (grading without AI): tenant, metadata, filter, namespace, isolation

## Cost Management (bank_dataeng.py)

- [ ] **MC, easy:** Which help control cloud costs?
  - A. **Turning off unused resources**
  - B. **Right-sizing instances**
  - C. **Budgets and alerts**
  - D. Leaving test servers running all year
- [ ] **MC, easy:** Which statements about cloud pricing are true?
  - A. **Many services charge for what you use**
  - B. **Moving data out of a cloud can cost money**
  - C. **Storage is billed by size and time**
  - D. Everything in the cloud is free
- [ ] **MC, easy:** Which make LLM API use cheaper?
  - A. **A smaller model where it is good enough**
  - B. **Shorter prompts**
  - C. **Caching repeated answers**
  - D. Sending the whole document every time
- [ ] **MC, medium:** Which statements about reserved instances and savings plans are true?
  - A. **They give discounts for committing to usage**
  - B. **They suit steady workloads**
  - C. **They cost money even when unused**
  - D. They suit short experiments best
- [ ] **MC, medium:** Which statements about spot instances are true?
  - A. **They are much cheaper than on-demand instances**
  - B. **The provider can take them back at short notice**
  - C. **They suit fault-tolerant batch jobs**
  - D. They suit a single critical database
- [ ] **MC, medium:** Which show who is spending what?
  - A. **Tagging resources by team or project**
  - B. **Cost reports grouped by tag**
  - C. **Separate accounts per environment**
  - D. One shared account with no tags
- [ ] **MC, medium:** Which statements about autoscaling and cost are true?
  - A. **It adds capacity when load rises**
  - B. **It removes capacity when load falls**
  - C. **Scaling to zero can save money on idle services**
  - D. It always increases cost
- [ ] **MC, hard:** Which statements about FinOps are true?
  - A. **It brings finance, engineering and business together on cloud spending**
  - B. **It makes teams see and own their costs**
  - C. **It uses unit costs such as cost per customer**
  - D. It means cutting all spending
- [ ] **MC, hard:** Which reduce data transfer costs?
  - A. **Keeping services in the same region**
  - B. **Using a CDN for public content**
  - C. **Compressing data**
  - D. Copying data between regions all the time
- [ ] **MC, hard:** Which help control LLM costs in production?
  - A. **Tracking token use per feature and user**
  - B. **Limiting output length**
  - C. **Prompt caching for repeated prefixes**
  - D. Allowing unlimited requests per user
- [ ] **Blank, easy:** Matching a server's size to what it actually needs is called ________.  
  Accepted: right-sizing, rightsizing
- [ ] **Blank, easy:** Labels on resources that show which team owns them are called ________.  
  Accepted: tags
- [ ] **Blank, easy:** A spending limit that sends an alert when crossed is a ________.  
  Accepted: budget
- [ ] **Blank, medium:** Cheap instances that the provider can take back at short notice are ________ instances.  
  Accepted: spot
- [ ] **Blank, medium:** Committing to steady usage for a discount uses reserved instances or savings ________.  
  Accepted: plans
- [ ] **Blank, medium:** Adding and removing capacity as demand changes is called ________.  
  Accepted: autoscaling, auto scaling
- [ ] **Blank, medium:** LLM providers usually charge per ________ of input and output.  
  Accepted: token
- [ ] **Blank, hard:** The practice of managing cloud spending across finance and engineering is called ________.  
  Accepted: FinOps
- [ ] **Blank, hard:** Cost divided by a business measure, such as cost per order, is a ________ cost.  
  Accepted: unit
- [ ] **Blank, hard:** Reusing a long, repeated prompt prefix at a lower price is called prompt ________.  
  Accepted: caching
- [ ] **Brief, easy:** How would you keep cloud costs under control?
  - Key fact: turn off or delete unused resources and right-size the rest
  - Key fact: set budgets and alerts so surprises are caught early
  - Key fact: tag resources so each team sees its own spending
  - Keywords (grading without AI): unused, right-size, budget, alert, tags
- [ ] **Brief, medium:** When would you use reserved, on-demand and spot instances?
  - Key fact: on-demand suits short or unpredictable workloads
  - Key fact: reserved instances or savings plans suit steady, long-running workloads at a discount
  - Key fact: spot instances suit fault-tolerant batch jobs that can be interrupted
  - Keywords (grading without AI): on-demand, reserved, savings plan, spot, workload
- [ ] **Brief, medium:** How would you reduce the cost of an LLM feature?
  - Key fact: use the smallest model that meets the quality bar, and send only hard cases to larger ones
  - Key fact: shorten prompts, limit output length and cache repeated answers or prompt prefixes
  - Key fact: track token use per feature and user and set limits
  - Keywords (grading without AI): smaller model, prompt length, caching, token usage, limits
- [ ] **Brief, hard:** How would you find out why a cloud bill suddenly went up?
  - Key fact: break the bill down by service, account, region and tag to find what changed
  - Key fact: check what happened at that time, such as deployments, traffic or forgotten resources
  - Key fact: fix the cause and add an alert or budget so it is caught sooner next time
  - Keywords (grading without AI): cost breakdown, tags, deployment, anomaly, alert
- [ ] **Brief, hard:** How do you make teams responsible for their own cloud costs?
  - Key fact: tag resources and show each team its costs in regular reports
  - Key fact: track unit costs such as cost per customer next to product metrics
  - Key fact: give teams budgets and make cost part of design reviews
  - Keywords (grading without AI): tags, showback, unit cost, budget, ownership

## Behavioral

- [ ] **brief, teamwork:** Tell me about a time you helped a teammate who was stuck.
- [ ] **brief, teamwork:** Tell me about a time you worked with someone whose way of working was very different from yours.
- [ ] **voice, teamwork:** Tell me about a time you had to rely on others to finish your part of the work.
- [ ] **voice, teamwork:** Tell me about a time you shared something you knew that helped your team work faster.
- [ ] **brief, conflict:** Tell me about a time you disagreed with a teammate about a technical decision.
- [ ] **brief, conflict:** Tell me about a time you got criticism of your work that you did not agree with at first.
- [ ] **voice, conflict:** Tell me about a time you had to say no to a request from a teammate or a manager.
- [ ] **voice, conflict:** Tell me about a time two people you worked with wanted different things, and how you handled it.
- [ ] **brief, failure:** Tell me about a time something you built did not work as planned.
- [ ] **brief, failure:** Tell me about a time you missed something important in your work.
- [ ] **voice, failure:** Tell me about a time you made a mistake, and what you changed afterwards.
- [ ] **voice, failure:** Tell me about a time you had to start a piece of work over.
- [ ] **brief, leadership:** Tell me about a time you took the lead without being asked.
- [ ] **brief, leadership:** Tell me about a time you had to convince others to try your idea.
- [ ] **voice, leadership:** Tell me about a time you guided someone with less experience than you.
- [ ] **voice, leadership:** Tell me about a time you had to decide something while information was missing.
- [ ] **brief, deadline_pressure:** Tell me about a time you had to finish work with very little time.
- [ ] **brief, deadline_pressure:** Tell me about a time you had to choose what to leave out to meet a deadline.
- [ ] **voice, deadline_pressure:** Tell me about a time you were working on several tasks with close deadlines.
- [ ] **voice, deadline_pressure:** Tell me about a time you saw you would miss a deadline, and what you did.
