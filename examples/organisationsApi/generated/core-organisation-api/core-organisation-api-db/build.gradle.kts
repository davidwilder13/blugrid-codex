
plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
    alias(libs.plugins.shadow)
    alias(libs.plugins.application)
}

version = "0.1.0"
group = "net.blugrid.api"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":common-api:common-api:common-api-model"))

    implementation(platform("io.micronaut.platform:micronaut-platform"))
    kapt(annotationProcessorLibs.bundles.commonAnnotationProcessors)
    implementation(libs.bundles.commonLibs)
    implementation(libs.bundles.webServiceLibs)

    runtimeOnly(runTimeLibs.bundles.commonRuntimeLibs)

    compileOnly(libs.bundles.compileOnlyLibs)
    testImplementation(testLibs.bundles.testImplementationLibs) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
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
