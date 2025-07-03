plugins {
    alias(libs.plugins.jvm)
}

dependencies {
    api(libs.jakarta.validation)

    // Domain modules should be pure - no framework dependencies
    implementation(libs.bundles.kotlinCore)
}
