plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.allopen)
    alias(libs.plugins.kapt)
}

dependencies {
    implementation(platform(libs.micronaut.bom))
    kapt(platform(libs.micronaut.bom))

    implementation(libs.micronaut.inject)
    implementation(libs.micronaut.core)
    implementation(libs.micronaut.runtime)
    implementation(libs.micronaut.validation)
    implementation(libs.jakarta.validation)

    kapt(libs.micronaut.inject.java)

    testImplementation(libs.bundles.testing)
}

tasks.test {
    useJUnitPlatform()
}
