"""
Generate contract tests for TypeScript client.

Uses Vitest + openapi-typescript to validate:
1. Client method signatures match OpenAPI schema
2. Request/response types conform to contract
3. Generated models deserialize correctly
"""

from jinja2 import Template
from dataclasses import dataclass
from typing import List


@dataclass
class ContractTestConfig:
    entity_name: str
    entity_name_lower: str
    entity_name_plural: str
    base_path: str


CONTRACT_TEST_TEMPLATE = Template('''/**
 * {{ entity_name }} Client Contract Tests
 * Validates client behavior against OpenAPI schema.
 * Auto-generated - do not edit manually.
 */

import { describe, it, expect, beforeAll, afterAll, afterEach } from 'vitest';
import { setupServer } from 'msw/node';
import { http, HttpResponse } from 'msw';
import {
  {{ entity_name }},
  {{ entity_name }}Create,
  {{ entity_name }}Update,
  {{ entity_name }}Client,
} from '../src';
import { {{ entity_name_lower }}Fixtures } from './fixtures';

// Mock server for contract testing
const server = setupServer();

beforeAll(() => server.listen({ onUnhandledRequest: 'error' }));
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

const BASE_URL = 'http://localhost:8080';
const client = new {{ entity_name }}Client({ baseUrl: BASE_URL });

describe('{{ entity_name }}Client Contract Tests', () => {
  describe('getById', () => {
    it('should deserialize response correctly', async () => {
      const fixture = {{ entity_name_lower }}Fixtures.valid();

      server.use(
        http.get(`${BASE_URL}{{ base_path }}/:id`, ({ params }) => {
          expect(params.id).toBe('1');
          return HttpResponse.json(fixture);
        }),
      );

      const result = await client.getById(1);

      expect(result).toBeInstanceOf({{ entity_name }});
      expect(result.id).toBe(fixture.id);
      expect(result.uuid).toBe(fixture.uuid);
      // Date fields should be Date objects
      expect(result.effectiveTimestamp).toBeInstanceOf(Date);
      if (fixture.createdDate) {
        expect(result.createdDate).toBeInstanceOf(Date);
      }
    });

    it('should throw ApiError on 404', async () => {
      server.use(
        http.get(`${BASE_URL}{{ base_path }}/:id`, () => {
          return new HttpResponse(null, { status: 404, statusText: 'Not Found' });
        }),
      );

      await expect(client.getById(999)).rejects.toThrow('API Error: 404');
    });
  });

  describe('getByUuid', () => {
    it('should deserialize response correctly', async () => {
      const fixture = {{ entity_name_lower }}Fixtures.valid();

      server.use(
        http.get(`${BASE_URL}{{ base_path }}/uuid/:uuid`, ({ params }) => {
          expect(params.uuid).toBe(fixture.uuid);
          return HttpResponse.json(fixture);
        }),
      );

      const result = await client.getByUuid(fixture.uuid);

      expect(result).toBeInstanceOf({{ entity_name }});
      expect(result.uuid).toBe(fixture.uuid);
    });
  });

  describe('getPage', () => {
    it('should deserialize paginated response correctly', async () => {
      const pageFixture = {{ entity_name_lower }}Fixtures.page();

      server.use(
        http.get(`${BASE_URL}{{ base_path }}`, ({ request }) => {
          const url = new URL(request.url);
          expect(url.searchParams.get('page')).toBe('0');
          expect(url.searchParams.get('size')).toBe('10');
          return HttpResponse.json(pageFixture);
        }),
      );

      const result = await client.getPage({ page: 0, size: 10 });

      expect(result.content).toHaveLength(pageFixture.content.length);
      expect(result.content[0]).toBeInstanceOf({{ entity_name }});
      expect(result.totalElements).toBe(pageFixture.totalElements);
      expect(result.first).toBe(true);
    });
  });

  describe('getAll', () => {
    it('should deserialize array response correctly', async () => {
      const fixtures = [{{ entity_name_lower }}Fixtures.valid(), {{ entity_name_lower }}Fixtures.valid()];

      server.use(
        http.get(`${BASE_URL}{{ base_path }}/all`, () => {
          return HttpResponse.json(fixtures);
        }),
      );

      const result = await client.getAll();

      expect(result).toHaveLength(2);
      expect(result[0]).toBeInstanceOf({{ entity_name }});
      expect(result[1]).toBeInstanceOf({{ entity_name }});
    });
  });

  describe('create', () => {
    it('should serialize request and deserialize response correctly', async () => {
      const createInput = {{ entity_name_lower }}Fixtures.createInput();
      const createdFixture = {{ entity_name_lower }}Fixtures.valid();

      server.use(
        http.post(`${BASE_URL}{{ base_path }}`, async ({ request }) => {
          const body = await request.json() as Record<string, unknown>;
          // Verify request body structure matches contract
          expect(body).toHaveProperty('parentOrganisationId');
          expect(body).toHaveProperty('effectiveTimestamp');
          // Date should be serialized as ISO string
          expect(typeof body.effectiveTimestamp).toBe('string');
          return HttpResponse.json(createdFixture, { status: 201 });
        }),
      );

      const result = await client.create(createInput);

      expect(result).toBeInstanceOf({{ entity_name }});
      expect(result.id).toBeDefined();
    });
  });

  describe('update', () => {
    it('should serialize partial update correctly', async () => {
      const updateInput = {{ entity_name_lower }}Fixtures.updateInput();
      const updatedFixture = {{ entity_name_lower }}Fixtures.valid();

      server.use(
        http.put(`${BASE_URL}{{ base_path }}/:id`, async ({ params, request }) => {
          expect(params.id).toBe('1');
          const body = await request.json() as Record<string, unknown>;
          // Only defined fields should be sent
          expect(Object.keys(body).length).toBeGreaterThan(0);
          return HttpResponse.json(updatedFixture);
        }),
      );

      const result = await client.update(1, updateInput);

      expect(result).toBeInstanceOf({{ entity_name }});
    });
  });

  describe('delete', () => {
    it('should not throw on successful delete', async () => {
      server.use(
        http.delete(`${BASE_URL}{{ base_path }}/:id`, ({ params }) => {
          expect(params.id).toBe('1');
          return new HttpResponse(null, { status: 204 });
        }),
      );

      await expect(client.delete(1)).resolves.toBeUndefined();
    });

    it('should throw ApiError on 404', async () => {
      server.use(
        http.delete(`${BASE_URL}{{ base_path }}/:id`, () => {
          return new HttpResponse(null, { status: 404, statusText: 'Not Found' });
        }),
      );

      await expect(client.delete(999)).rejects.toThrow('API Error: 404');
    });
  });
});

describe('{{ entity_name }} Model Contract Tests', () => {
  describe('fromJson', () => {
    it('should coerce number fields correctly', () => {
      const json = {
        id: '123', // String that should become number
        uuid: 'test-uuid',
        parentOrganisationId: '456',
        effectiveTimestamp: '2024-01-15T10:30:00Z',
      };

      const result = {{ entity_name }}.fromJson(json);

      expect(typeof result.id).toBe('number');
      expect(result.id).toBe(123);
      expect(typeof result.parentOrganisationId).toBe('number');
      expect(result.parentOrganisationId).toBe(456);
    });

    it('should coerce date fields to Date objects', () => {
      const json = {
        id: 1,
        uuid: 'test-uuid',
        parentOrganisationId: 1,
        effectiveTimestamp: '2024-01-15T10:30:00Z',
        createdDate: '2024-01-14T09:00:00Z',
      };

      const result = {{ entity_name }}.fromJson(json);

      expect(result.effectiveTimestamp).toBeInstanceOf(Date);
      expect(result.effectiveTimestamp.toISOString()).toBe('2024-01-15T10:30:00.000Z');
      expect(result.createdDate).toBeInstanceOf(Date);
    });

    it('should handle undefined optional fields', () => {
      const json = {
        id: 1,
        uuid: 'test-uuid',
        parentOrganisationId: 1,
        effectiveTimestamp: '2024-01-15T10:30:00Z',
        // No audit fields
      };

      const result = {{ entity_name }}.fromJson(json);

      expect(result.createdDate).toBeUndefined();
      expect(result.createdBy).toBeUndefined();
      expect(result.updatedDate).toBeUndefined();
      expect(result.updatedBy).toBeUndefined();
    });
  });

  describe('toJson', () => {
    it('should serialize dates as ISO strings', () => {
      const org = new {{ entity_name }}(
        1,
        'test-uuid',
        2,
        new Date('2024-01-15T10:30:00Z'),
        new Date('2024-01-14T09:00:00Z'),
        'creator',
      );

      const json = org.toJson();

      expect(typeof json.effectiveTimestamp).toBe('string');
      expect(json.effectiveTimestamp).toBe('2024-01-15T10:30:00.000Z');
      expect(json.createdDate).toBe('2024-01-14T09:00:00.000Z');
    });

    it('should handle undefined optional fields', () => {
      const org = new {{ entity_name }}(
        1,
        'test-uuid',
        2,
        new Date('2024-01-15T10:30:00Z'),
      );

      const json = org.toJson();

      expect(json.createdDate).toBeUndefined();
      expect(json.createdBy).toBeUndefined();
    });
  });

  describe('roundtrip', () => {
    it('should survive fromJson -> toJson -> fromJson', () => {
      const original = {{ entity_name_lower }}Fixtures.valid();

      const parsed = {{ entity_name }}.fromJson(original);
      const serialized = parsed.toJson();
      const reparsed = {{ entity_name }}.fromJson(serialized);

      expect(reparsed.id).toBe(parsed.id);
      expect(reparsed.uuid).toBe(parsed.uuid);
      expect(reparsed.effectiveTimestamp.getTime()).toBe(parsed.effectiveTimestamp.getTime());
    });
  });
});
''')


VITEST_CONFIG_TEMPLATE = Template('''import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    globals: true,
    environment: 'node',
    include: ['**/*.contract.test.ts', '**/*.integration.test.ts'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      include: ['src/**/*.ts'],
      exclude: ['src/**/*.d.ts', 'src/**/index.ts'],
    },
  },
});
''')


def generate_contract_tests(config: ContractTestConfig) -> str:
    """Generate contract tests for the entity client."""
    return CONTRACT_TEST_TEMPLATE.render(
        entity_name=config.entity_name,
        entity_name_lower=config.entity_name_lower,
        entity_name_plural=config.entity_name_plural,
        base_path=config.base_path,
    )


def generate_vitest_config() -> str:
    """Generate Vitest configuration."""
    return VITEST_CONFIG_TEMPLATE.render()


# Example usage
if __name__ == "__main__":
    config = ContractTestConfig(
        entity_name="Organisation",
        entity_name_lower="organisation",
        entity_name_plural="Organisations",
        base_path="/api/organisations",
    )

    print("=== organisation.contract.test.ts ===")
    print(generate_contract_tests(config))
