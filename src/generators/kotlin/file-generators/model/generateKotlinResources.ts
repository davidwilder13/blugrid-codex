import fs from 'fs-extra'
import { camelCase } from 'lodash-es'
import path from 'path'
import { CodegenEntityModel } from '../../../../model/CodegenEntityModel.js'
import { toMustacheList } from '../../../../utils/index.js'
import { KotlinResourceTemplate, KotlinResourceTemplateVariant } from '../../templates/model/KotlinResourceTemplate.js'

export async function generateKotlinResources(
    entity: CodegenEntityModel,
    outputDir: string,
) {
    const outputPath = path.join(outputDir, 'model')

    const variants: { variant: KotlinResourceTemplateVariant; outName: string }[] = [
        { variant: 'model', outName: `${entity.name}Resource.kt` },
        { variant: 'interface', outName: `I${entity.name}.kt` },
        { variant: 'create', outName: `${entity.name}Create.kt` },
        { variant: 'update', outName: `${entity.name}Update.kt` },
    ]

    for (const { variant, outName } of variants) {
        const outputFile = path.join(outputPath, outName)

        const rendered = KotlinResourceTemplate({
            variant,
            packageName: `${entity.packageName}.model`,
            name: entity.name,
            nameLower: camelCase(entity.name),
            nameUpperSnake: entity.tableName.toUpperCase(),
            fields: toMustacheList(entity.fields.map(f => ({
                name: f.name,
                type: f.resourceType,
                required: f.required,
                description: f.description,
                example: f.example,
            }))),
            interfaceFields: toMustacheList(entity.fields.map(f => ({
                name: f.name,
                type: f.resourceType,
                required: f.required,
                description: f.description,
                example: f.example,
            }))),
            imports: toMustacheList(entity.importStatements),
        })

        await fs.outputFile(outputFile, rendered)
        console.log(`✅ Generated ${outputFile}`)
    }
}
