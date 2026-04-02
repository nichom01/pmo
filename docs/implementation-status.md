# Implementation Status

## Completed

- Monorepo structure with all source code under `src/`
- Separated `src/ui`, `src/backend`, `src/database`, and `src/scripts`
- Local stack orchestration (`infra/docker-compose.yml`, `dev-up.sh`, `dev-down.sh`)
- Core database schema bootstrap (`src/database/init/001_schema.sql`)
- Spring Boot backend scaffold with:
  - package-by-layer structure
  - health endpoint
  - auth route shell (`/api/v1/auth/login|refresh|logout`)
  - project list/create endpoints under `/api/v1/teams/{teamId}/projects`
  - workflow state list endpoint under `/api/v1/teams/{teamId}/workflow-states`
  - issue list/create endpoints under `/api/v1/projects/{projectId}/issues`
  - issue cycle assignment endpoint under `/api/v1/issues/{issueId}/cycle`
  - cycle list/create/update endpoints (`/api/v1/projects/{projectId}/cycles`, `/api/v1/cycles/{cycleId}`)
  - active cycle enforcement (one active cycle per project)
  - cursor-based pagination envelope for issue lists (`data`, `nextCursor`, `hasMore`)
  - basic role-gated mutation checks (`X-Team-Role`, guest denied)
  - global ProblemDetail exception mapping
  - soft-delete filtering on Project (`@SQLRestriction`)
  - demo bootstrap/context endpoints for local UI wiring (`/api/v1/demo/context`)
- React UI scaffold with router, TanStack Query, Zustand store, and API wiring
- Projects route wired to backend list/create endpoints (no placeholder page)
- Comment lifecycle endpoints include list/create/update/delete support
- Attachment lifecycle endpoints include list/create/delete support
- Organisation/team read endpoints implemented (incl. org members + team listing) and `users/me` profile endpoints added
- Added `GET/PATCH/DELETE` for `projects/:projectId` and `issues/:issueId`, plus `GET /cycles/:cycleId` detail
- Mutation authorization for these resources now resolves effective roles from seeded organisation/team memberships
- Cycle completion is now exercised end-to-end in the UI + Playwright smoke test
- Notifications UX now includes unread count + mark-all-read action (UI + Playwright smoke test)
- Issue workflow movement is now covered in UI + Playwright smoke test (Backlog -> Todo)
- Unit/integration/e2e test foundations:
  - backend unit + Testcontainers integration tests
  - frontend Vitest tests
  - Playwright smoke test including cycle and assignment flow
- GitHub Actions CI and release workflow placeholders
- MCP usage guide for GitHub and Playwright

## Remaining to reach full design parity

- Implement all entities and full endpoint surface listed in `docs/system-design.md`
- Complete authorization model with org/team membership enforcement
- Implement cursor pagination envelopes on all list endpoints
- Implement comments, attachments, notifications, API keys in backend + UI
- Implement storage abstraction with local + S3 profile switching
- Expand e2e to full critical flows from design (auth, issue lifecycle, cycle completion)
