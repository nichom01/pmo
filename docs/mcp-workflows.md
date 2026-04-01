# MCP Workflows

## GitHub MCP

Use the GitHub MCP server for:
- creating pull requests with a consistent summary + test plan body
- reading and triaging PR comments
- linking implementation work to issues

Recommended workflow:
1. Create branch from issue identifier.
2. Run local tests via `bash src/scripts/test-all.sh`.
3. Use MCP GitHub tooling to open PR with:
   - Summary
   - Test plan
4. Use MCP GitHub tooling to fetch review comments and apply fixes.

## Playwright MCP

Use Playwright MCP and repository Playwright tests together:
- repository tests are source-of-truth automation (`src/ui/playwright`)
- MCP browser tooling supports interactive repro/debug of failures

Recommended workflow:
1. Run `npm run test:e2e` in `src/ui`.
2. If failures occur, reproduce scenario with MCP browser session.
3. Capture traces/screenshots and patch selectors or waits.
