import { camelCase, compact, snakeCase } from 'lodash-es'
import { JdlEntity, JdlField } from '../jdl/models/index.js'
import { CodegenEntityFieldModel, CodegenEntityModel } from '../model/index.js'

const jdlToKotlinTypeMap: Record<string, string> = {
    String: 'String',
    UUID: 'UUID',
    Long: 'Long',
    Integer: 'Long',
    BigInteger: 'Long',
    Float: 'Double',
    Double: 'Double',
    BigDecimal: 'Double',
    Boolean: 'Boolean',
    Instant: 'LocalDateTime',
    ZonedDateTime: 'LocalDateTime',
    LocalDate: 'LocalDateTime',
    'byte[]': 'ByteArray',
    'byte[] (image)': 'ByteArray',
    ImageBlob: 'ByteArray',
}

const kotlinTypeExampleMap: Record<string, string> = {
    UUID: '123e4567-e89b-12d3-a456-426614174000',
    String: 'Example String',
    Long: '1001',
    Int: '1001',
    Integer: '1001',
    BigInteger: '1001',
    Float: '123.45',
    Double: '123.45',
    BigDecimal: '123.45',
    Boolean: 'true',
    Instant: '2024-08-25T14:15:22Z',
    ZonedDateTime: '2024-08-25T14:15:22+10:00',
    LocalDateTime: '2024-08-25T14:15:22',
    LocalDate: '2024-08-25',
    'byte[]': '<binary>',
    'byte[] (image)': '<binary>',
    ImageBlob: '<binary>',
}

const kotlinTypeImportMap: Record<string, string> = {
    BigDecimal: 'java.math.BigDecimal',
    LocalDate: 'java.time.LocalDateTime',
    LocalDateTime: 'java.time.LocalDateTime',
    LocalTime: 'java.time.LocalDateTime',
    UUID: 'java.util.UUID',
    ZonedDateTime: 'java.time.ZonedDateTime',
}

const jdlToDbDomainMap: Record<string, string> = {
    UUID: 't_uuid',
    String: 't_text',
    Boolean: 't_boolean',
    Int: 'bigint',
    Long: 'bigint',
    BigDecimal: 't_money',
    Float: 't_float',
    Double: 't_float',
    LocalDate: 't_datetime',
    LocalDateTime: 't_timestamp',
    Instant: 't_timestamp',
    ZonedDateTime: 't_timestampz',
    Short: 't_short_count',
    Byte: 't_code1',
    ByteArray: 't_bytea',
}

function cleanJavadoc(javadoc: string | null): string | undefined {
    if (!javadoc) return undefined
    return javadoc
        .replace(/(^\s*\*\s*)/gm, '') // remove leading '*'
        .replace(/\r?\n|\r/g, ' ')    // collapse newlines to spaces
        .trim()
}

const snakeLowerCase = (name: string): string => snakeCase(name).toLowerCase()

export function mapJdlEntityToCodegenEntity(jdl: JdlEntity, basePackage: string): CodegenEntityModel {
    const fields = jdl.fields.map(mapJdlFieldToCodegenField)
    const imports = compact(fields.map(f => kotlinTypeImportMap[f.kotlinType]))
    return new CodegenEntityModel(
        jdl.name,
        `${basePackage}.${camelCase(jdl.name)}`,
        snakeLowerCase(jdl.name),
        fields,
        imports,
        (jdl.getAnnotationValue('resourceType') as 'UnscopedResource' | 'TenantResource' | 'BusinessUnitResource') || 'UnscopedResource',
        jdl.hasAnnotation('Auditable'),
        jdl.hasAnnotation('Searchable'),
    )
}

function mapJdlFieldToCodegenField(field: JdlField): CodegenEntityFieldModel {
    return new CodegenEntityFieldModel(
        field.name,
        jdlToKotlinTypeMap[field.type],
        jdlToKotlinTypeMap[field.type],
        snakeCase(field.name),
        jdlToDbDomainMap[field.type] || 'text',
        field.validations.some(v => v.name === 'required'),
        !field.validations.some(v => v.name === 'required'),
        cleanJavadoc(field.javadoc),
        kotlinTypeExampleMap[field.type],
    )
}
