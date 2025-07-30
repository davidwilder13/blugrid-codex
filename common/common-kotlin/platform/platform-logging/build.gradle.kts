plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.allopen)
    alias(libs.plugins.kapt)
}

dependencies {
    api(project(":common:common-kotlin:common:common-model"))

    implementation(platform(libs.micronaut.bom))
    kapt(platform(libs.micronaut.bom))

    implementation(libs.logback.classic)
    implementation(libs.kotlin.reflect)
    implementation(libs.jakarta.annotation)
    implementation(libs.micronaut.inject)
    implementation(libs.micronaut.core)

    kapt(libs.micronaut.inject.java)
    kapt(libs.bundles.annotationProcessors)

    testImplementation(libs.bundles.testing) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
}

kapt {
    arguments {
        arg("micronaut.openapi.project.dir", projectDir.toString())
        arg("micronaut.processing.incremental", "true")
        arg("micronaut.processing.annotations", "net.blugrid.platform.logging")
    }
}

tasks.test {
    useJUnitPlatform()
}
