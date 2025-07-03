plugins {
   alias(libs.plugins.jvm)
   alias(libs.plugins.kapt)
   alias(libs.plugins.allopen)
   alias(libs.plugins.jpa)
   alias(libs.plugins.shadow)
   alias(libs.plugins.application)
    //alias(libs.plugins.dockerCompose)
}

repositories {
    mavenCentral()
}

configureIntegTest()

dependencies {
    api(project(":common:common-kotlin:common-api:common-api"))
    api(project(":common:common-kotlin:common-api:common-api-db"))
    api(project(":common:common-kotlin:common-api:common-api-json"))
    api(project(":common:common-kotlin:common-api:common-api-model"))
    api(project(":common:common-kotlin:common-api:common-api-security"))
    testImplementation(project(":common:common-kotlin:common-api:common-api-test"))

    implementation(platform("io.micronaut.platform:micronaut-platform"))
    implementation(platform("aws.sdk.kotlin:bom:1.4.92"))
    kapt(annotationProcessorLibs.bundles.commonAnnotationProcessors)
    implementation(libs.bundles.commonLibs)
    implementation(libs.bundles.dbLibs)
    implementation(libs.bundles.webServiceLibs)
    implementation(libs.bundles.securityLibs)

    runtimeOnly(runTimeLibs.bundles.commonRuntimeLibs)
    runtimeOnly(runTimeLibs.bundles.securityRuntimeLibs)
    runtimeOnly(runTimeLibs.bundles.dbRuntimeLibs)

    compileOnly(libs.bundles.compileOnlyLibs)

    testImplementation(testLibs.bundles.testImplementationLibs) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
}

application {
    mainClass.set("net.blugrid.api.ApplicationKt")
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

fun Project.configureIntegTest() {
    sourceSets {
        create("integTest") {
            compileClasspath += sourceSets.main.get().output
            runtimeClasspath += sourceSets.main.get().output
        }
    }

    val integTestImplementation by configurations.getting {
        extendsFrom(configurations.implementation.get())
        extendsFrom(configurations.testImplementation.get())
    }

    configurations["integTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

    dependencies.add("kaptIntegTest", "io.micronaut:micronaut-inject-java")
    dependencies.add("integTestImplementation", "io.micronaut.test:micronaut-test-junit5")

    tasks.register<Test>("integrationTest") {
        group = "verification"
        description = "Runs integration tests against the application."

        testClassesDirs = sourceSets["integTest"].output.classesDirs
        classpath = sourceSets["integTest"].runtimeClasspath

        shouldRunAfter("test")
    }
}
