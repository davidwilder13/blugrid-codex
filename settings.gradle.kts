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
            from(files("gradle/commonLibs.versions.toml"))
        }
        create("annotationProcessorLibs") {
            from(files("gradle/annotationProcessorLibs.versions.toml"))
        }
        create("runTimeLibs") {
            from(files("gradle/runTimeLibs.versions.toml"))
        }
        create("testLibs") {
            from(files("gradle/testLibs.versions.toml"))
        }
    }
}

// Common API projects
include("common:common-kotlin:common-api:common-api")
include("common:common-kotlin:common-api:common-api-audit")
include("common:common-kotlin:common-api:common-api-client")
include("common:common-kotlin:common-api:common-api-db")
include("common:common-kotlin:common-api:common-api-domain")
include("common:common-kotlin:common-api:common-api-grpc")
include("common:common-kotlin:common-api:common-api-grpc-proto")
include("common:common-kotlin:common-api:common-api-json")
include("common:common-kotlin:common-api:common-api-logging")
include("common:common-kotlin:common-api:common-api-model")
include("common:common-kotlin:common-api:common-api-multitenant")
include("common:common-kotlin:common-api:common-api-persistence")
include("common:common-kotlin:common-api:common-api-security")
include("common:common-kotlin:common-api:common-api-test")


// JDL-generated example projects (Organisation)
include("examples:organisations:generated:core-organisation-api:core-organisation-api")
include("examples:organisations:generated:core-organisation-api:core-organisation-api-db")
include("examples:organisations:generated:core-organisation-api:core-organisation-api-grpc")
include("examples:organisations:generated:core-organisation-api:core-organisation-api-grpc-client")
include("examples:organisations:generated:core-organisation-api:core-organisation-api-grpc-proto")
include("examples:organisations:generated:core-organisation-api:core-organisation-api-model")
include("examples:organisations:generated:core-organisation-api:core-organisation-api-test")

