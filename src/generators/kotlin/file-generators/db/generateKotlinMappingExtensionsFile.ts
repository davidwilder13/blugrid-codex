import fs from 'fs-extra'
import path from 'path'
import { KotlinMappingExtensionsTemplate } from '../../templates/db/mapping/KotlinMappingExtensionsTemplate.js'
import { CodegenEntityModel } from '../../../../model/CodegenEntityModel.js'

export async function generateKotlinMappingExtensionsFile(
    entity: CodegenEntityModel,
    outputDir: string,
) {
    const mappingDir = path.join(outputDir, 'mapping')
    const outputFile = path.join(mappingDir, `${entity.name}MappingExtensions.kt`)

    const kotlinCode = KotlinMappingExtensionsTemplate(entity)

    await fs.outputFile(outputFile, kotlinCode, 'utf-8')
    console.log(`✅ Generated Kotlin Mapping Extensions: ${outputFile}`)
}
