import { defineConfig } from "@playwright/test";

export default defineConfig({
  testDir: "./playwright",
  webServer: {
    command: process.env.CI ? "npm run dev -- --host 127.0.0.1 --port 5173" : "npm run dev",
    url: "http://127.0.0.1:5173",
    reuseExistingServer: !process.env.CI,
    timeout: 120_000
  },
  use: {
    baseURL: "http://127.0.0.1:5173"
  }
});
