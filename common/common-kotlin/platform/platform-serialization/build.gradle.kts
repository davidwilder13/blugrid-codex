plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.allopen)
}

dependencies {
    implementation(platform(libs.micronaut.bom))
    implementation(libs.bundles.jacksonLibs)
    implementation(libs.bundles.jsonLibs)

    testImplementation(libs.bundles.testing)
}

tasks.test {
    useJUnitPlatform()
}
