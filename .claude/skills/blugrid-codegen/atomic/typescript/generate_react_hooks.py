"""
Generate React hooks wrapper for the vanilla API client.

Uses TanStack Query (React Query) for:
- Queries with caching, refetching, stale-while-revalidate
- Mutations with optimistic updates support
- Proper TypeScript types
"""

from jinja2 import Template
from dataclasses import dataclass


@dataclass
class ReactHooksConfig:
    entity_name: str
    entity_name_lower: str
    entity_name_plural: str


REACT_HOOKS_TEMPLATE = Template('''/**
 * {{ entity_name }} React Hooks
 * TanStack Query hooks wrapping the vanilla API client.
 * Auto-generated from JDL entity definition.
 */

import { useQuery, useMutation, useQueryClient, UseQueryOptions, UseMutationOptions } from '@tanstack/react-query';
import {
  {{ entity_name }},
  {{ entity_name }}Create,
  {{ entity_name }}Update,
  {{ entity_name }}Client,
  Page,
  PageParams,
  ApiError,
} from '@blugrid/{{ entity_name_lower }}-api-client';

/**
 * Query keys for {{ entity_name }} queries.
 * Use these for cache invalidation and prefetching.
 */
export const {{ entity_name_lower }}Keys = {
  all: ['{{ entity_name_plural }}'] as const,
  lists: () => [...{{ entity_name_lower }}Keys.all, 'list'] as const,
  list: (params: PageParams) => [...{{ entity_name_lower }}Keys.lists(), params] as const,
  details: () => [...{{ entity_name_lower }}Keys.all, 'detail'] as const,
  detail: (id: number) => [...{{ entity_name_lower }}Keys.details(), id] as const,
  detailByUuid: (uuid: string) => [...{{ entity_name_lower }}Keys.details(), 'uuid', uuid] as const,
};

/**
 * Context type for providing the client instance.
 */
export interface {{ entity_name }}HooksContext {
  client: {{ entity_name }}Client;
}

/**
 * Hook to get a single {{ entity_name_lower }} by ID.
 */
export function use{{ entity_name }}(
  ctx: {{ entity_name }}HooksContext,
  id: number,
  options?: Omit<UseQueryOptions<{{ entity_name }}, ApiError>, 'queryKey' | 'queryFn'>,
) {
  return useQuery({
    queryKey: {{ entity_name_lower }}Keys.detail(id),
    queryFn: () => ctx.client.getById(id),
    ...options,
  });
}

/**
 * Hook to get a single {{ entity_name_lower }} by UUID.
 */
export function use{{ entity_name }}ByUuid(
  ctx: {{ entity_name }}HooksContext,
  uuid: string,
  options?: Omit<UseQueryOptions<{{ entity_name }}, ApiError>, 'queryKey' | 'queryFn'>,
) {
  return useQuery({
    queryKey: {{ entity_name_lower }}Keys.detailByUuid(uuid),
    queryFn: () => ctx.client.getByUuid(uuid),
    ...options,
  });
}

/**
 * Hook to get a paginated list of {{ entity_name_plural }}.
 */
export function use{{ entity_name_plural }}(
  ctx: {{ entity_name }}HooksContext,
  params: PageParams = {},
  options?: Omit<UseQueryOptions<Page<{{ entity_name }}>, ApiError>, 'queryKey' | 'queryFn'>,
) {
  return useQuery({
    queryKey: {{ entity_name_lower }}Keys.list(params),
    queryFn: () => ctx.client.getPage(params),
    ...options,
  });
}

/**
 * Hook to get all {{ entity_name_plural }}.
 * Use with caution for large datasets.
 */
export function useAll{{ entity_name_plural }}(
  ctx: {{ entity_name }}HooksContext,
  options?: Omit<UseQueryOptions<{{ entity_name }}[], ApiError>, 'queryKey' | 'queryFn'>,
) {
  return useQuery({
    queryKey: {{ entity_name_lower }}Keys.all,
    queryFn: () => ctx.client.getAll(),
    ...options,
  });
}

/**
 * Hook to create a new {{ entity_name_lower }}.
 */
export function useCreate{{ entity_name }}(
  ctx: {{ entity_name }}HooksContext,
  options?: Omit<UseMutationOptions<{{ entity_name }}, ApiError, {{ entity_name }}Create>, 'mutationFn'>,
) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (input: {{ entity_name }}Create) => ctx.client.create(input),
    onSuccess: () => {
      // Invalidate list queries to refetch
      queryClient.invalidateQueries({ queryKey: {{ entity_name_lower }}Keys.lists() });
    },
    ...options,
  });
}

/**
 * Hook to update an existing {{ entity_name_lower }}.
 */
export function useUpdate{{ entity_name }}(
  ctx: {{ entity_name }}HooksContext,
  options?: Omit<UseMutationOptions<{{ entity_name }}, ApiError, { id: number; input: {{ entity_name }}Update }>, 'mutationFn'>,
) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, input }: { id: number; input: {{ entity_name }}Update }) =>
      ctx.client.update(id, input),
    onSuccess: (data, { id }) => {
      // Update the cache for this specific item
      queryClient.setQueryData({{ entity_name_lower }}Keys.detail(id), data);
      // Invalidate list queries
      queryClient.invalidateQueries({ queryKey: {{ entity_name_lower }}Keys.lists() });
    },
    ...options,
  });
}

/**
 * Hook to delete a {{ entity_name_lower }}.
 */
export function useDelete{{ entity_name }}(
  ctx: {{ entity_name }}HooksContext,
  options?: Omit<UseMutationOptions<void, ApiError, number>, 'mutationFn'>,
) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => ctx.client.delete(id),
    onSuccess: (_, id) => {
      // Remove from cache
      queryClient.removeQueries({ queryKey: {{ entity_name_lower }}Keys.detail(id) });
      // Invalidate list queries
      queryClient.invalidateQueries({ queryKey: {{ entity_name_lower }}Keys.lists() });
    },
    ...options,
  });
}
''')


REACT_HOOKS_INDEX_TEMPLATE = Template('''/**
 * {{ entity_name }} React Hooks Package
 * Auto-generated exports.
 */

export {
  {{ entity_name_lower }}Keys,
  use{{ entity_name }},
  use{{ entity_name }}ByUuid,
  use{{ entity_name_plural }},
  useAll{{ entity_name_plural }},
  useCreate{{ entity_name }},
  useUpdate{{ entity_name }},
  useDelete{{ entity_name }},
} from './use{{ entity_name }}';

export type { {{ entity_name }}HooksContext } from './use{{ entity_name }}';

// Re-export from api-client for convenience
export {
  {{ entity_name }},
  {{ entity_name }}Create,
  {{ entity_name }}Update,
  {{ entity_name }}Client,
  {{ entity_name }}ClientConfig,
  ApiError,
  Page,
  PageParams,
} from '@blugrid/{{ entity_name_lower }}-api-client';
''')


PACKAGE_JSON_TEMPLATE = Template('''{
  "name": "@blugrid/{{ entity_name_lower }}-react-client",
  "version": "0.0.1",
  "description": "React hooks for {{ entity_name }} API",
  "main": "dist/index.js",
  "module": "dist/index.mjs",
  "types": "dist/index.d.ts",
  "exports": {
    ".": {
      "import": "./dist/index.mjs",
      "require": "./dist/index.js",
      "types": "./dist/index.d.ts"
    }
  },
  "scripts": {
    "build": "tsup src/index.ts --format cjs,esm --dts",
    "dev": "tsup src/index.ts --format cjs,esm --dts --watch",
    "lint": "eslint src/",
    "typecheck": "tsc --noEmit"
  },
  "peerDependencies": {
    "@tanstack/react-query": "^5.0.0",
    "react": "^18.0.0"
  },
  "dependencies": {
    "@blugrid/{{ entity_name_lower }}-api-client": "workspace:*"
  },
  "devDependencies": {
    "@tanstack/react-query": "^5.59.0",
    "@types/react": "^18.3.0",
    "react": "^18.3.0",
    "tsup": "^8.0.0",
    "typescript": "^5.6.0"
  }
}
''')


def generate_react_hooks(config: ReactHooksConfig) -> str:
    """Generate React hooks for the entity."""
    return REACT_HOOKS_TEMPLATE.render(
        entity_name=config.entity_name,
        entity_name_lower=config.entity_name_lower,
        entity_name_plural=config.entity_name_plural,
    )


def generate_react_hooks_index(config: ReactHooksConfig) -> str:
    """Generate the React hooks index file."""
    return REACT_HOOKS_INDEX_TEMPLATE.render(
        entity_name=config.entity_name,
        entity_name_lower=config.entity_name_lower,
        entity_name_plural=config.entity_name_plural,
    )


def generate_react_package_json(config: ReactHooksConfig) -> str:
    """Generate package.json for the React hooks package."""
    return PACKAGE_JSON_TEMPLATE.render(
        entity_name=config.entity_name,
        entity_name_lower=config.entity_name_lower,
    )


# Example usage
if __name__ == "__main__":
    config = ReactHooksConfig(
        entity_name="Organisation",
        entity_name_lower="organisation",
        entity_name_plural="Organisations",
    )

    print("=== useOrganisation.ts ===")
    print(generate_react_hooks(config))
    print("\n=== index.ts ===")
    print(generate_react_hooks_index(config))
    print("\n=== package.json ===")
    print(generate_react_package_json(config))
