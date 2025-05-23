function mapJdlTypeToKotlin(jdlType: string): string {
    switch (jdlType) {
        case 'String':
            return 'String'
        case 'UUID':
            return 'UUID'
        case 'Long':
        case 'Integer':
        case 'BigInteger':
            return 'Long'
        case 'Float':
        case 'Double':
        case 'BigDecimal':
            return 'Double'
        case 'Boolean':
            return 'Boolean'
        case 'Instant':
        case 'ZonedDateTime':
        case 'LocalDate':
            return 'LocalDateTime'
        case 'byte[]':
            return 'ByteArray'
        case 'byte[] (image)':
        case 'ImageBlob':
            return 'ByteArray' // or a custom wrapper like `ImageData` if needed
        default:
            return jdlType // fallback for enums or custom types
    }
}

function getExampleForType(jdlType: string): string {
    switch (jdlType) {
        case 'UUID':
            return '123e4567-e89b-12d3-a456-426614174000'
        case 'String':
            return 'Example String'
        case 'Long':
        case 'Integer':
        case 'BigInteger':
            return '1001'
        case 'Float':
        case 'Double':
        case 'BigDecimal':
            return '123.45'
        case 'Boolean':
            return 'true'
        case 'Instant':
        case 'ZonedDateTime':
        case 'LocalDate':
            return '2024-08-25T14:15:22'
        case 'byte[]':
        case 'byte[] (image)':
        case 'ImageBlob':
            return '<binary>'
        default:
            return jdlType // fallback for enums or custom types
    }
}

function mapKotlinTypeToDbDomain(kotlinType: string): string {
    switch (kotlinType) {
        case 'UUID':
            return 't_uuid'
        case 'String':
            return 't_text'
        case 'Boolean':
            return 't_boolean'
        case 'Int':
        case 'Long':
            return 't_identity'
        case 'BigDecimal':
            return 't_money'
        case 'Float':
        case 'Double':
            return 't_float'
        case 'LocalDate':
            return 't_datetime'
        case 'LocalDateTime':
        case 'Instant':
            return 't_timestamp'
        case 'ZonedDateTime':
            return 't_timestampz'
        case 'Short':
            return 't_short_count'
        case 'Byte':
            return 't_code1'
        default:
            return 't_unknown'
    }
}


export { mapJdlTypeToKotlin, getExampleForType, mapKotlinTypeToDbDomain }
