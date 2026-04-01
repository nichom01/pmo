import { render, screen, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { MemoryRouter } from "react-router-dom";
import { App } from "../src/App";

vi.stubGlobal(
  "fetch",
  vi.fn(async (input: RequestInfo | URL) => {
    const url = String(input);
    if (url.includes("/health")) {
      return { ok: true, json: async () => ({ status: "ok" }) };
    }
    if (url.includes("/demo/context")) {
      return {
        ok: true,
        json: async () => ({
          orgSlug: "acme",
          teamId: "team-1",
          teamIdentifier: "eng",
          projectId: "project-1",
          workflowStateId: "wf-1"
        })
      };
    }
    if (url.includes("/projects/project-1/issues")) {
      return {
        ok: true,
        json: async () => ({
          data: [{ id: "i1", sequenceNumber: 1, title: "Example", projectId: "project-1", teamId: "team-1", workflowStateId: "wf-1", cycleId: null, description: null, priority: "none", createdAt: "2026-01-01T00:00:00Z" }],
          nextCursor: null,
          hasMore: false
        })
      };
    }
    if (url.includes("/projects/project-1/cycles")) {
      return {
        ok: true,
        json: async () => [{ id: "c1", projectId: "project-1", name: "Cycle 1", description: null, status: "draft", startDate: "2026-01-01", endDate: "2026-01-08" }]
      };
    }
    if (url.includes("/issues/i1/comments")) {
      return {
        ok: true,
        json: async () => [{ id: "cm1", issueId: "i1", authorId: "u1", body: "Comment 1", createdAt: "2026-01-01T00:00:00Z" }]
      };
    }
    if (url.includes("/notifications")) {
      return {
        ok: true,
        json: async () => [{ id: "n1", recipientId: "u1", actorId: "u1", issueId: "i1", type: "issue_commented", readAt: null, createdAt: "2026-01-01T00:00:00Z" }]
      };
    }
    return { ok: true, json: async () => ({}) };
  }) as unknown as typeof fetch
);

describe("App", () => {
  it("renders issues page route", async () => {
    const client = new QueryClient();
    render(
      <QueryClientProvider client={client}>
        <MemoryRouter initialEntries={["/acme/eng/issues"]}>
          <App />
        </MemoryRouter>
      </QueryClientProvider>
    );

    expect(await screen.findByText("Project Management UI")).toBeTruthy();
    const count = await screen.findByTestId("issues-count");
    await waitFor(() => {
      expect(count.textContent).toContain("Issues: 1");
    });
    const cycles = await screen.findByTestId("cycles-count");
    await waitFor(() => {
      expect(cycles.textContent).toContain("Cycles: 1");
    });
    const notifications = await screen.findByTestId("notifications-count");
    await waitFor(() => {
      expect(notifications.textContent).toContain("Notifications: 1");
    });
  });
});
