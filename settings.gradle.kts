rootProject.name = "blugrid-codex"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

// Common API projects (legacy - to be migrated)
//include("common:common-kotlin:common-api:common-api-audit")
//include("common:common-kotlin:common-api:common-api-client")
//include("common:common-kotlin:common-api:common-api-domain")
//include("common:common-kotlin:common-api:common-api-grpc")
//include("common:common-kotlin:common-api:common-api-grpc-proto")
//include("common:common-kotlin:common-api:common-api-json")
//include("common:common-kotlin:common-api:common-api-jwt")
//include("common:common-kotlin:common-api:common-api-logging")
//include("common:common-kotlin:common-api:common-api-model")
//include("common:common-kotlin:common-api:common-api-multitenant")
//include("common:common-kotlin:common-api:common-api-persistence")
//include("common:common-kotlin:common-api:common-api-security")
//include("common:common-kotlin:common-api:common-api-test")
//include("common:common-kotlin:common-api:common-api-web")

// Common modules
include("common:common-kotlin:common:common-domain")
include("common:common-kotlin:common:common-model")

// Platform modules - cross-platform utilities
include("common:common-kotlin:platform:platform-logging")
include("common:common-kotlin:platform:platform-config")
include("common:common-kotlin:platform:platform-validation")
include("common:common-kotlin:platform:platform-serialization")
include("common:common-kotlin:platform:platform-testing")

// Security domain modules
include("common:common-kotlin:security:security-core")
include("common:common-kotlin:security:security-tokens")
include("common:common-kotlin:security:security-authentication")
include("common:common-kotlin:security:security-authorization")
include("common:common-kotlin:security:security-crypto")

// Data domain modules
include("common:common-kotlin:data:data-models")
include("common:common-kotlin:data:data-persistence")
include("common:common-kotlin:data:data-validation")
include("common:common-kotlin:data:data-sync")

// Server platform modules
include("common:common-kotlin:server:server-api")
include("common:common-kotlin:server:server-rest")
include("common:common-kotlin:server:server-persistence")
include("common:common-kotlin:server:server-multitenancy")
include("common:common-kotlin:server:server-standalone")
include("common:common-kotlin:server:server-services")

// Integration modules
include("common:common-kotlin:integration:integration-grpc")
include("common:common-kotlin:integration:integration-grpc-proto")
include("common:common-kotlin:integration:integration-http-client")

// JDL-generated example projects (Organisation)
include("examples:organisations:generated:core-organisation-api:core-organisation-api")
include("examples:organisations:generated:core-organisation-api:core-organisation-api-db")
include("examples:organisations:generated:core-organisation-api:core-organisation-api-grpc")
include("examples:organisations:generated:core-organisation-api:core-organisation-api-grpc-client")
include("examples:organisations:generated:core-organisation-api:core-organisation-api-grpc-proto")
include("examples:organisations:generated:core-organisation-api:core-organisation-api-model")
include("examples:organisations:generated:core-organisation-api:core-organisation-api-test")

