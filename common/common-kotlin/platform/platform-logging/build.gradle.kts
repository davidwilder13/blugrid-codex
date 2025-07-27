plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.allopen)
}

dependencies {
    implementation(platform(libs.micronaut.bom))

    implementation(libs.logback.classic)
    implementation(libs.kotlin.reflect)
    implementation(libs.jakarta.annotation)
    implementation(libs.micronaut.inject)
    implementation(libs.micronaut.core)

    testImplementation(libs.bundles.testing) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
}

tasks.test {
    useJUnitPlatform()
}
