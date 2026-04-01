# Development Workflow

This project uses an iterative delivery loop for implementation slices.

## Standard Loop (Required)

For each slice of work:

1. Implement the slice.
2. Validate with tests:
   - backend tests
   - frontend unit tests
   - e2e tests
3. Update `docs/work-log.md` with timestamped entries:
   - what was implemented
   - validation outcomes
   - commit reference after push
4. Commit with a clear message focused on intent.
5. Push to `main`.
6. Continue to the next slice.

## Logging Requirements

- Every completed slice must have timestamped entries in `docs/work-log.md`.
- Timestamps should use local project time format, for example:
  - `YYYY-MM-DD HH:MM:SS TZ`
- Include important execution notes (for example, test flags used for stability).

## Source Layout Constraints

- All source code must live under `src/`.
- UI, backend, and database concerns remain separated:
  - `src/ui`
  - `src/backend`
  - `src/database`

## Local Validation Baseline

Before considering a slice complete:

- Local stack boots (`src/scripts/dev-up.sh`).
- Relevant tests pass.
- Work log is updated.
- Changes are committed and pushed.
