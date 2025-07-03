import fs from 'fs-extra'
import { camelCase } from 'lodash-es'
import path from 'path'
import { KotlinStateServiceInterfaceTemplate } from '../../templates/model/KotlinStateServiceInterfaceTemplate.js'

export async function generateKotlinServiceInterfaceFile(
    entityName: string,
    packageName: string,
    outputDir: string,
) {
    const repositoryDir = path.join(outputDir, 'service')
    const outputFile = path.join(repositoryDir, `${entityName}StateService.kt`)

    const rendered = KotlinStateServiceInterfaceTemplate({ entityName, entityFolder: camelCase(entityName), packageName })
    await fs.outputFile(outputFile, rendered)
    console.log(`✅ Generated Service Interface: ${outputFile}`)
}
