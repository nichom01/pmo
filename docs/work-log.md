# Work Log

## 2026-04-01

- 23:01:46 BST - Started slice: comments, issue activity, and notifications foundation. Running implementation + tests + commit/push workflow.
- 23:07:37 BST - Implemented backend comment/activity/notification foundations: new entities (`Comment`, `IssueActivity`, `Notification`), repositories, services, and endpoints (`/issues/:id/comments`, `/issues/:id/activity`, `/notifications`, `/notifications/:id/read`, `/notifications/read-all`).
- 23:07:37 BST - Extended issue lifecycle hooks to record activity and create notifications for issue creation, cycle assignment, and comment creation.
- 23:07:37 BST - Updated UI issues page to show notifications count, first-issue comments, and add-comment action; expanded API client types and methods.
- 23:07:37 BST - Validation run complete: backend tests passed, frontend unit tests passed, e2e passed after tightening selector to avoid strict-mode ambiguity.
- 23:08:08 BST - Committed and pushed slice to `main` (`1b39ee4`). Starting next slice: attachments + storage service foundation.
- 23:14:41 BST - Implemented attachment foundation: `Attachment` entity, repository, service, DTOs, and endpoints (`/issues/:id/attachments` GET/POST).
- 23:14:41 BST - Added storage abstraction with `StorageService`, `LocalStorageServiceImpl` (`dev` profile), and `S3StorageServiceImpl` placeholder (`prod` profile).
- 23:14:41 BST - Wired UI attachment actions/listing on issues page and added attachment integration test coverage in backend.
- 23:14:41 BST - Validation run complete: backend tests passed, frontend unit tests passed, e2e passed.
- 23:15:11 BST - Committed and pushed slice to `main` (`f44d214`). Starting next slice: API key management foundation.
- 23:19:17 BST - Implemented API key management foundation: `ApiKey` entity, repository, service, DTOs, and endpoints (`/users/me/api-keys` GET/POST/DELETE).
- 23:19:17 BST - Updated UI with API key listing count and create action; expanded client APIs and unit/e2e flow coverage.
- 23:19:17 BST - Validation run complete: backend tests passed, frontend unit tests passed, e2e passed.
