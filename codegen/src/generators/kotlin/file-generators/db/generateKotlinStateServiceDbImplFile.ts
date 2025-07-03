import fs from 'fs-extra'
import path from 'path'
import { CodegenEntityModel } from '../../../../model/index.js'
import { KotlinKotlinStateServiceDbImplTemplate } from '../../templates/db/service/KotlinStateServiceDbImplTemplate.js'

export async function generateKotlinStateServiceDbImplFile(
    entity: CodegenEntityModel,
    outputDir: string,
) {
    const repositoryDir = path.join(outputDir, 'service')
    const outputFile = path.join(repositoryDir, `${entity.name}StateServiceDb.kt`)

    const rendered = KotlinKotlinStateServiceDbImplTemplate({ entityName: entity.name, packageName: entity.packageName, entityFolder: entity.name.toLowerCase() })
    await fs.outputFile(outputFile, rendered)
    console.log(`✅ Generated State Service DB Impl: ${outputFile}`)
}
