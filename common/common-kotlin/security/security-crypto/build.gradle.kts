plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.allopen)
    alias(libs.plugins.kapt)
}

dependencies {
    implementation(project(":common:common-kotlin:security:security-core"))

    implementation(platform(libs.micronaut.bom))
    kapt(platform(libs.micronaut.bom))

    implementation(libs.micronaut.inject)
    implementation(libs.micronaut.core)

    kapt(libs.micronaut.inject.java)

    testImplementation(libs.bundles.testing)
}

tasks.test {
    useJUnitPlatform()
}
