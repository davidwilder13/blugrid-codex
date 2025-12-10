"""
Generate Testcontainers-based integration tests for TypeScript client.

Spins up real Docker containers with the Kotlin API and tests
the client against the live service.
"""

from jinja2 import Template
from dataclasses import dataclass


@dataclass
class IntegrationTestConfig:
    entity_name: str
    entity_name_lower: str
    entity_name_plural: str
    base_path: str
    api_docker_image: str  # e.g., "blugrid/organisation-api:latest"
    api_port: int = 8080


INTEGRATION_TEST_TEMPLATE = Template('''/**
 * {{ entity_name }} Client Integration Tests
 * Tests against real Docker containers using Testcontainers.
 * Auto-generated - do not edit manually.
 */

import { describe, it, expect, beforeAll, afterAll } from 'vitest';
import { GenericContainer, StartedTestContainer, Wait } from 'testcontainers';
import {
  {{ entity_name }},
  {{ entity_name }}Create,
  {{ entity_name }}Update,
  {{ entity_name }}Client,
} from '../src';
import { {{ entity_name_lower }}Fixtures } from './fixtures';

describe('{{ entity_name }}Client Integration Tests', () => {
  let container: StartedTestContainer;
  let client: {{ entity_name }}Client;
  let baseUrl: string;

  beforeAll(async () => {
    // Start the API container
    container = await new GenericContainer('{{ api_docker_image }}')
      .withExposedPorts({{ api_port }})
      .withEnvironment({
        SPRING_PROFILES_ACTIVE: 'test',
        // Add database config if needed
        // DATABASE_URL: 'jdbc:postgresql://...',
      })
      .withWaitStrategy(Wait.forHttp('/actuator/health', {{ api_port }}).forStatusCode(200))
      .withStartupTimeout(120_000)
      .start();

    const mappedPort = container.getMappedPort({{ api_port }});
    const host = container.getHost();
    baseUrl = `http://${host}:${mappedPort}`;

    client = new {{ entity_name }}Client({ baseUrl });

    console.log(`{{ entity_name }} API started at ${baseUrl}`);
  }, 180_000); // 3 minute timeout for container startup

  afterAll(async () => {
    if (container) {
      await container.stop();
    }
  });

  describe('CRUD Operations', () => {
    let createdId: number;
    let createdUuid: string;

    it('should create a new {{ entity_name_lower }}', async () => {
      const input = new {{ entity_name }}Create(
        1, // parentOrganisationId
        new Date(), // effectiveTimestamp
      );

      const result = await client.create(input);

      expect(result).toBeInstanceOf({{ entity_name }});
      expect(result.id).toBeDefined();
      expect(result.uuid).toBeDefined();
      expect(result.parentOrganisationId).toBe(1);

      createdId = result.id;
      createdUuid = result.uuid;
    });

    it('should get {{ entity_name_lower }} by ID', async () => {
      const result = await client.getById(createdId);

      expect(result).toBeInstanceOf({{ entity_name }});
      expect(result.id).toBe(createdId);
      expect(result.uuid).toBe(createdUuid);
    });

    it('should get {{ entity_name_lower }} by UUID', async () => {
      const result = await client.getByUuid(createdUuid);

      expect(result).toBeInstanceOf({{ entity_name }});
      expect(result.id).toBe(createdId);
    });

    it('should get paginated list', async () => {
      const result = await client.getPage({ page: 0, size: 10 });

      expect(result.content.length).toBeGreaterThan(0);
      expect(result.content[0]).toBeInstanceOf({{ entity_name }});
      expect(result.totalElements).toBeGreaterThan(0);
    });

    it('should get all {{ entity_name_plural }}', async () => {
      const result = await client.getAll();

      expect(result.length).toBeGreaterThan(0);
      expect(result[0]).toBeInstanceOf({{ entity_name }});
    });

    it('should update {{ entity_name_lower }}', async () => {
      const update = new {{ entity_name }}Update(
        2, // new parentOrganisationId
      );

      const result = await client.update(createdId, update);

      expect(result).toBeInstanceOf({{ entity_name }});
      expect(result.parentOrganisationId).toBe(2);
    });

    it('should delete {{ entity_name_lower }}', async () => {
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

      const input = new {{ entity_name }}Create(1, timestamp);
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
''')


DOCKER_COMPOSE_TEST_TEMPLATE = Template('''# Docker Compose for {{ entity_name }} Integration Tests
# Auto-generated - do not edit manually.

version: '3.8'

services:
  {{ entity_name_lower }}-api:
    image: {{ api_docker_image }}
    ports:
      - "{{ api_port }}:{{ api_port }}"
    environment:
      SPRING_PROFILES_ACTIVE: test
      DATABASE_URL: jdbc:postgresql://postgres:5432/{{ entity_name_lower }}_test
      DATABASE_USERNAME: test
      DATABASE_PASSWORD: test
    depends_on:
      postgres:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:{{ api_port }}/actuator/health"]
      interval: 10s
      timeout: 5s
      retries: 5

  postgres:
    image: postgres:17-alpine
    environment:
      POSTGRES_DB: {{ entity_name_lower }}_test
      POSTGRES_USER: test
      POSTGRES_PASSWORD: test
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U test -d {{ entity_name_lower }}_test"]
      interval: 5s
      timeout: 5s
      retries: 5
    tmpfs:
      - /var/lib/postgresql/data
''')


TESTCONTAINERS_SETUP_TEMPLATE = Template('''/**
 * Testcontainers setup utilities for {{ entity_name }} integration tests.
 * Auto-generated - do not edit manually.
 */

import { GenericContainer, StartedTestContainer, Network, Wait } from 'testcontainers';
import { PostgreSqlContainer, StartedPostgreSqlContainer } from '@testcontainers/postgresql';

export interface {{ entity_name }}TestEnvironment {
  apiContainer: StartedTestContainer;
  postgresContainer: StartedPostgreSqlContainer;
  apiUrl: string;
  cleanup: () => Promise<void>;
}

/**
 * Start a complete test environment with API and database containers.
 */
export async function start{{ entity_name }}TestEnvironment(): Promise<{{ entity_name }}TestEnvironment> {
  // Create shared network
  const network = await new Network().start();

  // Start PostgreSQL
  const postgresContainer = await new PostgreSqlContainer('postgres:17-alpine')
    .withNetwork(network)
    .withNetworkAliases('postgres')
    .withDatabase('{{ entity_name_lower }}_test')
    .withUsername('test')
    .withPassword('test')
    .start();

  // Start API container
  const apiContainer = await new GenericContainer('{{ api_docker_image }}')
    .withNetwork(network)
    .withExposedPorts({{ api_port }})
    .withEnvironment({
      SPRING_PROFILES_ACTIVE: 'test',
      DATABASE_URL: `jdbc:postgresql://postgres:5432/{{ entity_name_lower }}_test`,
      DATABASE_USERNAME: 'test',
      DATABASE_PASSWORD: 'test',
    })
    .withWaitStrategy(Wait.forHttp('/actuator/health', {{ api_port }}).forStatusCode(200))
    .withStartupTimeout(120_000)
    .start();

  const mappedPort = apiContainer.getMappedPort({{ api_port }});
  const host = apiContainer.getHost();
  const apiUrl = `http://${host}:${mappedPort}`;

  return {
    apiContainer,
    postgresContainer,
    apiUrl,
    cleanup: async () => {
      await apiContainer.stop();
      await postgresContainer.stop();
      await network.stop();
    },
  };
}
''')


def generate_integration_tests(config: IntegrationTestConfig) -> str:
    """Generate Testcontainers integration tests."""
    return INTEGRATION_TEST_TEMPLATE.render(
        entity_name=config.entity_name,
        entity_name_lower=config.entity_name_lower,
        entity_name_plural=config.entity_name_plural,
        base_path=config.base_path,
        api_docker_image=config.api_docker_image,
        api_port=config.api_port,
    )


def generate_docker_compose_test(config: IntegrationTestConfig) -> str:
    """Generate Docker Compose file for integration tests."""
    return DOCKER_COMPOSE_TEST_TEMPLATE.render(
        entity_name=config.entity_name,
        entity_name_lower=config.entity_name_lower,
        api_docker_image=config.api_docker_image,
        api_port=config.api_port,
    )


def generate_testcontainers_setup(config: IntegrationTestConfig) -> str:
    """Generate Testcontainers setup utilities."""
    return TESTCONTAINERS_SETUP_TEMPLATE.render(
        entity_name=config.entity_name,
        entity_name_lower=config.entity_name_lower,
        api_docker_image=config.api_docker_image,
        api_port=config.api_port,
    )


# Example usage
if __name__ == "__main__":
    config = IntegrationTestConfig(
        entity_name="Organisation",
        entity_name_lower="organisation",
        entity_name_plural="Organisations",
        base_path="/api/organisations",
        api_docker_image="blugrid/organisation-api:latest",
        api_port=8080,
    )

    print("=== organisation.integration.test.ts ===")
    print(generate_integration_tests(config))
    print("\n=== docker-compose.test.yml ===")
    print(generate_docker_compose_test(config))
