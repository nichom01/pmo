import { defineConfig } from "@playwright/test";

export default defineConfig({
  testDir: "./playwright",
  webServer: {
    command: "npm run dev -- --host 127.0.0.1 --port 5173",
    url: "http://localhost:5173",
    reuseExistingServer: true,
    timeout: 120_000
  },
  use: {
    baseURL: "http://localhost:5173"
  }
});
