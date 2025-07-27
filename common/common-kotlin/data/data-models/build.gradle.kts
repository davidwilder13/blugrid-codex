plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.allopen)
    alias(libs.plugins.kapt)
}

dependencies {
    implementation(project(":common:common-kotlin:platform:platform-serialization"))
    implementation(project(":common:common-kotlin:platform:platform-validation"))

    implementation(platform(libs.micronaut.bom))
    kapt(platform(libs.micronaut.bom))

    implementation(libs.bundles.micronautCore)
    implementation(libs.bundles.jacksonLibs)
    implementation(libs.bundles.validationLibs)

    kapt(libs.bundles.annotationProcessors)

    testImplementation(libs.bundles.testing)
}

tasks.test {
    useJUnitPlatform()
}
