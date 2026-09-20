# Component-Traceability

A work-in-progress (WIP) traceability system for **Cochlear Implant (CI)** manufacturing.
It tracks CIs as they move through the production process — recording the quantity ready at
each operation, the quantity moved on, and any changes from scrap or hold — and visualises
the data in real time. LLMs are integrated to summarise activity, answer questions, and
(with approval) carry out operator actions.

Domain terms: a **part number** is a kind of CI serial; a **job** is a batch of that serial;
a **component** is a physical unit; a **location** is a production **operation**.

## The Problem

Many production areas still rely on manual records and spreadsheets, which lead to:

1. On-hand quantity not matching system quantity (from scrap or hold), forcing physical recounts.
2. Movement errors — negative or duplicate movements.
3. Non-conforming components still being moved through the flow.
4. No daily summary or visibility into priority — status is checked manually.

## What the System Does (Aims)

1. **Accurate movement history** — an append-only record of every quantity/location change,
   so stock stays correct and manual cycle counts are minimised.
2. **Movement guard rules** — stop and warn on a movement that would make quantity negative,
   and reject duplicate movements.
3. **Non-conforming control** — block scrapped / on-hold components from moving further; they
   can only be scrapped, and their full record is always kept (never hard-deleted).
4. **AI daily summary** — an assistant that summarises the day's events (see *Use of AI*).

Core user actions (web UI): search for a component, record a movement, and view a
component's full movement and status-check history in chronological order.

## Data Model — eight tables

Core chain **Part → Job → Component**: a part is *what it is*, a job is *which batch*, a
component is *which physical split* that moves around.

- **Definition:** `parts`, `jobs` (batch of a part, with expiry; soft-delete only)
- **Lookups:** `locations` (operations), `operators` (who performs actions), `failure_codes`
- **Current state:** `components` (job, split code, quantity, location, status)
- **Append-only history:** `movement_history`, `status_checks`

Movement history is never edited or deleted — that is the basis of traceability. A move is
one atomic transaction: the history row and the component's new location/quantity are written
together so they can never disagree.

## Tech Stack

- **Frontend:** React (Vite), React Router, Axios
- **Backend:** Java 21, Spring Boot (Maven)
- **Database:** PostgreSQL 16 (Docker), Flyway migrations
- **API:** REST
- **Data visualisation:** React dashboard and/or Power BI (optional)
- **AI:** Anthropic Java SDK (`claude-opus-4-8`)

## Use of AI

Built in three escalating tiers. Deterministic detection (shortages, quality rollups,
anomalies) is done in SQL/Java; the LLM only narrates verified numbers.

1. **Daily summary** — a scheduled job runs aggregate SQL and passes the computed numbers to
   a single Claude call, which writes a short report (quality, anomalies; production-plan and
   serial-prioritisation detectors are planned future scope).
2. **Chat assistant** — a Q&A chat box answering from live data via read-only tool use.
3. **Operator agent** — carries out commands through the existing transactional services,
   with every state-changing action gated behind explicit human approval.

## Status

Learning project, hand-built. Phases 0–2 complete (repo, Dockerised Postgres 16, 8-table
schema applied and verified). Currently in **Phase 3 (backend)** — JPA entity layer complete;
building repositories → services → controllers next. See `PLAN.md` for the schedule and
`CLAUDE.md` for the full design.
