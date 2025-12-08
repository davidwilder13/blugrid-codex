#!/usr/bin/env python3
# parsers/openapi_validator.py
"""
OpenAPI Validator Skill

Validates OpenAPI specs and checks custom x- extensions.

Usage:
    python openapi_validator.py --input spec.yaml
"""

import argparse
import sys
from pathlib import Path
import yaml


def load_extension_schema() -> dict:
    """Load the extension schema definition."""
    config_dir = Path(__file__).parent.parent / "config"
    schema_path = config_dir / "openapi-extensions.yaml"
    try:
        with open(schema_path) as f:
            schema = yaml.safe_load(f)
            if schema is None:
                raise ValueError(f"Extension schema file is empty or malformed: {schema_path}")
            return schema
    except FileNotFoundError:
        raise FileNotFoundError(f"Extension schema not found: {schema_path}")
    except yaml.YAMLError as e:
        raise ValueError(f"Failed to parse extension schema: {e}")


def validate_openapi(spec_path: str) -> tuple[bool, list[str]]:
    """Validate OpenAPI spec and return (valid, errors)."""
    errors = []

    try:
        with open(spec_path) as f:
            spec = yaml.safe_load(f)
    except FileNotFoundError:
        errors.append(f"OpenAPI spec file not found: {spec_path}")
        return False, errors
    except yaml.YAMLError as e:
        errors.append(f"Failed to parse OpenAPI spec: {e}")
        return False, errors

    # Check if spec is empty or malformed
    if spec is None:
        errors.append("OpenAPI spec file is empty or contains only whitespace")
        return False, errors

    # Check required fields
    if "openapi" not in spec:
        errors.append("Missing 'openapi' version field")

    if "info" not in spec:
        errors.append("Missing 'info' section")
    else:
        if "x-base-package" not in spec["info"]:
            errors.append("Missing 'x-base-package' in info section")

    if "components" not in spec or "schemas" not in spec.get("components", {}):
        errors.append("Missing 'components.schemas' section")

    # Validate schemas
    extension_schema = load_extension_schema()
    valid_resource_types = extension_schema["extensions"]["schema"]["x-resource-type"]["enum"]

    for name, schema in spec.get("components", {}).get("schemas", {}).items():
        resource_type = schema.get("x-resource-type", "UnscopedResource")
        if resource_type not in valid_resource_types:
            errors.append(f"Schema '{name}': invalid x-resource-type '{resource_type}'")

    return len(errors) == 0, errors


def main():
    parser = argparse.ArgumentParser(description="Validate OpenAPI spec")
    parser.add_argument("--input", "-i", required=True, help="OpenAPI spec file")

    args = parser.parse_args()

    valid, errors = validate_openapi(args.input)

    if valid:
        print("OpenAPI spec is valid")
        sys.exit(0)
    else:
        print("Validation errors:")
        for error in errors:
            print(f"  - {error}")
        sys.exit(1)


if __name__ == "__main__":
    main()
