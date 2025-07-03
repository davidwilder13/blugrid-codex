import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

plugins {
   alias(libs.plugins.jvm)
   alias(libs.plugins.kapt)
   alias(libs.plugins.allopen)
   alias(libs.plugins.shadow)
   alias(libs.plugins.application) apply false
}

allprojects {
    group = "net.blugrid.api"

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

    dependencies {
        testImplementation("io.github.serpro69:kotlin-faker:1.13.0") {
            exclude(group = "org.slf4j", module = "slf4j-api")
        }
    }

    tasks.named<ProcessResources>("processResources") {
        duplicatesStrategy = DuplicatesStrategy.WARN
    }

    plugins.withId("com.github.johnrengelman.shadow") {
        tasks.named<Jar>("shadowJar") {
            isZip64 = true
        }
    }
}

fun Project.configureKotlin() {
    tasks.withType<KotlinCompile<KotlinJvmOptions>>().configureEach {
        kotlinOptions {
            allWarningsAsErrors = false
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }
}

fun Project.configureLintCheck() {
    val configuration = configurations.create("ktlint")
    dependencies.add("ktlint", "com.pinterest:ktlint:0.48.1")

    tasks.register<JavaExec>("ktlint") {
        group = "verification"
        description = "Runs Kotlin lint (code style) checks."

        mainClass.set("com.pinterest.ktlint.Main")
        classpath = configuration
        args("src/**/*.kt", "!**/docker/**", "!**/build/**", "!**/migration/**")
    }

    tasks.register<JavaExec>("ktlintFormat") {
        group = "verification"
        description = "Formats Kotlin source code to match lint (style) rules."

        mainClass.set("com.pinterest.ktlint.Main")
        classpath = configuration
        args("--format", "src/**/*.kt", "!**/docker/**", "!**/build/**", "!**/migration/**")
    }
}

tasks.register("aggregateOpenApiSpecs") {
    group = "openapi"
    description = "Aggregates the OpenAPI specifications from all subprojects"

    doLast {
        // Define the output directory for aggregated OpenAPI specs in the main project
        val outputDir = layout.buildDirectory.dir("tmp/kapt3/classes/main/META-INF/swagger").get().asFile
        outputDir.mkdirs() // Create the directory if it doesn't exist
        println("Output directory: ${outputDir.absolutePath}")

        // Iterate over all subprojects to collect their OpenAPI specs from the correct directory
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
            } else {
                println("Directory does not exist: ${subprojectSwaggerDir.absolutePath}")
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

