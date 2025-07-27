plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.allopen)
}

dependencies {
    implementation(platform(libs.micronaut.bom))

    implementation(libs.jakarta.validation)

    testImplementation(libs.bundles.testing)
}

tasks.test {
    useJUnitPlatform()
}
