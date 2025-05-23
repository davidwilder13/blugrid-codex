import { JdlEntity, JdlField } from '../jdl/models/index.js'
import { CodegenEntityFieldModel, CodegenEntityModel } from '../model/index.js'

const kotlinTypeMap: Record<string, string> = {
    String: 'String',
    UUID: 'UUID',
    Long: 'Long',
    Instant: 'LocalDateTime',
    Integer: 'Int',
    Boolean: 'Boolean',
    Double: 'Double',
}

const dbDataTypeMap: Record<string, string> = {
    String: 'VARCHAR',
    UUID: 'UUID',
    Long: 'BIGINT',
    Integer: 'INTEGER',
    Instant: 'TIMESTAMP WITH TIME ZONE',
    Boolean: 'BOOLEAN',
    Double: 'DOUBLE PRECISION',
}

const resourceTypeMap: Record<string, string> = kotlinTypeMap

const camelToUpperSnake = (name: string): string =>
    name.replace(/([a-z])([A-Z])/g, '$1_$2').toUpperCase()

export function mapJdlEntityToCodegenEntity(jdl: JdlEntity, pkg: string): CodegenEntityModel {
    const fields = jdl.fields.map(mapJdlFieldToCodegenField)
    return new CodegenEntityModel(
        jdl.name,
        pkg,
        jdl.getAnnotationValue('tableName') ||
        jdl.name.replace(/([a-z])([A-Z])/g, '$1_$2').toLowerCase(),
        fields,
        (jdl.getAnnotationValue('resourceType') as 'UnscopedResource' | 'TenantResource' | 'BusinessUnitResource') || 'UnscopedResource',
        jdl.hasAnnotation('auditable'),
        jdl.hasAnnotation('searchable'),
    )
}

function mapJdlFieldToCodegenField(field: JdlField): CodegenEntityFieldModel {
    return new CodegenEntityFieldModel(
        field.name,
        kotlinTypeMap[field.type] || field.type,
        resourceTypeMap[field.type] || field.type,
        camelToUpperSnake(field.name),
        dbDataTypeMap[field.type] || 'VARCHAR',
        field.isRequired,
        !field.isRequired,
        field.javadoc || '',
        field.getAnnotation('example'),
    )
}
