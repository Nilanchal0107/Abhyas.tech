"""Builds bank_starter.csv and V3__starter_bank.sql (FR-20, ticket 29) from the questions below, and checks them.

Run from this folder:  python gen_bank.py
The questions are general knowledge written by Claude and have NOT been reviewed. After the review, move V3__starter_bank.sql to
../migration/. Flyway only runs files in that folder, so nothing here is loaded yet.
"""
import csv, hashlib, json, re, sys
from pathlib import Path

HERE = Path(__file__).parent
BLANK = "________"

# (skill, multiple-correct question, [4 options], correct letters, fill-in-the-blank text, [accepted wordings per blank])
Q = [
 ("Data Structures", "Which operations on a hash table take O(1) time on average?", ["Looking up a value by key", "Inserting a key", "Listing all keys in sorted order", "Finding the smallest key"], "AB",
  f"A {BLANK} removes items in the same order they were added (first in, first out).", [["queue"]]),
 ("Algorithms", "Which of these sorting algorithms take O(n log n) time in the average case?", ["Merge sort", "Quick sort", "Bubble sort", "Insertion sort"], "AB",
  f"Binary search needs the input to be {BLANK} and takes O(log n) time.", [["sorted", "in sorted order"]]),
 ("Object-Oriented Programming", "Which of these are pillars of object-oriented programming?", ["Encapsulation", "Polymorphism", "Compilation", "Inheritance"], "ABD",
  f"Hiding the internal state of an object behind public methods is called {BLANK}.", [["encapsulation"]]),
 ("System Design", "Which techniques help a web service handle more traffic?", ["Load balancing", "Caching", "Removing all database indexes", "Horizontal scaling"], "ABD",
  f"Adding more machines to share the load is called {BLANK} scaling.", [["horizontal"]]),
 ("SQL", "Which statements about SQL joins are true?", ["An INNER JOIN returns only rows that match in both tables", "A LEFT JOIN keeps every row of the left table", "A CROSS JOIN needs an ON condition", "An INNER JOIN keeps unmatched rows of both tables"], "AB",
  f"The {BLANK} clause filters groups after GROUP BY has been applied.", [["HAVING"]]),
 ("Git", "Which statements about Git branches are true?", ["A branch is a movable pointer to a commit", "git merge combines the history of two branches", "Creating a branch copies every file of the project", "Deleting a branch at once deletes all its commits for good"], "AB",
  f"The command git {BLANK} downloads new commits from a remote without changing your working files.", [["fetch"]]),
 ("REST APIs", "Which HTTP methods are idempotent by definition?", ["GET", "PUT", "POST", "DELETE"], "ABD",
  f"A REST API usually answers with the status code {BLANK} when a resource was created.", [["201", "201 Created"]]),
 ("Unit Testing", "Which are good qualities of a unit test?", ["It runs fast", "It does not depend on other tests", "It only passes if tests run in a fixed order", "It checks one behaviour"], "ABD",
  f"A {BLANK} replaces a real dependency with a controlled fake during a test.", [["mock", "stub", "fake", "test double"]]),
 ("Design Patterns", "Which of these are creational design patterns?", ["Singleton", "Factory Method", "Observer", "Builder"], "ABD",
  f"The {BLANK} pattern lets many objects react when one object changes state.", [["observer"]]),
 ("Debugging", "Which actions help you find the cause of a bug?", ["Reproduce it reliably", "Read the error message and stack trace", "Change many things at once", "Pause the program at a breakpoint"], "ABD",
  f"A {BLANK} pauses a running program at a chosen line so you can inspect its variables.", [["breakpoint"]]),
 ("Agile", "Which of these are Scrum events?", ["Sprint planning", "Daily scrum", "Gantt review", "Sprint retrospective"], "ABD",
  f"In Scrum, a fixed period of work, usually one to four weeks, is called a {BLANK}.", [["sprint"]]),
 ("Java", "Which statements about Java are true?", ["Source code is compiled to bytecode that runs on the JVM", "A class can implement several interfaces", "A class can extend several classes", "Unused objects are reclaimed by garbage collection"], "ABD",
  f"In Java, the {BLANK} keyword makes a variable impossible to reassign.", [["final"]]),
 ("Spring Boot", "Which annotations are commonly used in a Spring Boot REST application?", ["@RestController", "@Service", "@Component", "@EntryPoint"], "ABC",
  f"The {BLANK} annotation on the main class turns on auto-configuration in Spring Boot.", [["@SpringBootApplication", "SpringBootApplication"]]),
 ("MySQL", "Which statements about indexes in MySQL are true?", ["They can speed up reads", "They add work to every write", "They make every query faster", "A primary key is indexed"], "ABD",
  f"The default MySQL storage engine, which supports transactions, is {BLANK}.", [["InnoDB"]]),
 ("Microservices", "Which are common challenges of microservices?", ["Network calls between services can fail", "Data is spread over several databases", "Services can never be deployed on their own", "Tracing one request across services is harder"], "ABD",
  f"An API {BLANK} gives clients one entry point to many backend services.", [["gateway"]]),
 ("Redis", "Which of these are Redis data types?", ["String", "List", "Sorted set", "Foreign key"], "ABC",
  f"Setting a {BLANK} on a Redis key makes it expire after a number of seconds.", [["TTL", "time to live", "expiry"]]),
 ("Message Queues", "Which benefits does a message queue give?", ["It decouples the producer from the consumer", "It smooths out traffic spikes", "It guarantees zero latency", "It lets consumers work at their own pace"], "ABD",
  f"A {BLANK} letter queue holds messages that could not be processed.", [["dead"]]),
 ("Authentication", "Which are good practices for storing passwords?", ["Hash them with a slow algorithm such as bcrypt", "Add a unique salt to each password", "Store them as plain text", "Never write them to logs"], "ABD",
  f"A signed {BLANK} carries claims and is often used for stateless login.", [["JWT", "JSON Web Token"]]),
 ("Docker", "Which statements about Docker are true?", ["An image is a read-only template for containers", "Containers share the kernel of the host", "Each container needs its own full operating-system kernel", "A Dockerfile describes how to build an image"], "ABD",
  f"The command docker {BLANK} starts a new container from an image.", [["run"]]),
 ("JavaScript", "Which of these values are falsy in JavaScript?", ["0", "The empty string", "The text 0 (a string holding the digit zero)", "null"], "ABD",
  f"The {BLANK} keyword declares a block-scoped variable that can be reassigned.", [["let"]]),
 ("TypeScript", "Which of these are features of TypeScript?", ["Static types", "Interfaces", "Compiles to JavaScript", "Runs in browsers without being compiled"], "ABC",
  f"TypeScript is compiled to plain {BLANK} before it runs.", [["JavaScript", "JS"]]),
 ("React", "Which statements about React are true?", ["Components can hold state", "Props are passed from a parent to a child", "Hooks may be called inside loops and conditions", "Changing state triggers a re-render"], "ABD",
  f"The {BLANK} hook adds local state to a function component.", [["useState"]]),
 ("HTML", "Which of these are semantic HTML elements?", ["nav", "article", "div", "footer"], "ABD",
  f"The {BLANK} attribute of an img tag gives text for screen readers and for images that fail to load.", [["alt"]]),
 ("CSS", "Which of these are CSS layout methods?", ["Flexbox", "Grid", "Float", "Gridflex"], "ABC",
  f"The CSS property {BLANK} sets the space between an element's content and its border.", [["padding"]]),
 ("Responsive Design", "Which help make a page responsive?", ["Media queries", "Widths in percentages", "Fixed pixel widths for the whole page", "The viewport meta tag"], "ABD",
  f"A CSS {BLANK} query applies styles only when the screen meets a condition such as a minimum width.", [["media"]]),
 ("Web Accessibility", "Which of these improve accessibility?", ["Text alternatives for images", "Every control usable with the keyboard", "Using colour alone to show meaning", "A visible focus indicator"], "ABD",
  f"ARIA stands for Accessible Rich Internet {BLANK}.", [["Applications"]]),
 ("State Management", "Which problems does a shared state store help with?", ["Many components reading the same data", "Predictable updates through defined actions", "Removing the need for any state", "Easier debugging of changes over time"], "ABD",
  f"In Redux, a {BLANK} is a function that takes the current state and an action and returns the new state.", [["reducer"]]),
 ("Web Performance", "Which usually improve page load time?", ["Compressing images", "Caching static files", "Loading every script synchronously in the head", "Minifying JavaScript"], "ABD",
  f"Loading images only when they are about to scroll into view is called {BLANK} loading.", [["lazy"]]),
 ("Node.js", "Which statements about Node.js are true?", ["It runs JavaScript outside the browser", "It is built around an event loop", "Every JavaScript statement runs on its own thread", "npm manages its packages"], "ABD",
  f"Node.js code that waits for input or output uses a {BLANK} model, so the main thread is not blocked.", [["non-blocking", "asynchronous", "async"]]),
 ("MongoDB", "Which statements about MongoDB are true?", ["It stores documents in a JSON-like format", "Collections group documents", "It needs a fixed schema for every collection", "Documents can contain nested documents"], "ABD",
  f"In MongoDB, a group of documents is stored in a {BLANK}.", [["collection"]]),
 ("Kotlin", "Which of these are Kotlin features?", ["Null safety in the type system", "Data classes", "A mandatory semicolon at the end of every line", "Coroutines"], "ABD",
  f"In Kotlin, the {BLANK} keyword declares a read-only variable.", [["val"]]),
 ("Swift", "Which statements about Swift are true?", ["Optionals represent a missing value", "Structs are value types", "Classes are value types", "ARC manages memory for class instances"], "ABD",
  f"In Swift, the {BLANK} keyword declares a constant.", [["let"]]),
 ("React Native", "Which statements about React Native are true?", ["It uses JavaScript or TypeScript", "It renders native UI components", "It draws everything inside a web view", "One codebase can target iOS and Android"], "ABD",
  f"In React Native, the {BLANK} component shows a long scrollable list and renders its items efficiently.", [["FlatList"]]),
 ("Mobile UI Design", "Which are good mobile UI practices?", ["Touch targets large enough to tap", "Clear navigation", "Very small text to fit more on screen", "Following the platform design guidelines"], "ABD",
  f"Material Design is the design system from {BLANK} for Android apps.", [["Google"]]),
 ("Local Storage", "Which are ways to store data on the device in a mobile app?", ["A SQLite database", "Key-value preferences", "The Keychain or Keystore for secrets", "Plain text files for passwords"], "ABC",
  f"Android includes the {BLANK} relational database engine for local storage.", [["SQLite"]]),
 ("App Lifecycle", "Which of these are Android Activity lifecycle callbacks?", ["onCreate", "onStart", "onPause", "onRender"], "ABC",
  f"On Android, {BLANK} is called when an activity is first created.", [["onCreate"]]),
 ("Push Notifications", "Which of these are used to deliver push notifications?", ["Firebase Cloud Messaging", "Apple Push Notification service", "SMTP email servers", "A device token that identifies the app install"], "ABD",
  f"Apple's service for delivering push notifications to iOS devices is abbreviated {BLANK}.", [["APNs", "APNS"]]),
 ("App Store Publishing", "Which are needed to publish an Android app on Google Play?", ["A developer account", "A signed app bundle", "An iPhone", "A store listing with a description and screenshots"], "ABD",
  f"New apps on Google Play are uploaded as an Android App {BLANK} (.aab file).", [["Bundle"]]),
 ("C#", "Which statements about C# are true?", ["It runs on the .NET runtime", "It has garbage collection", "Every reference must be a raw pointer", "It supports properties"], "ABD",
  f"In C#, the {BLANK} keyword marks a class that cannot be inherited.", [["sealed"]]),
 ("C++", "Which statements about C++ are true?", ["Destructors can free resources", "Memory can be managed by hand with new and delete", "It has a built-in garbage collector", "Templates support generic code"], "ABD",
  f"In C++, a {BLANK} stores the memory address of another variable.", [["pointer"]]),
 ("Unity", "Which of these are Unity concepts?", ["GameObject", "Prefab", "Rigidbody", "Dockerfile"], "ABC",
  f"In Unity, the {BLANK} method of a MonoBehaviour runs once per frame.", [["Update"]]),
 ("Game Physics", "Which of these are used in game physics?", ["Colliders", "Rigidbodies", "Gravity forces", "Primary keys"], "ABC",
  f"Unity runs physics code in FixedUpdate, at a {BLANK} time step.", [["fixed"]]),
 ("3D Math", "Which of these can represent a 3D rotation?", ["Quaternions", "Euler angles", "Rotation matrices", "Hash maps"], "ABC",
  f"The {BLANK} product of two vectors gives a single number and is related to the angle between them.", [["dot", "scalar"]]),
 ("Game Loop", "What does a typical game loop do on each frame?", ["Process input", "Update the game state", "Render the frame", "Recompile the game"], "ABC",
  f"Multiplying movement by {BLANK} time keeps speed the same on fast and slow computers.", [["delta"]]),
 ("Graphics Programming", "Which are stages or parts of a GPU rendering pipeline?", ["Vertex shader", "Fragment shader", "Rasterization", "Garbage collector"], "ABC",
  f"A {BLANK} shader runs for each pixel candidate to work out its colour.", [["fragment", "pixel"]]),
 ("Performance Optimization", "Which usually reduce CPU cost in a game?", ["Object pooling", "Culling objects that are not visible", "Creating new objects every frame", "Batching draw calls"], "ABD",
  f"Reusing objects from a ready-made set instead of creating new ones is called object {BLANK}.", [["pooling"]]),
 ("C", "Which statements about C are true?", ["An array decays to a pointer when passed to a function", "malloc allocates memory on the heap", "Memory from malloc is freed automatically", "A pointer can be NULL"], "ABD",
  f"Memory allocated with malloc must be released with {BLANK}.", [["free"]]),
 ("Microcontrollers", "Which are usually found on a microcontroller chip?", ["A CPU core", "Flash memory for the program", "RAM", "A full desktop operating system by default"], "ABC",
  f"A {BLANK} timer in a microcontroller resets the chip if the program stops responding.", [["watchdog"]]),
 ("RTOS", "Which of these are RTOS concepts?", ["Tasks", "Priorities", "Semaphores", "Hyperlinks"], "ABC",
  f"{BLANK} inversion happens when a high-priority task waits for a low-priority task that holds a lock.", [["priority"]]),
 ("Communication Protocols", "Which are serial protocols common in embedded systems?", ["I2C", "SPI", "UART", "HTML"], "ABC",
  f"I2C uses two wires: SDA for data and {BLANK} for the clock.", [["SCL"]]),
 ("Hardware Interfacing", "Which are used to connect a microcontroller to other hardware?", ["GPIO pins", "ADC inputs", "PWM outputs", "Cascading style sheets"], "ABC",
  f"An {BLANK} converts an analog sensor voltage into a digital number.", [["ADC", "analog-to-digital converter", "analog to digital converter"]]),
 ("Memory Management", "Which cause memory leaks in C or C++?", ["Allocating memory and never freeing it", "Losing the pointer to allocated memory", "Freeing memory exactly once", "Allocating in a loop without freeing"], "ABD",
  f"A pointer to memory that was already freed is called a {BLANK} pointer.", [["dangling"]]),
 ("Linux", "Which commands show a directory listing or file contents?", ["ls", "cat", "chmod", "head"], "ABD",
  f"The {BLANK} command changes the permissions of a file.", [["chmod"]]),
]


def hash_text(skill, kind, text):
    """Same recipe as PracticeBank.learn: SHA-256 of skill|type|text, stripped and lower-cased."""
    return hashlib.sha256(f"{skill}|{kind}|{text}".strip().lower().encode("utf-8")).hexdigest()


def sql(s):
    return "'" + s.replace("'", "''") + "'"


def software_skills():
    """Skills of the 7 software development roles in database-schema.md section 12."""
    text = (HERE.parents[5] / "database-schema.md").read_text(encoding="utf-8")
    skills = set()
    for row in csv.reader(re.findall(r'^"Software Development".*$', text, re.M)):
        skills.update(row[3].split("|"))
    return skills


def main():
    wanted = software_skills()
    seen, hashes, rows = set(), set(), []
    for skill, mc, options, correct, fb, accepted in Q:
        assert skill in wanted, f"{skill} is not a skill of the software development roles"
        assert skill not in seen, f"{skill} appears twice"
        seen.add(skill)
        assert len(options) == 4 and len(set(options)) == 4, f"{skill}: need 4 different options"
        assert correct and set(correct) <= set("ABCD") and len(set(correct)) == len(correct) < 4, f"{skill}: bad correct letters"
        blanks = fb.count(BLANK)
        assert blanks in (1, 2) and blanks == len(accepted) and all(accepted), f"{skill}: blanks and accepted wordings differ"
        for text in [mc, fb, *options, *[w for a in accepted for w in a]]:
            assert "\\" not in text and '"' not in text, f"{skill}: avoid quotes and backslashes: {text}"
        opts = [{"key": k, "text": t} for k, t in zip("ABCD", options)]
        rows.append((skill, "multiple_correct", mc, json.dumps(opts, ensure_ascii=False), json.dumps(list(correct)), None))
        rows.append((skill, "fill_blank", fb, None, None, json.dumps(accepted, ensure_ascii=False)))
    assert seen == wanted, f"skills without questions: {sorted(wanted - seen)}"
    for r in rows:
        h = hash_text(r[0], r[1], r[2])
        assert h not in hashes, f"duplicate question {r[2]}"
        hashes.add(h)

    with open(HERE / "bank_starter.csv", "w", encoding="utf-8", newline="") as f:
        w = csv.writer(f, quoting=csv.QUOTE_ALL)
        w.writerow(["skill", "type", "text", "options", "correct_options", "accepted_answers"])
        w.writerows([[c if c is not None else "" for c in r] for r in rows])

    lines = ["-- V3: starter practice bank for the software development category (FR-20, ticket 29).",
             "-- Generated by gen_bank.py from bank_starter.csv. General knowledge, two questions per skill: one multiple-correct, one fill-in-the-blank.",
             "-- Rows are shared by all users, marked 'seed', and hold no personal data.", ""]
    for skill, kind, text, options, correct, accepted in rows:
        n = lambda v: "NULL" if v is None else sql(v)
        lines.append("INSERT INTO bank_question (type, skill_id, difficulty, text_template, options, correct_options, accepted_answers, source, text_hash)\n"
                     f"SELECT {sql(kind)}, id, 'medium', {sql(text)}, {n(options)}, {n(correct)}, {n(accepted)}, 'seed', {sql(hash_text(skill, kind, text))}\n"
                     f"FROM skill WHERE name = {sql(skill)};")
    (HERE / "V3__starter_bank.sql").write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"{len(rows)} questions for {len(seen)} skills")


if __name__ == "__main__":
    sys.exit(main())
