#!/usr/bin/env python3
# atomic/kotlin/generate_mapping_extensions.py
"""
Mapping Extensions Generator Skill

Generates extension functions for entity <-> resource mapping.

Usage:
    python generate_mapping_extensions.py --schema schema.yaml --output OrganisationMappingExtensions.kt
"""

import argparse
import sys
from pathlib import Path
import yaml

sys.path.insert(0, str(Path(__file__).parent.parent.parent))

from utils.templates import render_template


def generate_mapping_extensions(schema: dict, output_path: str) -> str:
    """
    Generate a Kotlin mapping extensions file.

    Args:
        schema: Schema dictionary containing entity metadata
        output_path: Path where the generated file will be written

    Returns:
        The generated content as a string
    """
    template_path = Path(__file__).parent.parent.parent / "templates" / "kotlin" / "mapping_extensions.kt.j2"

    entity_name = schema["name"]

    # Process fields
    fields = []
    for field in schema.get("fields", []):
        fields.append({
            "name": field["name"],
            "type": field.get("type", "String"),
        })

    context = {
        "packageName": schema.get("packageName", schema.get("package_name", "")),
        "entityName": entity_name,
        "fields": fields,
    }

    content = render_template(str(template_path), context)

    output = Path(output_path)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(content)

    return content


def main():
    parser = argparse.ArgumentParser(description="Generate Kotlin Mapping Extensions")
    parser.add_argument("--schema", "-s", required=True, help="Schema YAML/JSON file")
    parser.add_argument("--output", "-o", required=True, help="Output file path")

    args = parser.parse_args()

    with open(args.schema) as f:
        if args.schema.endswith('.json'):
            import json
            schema = json.load(f)
        else:
            schema = yaml.safe_load(f)

    generate_mapping_extensions(schema, args.output)
    print(f"Generated: {args.output}")


if __name__ == "__main__":
    main()
