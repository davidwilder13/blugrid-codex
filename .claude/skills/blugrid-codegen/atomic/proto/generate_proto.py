#!/usr/bin/env python3
# atomic/proto/generate_proto.py
"""
Proto Generator Skill

Generates Protocol Buffer service definition with CRUD operations.

Usage:
    python generate_proto.py --schema schema.yaml --output organisation.proto
"""

import argparse
import sys
from pathlib import Path
import yaml

sys.path.insert(0, str(Path(__file__).parent.parent.parent))

from utils.templates import render_template
from utils.naming import to_snake_case, to_camel_case, to_pascal_case


def get_proto_type(kotlin_type: str) -> str:
    """Map Kotlin type to proto type."""
    type_map = {
        "Long": "int64",
        "Int": "int32",
        "Integer": "int32",
        "String": "string",
        "Boolean": "bool",
        "Double": "double",
        "Float": "float",
        "LocalDateTime": "string",  # ISO string format
        "LocalDate": "string",
        "UUID": "string",
        "BigDecimal": "string",
    }
    return type_map.get(kotlin_type, "string")


def generate_proto(schema: dict, output_path: str) -> str:
    """
    Generate a Protocol Buffer service definition file.

    Args:
        schema: Schema dictionary containing entity metadata
        output_path: Path where the generated file will be written

    Returns:
        The generated content as a string
    """
    template_path = Path(__file__).parent.parent.parent / "templates" / "proto" / "service.proto.j2"

    entity_name = schema["name"]
    entity_name_lower = entity_name[0].lower() + entity_name[1:]

    # Generate plural form
    if entity_name.endswith("y"):
        entity_name_lower_plural = entity_name_lower[:-1] + "ies"
    elif entity_name.endswith("s") or entity_name.endswith("x") or entity_name.endswith("ch"):
        entity_name_lower_plural = entity_name_lower + "es"
    else:
        entity_name_lower_plural = entity_name_lower + "s"

    # Process fields with field numbers
    fields = []
    filter_fields = []
    field_num = 2  # Start at 2 (uuid is 1)

    for field in schema.get("fields", []):
        kotlin_type = field.get("type", "String")
        proto_type = get_proto_type(kotlin_type)

        fields.append({
            "name": field["name"],
            "protoType": proto_type,
            "fieldNumber": field_num,
            "updateFieldNumber": field_num + 2,  # Account for id, uuid
            "responseFieldNumber": field_num + 1,  # Account for id, uuid
        })

        # Filter fields (usually repeated for lists)
        if kotlin_type == "Long":
            filter_fields.append({
                "name": f"{field['name']}s",
                "protoType": f"repeated int64",
                "filterFieldNumber": len(filter_fields) + 3,  # After ids, uuids
            })
        elif kotlin_type == "LocalDateTime":
            filter_fields.append({
                "name": f"{field['name']}From",
                "protoType": "string",
                "filterFieldNumber": len(filter_fields) + 3,
            })
            filter_fields.append({
                "name": f"{field['name']}To",
                "protoType": "string",
                "filterFieldNumber": len(filter_fields) + 3,
            })

        field_num += 1

    # Calculate filter pagination field numbers
    filter_base = len(filter_fields) + 3

    context = {
        "packageName": schema.get("packageName", schema.get("package_name", "")),
        "entityName": entity_name,
        "entityNameLower": entity_name_lower,
        "entityNameLowerPlural": entity_name_lower_plural,
        "fields": fields,
        "filterFields": filter_fields,
        "filterPageFieldNumber": filter_base,
        "filterSizeFieldNumber": filter_base + 1,
        "filterSortFieldNumber": filter_base + 2,
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
    parser = argparse.ArgumentParser(description="Generate Protocol Buffer service definition")
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
    generate_proto(schema, args.output)
    print(f"Generated: {args.output}")


if __name__ == "__main__":
    main()
