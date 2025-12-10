/**
 * Organisation React Hooks Package
 * Auto-generated exports.
 */

export {
  organisationKeys,
  useOrganisation,
  useOrganisationByUuid,
  useOrganisations,
  useAllOrganisations,
  useCreateOrganisation,
  useUpdateOrganisation,
  useDeleteOrganisation,
} from './useOrganisation';

export type { OrganisationHooksContext } from './useOrganisation';

// Re-export from api-client for convenience
export {
  Organisation,
  OrganisationCreate,
  OrganisationUpdate,
  OrganisationClient,
  OrganisationClientConfig,
  ApiError,
  Page,
  PageParams,
} from '@blugrid/organisation-api-client';