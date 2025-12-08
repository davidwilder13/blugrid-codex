#!/usr/bin/env python3
# atomic/sql/generate_table.py
"""
SQL Table Generator Skill

Generates PostgreSQL table definitions with inheritance.

Usage:
    python generate_table.py --schema schema.yaml --output V1__create_organisation.sql
"""

import argparse
import sys
from pathlib import Path
import yaml

sys.path.insert(0, str(Path(__file__).parent.parent.parent))

from utils.templates import render_template
from utils.naming import to_snake_case
from utils.config import get_db_domain


def generate_table(schema: dict, output_path: str) -> str:
    """
    Generate PostgreSQL table definition.

    Args:
        schema: Schema dictionary containing entity metadata
        output_path: Path where the generated SQL will be written

    Returns:
        The generated SQL content as a string
    """
    template_path = Path(__file__).parent.parent.parent / "templates" / "sql" / "table.sql.j2"

    # Process fields to add column names and DB domain types
    columns = []
    for field in schema.get("fields", []):
        # Get or compute DB domain type
        db_domain = field.get("dbDomain") or field.get("db_domain")
        if not db_domain:
            jdl_type = field.get("type", "String")
            db_domain = get_db_domain(jdl_type)

        # Get default value if specified
        default_value = field.get("defaultValue") or field.get("default_value")

        columns.append({
            "name": field.get("columnName") or field.get("column_name") or to_snake_case(field["name"]),
            "dataType": db_domain,
            "defaultValue": default_value,
        })

    # Map resource type to scope
    resource_type = schema.get("resourceType", schema.get("resource_type", "UnscopedResource"))
    scope_map = {
        "UnscopedResource": "unscoped",
        "TenantResource": "tenantScoped",
        "BusinessUnitResource": "businessUnitScoped",
        "UserResource": "businessUnitScoped",  # User resources are typically business unit scoped
        "GenericResource": "generic",
    }
    scope = scope_map.get(resource_type, "unscoped")

    # Get table name
    entity_name = schema["name"]
    table_name = schema.get("tableName") or schema.get("table_name") or to_snake_case(entity_name)

    # Process indexes
    indexes = []
    for idx in schema.get("indexes", []):
        indexes.append({
            "name": idx.get("name", "_".join(idx.get("columns", []))),
            "columns": idx.get("columns", []),
            "unique": idx.get("unique", False),
        })

    # Build context
    context = {
        "name": table_name,
        "scope": scope,
        "columns": columns,
        "indexes": indexes,
        "isPartitioned": schema.get("isPartitioned", schema.get("is_partitioned", False)),
        "baseTables": schema.get("baseTables", schema.get("base_tables", [])),
    }

    # Render template
    content = render_template(str(template_path), context)

    # Write output
    output = Path(output_path)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(content)

    return content


def main():
    """CLI entry point."""
    parser = argparse.ArgumentParser(description="Generate PostgreSQL table")
    parser.add_argument("--schema", "-s", required=True, help="Schema YAML/JSON file")
    parser.add_argument("--output", "-o", required=True, help="Output file path")

    args = parser.parse_args()

    # Load schema
    with open(args.schema) as f:
        if args.schema.endswith('.json'):
            import json
            schema = json.load(f)
        else:
            schema = yaml.safe_load(f)

    # Generate
    generate_table(schema, args.output)
    print(f"Generated: {args.output}")


if __name__ == "__main__":
    main()
