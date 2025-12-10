#!/usr/bin/env python3
# atomic/kotlin/generate_repository.py
"""
Repository Generator Skill

Generates Micronaut Data repository interface.

Usage:
    python generate_repository.py --schema schema.yaml --output OrganisationRepository.kt
"""

import argparse
import sys
from pathlib import Path
import yaml

sys.path.insert(0, str(Path(__file__).parent.parent.parent))

from utils.templates import render_template


def generate_repository(schema: dict, output_path: str) -> str:
    """
    Generate a Kotlin repository interface.

    Args:
        schema: Schema dictionary containing entity metadata
        output_path: Path where the generated file will be written

    Returns:
        The generated content as a string
    """
    template_path = Path(__file__).parent.parent.parent / "templates" / "kotlin" / "repository.kt.j2"

    entity_name = schema["name"]

    context = {
        "packageName": schema.get("packageName", schema.get("package_name", "")),
        "entityName": entity_name,
    }

    content = render_template(str(template_path), context)

    output = Path(output_path)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(content)

    return content


def main():
    parser = argparse.ArgumentParser(description="Generate Kotlin Repository")
    parser.add_argument("--schema", "-s", required=True, help="Schema YAML/JSON file")
    parser.add_argument("--output", "-o", required=True, help="Output file path")

    args = parser.parse_args()

    with open(args.schema) as f:
        if args.schema.endswith('.json'):
            import json
            schema = json.load(f)
        else:
            schema = yaml.safe_load(f)

    generate_repository(schema, args.output)
    print(f"Generated: {args.output}")


if __name__ == "__main__":
    main()
