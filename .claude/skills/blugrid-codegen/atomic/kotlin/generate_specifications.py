#!/usr/bin/env python3
# atomic/kotlin/generate_specifications.py
"""
Specifications Generator Skill

Generates JPA Specification builders for filtering.

Usage:
    python generate_specifications.py --schema schema.yaml --output OrganisationSpecifications.kt
"""

import argparse
import sys
from pathlib import Path
import yaml

sys.path.insert(0, str(Path(__file__).parent.parent.parent))

from utils.templates import render_template
from utils.naming import to_snake_case


def get_specification_operation(field_type: str) -> str:
    """Get the specification operation based on field type."""
    if field_type in ["LocalDateTime", "LocalDate", "Instant"]:
        return None  # These need special handling (from/to ranges)
    return "`in`"


def generate_specifications(schema: dict, output_path: str) -> str:
    """
    Generate a Kotlin Specifications file.

    Args:
        schema: Schema dictionary containing entity metadata
        output_path: Path where the generated file will be written

    Returns:
        The generated content as a string
    """
    template_path = Path(__file__).parent.parent.parent / "templates" / "kotlin" / "specifications.kt.j2"

    entity_name = schema["name"]
    imports = set()
    specifications = []

    for field in schema.get("fields", []):
        field_name = field["name"]
        field_type = field.get("type", "String")

        # Handle different field types
        if field_type in ["LocalDateTime", "Instant"]:
            # Date/time fields get from/to specifications
            imports.add("java.time.LocalDateTime" if field_type == "LocalDateTime" else "java.time.Instant")
            specifications.append({
                "name": f"{field_name}From",
                "paramName": "from",
                "paramType": f"java.time.{field_type}",
                "fieldName": field_name,
                "operation": "greaterThanOrEqualTo",
                "filterField": f"{field_name}From" if field_name.endswith("Timestamp") else f"{field_name[:-9]}From" if field_name.endswith("Timestamp") else f"effectiveFrom",
            })
            specifications.append({
                "name": f"{field_name}To",
                "paramName": "to",
                "paramType": f"java.time.{field_type}",
                "fieldName": field_name,
                "operation": "lessThanOrEqualTo",
                "filterField": f"{field_name}To" if field_name.endswith("Timestamp") else f"effectiveTo",
            })
        elif field_type == "Long" and field_name.endswith("Id"):
            # Foreign key fields get In specification
            filter_field = f"{field_name[:-2]}Ids" if field_name.endswith("Id") else f"{field_name}s"
            specifications.append({
                "name": f"{field_name}In",
                "paramName": "ids",
                "paramType": "List<Long>",
                "fieldName": field_name,
                "operation": "`in`",
                "filterField": filter_field,
            })

    context = {
        "packageName": schema.get("packageName", schema.get("package_name", "")),
        "entityName": entity_name,
        "specifications": specifications,
        "imports": sorted(imports),
    }

    content = render_template(str(template_path), context)

    output = Path(output_path)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(content)

    return content


def main():
    parser = argparse.ArgumentParser(description="Generate Kotlin Specifications")
    parser.add_argument("--schema", "-s", required=True, help="Schema YAML/JSON file")
    parser.add_argument("--output", "-o", required=True, help="Output file path")

    args = parser.parse_args()

    with open(args.schema) as f:
        if args.schema.endswith('.json'):
            import json
            schema = json.load(f)
        else:
            schema = yaml.safe_load(f)

    generate_specifications(schema, args.output)
    print(f"Generated: {args.output}")


if __name__ == "__main__":
    main()
