import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.allopen) apply false
    alias(libs.plugins.jpa) apply false
    alias(libs.plugins.application) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.protobuf) apply false
}

allprojects {
    group = "net.blugrid.api"
    version = project.findProperty("version") ?: "0.1.0"

    repositories {
        mavenCentral()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    }

    configureLintCheck()
}

subprojects {
    apply(plugin = "java")

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    configureKotlin()

    // Common test dependency for all projects
    dependencies {
        testImplementation("io.github.serpro69:kotlin-faker:1.13.0") {
            exclude(group = "org.slf4j", module = "slf4j-api")
        }
    }

    // Resource processing configuration
    tasks.named<ProcessResources>("processResources") {
        duplicatesStrategy = DuplicatesStrategy.WARN
    }

    // Shadow JAR configuration when plugin is applied
    plugins.withId("com.github.johnrengelman.shadow") {
        tasks.named<Jar>("shadowJar") {
            isZip64 = true
        }
    }
}

fun Project.configureKotlin() {
    // Modern Kotlin compilation configuration
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            javaParameters.set(true)
            allWarningsAsErrors.set(false)
        }
    }
}

fun Project.configureLintCheck() {
    val ktlintConfig = configurations.create("ktlint")
    dependencies.add("ktlint", "com.pinterest:ktlint:0.48.1")

    tasks.register<JavaExec>("ktlint") {
        group = "verification"
        description = "Runs Kotlin lint checks"
        mainClass.set("com.pinterest.ktlint.Main")
        classpath = ktlintConfig
        args("src/**/*.kt", "!**/build/**", "!**/migration/**")
    }

    tasks.register<JavaExec>("ktlintFormat") {
        group = "verification"
        description = "Formats Kotlin source code"
        mainClass.set("com.pinterest.ktlint.Main")
        classpath = ktlintConfig
        args("--format", "src/**/*.kt", "!**/build/**", "!**/migration/**")
    }
}

// OpenAPI aggregation task
tasks.register("aggregateOpenApiSpecs") {
    group = "openapi"
    description = "Aggregates OpenAPI specifications from all subprojects"

    doLast {
        // Define the output directory for aggregated OpenAPI specs in the main project
        val outputDir = layout.buildDirectory.dir("tmp/kapt3/classes/main/META-INF/swagger").get().asFile
        outputDir.mkdirs() // Create the directory if it doesn't exist
        println("Output directory: ${outputDir.absolutePath}")

        subprojects.forEach { subproject ->
            val subprojectSwaggerDir = subproject.layout.buildDirectory.dir("tmp/kapt3/classes/main/META-INF/swagger").get().asFile
            println("Checking subproject directory: ${subprojectSwaggerDir.absolutePath}")

            if (subprojectSwaggerDir.exists()) {
                println("Directory exists. Files in directory:")
                subprojectSwaggerDir.listFiles()?.forEach { file ->
                    println(" - Found file: ${file.name}")
                    val destinationFile = file("${outputDir}/${file.name}")
                    file.copyTo(destinationFile, overwrite = true)
                    println("Copied file to: ${destinationFile.absolutePath}")
                } ?: run {
                    println("No files found in ${subprojectSwaggerDir.absolutePath}")
                }
            }
        }
    }
}

tasks.named("build").configure {
    dependsOn("aggregateOpenApiSpecs")
}

gradle.settingsEvaluated {
    gradle.rootProject.allprojects {
        tasks.withType<JavaCompile>().configureEach {
            options.release.set(17)
        }
    }
}
