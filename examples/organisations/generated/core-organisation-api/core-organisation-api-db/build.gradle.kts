
plugins {
   alias(libs.plugins.jvm)
   alias(libs.plugins.kapt)
   alias(libs.plugins.allopen)
   alias(libs.plugins.jpa)
   alias(libs.plugins.shadow)
   alias(libs.plugins.application)
}

version = "0.1.0"
group = "net.blugrid.api"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":common:common-kotlin:common-api:common-api-model"))
    api(project(":common:common-kotlin:common-api:common-api-json"))
    api(project(":common:common-kotlin:common-api:common-api-db"))
    api(project(":common:common-kotlin:common-api:common-api-persistence"))
    api(project(":common:common-kotlin:common-api:common-api-multitenant"))

    api(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-model"))

    implementation(platform("io.micronaut.platform:micronaut-platform"))
    implementation(platform("aws.sdk.kotlin:bom:1.4.92"))
    kapt(annotationProcessorLibs.bundles.commonAnnotationProcessors)
    implementation(libs.bundles.commonLibs)
    implementation(libs.bundles.dbLibs)

    runtimeOnly(runTimeLibs.bundles.commonRuntimeLibs)
    runtimeOnly(runTimeLibs.bundles.dbRuntimeLibs)

    compileOnly(libs.bundles.compileOnlyLibs)
    testImplementation(project(":common:common-kotlin:common-api:common-api-test"))
    testImplementation(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-test"))
}

application {
    mainClass.set("net.blugrid.core.organisation.ApplicationKt")
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

kapt {
    arguments {
        arg("micronaut.openapi.project.dir", projectDir.toString())
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

graalvmNative.toolchainDetection = false

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("net.blugrid.core.organisation.*")
    }
}
