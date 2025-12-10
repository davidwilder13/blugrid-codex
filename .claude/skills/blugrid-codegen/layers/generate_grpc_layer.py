#!/usr/bin/env python3
# layers/generate_grpc_layer.py
"""
gRPC Layer Generator Skill

Generates all gRPC-related modules:
- grpc-proto: Protocol Buffer service definitions
- grpc: gRPC server implementation
- grpc-client: gRPC client library

Usage:
    python generate_grpc_layer.py --spec openapi.yaml --output ./output/
"""

import argparse
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).parent.parent))

from atomic.proto.generate_proto import generate_proto
from atomic.kotlin.generate_grpc_service import generate_grpc_service
from atomic.kotlin.generate_grpc_mapping import generate_grpc_mapping
from atomic.kotlin.generate_grpc_client import generate_grpc_client
from layers.generate_model_layer import extract_schemas_from_openapi
from utils.naming import to_snake_case, to_pascal_case
import yaml


def generate_grpc_layer(spec_path: str, output_dir: str, module_name: str = None) -> dict[str, list[str]]:
    """
    Generate all gRPC layer files across three modules.

    Args:
        spec_path: Path to OpenAPI specification file
        output_dir: Base output directory
        module_name: Optional module name override

    Returns:
        Dictionary with generated files per module type
    """
    with open(spec_path) as f:
        spec = yaml.safe_load(f)

    schemas = extract_schemas_from_openapi(spec)
    output = Path(output_dir)

    # Get metadata from spec
    info = spec.get("info", {})
    base_package = info.get("x-base-package", "net.blugrid.api.core")
    module_name = module_name or info.get("x-module-name", "")

    generated = {
        "proto": [],
        "server": [],
        "client": [],
    }

    for schema in schemas:
        name = schema["name"]
        name_lower = name[0].lower() + name[1:]
        name_snake = to_snake_case(name)

        # Enhance schema
        schema["packageName"] = f"{base_package}.{name_lower}"

        # Generate plural form
        if name.endswith("y"):
            name_plural = name[:-1] + "ies"
            name_lower_plural = name_lower[:-1] + "ies"
        else:
            name_plural = name + "s"
            name_lower_plural = name_lower + "s"

        schema["entityNamePlural"] = name_plural
        schema["entityNameLowerPlural"] = name_lower_plural
        schema["grpcServiceName"] = f"{name_lower}-grpc"

        # ==========================================
        # 1. GRPC-PROTO MODULE
        # ==========================================
        proto_dir = output / f"core-{name_snake}-api-grpc-proto"

        # Proto file
        proto_file = proto_dir / "src" / "main" / "proto" / f"{name_lower}.proto"
        generate_proto(schema, str(proto_file))
        generated["proto"].append(str(proto_file))
        print(f"Generated: {proto_file}")

        # Proto build.gradle.kts
        proto_build = proto_dir / "build.gradle.kts"
        proto_build_content = generate_proto_build_gradle(schema, spec)
        proto_build.parent.mkdir(parents=True, exist_ok=True)
        proto_build.write_text(proto_build_content)
        generated["proto"].append(str(proto_build))
        print(f"Generated: {proto_build}")

        # Proto gradle.properties
        proto_props = proto_dir / "gradle.properties"
        proto_props.write_text(generate_gradle_properties())
        generated["proto"].append(str(proto_props))
        print(f"Generated: {proto_props}")

        # ==========================================
        # 2. GRPC SERVER MODULE
        # ==========================================
        server_dir = output / f"core-{name_snake}-api-grpc"
        package_path = schema["packageName"].replace(".", "/")

        # Application.kt
        app_file = server_dir / "src" / "main" / "kotlin" / package_path / "grpc" / "Application.kt"
        app_content = generate_grpc_application(schema)
        app_file.parent.mkdir(parents=True, exist_ok=True)
        app_file.write_text(app_content)
        generated["server"].append(str(app_file))
        print(f"Generated: {app_file}")

        # GrpcService.kt
        service_file = server_dir / "src" / "main" / "kotlin" / package_path / "grpc" / f"{name}GrpcService.kt"
        generate_grpc_service(schema, str(service_file))
        generated["server"].append(str(service_file))
        print(f"Generated: {service_file}")

        # MappingExtensions.kt
        mapping_file = server_dir / "src" / "main" / "kotlin" / package_path / "grpc" / f"{name}GrpcMappingExtensions.kt"
        generate_grpc_mapping(schema, str(mapping_file))
        generated["server"].append(str(mapping_file))
        print(f"Generated: {mapping_file}")

        # GrpcContextConfiguration.kt
        config_file = server_dir / "src" / "main" / "kotlin" / package_path / "grpc" / "config" / "GrpcContextConfiguration.kt"
        config_content = generate_grpc_config(schema)
        config_file.parent.mkdir(parents=True, exist_ok=True)
        config_file.write_text(config_content)
        generated["server"].append(str(config_file))
        print(f"Generated: {config_file}")

        # application.yml
        server_yml = server_dir / "src" / "main" / "resources" / "application.yml"
        server_yml_content = generate_server_application_yml(schema)
        server_yml.parent.mkdir(parents=True, exist_ok=True)
        server_yml.write_text(server_yml_content)
        generated["server"].append(str(server_yml))
        print(f"Generated: {server_yml}")

        # logback.xml
        logback_file = server_dir / "src" / "main" / "resources" / "logback.xml"
        logback_file.write_text(generate_logback())
        generated["server"].append(str(logback_file))
        print(f"Generated: {logback_file}")

        # Server build.gradle.kts
        server_build = server_dir / "build.gradle.kts"
        server_build_content = generate_server_build_gradle(schema, spec)
        server_build.write_text(server_build_content)
        generated["server"].append(str(server_build))
        print(f"Generated: {server_build}")

        # Server gradle.properties
        server_props = server_dir / "gradle.properties"
        server_props.write_text(generate_gradle_properties())
        generated["server"].append(str(server_props))
        print(f"Generated: {server_props}")

        # Dockerfile
        dockerfile = server_dir / "Dockerfile"
        dockerfile_content = generate_dockerfile(schema)
        dockerfile.write_text(dockerfile_content)
        generated["server"].append(str(dockerfile))
        print(f"Generated: {dockerfile}")

        # ==========================================
        # 3. GRPC CLIENT MODULE
        # ==========================================
        client_dir = output / f"core-{name_snake}-api-grpc-client"

        # GrpcClient.kt
        client_file = client_dir / "src" / "main" / "kotlin" / package_path / "grpc" / "client" / f"{name}GrpcClient.kt"
        generate_grpc_client(schema, str(client_file))
        generated["client"].append(str(client_file))
        print(f"Generated: {client_file}")

        # GrpcClientFactory.kt
        factory_file = client_dir / "src" / "main" / "kotlin" / package_path / "grpc" / "client" / f"{name}GrpcClientFactory.kt"
        factory_content = generate_client_factory(schema)
        factory_file.parent.mkdir(parents=True, exist_ok=True)
        factory_file.write_text(factory_content)
        generated["client"].append(str(factory_file))
        print(f"Generated: {factory_file}")

        # CommandServiceGrpcClientImpl.kt
        impl_file = client_dir / "src" / "main" / "kotlin" / package_path / "grpc" / "client" / f"{name}CommandServiceGrpcClientImpl.kt"
        impl_content = generate_service_impl(schema)
        impl_file.write_text(impl_content)
        generated["client"].append(str(impl_file))
        print(f"Generated: {impl_file}")

        # ProtoMappers.kt
        mappers_file = client_dir / "src" / "main" / "kotlin" / package_path / "grpc" / f"{name}ProtoMappers.kt"
        mappers_content = generate_proto_mappers(schema)
        mappers_file.parent.mkdir(parents=True, exist_ok=True)
        mappers_file.write_text(mappers_content)
        generated["client"].append(str(mappers_file))
        print(f"Generated: {mappers_file}")

        # Client application.yml
        client_yml = client_dir / "src" / "main" / "resources" / "application.yml"
        client_yml_content = generate_client_application_yml(schema)
        client_yml.parent.mkdir(parents=True, exist_ok=True)
        client_yml.write_text(client_yml_content)
        generated["client"].append(str(client_yml))
        print(f"Generated: {client_yml}")

        # Client build.gradle.kts
        client_build = client_dir / "build.gradle.kts"
        client_build_content = generate_client_build_gradle(schema, spec)
        client_build.write_text(client_build_content)
        generated["client"].append(str(client_build))
        print(f"Generated: {client_build}")

        # Client gradle.properties
        client_props = client_dir / "gradle.properties"
        client_props.write_text(generate_gradle_properties())
        generated["client"].append(str(client_props))
        print(f"Generated: {client_props}")

    return generated


def generate_grpc_application(schema: dict) -> str:
    """Generate gRPC Application.kt content."""
    package_name = schema.get("packageName", "")
    return f"""package {package_name}.grpc

import io.grpc.protobuf.services.ProtoReflectionService
import io.micronaut.runtime.Micronaut

object Application

fun main(args: Array<String>) {{
    Micronaut.build()
        .packages("net.blugrid.api")
        .mainClass(Application.javaClass)
        .singletons(ProtoReflectionService.newInstance())
        .start()
}}
"""


def generate_grpc_config(schema: dict) -> str:
    """Generate GrpcContextConfiguration.kt content."""
    package_name = schema.get("packageName", "")
    return f"""package {package_name}.grpc.config

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.scheduling.TaskExecutors
import jakarta.inject.Named
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.ExecutorService

@Factory
class GrpcContextConfiguration {{

    @Bean
    @Singleton
    @Named("grpcDispatcher")
    fun grpcContextAwareDispatcher(
        @Named(TaskExecutors.IO) ioExecutor: ExecutorService
    ): CoroutineDispatcher {{
        return ioExecutor.asCoroutineDispatcher()
    }}
}}
"""


def generate_server_application_yml(schema: dict) -> str:
    """Generate server application.yml content."""
    name_lower = schema["name"][0].lower() + schema["name"][1:]
    return f"""micronaut:
  application:
    name: {name_lower}-grpc
  server:
    port: 0  # dynamically assign REST HTTP by default

grpc:
  server:
    port: ${{GRPC_SERVER_PORT:50051}}
    instance-id: ${{GRPC_SERVER_INSTANCE_ID:{name_lower}-grpc}}
    health:
      enabled: true
    reflection:
      enabled: true

consul:
  client:
    registration:
      enabled: true
      ip-addr: ${{CONSUL_CLIENT_REGISTRATION_IP_ADDR:${{DOCKER_HOST_IP}}}}
    defaultZone: "http://${{CONSUL_HOST:consul}}:${{CONSUL_PORT:8500}}"
"""


def generate_client_application_yml(schema: dict) -> str:
    """Generate client application.yml content."""
    name_lower = schema["name"][0].lower() + schema["name"][1:]
    return f"""grpc:
  client:
    {name_lower}-grpc:
      address: ${{GRPC_CLIENT_ADDRESS:static://localhost:50051}}
"""


def generate_client_factory(schema: dict) -> str:
    """Generate GrpcClientFactory.kt content."""
    package_name = schema.get("packageName", "")
    entity_name = schema["name"]
    entity_name_lower = entity_name[0].lower() + entity_name[1:]

    return f"""package {package_name}.grpc.client

import io.grpc.ClientInterceptor
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Primary
import io.micronaut.grpc.annotation.GrpcChannel
import jakarta.inject.Singleton
import {package_name}.grpc.{entity_name}StateServiceGrpcKt

@Factory
class {entity_name}GrpcClientFactory(
    private val authInterceptor: ClientInterceptor
) {{

    fun create(host: String, port: Int): {entity_name}GrpcClient {{
        val channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .intercept(authInterceptor)
            .build()

        val stub = {entity_name}StateServiceGrpcKt.{entity_name}StateServiceCoroutineStub(channel)
        return {entity_name}GrpcClient(stub)
    }}

    @Singleton
    @Primary
    fun {entity_name_lower}Stub(
        @GrpcChannel("{entity_name_lower}-grpc") channel: ManagedChannel
    ): {entity_name}StateServiceGrpcKt.{entity_name}StateServiceCoroutineStub {{
        return {entity_name}StateServiceGrpcKt.{entity_name}StateServiceCoroutineStub(channel)
    }}

    @Singleton
    @Primary
    fun {entity_name_lower}GrpcClient(
        stub: {entity_name}StateServiceGrpcKt.{entity_name}StateServiceCoroutineStub
    ): {entity_name}GrpcClient {{
        return {entity_name}GrpcClient(stub)
    }}
}}
"""


def generate_service_impl(schema: dict) -> str:
    """Generate CommandServiceGrpcClientImpl.kt content."""
    package_name = schema.get("packageName", "")
    entity_name = schema["name"]
    entity_name_lower = entity_name[0].lower() + entity_name[1:]
    entity_name_lower_plural = schema.get("entityNameLowerPlural", entity_name_lower + "s")

    return f"""package {package_name}.grpc.client

import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import {package_name}.grpc.to{entity_name}
import {package_name}.grpc.to{entity_name}CreateRequest
import {package_name}.grpc.to{entity_name}PageRequest
import {package_name}.grpc.to{entity_name}UpdateRequest
import {package_name}.model.{entity_name}
import {package_name}.model.{entity_name}Create
import {package_name}.model.{entity_name}Filter
import {package_name}.model.{entity_name}Update
import {package_name}.service.{entity_name}CommandService
import {package_name}.service.{entity_name}QueryService
import net.blugrid.common.model.pagination.Page
import net.blugrid.common.model.pagination.PageRequest
import net.blugrid.common.model.pagination.Pageable
import java.util.Optional
import java.util.UUID

@Singleton
@Requires(classes = [{entity_name}GrpcClient::class])
class {entity_name}CommandServiceGrpcClientImpl(
    private val grpcClient: {entity_name}GrpcClient
) : {entity_name}CommandService, {entity_name}QueryService {{

    override fun getPage(pageable: Pageable): Page<{entity_name}> = runBlocking {{
        val response = grpcClient.getPage(pageable.to{entity_name}PageRequest())
        Page.of(
            response.{entity_name_lower_plural}List.map {{ it.to{entity_name}() }},
            PageRequest.of(pageable.number, pageable.size, pageable.sort),
            response.totalElements.toLong()
        )
    }}

    override fun getById(id: Long): {entity_name} = runBlocking {{
        grpcClient.getById(id).to{entity_name}()
    }}

    override fun getByIdOptional(id: Long): Optional<{entity_name}> = runBlocking {{
        val response = grpcClient.getByIdOptional(id)
        if (response.exists) Optional.of(response.{entity_name_lower}.to{entity_name}())
        else Optional.empty()
    }}

    override fun getByUuid(uuid: UUID): {entity_name} = runBlocking {{
        grpcClient.getByUuid(uuid).to{entity_name}()
    }}

    override fun getByUuidOptional(uuid: UUID): Optional<{entity_name}> = runBlocking {{
        val response = grpcClient.getByUuidOptional(uuid)
        if (response.exists) Optional.of(response.{entity_name_lower}.to{entity_name}())
        else Optional.empty()
    }}

    override fun findByFilter(filter: {entity_name}Filter, pageable: Pageable): Page<{entity_name}> {{
        TODO("Not yet implemented")
    }}

    override fun getAll(): List<{entity_name}> = runBlocking {{
        grpcClient.getAll().{entity_name_lower_plural}List.map {{ it.to{entity_name}() }}
    }}

    override fun create(newResource: {entity_name}Create): {entity_name} = runBlocking {{
        grpcClient.create(newResource.to{entity_name}CreateRequest()).to{entity_name}()
    }}

    override fun update(id: Long, update: {entity_name}Update): {entity_name} = runBlocking {{
        grpcClient.update(update.to{entity_name}UpdateRequest()).to{entity_name}()
    }}

    override fun delete(id: Long) = runBlocking {{
        grpcClient.delete(id)
    }}
}}
"""


def generate_proto_mappers(schema: dict) -> str:
    """Generate ProtoMappers.kt content."""
    package_name = schema.get("packageName", "")
    entity_name = schema["name"]
    entity_name_plural = schema.get("entityNamePlural", entity_name + "s")

    # Build field mappings
    fields = schema.get("fields", [])
    field_lines_response = []
    field_lines_create = []
    field_lines_update = []
    field_set_lines = []
    field_create_set_lines = []
    field_update_set_lines = []

    for field in fields:
        name = field["name"]
        name_pascal = name[0].upper() + name[1:]
        kotlin_type = field.get("type", "String")

        if kotlin_type == "LocalDateTime":
            field_lines_response.append(f"        {name} = LocalDateTime.parse(this.{name})")
            field_lines_create.append(f"        {name} = LocalDateTime.parse(this.{name})")
            field_lines_update.append(f"        {name} = LocalDateTime.parse(this.{name})")
            field_set_lines.append(f"        .set{name_pascal}(this.{name}.toString())")
            field_create_set_lines.append(f"        .set{name_pascal}(this.{name}.toString())")
            field_update_set_lines.append(f"        .set{name_pascal}(this.{name}.toString())")
        else:
            field_lines_response.append(f"        {name} = this.{name}")
            field_lines_create.append(f"        {name} = this.{name}")
            field_lines_update.append(f"        {name} = this.{name}")
            field_set_lines.append(f"        .set{name_pascal}(this.{name})")
            field_create_set_lines.append(f"        .set{name_pascal}(this.{name})")
            field_update_set_lines.append(f"        .set{name_pascal}(this.{name})")

    field_str_response = ",\n".join(field_lines_response)
    field_str_create = ",\n".join(field_lines_create)
    field_str_update = ",\n".join(field_lines_update)
    field_set_str = "\n".join(field_set_lines)
    field_create_set_str = "\n".join(field_create_set_lines)
    field_update_set_str = "\n".join(field_update_set_lines)

    return f"""package {package_name}.grpc

import {package_name}.model.{entity_name}
import {package_name}.model.{entity_name}Create
import {package_name}.model.{entity_name}Update
import net.blugrid.common.domain.IdentityID
import net.blugrid.common.domain.IdentityUUID
import net.blugrid.common.model.pagination.Page
import net.blugrid.common.model.pagination.Pageable
import net.blugrid.integration.grpc.mapper.toProto
import java.time.LocalDateTime
import java.util.UUID

fun {entity_name}Response.to{entity_name}(): {entity_name} =
    {entity_name}(
        id = IdentityID(this.id),
        uuid = IdentityUUID(UUID.fromString(this.uuid)),
{field_str_response if field_str_response else ""}
    )

fun {entity_name}CreateRequest.to{entity_name}Create(): {entity_name}Create =
    {entity_name}Create(
        uuid = IdentityUUID(UUID.randomUUID()),
{field_str_create if field_str_create else ""}
    )

fun {entity_name}UpdateRequest.to{entity_name}Update(): {entity_name}Update =
    {entity_name}Update(
        id = IdentityID(this.id),
        uuid = IdentityUUID(UUID.fromString(this.uuid)),
{field_str_update if field_str_update else ""}
    )

// Model -> gRPC

fun Pageable.to{entity_name}PageRequest(): {entity_name}PageRequest {{
    return {entity_name}PageRequest.newBuilder()
        .setPage(this.number)
        .setSize(this.size)
        .setSort(this.sort.toProto())
        .build()
}}

fun {entity_name}.to{entity_name}Response(): {entity_name}Response =
    {entity_name}Response.newBuilder()
        .setId(this.id.value)
        .setUuid(this.uuid.value.toString())
{field_set_str if field_set_str else ""}
        .build()

fun {entity_name}Create.to{entity_name}CreateRequest(): {entity_name}CreateRequest =
    {entity_name}CreateRequest.newBuilder()
        .setUuid(this.uuid.toString())
{field_create_set_str if field_create_set_str else ""}
        .build()

fun {entity_name}Update.to{entity_name}UpdateRequest(): {entity_name}UpdateRequest =
    {entity_name}UpdateRequest.newBuilder()
        .setId(this.id.value)
        .setUuid(this.uuid.value.toString())
{field_update_set_str if field_update_set_str else ""}
        .build()

fun Page<{entity_name}>.toGrpcPage(): {entity_name}PageResponse =
    {entity_name}PageResponse.newBuilder()
        .addAll{entity_name_plural}(content.map {{ it.to{entity_name}Response() }})
        .setTotalElements(totalElements.toInt())
        .setTotalPages(totalPages)
        .setPage(number)
        .setSize(size)
        .build()
"""


def generate_proto_build_gradle(schema: dict, spec: dict) -> str:
    """Generate proto module build.gradle.kts content."""
    info = spec.get("info", {})
    model_module = info.get("x-model-module-path", "")

    return f"""import com.google.protobuf.gradle.id

plugins {{
    alias(libs.plugins.jvm)
    id("com.google.protobuf") version "0.9.4"
}}

version = "0.1.0"
group = "net.blugrid.api"

repositories {{
    mavenCentral()
}}

dependencies {{
    api(project(":common:common-kotlin:integration:integration-grpc-proto"))

{f'    api(project(":{model_module}"))' if model_module else '    // api(project(":path:to:model-module"))'}

    implementation("io.grpc:grpc-kotlin-stub:1.3.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    implementation("io.grpc:grpc-stub:1.62.2")
    implementation("io.grpc:grpc-netty-shaded:1.62.2")
    implementation("io.grpc:grpc-protobuf:1.62.2")
    implementation("com.google.protobuf:protobuf-java:4.31.1")
    implementation("com.google.protobuf:protobuf-kotlin:4.31.1")
}}

java {{
    sourceCompatibility = JavaVersion.toVersion("17")
}}

kotlin {{
    jvmToolchain {{
        languageVersion.set(JavaLanguageVersion.of(17))
    }}
}}

protobuf {{
    protoc {{
        artifact = "com.google.protobuf:protoc:4.31.1"
    }}
    plugins {{
        id("grpc") {{
            artifact = "io.grpc:protoc-gen-grpc-java:1.62.2"
        }}
        id("grpckt") {{
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }}
    }}
    generateProtoTasks {{
        all().forEach {{ task ->
            task.builtins {{
                id("kotlin")
            }}
            task.plugins {{
                id("grpc")
                id("grpckt")
            }}
        }}
    }}
}}

sourceSets["main"].java.srcDirs(
    "build/generated/source/proto/main/java",
    "build/generated/source/proto/main/grpc",
    "build/generated/source/proto/main/grpckt"
)

sourceSets {{
    main {{
        proto {{
            srcDir("src/main/proto")
        }}
    }}
}}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {{
    dependsOn("generateProto")
}}

tasks.withType<JavaCompile> {{
    dependsOn("generateProto")
}}
"""


def generate_server_build_gradle(schema: dict, spec: dict) -> str:
    """Generate server module build.gradle.kts content."""
    package_name = schema.get("packageName", "")
    name_snake = to_snake_case(schema["name"])
    info = spec.get("info", {})
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
    alias(libs.plugins.protobuf)
}}

dependencies {{
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common:common-model"))
    api(project(":common:common-kotlin:data:data-persistence"))
    api(project(":common:common-kotlin:integration:integration-grpc"))
    api(project(":common:common-kotlin:integration:integration-grpc-proto"))
    api(project(":common:common-kotlin:platform:platform-config"))
    api(project(":common:common-kotlin:platform:platform-logging"))
    api(project(":common:common-kotlin:platform:platform-serialization"))
    api(project(":common:common-kotlin:security:security-core"))
    api(project(":common:common-kotlin:server:server-multitenancy"))

    // Domain-specific modules
{f'    api(project(":{model_module}"))' if model_module else '    // api(project(":path:to:model-module"))'}
{f'    api(project(":{db_module}"))' if db_module else '    // api(project(":path:to:db-module"))'}
    // api(project(":path:to:grpc-proto-module"))

    // Platform BOMs
    implementation(platform(libs.micronaut.bom))
    implementation(platform(libs.aws.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautCore)
    implementation(libs.bundles.micronautData)     // Database access
    implementation(libs.bundles.grpcCore)          // gRPC core functionality
    implementation(libs.bundles.grpcServer)        // gRPC server support

    api(libs.kotlinx.coroutines.core)

    // Service discovery for gRPC
    implementation(libs.micronaut.discovery)

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Runtime dependencies
    runtimeOnly(libs.bundles.runtimeCore)
    runtimeOnly(libs.bundles.runtimeDatabase)

    // Test dependencies
    testImplementation(project(":common:common-kotlin:platform:platform-testing"))
{f'    testImplementation(project(":{test_module}"))' if test_module else '    // testImplementation(project(":path:to:test-module"))'}
    testImplementation(libs.bundles.testing) {{
        exclude(group = "org.slf4j", module = "slf4j-api")
    }}
    testImplementation(libs.kotlinx.coroutines.test)
}}

application {{
    mainClass.set("{package_name}.grpc.ApplicationKt")
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
        arg("micronaut.processing.annotations", "net.blugrid.*")
    }}
}}

micronaut {{
    runtime("netty")
    testRuntime("junit5")
    processing {{
        incremental(true)
        annotations("{package_name}.grpc.*")
    }}
}}

protobuf {{
    protoc {{
        artifact = "com.google.protobuf:protoc:${{libs.versions.protobuf.get()}}"
    }}
    plugins {{
        create("grpc") {{
            artifact = "io.grpc:protoc-gen-grpc-java:${{libs.versions.grpc.get()}}"
        }}
        create("grpckt") {{
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }}
    }}
    generateProtoTasks {{
        all().forEach {{ task ->
            task.builtins {{
                create("kotlin")
            }}
            task.plugins {{
                create("grpc")
                create("grpckt")
            }}
        }}
    }}
}}

sourceSets["main"].java.srcDirs(
    "build/generated/source/proto/main/java",
    "build/generated/source/proto/main/grpc",
    "build/generated/source/proto/main/grpckt"
)

tasks.withType<Jar> {{
    manifest {{
        attributes["Main-Class"] = application.mainClass.get()
    }}
}}

tasks.register<Exec>("buildLocalGrpcDockerImage") {{
    dependsOn("shadowJar")
    commandLine("docker", "build", "-t", "core-{name_snake}-api-grpc:local", ".")
}}
"""


def generate_client_build_gradle(schema: dict, spec: dict) -> str:
    """Generate client module build.gradle.kts content."""
    package_name = schema.get("packageName", "")
    info = spec.get("info", {})
    model_module = info.get("x-model-module-path", "")
    test_module = info.get("x-test-module-path", "")

    return f"""plugins {{
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.application)
}}

dependencies {{
    // API dependencies - expose to consumers
    api(project(":common:common-kotlin:common:common-model"))
    api(project(":common:common-kotlin:integration:integration-grpc-client"))
    api(project(":common:common-kotlin:integration:integration-grpc-proto"))
    api(project(":common:common-kotlin:platform:platform-serialization"))

    // Domain-specific modules
{f'    api(project(":{model_module}"))' if model_module else '    // api(project(":path:to:model-module"))'}
    // api(project(":path:to:grpc-proto-module"))

    // Platform BOM
    implementation(platform(libs.micronaut.bom))

    // Core dependencies using new bundles
    implementation(libs.bundles.kotlinCore)
    implementation(libs.bundles.micronautClient)
    implementation(libs.bundles.grpcClientOnly)

    // Micronaut Data
    implementation(libs.micronaut.data.model)

    // Kotlin coroutines for async gRPC calls
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Runtime dependencies
    runtimeOnly(libs.bundles.runtimeClientOnly)

    // Test dependencies
    testImplementation(project(":common:common-kotlin:platform:platform-testing"))
    testImplementation(project(":common:common-kotlin:integration:integration-grpc-client"))
{f'    testImplementation(project(":{test_module}"))' if test_module else '    // testImplementation(project(":path:to:test-module"))'}
    testImplementation(libs.bundles.testing) {{
        exclude(group = "org.slf4j", module = "slf4j-api")
    }}

    // ===== CRITICAL: EXCLUDE ALL JPA/HIBERNATE DEPENDENCIES =====
    configurations.all {{
        exclude(group = "io.micronaut.sql", module = "micronaut-hibernate-jpa")
        exclude(group = "io.micronaut.sql", module = "micronaut-jdbc-hikari")
        exclude(group = "io.micronaut.data", module = "micronaut-data-hibernate-jpa")
        exclude(group = "io.micronaut.flyway", module = "micronaut-flyway")
        exclude(group = "org.hibernate")
        exclude(group = "org.postgresql", module = "postgresql")
        exclude(group = "com.zaxxer", module = "HikariCP")
    }}
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
        arg("micronaut.processing.annotations", "{package_name}.grpc.*")
    }}
}}

micronaut {{
    testRuntime("junit5")
    processing {{
        incremental(true)
        annotations("{package_name}.grpc.*")
    }}
}}

protobuf {{
    protoc {{
        artifact = "com.google.protobuf:protoc:${{libs.versions.protobuf.get()}}"
    }}
    plugins {{
        create("grpc") {{
            artifact = "io.grpc:protoc-gen-grpc-java:${{libs.versions.grpc.get()}}"
        }}
        create("grpckt") {{
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }}
    }}
    generateProtoTasks {{
        all().forEach {{ task ->
            task.builtins {{
                create("kotlin")
            }}
            task.plugins {{
                create("grpc")
                create("grpckt")
            }}
        }}
    }}
}}

sourceSets["main"].java.srcDirs(
    "build/generated/source/proto/main/java",
    "build/generated/source/proto/main/grpc",
    "build/generated/source/proto/main/grpckt"
)
"""


def generate_dockerfile(schema: dict) -> str:
    """Generate Dockerfile content."""
    name_snake = to_snake_case(schema["name"])
    return f"""FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY build/libs/*-all.jar app.jar

EXPOSE 50051

ENTRYPOINT ["java", "-jar", "app.jar"]
"""


def generate_logback() -> str:
    """Generate logback.xml content."""
    return """<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} -- %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>

    <logger name="net.blugrid" level="DEBUG" additivity="false">
        <appender-ref ref="CONSOLE"/>
    </logger>
</configuration>
"""


def generate_gradle_properties() -> str:
    """Generate gradle.properties content."""
    return """micronautVersion=4.4.3
kotlinVersion=1.9.23
"""


def main():
    parser = argparse.ArgumentParser(description="Generate gRPC layer")
    parser.add_argument("--spec", "-s", required=True, help="OpenAPI spec file")
    parser.add_argument("--output", "-o", required=True, help="Output directory")
    parser.add_argument("--module", "-m", help="Module name override")

    args = parser.parse_args()

    generated = generate_grpc_layer(args.spec, args.output, args.module)

    total = sum(len(files) for files in generated.values())
    print(f"\nGenerated {total} files:")
    print(f"  - Proto module: {len(generated['proto'])} files")
    print(f"  - Server module: {len(generated['server'])} files")
    print(f"  - Client module: {len(generated['client'])} files")


if __name__ == "__main__":
    main()
