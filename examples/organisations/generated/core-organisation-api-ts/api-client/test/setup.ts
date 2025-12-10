/**
 * Vitest setup file for MSW.
 */
import { beforeAll, afterEach, afterAll } from 'vitest';
import { setupServer } from 'msw/node';
import { http, HttpResponse } from 'msw';

// Default handlers (empty, tests will add their own)
const handlers: never[] = [];

// Export for use in tests if needed
export const server = setupServer(...handlers);

// Export http and HttpResponse for convenience
export { http, HttpResponse };

beforeAll(() => {
  // Start MSW server in Node.js
  server.listen({
    onUnhandledRequest: 'warn',
  });
});

afterEach(() => {
  // Reset handlers after each test (remove runtime handlers)
  server.resetHandlers();
});

afterAll(() => {
  // Clean up after all tests
  server.close();
});
