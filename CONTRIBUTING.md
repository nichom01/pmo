# Contributing

## Engineering rules

- Keep all source code under `src/`.
- Keep UI, backend, and database concerns separate.
- Add tests at the right layer for every behavior change.

## Required checks before PR

1. `bash src/scripts/test-all.sh`
2. Verify stack boots with `bash src/scripts/dev-up.sh`
3. Ensure docs are updated for behavior or API changes.
