"""
Generate complete TypeScript client layer for an entity.

Orchestrates:
- Model classes (types)
- Vanilla API client
- React hooks wrapper
- GraphQL client
"""

import os
import sys
from pathlib import Path
from typing import List

# Support both package import and direct execution
try:
    from ..atomic.typescript.generate_model_class import (
        TypeScriptField,
        generate_model_class,
        generate_create_input,
        generate_update_input,
    )
    from ..atomic.typescript.generate_api_client import (
        ApiClientConfig,
        generate_api_client,
        generate_index_file,
        generate_models_index,
        to_camel_case,
        to_plural,
    )
    from ..atomic.typescript.generate_react_hooks import (
        ReactHooksConfig,
        generate_react_hooks,
        generate_react_hooks_index,
        generate_react_package_json,
    )
    from ..atomic.typescript.generate_graphql_client import (
        GraphQLClientConfig,
        generate_graphql_fragment,
        generate_graphql_queries,
        generate_graphql_mutations,
        generate_codegen_config,
        generate_apollo_client,
        generate_graphql_index,
        generate_graphql_package_json,
    )
    from ..atomic.typescript.generate_openapi_schema import (
        OpenApiField,
        generate_openapi_json,
    )
    from ..atomic.typescript.generate_contract_tests import (
        ContractTestConfig,
        generate_contract_tests,
        generate_vitest_config,
    )
    from ..atomic.typescript.generate_integration_tests import (
        IntegrationTestConfig,
        generate_integration_tests,
        generate_docker_compose_test,
        generate_testcontainers_setup,
    )
    from ..atomic.typescript.generate_test_fixtures import (
        FixtureField,
        generate_test_fixtures,
    )
except ImportError:
    # Direct execution - add parent to path
    sys.path.insert(0, str(Path(__file__).parent.parent))
    from atomic.typescript.generate_model_class import (
        TypeScriptField,
        generate_model_class,
        generate_create_input,
        generate_update_input,
    )
    from atomic.typescript.generate_api_client import (
        ApiClientConfig,
        generate_api_client,
        generate_index_file,
        generate_models_index,
        to_camel_case,
        to_plural,
    )
    from atomic.typescript.generate_react_hooks import (
        ReactHooksConfig,
        generate_react_hooks,
        generate_react_hooks_index,
        generate_react_package_json,
    )
    from atomic.typescript.generate_graphql_client import (
        GraphQLClientConfig,
        generate_graphql_fragment,
        generate_graphql_queries,
        generate_graphql_mutations,
        generate_codegen_config,
        generate_apollo_client,
        generate_graphql_index,
        generate_graphql_package_json,
    )
    from atomic.typescript.generate_openapi_schema import (
        OpenApiField,
        generate_openapi_json,
    )
    from atomic.typescript.generate_contract_tests import (
        ContractTestConfig,
        generate_contract_tests,
        generate_vitest_config,
    )
    from atomic.typescript.generate_integration_tests import (
        IntegrationTestConfig,
        generate_integration_tests,
        generate_docker_compose_test,
        generate_testcontainers_setup,
    )
    from atomic.typescript.generate_test_fixtures import (
        FixtureField,
        generate_test_fixtures,
    )


def generate_typescript_layer(
    entity_name: str,
    fields: List[TypeScriptField],
    output_dir: str,
    include_audit_fields: bool = True,
    generate_tests: bool = True,
    api_docker_image: str = None,
) -> None:
    """
    Generate complete TypeScript client layer.

    Creates:
    - types/ - Model classes
    - api-client/ - Vanilla fetch-based client
    - react-client/ - TanStack Query hooks
    - graphql-client/ - Apollo client with operations
    - api-client/test/ - Contract tests, integration tests, fixtures (if generate_tests=True)

    Args:
        entity_name: PascalCase entity name (e.g., "Organisation")
        fields: List of entity fields
        output_dir: Base output directory
        include_audit_fields: Whether to include createdBy/updatedBy fields
        generate_tests: Whether to generate test files
        api_docker_image: Docker image for integration tests (e.g., "blugrid/organisation-api:latest")
    """
    entity_lower = to_camel_case(entity_name)
    entity_plural = to_plural(entity_name)
    base_path = f"/api/{entity_lower}s"

    output_path = Path(output_dir)

    # === Types Module ===
    types_dir = output_path / "types" / "src" / "models"
    types_dir.mkdir(parents=True, exist_ok=True)

    # Model class
    (types_dir / f"{entity_name}.ts").write_text(
        generate_model_class(entity_name, fields, include_audit_fields)
    )

    # Create input
    (types_dir / f"{entity_name}Create.ts").write_text(
        generate_create_input(entity_name, fields)
    )

    # Update input
    (types_dir / f"{entity_name}Update.ts").write_text(
        generate_update_input(entity_name, fields)
    )

    # Models index
    (types_dir / "index.ts").write_text(
        generate_models_index(entity_name)
    )

    # Types package.json
    (output_path / "types" / "package.json").write_text(f'''{{
  "name": "@blugrid/{entity_lower}-types",
  "version": "0.0.1",
  "description": "TypeScript types for {entity_name}",
  "main": "dist/index.js",
  "module": "dist/index.mjs",
  "types": "dist/index.d.ts",
  "exports": {{
    ".": {{
      "import": "./dist/index.mjs",
      "require": "./dist/index.js",
      "types": "./dist/index.d.ts"
    }}
  }},
  "scripts": {{
    "build": "tsup src/models/index.ts --format cjs,esm --dts",
    "dev": "tsup src/models/index.ts --format cjs,esm --dts --watch",
    "lint": "eslint src/",
    "typecheck": "tsc --noEmit"
  }},
  "devDependencies": {{
    "tsup": "^8.0.0",
    "typescript": "^5.6.0"
  }}
}}
''')

    # === API Client Module ===
    api_client_dir = output_path / "api-client" / "src"
    api_client_dir.mkdir(parents=True, exist_ok=True)

    config = ApiClientConfig(
        entity_name=entity_name,
        entity_name_lower=entity_lower,
        entity_name_plural=entity_lower + "s",
        base_path=base_path,
    )

    # API client class
    (api_client_dir / f"{entity_name}Client.ts").write_text(
        generate_api_client(config)
    )

    # Symlink or reference to models
    (api_client_dir / "models.ts").write_text(
        f"export * from '@blugrid/{entity_lower}-types';\n"
    )

    # Index file
    (api_client_dir / "index.ts").write_text(
        generate_index_file(entity_name)
    )

    # API client package.json
    (output_path / "api-client" / "package.json").write_text(f'''{{
  "name": "@blugrid/{entity_lower}-api-client",
  "version": "0.0.1",
  "description": "API client for {entity_name}",
  "main": "dist/index.js",
  "module": "dist/index.mjs",
  "types": "dist/index.d.ts",
  "exports": {{
    ".": {{
      "import": "./dist/index.mjs",
      "require": "./dist/index.js",
      "types": "./dist/index.d.ts"
    }}
  }},
  "scripts": {{
    "build": "tsup src/index.ts --format cjs,esm --dts",
    "dev": "tsup src/index.ts --format cjs,esm --dts --watch",
    "lint": "eslint src/",
    "typecheck": "tsc --noEmit"
  }},
  "dependencies": {{
    "@blugrid/{entity_lower}-types": "workspace:*"
  }},
  "devDependencies": {{
    "tsup": "^8.0.0",
    "typescript": "^5.6.0"
  }}
}}
''')

    # === React Client Module ===
    react_client_dir = output_path / "react-client" / "src"
    react_client_dir.mkdir(parents=True, exist_ok=True)

    react_config = ReactHooksConfig(
        entity_name=entity_name,
        entity_name_lower=entity_lower,
        entity_name_plural=entity_plural,
    )

    # React hooks
    (react_client_dir / f"use{entity_name}.ts").write_text(
        generate_react_hooks(react_config)
    )

    # Index file
    (react_client_dir / "index.ts").write_text(
        generate_react_hooks_index(react_config)
    )

    # React package.json
    (output_path / "react-client" / "package.json").write_text(
        generate_react_package_json(react_config)
    )

    # === GraphQL Client Module ===
    graphql_client_dir = output_path / "graphql-client" / "src"
    graphql_operations_dir = graphql_client_dir / "operations"
    graphql_operations_dir.mkdir(parents=True, exist_ok=True)

    # Field names for GraphQL (exclude audit fields as they're added separately)
    field_names = [f.name for f in fields]

    graphql_config = GraphQLClientConfig(
        entity_name=entity_name,
        entity_name_lower=entity_lower,
        entity_name_plural=entity_plural,
        fields=field_names,
    )

    # GraphQL fragments
    (graphql_operations_dir / "fragments.graphql").write_text(
        generate_graphql_fragment(graphql_config)
    )

    # GraphQL queries
    (graphql_operations_dir / "queries.graphql").write_text(
        generate_graphql_queries(graphql_config)
    )

    # GraphQL mutations
    (graphql_operations_dir / "mutations.graphql").write_text(
        generate_graphql_mutations(graphql_config)
    )

    # Apollo client setup
    (graphql_client_dir / "client.ts").write_text(
        generate_apollo_client(graphql_config)
    )

    # Index file
    (graphql_client_dir / "index.ts").write_text(
        generate_graphql_index(graphql_config)
    )

    # Codegen config
    (output_path / "graphql-client" / "codegen.ts").write_text(
        generate_codegen_config()
    )

    # GraphQL package.json
    (output_path / "graphql-client" / "package.json").write_text(
        generate_graphql_package_json(graphql_config)
    )

    # Create generated directory placeholder
    (graphql_client_dir / "generated").mkdir(exist_ok=True)
    (graphql_client_dir / "generated" / ".gitkeep").write_text(
        "# Generated files will appear here after running codegen\n"
    )

    # === Tests Module (if enabled) ===
    if generate_tests:
        test_dir = output_path / "api-client" / "test"
        test_dir.mkdir(parents=True, exist_ok=True)

        # Convert TypeScriptField to FixtureField for fixtures
        fixture_fields = [
            FixtureField(name=f.name, jdl_type=f.jdl_type, required=f.required)
            for f in fields
        ]

        # Convert TypeScriptField to OpenApiField for schema
        openapi_fields = [
            OpenApiField(name=f.name, jdl_type=f.jdl_type, required=f.required)
            for f in fields
        ]

        # Test fixtures
        (test_dir / "fixtures.ts").write_text(
            generate_test_fixtures(entity_name, entity_lower, fixture_fields)
        )

        # Contract tests
        contract_config = ContractTestConfig(
            entity_name=entity_name,
            entity_name_lower=entity_lower,
            entity_name_plural=entity_plural,
            base_path=base_path,
        )
        (test_dir / f"{entity_lower}.contract.test.ts").write_text(
            generate_contract_tests(contract_config)
        )

        # OpenAPI schema
        (test_dir / "openapi.json").write_text(
            generate_openapi_json(
                entity_name=entity_name,
                entity_name_lower=entity_lower,
                entity_name_plural=entity_plural,
                fields=openapi_fields,
                base_path=base_path,
            )
        )

        # Vitest config
        (output_path / "api-client" / "vitest.config.ts").write_text(
            generate_vitest_config()
        )

        # Integration tests (if docker image provided)
        if api_docker_image:
            integration_config = IntegrationTestConfig(
                entity_name=entity_name,
                entity_name_lower=entity_lower,
                entity_name_plural=entity_plural,
                base_path=base_path,
                api_docker_image=api_docker_image,
            )

            (test_dir / f"{entity_lower}.integration.test.ts").write_text(
                generate_integration_tests(integration_config)
            )

            (test_dir / "testcontainers-setup.ts").write_text(
                generate_testcontainers_setup(integration_config)
            )

            (output_path / "api-client" / "docker-compose.test.yml").write_text(
                generate_docker_compose_test(integration_config)
            )

        # Update API client package.json with test dependencies
        (output_path / "api-client" / "package.json").write_text(f'''{{
  "name": "@blugrid/{entity_lower}-api-client",
  "version": "0.0.1",
  "description": "API client for {entity_name}",
  "main": "dist/index.js",
  "module": "dist/index.mjs",
  "types": "dist/index.d.ts",
  "exports": {{
    ".": {{
      "import": "./dist/index.mjs",
      "require": "./dist/index.js",
      "types": "./dist/index.d.ts"
    }}
  }},
  "scripts": {{
    "build": "tsup src/index.ts --format cjs,esm --dts",
    "dev": "tsup src/index.ts --format cjs,esm --dts --watch",
    "lint": "eslint src/",
    "typecheck": "tsc --noEmit",
    "test": "vitest run",
    "test:watch": "vitest",
    "test:contract": "vitest run --testNamePattern='Contract'",
    "test:integration": "vitest run --testNamePattern='Integration'"
  }},
  "dependencies": {{
    "@blugrid/{entity_lower}-types": "workspace:*"
  }},
  "devDependencies": {{
    "@faker-js/faker": "^9.0.0",
    "msw": "^2.4.0",
    "testcontainers": "^10.13.0",
    "@testcontainers/postgresql": "^10.13.0",
    "tsup": "^8.0.0",
    "typescript": "^5.6.0",
    "vitest": "^2.1.0"
  }}
}}
''')

    print(f"Generated TypeScript layer for {entity_name} in {output_dir}")


# Example usage
if __name__ == "__main__":
    # Example: Organisation entity
    organisation_fields = [
        TypeScriptField(
            name="parentOrganisationId",
            jdl_type="Long",
            ts_type="number",
            required=True,
        ),
        TypeScriptField(
            name="effectiveTimestamp",
            jdl_type="LocalDateTime",
            ts_type="Date",
            required=True,
        ),
    ]

    generate_typescript_layer(
        entity_name="Organisation",
        fields=organisation_fields,
        output_dir="examples/organisations/generated/core-organisation-api-ts",
        include_audit_fields=True,
        generate_tests=True,
        api_docker_image="blugrid/organisation-api:latest",
    )
