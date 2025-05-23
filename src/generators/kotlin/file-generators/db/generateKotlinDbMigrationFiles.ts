import fs from 'fs-extra'
import path from 'path'
import { CodegenEntityModel } from '../../../../model/index.js'
import { toMustacheList } from '../../../../utils/index.js'
import { KotlinModule } from '../../model/KotlinModule.js'
import { RepeatableViewMigrationTemplate } from '../../templates/db/migrations/index.js'
import { CreateTableSQLTemplate, CreateViewSQLTemplate } from '../../templates/db/sql/index.js'

export async function generateKotlinDbMigrationFiles(
    entity: CodegenEntityModel,
    module: KotlinModule,
    outputDir: string,
) {
    const name = entity.tableName;
    const migrationDir = path.join(outputDir, 'migration');
    await fs.mkdir(migrationDir, { recursive: true });

    // --- Table Migration ---
    const tableSql = CreateTableSQLTemplate({
        name,
        columns: toMustacheList(entity.fields.map(field => ({
            name: field.dbColumnName,
            dataType: field.dbDataType,
            nullable: field.nullable,
        }))),
        scope:
            entity.resourceType === 'TenantResource'
                ? 'tenantScoped'
                : entity.resourceType === 'BusinessUnitResource'
                    ? 'businessUnitScoped'
                    : 'unscoped',
    });

    const tableKt = RepeatableViewMigrationTemplate({
        packageName: module.packageName,
        nameLower: name,
        createOrReplaceSql: tableSql,
    });

    const tableKtPath = path.join(migrationDir, `R__5_table_${name}.kt`);
    await fs.writeFile(tableKtPath, tableKt, 'utf-8');

    // --- View Migration ---
    const viewSql = CreateViewSQLTemplate({
        name,
        type:
            entity.resourceType === 'TenantResource'
                ? 'tenantScoped'
                : entity.resourceType === 'BusinessUnitResource'
                    ? 'businessUnitScoped'
                    : 'unscoped',
        unscoped: entity.resourceType === 'UnscopedResource',
    });

    const viewKt = RepeatableViewMigrationTemplate({
        packageName: module.packageName,
        nameLower: name,
        createOrReplaceSql: viewSql,
    });

    const viewKtPath = path.join(migrationDir, `R__6_view_${name}.kt`);
    await fs.writeFile(viewKtPath, viewKt, 'utf-8');

    console.log(`✅ Generated DB table + view migrations for ${name}`);
}
