# Component Traceability System

A digital traceability system for a manufacturing workflow. Tracks components as they
move through production — recording their locations, statuses, and full movement
history over time — with an optional analytics dashboard for manufacturing decisions.

This is a first-time learning project. The point is to build the skills, not just ship
the artifact. Read the working mode below before doing anything.

---

## WORKING MODE — guided learning (applies Phase 0 through Phase 7)

**This rule is in force for the entire project, every phase, every session.**

I (the developer) write the code and set up the environment myself. Claude's role is
to guide, explain, and review — NOT to build.

- Give me step instructions and the reasoning, then let me do the step.
- After I do something, review it: what's correct, what's wrong, and why.
- When I'm stuck, explain the concept or diagnose the error. Do not fix it by writing
  the whole thing for me.
- Only write code directly when I explicitly say "write this for me" for a specific
  piece. The default is: I type, Claude explains and reviews.
- Favor explaining an annotation, a SQL clause, or a hook over producing finished
  boilerplate.

There is a separate foundation plan (Phases 0–2 scaffolding). It is a REFERENCE ONLY.
Do not execute it. I am hand-building every step; use it to check my work when asked.

---

## Tech stack

- Frontend: React (Vite), React Router, Axios
- Backend: Java 21, Spring Boot 3.x (Maven)
- Database: PostgreSQL 16
- API: REST
- Data viz (optional): React Recharts dashboard, and/or Power BI direct to Postgres

## Environment (this machine)

- Mac, Apple Silicon (M4)
- JDK 21 Temurin (Homebrew)
- Node via Homebrew
- PostgreSQL via Docker (docker-compose.yml — postgres:16, db `traceability`,
  port 5432:5432, named volume for persistence)
- VS Code, IntelliJ IDEA Community, DBeaver (connects to the Dockerized Postgres
  on localhost:5432), Git, Docker Desktop

---

## Core user stories (Phase 0)

- As an operator, I can search for a component by ID / part number and see its current
  location and status.
- As an operator, I can record that a component moved from one location to another.
- As a quality engineer, I can view a component's full movement and status history in
  chronological order.

## Key design decisions (locked)

- **Movement history is append-only.** Never update or delete history rows — that's the
  basis of traceability. `components` holds current state; `movement_history` holds the
  audit trail.
- **A move is one atomic transaction:** insert a history row AND update the component's
  current location/quantity together, so they can never disagree. (`@Transactional`.)
- **Locations are normalized** into their own table and referenced by ID — no free-text
  location strings.
- **Status vocabulary (enum):** IN_STOCK, IN_TRANSIT, IN_PRODUCTION, QUALITY_HOLD,
  PASSED, REJECTED, SHIPPED.
- Flyway owns the schema; JPA `ddl-auto: validate`.

---

## Database — six tables: three lookups, one current-state, two append-only

**Lookup tables** (small, referenced by ID):
- `locations` (location_id PK, name NOT NULL UNIQUE, zone)
- `operators` (operator_id PK, full_name NOT NULL, employee_no UNIQUE, created_at)
  — the users who record movements and quality checks
- `failure_codes` (failure_code_id PK, code NOT NULL UNIQUE e.g. `DAMAGED`/`EXPIRED`,
  description) — reasons a component is scrapped

**Current state:**
- `components` (component_id PK, part_number NOT NULL, name NOT NULL,
  quantity NOT NULL CHECK ≥ 0, location_id FK→locations NOT NULL, status NOT NULL,
  created_at, updated_at)

**Append-only history:**
- `movement_history` (movement_id PK, component_id FK→components NOT NULL,
  from_location_id FK→locations NULLABLE, to_location_id FK→locations NOT NULL,
  quantity_moved NOT NULL CHECK > 0, moved_by FK→operators, moved_at, notes)
- `status_checks` (check_id PK, component_id FK→components NOT NULL, status NOT NULL,
  failure_code_id FK→failure_codes NULLABLE — set only when status = REJECTED,
  checked_by FK→operators, checked_at, notes)

**Relationships:**
- Every `component` sits at one `location`.
- Each `movement_history` row = one component moving between locations, by one operator.
- Each `status_checks` row = one quality assessment by one operator; a rejection points
  at the `failure_code` explaining why.
- "Quantity scrapped under each failure code" = sum over `status_checks` joined to
  `failure_codes`, grouped by code.

**Design notes:**
- `moved_by` / `checked_by` are FKs to `operators` (normalized "who", like locations).
- A scrap is a `status_checks` row with status = REJECTED and a `failure_code_id` — no
  separate scrap table.

Indexes: `idx_movement_component (component_id, moved_at DESC)`,
`idx_status_component (component_id, checked_at DESC)`,
`idx_components_part (part_number)`,
`idx_status_failure (failure_code_id)`.

## REST API (target shape)

| Method | Path                                | Purpose                          |
|--------|-------------------------------------|----------------------------------|
| GET    | /api/components?search=&status=&location=&page= | search + filter, paginated |
| GET    | /api/components/{id}                 | one component, current state     |
| POST   | /api/components                      | register a component             |
| PUT    | /api/components/{id}                 | edit component details           |
| POST   | /api/movements                      | record a movement (transactional)|
| GET    | /api/components/{id}/movements       | full movement history            |
| POST   | /api/status-checks                  | record a quality check           |
| GET    | /api/components/{id}/status-checks   | status history                   |
| GET    | /api/locations                      | list locations (for dropdowns)   |
| GET    | /api/analytics/summary              | counts by status/location        |

## Backend layering

`entity` → `repository` → `dto` → `service` → `controller`, plus `exception` and
`config`. Controllers stay thin (no logic). DTOs never expose entities directly.
Movement logic is `@Transactional`. Errors via `@RestControllerAdvice`.

## Frontend structure

All API calls go through a single `src/api.js` module. Four pages: Search,
Detail/timeline, Record Movement, Register + Status Check forms. State with
`useState`/`useEffect` only — no Redux.

---

## Phase roadmap

- Phase 0 — requirements & design (done: this file)
- Phase 1 — environment setup (in progress, hand-built)
- Phase 2 — database schema (V1__init.sql, hand-written and applied)
- Phase 3 — Spring Boot backend, vertical slice at a time (I write, Claude reviews)
- Phase 4 — React frontend, page by page
- Phase 5 — integration & hardening, seed data
- Phase 6 — analytics dashboard (optional)
- Phase 7 — testing & deployment

Current position: Phase 1 (environment). Next: write docker-compose.yml by hand,
bring up Postgres in Docker, verify with `docker exec trace-db pg_isready`, then
Phase 2 schema (V1__init.sql).
