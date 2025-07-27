plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.allopen)
    alias(libs.plugins.kapt)
}

dependencies {
    implementation(project(":common:common-kotlin:common:common-model"))
    implementation(project(":common:common-kotlin:platform:platform-config"))
    implementation(project(":common:common-kotlin:platform:platform-logging"))
    implementation(project(":common:common-kotlin:platform:platform-serialization"))
    implementation(project(":common:common-kotlin:security:security-core"))
    implementation(project(":common:common-kotlin:security:security-tokens"))
    implementation(project(":common:common-kotlin:security:security-authentication"))

    implementation(platform(libs.micronaut.bom))
    kapt(platform(libs.micronaut.bom))

    implementation(libs.micronaut.http.server)
    implementation(libs.micronaut.http.client)
    implementation(libs.micronaut.inject)
    implementation(libs.micronaut.core)
    implementation(libs.micronaut.runtime)
    implementation(libs.micronaut.security)
    implementation(libs.micronaut.security.oauth2)
    implementation(libs.nimbus.jose.jwt)

    implementation(libs.bundles.micronautReactive)
    implementation(libs.bundles.jacksonLibs)

    kapt(libs.micronaut.inject.java)

    testImplementation(libs.bundles.testing)
    testImplementation(libs.micronaut.test.junit5)
}

tasks.test {
    useJUnitPlatform()
}
