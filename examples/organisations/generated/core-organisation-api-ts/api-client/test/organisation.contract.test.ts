/**
 * Organisation Client Contract Tests
 * Validates client behavior against OpenAPI schema.
 * Auto-generated - do not edit manually.
 */

import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  Organisation,
  OrganisationCreate,
  OrganisationUpdate,
  OrganisationClient,
} from '../src';
import { organisationFixtures } from './fixtures';

// Create a mock fetch function
const createMockFetch = (handler: (url: string, options?: RequestInit) => Promise<Response>) => {
  return vi.fn(handler);
};

describe('OrganisationClient Contract Tests', () => {
  describe('getById', () => {
    it('should deserialize response correctly', async () => {
      const fixture = organisationFixtures.valid();

      const mockFetch = createMockFetch(async (url) => {
        expect(url).toBe('http://localhost:8080/api/organisations/1');
        return new Response(JSON.stringify(fixture), {
          status: 200,
          headers: { 'Content-Type': 'application/json' },
        });
      });

      const client = new OrganisationClient({
        baseUrl: 'http://localhost:8080',
        fetch: mockFetch,
      });

      const result = await client.getById(1);

      expect(result).toBeInstanceOf(Organisation);
      expect(result.id).toBe(fixture.id);
      expect(result.uuid).toBe(fixture.uuid);
      // Date fields should be Date objects
      expect(result.effectiveTimestamp).toBeInstanceOf(Date);
      if (fixture.createdDate) {
        expect(result.createdDate).toBeInstanceOf(Date);
      }
      expect(mockFetch).toHaveBeenCalledTimes(1);
    });

    it('should throw ApiError on 404', async () => {
      const mockFetch = createMockFetch(async () => {
        return new Response(null, { status: 404, statusText: 'Not Found' });
      });

      const client = new OrganisationClient({
        baseUrl: 'http://localhost:8080',
        fetch: mockFetch,
      });

      await expect(client.getById(999)).rejects.toThrow('API Error: 404');
    });
  });

  describe('getByUuid', () => {
    it('should deserialize response correctly', async () => {
      const fixture = organisationFixtures.valid();
      const uuid = fixture.uuid as string;

      const mockFetch = createMockFetch(async (url) => {
        expect(url).toBe(`http://localhost:8080/api/organisations/uuid/${uuid}`);
        return new Response(JSON.stringify(fixture), {
          status: 200,
          headers: { 'Content-Type': 'application/json' },
        });
      });

      const client = new OrganisationClient({
        baseUrl: 'http://localhost:8080',
        fetch: mockFetch,
      });

      const result = await client.getByUuid(uuid);

      expect(result).toBeInstanceOf(Organisation);
      expect(result.uuid).toBe(uuid);
    });
  });

  describe('getPage', () => {
    it('should deserialize paginated response correctly', async () => {
      const pageFixture = organisationFixtures.page();

      const mockFetch = createMockFetch(async (url) => {
        expect(url).toContain('/api/organisations?');
        expect(url).toContain('page=0');
        expect(url).toContain('size=10');
        return new Response(JSON.stringify(pageFixture), {
          status: 200,
          headers: { 'Content-Type': 'application/json' },
        });
      });

      const client = new OrganisationClient({
        baseUrl: 'http://localhost:8080',
        fetch: mockFetch,
      });

      const result = await client.getPage({ page: 0, size: 10 });

      expect(result.content).toHaveLength((pageFixture.content as unknown[]).length);
      expect(result.content[0]).toBeInstanceOf(Organisation);
      expect(result.totalElements).toBe(pageFixture.totalElements);
      expect(result.first).toBe(true);
    });
  });

  describe('getAll', () => {
    it('should deserialize array response correctly', async () => {
      const fixtures = [organisationFixtures.valid(), organisationFixtures.valid()];

      const mockFetch = createMockFetch(async (url) => {
        expect(url).toBe('http://localhost:8080/api/organisations/all');
        return new Response(JSON.stringify(fixtures), {
          status: 200,
          headers: { 'Content-Type': 'application/json' },
        });
      });

      const client = new OrganisationClient({
        baseUrl: 'http://localhost:8080',
        fetch: mockFetch,
      });

      const result = await client.getAll();

      expect(result).toHaveLength(2);
      expect(result[0]).toBeInstanceOf(Organisation);
      expect(result[1]).toBeInstanceOf(Organisation);
    });
  });

  describe('create', () => {
    it('should serialize request and deserialize response correctly', async () => {
      const createInput = organisationFixtures.createInput();
      const createdFixture = organisationFixtures.valid();

      const mockFetch = createMockFetch(async (url, options) => {
        expect(url).toBe('http://localhost:8080/api/organisations');
        expect(options?.method).toBe('POST');

        const body = JSON.parse(options?.body as string);
        // Verify request body structure matches contract
        expect(body).toHaveProperty('parentOrganisationId');
        expect(body).toHaveProperty('effectiveTimestamp');
        // Date should be serialized as ISO string
        expect(typeof body.effectiveTimestamp).toBe('string');

        return new Response(JSON.stringify(createdFixture), {
          status: 201,
          headers: { 'Content-Type': 'application/json' },
        });
      });

      const client = new OrganisationClient({
        baseUrl: 'http://localhost:8080',
        fetch: mockFetch,
      });

      const result = await client.create(createInput);

      expect(result).toBeInstanceOf(Organisation);
      expect(result.id).toBeDefined();
    });
  });

  describe('update', () => {
    it('should serialize partial update correctly', async () => {
      const updateInput = organisationFixtures.updateInput();
      const updatedFixture = organisationFixtures.valid();

      const mockFetch = createMockFetch(async (url, options) => {
        expect(url).toBe('http://localhost:8080/api/organisations/1');
        expect(options?.method).toBe('PUT');

        const body = JSON.parse(options?.body as string);
        // Only defined fields should be sent
        expect(Object.keys(body).length).toBeGreaterThan(0);

        return new Response(JSON.stringify(updatedFixture), {
          status: 200,
          headers: { 'Content-Type': 'application/json' },
        });
      });

      const client = new OrganisationClient({
        baseUrl: 'http://localhost:8080',
        fetch: mockFetch,
      });

      const result = await client.update(1, updateInput);

      expect(result).toBeInstanceOf(Organisation);
    });
  });

  describe('delete', () => {
    it('should not throw on successful delete', async () => {
      const mockFetch = createMockFetch(async (url, options) => {
        expect(url).toBe('http://localhost:8080/api/organisations/1');
        expect(options?.method).toBe('DELETE');
        return new Response(null, { status: 204 });
      });

      const client = new OrganisationClient({
        baseUrl: 'http://localhost:8080',
        fetch: mockFetch,
      });

      await expect(client.delete(1)).resolves.toBeUndefined();
    });

    it('should throw ApiError on 404', async () => {
      const mockFetch = createMockFetch(async () => {
        return new Response(null, { status: 404, statusText: 'Not Found' });
      });

      const client = new OrganisationClient({
        baseUrl: 'http://localhost:8080',
        fetch: mockFetch,
      });

      await expect(client.delete(999)).rejects.toThrow('API Error: 404');
    });
  });
});

describe('Organisation Model Contract Tests', () => {
  describe('fromJson', () => {
    it('should coerce number fields correctly', () => {
      const json = {
        id: '123', // String that should become number
        uuid: 'test-uuid',
        parentOrganisationId: '456',
        effectiveTimestamp: '2024-01-15T10:30:00Z',
      };

      const result = Organisation.fromJson(json);

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

      const result = Organisation.fromJson(json);

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

      const result = Organisation.fromJson(json);

      expect(result.createdDate).toBeUndefined();
      expect(result.createdBy).toBeUndefined();
      expect(result.updatedDate).toBeUndefined();
      expect(result.updatedBy).toBeUndefined();
    });
  });

  describe('toJson', () => {
    it('should serialize dates as ISO strings', () => {
      const org = new Organisation(
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
      const org = new Organisation(
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
      const original = organisationFixtures.valid();

      const parsed = Organisation.fromJson(original);
      const serialized = parsed.toJson();
      const reparsed = Organisation.fromJson(serialized);

      expect(reparsed.id).toBe(parsed.id);
      expect(reparsed.uuid).toBe(parsed.uuid);
      expect(reparsed.effectiveTimestamp.getTime()).toBe(parsed.effectiveTimestamp.getTime());
    });
  });
});
