#!/usr/bin/env python3
# atomic/kotlin/generate_assertions.py
"""
Assertions Generator Skill

Generates assertion utilities for testing models.

Usage:
    python generate_assertions.py --schema schema.yaml --output OrganisationAssertions.kt
"""

import argparse
import sys
from pathlib import Path
import yaml

sys.path.insert(0, str(Path(__file__).parent.parent.parent))

from utils.templates import render_template
from utils.naming import to_snake_case


def get_assert_type(field: dict) -> str:
    """Get the assertion type for a field (unwrapped from nullable)."""
    field_type = field.get("type", "String")
    # Most types are used as-is in assertions
    return field_type


def collect_imports(fields: list[dict]) -> list[str]:
    """Collect required imports for fields."""
    import_map = {
        "LocalDateTime": "java.time.LocalDateTime",
        "LocalDate": "java.time.LocalDate",
        "BigDecimal": "java.math.BigDecimal",
    }

    imports = set()
    for field in fields:
        field_type = field.get("type", "")
        if field_type in import_map:
            imports.add(import_map[field_type])

    return sorted(imports)


def generate_assertions(schema: dict, output_path: str) -> str:
    """
    Generate a Kotlin assertions file.

    Args:
        schema: Schema dictionary containing entity metadata
        output_path: Path where the generated file will be written

    Returns:
        The generated content as a string
    """
    template_path = Path(__file__).parent.parent.parent / "templates" / "kotlin" / "assertions.kt.j2"

    entity_name = schema["name"]

    # Process fields to add assert types
    fields = []
    for field in schema.get("fields", []):
        fields.append({
            "name": field["name"],
            "type": field.get("type", "String"),
            "required": field.get("required", False),
            "assertType": get_assert_type(field),
        })

    # Build context for template
    context = {
        "packageName": schema.get("packageName", schema.get("package_name", "")),
        "entityName": entity_name,
        "fields": fields,
        "imports": collect_imports(schema.get("fields", [])),
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
    parser = argparse.ArgumentParser(description="Generate Kotlin Assertions")
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
    generate_assertions(schema, args.output)
    print(f"Generated: {args.output}")


if __name__ == "__main__":
    main()
