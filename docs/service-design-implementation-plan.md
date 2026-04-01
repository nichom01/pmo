# Service Design Implementation Plan

This plan implements the requirements from `docs/system-design.md` and enforces:
- all source code in `src/`
- separate UI, backend, and database concerns
- local runnable stack via a single startup script
- unit, integration, and end-to-end tests
- MCP integration for GitHub and Playwright workflows

If a separate `service-design.md` is added later, this plan should be updated to match it exactly.

---

## 1) Scope and Non-Negotiable Constraints

1. Follow entity model, auth model, API conventions, frontend architecture, and testing strategy from `system-design.md`.
2. Place all application source code under `src/`.
3. Keep a clear separation of concerns:
   - UI application
   - backend API service
   - database runtime + schema lifecycle
4. Provide local one-command startup.
5. Provide test layers:
   - unit tests (frontend + backend)
   - integration tests (backend with PostgreSQL)
   - e2e tests (Playwright against running stack)
6. Include MCP-enabled workflows for GitHub automation and Playwright-driven browser validation.

---

## 2) Target Repository Structure

```text
.
├── src/
│   ├── ui/                        # React + Vite + TS app
│   │   ├── src/
│   │   ├── tests/
│   │   ├── playwright/            # e2e specs owned by UI package (or move to /tests/e2e)
│   │   ├── package.json
│   │   └── Dockerfile
│   ├── backend/                   # Spring Boot app
│   │   ├── src/main/java/com/yourapp/
│   │   ├── src/main/resources/
│   │   ├── src/test/java/
│   │   ├── build.gradle.kts (or pom.xml)
│   │   └── Dockerfile
│   ├── database/
│   │   ├── init/                  # seed SQL for local dev
│   │   ├── migrations/            # optional if Flyway/Liquibase adopted
│   │   └── README.md
│   └── scripts/
│       ├── dev-up.sh              # starts local stack
│       ├── dev-down.sh
│       ├── test-all.sh            # unit + integration + e2e orchestration
│       └── wait-for-services.sh
├── infra/
│   ├── docker-compose.yml
│   └── env/
│       ├── .env.example
│       ├── backend.env.example
│       └── ui.env.example
├── tests/
│   └── e2e/                       # optional central e2e location
├── .github/workflows/
│   ├── ci.yml
│   └── release.yml
└── docs/
    ├── system-design.md
    └── service-design-implementation-plan.md
```

---

## 3) Implementation Phases

## Phase 0 - Project bootstrap

Deliverables:
1. Create folder structure from Section 2.
2. Add root-level docs:
   - `README.md` with local run instructions
   - `CONTRIBUTING.md` for testing expectations
3. Add environment templates under `infra/env/`.

Acceptance criteria:
- Repository has `src/ui`, `src/backend`, `src/database`, `src/scripts`.
- Developer can understand local setup from `README.md` alone.

## Phase 1 - Database foundation

Deliverables:
1. Provision PostgreSQL via Docker Compose.
2. Encode schema for all entities in `system-design.md`:
   - Organisation, User, OrganisationMembership, Team, TeamMembership
   - Project, WorkflowState, Issue, Label, IssueLabel, Cycle
   - Comment, IssueActivity, Attachment, Notification, NotificationPreference, ApiKey
3. Enforce key constraints:
   - unique slugs/usernames/emails
   - one-level sub-issues only (app + DB checks where possible)
   - one active cycle per project (partial unique index)
4. Add seed data scripts for local development.

Acceptance criteria:
- Fresh local DB starts cleanly and can be seeded.
- Constraints from design are represented and validated.

## Phase 2 - Backend service (Spring Boot)

Deliverables:
1. Implement package structure described in design:
   - controllers, services, repositories, entities, dtos, exceptions, security, storage, config
2. Implement auth:
   - login/refresh/logout
   - JWT access + refresh tokens in httpOnly cookies
   - API key auth for `Authorization: Bearer <key>`
3. Implement authorization:
   - `PermissionService` with effective role resolution (org admin supersedes team role)
4. Implement soft-delete behavior:
   - Issue, Project, Cycle, Comment
5. Implement RFC 9457 Problem Details through global exception handling.
6. Implement cursor pagination pattern (`created_at`, `id`) for list endpoints.
7. Implement `StorageService` abstraction:
   - local filesystem profile for dev
   - S3 profile for production readiness

Acceptance criteria:
- All key `/api/v1` endpoints in design exist and return contract-compliant responses.
- Protected endpoints enforce role matrix correctly.

## Phase 3 - UI application (React + TypeScript)

Deliverables:
1. Scaffold Vite + React + TypeScript app.
2. Add architecture:
   - React Router nested layouts (`RootLayout`, `OrgLayout`, `TeamLayout`)
   - TanStack Query for server state
   - Zustand for auth/org/team/ui state
   - Tailwind for styling
3. Implement core flows:
   - auth flow (login, refresh handling)
   - issue list/detail
   - project list/detail
   - cycle list/detail
   - notifications read/read-all
4. Respect URL structure from design.
5. Add error handling UI for Problem Details payloads.

Acceptance criteria:
- UI can complete critical journeys against local backend.
- URL structure and page hierarchy match design.

## Phase 4 - Local runtime and developer scripts

Deliverables:
1. `infra/docker-compose.yml` with:
   - db service (PostgreSQL)
   - backend service
   - ui service
2. `src/scripts/dev-up.sh`:
   - validates required tooling
   - creates `.env` from templates when missing
   - starts compose stack
   - waits for health endpoints and prints URLs
3. `src/scripts/dev-down.sh` to stop/clean local runtime.
4. `src/scripts/test-all.sh`:
   - backend unit tests
   - backend integration tests (Testcontainers)
   - frontend unit tests
   - e2e tests (Playwright)

Acceptance criteria:
- One command boots full local stack.
- One command runs complete test suite.

## Phase 5 - Test pyramid implementation

Deliverables:
1. Backend unit tests:
   - service layer logic with Mockito
   - permission decisions and validation edge cases
2. Backend integration tests:
   - Testcontainers PostgreSQL
   - repository + controller integration for key endpoints
   - pagination and Problem Details assertions
3. Frontend unit tests:
   - components, hooks, route guards, data mappers
4. E2E (Playwright):
   - auth flow
   - create issue
   - move issue through workflow/cycle
   - complete cycle and validate resulting state

Acceptance criteria:
- Failing functionality is caught at appropriate test layer.
- Critical user journeys are deterministic in CI and local runs.

## Phase 6 - CI/CD and quality gates

Deliverables:
1. GitHub Actions CI:
   - backend tests
   - frontend tests
   - e2e tests
2. Enforce checks on pull requests.
3. Add artifact capture for e2e screenshots/traces on failure.
4. Add coverage reporting thresholds (initially moderate, tightened over time).

Acceptance criteria:
- No PR can merge without passing full automated validation.

---

## 4) MCP Integration Plan (GitHub + Playwright)

## MCP for GitHub

Purpose:
- automate repo operations, PR creation, issue linking, and review workflows.

Implementation tasks:
1. Ensure MCP GitHub server authentication is completed for contributors.
2. Define standard automations:
   - create PR with required template sections (summary, test plan)
   - fetch PR comments for review loops
   - update issue status when PR merges
3. Add team docs:
   - MCP usage guide with approved commands and safety constraints
   - branch/PR naming conventions tied to issue IDs

Acceptance criteria:
- Contributors can create and inspect PRs through MCP-assisted workflows with consistent formatting.

## MCP for Playwright

Purpose:
- run browser-level validation through MCP-assisted interaction and debugging workflows.

Implementation tasks:
1. Keep Playwright test suite as source-of-truth automation in repo.
2. Add MCP browser usage guidelines for:
   - local smoke checks during development
   - targeted repro/debug of failing e2e scenarios
3. Standardize trace/screenshot retention and debugging flow:
   - run scenario
   - capture trace on failure
   - replay and inspect

Acceptance criteria:
- Failing e2e tests can be reproduced and diagnosed quickly with MCP-assisted browser sessions.

---

## 5) Endpoint Delivery Priority

Implement in this order to reduce dependency risk:
1. Auth + user profile (`/auth/*`, `/users/me`)
2. Organisation/team read + membership basics
3. Projects
4. Workflow states + issues
5. Comments + issue activity
6. Cycles
7. Attachments
8. Notifications + preferences
9. API keys

---

## 6) Definition of Done (Per Feature)

A feature is complete only when all are true:
1. Backend endpoint(s) implemented with auth + permission checks.
2. DB schema/constraints support expected behavior.
3. UI path implemented and usable.
4. Unit tests added/updated.
5. Integration tests added/updated for server behavior.
6. E2E coverage exists for critical journey impact.
7. Feature works in local full-stack startup.
8. Documentation updated where behavior/config changed.

---

## 7) Risks and Mitigations

1. Role-model complexity causes authorization bugs
   - Mitigation: centralize decisions in `PermissionService`, exhaustively test role matrix.
2. Cursor pagination inconsistency across endpoints
   - Mitigation: shared pagination utility and contract tests.
3. Soft-delete leakage in queries
   - Mitigation: default repository filters + tests on every list/detail endpoint.
4. E2E flakiness from async UI/state
   - Mitigation: robust Playwright waiting patterns and test data isolation.
5. Local environment drift
   - Mitigation: enforce script-driven startup and pinned container images.

---

## 8) Suggested Execution Timeline

- Week 1: Phase 0-1 (bootstrap + DB)
- Week 2-3: Phase 2 (backend core + auth + projects/issues)
- Week 3-4: Phase 3 (UI core flows)
- Week 4: Phase 4 (local scripts + full stack compose)
- Week 5: Phase 5-6 (tests + CI hardening + MCP workflow docs)

Adjust timeline based on team size and whether endpoint scope is reduced for an MVP cut.
