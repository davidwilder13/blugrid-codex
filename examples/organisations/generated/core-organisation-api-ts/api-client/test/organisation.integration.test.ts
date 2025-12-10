/**
 * Organisation Client Integration Tests
 * Tests against real Docker containers using Testcontainers.
 * Auto-generated - do not edit manually.
 *
 * NOTE: These tests require Docker and the API image to be available.
 * Build the Kotlin API and push to Docker registry before running.
 * Run with: pnpm test:integration
 */

import { describe, it, expect, beforeAll, afterAll } from 'vitest';
import { GenericContainer, StartedTestContainer, Wait } from 'testcontainers';
import {
  Organisation,
  OrganisationCreate,
  OrganisationUpdate,
  OrganisationClient,
} from '../src';
import { organisationFixtures } from './fixtures';

// Skip integration tests if SKIP_INTEGRATION is set or Docker image doesn't exist
const SKIP_INTEGRATION = process.env.SKIP_INTEGRATION === 'true' ||
  process.env.CI === 'true'; // Skip in CI unless explicitly enabled

describe.skipIf(SKIP_INTEGRATION)('OrganisationClient Integration Tests', () => {
  let container: StartedTestContainer;
  let client: OrganisationClient;
  let baseUrl: string;

  beforeAll(async () => {
    // Start the API container
    container = await new GenericContainer('blugrid/organisation-api:latest')
      .withExposedPorts(8080)
      .withEnvironment({
        SPRING_PROFILES_ACTIVE: 'test',
        // Add database config if needed
        // DATABASE_URL: 'jdbc:postgresql://...',
      })
      .withWaitStrategy(Wait.forHttp('/actuator/health', 8080).forStatusCode(200))
      .withStartupTimeout(120_000)
      .start();

    const mappedPort = container.getMappedPort(8080);
    const host = container.getHost();
    baseUrl = `http://${host}:${mappedPort}`;

    client = new OrganisationClient({ baseUrl });

    console.log(`Organisation API started at ${baseUrl}`);
  }, 180_000); // 3 minute timeout for container startup

  afterAll(async () => {
    if (container) {
      await container.stop();
    }
  });

  describe('CRUD Operations', () => {
    let createdId: number;
    let createdUuid: string;

    it('should create a new organisation', async () => {
      const input = new OrganisationCreate(
        1, // parentOrganisationId
        new Date(), // effectiveTimestamp
      );

      const result = await client.create(input);

      expect(result).toBeInstanceOf(Organisation);
      expect(result.id).toBeDefined();
      expect(result.uuid).toBeDefined();
      expect(result.parentOrganisationId).toBe(1);

      createdId = result.id;
      createdUuid = result.uuid;
    });

    it('should get organisation by ID', async () => {
      const result = await client.getById(createdId);

      expect(result).toBeInstanceOf(Organisation);
      expect(result.id).toBe(createdId);
      expect(result.uuid).toBe(createdUuid);
    });

    it('should get organisation by UUID', async () => {
      const result = await client.getByUuid(createdUuid);

      expect(result).toBeInstanceOf(Organisation);
      expect(result.id).toBe(createdId);
    });

    it('should get paginated list', async () => {
      const result = await client.getPage({ page: 0, size: 10 });

      expect(result.content.length).toBeGreaterThan(0);
      expect(result.content[0]).toBeInstanceOf(Organisation);
      expect(result.totalElements).toBeGreaterThan(0);
    });

    it('should get all Organisations', async () => {
      const result = await client.getAll();

      expect(result.length).toBeGreaterThan(0);
      expect(result[0]).toBeInstanceOf(Organisation);
    });

    it('should update organisation', async () => {
      const update = new OrganisationUpdate(
        2, // new parentOrganisationId
      );

      const result = await client.update(createdId, update);

      expect(result).toBeInstanceOf(Organisation);
      expect(result.parentOrganisationId).toBe(2);
    });

    it('should delete organisation', async () => {
      await expect(client.delete(createdId)).resolves.toBeUndefined();

      // Verify deletion
      await expect(client.getById(createdId)).rejects.toThrow('404');
    });
  });

  describe('Error Handling', () => {
    it('should throw on non-existent ID', async () => {
      await expect(client.getById(999999)).rejects.toThrow('404');
    });

    it('should throw on non-existent UUID', async () => {
      await expect(client.getByUuid('00000000-0000-0000-0000-000000000000')).rejects.toThrow('404');
    });

    it('should throw on delete non-existent', async () => {
      await expect(client.delete(999999)).rejects.toThrow('404');
    });
  });

  describe('Date Handling', () => {
    it('should preserve date precision through round-trip', async () => {
      const timestamp = new Date('2024-06-15T14:30:00.000Z');

      const input = new OrganisationCreate(1, timestamp);
      const created = await client.create(input);

      expect(created.effectiveTimestamp.getTime()).toBe(timestamp.getTime());

      // Fetch again and verify
      const fetched = await client.getById(created.id);
      expect(fetched.effectiveTimestamp.getTime()).toBe(timestamp.getTime());

      // Cleanup
      await client.delete(created.id);
    });
  });
});