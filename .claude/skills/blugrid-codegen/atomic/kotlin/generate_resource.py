#!/usr/bin/env python3
# atomic/kotlin/generate_resource.py
"""
Resource Generator Skill

Generates Kotlin resource DTOs (model, create, update, interface variants).

Usage:
    python generate_resource.py --schema schema.yaml --variant model --output Organisation.kt
"""

import argparse
import sys
from pathlib import Path
import yaml

# Add parent paths for imports
sys.path.insert(0, str(Path(__file__).parent.parent.parent))

from utils.templates import render_template
from utils.naming import to_snake_case


def generate_resource(schema: dict, variant: str, output_path: str) -> str:
    """
    Generate a Kotlin resource file.

    Args:
        schema: Schema dictionary containing entity metadata
        variant: One of 'model', 'create', 'update', 'interface'
        output_path: Path where the generated file will be written

    Returns:
        The generated content as a string
    """
    template_path = Path(__file__).parent.parent.parent / "templates" / "kotlin" / "resource.kt.j2"

    # Build context for template
    # The template expects these keys to match the template variables
    context = {
        "variant": variant,
        "packageName": schema.get("packageName", schema.get("package_name", "")),
        "name": schema["name"],
        "nameLower": schema.get("nameLower", schema.get("name_lower", schema["name"][0].lower() + schema["name"][1:])),
        "nameUpperSnake": schema.get("nameUpperSnake", schema.get("name_upper_snake", to_snake_case(schema["name"]).upper())),
        "resourceType": schema.get("resourceType", schema.get("resource_type", "UnscopedResource")),
        "fields": schema.get("fields", []),
        "interfaceFields": schema.get("interfaceFields", schema.get("interface_fields", schema.get("fields", []))),
        "imports": schema.get("imports", []),
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
    parser = argparse.ArgumentParser(description="Generate Kotlin resource DTO")
    parser.add_argument("--schema", "-s", required=True, help="Schema YAML/JSON file")
    parser.add_argument("--variant", "-v", required=True,
                        choices=["model", "create", "update", "interface"],
                        help="Resource variant to generate")
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
    generate_resource(schema, args.variant, args.output)
    print(f"Generated: {args.output}")


if __name__ == "__main__":
    main()
