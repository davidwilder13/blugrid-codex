#!/usr/bin/env python3
# atomic/kotlin/generate_controller.py
"""
Controller Generator Skill

Generates Kotlin REST controllers with full CRUD operations.

Usage:
    python generate_controller.py --schema schema.yaml --output OrganisationController.kt
"""

import argparse
import sys
from pathlib import Path
import yaml

sys.path.insert(0, str(Path(__file__).parent.parent.parent))

from utils.templates import render_template
from utils.naming import to_snake_case, to_camel_case


def generate_controller(schema: dict, output_path: str) -> str:
    """
    Generate a Kotlin REST controller file.

    Args:
        schema: Schema dictionary containing entity metadata
        output_path: Path where the generated file will be written

    Returns:
        The generated content as a string
    """
    template_path = Path(__file__).parent.parent.parent / "templates" / "kotlin" / "controller.kt.j2"

    entity_name = schema["name"]
    entity_name_lower = entity_name[0].lower() + entity_name[1:]

    # Generate plural form (simple English pluralization)
    entity_name_plural = schema.get("namePlural", schema.get("name_plural"))
    if not entity_name_plural:
        if entity_name.endswith("y"):
            entity_name_plural = entity_name[:-1] + "ies"
        elif entity_name.endswith("s") or entity_name.endswith("x") or entity_name.endswith("ch"):
            entity_name_plural = entity_name + "es"
        else:
            entity_name_plural = entity_name + "s"

    # Build context for template
    context = {
        "packageName": schema.get("packageName", schema.get("package_name", "")),
        "entityName": entity_name,
        "entityNameLower": entity_name_lower,
        "entityNamePlural": entity_name_plural,
        "basePath": schema.get("basePath", schema.get("base_path", to_snake_case(entity_name_plural).replace("_", "-"))),
        "tagName": entity_name_plural,
        "tagDescription": f"{entity_name} management operations",
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
    parser = argparse.ArgumentParser(description="Generate Kotlin REST Controller")
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
    generate_controller(schema, args.output)
    print(f"Generated: {args.output}")


if __name__ == "__main__":
    main()
