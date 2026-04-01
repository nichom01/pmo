# Work Log

## 2026-04-01

- 23:01:46 BST - Started slice: comments, issue activity, and notifications foundation. Running implementation + tests + commit/push workflow.
- 23:07:37 BST - Implemented backend comment/activity/notification foundations: new entities (`Comment`, `IssueActivity`, `Notification`), repositories, services, and endpoints (`/issues/:id/comments`, `/issues/:id/activity`, `/notifications`, `/notifications/:id/read`, `/notifications/read-all`).
- 23:07:37 BST - Extended issue lifecycle hooks to record activity and create notifications for issue creation, cycle assignment, and comment creation.
- 23:07:37 BST - Updated UI issues page to show notifications count, first-issue comments, and add-comment action; expanded API client types and methods.
- 23:07:37 BST - Validation run complete: backend tests passed, frontend unit tests passed, e2e passed after tightening selector to avoid strict-mode ambiguity.
