import fs from 'fs-extra'
import path from 'path'
import { KotlinMappingServiceTemplate } from '../../templates/db/mapping/KotlinMappingServiceTemplate.js'
import { CodegenEntityModel } from '../../../../model/CodegenEntityModel.js'

export async function generateKotlinMappingServiceFile(
    entity: CodegenEntityModel,
    outputDir: string,
) {
    const mappingDir = path.join(outputDir, 'mapping')
    const outputFile = path.join(mappingDir, `${entity.name}MappingService.kt`)

    const kotlinCode = KotlinMappingServiceTemplate({ packageName: entity.packageName, entityName: entity.name })

    await fs.outputFile(outputFile, kotlinCode, 'utf-8')
    console.log(`✅ Generated Kotlin Mapping Service: ${outputFile}`)
}
