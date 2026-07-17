# 8-Week Project Plan — AI Assistant for the Component Traceability System

## 1. Project Overview

**Subject:** Design and develop a digital components traceability system for the manufacturing inventory workflow.

The goal of this project is to use a database to track components as they move through the production process, recording their locations, statuses, and movements over time. to improve component traceability, and visualise real-time data to support manufacturing decisions and to address a scalable manufacturing information system.

**What the System Will Do**

1. The system will be a web-based interface where users can:

   - Search for components

   - Record component movements

   - View a full movement history for traceability

2. The database will manage three main tables:
   - Components table: stores the component ID, quantity, location, status and more info if needed
   - Movement history table: records every movement a component makes
   - Status checking table: tracks the current quality of components

3. AI assistant (in 3 tiers):

   a)   Daily Summary (must be done during the program)

   - The system schedules a certain time and automatically writes a short report about what happened during the day. Focus on these core features:
     - Shortage prediction & reorder recommendations
     - Quality analysis (Damage/failure/expiry)
     - Anomaly detection
     - Daily data summary

   b)   Chat assistant (optional)
   Q&A chat box answers the questions by digging through the data, so the user without digging through the data

   c)   Operator agent (focused stage)
   An assistant that can actually perform your commands and approvals.

**Tech Stack**
• Frontend: JavaScript / React.js
• Backend: Java Spring Boot
• Database: PostgreSQL
• API: REST API
• Data Visualisation: React Dashboard / Power BI

---

## 2. Scope of This Plan

**Prerequisite (completed before this plan begins):** 

The core full-stack application demo. This plan assumes that the system is complete and running with representative data.

**In scope for these 8 weeks:** 

Design and implementation of an AI assistant, built in three escalating tiers, integrated into the existing backend.

**Overview:** 

The AI assistant is primarily backend Java/Spring integration work, with a large language model (LLM) layer on top. All deterministic computation (shortage detection, quality rollups, anomaly thresholds) is done in SQL/Java; the LLM is used to summarise, converse, and act (with human approval). This keeps results accurate and auditable.

---

## 5. Week-by-Week Plan

**Week 1:** 

Test demo and design the rule of each detector, the structure of the daily report, what the LLM sees and returns. Then prepare the data layer and design the issue case to every detector, so every detector has a known-answer test case to catch

**Week 2:**

Builda calculater of daily consumption

### 

Build the consumption-rate calculator over `movement_history` (units out per day, trailing window), project against current stock, produce structured findings: `{part, daysUntilShortage, recommendedQty}`. This is the hardest SQL of the project — window functions and date bucketing.
 **Milestone: shortage detector correctly flags the salted fast-consuming component and ignores healthy ones.**

### Week 3 — Detection engine, part 2 (anomalies + quality)

Duplicate-transfer detection (self-join within a time window), impossible-quantity and abnormal-movement rules, cycle-count mismatch detection, and quality aggregation (rejection rates by part/location/week). Unify everything into one structured `DailyFindings` object produced by a single service call.
 **Milestone: one method call returns complete, correct findings JSON from the salted data.**

### Week 4 — LLM integration: the narrator

The "rules detect, LLM narrates" seam. Learn LLM API basics (prompting, structured input, response handling), then: findings JSON → prompt → digest text → stored in `daily_reports`. Add `@Scheduled` daily trigger + manual generate endpoint. Spend real time on prompt iteration — grounding the model so it *only* narrates the findings and never invents numbers. Test: does the digest ever claim something not in the findings?
 **Milestone: generated digest is accurate, readable, and hallucination-free across multiple runs.**

### Week 5 — Daily Report page + digest hardening

React page rendering the digest with its structured findings (shortage table, anomaly list, quality highlights — not just a wall of text). Handle edge cases: empty days, LLM API failures (report generates with raw findings + "narrative unavailable"), long histories. **Priority 1 is now done and demo-ready** — everything after this is upside.
 **Milestone: end-to-end demo: seeded anomalies → scheduled job → readable report in browser.**

### Week 6 — Chat assistant: tool-calling foundations

The new concept this week is **tool calling**: the LLM choosing which of your functions to invoke. Define 4–5 read-only tools mapped to existing endpoints (search, detail, history, status checks, stock). Build the loop: user question → model picks tool → your code calls the API → result goes back to model → answer. Command-line or Postman testing only this week; no UI yet.
 **Milestone: multi-step questions ("where is X and when did it last move?") answered correctly via tools.**

### Week 7 — Chat UI + operator agent (approval-gated writes)

Chat box in React with conversation history. Then the trust upgrade: add ONE write tool — `record_movement` — behind the approval pattern: agent *proposes* a structured action, UI renders a confirm card, only on click does the backend execute, logged as `agent (requested by user)`. One write action done properly beats five done loosely.
 **Milestone: "move 50 of X to assembly" → proposal card → approve → movement recorded and visible in history.**

### Week 8 — Buffer, evaluation, presentation

The overrun absorber (tool-calling in week 6 is the likeliest overrun). If on schedule: add a second write tool (create reorder from a digest recommendation — nicely connects Priority 1 to Priority 3), write a small evaluation section (test questions vs. expected answers, digest accuracy checks — supervisors love a systematic eval), polish the demo script.
 **Milestone: full demo — digest, chat, approved action — plus documentation.**

| Week | Focus | Key tasks | Deliverable / milestone | ~Hrs |
|---|---|---|---|---|
| **1** | SDK integration | Add the Anthropic Java SDK; configure API key via env var; implement a thin service wrapper around the messages API; verify a round-trip call with error handling | Backend can make an authenticated call to the LLM and return a response | 8–10 |
| **2** | Analytics data layer (Tier 1 prep) | Define daily-summary metrics (counts by status/location, movements/day, items on quality hold, quantities below reorder threshold, scrap by failure code); implement as SQL aggregate queries/repository methods | A service returns accurate daily metrics from real data (unit-tested) | 8–10 |
| **3** | Daily summary generation (Tier 1) | Design the summarisation prompt; call the LLM with the computed metrics; store the report (new table via Flyway migration); add a scheduled daily job | A generated daily summary produced from real data on a schedule | 8–10 |
| **4** | Daily summary UI (Tier 1 complete) | Add `GET /api/reports/latest` (+ history); build a React view showing the latest report | **Tier 1 complete** — daily summary visible in the web UI | 8–10 |
| **5** | Conversational assistant — tool use (Tier 2 core) | Apply the SDK tool-use mechanism; define read-only tools (search components, get movement history) that call existing services; wire the tool-use loop | The assistant answers a data question by invoking a backend tool (verified in tests) | 8–10 |
| **6** | Chat endpoint & UI (Tier 2 complete) | Implement `POST /api/assistant/chat` with multi-turn handling; build a minimal chat interface | **Tier 2 complete** — user asks questions in the UI and receives data-grounded answers | 8–10 |
| **7** | Operator agent — write tools (Tier 3 core) | Define custom write tools wrapping the movement / status-check services; implement a propose → human-approve → execute flow | The agent proposes an action; after approval it executes via the existing transactional service | 8–10 |
| **8** | Hardening, guardrails & demo (Tier 3 complete + buffer) | Enforce that all writes go through existing transactional services and validation (no bypass); add error handling, logging, and safety checks; full end-to-end demo; absorb slippage | All three tiers demoable and documented | 8–10 |

**Total:** ~60–80 hours over 8 weeks.

---

## 7. Success Criteria (Definition of Done)

- **Tier 1:** An automated daily summary is generated from live data and is viewable in the UI.
- **Tier 2:** The assistant answers questions grounded in live data via backend tool use.
- **Tier 3:** The operator agent can propose and safely execute component actions after human approval and via the existing transactional services.
