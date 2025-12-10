import type { CodegenConfig } from '@graphql-codegen/cli';

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