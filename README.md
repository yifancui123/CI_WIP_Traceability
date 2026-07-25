# Cochlear Implant WIP Traceability System

A work-in-progress (WIP) traceability system for **Cochlear Implant (CI)** manufacturing.
It tracks CIs as they move through production operations — recording the quantity ready at
each operation, the quantity moved to the next, and any changes from scrap or hold — and
integrates a large language model (LLM) for AI-assisted reporting.

## Problem

Many production areas still rely on manual records and spreadsheets, which cause:

- On-hand quantity not matching system quantity (from scrap or hold), forcing manual recounts.
- Movement errors such as negative or duplicate movements.
- Non-conforming components still moving through the flow.
- No daily summary or visibility into priority.

## What the System Does

- **Web interface** to search CIs, record movements, and view full movement history —
  presented as an Excel-like table of the data.
- **Movement rules** that reject negative and duplicate movements and stop non-conforming
  components from continuing (while keeping their full record).
- **Append-only movement history** as the basis of traceability.
- **AI assistant (3 tiers):** an automated daily summary, a Q&A chat assistant, and an
  approval-gated operator agent.

## Data Model — Part → Job → Component

- **Part number** — a kind of CI serial.
- **Job** — a batch of that serial (unique job number).
- **Component** — a physical unit (component ID) with a current location (operation),
  quantity, and status; a failure code marks non-conforming units. The performing operator
  is recorded on each movement and quality check, not on the component itself.

## Tech Stack

- Frontend: JavaScript / React.js
- Backend: Java / Spring Boot
- Database: PostgreSQL
- API: REST API
- Data visualisation: React dashboard or Power BI
- AI: LLM API (Anthropic Java SDK)
