import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// ===== TIMEZONE-AWARE EXTENSION FUNCTIONS =====

/**
 * Parse string timestamp consistently as LocalDateTime
 * Treats input as timezone-naive (which matches your test expectations)
 */
fun String.parseAsLocalDateTime(): LocalDateTime {
    return LocalDateTime.parse(this)
}

/**
 * Format LocalDateTime consistently for gRPC responses
 * Uses standard ISO format without timezone info (matches input format)
 */
fun LocalDateTime.toIsoString(): String {
    return this.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}
