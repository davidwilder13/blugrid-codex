#!/usr/bin/env python3
# layers/generate_rest_layer.py
"""
REST Layer Generator Skill

Generates all REST API layer files (controllers, application entry point, configs, tests).

This layer produces the core-<entity>-api module that contains:
- Application.kt (entry point)
- Controller.kt (REST endpoints)
- application.yml (main config)
- application-test.yml (test config)
- logback.xml (logging config)
- ControllerIntegTest.kt (integration tests)
- build.gradle.kts (build configuration)
- gradle.properties (version properties)

Usage:
    python generate_rest_layer.py --spec openapi.yaml --output ./output/rest/
"""

import argparse
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))

from atomic.kotlin.generate_controller import generate_controller
from atomic.kotlin.generate_application import generate_application
from atomic.kotlin.generate_controller_test import generate_controller_test
from layers.generate_model_layer import extract_schemas_from_openapi
from utils.templates import render_template
from utils.naming import to_snake_case
import yaml


def generate_rest_layer(spec_path: str, output_dir: str, module_name: str = None) -> list[str]:
    """
    Generate all REST API layer files.

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
    service_name = info.get("x-service-name", f"core-{to_snake_case(module_name).replace('_', '-')}-api")

    for schema in schemas:
        name = schema["name"]
        name_lower = name[0].lower() + name[1:]

        # Enhance schema with additional metadata
        schema["packageName"] = f"{base_package}.{name_lower}"
        schema["serviceName"] = service_name
        schema["apiTitle"] = info.get("title", f"{name} Core Rest API")
        schema["apiVersion"] = info.get("version", "0.0")
        schema["apiDescription"] = info.get("description", f"{name} Core ")

        # Source paths
        src_main = output / "src" / "main"
        src_test = output / "src" / "test"
        package_path = schema["packageName"].replace(".", "/")

        # 1. Generate Application.kt
        app_file = src_main / "kotlin" / package_path / "Application.kt"
        generate_application(schema, str(app_file))
        generated_files.append(str(app_file))
        print(f"Generated: {app_file}")

        # 2. Generate Controller
        controller_file = src_main / "kotlin" / package_path / "controller" / f"{name}Controller.kt"
        generate_controller(schema, str(controller_file))
        generated_files.append(str(controller_file))
        print(f"Generated: {controller_file}")

        # 3. Generate application.yml
        app_yml_file = src_main / "resources" / "application.yml"
        app_yml_content = generate_application_yml(schema)
        app_yml_file.parent.mkdir(parents=True, exist_ok=True)
        app_yml_file.write_text(app_yml_content)
        generated_files.append(str(app_yml_file))
        print(f"Generated: {app_yml_file}")

        # 4. Generate test files
        test_file = src_test / "kotlin" / package_path / "controller" / f"{name}ControllerIntegTest.kt"
        generate_controller_test(schema, str(test_file))
        generated_files.append(str(test_file))
        print(f"Generated: {test_file}")

        # 5. Generate application-test.yml
        test_yml_file = src_test / "resources" / "application-test.yml"
        test_yml_content = generate_test_yml()
        test_yml_file.parent.mkdir(parents=True, exist_ok=True)
        test_yml_file.write_text(test_yml_content)
        generated_files.append(str(test_yml_file))
        print(f"Generated: {test_yml_file}")

        # 6. Generate logback.xml for tests
        logback_file = src_test / "resources" / "logback.xml"
        logback_content = generate_logback()
        logback_file.parent.mkdir(parents=True, exist_ok=True)
        logback_file.write_text(logback_content)
        generated_files.append(str(logback_file))
        print(f"Generated: {logback_file}")

        # 7. Generate build.gradle.kts
        build_file = output / "build.gradle.kts"
        build_content = generate_build_gradle(schema, spec)
        build_file.write_text(build_content)
        generated_files.append(str(build_file))
        print(f"Generated: {build_file}")

        # 8. Generate gradle.properties
        gradle_props_file = output / "gradle.properties"
        gradle_props_content = generate_gradle_properties()
        gradle_props_file.write_text(gradle_props_content)
        generated_files.append(str(gradle_props_file))
        print(f"Generated: {gradle_props_file}")

    return generated_files


def generate_application_yml(schema: dict) -> str:
    """Generate application.yml content."""
    service_name = schema.get("serviceName", "core-api")
    return f"""micronaut:
  application:
    name: {service_name}

env:
  name: ${{SERVICE_NAME:{service_name}}}
  server:
    baseUri: ${{SERVICE_BASE_URI:`http://localhost:8080`}}
  web:
    baseUri: ${{WEB_BASE_URI:`http://localhost:4201`}}
"""


def generate_test_yml() -> str:
    """Generate application-test.yml content."""
    return """micronaut:
  server:
    port: -1

logger:
  levels:
    net.blugrid: TRACE
    testcontainers: INFO
    com.github.dockerjava: WARN
    io.micronaut.web.router: TRACE
"""


def generate_logback() -> str:
    """Generate logback.xml content for tests."""
    return """<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- Console appender for test output -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} -- %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Root logger configuration -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>

    <!-- Application-specific loggers -->
    <logger name="net.blugrid" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE"/>
    </logger>
</configuration>
"""


def generate_build_gradle(schema: dict, spec: dict) -> str:
    """Generate build.gradle.kts content."""
    info = spec.get("info", {})
    package_name = schema.get("packageName", "")
    main_class = f"{package_name}.ApplicationKt"

    # Module paths - these would typically be derived from the project structure
    model_module = info.get("x-model-module-path", "")
    db_module = info.get("x-db-module-path", "")
    test_module = info.get("x-test-module-path", "")

    return f"""plugins {{
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.jpa)
    alias(libs.plugins.application)
    alias(libs.plugins.shadow)
}}

dependencies {{
    // Common foundation APIs
    api(project(":common:common-kotlin:common:common-model"))
    api(project(":common:common-kotlin:data:data-persistence"))
    api(project(":common:common-kotlin:platform:platform-config"))
    api(project(":common:common-kotlin:platform:platform-logging"))
    api(project(":common:common-kotlin:platform:platform-serialization"))
    api(project(":common:common-kotlin:security:security-core"))
    api(project(":common:common-kotlin:server:server-rest"))

    // Domain-specific modules
{f'    implementation(project(":{model_module}"))' if model_module else '    // implementation(project(":path:to:model-module"))'}
{f'    implementation(project(":{db_module}"))' if db_module else '    // implementation(project(":path:to:db-module"))'}

    // Platform BOMs
    implementation(platform(libs.micronaut.bom))
    implementation(platform(libs.aws.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)
    implementation(libs.bundles.micronautWeb)      // REST API support
    implementation(libs.bundles.micronautData)     // Database operations
    implementation(libs.bundles.micronautSecurity) // Authentication/authorization

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Runtime dependencies
    runtimeOnly(libs.bundles.runtimeCore)
    runtimeOnly(libs.bundles.runtimeDatabase)
    runtimeOnly(libs.bundles.runtimeSecurity)

    // Test dependencies
    testImplementation(project(":common:common-kotlin:platform:platform-testing"))
{f'    testImplementation(project(":{test_module}"))' if test_module else '    // testImplementation(project(":path:to:test-module"))'}
    testImplementation(libs.bundles.testing) {{
        exclude(group = "org.slf4j", module = "slf4j-api")
    }}
}}

application {{
    mainClass.set("{main_class}")
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
    }}
}}

micronaut {{
    runtime("netty")
    testRuntime("junit5")
    processing {{
        incremental(true)
        annotations("{package_name}.*")
    }}
}}
"""


def generate_gradle_properties() -> str:
    """Generate gradle.properties content."""
    return """micronautVersion=4.4.3
kotlinVersion=1.9.23
"""


def main():
    parser = argparse.ArgumentParser(description="Generate REST API layer")
    parser.add_argument("--spec", "-s", required=True, help="OpenAPI spec file")
    parser.add_argument("--output", "-o", required=True, help="Output directory")
    parser.add_argument("--module", "-m", help="Module name override")

    args = parser.parse_args()

    files = generate_rest_layer(args.spec, args.output, args.module)
    print(f"\nGenerated {len(files)} files")


if __name__ == "__main__":
    main()
