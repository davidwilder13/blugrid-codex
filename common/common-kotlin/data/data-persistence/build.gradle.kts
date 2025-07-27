plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.allopen)
    alias(libs.plugins.kapt)
    alias(libs.plugins.application)
}

dependencies {
    api(project(":common:common-kotlin:common:common-model"))
    api(project(":common:common-kotlin:security:security-core"))
    api(project(":common:common-kotlin:platform:platform-config"))
    api(project(":common:common-kotlin:platform:platform-serialization"))

    implementation(project(":common:common-kotlin:common:common-domain"))
    implementation(project(":common:common-kotlin:platform:platform-logging"))

    // Platform BOM
    implementation(platform(libs.micronaut.bom))

    // Core dependencies
    implementation(libs.bundles.kotlinCore)

    // Data persistence stack using new bundles
    implementation(libs.bundles.micronautData)

    // Validation support
    implementation(libs.jakarta.validation)

    // Test dependencies
    testImplementation(libs.bundles.testing) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
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

tasks.test {
    useJUnitPlatform()
}
