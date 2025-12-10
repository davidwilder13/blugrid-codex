/**
 * Organisation Test Fixtures
 * Factory functions for generating valid test data.
 * Auto-generated - do not edit manually.
 */

import { faker } from '@faker-js/faker';
import { Organisation, OrganisationCreate, OrganisationUpdate } from '../src';

// Seed faker for reproducible tests (optional)
// faker.seed(12345);

/**
 * Raw JSON fixtures (for MSW mocking).
 */
export const organisationFixtures = {
  /**
   * Generate a valid Organisation JSON object.
   */
  valid(overrides: Partial<Record<string, unknown>> = {}): Record<string, unknown> {
    return {
      id: faker.number.int({ min: 1, max: 100000 }),
      uuid: faker.string.uuid(),
      parentOrganisationId: faker.number.int({ min: 1, max: 100000 }),
      effectiveTimestamp: faker.date.recent().toISOString(),
      createdDate: faker.date.past().toISOString(),
      createdBy: faker.internet.email(),
      updatedDate: faker.date.recent().toISOString(),
      updatedBy: faker.internet.email(),
      ...overrides,
    };
  },

  /**
   * Generate a valid OrganisationCreate input.
   */
  createInput(overrides: Partial<Record<string, unknown>> = {}): OrganisationCreate {
    const data = {
      parentOrganisationId: faker.number.int({ min: 1, max: 100000 }),
      effectiveTimestamp: faker.date.recent().toISOString(),
      ...overrides,
    };
    return OrganisationCreate.fromJson(data);
  },

  /**
   * Generate a valid OrganisationUpdate input.
   */
  updateInput(overrides: Partial<Record<string, unknown>> = {}): OrganisationUpdate {
    const data = {
      parentOrganisationId: faker.number.int({ min: 1, max: 100000 }),
      effectiveTimestamp: faker.date.recent().toISOString(),
      ...overrides,
    };
    return OrganisationUpdate.fromJson(data);
  },

  /**
   * Generate a paginated response.
   */
  page(count: number = 3, overrides: Partial<Record<string, unknown>> = {}): Record<string, unknown> {
    const content = Array.from({ length: count }, () => organisationFixtures.valid());
    return {
      content,
      totalElements: count,
      totalPages: 1,
      size: 20,
      number: 0,
      first: true,
      last: true,
      empty: count === 0,
      ...overrides,
    };
  },

  /**
   * Generate an array of valid Organisation objects.
   */
  list(count: number = 3): Record<string, unknown>[] {
    return Array.from({ length: count }, () => organisationFixtures.valid());
  },

  /**
   * Generate a Organisation model instance (not raw JSON).
   */
  instance(overrides: Partial<Record<string, unknown>> = {}): Organisation {
    return Organisation.fromJson(organisationFixtures.valid(overrides));
  },
};

/**
 * Strongly-typed fixture builders for more complex scenarios.
 */
export class OrganisationFixtureBuilder {
  private data: Record<string, unknown>;

  constructor() {
    this.data = organisationFixtures.valid();
  }

  withId(id: number): this {
    this.data.id = id;
    return this;
  }

  withUuid(uuid: string): this {
    this.data.uuid = uuid;
    return this;
  }

  withParentOrganisationId(value: number): this {
    this.data.parentOrganisationId = value;
    return this;
  }

  withEffectiveTimestamp(value: Date | string): this {
    this.data.effectiveTimestamp = value instanceof Date ? value.toISOString() : value;
    return this;
  }

  build(): Record<string, unknown> {
    return { ...this.data };
  }

  buildInstance(): Organisation {
    return Organisation.fromJson(this.data);
  }
}

/**
 * Factory function for builder pattern.
 */
export function aOrganisation(): OrganisationFixtureBuilder {
  return new OrganisationFixtureBuilder();
}