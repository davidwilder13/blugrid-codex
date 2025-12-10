#!/usr/bin/env python3
# atomic/kotlin/generate_entity.py
"""
Entity Generator Skill

Generates JPA Entity classes.

Usage:
    python generate_entity.py --schema schema.yaml --output OrganisationEntity.kt
"""

import argparse
import sys
from pathlib import Path
import yaml

sys.path.insert(0, str(Path(__file__).parent.parent.parent))

from utils.templates import render_template
from utils.naming import to_snake_case
from utils.config import get_kotlin_type, get_db_domain, get_kotlin_import


def generate_entity(schema: dict, output_path: str) -> str:
    """
    Generate a Kotlin JPA Entity file.

    Args:
        schema: Schema dictionary containing entity metadata
        output_path: Path where the generated file will be written

    Returns:
        The generated content as a string
    """
    template_path = Path(__file__).parent.parent.parent / "templates" / "kotlin" / "entity.kt.j2"

    # Process fields to add column names and type mappings
    fields = []
    imports = set()

    for field in schema.get("fields", []):
        # Get or compute kotlin type
        kotlin_type = field.get("kotlinType") or field.get("kotlin_type")
        if not kotlin_type:
            jdl_type = field.get("type", "String")
            kotlin_type = get_kotlin_type(jdl_type)

        # Add import if needed
        field_import = get_kotlin_import(kotlin_type)
        if field_import:
            imports.add(field_import)

        # Determine nullability
        nullable = not field.get("required", False)

        fields.append({
            "name": field["name"],
            "type": kotlin_type,
            "columnName": field.get("columnName") or field.get("column_name") or to_snake_case(field["name"]),
            "nullable": nullable,
            "updatable": field.get("updatable", True),
        })

    # Determine entity base class
    resource_type = schema.get("resourceType", schema.get("resource_type", "UnscopedResource"))
    extends_map = {
        "UnscopedResource": "UnscopedPersistable",
        "TenantResource": "TenantPersistable",
        "BusinessUnitResource": "BusinessUnitPersistable",
        "UserResource": "UserPersistable",
    }
    extends = extends_map.get(resource_type, "UnscopedPersistable")

    # Build context
    entity_name = schema["name"]
    table_name = schema.get("tableName") or schema.get("table_name") or to_snake_case(entity_name)

    context = {
        "packageName": schema.get("packageName", schema.get("package_name", "")),
        "entityName": entity_name,
        "tableName": table_name,
        "viewName": f"vw_{table_name}",
        "sequenceName": f"{table_name}-sequence",
        "extends": extends,
        "fields": fields,
        "imports": sorted(imports),
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
    parser = argparse.ArgumentParser(description="Generate Kotlin JPA Entity")
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
    generate_entity(schema, args.output)
    print(f"Generated: {args.output}")


if __name__ == "__main__":
    main()
