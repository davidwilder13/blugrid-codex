#!/usr/bin/env python3
# parsers/jdl-parser.py
"""
JDL Parser Skill

Parses JHipster Domain Language files and outputs structured JSON.
Uses jhipster-core (Node.js) for parsing.

Usage:
    python jdl-parser.py --input design.jdl --output parsed.json
    python jdl-parser.py --input design.jdl  # outputs to stdout
"""

import argparse
import json
import subprocess
import sys
from pathlib import Path


def parse_jdl(input_path: str, output_path: str | None = None) -> dict:
    """Parse JDL file using jhipster-core and return structured data."""
    script_dir = Path(__file__).parent
    js_parser = script_dir / "jdl-parser.cjs"

    # Resolve input path to absolute and check existence
    input_file = Path(input_path).resolve()
    if not input_file.exists():
        raise FileNotFoundError(f"Input file not found: {input_path} (resolved to: {input_file})")
    if not input_file.is_file():
        raise ValueError(f"Input path is not a file: {input_path}")

    # Get the project root to run node from there (4 levels up from parsers/)
    # parsers/ -> blugrid-codegen/ -> skills/ -> .claude/ -> project_root/
    project_root = script_dir.parent.parent.parent.parent

    # Pass absolute path to avoid path resolution issues
    cmd = ["node", str(js_parser), "--input", str(input_file)]

    result = subprocess.run(
        cmd,
        capture_output=True,
        text=True,
        cwd=str(project_root),
    )

    if result.returncode != 0:
        raise RuntimeError(f"JDL parsing failed: {result.stderr}")

    parsed = json.loads(result.stdout)

    if output_path:
        with open(output_path, "w") as f:
            json.dump(parsed, f, indent=2)
        print(f"Parsed JDL written to: {output_path}", file=sys.stderr)

    return parsed


def main():
    parser = argparse.ArgumentParser(description="Parse JDL files to JSON")
    parser.add_argument("--input", "-i", required=True, help="Input JDL file path")
    parser.add_argument("--output", "-o", help="Output JSON file path (stdout if omitted)")

    args = parser.parse_args()

    try:
        result = parse_jdl(args.input, args.output)
        if not args.output:
            print(json.dumps(result, indent=2))
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()
