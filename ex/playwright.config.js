// @ts-check
const { defineConfig } = require('@playwright/test');

module.exports = defineConfig({
  testDir: './e2e',
  timeout: 30000,
  expect: { timeout: 5000 },
  fullyParallel: false,
  retries: 0,
  use: {
    baseURL: 'http://localhost:8280',
    headless: true,
  },
  webServer: {
    // Assumes shadow-cljs is already running or compiled output is served
    // Start with: npx shadow-cljs watch client
    // Or serve the compiled output: npx shadow-cljs compile client && npx http-server resources/public -p 8280
    command: 'echo "Ensure shadow-cljs dev server is running on port 8280"',
    url: 'http://localhost:8280',
    reuseExistingServer: true,
    timeout: 5000,
  },
});
