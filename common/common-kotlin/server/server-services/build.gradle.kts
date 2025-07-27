plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.allopen)
    alias(libs.plugins.kapt)
}

dependencies {
    implementation(project(":common:common-kotlin:platform:platform-logging"))
    implementation(project(":common:common-kotlin:security:security-core"))
    implementation(project(":common:common-kotlin:security:security-authentication"))
    implementation(project(":common:common-kotlin:server:server-persistence"))

    implementation(platform(libs.micronaut.bom))
    kapt(platform(libs.micronaut.bom))

    implementation(libs.micronaut.inject)
    implementation(libs.micronaut.core)
    implementation(libs.micronaut.runtime)

    kapt(libs.micronaut.inject.java)

    testImplementation(libs.bundles.testing)
    testImplementation(libs.micronaut.test.junit5)
}

tasks.test {
    useJUnitPlatform()
}
