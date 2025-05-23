const kotlinTypeImportMap: Record<string, string> = {
    BigDecimal: 'java.math.BigDecimal',
    LocalTime: 'java.time.LocalDateTime',
    LocalDate: 'java.time.LocalDateTime',
    UUID: 'java.util.UUID',
    ZonedDateTime: 'java.time.ZonedDateTime',
}

export function getKotlinImport(type: string): string | undefined {
    return kotlinTypeImportMap[type]
}
