"""
Generate GraphQL client files for Apollo Client.

Generates:
- GraphQL operation documents (queries, mutations, fragments)
- GraphQL Codegen configuration
- Apollo client setup
"""

from jinja2 import Template
from dataclasses import dataclass
from typing import List


@dataclass
class GraphQLClientConfig:
    entity_name: str
    entity_name_lower: str
    entity_name_plural: str
    fields: List[str]  # Field names to include in queries


FRAGMENT_TEMPLATE = Template('''# {{ entity_name }} GraphQL Fragments
# Auto-generated from JDL entity definition.

fragment {{ entity_name }}Fields on {{ entity_name }}Type {
  id
  uuid
{%- for field in fields %}
  {{ field }}
{%- endfor %}
  createdDate
  createdBy
  updatedDate
  updatedBy
}

fragment {{ entity_name }}Connection on {{ entity_name }}Connection {
  edges {
    node {
      ...{{ entity_name }}Fields
    }
    cursor
  }
  pageInfo {
    hasNextPage
    hasPreviousPage
    startCursor
    endCursor
  }
  totalCount
}
''')


QUERIES_TEMPLATE = Template('''# {{ entity_name }} GraphQL Queries
# Auto-generated from JDL entity definition.

query Get{{ entity_name }}($id: Long!) {
  {{ entity_name_lower }}(id: $id) {
    ...{{ entity_name }}Fields
  }
}

query Get{{ entity_name_plural }}($first: Int, $after: String) {
  {{ entity_name_lower }}s(first: $first, after: $after) {
    ...{{ entity_name }}Connection
  }
}

query GetAll{{ entity_name_plural }} {
  all{{ entity_name_plural }} {
    ...{{ entity_name }}Fields
  }
}
''')


MUTATIONS_TEMPLATE = Template('''# {{ entity_name }} GraphQL Mutations
# Auto-generated from JDL entity definition.

mutation Create{{ entity_name }}($input: {{ entity_name }}CreateInput!) {
  create{{ entity_name }}(input: $input) {
    ...{{ entity_name }}Fields
  }
}

mutation Update{{ entity_name }}($id: Long!, $input: {{ entity_name }}UpdateInput!) {
  update{{ entity_name }}(id: $id, input: $input) {
    ...{{ entity_name }}Fields
  }
}

mutation Delete{{ entity_name }}($id: Long!) {
  delete{{ entity_name }}(id: $id)
}
''')


CODEGEN_CONFIG_TEMPLATE = Template('''import type { CodegenConfig } from '@graphql-codegen/cli';

const config: CodegenConfig = {
  schema: process.env.GRAPHQL_SCHEMA_URL || 'http://localhost:4000/graphql',
  documents: ['src/operations/**/*.graphql'],
  generates: {
    'src/generated/graphql.ts': {
      plugins: [
        'typescript',
        'typescript-operations',
        'typescript-react-apollo',
      ],
      config: {
        withHooks: true,
        withHOC: false,
        withComponent: false,
        scalars: {
          Long: 'number',
          DateTime: 'string',
          Date: 'string',
        },
        enumsAsTypes: true,
        skipTypename: false,
        dedupeFragments: true,
      },
    },
    'src/generated/schema.graphql': {
      plugins: ['schema-ast'],
    },
  },
  hooks: {
    afterAllFileWrite: ['prettier --write'],
  },
};

export default config;
''')


APOLLO_CLIENT_TEMPLATE = Template('''/**
 * Apollo Client configuration for {{ entity_name }} GraphQL API.
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
          {{ entity_name_lower }}: {
            read(_, { args, toReference }) {
              return toReference({
                __typename: '{{ entity_name }}Type',
                id: args?.id,
              });
            },
          },
        },
      },
      {{ entity_name }}Type: {
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
''')


GRAPHQL_INDEX_TEMPLATE = Template('''/**
 * {{ entity_name }} GraphQL Client Package
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
''')


GRAPHQL_PACKAGE_JSON_TEMPLATE = Template('''{
  "name": "@blugrid/{{ entity_name_lower }}-graphql-client",
  "version": "0.0.1",
  "description": "GraphQL client for {{ entity_name }} API",
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
    "build": "pnpm run codegen && tsup src/index.ts --format cjs,esm --dts",
    "dev": "tsup src/index.ts --format cjs,esm --dts --watch",
    "codegen": "graphql-codegen --config codegen.ts",
    "codegen:watch": "graphql-codegen --config codegen.ts --watch",
    "lint": "eslint src/",
    "typecheck": "tsc --noEmit"
  },
  "peerDependencies": {
    "@apollo/client": "^3.0.0",
    "graphql": "^16.0.0",
    "react": "^18.0.0"
  },
  "dependencies": {
    "@blugrid/{{ entity_name_lower }}-types": "workspace:*"
  },
  "devDependencies": {
    "@apollo/client": "^3.11.0",
    "@graphql-codegen/cli": "^5.0.0",
    "@graphql-codegen/schema-ast": "^4.0.0",
    "@graphql-codegen/typescript": "^4.0.0",
    "@graphql-codegen/typescript-operations": "^4.0.0",
    "@graphql-codegen/typescript-react-apollo": "^4.0.0",
    "@types/react": "^18.3.0",
    "graphql": "^16.9.0",
    "react": "^18.3.0",
    "tsup": "^8.0.0",
    "typescript": "^5.6.0"
  }
}
''')


def generate_graphql_fragment(config: GraphQLClientConfig) -> str:
    """Generate GraphQL fragments."""
    return FRAGMENT_TEMPLATE.render(
        entity_name=config.entity_name,
        fields=config.fields,
    )


def generate_graphql_queries(config: GraphQLClientConfig) -> str:
    """Generate GraphQL queries."""
    return QUERIES_TEMPLATE.render(
        entity_name=config.entity_name,
        entity_name_lower=config.entity_name_lower,
        entity_name_plural=config.entity_name_plural,
    )


def generate_graphql_mutations(config: GraphQLClientConfig) -> str:
    """Generate GraphQL mutations."""
    return MUTATIONS_TEMPLATE.render(
        entity_name=config.entity_name,
    )


def generate_codegen_config() -> str:
    """Generate GraphQL Codegen configuration."""
    return CODEGEN_CONFIG_TEMPLATE.render()


def generate_apollo_client(config: GraphQLClientConfig) -> str:
    """Generate Apollo client setup."""
    return APOLLO_CLIENT_TEMPLATE.render(
        entity_name=config.entity_name,
        entity_name_lower=config.entity_name_lower,
    )


def generate_graphql_index(config: GraphQLClientConfig) -> str:
    """Generate the GraphQL client index file."""
    return GRAPHQL_INDEX_TEMPLATE.render(
        entity_name=config.entity_name,
    )


def generate_graphql_package_json(config: GraphQLClientConfig) -> str:
    """Generate package.json for the GraphQL client package."""
    return GRAPHQL_PACKAGE_JSON_TEMPLATE.render(
        entity_name=config.entity_name,
        entity_name_lower=config.entity_name_lower,
    )


# Example usage
if __name__ == "__main__":
    config = GraphQLClientConfig(
        entity_name="Organisation",
        entity_name_lower="organisation",
        entity_name_plural="Organisations",
        fields=["parentOrganisationId", "effectiveTimestamp"],
    )

    print("=== fragments.graphql ===")
    print(generate_graphql_fragment(config))
    print("\n=== queries.graphql ===")
    print(generate_graphql_queries(config))
    print("\n=== mutations.graphql ===")
    print(generate_graphql_mutations(config))
    print("\n=== codegen.ts ===")
    print(generate_codegen_config())
    print("\n=== client.ts ===")
    print(generate_apollo_client(config))
