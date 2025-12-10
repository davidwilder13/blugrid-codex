/**
 * Testcontainers setup utilities for Organisation integration tests.
 * Auto-generated - do not edit manually.
 */

import { GenericContainer, StartedTestContainer, Network, Wait } from 'testcontainers';
import { PostgreSqlContainer, StartedPostgreSqlContainer } from '@testcontainers/postgresql';

export interface OrganisationTestEnvironment {
  apiContainer: StartedTestContainer;
  postgresContainer: StartedPostgreSqlContainer;
  apiUrl: string;
  cleanup: () => Promise<void>;
}

/**
 * Start a complete test environment with API and database containers.
 */
export async function startOrganisationTestEnvironment(): Promise<OrganisationTestEnvironment> {
  // Create shared network
  const network = await new Network().start();

  // Start PostgreSQL
  const postgresContainer = await new PostgreSqlContainer('postgres:17-alpine')
    .withNetwork(network)
    .withNetworkAliases('postgres')
    .withDatabase('organisation_test')
    .withUsername('test')
    .withPassword('test')
    .start();

  // Start API container
  const apiContainer = await new GenericContainer('blugrid/organisation-api:latest')
    .withNetwork(network)
    .withExposedPorts(8080)
    .withEnvironment({
      SPRING_PROFILES_ACTIVE: 'test',
      DATABASE_URL: `jdbc:postgresql://postgres:5432/organisation_test`,
      DATABASE_USERNAME: 'test',
      DATABASE_PASSWORD: 'test',
    })
    .withWaitStrategy(Wait.forHttp('/actuator/health', 8080).forStatusCode(200))
    .withStartupTimeout(120_000)
    .start();

  const mappedPort = apiContainer.getMappedPort(8080);
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