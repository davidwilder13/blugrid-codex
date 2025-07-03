plugins {
   alias(libs.plugins.jvm)
   alias(libs.plugins.kapt)
   alias(libs.plugins.allopen)
   alias(libs.plugins.jpa)
   alias(libs.plugins.shadow)
   alias(libs.plugins.application)
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":common:common-kotlin:common-api:common-api-model"))

    implementation(libs.bundles.commonLibs)
    implementation(libs.bundles.webServiceLibs)
    implementation(libs.bundles.securityLibs)
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
}
kapt {
    arguments {
        arg("micronaut.openapi.project.dir", projectDir.toString())
    }
}
kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

graalvmNative.toolchainDetection = false
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("net.blugrid.api.*")
    }
}

application {
    mainClass.set("net.blugrid.api.ApplicationKt")
}

