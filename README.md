# MCA Study Tracker

A JavaFX desktop application for tracking MCA coursework — subjects, assignments, and class/division-scoped notes — built with raw JDBC against a local SQLite database, with no Spring or ORM involved.

This project was built specifically to solve a real problem (keeping track of subjects, deadlines, and notes across a semester) while learning JavaFX GUI development and raw JDBC from the ground up, without Hibernate or Spring Data doing the database work invisibly.

## What This Demonstrates

- A real event-driven desktop GUI (JavaFX) — layouts, forms, dropdowns, date pickers, tabs, and click-handler callbacks
- Raw JDBC — hand-written SQL, `PreparedStatement` parameter binding, `ResultSet` row-to-object mapping, try-with-resources for connection safety — with no ORM abstracting any of it away
- A DAO (Data Access Object) layer, structured the same way a Spring Data repository layer is, just implemented by hand
- Foreign-key relationships (Assignment → Subject, Note → Subject) resolved manually via name-to-ID lookups, since there's no framework doing that translation
- A simple login/identity flow (`Student`) used to scope data access — notes are filtered so each logged-in student only sees notes matching their own class and division
- A splash screen using `PauseTransition`, and a custom CSS stylesheet for theming

## Tech Stack

- Java 17
- JavaFX 21 (Controls + FXML libraries, though FXML itself isn't used — UI is built programmatically)
- SQLite via the `sqlite-jdbc` driver (plain JDBC, no JPA/Hibernate)
- Maven (with the `javafx-maven-plugin` for running/building)
- No Spring anywhere in this project — everything is deliberately hand-wired

## Prerequisites

- JDK 17+
- Maven 3.8+

No database server to install — SQLite is just a file, created automatically on first run.

## Setup & Running

1. Clone/open the project — no separate database setup needed.
2. Run:
```bash
   mvn clean compile
   mvn javafx:run
```
3. A `study_tracker.db` file will be created automatically in the project root the first time the app runs (via `DatabaseManager.initializeTables()`).

## App Flow
Splash screen (2s, auto-transitions)
↓
Login screen — enter name, class, division
(matches an existing student, or creates a new one)
↓
Main app — three tabs: Subjects | Assignments | Notes

There's no password or real authentication — login here means "identify yourself" (name + class + division), not "prove who you are." See **Known Limitations** below for what this does and doesn't protect against.

## Features

### Subjects tab
- Add a subject (name + instructor)
- View all subjects in a list
- Validated (blank fields rejected), with success/error alerts

### Assignments tab
- Add an assignment: title, due date, priority (LOW/MEDIUM/HIGH), status (NOT_STARTED/IN_PROGRESS/COMPLETED), and which subject it belongs to (dropdown, resolved internally to the subject's real ID)
- View all assignments in a list

### Notes tab
- Add a note: title, content, and which subject it's about
- **Notes are automatically tagged with the logged-in student's class and division** — there's no field for the student to set this themselves; it's pulled directly from their login session
- The notes list only ever shows notes matching the logged-in student's own class + division — a student logged in as "TY / A" will never see notes tagged "SY / B", even though they're all stored in the same database file

## Project Structure

| Package | Contents |
|---|---|
| `model/` | `Subject`, `Assignment`, `Note`, `Student` (plain Java classes, no ORM annotations), `Priority` and `AssignmentStatus` (enums) |
| `db/` | `DatabaseManager` — connection setup and `CREATE TABLE IF NOT EXISTS` schema definitions |
| `dao/` | `SubjectDao`, `AssignmentDao`, `NoteDao`, `StudentDao` — hand-written JDBC CRUD for each entity |
| `MainApp.java` | Application entry point; builds the splash screen, login screen, and the main tabbed UI, all in one class |
| `resources/styles.css` | Dark-themed stylesheet applied to every screen |

## Database Schema

Four tables, created automatically on first launch:

- **`subjects`** — `id, name, instructor`
- **`assignments`** — `id, title, due_date, priority, status, subject_id` (FK → subjects)
- **`students`** — `id, name, class_name, division`
- **`notes`** — `id, title, content, class_name, division, subject_id` (FK → subjects)

Note: `class_name` is used instead of `class` throughout, since `class` is a reserved word in both SQL and Java.

## Known Limitations

- **Local only — not multi-device or networked.** This is a single-file SQLite database living on whichever machine runs the app. Two people running this app on two separate computers have two entirely separate, disconnected databases — there is no server, no sync, and no way for one student's notes to reach another student's machine. The class/division filtering is real and enforced in code, but it only applies *within one shared installation* (e.g. multiple students using the same lab computer) — it is not a substitute for an actual client-server system.
- **No real authentication.** "Login" is just typing a name/class/division — there's no password, and nothing stops someone from typing any name and seeing/adding notes for any class/division they claim. This is an identity-selection screen, not a security boundary.
- **No edit or delete** for any entity — Subjects, Assignments, Notes, and Students can only be created and listed, never updated or removed through the UI.
- **No study session timer or "what's due soon" dashboard** — these were part of the original project scope but weren't built in this pass.
- **No automated tests.**

## Possible Future Directions

- Turning this into a genuine multi-user system would require a real backend (e.g. a Spring Boot REST API backed by a hosted database) with this JavaFX app's DAOs replaced by HTTP calls — a substantially different architecture, not an extension of the current local-file approach
- Edit/delete support for all four entities
- The originally-planned study session timer and an overdue-assignments dashboard
- Real authentication if this were ever to run somewhere less trusted than a single personal machine