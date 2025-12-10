#!/usr/bin/env python3
# atomic/kotlin/generate_db_migration.py
"""
Database Migration Generator Skill

Generates Kotlin-based Flyway migrations for table and view creation.

Usage:
    python generate_db_migration.py --schema schema.yaml --output-table R__5_table_organisation.kt --output-view R__6_view_organisation.kt
"""

import argparse
import sys
from pathlib import Path
import yaml

sys.path.insert(0, str(Path(__file__).parent.parent.parent))

from utils.templates import render_template
from utils.naming import to_snake_case
from utils.config import get_db_domain


def generate_db_migration_table(schema: dict, output_path: str, migration_number: int = 5) -> str:
    """
    Generate a Kotlin Flyway migration for table creation.

    Args:
        schema: Schema dictionary containing entity metadata
        output_path: Path where the generated file will be written
        migration_number: The migration number to use

    Returns:
        The generated content as a string
    """
    template_path = Path(__file__).parent.parent.parent / "templates" / "kotlin" / "db_migration_table.kt.j2"

    entity_name = schema["name"]
    table_name = schema.get("tableName") or schema.get("table_name") or to_snake_case(entity_name)

    # Process columns
    columns = []
    for field in schema.get("fields", []):
        db_domain = field.get("dbDomain") or field.get("db_domain")
        if not db_domain:
            jdl_type = field.get("type", "String")
            db_domain = get_db_domain(jdl_type)

        columns.append({
            "name": field.get("columnName") or field.get("column_name") or to_snake_case(field["name"]),
            "dataType": db_domain,
        })

    # Map resource type to scope
    resource_type = schema.get("resourceType", schema.get("resource_type", "UnscopedResource"))
    scope_map = {
        "UnscopedResource": "unscoped",
        "TenantResource": "tenantScoped",
        "BusinessUnitResource": "businessUnitScoped",
    }
    scope = scope_map.get(resource_type, "unscoped")

    # Process indexes
    indexes = []
    for idx in schema.get("indexes", []):
        indexes.append({
            "name": f"idx_{table_name}_{idx.get('name', '_'.join(idx.get('columns', [])))}",
            "columns": idx.get("columns", []),
            "unique": idx.get("unique", False),
        })

    context = {
        "packageName": schema.get("packageName", schema.get("package_name", "")),
        "tableName": table_name,
        "migrationNumber": migration_number,
        "columns": columns,
        "scope": scope,
        "indexes": indexes,
    }

    content = render_template(str(template_path), context)

    output = Path(output_path)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(content)

    return content


def generate_db_migration_view(schema: dict, output_path: str, migration_number: int = 6) -> str:
    """
    Generate a Kotlin Flyway migration for view and triggers.

    Args:
        schema: Schema dictionary containing entity metadata
        output_path: Path where the generated file will be written
        migration_number: The migration number to use

    Returns:
        The generated content as a string
    """
    template_path = Path(__file__).parent.parent.parent / "templates" / "kotlin" / "db_migration_view.kt.j2"

    entity_name = schema["name"]
    table_name = schema.get("tableName") or schema.get("table_name") or to_snake_case(entity_name)

    context = {
        "packageName": schema.get("packageName", schema.get("package_name", "")),
        "tableName": table_name,
        "migrationNumber": migration_number,
    }

    content = render_template(str(template_path), context)

    output = Path(output_path)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(content)

    return content


def main():
    parser = argparse.ArgumentParser(description="Generate Kotlin DB Migrations")
    parser.add_argument("--schema", "-s", required=True, help="Schema YAML/JSON file")
    parser.add_argument("--output-table", "-t", required=True, help="Output file path for table migration")
    parser.add_argument("--output-view", "-v", required=True, help="Output file path for view migration")
    parser.add_argument("--table-number", type=int, default=5, help="Migration number for table")
    parser.add_argument("--view-number", type=int, default=6, help="Migration number for view")

    args = parser.parse_args()

    with open(args.schema) as f:
        if args.schema.endswith('.json'):
            import json
            schema = json.load(f)
        else:
            schema = yaml.safe_load(f)

    generate_db_migration_table(schema, args.output_table, args.table_number)
    print(f"Generated: {args.output_table}")

    generate_db_migration_view(schema, args.output_view, args.view_number)
    print(f"Generated: {args.output_view}")


if __name__ == "__main__":
    main()
