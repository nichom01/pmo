import { Link, Navigate, Route, Routes } from "react-router-dom";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { assignIssueToCycle, createCycle, createIssue, demoContext, health, listCycles, listIssues } from "./api";

function RootPage() {
  return <Navigate to="/acme/eng/issues" replace />;
}

function IssuesPage() {
  const queryClient = useQueryClient();
  const query = useQuery({ queryKey: ["health"], queryFn: health });
  const contextQuery = useQuery({ queryKey: ["demo-context"], queryFn: demoContext });
  const issuesQuery = useQuery({
    queryKey: ["issues", contextQuery.data?.projectId],
    queryFn: () => listIssues(contextQuery.data!.projectId),
    enabled: !!contextQuery.data?.projectId
  });
  const cyclesQuery = useQuery({
    queryKey: ["cycles", contextQuery.data?.projectId],
    queryFn: () => listCycles(contextQuery.data!.projectId),
    enabled: !!contextQuery.data?.projectId
  });
  const createIssueMutation = useMutation({
    mutationFn: async () => {
      const ctx = contextQuery.data;
      if (!ctx?.workflowStateId) throw new Error("No workflow state configured");
      return createIssue(ctx.projectId, ctx.workflowStateId, `New issue ${Date.now()}`);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["issues", contextQuery.data?.projectId] });
    }
  });
  const createCycleMutation = useMutation({
    mutationFn: async () => createCycle(contextQuery.data!.projectId, `Cycle ${Date.now()}`),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["cycles", contextQuery.data?.projectId] });
    }
  });
  const assignMutation = useMutation({
    mutationFn: async () => {
      const firstIssue = issuesQuery.data?.data[0];
      const firstCycle = cyclesQuery.data?.[0];
      if (!firstIssue || !firstCycle) throw new Error("Need issue and cycle");
      return assignIssueToCycle(firstIssue.id, firstCycle.id);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["issues", contextQuery.data?.projectId] });
    }
  });

  return (
    <main style={{ fontFamily: "sans-serif", padding: 24 }}>
      <h1>Project Management UI</h1>
      <p data-testid="health-status">
        Backend: {query.isSuccess ? query.data.status : "checking"}
      </p>
      <p data-testid="issues-count">
        Issues: {issuesQuery.data ? issuesQuery.data.data.length : 0}
      </p>
      <p data-testid="cycles-count">
        Cycles: {cyclesQuery.data ? cyclesQuery.data.length : 0}
      </p>
      <button onClick={() => createIssueMutation.mutate()} disabled={!contextQuery.data?.workflowStateId}>
        Create Issue
      </button>
      <button onClick={() => createCycleMutation.mutate()} disabled={!contextQuery.data?.projectId}>
        Create Cycle
      </button>
      <button
        onClick={() => assignMutation.mutate()}
        disabled={!issuesQuery.data?.data.length || !cyclesQuery.data?.length}
      >
        Assign First Issue To First Cycle
      </button>
      <ul>
        {issuesQuery.data?.data.map((issue) => (
          <li key={issue.id}>#{issue.sequenceNumber} {issue.title} {issue.cycleId ? "(in cycle)" : ""}</li>
        ))}
      </ul>
      <nav>
        <Link to="/acme/eng/projects">Projects</Link>
      </nav>
    </main>
  );
}

function ProjectsPage() {
  return (
    <main style={{ fontFamily: "sans-serif", padding: 24 }}>
      <h2>Projects</h2>
      <p>Project list placeholder wired for backend integration.</p>
      <Link to="/acme/eng/issues">Back to issues</Link>
    </main>
  );
}

export function App() {
  return (
    <Routes>
      <Route path="/" element={<RootPage />} />
      <Route path="/:orgSlug/:teamIdentifier/issues" element={<IssuesPage />} />
      <Route path="/:orgSlug/:teamIdentifier/projects" element={<ProjectsPage />} />
    </Routes>
  );
}
