import fs from 'fs-extra'
import mustache from 'mustache'
import path from 'path'
import { resolveTemplate } from '../../../../utils/resolve-template.js'
import { KotlinModule } from '../../model/KotlinModule.js'
import { GradleBuildFileTemplate } from '../../templates/common/GradleBuildFileTemplate.js'
import { GradlePropertiesTemplate } from '../../templates/common/GradlePropertiesTemplate.js'

export async function generateCommonModuleFiles({
                                                    group,
                                                    version,
                                                    packageName,
                                                    mainClassName,
                                                    coreDependencies,
                                                    includeDb,
                                                    includeSecurity,
                                                    includeWebService,
                                                    includeTest,
                                                    name,
                                                    gradleBuildFile,
                                                    gradlePropsFile,
                                                    gradlewFiles,
                                                    gradlewBatFile,
                                                }: KotlinModule, outputDir: string): Promise<void> {
    // Gradle build file (build.gradle.kts)
    const gradleBuild = GradleBuildFileTemplate({ group, version, packageName, mainClassName, coreDependencies, includeDb, includeSecurity, includeWebService, includeTest })
    await fs.outputFile(path.join(outputDir, gradleBuildFile), gradleBuild)

    // gradle.properties
    const gradleProps = GradlePropertiesTemplate()
    await fs.outputFile(path.join(outputDir, gradlePropsFile), gradleProps)

    // 3. Copy and render `gradlew` (mustache-based)
    const gradlewTemplate = await fs.readFile(resolveTemplate('gradlew.mustache'), 'utf8')
    const gradlewRendered = mustache.render(gradlewTemplate, null)
    await fs.outputFile(path.join(outputDir, gradlewFiles), gradlewRendered)

    // 4. Copy and render `gradlew.bat`
    const gradlewBatTemplate = await fs.readFile(resolveTemplate('gradlew.bat.mustache'), 'utf8')
    const gradlewBatRendered = mustache.render(gradlewBatTemplate, null)
    await fs.outputFile(path.join(outputDir, gradlewBatFile), gradlewBatRendered)

    console.log(`✅ Generated common Gradle files for ${name} in ${outputDir}`)
}
