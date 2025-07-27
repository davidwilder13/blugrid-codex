plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen)
}

dependencies {
    // API dependencies
    api(project(":common:common-kotlin:common:common-model"))
    api(project(":common:common-kotlin:platform:platform-logging"))

    // Micronaut dependencies
    implementation(libs.bundles.micronautCore)
    implementation(libs.bundles.micronautData)

    // Platform BOMs
    implementation(platform(libs.micronaut.bom))
    implementation(platform(libs.aws.bom))

    // Core dependencies
    implementation(libs.bundles.kotlinCore)

    // Annotation processing
    kapt(libs.bundles.annotationProcessors)

    // Compile-only dependencies
    compileOnly(libs.bundles.compileOnly)

    // Test dependencies
    testImplementation(libs.bundles.testing)
}

java {
    sourceCompatibility = JavaVersion.toVersion("17")
}

kapt {
    arguments {
        arg("micronaut.openapi.project.dir", projectDir.toString())
    }
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("net.blugrid.*")
    }
}
