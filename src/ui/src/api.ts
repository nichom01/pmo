export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api/v1";

export type DemoContext = {
  orgSlug: string;
  teamId: string;
  teamIdentifier: string;
  projectId: string;
  workflowStateId: string | null;
  cycleId?: string | null;
};

export type PaginatedResponse<T> = {
  data: T[];
  nextCursor: string | null;
  hasMore: boolean;
};

export type Issue = {
  id: string;
  projectId: string;
  teamId: string;
  workflowStateId: string;
  cycleId: string | null;
  sequenceNumber: number;
  title: string;
  description: string | null;
  priority: string;
  createdAt: string;
};

export type Cycle = {
  id: string;
  projectId: string;
  name: string;
  description: string | null;
  status: "draft" | "active" | "completed";
  startDate: string;
  endDate: string;
};

export async function health() {
  const response = await fetch(`${API_BASE_URL}/health`, { credentials: "include" });
  if (!response.ok) {
    throw new Error("Health check failed");
  }
  return response.json() as Promise<{ status: string }>;
}

export async function demoContext() {
  const response = await fetch(`${API_BASE_URL}/demo/context`);
  if (!response.ok) {
    throw new Error("Failed to load demo context");
  }
  return response.json() as Promise<DemoContext>;
}

export async function listIssues(projectId: string, cursor?: string) {
  const params = new URLSearchParams({ limit: "25" });
  if (cursor) params.set("cursor", cursor);
  const response = await fetch(`${API_BASE_URL}/projects/${projectId}/issues?${params.toString()}`);
  if (!response.ok) {
    throw new Error("Failed to load issues");
  }
  return response.json() as Promise<PaginatedResponse<Issue>>;
}

export async function createIssue(projectId: string, workflowStateId: string, title: string) {
  const response = await fetch(`${API_BASE_URL}/projects/${projectId}/issues`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-Team-Role": "member"
    },
    body: JSON.stringify({ title, workflowStateId })
  });
  if (!response.ok) {
    throw new Error("Failed to create issue");
  }
  return response.json() as Promise<Issue>;
}

export async function listCycles(projectId: string) {
  const response = await fetch(`${API_BASE_URL}/projects/${projectId}/cycles`);
  if (!response.ok) throw new Error("Failed to load cycles");
  return response.json() as Promise<Cycle[]>;
}

export async function createCycle(projectId: string, name: string) {
  const today = new Date();
  const start = today.toISOString().slice(0, 10);
  const endDate = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000).toISOString().slice(0, 10);
  const response = await fetch(`${API_BASE_URL}/projects/${projectId}/cycles`, {
    method: "POST",
    headers: { "Content-Type": "application/json", "X-Team-Role": "member" },
    body: JSON.stringify({ name, startDate: start, endDate })
  });
  if (!response.ok) throw new Error("Failed to create cycle");
  return response.json() as Promise<Cycle>;
}

export async function assignIssueToCycle(issueId: string, cycleId: string) {
  const response = await fetch(`${API_BASE_URL}/issues/${issueId}/cycle`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json", "X-Team-Role": "member" },
    body: JSON.stringify({ cycleId })
  });
  if (!response.ok) throw new Error("Failed to assign issue to cycle");
  return response.json() as Promise<Issue>;
}
