#!/usr/bin/env python3
# layers/generate_test_layer.py
"""
Test Layer Generator Skill

Generates all test utilities (factories, assertions) for integration tests.

This layer produces the core-<entity>-api-test module that contains:
- factory/<Entity>TestFactory.kt (test factories for Create, Update, Resource)
- assertion/<Entity>Assertions.kt (assertion utilities)
- build.gradle.kts (build configuration)
- gradle.properties (version properties)

Usage:
    python generate_test_layer.py --spec openapi.yaml --output ./output/test/
"""

import argparse
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))

from atomic.kotlin.generate_test_factory import generate_test_factory
from atomic.kotlin.generate_assertions import generate_assertions
from layers.generate_model_layer import extract_schemas_from_openapi
from utils.naming import to_snake_case
import yaml


def generate_test_layer(spec_path: str, output_dir: str, module_name: str = None) -> list[str]:
    """
    Generate all test utility files.

    Args:
        spec_path: Path to OpenAPI specification file
        output_dir: Output directory for generated files
        module_name: Optional module name override

    Returns:
        List of generated file paths
    """
    with open(spec_path) as f:
        spec = yaml.safe_load(f)

    schemas = extract_schemas_from_openapi(spec)
    generated_files = []
    output = Path(output_dir)

    # Get metadata from spec
    info = spec.get("info", {})
    base_package = info.get("x-base-package", "net.blugrid.api.core")
    module_name = module_name or info.get("x-module-name", "")

    for schema in schemas:
        name = schema["name"]
        name_lower = name[0].lower() + name[1:]

        # Enhance schema with additional metadata
        schema["packageName"] = f"{base_package}.{name_lower}"

        # Source paths
        src_main = output / "src" / "main"
        package_path = schema["packageName"].replace(".", "/")

        # 1. Generate Test Factory
        factory_file = src_main / "kotlin" / package_path / "factory" / f"{name}TestFactory.kt"
        generate_test_factory(schema, str(factory_file))
        generated_files.append(str(factory_file))
        print(f"Generated: {factory_file}")

        # 2. Generate Assertions
        assertions_file = src_main / "kotlin" / package_path / "assertion" / f"{name}Assertions.kt"
        generate_assertions(schema, str(assertions_file))
        generated_files.append(str(assertions_file))
        print(f"Generated: {assertions_file}")

    # 3. Generate build.gradle.kts
    build_file = output / "build.gradle.kts"
    build_content = generate_build_gradle(spec, base_package)
    build_file.parent.mkdir(parents=True, exist_ok=True)
    build_file.write_text(build_content)
    generated_files.append(str(build_file))
    print(f"Generated: {build_file}")

    # 4. Generate gradle.properties
    gradle_props_file = output / "gradle.properties"
    gradle_props_content = generate_gradle_properties()
    gradle_props_file.write_text(gradle_props_content)
    generated_files.append(str(gradle_props_file))
    print(f"Generated: {gradle_props_file}")

    return generated_files


def generate_build_gradle(spec: dict, base_package: str) -> str:
    """Generate build.gradle.kts content for test module."""
    info = spec.get("info", {})
    model_module = info.get("x-model-module-path", "")

    return f"""plugins {{
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
}}

dependencies {{
    // API dependencies - expose ALL to test consumers
    api(project(":common:common-kotlin:platform:platform-testing"))
{f'    api(project(":{model_module}"))' if model_module else '    // api(project(":path:to:model-module"))'}

    // Platform BOM - exposed to test consumers
    api(platform(libs.micronaut.bom))

    // Core dependencies using new bundles
    api(libs.bundles.kotlinCore)
    api(libs.bundles.micronautCore)

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Runtime dependencies for test environments
    runtimeOnly(libs.bundles.runtimeCore)
}}

java {{
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {{
        languageVersion.set(JavaLanguageVersion.of(17))
    }}
}}

kapt {{
    arguments {{
        arg("micronaut.openapi.project.dir", projectDir.toString())
        arg("micronaut.processing.incremental", "true")
        arg("micronaut.processing.annotations", "{base_package}.*")
    }}
}}
"""


def generate_gradle_properties() -> str:
    """Generate gradle.properties content."""
    return """micronautVersion=4.4.3
kotlinVersion=1.9.23
"""


def main():
    parser = argparse.ArgumentParser(description="Generate test utilities layer")
    parser.add_argument("--spec", "-s", required=True, help="OpenAPI spec file")
    parser.add_argument("--output", "-o", required=True, help="Output directory")
    parser.add_argument("--module", "-m", help="Module name override")

    args = parser.parse_args()

    files = generate_test_layer(args.spec, args.output, args.module)
    print(f"\nGenerated {len(files)} files")


if __name__ == "__main__":
    main()
