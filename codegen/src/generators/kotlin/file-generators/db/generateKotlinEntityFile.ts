import fs from 'fs-extra'
import { capitalize } from 'lodash-es'
import path from 'path'
import { CodegenEntityModel } from '../../../../model/index.js'
import { toMustacheList } from '../../../../utils/to-mustache-list.js'
import { KotlinEntityTemplate } from '../../templates/db/repository/KotlinEntityTemplate.js'

export async function generateKotlinEntityFile(
    entity: CodegenEntityModel,
    outputDir: string,
) {
    const repositoryModelDir = path.join(outputDir, 'repository', 'model')
    const outputPath = path.join(repositoryModelDir, `${entity.name}Entity.kt`)

    const entityType = toEntityType(entity.resourceType)

    const rendered = KotlinEntityTemplate({
        packageName: entity.packageName,
        entityName: capitalize(entity.name),
        tableName: `vw_${entity.tableName}`,
        sequenceName: `${entity.tableName}-sequence`,
        generatorStrategy: 'net.blugrid.api.db.GlobalTenantSequenceGenerator',
        fields: toMustacheList(entity.fields
            .filter(f => f.name !== 'uuid' && f.name !== 'id')
            .map(f => ({
                name: f.name,
                type: f.kotlinType,
                columnName: f.dbColumnName,
                nullable: f.nullable,
                updatable: false, // ← can later map from JDL if available
            })),
        ),
        entityType,
        isAudited: entity.isAuditable,
    })

    await fs.outputFile(outputPath, rendered)
    console.log(`✅ Generated Entity: ${outputPath}`)
}

function toEntityType(type: string): 'generic' | 'unscoped' | 'tenantScoped' | 'businessUnitScoped' {
    switch (type) {
        case 'TenantResource':
            return 'tenantScoped'
        case 'BusinessUnitResource':
            return 'businessUnitScoped'
        case 'UnscopedResource':
            return 'unscoped'
        default:
            return 'generic'
    }
}
