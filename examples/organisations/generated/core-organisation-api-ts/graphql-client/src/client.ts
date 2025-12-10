/**
 * Apollo Client configuration for Organisation GraphQL API.
 * Auto-generated setup.
 */

import {
  ApolloClient,
  InMemoryCache,
  HttpLink,
  ApolloLink,
  from,
} from '@apollo/client';
import { onError } from '@apollo/client/link/error';

/**
 * Configuration for the Apollo Client.
 */
export interface ApolloClientConfig {
  /** GraphQL endpoint URL */
  uri: string;
  /** Optional function to get auth headers */
  getAuthHeaders?: () => Promise<Record<string, string>> | Record<string, string>;
}

/**
 * Create an Apollo Client instance.
 */
export function createApolloClient(config: ApolloClientConfig): ApolloClient<unknown> {
  // HTTP link for GraphQL endpoint
  const httpLink = new HttpLink({
    uri: config.uri,
  });

  // Auth link for adding headers
  const authLink = new ApolloLink((operation, forward) => {
    return new Observable((observer) => {
      Promise.resolve(config.getAuthHeaders?.() ?? {})
        .then((headers) => {
          operation.setContext(({ headers: existingHeaders = {} }) => ({
            headers: {
              ...existingHeaders,
              ...headers,
            },
          }));
        })
        .then(() => {
          const subscription = forward(operation).subscribe({
            next: observer.next.bind(observer),
            error: observer.error.bind(observer),
            complete: observer.complete.bind(observer),
          });

          return () => subscription.unsubscribe();
        })
        .catch(observer.error.bind(observer));
    });
  });

  // Error handling link
  const errorLink = onError(({ graphQLErrors, networkError }) => {
    if (graphQLErrors) {
      graphQLErrors.forEach(({ message, locations, path }) => {
        console.error(
          `[GraphQL error]: Message: ${message}, Location: ${locations}, Path: ${path}`,
        );
      });
    }
    if (networkError) {
      console.error(`[Network error]: ${networkError}`);
    }
  });

  // Cache configuration with type policies
  const cache = new InMemoryCache({
    typePolicies: {
      Query: {
        fields: {
          organisation: {
            read(_, { args, toReference }) {
              return toReference({
                __typename: 'OrganisationType',
                id: args?.id,
              });
            },
          },
        },
      },
      OrganisationType: {
        keyFields: ['id'],
      },
    },
  });

  return new ApolloClient({
    link: from([errorLink, authLink, httpLink]),
    cache,
    defaultOptions: {
      watchQuery: {
        fetchPolicy: 'cache-and-network',
      },
    },
  });
}

// Import Observable for auth link
import { Observable } from '@apollo/client/utilities';