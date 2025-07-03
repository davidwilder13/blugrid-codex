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
    implementation(project(":common:common-kotlin:common-api:common-api-test"))
    implementation(project(":examples:organisations:generated:core-organisation-api:core-organisation-api-model"))

    implementation(platform("io.micronaut.platform:micronaut-platform"))
    implementation(testLibs.bundles.testImplementationLibs) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    kapt(annotationProcessorLibs.bundles.commonAnnotationProcessors)
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
