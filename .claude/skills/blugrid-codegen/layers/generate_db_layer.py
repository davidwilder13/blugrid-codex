#!/usr/bin/env python3
# layers/generate_db_layer.py
"""
DB Layer Generator Skill

Generates all database layer files (entities, migrations, repositories, services, mappings).

Usage:
    python generate_db_layer.py --spec openapi.yaml --output ./output/core-entity-api-db/
"""

import argparse
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))

from atomic.kotlin.generate_entity import generate_entity
from atomic.kotlin.generate_repository import generate_repository
from atomic.kotlin.generate_specifications import generate_specifications
from atomic.kotlin.generate_mapping_service import generate_mapping_service
from atomic.kotlin.generate_mapping_extensions import generate_mapping_extensions
from atomic.kotlin.generate_query_service_db_impl import generate_query_service_db_impl
from atomic.kotlin.generate_command_service_db_impl import generate_command_service_db_impl
from atomic.kotlin.generate_db_migration import generate_db_migration_table, generate_db_migration_view
from atomic.kotlin.generate_db_integration_test import generate_db_integration_test
from layers.generate_model_layer import extract_schemas_from_openapi
from utils.naming import to_snake_case
from utils.templates import render_template
import yaml


def generate_db_layer(spec_path: str, output_dir: str) -> list[str]:
    """Generate all database layer files."""
    with open(spec_path) as f:
        spec = yaml.safe_load(f)

    schemas = extract_schemas_from_openapi(spec)
    generated_files = []
    output = Path(output_dir)

    # Get base package from spec
    base_package = spec.get("info", {}).get("x-base-package", "net.blugrid.api")
    model_module_path = spec.get("info", {}).get("x-model-module-path", "")
    test_module_path = spec.get("info", {}).get("x-test-module-path", "")

    # Convert package to path
    package_path = base_package.replace(".", "/")

    for i, schema in enumerate(schemas):
        name = schema["name"]
        table_name = to_snake_case(name)

        # Ensure schema has package name
        schema["packageName"] = schema.get("packageName", base_package)

        # === Repository Layer ===

        # Generate entity
        entity_file = output / "src" / "main" / "kotlin" / package_path / "repository" / "model" / f"{name}Entity.kt"
        generate_entity(schema, str(entity_file))
        generated_files.append(str(entity_file))
        print(f"Generated: {entity_file}")

        # Generate repository
        repo_file = output / "src" / "main" / "kotlin" / package_path / "repository" / f"{name}Repository.kt"
        generate_repository(schema, str(repo_file))
        generated_files.append(str(repo_file))
        print(f"Generated: {repo_file}")

        # Generate specifications
        specs_file = output / "src" / "main" / "kotlin" / package_path / "repository" / f"{name}Specifications.kt"
        generate_specifications(schema, str(specs_file))
        generated_files.append(str(specs_file))
        print(f"Generated: {specs_file}")

        # === Mapping Layer ===

        # Generate mapping service
        mapping_svc_file = output / "src" / "main" / "kotlin" / package_path / "mapping" / f"{name}MappingService.kt"
        generate_mapping_service(schema, str(mapping_svc_file))
        generated_files.append(str(mapping_svc_file))
        print(f"Generated: {mapping_svc_file}")

        # Generate mapping extensions
        mapping_ext_file = output / "src" / "main" / "kotlin" / package_path / "mapping" / f"{name}MappingExtensions.kt"
        generate_mapping_extensions(schema, str(mapping_ext_file))
        generated_files.append(str(mapping_ext_file))
        print(f"Generated: {mapping_ext_file}")

        # === Service Layer ===

        # Generate query service DB impl
        query_svc_file = output / "src" / "main" / "kotlin" / package_path / "service" / f"{name}QueryServiceDbImpl.kt"
        generate_query_service_db_impl(schema, str(query_svc_file))
        generated_files.append(str(query_svc_file))
        print(f"Generated: {query_svc_file}")

        # Generate command service DB impl
        cmd_svc_file = output / "src" / "main" / "kotlin" / package_path / "service" / f"{name}CommandServiceDbImpl.kt"
        generate_command_service_db_impl(schema, str(cmd_svc_file))
        generated_files.append(str(cmd_svc_file))
        print(f"Generated: {cmd_svc_file}")

        # === Migration Layer ===

        # Generate table migration (Kotlin-based Flyway)
        table_migration_file = output / "src" / "main" / "kotlin" / package_path / "migration" / f"R__5_table_{table_name}.kt"
        generate_db_migration_table(schema, str(table_migration_file), migration_number=5)
        generated_files.append(str(table_migration_file))
        print(f"Generated: {table_migration_file}")

        # Generate view migration (Kotlin-based Flyway)
        view_migration_file = output / "src" / "main" / "kotlin" / package_path / "migration" / f"R__6_view_{table_name}.kt"
        generate_db_migration_view(schema, str(view_migration_file), migration_number=6)
        generated_files.append(str(view_migration_file))
        print(f"Generated: {view_migration_file}")

        # === Test Layer ===

        # Generate integration test
        test_file = output / "src" / "test" / "kotlin" / package_path / "service" / f"{name}StateServiceDbImplIntegTest.kt"
        generate_db_integration_test(schema, str(test_file))
        generated_files.append(str(test_file))
        print(f"Generated: {test_file}")

    # === Config Files ===

    # Generate logback-test.xml
    logback_template = Path(__file__).parent.parent / "templates" / "config" / "logback-test.xml.j2"
    logback_file = output / "src" / "test" / "resources" / "logback.xml"
    logback_content = render_template(str(logback_template), {})
    logback_file.parent.mkdir(parents=True, exist_ok=True)
    logback_file.write_text(logback_content)
    generated_files.append(str(logback_file))
    print(f"Generated: {logback_file}")

    # Generate build.gradle.kts
    gradle_template = Path(__file__).parent.parent / "templates" / "gradle" / "build.gradle.kts.db.j2"
    gradle_file = output / "build.gradle.kts"
    gradle_context = {
        "modelModulePath": model_module_path,
        "testModulePath": test_module_path,
    }
    gradle_content = render_template(str(gradle_template), gradle_context)
    gradle_file.parent.mkdir(parents=True, exist_ok=True)
    gradle_file.write_text(gradle_content)
    generated_files.append(str(gradle_file))
    print(f"Generated: {gradle_file}")

    # Generate gradle.properties
    props_template = Path(__file__).parent.parent / "templates" / "gradle" / "gradle.properties.j2"
    props_file = output / "gradle.properties"
    props_content = render_template(str(props_template), {"version": "1.0.0"})
    props_file.parent.mkdir(parents=True, exist_ok=True)
    props_file.write_text(props_content)
    generated_files.append(str(props_file))
    print(f"Generated: {props_file}")

    return generated_files


def main():
    parser = argparse.ArgumentParser(description="Generate DB layer")
    parser.add_argument("--spec", "-s", required=True, help="OpenAPI spec file")
    parser.add_argument("--output", "-o", required=True, help="Output directory")

    args = parser.parse_args()

    files = generate_db_layer(args.spec, args.output)
    print(f"\nGenerated {len(files)} files")


if __name__ == "__main__":
    main()
