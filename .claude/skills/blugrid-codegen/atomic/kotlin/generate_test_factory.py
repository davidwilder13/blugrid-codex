#!/usr/bin/env python3
# atomic/kotlin/generate_test_factory.py
"""
Test Factory Generator Skill

Generates test factories for Create, Update, and Resource models.

Usage:
    python generate_test_factory.py --schema schema.yaml --output OrganisationTestFactory.kt
"""

import argparse
import sys
from pathlib import Path
import yaml

sys.path.insert(0, str(Path(__file__).parent.parent.parent))

from utils.templates import render_template
from utils.naming import to_snake_case


def get_default_value(field: dict) -> str:
    """Get default value for a field based on its type."""
    field_type = field.get("type", "String")
    required = field.get("required", False)

    type_defaults = {
        "Long": "Long.random()",
        "Int": "Int.random()",
        "Integer": "Int.random()",
        "String": '"test-${java.util.UUID.randomUUID()}"',
        "Boolean": "false",
        "Double": "0.0",
        "Float": "0.0f",
        "BigDecimal": "java.math.BigDecimal.ZERO",
        "LocalDateTime": "java.time.LocalDateTime.now()",
        "LocalDate": "java.time.LocalDate.now()",
        "UUID": "java.util.UUID.randomUUID()",
        "IdentityID": "IdentityIDRandom.generate()",
        "IdentityUUID": "IdentityUUIDRandom.generate()",
    }

    default = type_defaults.get(field_type, "null")

    # If not required and no default, can be null
    if not required and default == "null":
        return "null"

    return default


def get_random_value(field: dict) -> str:
    """Get random value generator for a field based on its type."""
    field_type = field.get("type", "String")

    type_randoms = {
        "Long": "Long.random()",
        "Int": "Int.random()",
        "Integer": "Int.random()",
        "String": '"random-${java.util.UUID.randomUUID()}"',
        "Boolean": "listOf(true, false).random()",
        "Double": "Double.random()",
        "Float": "Float.random()",
        "BigDecimal": "java.math.BigDecimal(Double.random())",
        "LocalDateTime": "java.time.LocalDateTime.now().minusDays(Long.random(1, 365))",
        "LocalDate": "java.time.LocalDate.now().minusDays(Long.random(1, 365))",
        "UUID": "java.util.UUID.randomUUID()",
        "IdentityID": "IdentityIDRandom.generate()",
        "IdentityUUID": "IdentityUUIDRandom.generate()",
    }

    return type_randoms.get(field_type, "null")


def collect_imports(fields: list[dict]) -> list[str]:
    """Collect required imports for fields."""
    import_map = {
        "UUID": "java.util.UUID",
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


def generate_test_factory(schema: dict, output_path: str) -> str:
    """
    Generate a Kotlin test factory file.

    Args:
        schema: Schema dictionary containing entity metadata
        output_path: Path where the generated file will be written

    Returns:
        The generated content as a string
    """
    template_path = Path(__file__).parent.parent.parent / "templates" / "kotlin" / "test_factory.kt.j2"

    entity_name = schema["name"]
    entity_name_lower = entity_name[0].lower() + entity_name[1:]

    # Process fields to add default and random values
    fields = []
    for field in schema.get("fields", []):
        fields.append({
            "name": field["name"],
            "type": field.get("type", "String"),
            "required": field.get("required", False),
            "defaultValue": get_default_value(field),
            "randomValue": get_random_value(field),
        })

    # Build context for template
    context = {
        "packageName": schema.get("packageName", schema.get("package_name", "")),
        "entityName": entity_name,
        "entityNameLower": entity_name_lower,
        "fields": fields,
        "imports": collect_imports(schema.get("fields", [])),
        "scenarioMethods": schema.get("scenarioMethods", []),
        "scenarios": schema.get("scenarios", [
            {"name": "default", "factoryMethod": "createDefault()"},
        ]),
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
    parser = argparse.ArgumentParser(description="Generate Kotlin Test Factory")
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
    generate_test_factory(schema, args.output)
    print(f"Generated: {args.output}")


if __name__ == "__main__":
    main()
