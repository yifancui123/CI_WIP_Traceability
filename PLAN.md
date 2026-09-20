# 8-Week Project Plan — AI Assistant for the CI WIP Traceability System

## 1. Project Overview

**Subject:** A work-in-progress (WIP) traceability system for **Cochlear Implant (CI)**
manufacturing, with an AI assistant layered on top.

The system tracks CIs as they move through production operations — recording the quantity
ready at each operation, the quantity moved on, and any changes from scrap or hold — using an
append-only movement history as the basis of traceability. Domain terms: a **part number** is
a kind of CI serial; a **job** is a batch of that serial; a **component** is a physical unit;
a **location** is a production **operation**.

**What the system does (core app — the prerequisite for this plan):**

1. A web interface where operators can **search** for a component, **record a movement**, and
   **view a component's full movement and status-check history** in chronological order.
2. A PostgreSQL database of **eight tables** — the Part → Job → Component chain, the lookup
   tables (`locations`, `operators`, `failure_codes`), current state (`components`), and the
   append-only history (`movement_history`, `status_checks`).
3. **Movement guard rules** in the transactional service layer: reject negative-quantity
   moves, reject duplicate moves, and block non-conforming (scrapped / on-hold) components
   from moving on — they can only be scrapped, and their full record is always kept.

**AI assistant (this plan — built in three tiers):**

- **a) Daily summary (must-do):** a scheduled job writes a short daily report covering
  quality analysis, anomaly detection, and consumption/shortage. Unmet-production-plan and
  serial-prioritisation detectors are **future scope** — they need a `production_plan` /
  target table that is not part of the core schema yet. *Fixed rules in SQL/Java do the
  detection; the LLM only narrates the verified numbers.*
- **b) Chat assistant (optional):** a Q&A chat box that answers from live data via read-only
  tool use, so users don't have to dig through tables themselves.
- **c) Operator agent (stretch):** an assistant that carries out commands through the existing
  transactional services, with every state-changing action gated behind human approval.

**Tech stack:** React (Vite) frontend · Java 21 / Spring Boot backend · PostgreSQL 16 (Docker,
Flyway) · REST API · React dashboard / Power BI (optional) · Anthropic Java SDK
(`claude-opus-4-8`) for the AI features.

---

## 2. Scope of This Plan

**Prerequisite (must be complete before Week 1):** the core full-stack app is built, running,
and seeded with representative data. This plan assumes the entity/repository/service/controller
backend and the React frontend already work.

> **Current reality check (2026-09):** the core app is **not yet finished**. Position:
> Phases 0–2 done (repo, Dockerised Postgres 16, 8-table schema applied); Phase 3 backend in
> progress — JPA **entity layer complete**, repositories/services/controllers still to write;
> React frontend (Phase 4) not started. This 8-week AI plan therefore starts **after** the core
> app is done (target: core complete ~Feb 2027, then run this plan).

**In scope for these 8 weeks:** design and implementation of the AI assistant, in three
escalating tiers, integrated into the existing backend.

**Overview:** the assistant is primarily backend Java/Spring integration work with an LLM layer
on top. All deterministic computation (shortage detection, quality rollups, anomaly thresholds)
is done in SQL/Java; the LLM is used to summarise, converse, and act (with human approval). This
keeps results accurate and auditable.

---

## 3. Guiding Principle — "rules detect, the LLM narrates"

The single most important design seam: **the database and Java compute all numbers; the LLM
never calculates and never invents figures — it only turns verified findings into readable
text, answers, or proposed actions.** Every detector gets a known-answer ("salted") test case
so its output can be checked before the LLM ever sees it. This is what makes the AI features
accurate and auditable rather than a black box.

---

## 4. Week-by-Week Plan

| Week | Focus | Key tasks | Deliverable / milestone | ~Hrs |
|---|---|---|---|---|
| **1** | SDK setup + detector design | Add the Anthropic Java SDK; API key via env var; thin service wrapper around the Messages API; verify an authenticated round-trip with error handling. In parallel, design each detector's rule, the daily-report structure, and what the LLM sees/returns; build salted test data (one known-answer case per detector). | Backend makes an authenticated LLM call; every detector has a known-answer test case | 8–10 |
| **2** | Consumption / shortage detector | Build the consumption-rate calculator over `movement_history` (units out per day, trailing window), project against current stock, output `{part, daysUntilShortage, recommendedQty}`. The hardest SQL of the project — window functions and date bucketing. | Shortage detector flags the fast-consuming salted component and ignores healthy ones | 8–10 |
| **3** | Anomaly + quality detectors → one findings object | Duplicate-transfer detection (self-join in a time window), impossible/abnormal-quantity rules, cycle-count mismatch, and quality aggregation (rejection rates by part/location/week). Unify all detectors into one structured `DailyFindings` object from a single service call. | One method call returns complete, correct findings JSON from the salted data | 8–10 |
| **4** | LLM narrator (Tier 1 generation) | The "rules detect, LLM narrates" seam: findings JSON → prompt → digest text → stored in a new `daily_reports` table (Flyway migration). Add a `@Scheduled` daily trigger + a manual generate endpoint. Iterate on prompt grounding so the model only narrates the findings. | Generated digest is accurate, readable, and hallucination-free across runs | 8–10 |
| **5** | Daily-report UI → **Tier 1 complete** | `GET /api/reports/latest` (+ history); React page rendering the digest with its structured findings (shortage table, anomaly list, quality highlights). Handle edge cases: empty days, LLM API failure (fall back to raw findings + "narrative unavailable"). | **Tier 1 done & demo-ready:** seeded anomalies → scheduled job → readable report in browser | 8–10 |
| **6** | Chat assistant — tool-use foundations | The new concept: **tool calling**. Define 4–5 read-only tools mapped to existing services (search, detail, movement history, status checks, stock). Build the loop: question → model picks tool → your code calls the service → result back to model → answer. CLI/Postman testing only, no UI yet. | Multi-step questions ("where is X and when did it last move?") answered via tools | 8–10 |
| **7** | Chat UI + operator agent (Tier 2 done, Tier 3 core) | `POST /api/assistant/chat` with multi-turn history + a minimal React chat box (**Tier 2 complete**). Then add ONE write tool — `record_movement` — behind the approval pattern: agent *proposes* a structured action, UI renders a confirm card, backend executes only on click, logged as `agent (requested by user)`. | "Move 50 of X to assembly" → proposal card → approve → movement recorded and visible in history | 8–10 |
| **8** | Hardening, guardrails, evaluation, demo | Buffer for overruns (tool calling in Week 6 is the likeliest). Enforce that every write goes through the existing `@Transactional` services and validation (no bypass); add error handling, logging, safety checks. If on schedule: a second write tool (create reorder from a digest recommendation) + a small evaluation section (test questions vs. expected answers, digest-accuracy checks). Polish the demo. | **All three tiers demoable and documented** | 8–10 |

**Total:** ~60–80 hours over 8 weeks. Tiers 1 + 2 complete is already a success; Tier 3 is the
stretch.

---

## 5. Success Criteria (Definition of Done)

- **Tier 1:** an automated daily summary is generated from live data and viewable in the UI.
- **Tier 2:** the assistant answers questions grounded in live data via backend tool use.
- **Tier 3:** the operator agent proposes and — after human approval — safely executes
  component actions through the existing transactional services.

**Guardrail (all tiers):** all writes go through the same `@Transactional` services and
validation the REST API already uses; the agent gets no privileged path. Read tiers (1, 2)
before the write tier (3).
