#!/usr/bin/env python3
# atomic/kotlin/generate_application.py
"""
Application Generator Skill

Generates Kotlin Micronaut Application entry point.

Usage:
    python generate_application.py --schema schema.yaml --output Application.kt
"""

import argparse
import sys
from pathlib import Path
import yaml

sys.path.insert(0, str(Path(__file__).parent.parent.parent))

from utils.templates import render_template
from utils.naming import to_snake_case


def generate_application(schema: dict, output_path: str) -> str:
    """
    Generate a Kotlin Application entry point file.

    Args:
        schema: Schema dictionary containing application metadata
        output_path: Path where the generated file will be written

    Returns:
        The generated content as a string
    """
    template_path = Path(__file__).parent.parent.parent / "templates" / "kotlin" / "application.kt.j2"

    entity_name = schema["name"]

    # Build context for template
    context = {
        "packageName": schema.get("packageName", schema.get("package_name", "")),
        "apiTitle": schema.get("apiTitle", schema.get("api_title", f"{entity_name} Core Rest API")),
        "apiVersion": schema.get("apiVersion", schema.get("api_version", "0.0")),
        "apiDescription": schema.get("apiDescription", schema.get("api_description", f"{entity_name} Core ")),
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
    parser = argparse.ArgumentParser(description="Generate Kotlin Application entry point")
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
    generate_application(schema, args.output)
    print(f"Generated: {args.output}")


if __name__ == "__main__":
    main()
