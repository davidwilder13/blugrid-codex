#!/usr/bin/env python3
# atomic/kotlin/generate_grpc_mapping.py
"""
gRPC Mapping Extensions Generator Skill

Generates gRPC mapping extensions for domain <-> proto conversion.

Usage:
    python generate_grpc_mapping.py --schema schema.yaml --output OrganisationGrpcMappingExtensions.kt
"""

import argparse
import sys
from pathlib import Path
import yaml

sys.path.insert(0, str(Path(__file__).parent.parent.parent))

from utils.templates import render_template
from utils.naming import to_snake_case, to_pascal_case


def get_to_proto_expression(field: dict) -> str:
    """Get expression to convert Kotlin field to proto."""
    kotlin_type = field.get("type", "String")
    field_name = field["name"]

    if kotlin_type == "LocalDateTime":
        return f"{field_name}.toIsoString()"
    elif kotlin_type == "LocalDate":
        return f"{field_name}.toString()"
    elif kotlin_type == "UUID":
        return f"{field_name}.toString()"
    elif kotlin_type == "BigDecimal":
        return f"{field_name}.toString()"
    else:
        return field_name


def get_from_proto_expression(field: dict) -> str:
    """Get expression to convert proto field to Kotlin."""
    kotlin_type = field.get("type", "String")
    field_name = field["name"]

    if kotlin_type == "LocalDateTime":
        return f"{field_name}.parseAsLocalDateTime()"
    elif kotlin_type == "LocalDate":
        return f"java.time.LocalDate.parse({field_name})"
    elif kotlin_type == "UUID":
        return f"java.util.UUID.fromString({field_name})"
    elif kotlin_type == "BigDecimal":
        return f"java.math.BigDecimal({field_name})"
    else:
        return field_name


def generate_grpc_mapping(schema: dict, output_path: str) -> str:
    """
    Generate a Kotlin gRPC mapping extensions file.

    Args:
        schema: Schema dictionary containing entity metadata
        output_path: Path where the generated file will be written

    Returns:
        The generated content as a string
    """
    template_path = Path(__file__).parent.parent.parent / "templates" / "kotlin" / "grpc_mapping_extensions.kt.j2"

    entity_name = schema["name"]
    entity_name_lower = entity_name[0].lower() + entity_name[1:]

    # Generate plural form
    if entity_name.endswith("y"):
        entity_name_plural = entity_name[:-1] + "ies"
    else:
        entity_name_plural = entity_name + "s"

    # Process fields
    fields = []
    for field in schema.get("fields", []):
        fields.append({
            "name": field["name"],
            "namePascal": to_pascal_case(field["name"]),
            "type": field.get("type", "String"),
            "toProtoExpression": get_to_proto_expression(field),
            "fromProtoExpression": get_from_proto_expression(field),
        })

    context = {
        "packageName": schema.get("packageName", schema.get("package_name", "")),
        "entityName": entity_name,
        "entityNameLower": entity_name_lower,
        "entityNamePlural": entity_name_plural,
        "fields": fields,
    }

    content = render_template(str(template_path), context)

    output = Path(output_path)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(content)

    return content


def main():
    parser = argparse.ArgumentParser(description="Generate Kotlin gRPC Mapping Extensions")
    parser.add_argument("--schema", "-s", required=True, help="Schema YAML/JSON file")
    parser.add_argument("--output", "-o", required=True, help="Output file path")

    args = parser.parse_args()

    with open(args.schema) as f:
        if args.schema.endswith('.json'):
            import json
            schema = json.load(f)
        else:
            schema = yaml.safe_load(f)

    generate_grpc_mapping(schema, args.output)
    print(f"Generated: {args.output}")


if __name__ == "__main__":
    main()
