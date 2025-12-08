#!/usr/bin/env python3
# orchestrators/generate_module.py
"""
Module Orchestrator Skill

Generates a complete module with all layers (model, db, api).

Usage:
    python generate_module.py --spec openapi.yaml --output ./output/

Example:
    python generate_module.py --spec tests/fixtures/core-organisation.yaml --output ./output/

Output structure:
    output/
    └── {module-name}/
        ├── {module-name}-model/
        │   └── src/main/kotlin/model/
        │       ├── {Entity}.kt
        │       ├── {Entity}Create.kt
        │       ├── {Entity}Update.kt
        │       └── I{Entity}.kt
        └── {module-name}-db/
            ├── src/main/kotlin/repository/model/
            │   └── {Entity}Entity.kt
            └── src/main/resources/db/migration/
                └── V1__{table}.sql
"""

import argparse
import sys
from pathlib import Path
from typing import Dict, List

sys.path.insert(0, str(Path(__file__).parent.parent))

from layers.generate_model_layer import generate_model_layer
from layers.generate_db_layer import generate_db_layer
import yaml


def extract_module_info(spec: dict) -> dict:
    """Extract module metadata from OpenAPI spec info section."""
    info = spec.get("info", {})

    return {
        "module_name": info.get("x-module-name", "generated-api"),
        "base_package": info.get("x-base-package", "com.example"),
        "group": info.get("x-group", "com.example"),
        "version": info.get("version", "0.1.0"),
        "title": info.get("title", "Generated API"),
    }


def generate_module(spec_path: str, output_dir: str) -> Dict[str, List[str]]:
    """
    Generate a complete module with all layers.

    Args:
        spec_path: Path to OpenAPI specification file
        output_dir: Base output directory for generated code

    Returns:
        Dictionary mapping layer name to list of generated file paths:
        {
            "model": ["path/to/Entity.kt", ...],
            "db": ["path/to/EntityEntity.kt", ...],
            "api": []  # TODO: API layer not yet implemented
        }
    """
    # Load and parse OpenAPI spec
    with open(spec_path) as f:
        spec = yaml.safe_load(f)

    # Extract module info from spec
    module_info = extract_module_info(spec)
    module_name = module_info["module_name"]

    print(f"\n{'=' * 60}")
    print(f"Generating module: {module_name}")
    print(f"Base package: {module_info['base_package']}")
    print(f"Version: {module_info['version']}")
    print(f"{'=' * 60}\n")

    # Prepare output directories
    output = Path(output_dir) / module_name
    generated = {"model": [], "db": [], "api": []}

    # Generate model layer
    print("\n--- Model Layer ---")
    model_dir = output / f"{module_name}-model" / "src" / "main" / "kotlin" / "model"
    try:
        generated["model"] = generate_model_layer(spec_path, str(model_dir))
    except Exception as e:
        print(f"Error generating model layer: {e}")
        raise

    # Generate db layer
    print("\n--- DB Layer ---")
    db_dir = output / f"{module_name}-db"
    try:
        generated["db"] = generate_db_layer(spec_path, str(db_dir))
    except Exception as e:
        print(f"Error generating DB layer: {e}")
        raise

    # TODO: Generate API layer (controllers, services)
    # print("\n--- API Layer ---")
    # api_dir = output / f"{module_name}-api"
    # generated["api"] = generate_api_layer(spec_path, str(api_dir))

    return generated


def print_summary(result: Dict[str, List[str]], module_info: dict):
    """Print generation summary."""
    total = sum(len(files) for files in result.values())

    print(f"\n{'=' * 60}")
    print(f"Generation Complete")
    print(f"{'=' * 60}")
    print(f"Module: {module_info['module_name']}")
    print(f"\nGenerated files by layer:")
    print(f"  Model layer: {len(result['model'])} files")
    print(f"  DB layer: {len(result['db'])} files")
    print(f"  API layer: {len(result['api'])} files")
    print(f"\nTotal: {total} files")
    print(f"{'=' * 60}\n")


def main():
    """Main entry point for CLI."""
    parser = argparse.ArgumentParser(
        description="Generate complete Kotlin module from OpenAPI spec",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Generate from OpenAPI spec
  python generate_module.py --spec openapi.yaml --output ./output/

  # Generate with verbose output
  python generate_module.py -s api.yaml -o ./generated/ --verbose
        """
    )
    parser.add_argument(
        "--spec", "-s",
        required=True,
        help="OpenAPI specification file path"
    )
    parser.add_argument(
        "--output", "-o",
        required=True,
        help="Output directory for generated code"
    )
    parser.add_argument(
        "--verbose", "-v",
        action="store_true",
        help="Enable verbose output"
    )

    args = parser.parse_args()

    try:
        # Validate input file exists
        if not Path(args.spec).exists():
            print(f"Error: OpenAPI spec file not found: {args.spec}", file=sys.stderr)
            sys.exit(1)

        # Load spec to get module info for summary
        with open(args.spec) as f:
            spec = yaml.safe_load(f)
        module_info = extract_module_info(spec)

        # Generate module
        result = generate_module(args.spec, args.output)

        # Print summary
        print_summary(result, module_info)

        sys.exit(0)

    except FileNotFoundError as e:
        print(f"Error: File not found: {e}", file=sys.stderr)
        sys.exit(1)
    except yaml.YAMLError as e:
        print(f"Error: Invalid YAML in spec file: {e}", file=sys.stderr)
        sys.exit(1)
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        if args.verbose:
            import traceback
            traceback.print_exc()
        sys.exit(1)


if __name__ == "__main__":
    main()
