#!/usr/bin/env python3
# tests/run_tests.py
"""
Test Pipeline Runner

Runs the three-stage test pipeline:
1. Golden file diff
2. Build verification
3. Generated test execution

Usage:
    python run_tests.py --fixture fixtures/core-organisation.yaml
    python run_tests.py --fixture fixtures/core-organisation.yaml --update-golden
"""

import argparse
import subprocess
import sys
from pathlib import Path
import tempfile
import shutil


def run_command(cmd: list[str], cwd: str = None) -> tuple[int, str, str]:
    """Run a command and return (returncode, stdout, stderr)."""
    result = subprocess.run(cmd, capture_output=True, text=True, cwd=cwd)
    return result.returncode, result.stdout, result.stderr


def stage_1_golden_diff(generated_dir: Path, golden_dir: Path) -> bool:
    """Stage 1: Compare generated output against golden files."""
    print("\n=== Stage 1: Golden File Diff ===")

    if not golden_dir.exists():
        print(f"No golden directory found at {golden_dir}")
        print("Skipping golden diff (run with --update-golden to create)")
        return True

    code, stdout, stderr = run_command([
        "git", "diff", "--no-index", "--stat",
        str(golden_dir), str(generated_dir)
    ])

    if code == 0:
        print("✓ Generated output matches golden files")
        return True
    else:
        print("✗ Differences found:")
        print(stdout)
        return False


def stage_2_build_verification(generated_dir: Path) -> bool:
    """Stage 2: Run gradle build on generated code."""
    print("\n=== Stage 2: Build Verification ===")

    gradle_wrapper = generated_dir / "gradlew"
    if not gradle_wrapper.exists():
        print("No gradlew found, skipping build verification")
        return True

    code, stdout, stderr = run_command(
        ["./gradlew", "build", "-x", "test"],
        cwd=str(generated_dir)
    )

    if code == 0:
        print("✓ Build successful")
        return True
    else:
        print("✗ Build failed:")
        print(stderr)
        return False


def stage_3_run_tests(generated_dir: Path) -> bool:
    """Stage 3: Run generated tests."""
    print("\n=== Stage 3: Generated Test Execution ===")

    gradle_wrapper = generated_dir / "gradlew"
    if not gradle_wrapper.exists():
        print("No gradlew found, skipping test execution")
        return True

    code, stdout, stderr = run_command(
        ["./gradlew", "test"],
        cwd=str(generated_dir)
    )

    if code == 0:
        print("✓ All tests passed")
        return True
    else:
        print("✗ Tests failed:")
        print(stderr)
        return False


def main():
    parser = argparse.ArgumentParser(description="Run test pipeline")
    parser.add_argument("--fixture", "-f", required=True, help="Fixture file to test")
    parser.add_argument("--golden", "-g", help="Golden directory path")
    parser.add_argument("--update-golden", action="store_true", help="Update golden files")

    args = parser.parse_args()

    fixture = Path(args.fixture)
    tests_dir = Path(__file__).parent
    golden_dir = Path(args.golden) if args.golden else tests_dir / "golden" / fixture.stem

    # Generate to temp directory
    with tempfile.TemporaryDirectory() as tmp:
        generated_dir = Path(tmp) / "generated"

        # TODO: Run full generation pipeline
        # For now, placeholder
        print(f"Would generate from {fixture} to {generated_dir}")

        if args.update_golden:
            print(f"\nUpdating golden files at {golden_dir}")
            if golden_dir.exists():
                shutil.rmtree(golden_dir)
            shutil.copytree(generated_dir, golden_dir)
            print("✓ Golden files updated")
            return

        # Run pipeline stages
        results = {
            "golden_diff": stage_1_golden_diff(generated_dir, golden_dir),
            "build": stage_2_build_verification(generated_dir),
            "tests": stage_3_run_tests(generated_dir),
        }

        # Summary
        print("\n=== Test Pipeline Summary ===")
        all_passed = all(results.values())
        for stage, passed in results.items():
            status = "✓" if passed else "✗"
            print(f"  {status} {stage}")

        sys.exit(0 if all_passed else 1)


if __name__ == "__main__":
    main()
