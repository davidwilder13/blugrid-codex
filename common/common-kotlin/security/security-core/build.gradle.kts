plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.allopen)
    alias(libs.plugins.kapt)
}

dependencies {
    implementation(project(":common:common-kotlin:platform:platform-logging"))
    implementation(project(":common:common-kotlin:common:common-model"))

    implementation(platform(libs.micronaut.bom))
    kapt(platform(libs.micronaut.bom))

    implementation(libs.micronaut.inject)
    implementation(libs.micronaut.core)

    implementation(libs.bundles.jacksonLibs)
    kapt(libs.bundles.jacksonLibs)

    kapt(libs.micronaut.inject.java)

    testImplementation(libs.bundles.testing)
}

tasks.test {
    useJUnitPlatform()
}
