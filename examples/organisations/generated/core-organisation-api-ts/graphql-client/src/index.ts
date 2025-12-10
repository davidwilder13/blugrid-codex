/**
 * Organisation GraphQL Client Package
 * Auto-generated exports.
 */

// Generated types and hooks from GraphQL Codegen
export * from './generated/graphql';

// Apollo client setup
export { createApolloClient } from './client';
export type { ApolloClientConfig } from './client';

// Re-export Apollo hooks for convenience
export {
  useQuery,
  useMutation,
  useLazyQuery,
  useSubscription,
  ApolloProvider,
} from '@apollo/client';