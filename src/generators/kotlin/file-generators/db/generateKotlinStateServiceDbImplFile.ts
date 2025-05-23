import fs from 'fs-extra'
import path from 'path'
import { KotlinKotlinStateServiceDbImplTemplate } from '../../templates/db/service/KotlinStateServiceDbImplTemplate.js'

export async function generateKotlinStateServiceDbImplFile(
    entityName: string,
    packageName: string,
    outputDir: string,
) {
    const repositoryDir = path.join(outputDir, 'service')
    const outputFile = path.join(repositoryDir, `${entityName}StateServiceDb.kt`)

    const rendered = KotlinKotlinStateServiceDbImplTemplate({ entityName, packageName, entityFolder: entityName.toLowerCase() })
    await fs.outputFile(outputFile, rendered)
    console.log(`✅ Generated State Service DB Impl: ${outputFile}`)
}
