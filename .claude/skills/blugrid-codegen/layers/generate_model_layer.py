#!/usr/bin/env python3
# layers/generate_model_layer.py
"""
Model Layer Generator Skill

Generates all model layer files (resources, DTOs, interfaces).

Usage:
    python generate_model_layer.py --spec openapi.yaml --output ./output/model/
"""

import argparse
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))

from atomic.kotlin.generate_resource import generate_resource
from utils.naming import to_snake_case
import yaml


def extract_schemas_from_openapi(spec: dict) -> list[dict]:
    """Extract entity schemas from OpenAPI spec."""
    schemas = []
    base_package = spec.get("info", {}).get("x-base-package", "com.example")

    for name, schema in spec.get("components", {}).get("schemas", {}).items():
        # Convert OpenAPI schema to our format
        fields = []
        required_fields = schema.get("required", [])

        for prop_name, prop in schema.get("properties", {}).items():
            if prop.get("x-generated"):
                continue  # Skip id, uuid

            fields.append({
                "name": prop_name,
                "type": map_openapi_type(prop),
                "required": prop_name in required_fields,
                "description": prop.get("description", ""),
                "example": prop.get("example", ""),
            })

        schemas.append({
            "name": name,
            "package_name": f"{base_package}",
            "resource_type": schema.get("x-resource-type", "UnscopedResource"),
            "auditable": schema.get("x-auditable", False),
            "fields": fields,
            "imports": collect_imports(fields),
        })

    return schemas


def map_openapi_type(prop: dict) -> str:
    """Map OpenAPI type to Kotlin type."""
    type_map = {
        ("integer", "int64"): "Long",
        ("integer", "int32"): "Int",
        ("integer", None): "Int",
        ("string", "uuid"): "UUID",
        ("string", "date-time"): "LocalDateTime",
        ("string", "date"): "LocalDate",
        ("string", None): "String",
        ("boolean", None): "Boolean",
        ("number", "double"): "Double",
        ("number", None): "Double",
    }

    openapi_type = prop.get("type")
    openapi_format = prop.get("format")

    # Check for x-kotlin-type override
    if "x-kotlin-type" in prop:
        return prop["x-kotlin-type"]

    return type_map.get((openapi_type, openapi_format), "String")


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
        if field["type"] in import_map:
            imports.add(import_map[field["type"]])

    return sorted(imports)


def generate_model_layer(spec_path: str, output_dir: str) -> list[str]:
    """Generate all model layer files."""
    with open(spec_path) as f:
        spec = yaml.safe_load(f)

    schemas = extract_schemas_from_openapi(spec)
    generated_files = []
    output = Path(output_dir)

    for schema in schemas:
        name = schema["name"]

        # Generate all variants
        variants = [
            ("model", f"{name}.kt"),
            ("create", f"{name}Create.kt"),
            ("update", f"{name}Update.kt"),
            ("interface", f"I{name}.kt"),
        ]

        for variant, filename in variants:
            output_file = output / filename
            generate_resource(schema, variant, str(output_file))
            generated_files.append(str(output_file))
            print(f"Generated: {output_file}")

    return generated_files


def main():
    parser = argparse.ArgumentParser(description="Generate model layer")
    parser.add_argument("--spec", "-s", required=True, help="OpenAPI spec file")
    parser.add_argument("--output", "-o", required=True, help="Output directory")

    args = parser.parse_args()

    files = generate_model_layer(args.spec, args.output)
    print(f"\nGenerated {len(files)} files")


if __name__ == "__main__":
    main()
