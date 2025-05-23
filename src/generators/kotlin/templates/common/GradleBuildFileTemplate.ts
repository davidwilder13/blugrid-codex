import Mustache from 'mustache'
import { KotlinModuleType } from '../../model/KotlinModule.js'

export interface GradleModuleTemplateProps {
    group: string;
    version: string;
    packageName: string;
    mainClassName?: string; // e.g., "net.blugrid.api.ApplicationKt"

    // dependencies
    coreDependencies?: {
       name: string;
       type: KotlinModuleType
    }[]

    // Framework flags
    includeDb?: boolean
    includeWebService?: boolean
    includeSecurity?: boolean
    includeTest?: boolean
}

const gradleBuildTemplate = String.raw`
plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.kapt)
    alias(libs.plugins.allopen){{#includeDb}}
    alias(libs.plugins.jpa){{/includeDb}}
    alias(libs.plugins.shadow)
    alias(libs.plugins.application)
}

version = "{{version}}"
group = "{{group}}"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":common-api:common-api:common-api-model"))
    api(project(":common-api:common-api:common-api-json")){{#includeDb}}
    api(project(":common-api:common-api:common-api-db"))
    api(project(":common-api:common-api:common-api-multitenant")){{/includeDb}}{{#includeSecurity}}
    api(project(":common-api:common-api:common-api-security")){{/includeSecurity}}
    
    {{#coreDependencies}}
    api(project(":core-api:core-{{name}}-api:core-{{name}}-api-{{type}}")){{/coreDependencies}}

    implementation(platform("io.micronaut.platform:micronaut-platform"))
    kapt(annotationProcessorLibs.bundles.commonAnnotationProcessors)
    implementation(libs.bundles.commonLibs){{#includeDb}}
    implementation(libs.bundles.dbLibs){{/includeDb}}{{#includeWebService}}
    implementation(libs.bundles.webServiceLibs){{/includeWebService}}{{#includeSecurity}}
    implementation(libs.bundles.securityLibs){{/includeSecurity}}

    runtimeOnly(runTimeLibs.bundles.commonRuntimeLibs){{#includeDb}}
    runtimeOnly(runTimeLibs.bundles.dbRuntimeLibs){{/includeDb}}{{#includeSecurity}}
    runtimeOnly(runTimeLibs.bundles.securityRuntimeLibs){{/includeSecurity}}

    compileOnly(libs.bundles.compileOnlyLibs){{#includeTest}}
    testImplementation(project(":common-api:common-api:common-api-test"))
    testImplementation(testLibs.bundles.testImplementationLibs) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }{{/includeTest}}
}

{{#mainClassName}}application {
    mainClass.set("{{mainClassName}}")
}
{{/mainClassName}}

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
        annotations("{{packageName}}.*")
    }
}
`

export const GradleBuildFileTemplate = (props: GradleModuleTemplateProps): string =>
    Mustache.render(gradleBuildTemplate, props)
        .replace(/\n{3,}/g, '\n\n')
