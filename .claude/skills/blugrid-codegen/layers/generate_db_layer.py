#!/usr/bin/env python3
# layers/generate_db_layer.py
"""
DB Layer Generator Skill

Generates all database layer files (entities, migrations, repositories).

Usage:
    python generate_db_layer.py --spec openapi.yaml --output ./output/db/
"""

import argparse
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))

from atomic.kotlin.generate_entity import generate_entity
from atomic.sql.generate_table import generate_table
from layers.generate_model_layer import extract_schemas_from_openapi
from utils.naming import to_snake_case
import yaml


def generate_db_layer(spec_path: str, output_dir: str) -> list[str]:
    """Generate all database layer files."""
    with open(spec_path) as f:
        spec = yaml.safe_load(f)

    schemas = extract_schemas_from_openapi(spec)
    generated_files = []
    output = Path(output_dir)

    for i, schema in enumerate(schemas):
        name = schema["name"]
        table_name = to_snake_case(name)

        # Generate entity
        entity_dir = output / "src" / "main" / "kotlin" / "repository" / "model"
        entity_file = entity_dir / f"{name}Entity.kt"
        generate_entity(schema, str(entity_file))
        generated_files.append(str(entity_file))
        print(f"Generated: {entity_file}")

        # Generate SQL migration
        migration_dir = output / "src" / "main" / "resources" / "db" / "migration"
        migration_file = migration_dir / f"V{i+1}__{table_name}.sql"
        generate_table(schema, str(migration_file))
        generated_files.append(str(migration_file))
        print(f"Generated: {migration_file}")

    return generated_files


def main():
    parser = argparse.ArgumentParser(description="Generate DB layer")
    parser.add_argument("--spec", "-s", required=True, help="OpenAPI spec file")
    parser.add_argument("--output", "-o", required=True, help="Output directory")

    args = parser.parse_args()

    files = generate_db_layer(args.spec, args.output)
    print(f"\nGenerated {len(files)} files")


if __name__ == "__main__":
    main()
