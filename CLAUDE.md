# Component Traceability System

A work-in-progress (WIP) traceability system for **Cochlear Implant (CI)** manufacturing.
Tracks CIs as they move through production operations — recording quantities ready at each
operation, quantities moved on, and changes from scrap or hold — with an LLM integrated for
daily summaries, chat, and an approval-gated operator agent.

Domain terms: **part number** = a kind of CI serial; **job** = a batch of that serial;
**component** = a physical unit (component ID); **location** = a production **operation**.

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

**Movement guard rules** (enforced in the `recordMovement` service, Phase 3):
- Reject any movement that would make a component's quantity negative (stop + warn).
- Reject duplicate movements.
- Non-conforming components (scrapped / on hold) cannot move further through the flow —
  they can only be scrapped; their full record is always kept (never hard-deleted).
- Scrap subtracts quantity but preserves the component's complete history.

**Terminology:** a `location` represents a production **operation**.

**Planned, not built yet:** a `production_plan` / target table to power the "unmet
production plans" and serial-prioritisation detectors in the daily summary. Kept in the
design as future scope — the other detectors (quality, anomaly) work without it.

---

## Database — eight tables (Part → Job → Component chain + lookups + history)

Core model: a **Part** is the definition of an item; a **Job** is a batch/lot of that
part with an expiry; a **Component** is a physical split of a job that moves around.
Chain: `parts` → `jobs` → `components`.

**Definition / catalog:**
- `parts` (part_id PK, part_number NOT NULL UNIQUE e.g. `P12345`, name NOT NULL,
  unit NOT NULL e.g. `ml`/`box`/`each`)
- `jobs` (job_id PK, job_number NOT NULL UNIQUE e.g. `silicon123`,
  part_id FK→parts NOT NULL, expiry_date DATE NOT NULL,
  is_deleted BOOLEAN NOT NULL DEFAULT false) — a lot/batch of a part. Soft-delete only
  (row stays so a deleted job_number can never be reused).

**Lookup tables** (small, referenced by ID):
- `locations` (location_id PK, name NOT NULL UNIQUE, zone)
- `operators` (operator_id PK, full_name NOT NULL, employee_no UNIQUE,
  role NOT NULL DEFAULT `OPERATOR` — `OPERATOR`/`LEADER`, created_at)
  — the users who record movements and quality checks; role gates leader-only actions
- `failure_codes` (failure_code_id PK, code NOT NULL UNIQUE e.g. `DAMAGED`/`EXPIRED`,
  description) — reasons a component is scrapped

**Current state:**
- `components` (component_id PK, job_id FK→jobs NOT NULL,
  split_code NOT NULL UNIQUE e.g. `silicon123.001`, quantity NOT NULL CHECK ≥ 0,
  location_id FK→locations NOT NULL, status NOT NULL, created_at, updated_at)

**Append-only history:**
- `movement_history` (movement_id PK, component_id FK→components NOT NULL,
  from_location_id FK→locations NULLABLE, to_location_id FK→locations NOT NULL,
  quantity_moved NOT NULL CHECK > 0, moved_by FK→operators, moved_at, notes)
- `status_checks` (check_id PK, component_id FK→components NOT NULL, status NOT NULL,
  failure_code_id FK→failure_codes NULLABLE — set only when status = REJECTED,
  quantity NULLABLE CHECK > 0 — units scrapped, set on a scrap/rejection,
  checked_by FK→operators, checked_at, notes)

**Relationships:**
- A `component` is a split of one `job`; a `job` is a lot of one `part`. Expiry lives on
  the job (whole lot expires together); a component's part/name comes via its job.
- Every `component` sits at one `location`.
- Each `movement_history` row = one component moving between locations, by one operator.
- Each `status_checks` row = one quality assessment by one operator; a rejection points
  at the `failure_code` explaining why.
- "Quantity scrapped under each failure code" = sum over `status_checks` joined to
  `failure_codes`, grouped by code.

**Design notes:**
- Part → Job → Component normalizes "what it is" (part), "which lot" (job), "which
  physical split" (component). `split_code` like `silicon123.001` identifies a component.
- `moved_by` / `checked_by` are FKs to `operators` (normalized "who", like locations).
- A scrap is a `status_checks` row with status = REJECTED and a `failure_code_id` — no
  separate scrap table.

Creation order (FK dependencies): parts → jobs → locations → operators → failure_codes →
components → movement_history → status_checks.

Indexes: `idx_jobs_part (part_id)`, `idx_components_job (job_id)`,
`idx_movement_component (component_id, moved_at DESC)`,
`idx_status_component (component_id, checked_at DESC)`,
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
- Phase 8 — AI foundations (warm-up: get ready for LLM/agent development)
- Phase 9 — AI assistant (optional, after core app works)

Current position: Phase 3 (backend). Phases 0–2 done (repo, Dockerized Postgres 16,
8-table schema applied & verified). Working through JPA entities one per table —
`Location` done and committed; next up is `Part`.

---

## Phase 8 — AI foundations (get ready for AI agent development)

A short warm-up (~a few days) done right after the core app works and **before** building
the AI assistant, so the SDK / tool-use / agent concepts read as concrete steps instead of
jargon. Same just-in-time principle as the rest of the project — learn it right before
using it. I have zero LLM background, so this builds the mental model first.

1. **Read one high-level explainer on how LLMs work** — the mental model, not the maths;
   what an LLM call is and isn't.
2. **First raw LLM call from Postman** — get an API key, no code, just watch the
   request/response JSON go in and out, exactly like testing a REST endpoint.
3. **Same call from a tiny standalone Java program** — confirm it works from Java before
   touching the backend.
4. **Read the "tool use" API page and do one tool-calling round trip by hand** — the model
   requests a tool, I return a result. This is the basis of the chat assistant and operator
   agent (Tiers 2 and 3).

Outcome: every task in Phase 9 (and in `PLAN.md`) reads as concrete instructions.

---

## Phase 9 — AI assistant (optional, after core app works)

Uses the official Anthropic Java SDK (`com.anthropic:anthropic-java`); default model
`claude-opus-4-8`, API key from the `ANTHROPIC_API_KEY` env var (never hardcoded, never
committed). This is a **paid API** — budget for that. Build in three escalating tiers:

1. **Daily summary** — a `@Scheduled` backend job runs aggregate SQL (counts by
   status/location, movements/day, items on QUALITY_HOLD, quantities below a reorder
   threshold), passes those computed numbers to a single Claude call, and stores the
   generated report. *Deterministic detection (shortages, anomalies, quality rollups)
   is done in SQL/Java; Claude only writes the narrative.*
2. **Chat assistant (optional)** — `POST /api/assistant/chat`. Claude answers questions
   using **tool use**: expose read-only tools (search components, get movement history)
   so answers come from live data, not guesses.
3. **Operator agent (stretch)** — Claude with **custom tools** wrapping the existing
   transactional services (record movement, record status check). Every state-changing
   action is gated behind explicit human confirmation before it runs — the model
   proposes, the operator approves.

Guardrails: keep all writes behind the same `@Transactional` services and validation the
REST API already uses; the agent gets no privileged path. Read tiers (1, 2) before write
tier (3).
