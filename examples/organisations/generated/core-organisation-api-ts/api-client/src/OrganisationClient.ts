/**
 * Organisation API Client
 * Framework-agnostic HTTP client using fetch API.
 * Auto-generated from JDL entity definition.
 */

import { Organisation, OrganisationCreate, OrganisationUpdate } from './models';

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
 * Configuration for the Organisation client.
 */
export interface OrganisationClientConfig {
  /** Base URL for the API (e.g., 'https://api.example.com') */
  baseUrl: string;
  /** Optional function to get auth headers */
  getAuthHeaders?: () => Promise<Record<string, string>> | Record<string, string>;
  /** Optional custom fetch implementation */
  fetch?: typeof fetch;
}

/**
 * Organisation API client for CRUD operations.
 */
export class OrganisationClient {
  private readonly baseUrl: string;
  private readonly getAuthHeaders?: () => Promise<Record<string, string>> | Record<string, string>;
  private readonly fetchFn: typeof fetch;

  constructor(config: OrganisationClientConfig) {
    this.baseUrl = config.baseUrl.replace(/\/$/, ''); // Remove trailing slash
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
   * Get a single organisation by ID.
   */
  async getById(id: number): Promise<Organisation> {
    const response = await this.fetchFn(
      `${this.baseUrl}/api/organisations/${id}`,
      {
        method: 'GET',
        headers: await this.buildHeaders(),
      },
    );

    return this.handleResponse(response, Organisation.fromJson);
  }

  /**
   * Get a single organisation by UUID.
   */
  async getByUuid(uuid: string): Promise<Organisation> {
    const response = await this.fetchFn(
      `${this.baseUrl}/api/organisations/uuid/${uuid}`,
      {
        method: 'GET',
        headers: await this.buildHeaders(),
      },
    );

    return this.handleResponse(response, Organisation.fromJson);
  }

  /**
   * Get a paginated list of organisations.
   */
  async getPage(params: PageParams = {}): Promise<Page<Organisation>> {
    const searchParams = new URLSearchParams();
    if (params.page !== undefined) searchParams.set('page', String(params.page));
    if (params.size !== undefined) searchParams.set('size', String(params.size));
    if (params.sort) searchParams.set('sort', params.sort);

    const url = `${this.baseUrl}/api/organisations?${searchParams.toString()}`;

    const response = await this.fetchFn(url, {
      method: 'GET',
      headers: await this.buildHeaders(),
    });

    return this.handleResponse(response, (data) => {
      const page = data as Record<string, unknown>;
      return {
        content: (page.content as unknown[]).map(Organisation.fromJson),
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
   * Get all organisations (use with caution for large datasets).
   */
  async getAll(): Promise<Organisation[]> {
    const response = await this.fetchFn(
      `${this.baseUrl}/api/organisations/all`,
      {
        method: 'GET',
        headers: await this.buildHeaders(),
      },
    );

    return this.handleResponse(response, (data) =>
      (data as unknown[]).map(Organisation.fromJson),
    );
  }

  /**
   * Create a new organisation.
   */
  async create(input: OrganisationCreate): Promise<Organisation> {
    const response = await this.fetchFn(
      `${this.baseUrl}/api/organisations`,
      {
        method: 'POST',
        headers: await this.buildHeaders('application/json'),
        body: JSON.stringify(input.toJson()),
      },
    );

    return this.handleResponse(response, Organisation.fromJson);
  }

  /**
   * Update an existing organisation.
   */
  async update(id: number, input: OrganisationUpdate): Promise<Organisation> {
    const response = await this.fetchFn(
      `${this.baseUrl}/api/organisations/${id}`,
      {
        method: 'PUT',
        headers: await this.buildHeaders('application/json'),
        body: JSON.stringify(input.toJson()),
      },
    );

    return this.handleResponse(response, Organisation.fromJson);
  }

  /**
   * Delete a organisation by ID.
   */
  async delete(id: number): Promise<void> {
    const response = await this.fetchFn(
      `${this.baseUrl}/api/organisations/${id}`,
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