plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.allopen)
    alias(libs.plugins.kapt)
}

dependencies {
    implementation(project(":common:common-kotlin:platform:platform-logging"))
    implementation(project(":common:common-kotlin:platform:platform-config"))
    implementation(project(":common:common-kotlin:data:data-models"))
    implementation(project(":common:common-kotlin:server:server-api"))

    implementation(platform(libs.micronaut.bom))
    kapt(platform(libs.micronaut.bom))

    implementation(libs.micronaut.data.hibernate)
    implementation(libs.micronaut.hibernate.jpa)
    implementation(libs.micronaut.flyway)
    implementation(libs.postgresql)
    implementation(libs.micronaut.inject)
    implementation(libs.micronaut.core)

    kapt(libs.micronaut.inject.java)
    kapt(libs.micronaut.data.processor)

    testImplementation(libs.bundles.testing)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.junit)
}

tasks.test {
    useJUnitPlatform()
}
