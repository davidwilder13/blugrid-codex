"""
Generate TypeScript vanilla API client.

Generates a framework-agnostic HTTP client class with:
- CRUD operations (getById, getAll, create, update, delete)
- Pagination support
- Proper error handling
- Uses fetch API (works in browser and Node.js 18+)
"""

from jinja2 import Template
from typing import List, Optional
from dataclasses import dataclass


@dataclass
class ApiClientConfig:
    entity_name: str
    entity_name_lower: str  # camelCase
    entity_name_plural: str
    base_path: str  # e.g., "/api/organisations"
    has_pagination: bool = True


API_CLIENT_TEMPLATE = Template('''/**
 * {{ entity_name }} API Client
 * Framework-agnostic HTTP client using fetch API.
 * Auto-generated from JDL entity definition.
 */

import { {{ entity_name }}, {{ entity_name }}Create, {{ entity_name }}Update } from './models';

/**
 * Pagination parameters for list queries.
 */
export interface PageParams {
  page?: number;
  size?: number;
  sort?: string;
}

/**
 * Paginated response wrapper.
 */
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

/**
 * API error response.
 */
export class ApiError extends Error {
  constructor(
    public readonly status: number,
    public readonly statusText: string,
    public readonly body?: unknown,
  ) {
    super(`API Error: ${status} ${statusText}`);
    this.name = 'ApiError';
  }
}

/**
 * Configuration for the {{ entity_name }} client.
 */
export interface {{ entity_name }}ClientConfig {
  /** Base URL for the API (e.g., 'https://api.example.com') */
  baseUrl: string;
  /** Optional function to get auth headers */
  getAuthHeaders?: () => Promise<Record<string, string>> | Record<string, string>;
  /** Optional custom fetch implementation */
  fetch?: typeof fetch;
}

/**
 * {{ entity_name }} API client for CRUD operations.
 */
export class {{ entity_name }}Client {
  private readonly baseUrl: string;
  private readonly getAuthHeaders?: () => Promise<Record<string, string>> | Record<string, string>;
  private readonly fetchFn: typeof fetch;

  constructor(config: {{ entity_name }}ClientConfig) {
    this.baseUrl = config.baseUrl.replace(/\\/$/, ''); // Remove trailing slash
    this.getAuthHeaders = config.getAuthHeaders;
    this.fetchFn = config.fetch ?? fetch;
  }

  /**
   * Build headers for API requests.
   */
  private async buildHeaders(contentType?: string): Promise<Record<string, string>> {
    const headers: Record<string, string> = {
      'Accept': 'application/json',
    };

    if (contentType) {
      headers['Content-Type'] = contentType;
    }

    if (this.getAuthHeaders) {
      const authHeaders = await this.getAuthHeaders();
      Object.assign(headers, authHeaders);
    }

    return headers;
  }

  /**
   * Handle API response and errors.
   */
  private async handleResponse<T>(
    response: Response,
    transform: (data: unknown) => T,
  ): Promise<T> {
    if (!response.ok) {
      let body: unknown;
      try {
        body = await response.json();
      } catch {
        body = await response.text();
      }
      throw new ApiError(response.status, response.statusText, body);
    }

    const data = await response.json();
    return transform(data);
  }

  /**
   * Get a single {{ entity_name_lower }} by ID.
   */
  async getById(id: number): Promise<{{ entity_name }}> {
    const response = await this.fetchFn(
      `${this.baseUrl}{{ base_path }}/${id}`,
      {
        method: 'GET',
        headers: await this.buildHeaders(),
      },
    );

    return this.handleResponse(response, {{ entity_name }}.fromJson);
  }

  /**
   * Get a single {{ entity_name_lower }} by UUID.
   */
  async getByUuid(uuid: string): Promise<{{ entity_name }}> {
    const response = await this.fetchFn(
      `${this.baseUrl}{{ base_path }}/uuid/${uuid}`,
      {
        method: 'GET',
        headers: await this.buildHeaders(),
      },
    );

    return this.handleResponse(response, {{ entity_name }}.fromJson);
  }

  /**
   * Get a paginated list of {{ entity_name_plural }}.
   */
  async getPage(params: PageParams = {}): Promise<Page<{{ entity_name }}>> {
    const searchParams = new URLSearchParams();
    if (params.page !== undefined) searchParams.set('page', String(params.page));
    if (params.size !== undefined) searchParams.set('size', String(params.size));
    if (params.sort) searchParams.set('sort', params.sort);

    const url = `${this.baseUrl}{{ base_path }}?${searchParams.toString()}`;

    const response = await this.fetchFn(url, {
      method: 'GET',
      headers: await this.buildHeaders(),
    });

    return this.handleResponse(response, (data) => {
      const page = data as Record<string, unknown>;
      return {
        content: (page.content as unknown[]).map({{ entity_name }}.fromJson),
        totalElements: Number(page.totalElements),
        totalPages: Number(page.totalPages),
        size: Number(page.size),
        number: Number(page.number),
        first: Boolean(page.first),
        last: Boolean(page.last),
        empty: Boolean(page.empty),
      };
    });
  }

  /**
   * Get all {{ entity_name_plural }} (use with caution for large datasets).
   */
  async getAll(): Promise<{{ entity_name }}[]> {
    const response = await this.fetchFn(
      `${this.baseUrl}{{ base_path }}/all`,
      {
        method: 'GET',
        headers: await this.buildHeaders(),
      },
    );

    return this.handleResponse(response, (data) =>
      (data as unknown[]).map({{ entity_name }}.fromJson),
    );
  }

  /**
   * Create a new {{ entity_name_lower }}.
   */
  async create(input: {{ entity_name }}Create): Promise<{{ entity_name }}> {
    const response = await this.fetchFn(
      `${this.baseUrl}{{ base_path }}`,
      {
        method: 'POST',
        headers: await this.buildHeaders('application/json'),
        body: JSON.stringify(input.toJson()),
      },
    );

    return this.handleResponse(response, {{ entity_name }}.fromJson);
  }

  /**
   * Update an existing {{ entity_name_lower }}.
   */
  async update(id: number, input: {{ entity_name }}Update): Promise<{{ entity_name }}> {
    const response = await this.fetchFn(
      `${this.baseUrl}{{ base_path }}/${id}`,
      {
        method: 'PUT',
        headers: await this.buildHeaders('application/json'),
        body: JSON.stringify(input.toJson()),
      },
    );

    return this.handleResponse(response, {{ entity_name }}.fromJson);
  }

  /**
   * Delete a {{ entity_name_lower }} by ID.
   */
  async delete(id: number): Promise<void> {
    const response = await this.fetchFn(
      `${this.baseUrl}{{ base_path }}/${id}`,
      {
        method: 'DELETE',
        headers: await this.buildHeaders(),
      },
    );

    if (!response.ok) {
      let body: unknown;
      try {
        body = await response.json();
      } catch {
        body = await response.text();
      }
      throw new ApiError(response.status, response.statusText, body);
    }
  }
}
''')


INDEX_TEMPLATE = Template('''/**
 * {{ entity_name }} API Client Package
 * Auto-generated exports.
 */

// Models
export { {{ entity_name }} } from './models/{{ entity_name }}';
export { {{ entity_name }}Create } from './models/{{ entity_name }}Create';
export { {{ entity_name }}Update } from './models/{{ entity_name }}Update';

// Client
export {
  {{ entity_name }}Client,
  {{ entity_name }}ClientConfig,
  ApiError,
  Page,
  PageParams,
} from './{{ entity_name }}Client';
''')


MODELS_INDEX_TEMPLATE = Template('''/**
 * {{ entity_name }} model exports.
 */
export { {{ entity_name }} } from './{{ entity_name }}';
export { {{ entity_name }}Create } from './{{ entity_name }}Create';
export { {{ entity_name }}Update } from './{{ entity_name }}Update';
''')


def generate_api_client(config: ApiClientConfig) -> str:
    """Generate the API client class."""
    return API_CLIENT_TEMPLATE.render(
        entity_name=config.entity_name,
        entity_name_lower=config.entity_name_lower,
        entity_name_plural=config.entity_name_plural,
        base_path=config.base_path,
    )


def generate_index_file(entity_name: str) -> str:
    """Generate the package index file."""
    return INDEX_TEMPLATE.render(entity_name=entity_name)


def generate_models_index(entity_name: str) -> str:
    """Generate the models index file."""
    return MODELS_INDEX_TEMPLATE.render(entity_name=entity_name)


def to_camel_case(name: str) -> str:
    """Convert PascalCase to camelCase."""
    if not name:
        return name
    return name[0].lower() + name[1:]


def to_plural(name: str) -> str:
    """Simple pluralization."""
    if name.endswith('y'):
        return name[:-1] + 'ies'
    elif name.endswith('s') or name.endswith('x') or name.endswith('ch') or name.endswith('sh'):
        return name + 'es'
    else:
        return name + 's'


# Example usage
if __name__ == "__main__":
    config = ApiClientConfig(
        entity_name="Organisation",
        entity_name_lower="organisation",
        entity_name_plural="organisations",
        base_path="/api/organisations",
    )

    print("=== OrganisationClient.ts ===")
    print(generate_api_client(config))
    print("\n=== index.ts ===")
    print(generate_index_file("Organisation"))
    print("\n=== models/index.ts ===")
    print(generate_models_index("Organisation"))
