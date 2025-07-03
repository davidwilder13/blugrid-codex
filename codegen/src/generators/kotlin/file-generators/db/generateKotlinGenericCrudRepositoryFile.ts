import fs from 'fs-extra';
import path from 'path';
import { CodegenEntityModel } from '../../../../model/CodegenEntityModel.js';
import { KotlinGenericCrudRepositoryTemplate } from '../../templates/db/repository/KotlinGenericCrudRepositoryTemplate.js';

export async function generateKotlinGenericCrudRepositoryFile(
    entity: CodegenEntityModel,
    outputDir: string
) {
    const repositoryDir = path.join(outputDir, 'repository');
    const outputFile = path.join(repositoryDir, `${entity.name}Repository.kt`);

    const context = {
        packageName: entity.packageName,
        entityName: entity.name,
    };

    const rendered = KotlinGenericCrudRepositoryTemplate(context);
    await fs.outputFile(outputFile, rendered);
    console.log(`✅ Generated Repository: ${outputFile}`);
}
