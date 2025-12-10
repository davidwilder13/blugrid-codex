/**
 * Organisation React Hooks
 * TanStack Query hooks wrapping the vanilla API client.
 * Auto-generated from JDL entity definition.
 */

import { useQuery, useMutation, useQueryClient, UseQueryOptions, UseMutationOptions } from '@tanstack/react-query';
import {
  Organisation,
  OrganisationCreate,
  OrganisationUpdate,
  OrganisationClient,
  Page,
  PageParams,
  ApiError,
} from '@blugrid/organisation-api-client';

/**
 * Query keys for Organisation queries.
 * Use these for cache invalidation and prefetching.
 */
export const organisationKeys = {
  all: ['Organisations'] as const,
  lists: () => [...organisationKeys.all, 'list'] as const,
  list: (params: PageParams) => [...organisationKeys.lists(), params] as const,
  details: () => [...organisationKeys.all, 'detail'] as const,
  detail: (id: number) => [...organisationKeys.details(), id] as const,
  detailByUuid: (uuid: string) => [...organisationKeys.details(), 'uuid', uuid] as const,
};

/**
 * Context type for providing the client instance.
 */
export interface OrganisationHooksContext {
  client: OrganisationClient;
}

/**
 * Hook to get a single organisation by ID.
 */
export function useOrganisation(
  ctx: OrganisationHooksContext,
  id: number,
  options?: Omit<UseQueryOptions<Organisation, ApiError>, 'queryKey' | 'queryFn'>,
) {
  return useQuery({
    queryKey: organisationKeys.detail(id),
    queryFn: () => ctx.client.getById(id),
    ...options,
  });
}

/**
 * Hook to get a single organisation by UUID.
 */
export function useOrganisationByUuid(
  ctx: OrganisationHooksContext,
  uuid: string,
  options?: Omit<UseQueryOptions<Organisation, ApiError>, 'queryKey' | 'queryFn'>,
) {
  return useQuery({
    queryKey: organisationKeys.detailByUuid(uuid),
    queryFn: () => ctx.client.getByUuid(uuid),
    ...options,
  });
}

/**
 * Hook to get a paginated list of Organisations.
 */
export function useOrganisations(
  ctx: OrganisationHooksContext,
  params: PageParams = {},
  options?: Omit<UseQueryOptions<Page<Organisation>, ApiError>, 'queryKey' | 'queryFn'>,
) {
  return useQuery({
    queryKey: organisationKeys.list(params),
    queryFn: () => ctx.client.getPage(params),
    ...options,
  });
}

/**
 * Hook to get all Organisations.
 * Use with caution for large datasets.
 */
export function useAllOrganisations(
  ctx: OrganisationHooksContext,
  options?: Omit<UseQueryOptions<Organisation[], ApiError>, 'queryKey' | 'queryFn'>,
) {
  return useQuery({
    queryKey: organisationKeys.all,
    queryFn: () => ctx.client.getAll(),
    ...options,
  });
}

/**
 * Hook to create a new organisation.
 */
export function useCreateOrganisation(
  ctx: OrganisationHooksContext,
  options?: Omit<UseMutationOptions<Organisation, ApiError, OrganisationCreate>, 'mutationFn'>,
) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (input: OrganisationCreate) => ctx.client.create(input),
    onSuccess: () => {
      // Invalidate list queries to refetch
      queryClient.invalidateQueries({ queryKey: organisationKeys.lists() });
    },
    ...options,
  });
}

/**
 * Hook to update an existing organisation.
 */
export function useUpdateOrganisation(
  ctx: OrganisationHooksContext,
  options?: Omit<UseMutationOptions<Organisation, ApiError, { id: number; input: OrganisationUpdate }>, 'mutationFn'>,
) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, input }: { id: number; input: OrganisationUpdate }) =>
      ctx.client.update(id, input),
    onSuccess: (data, { id }) => {
      // Update the cache for this specific item
      queryClient.setQueryData(organisationKeys.detail(id), data);
      // Invalidate list queries
      queryClient.invalidateQueries({ queryKey: organisationKeys.lists() });
    },
    ...options,
  });
}

/**
 * Hook to delete a organisation.
 */
export function useDeleteOrganisation(
  ctx: OrganisationHooksContext,
  options?: Omit<UseMutationOptions<void, ApiError, number>, 'mutationFn'>,
) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => ctx.client.delete(id),
    onSuccess: (_, id) => {
      // Remove from cache
      queryClient.removeQueries({ queryKey: organisationKeys.detail(id) });
      // Invalidate list queries
      queryClient.invalidateQueries({ queryKey: organisationKeys.lists() });
    },
    ...options,
  });
}