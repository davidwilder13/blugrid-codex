plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.allopen)
    alias(libs.plugins.kapt)
}

dependencies {
    implementation(project(":common:common-kotlin:common:common-model"))
    implementation(project(":common:common-kotlin:data:data-persistence"))
    implementation(project(":common:common-kotlin:platform:platform-config"))
    implementation(project(":common:common-kotlin:platform:platform-logging"))
    implementation(project(":common:common-kotlin:security:security-core"))
    implementation(project(":common:common-kotlin:server:server-api"))

    implementation(platform(libs.micronaut.bom))
    kapt(platform(libs.micronaut.bom))

    implementation(libs.micronaut.multitenancy)
    implementation(libs.micronaut.data.hibernate)
    implementation(libs.postgresql)
    implementation(libs.micronaut.inject)
    implementation(libs.micronaut.core)

    kapt(libs.micronaut.inject.java)

    testImplementation(libs.bundles.testing)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.junit)
}

tasks.test {
    useJUnitPlatform()
}
