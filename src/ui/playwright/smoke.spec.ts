import { test, expect } from "@playwright/test";

test("loads issue route, creates cycle, assigns issue, adds comment and attachment", async ({ page }) => {
  await page.goto("/acme/eng/issues");
  await expect(page.getByText("Project Management UI")).toBeVisible();
  await expect(page.getByTestId("issues-count")).toBeVisible();
  await expect(page.getByTestId("cycles-count")).toBeVisible();
  const createCycleButton = page.getByRole("button", { name: "Create Cycle" });
  const createIssueButton = page.getByRole("button", { name: "Create Issue" });
  await expect(createCycleButton).toBeEnabled({ timeout: 20000 });
  await expect(createIssueButton).toBeEnabled({ timeout: 20000 });
  await createCycleButton.click();
  await createIssueButton.click();
  await page.getByRole("button", { name: "Assign First Issue To First Cycle" }).click();
  await page.getByRole("button", { name: "Add Comment To First Issue" }).click();
  await page.getByRole("button", { name: "Add Attachment To First Issue" }).click();
  await expect(page.locator("li").first()).toBeVisible();
  await expect(page.getByText(/^Comment \d+/)).toBeVisible();
});
