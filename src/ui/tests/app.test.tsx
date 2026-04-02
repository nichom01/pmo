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
    if (url.includes("/organisations/acme")) {
      return {
        ok: true,
        json: async () => ({ id: "org-1", name: "Acme", slug: "acme", issueSequence: 0 })
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
    if (url.includes("/teams/team-1/workflow-states")) {
      return {
        ok: true,
        json: async () => [
          { id: "wf-1", teamId: "team-1", name: "Backlog", color: "#9ca3af", type: "backlog", position: 0 },
          { id: "wf-2", teamId: "team-1", name: "Todo", color: "#60a5fa", type: "unstarted", position: 1 }
        ]
      };
    }
    if (url.includes("/teams/team-1/projects")) {
      return {
        ok: true,
        json: async () => [{ id: "p1", teamId: "team-1", name: "Project One", description: "demo", status: "active" }]
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
    if (url.includes("/issues/i1/attachments")) {
      return {
        ok: true,
        json: async () => [{ id: "a1", issueId: "i1", uploaderId: "u1", filename: "note.txt", fileUrl: "storage/note.txt", fileSize: 10, mimeType: "text/plain" }]
      };
    }
    if (url.includes("/users/me/api-keys")) {
      return {
        ok: true,
        json: async () => [{ id: "k1", userId: "u1", label: "local", createdAt: "2026-01-01T00:00:00Z" }]
      };
    }
    if (url.includes("/users/me/notification-preferences")) {
      return {
        ok: true,
        json: async () => [{ id: "p1", userId: "u1", organisationId: "o1", eventType: "issue_commented", channel: "in_app", enabled: true }]
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
    const orgName = await screen.findByTestId("org-name");
    await waitFor(() => {
      expect(orgName.textContent).toContain("Org: Acme");
    });
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
    const apiKeys = await screen.findByTestId("api-keys-count");
    await waitFor(() => {
      expect(apiKeys.textContent).toContain("API Keys: 1");
    });
    const prefs = await screen.findByTestId("notification-prefs-count");
    await waitFor(() => {
      expect(prefs.textContent).toContain("Notification Prefs: 1");
    });
  });

  it("renders projects page route", async () => {
    const client = new QueryClient();
    render(
      <QueryClientProvider client={client}>
        <MemoryRouter initialEntries={["/acme/eng/projects"]}>
          <App />
        </MemoryRouter>
      </QueryClientProvider>
    );

    expect(await screen.findByText("Projects")).toBeTruthy();
    const count = await screen.findByTestId("projects-count");
    await waitFor(() => {
      expect(count.textContent).toContain("Projects: 1");
    });
    expect(await screen.findByText("Project One")).toBeTruthy();
  });
});
