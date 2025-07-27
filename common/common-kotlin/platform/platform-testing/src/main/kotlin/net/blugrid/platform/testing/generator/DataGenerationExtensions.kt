package net.blugrid.platform.testing.generator

import net.blugrid.common.domain.IdentityID
import net.blugrid.common.domain.IdentityUUID
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.random.Random

/**
 * Pure Kotlin test data generation extensions
 * No external dependencies, fully deterministic and reliable
 */

// =============================================================================
// RANDOM INSTANCE CONFIGURATION
// =============================================================================

/**
 * Global random instance with fixed seed for deterministic tests
 */
val globalRandom = Random(42)

/**
 * Create a seeded random instance for deterministic testing
 */
fun createSeededRandom(seed: Long = System.currentTimeMillis()): Random = Random(seed)

// =============================================================================
// NUMERIC TYPES
// =============================================================================

/**
 * Generate a random Long within the specified range
 */
fun Long.Companion.random(start: Long = 0L, end: Long = Long.MAX_VALUE, random: Random = globalRandom): Long =
    random.nextLong(start, end)

/**
 * Generate a random positive Long
 */
fun Long.Companion.randomPositive(random: Random = globalRandom): Long = random(1L, Long.MAX_VALUE, random)

/**
 * Generate a random ID-like Long (1 to 1 million)
 */
fun Long.Companion.randomId(random: Random = globalRandom): Long = random(1L, 1_000_000L, random)

/**
 * Generate a random Int within the specified range
 */
fun Int.Companion.random(start: Int = 0, end: Int = Int.MAX_VALUE, random: Random = globalRandom): Int =
    random.nextInt(start, end)

/**
 * Generate a random positive Int
 */
fun Int.Companion.randomPositive(random: Random = globalRandom): Int = random(1, Int.MAX_VALUE, random)

/**
 * Generate a random ID-like Int (1 to 100,000)
 */
fun Int.Companion.randomId(random: Random = globalRandom): Int = random(1, 100_000, random)

/**
 * Generate a random Double within the specified range
 */
fun Double.Companion.random(start: Double = 0.0, end: Double = Double.MAX_VALUE, random: Random = globalRandom): Double =
    start + random.nextDouble() * (end - start)

/**
 * Generate a random price-like Double (0.01 to 10,000.00)
 */
fun Double.Companion.randomPrice(random: Random = globalRandom): Double =
    (random(0.01, 10_000.0, random) * 100).toInt() / 100.0

/**
 * Generate a random percentage (0.0 to 100.0)
 */
fun Double.Companion.randomPercentage(random: Random = globalRandom): Double = random(0.0, 100.0, random)

/**
 * Generate a random Float within the specified range
 */
fun Float.Companion.random(start: Float = 0f, end: Float = Float.MAX_VALUE, random: Random = globalRandom): Float =
    start + random.nextFloat() * (end - start)

// =============================================================================
// BIGDECIMAL HELPERS (No companion object available)
// =============================================================================

/**
 * BigDecimal random generation utilities
 */
object BigDecimalRandom {

    /**
     * Generate a random BigDecimal within the specified range
     */
    fun random(
        start: BigDecimal = BigDecimal.ZERO,
        end: BigDecimal = BigDecimal("999999.99"),
        scale: Int = 2,
        random: Random = globalRandom
    ): BigDecimal {
        val range = end.subtract(start)
        val randomValue = range.multiply(BigDecimal(random.nextDouble()))
        return start.add(randomValue).setScale(scale, RoundingMode.HALF_UP)
    }

    /**
     * Generate a random currency amount
     */
    fun randomCurrency(scale: Int = 2, random: Random = globalRandom): BigDecimal =
        random(BigDecimal("0.01"), BigDecimal("100000.00"), scale, random)

    /**
     * Generate a random price with proper currency formatting
     */
    fun randomPrice(random: Random = globalRandom): BigDecimal =
        random(BigDecimal("0.01"), BigDecimal("9999.99"), 2, random)

    /**
     * Generate a random percentage as BigDecimal
     */
    fun randomPercentage(scale: Int = 2, random: Random = globalRandom): BigDecimal =
        random(BigDecimal.ZERO, BigDecimal("100.00"), scale, random)
}

// =============================================================================
// STRING TYPES
// =============================================================================

private val alphanumericChars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
private val alphabeticChars = ('a'..'z') + ('A'..'Z')
private val numericChars = ('0'..'9')

/**
 * Generate a random string of specified length
 */
fun String.Companion.random(length: Int = 10, random: Random = globalRandom): String =
    (1..length).map { alphanumericChars.random(random) }.joinToString("")

/**
 * Generate a random alphanumeric string
 */
fun String.Companion.randomAlphanumeric(length: Int = 10, random: Random = globalRandom): String =
    (1..length).map { alphanumericChars.random(random) }.joinToString("")

/**
 * Generate a random alphabetic string (letters only)
 */
fun String.Companion.randomAlphabetic(length: Int = 10, random: Random = globalRandom): String =
    (1..length).map { alphabeticChars.random(random) }.joinToString("")

/**
 * Generate a random numeric string
 */
fun String.Companion.randomNumeric(length: Int = 10, random: Random = globalRandom): String =
    (1..length).map { numericChars.random(random) }.joinToString("")

/**
 * Generate a random name
 */
fun String.Companion.randomName(random: Random = globalRandom): String {
    val firstNames = listOf(
        "Alice", "Bob", "Charlie", "Diana", "Edward", "Fiona", "George", "Hannah",
        "Ivan", "Julia", "Kevin", "Laura", "Michael", "Nina", "Oscar", "Paula",
        "Quentin", "Rachel", "Steven", "Tina", "Ulrich", "Victoria", "William", "Xara", "Yuri", "Zoe"
    )
    val lastNames = listOf(
        "Anderson", "Brown", "Clark", "Davis", "Evans", "Foster", "Garcia", "Harris",
        "Johnson", "King", "Lewis", "Martinez", "Nelson", "O'Connor", "Parker", "Quinn",
        "Roberts", "Smith", "Taylor", "Turner", "Underwood", "Valdez", "Williams", "Xavier", "Young", "Zhang"
    )
    return "${firstNames.random(random)} ${lastNames.random(random)}"
}

/**
 * Generate a random first name
 */
fun String.Companion.randomFirstName(random: Random = globalRandom): String {
    val firstNames = listOf(
        "Alice", "Bob", "Charlie", "Diana", "Edward", "Fiona", "George", "Hannah",
        "Ivan", "Julia", "Kevin", "Laura", "Michael", "Nina", "Oscar", "Paula",
        "Quentin", "Rachel", "Steven", "Tina", "Ulrich", "Victoria", "William", "Xara", "Yuri", "Zoe"
    )
    return firstNames.random(random)
}

/**
 * Generate a random last name
 */
fun String.Companion.randomLastName(random: Random = globalRandom): String {
    val lastNames = listOf(
        "Anderson", "Brown", "Clark", "Davis", "Evans", "Foster", "Garcia", "Harris",
        "Johnson", "King", "Lewis", "Martinez", "Nelson", "O'Connor", "Parker", "Quinn",
        "Roberts", "Smith", "Taylor", "Turner", "Underwood", "Valdez", "Williams", "Xavier", "Young", "Zhang"
    )
    return lastNames.random(random)
}

/**
 * Generate a random company name
 */
fun String.Companion.randomCompany(random: Random = globalRandom): String {
    val prefixes = listOf("Global", "Mega", "Ultra", "Super", "Advanced", "Future", "Tech", "Digital", "Smart", "Pro")
    val suffixes = listOf("Corp", "Inc", "LLC", "Solutions", "Systems", "Technologies", "Enterprises", "Group", "Associates", "Partners")
    val middle = listOf("Data", "Cloud", "Software", "Networks", "Services", "Consulting", "Innovation", "Development", "Analytics", "Security")

    return when (random.nextInt(3)) {
        0 -> "${prefixes.random(random)} ${suffixes.random(random)}"
        1 -> "${prefixes.random(random)} ${middle.random(random)} ${suffixes.random(random)}"
        else -> "${randomLastName(random)} ${suffixes.random(random)}"
    }
}

/**
 * Generate a random email address
 */
fun String.Companion.randomEmail(random: Random = globalRandom): String {
    val domains = listOf("gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "company.com", "example.org", "test.net")
    val username = randomAlphanumeric(random.nextInt(5, 12), random).lowercase()
    return "$username@${domains.random(random)}"
}

/**
 * Generate a random domain name
 */
fun String.Companion.randomDomain(random: Random = globalRandom): String {
    val tlds = listOf("com", "org", "net", "io", "co", "app", "dev", "tech")
    val name = randomAlphabetic(random.nextInt(5, 12), random).lowercase()
    return "$name.${tlds.random(random)}"
}

/**
 * Generate a random URL
 */
fun String.Companion.randomUrl(random: Random = globalRandom): String {
    val protocols = listOf("http", "https")
    val domain = randomDomain(random)
    return "${protocols.random(random)}://$domain"
}

/**
 * Generate a random phone number
 */
fun String.Companion.randomPhone(random: Random = globalRandom): String {
    val area = randomNumeric(3, random)
    val exchange = randomNumeric(3, random)
    val number = randomNumeric(4, random)
    return "($area) $exchange-$number"
}

/**
 * Generate a random address
 */
fun String.Companion.randomAddress(random: Random = globalRandom): String {
    val streetNumbers = random.nextInt(1, 9999)
    val streetNames = listOf(
        "Main St", "Oak Ave", "Pine Rd", "Elm Dr", "Park Blvd", "First St", "Second Ave",
        "Maple Dr", "Cedar Ln", "Washington St", "Lincoln Ave", "Roosevelt Blvd"
    )
    val streetName = streetNames.random(random)
    return "$streetNumbers $streetName"
}

/**
 * Generate a random city
 */
fun String.Companion.randomCity(random: Random = globalRandom): String {
    val cities = listOf(
        "Springfield", "Riverside", "Franklin", "Georgetown", "Clinton", "Greenwood",
        "Bristol", "Fairview", "Wellington", "Manchester", "Oxford", "Cambridge",
        "Kingston", "Arlington", "Ashland", "Burlington", "Chester", "Dover"
    )
    return cities.random(random)
}

/**
 * Generate a random country
 */
fun String.Companion.randomCountry(random: Random = globalRandom): String {
    val countries = listOf(
        "United States", "Canada", "United Kingdom", "Germany", "France", "Japan",
        "Australia", "Brazil", "India", "China", "South Korea", "Netherlands",
        "Sweden", "Norway", "Denmark", "Switzerland", "Italy", "Spain"
    )
    return countries.random(random)
}

/**
 * Generate a random UUID string
 */
fun String.Companion.randomUuid(random: Random = globalRandom): String = UUID.randomUUID().toString()

/**
 * Generate a random slug (URL-friendly string)
 */
fun String.Companion.randomSlug(words: Int = 3, random: Random = globalRandom): String =
    (1..words).joinToString("-") { randomAlphabetic(random.nextInt(4, 8), random).lowercase() }

/**
 * Generate random text
 */
fun String.Companion.randomText(words: Int = 10, random: Random = globalRandom): String {
    val commonWords = listOf(
        "the", "and", "for", "are", "but", "not", "you", "all", "can", "had", "her", "was", "one", "our", "out",
        "day", "get", "has", "him", "his", "how", "its", "may", "new", "now", "old", "see", "two", "who", "boy",
        "did", "man", "car", "home", "work", "life", "time", "year", "way", "back", "good", "great", "right"
    )
    return (1..words).map { commonWords.random(random) }.joinToString(" ")
}

/**
 * Generate a random sentence
 */
fun String.Companion.randomSentence(random: Random = globalRandom): String {
    val words = randomText(random.nextInt(5, 15), random)
    return words.replaceFirstChar { it.uppercase() } + "."
}

/**
 * Generate a random paragraph
 */
fun String.Companion.randomParagraph(random: Random = globalRandom): String =
    (1..random.nextInt(3, 6)).joinToString(" ") { randomSentence(random) }

// =============================================================================
// TEMPORAL HELPERS (No companion objects available)
// =============================================================================

/**
 * LocalDate random generation utilities
 */
object LocalDateRandom {

    /**
     * Generate a random LocalDate between start and end dates
     */
    fun random(
        start: LocalDate = LocalDate.of(2020, 1, 1),
        end: LocalDate = LocalDate.now(),
        random: Random = globalRandom
    ): LocalDate {
        val daysBetween = ChronoUnit.DAYS.between(start, end)
        val randomDays = random.nextLong(0, daysBetween + 1)
        return start.plusDays(randomDays)
    }

    /**
     * Generate a random past LocalDate (within last year)
     */
    fun randomPast(random: Random = globalRandom): LocalDate =
        random(LocalDate.now().minusYears(1), LocalDate.now(), random)

    /**
     * Generate a random future LocalDate (within next year)
     */
    fun randomFuture(random: Random = globalRandom): LocalDate =
        random(LocalDate.now(), LocalDate.now().plusYears(1), random)

    /**
     * Generate a random birth date (18-80 years ago)
     */
    fun randomBirthDate(random: Random = globalRandom): LocalDate =
        random(LocalDate.now().minusYears(80), LocalDate.now().minusYears(18), random)
}

/**
 * LocalDateTime random generation utilities
 */
object LocalDateTimeRandom {

    /**
     * Generate a random LocalDateTime between start and end
     */
    fun random(
        start: LocalDateTime = LocalDateTime.of(2020, 1, 1, 0, 0),
        end: LocalDateTime = LocalDateTime.now(),
        random: Random = globalRandom
    ): LocalDateTime {
        val secondsBetween = ChronoUnit.SECONDS.between(start, end)
        val randomSeconds = random.nextLong(0, secondsBetween + 1)
        return start.plusSeconds(randomSeconds)
    }

    /**
     * Generate a random past LocalDateTime (within last month)
     */
    fun randomPast(random: Random = globalRandom): LocalDateTime =
        random(LocalDateTime.now().minusMonths(1), LocalDateTime.now(), random)

    /**
     * Generate a random future LocalDateTime (within next month)
     */
    fun randomFuture(random: Random = globalRandom): LocalDateTime =
        random(LocalDateTime.now(), LocalDateTime.now().plusMonths(1), random)
}

/**
 * LocalTime random generation utilities
 */
object LocalTimeRandom {

    /**
     * Generate a random LocalTime
     */
    fun random(random: Random = globalRandom): LocalTime =
        LocalTime.of(
            random.nextInt(0, 24),
            random.nextInt(0, 60),
            random.nextInt(0, 60)
        )

    /**
     * Generate a random business hours time (9 AM to 5 PM)
     */
    fun randomBusinessHours(random: Random = globalRandom): LocalTime =
        LocalTime.of(
            random.nextInt(9, 17),
            random.nextInt(0, 60)
        )
}

/**
 * Instant random generation utilities
 */
object InstantRandom {

    /**
     * Generate a random Instant between start and end
     */
    fun random(
        start: Instant = Instant.now().minus(365, ChronoUnit.DAYS),
        end: Instant = Instant.now(),
        random: Random = globalRandom
    ): Instant {
        val secondsBetween = ChronoUnit.SECONDS.between(start, end)
        val randomSeconds = random.nextLong(0, secondsBetween + 1)
        return start.plusSeconds(randomSeconds)
    }

    /**
     * Generate a random past Instant (within last week)
     */
    fun randomPast(random: Random = globalRandom): Instant =
        random(Instant.now().minus(7, ChronoUnit.DAYS), Instant.now(), random)
}

/**
 * Duration random generation utilities
 */
object DurationRandom {

    /**
     * Generate a random Duration between min and max
     */
    fun random(
        min: Duration = Duration.ofMinutes(1),
        max: Duration = Duration.ofHours(24),
        random: Random = globalRandom
    ): Duration {
        val minSeconds = min.seconds
        val maxSeconds = max.seconds
        val randomSeconds = random.nextLong(minSeconds, maxSeconds)
        return Duration.ofSeconds(randomSeconds)
    }
}

// =============================================================================
// BOOLEAN AND OTHER TYPES
// =============================================================================

/**
 * Generate a random boolean
 */
fun Boolean.Companion.random(random: Random = globalRandom): Boolean = random.nextBoolean()

/**
 * Generate a random boolean with specified probability of being true
 */
fun Boolean.Companion.random(trueProbability: Double, random: Random = globalRandom): Boolean =
    random.nextDouble() < trueProbability

/**
 * Generate a random UUID
 */
fun randomUUID(): UUID = UUID.randomUUID()

// =============================================================================
// COLLECTION HELPERS
// =============================================================================

/**
 * Generate a random list of items using the provided generator
 */
inline fun <T> randomList(size: Int = 5, random: Random = globalRandom, generator: (Random) -> T): List<T> =
    (1..size).map { generator(random) }

/**
 * Generate a random set of items using the provided generator
 */
inline fun <T> randomSet(size: Int = 5, random: Random = globalRandom, generator: (Random) -> T): Set<T> {
    val result = mutableSetOf<T>()
    var attempts = 0
    while (result.size < size && attempts < size * 10) {
        result.add(generator(random))
        attempts++
    }
    return result
}

/**
 * Generate a random map with the provided key and value generators
 */
inline fun <K, V> randomMap(
    size: Int = 5,
    random: Random = globalRandom,
    keyGenerator: (Random) -> K,
    valueGenerator: (Random) -> V
): Map<K, V> = (1..size).associate { keyGenerator(random) to valueGenerator(random) }

// =============================================================================
// DOMAIN-SPECIFIC GENERATORS
// =============================================================================

/**
 * Domain-specific data generators for common business entities
 */
object DomainData {

    fun randomCreditCardNumber(random: Random = globalRandom): String =
        "4" + String.randomNumeric(15, random) // Visa format

    fun randomSsn(random: Random = globalRandom): String {
        val area = String.randomNumeric(3, random)
        val group = String.randomNumeric(2, random)
        val serial = String.randomNumeric(4, random)
        return "$area-$group-$serial"
    }

    fun randomIpAddress(random: Random = globalRandom): String =
        (1..4).joinToString(".") { random.nextInt(1, 255).toString() }

    fun randomMacAddress(random: Random = globalRandom): String =
        (1..6).joinToString(":") { "%02x".format(random.nextInt(0, 256)) }

    fun randomColorHex(random: Random = globalRandom): String =
        "#" + (1..6).map { "0123456789ABCDEF".random(random) }.joinToString("")

    fun randomColorName(random: Random = globalRandom): String {
        val colors = listOf(
            "Red", "Blue", "Green", "Yellow", "Orange", "Purple", "Pink", "Brown",
            "Black", "White", "Gray", "Cyan", "Magenta", "Lime", "Indigo", "Violet"
        )
        return colors.random(random)
    }

    fun randomJobTitle(random: Random = globalRandom): String {
        val adjectives = listOf("Senior", "Junior", "Lead", "Principal", "Chief", "Executive", "Associate")
        val roles = listOf("Engineer", "Developer", "Manager", "Analyst", "Consultant", "Director", "Specialist")
        val departments = listOf("Software", "Data", "Product", "Marketing", "Sales", "Finance", "Operations")

        return when (random.nextInt(3)) {
            0 -> "${adjectives.random(random)} ${roles.random(random)}"
            1 -> "${departments.random(random)} ${roles.random(random)}"
            else -> "${adjectives.random(random)} ${departments.random(random)} ${roles.random(random)}"
        }
    }

    fun randomCurrency(random: Random = globalRandom): String {
        val currencies = listOf("USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "SEK", "NOK")
        return currencies.random(random)
    }

    fun randomTimezone(random: Random = globalRandom): String =
        ZoneId.getAvailableZoneIds().random(random)

    fun randomFileExtension(random: Random = globalRandom): String {
        val extensions = listOf("txt", "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "jpg", "png", "gif", "mp4", "mp3")
        return extensions.random(random)
    }

    fun randomMimeType(random: Random = globalRandom): String {
        val mimeTypes = listOf(
            "text/plain", "text/html", "application/json", "application/pdf",
            "image/jpeg", "image/png", "image/gif", "video/mp4", "audio/mpeg"
        )
        return mimeTypes.random(random)
    }

    fun randomUserAgent(random: Random = globalRandom): String {
        val browsers = listOf("Chrome", "Firefox", "Safari", "Edge")
        val versions = listOf("91.0", "92.0", "93.0", "94.0", "95.0")
        val os = listOf("Windows NT 10.0", "Macintosh; Intel Mac OS X 10_15_7", "X11; Linux x86_64")

        return "Mozilla/5.0 (${os.random(random)}) ${browsers.random(random)}/${versions.random(random)}"
    }
}

// =============================================================================
// BUILDER PATTERN FOR COMPLEX OBJECTS
// =============================================================================

/**
 * Builder for creating test data with fluent API
 */
class TestDataBuilder<T>(
    private val constructor: () -> T,
    private val random: Random = globalRandom
) {
    private val modifications = mutableListOf<T.() -> Unit>()

    fun with(modification: T.() -> Unit): TestDataBuilder<T> {
        modifications.add(modification)
        return this
    }

    fun build(): T {
        val instance = constructor()
        modifications.forEach { instance.it() }
        return instance
    }

    fun buildList(count: Int): List<T> = (1..count).map { build() }
}

/**
 * Create a test data builder for any type
 */
fun <T> testData(random: Random = globalRandom, constructor: () -> T): TestDataBuilder<T> =
    TestDataBuilder(constructor, random)

// =============================================================================
// USAGE EXAMPLES
// =============================================================================

//object Examples {
//
//    // Simple primitive generation
//    fun examplePrimitives() {
//        val randomId = Long.randomId()
//        val randomPrice = Double.randomPrice()
//        val randomBigDecimalPrice = BigDecimalRandom.randomPrice()
//        val randomName = String.randomName()
//        val randomDate = LocalDateRandom.randomPast()
//        val randomTime = LocalTimeRandom.random()
//        val randomDateTime = LocalDateTimeRandom.randomPast()
//        val randomInstant = InstantRandom.randomPast()
//        val randomDuration = DurationRandom.random()
//        val randomFlag = Boolean.random(0.3) // 30% chance of true
//        val randomUuid = randomUUID()
//    }
//
//    // Collection generation
//    fun exampleCollections() {
//        val randomNames = randomList(10) { String.randomName(it) }
//        val randomIds = randomSet(5) { Long.randomId(it) }
//        val randomMap = randomMap(
//            3,
//            keyGenerator = { String.randomAlphanumeric(5, it) },
//            valueGenerator = { Int.randomPositive(it) }
//        )
//    }
//
//    // Domain-specific data
//    fun exampleDomainData() {
//        val email = String.randomEmail()
//        val phone = String.randomPhone()
//        val address = String.randomAddress()
//        val creditCard = DomainData.randomCreditCardNumber()
//        val ipAddress = DomainData.randomIpAddress()
//// =============================================================================
//// COMPREHENSIVE USAGE GUIDE
//// =============================================================================
//
//        /**
//        // Quick reference for all available random generators
//        */
//        object TestDataGenerator {
//
//            // NUMERIC TYPES (use Companion extensions)
//            fun generateNumericData(): Map<String, Any> = mapOf(
//                "longId" to Long.randomId(),
//                "intCount" to Int.randomPositive(),
//                "price" to Double.randomPrice(),
//                "percentage" to Double.randomPercentage(),
//                "floatValue" to Float.random(0f, 100f),
//                "bigDecimalCurrency" to BigDecimalRandom.randomCurrency(),
//                "bigDecimalPrice" to BigDecimalRandom.randomPrice()
//            )
//
//            // STRING TYPES (use Companion extensions)
//            fun generateStringData(): Map<String, String> = mapOf(
//                "randomString" to String.random(10),
//                "alphanumeric" to String.randomAlphanumeric(8),
//                "alphabetic" to String.randomAlphabetic(6),
//                "numeric" to String.randomNumeric(5),
//                "fullName" to String.randomName(),
//                "firstName" to String.randomFirstName(),
//                "lastName" to String.randomLastName(),
//                "company" to String.randomCompany(),
//                "email" to String.randomEmail(),
//                "domain" to String.randomDomain(),
//                "url" to String.randomUrl(),
//                "phone" to String.randomPhone(),
//                "address" to String.randomAddress(),
//                "city" to String.randomCity(),
//                "country" to String.randomCountry(),
//                "uuid" to String.randomUuid(),
//                "slug" to String.randomSlug(3),
//                "text" to String.randomText(5),
//                "sentence" to String.randomSentence(),
//                "paragraph" to String.randomParagraph()
//            )
//
//            // TEMPORAL TYPES (use Object-based API)
//            fun generateTemporalData(): Map<String, Any> = mapOf(
//                "pastDate" to LocalDateRandom.randomPast(),
//                "futureDate" to LocalDateRandom.randomFuture(),
//                "birthDate" to LocalDateRandom.randomBirthDate(),
//                "customDate" to LocalDateRandom.random(LocalDate.of(2023, 1, 1), LocalDate.of(2024, 12, 31)),
//                "pastDateTime" to LocalDateTimeRandom.randomPast(),
//                "futureDateTime" to LocalDateTimeRandom.randomFuture(),
//                "randomTime" to LocalTimeRandom.random(),
//                "businessTime" to LocalTimeRandom.randomBusinessHours(),
//                "pastInstant" to InstantRandom.randomPast(),
//                "randomDuration" to DurationRandom.random()
//            )
//
//            // BOOLEAN AND UUID
//            fun generateMiscData(): Map<String, Any> = mapOf(
//                "randomFlag" to Boolean.random(),
//                "biasedFlag" to Boolean.random(0.3), // 30% true
//                "uuid" to randomUUID()
//            )
//
//            // DOMAIN-SPECIFIC DATA
//            fun generateDomainData(): Map<String, String> = mapOf(
//                "creditCard" to DomainData.randomCreditCardNumber(),
//                "ssn" to DomainData.randomSsn(),
//                "ipAddress" to DomainData.randomIpAddress(),
//                "macAddress" to DomainData.randomMacAddress(),
//                "colorHex" to DomainData.randomColorHex(),
//                "colorName" to DomainData.randomColorName(),
//                "jobTitle" to DomainData.randomJobTitle(),
//                "currency" to DomainData.randomCurrency(),
//                "timezone" to DomainData.randomTimezone(),
//                "fileExtension" to DomainData.randomFileExtension(),
//                "mimeType" to DomainData.randomMimeType(),
//                "userAgent" to DomainData.randomUserAgent()
//            )
//        }
//    }
//
//    // Deterministic testing with custom seed
//    fun exampleDeterministicTesting() {
//        // Create a seeded random for repeatable tests
//        val testRandom = createSeededRandom(12345)
//
//        // These will always produce the same values
//        val deterministicName1 = String.randomName(testRandom)
//        val deterministicEmail1 = String.randomEmail(testRandom)
//
//        // Create another random with same seed
//        val anotherTestRandom = createSeededRandom(12345)
//        val deterministicName2 = String.randomName(anotherTestRandom)
//        val deterministicEmail2 = String.randomEmail(anotherTestRandom)
//
//        // These assertions will pass
//        check(deterministicName1 == deterministicName2)
//        check(deterministicEmail1 == deterministicEmail2)
//    }
//
//    // Builder pattern example
//    data class TestUser(
//        var id: Long = 0,
//        var name: String = "",
//        var email: String = "",
//        var createdAt: LocalDateTime = LocalDateTime.now(),
//        var isActive: Boolean = true
//    )
//
//    fun exampleBuilder() {
//        val user = testData { TestUser() }
//            .with { id = Long.randomId() }
//            .with { name = String.randomName() }
//            .with { email = String.randomEmail() }
//            .with { createdAt = LocalDateTimeRandom.randomPast() }
//            .with { isActive = Boolean.random(0.8) } // 80% chance active
//            .build()
//
//        val users = testData { TestUser() }
//            .with { name = String.randomName() }
//            .with { email = String.randomEmail() }
//            .buildList(10)
//    }
//
//    // Custom random instance for specific test scenarios
//    fun exampleCustomRandom() {
//        val customRandom = createSeededRandom(999)
//
//        val customUser = testData(customRandom) { TestUser() }
//            .with { id = Long.randomId(customRandom) }
//            .with { name = String.randomName(customRandom) }
//            .with { email = String.randomEmail(customRandom) }
//            .with { createdAt = LocalDateTimeRandom.randomPast(customRandom) }
//            .build()
//    }
//}

// =============================================================================
// IDENTITY ID EXTENSIONS
// =============================================================================

/**
 * IdentityID random generation extensions
 */
object IdentityIDRandom {

    /**
     * Generate a random IdentityID in the typical range
     */
    fun generate(random: Random = globalRandom): IdentityID =
        IdentityID(Long.random(1L, 999_999L, random))

    /**
     * Generate a sequential IdentityID starting from a base value
     */
    fun generateSequential(startFrom: Long = 1000L): () -> IdentityID {
        var current = startFrom
        return { IdentityID(current++) }
    }

    /**
     * Generate IdentityID within a specific range
     */
    fun generateInRange(min: Long = 1L, max: Long = Long.MAX_VALUE, random: Random = globalRandom): IdentityID {
        require(min >= 0) { "min must be non-negative" }
        require(max > min) { "max must be greater than min" }
        return IdentityID(Long.random(min, max, random))
    }

    /**
     * Generate a small IdentityID (good for test readability)
     */
    fun generateSmall(random: Random = globalRandom): IdentityID =
        IdentityID(Long.random(1L, 100L, random))

    /**
     * Generate a large IdentityID (stress testing)
     */
    fun generateLarge(random: Random = globalRandom): IdentityID =
        IdentityID(Long.random(1_000_000L, Long.MAX_VALUE, random))

    /**
     * Generate a list of unique IdentityIDs
     */
    fun generateUniqueList(count: Int, min: Long = 1L, max: Long = 999_999L, random: Random = globalRandom): List<IdentityID> {
        require(count >= 0) { "count must be non-negative" }
        require(max > min) { "max must be greater than min" }
        require((max - min) >= count) { "range must be large enough to generate $count unique values" }

        return generateSequence {
            Long.random(min, max, random)
        }
            .distinct()
            .take(count)
            .map { IdentityID(it) }
            .toList()
    }

    /**
     * Generate IdentityID with specific pattern (useful for debugging)
     */
    fun generateWithPattern(pattern: String, random: Random = globalRandom): IdentityID {
        // Pattern examples: "1000", "2XXX" where X is random digit
        val value = pattern.map { char ->
            when (char) {
                'X' -> random.nextInt(10).toString()
                else -> char.toString()
            }
        }.joinToString("")

        return IdentityID(value.toLong())
    }

    /**
     * Generate tenant-scoped IdentityID (using tenant prefix)
     */
    fun generateForTenant(tenantId: Long, random: Random = globalRandom): IdentityID {
        val suffix = Long.random(1L, 999_999L, random)
        return IdentityID(tenantId * 1_000_000L + suffix)
    }

    /**
     * Common test values and generators
     */
    object Common {
        val DEFAULT = IdentityID(1001L)
        val TENANT_1 = IdentityID(1001L)
        val TENANT_2 = IdentityID(2001L)
        val BUSINESS_UNIT_1 = IdentityID(10001L)
        val BUSINESS_UNIT_2 = IdentityID(20001L)
        val SESSION_1 = IdentityID(100001L)
        val USER_1 = IdentityID(1000001L)

        /**
         * Pre-defined test ranges for different entity types
         */
        fun tenantId(random: Random = globalRandom): IdentityID =
            IdentityID(Long.random(1000L, 9999L, random))

        fun businessUnitId(random: Random = globalRandom): IdentityID =
            IdentityID(Long.random(10000L, 99999L, random))

        fun sessionId(random: Random = globalRandom): IdentityID =
            IdentityID(Long.random(100000L, 999999L, random))

        fun userId(random: Random = globalRandom): IdentityID =
            IdentityID(Long.random(1000000L, 9999999L, random))
    }
}

// =============================================================================
// IDENTITY UUID EXTENSIONS
// =============================================================================

/**
 * IdentityUUID random generation extensions
 */
object IdentityUUIDRandom {

    /**
     * Generate a random IdentityUUID
     */
    fun generate(): IdentityUUID = IdentityUUID(UUID.randomUUID())

    /**
     * Generate IdentityUUID from string (useful for predictable tests)
     */
    fun generateFromString(uuidString: String): IdentityUUID =
        IdentityUUID(UUID.fromString(uuidString))

    /**
     * Generate deterministic IdentityUUID (for reproducible tests)
     */
    fun generateDeterministic(seed: Long): IdentityUUID {
        val random = Random(seed)
        val mostSigBits = random.nextLong()
        val leastSigBits = random.nextLong()
        return IdentityUUID(UUID(mostSigBits, leastSigBits))
    }

    /**
     * Generate a list of unique IdentityUUIDs
     */
    fun generateUniqueList(count: Int): List<IdentityUUID> =
        (1..count).map { generate() }

    /**
     * Generate IdentityUUID with specific pattern (for debugging)
     */
    fun generateWithPrefix(prefix: String): IdentityUUID {
        require(prefix.length <= 8) { "prefix too long for UUID format" }

        val uuid = UUID.randomUUID()
        val uuidString = uuid.toString()

        // Pad prefix to 8 characters and replace first segment
        val paddedPrefix = prefix.padEnd(8, '0')
        val segments = uuidString.split('-')
        val prefixedString = "$paddedPrefix-${segments[1]}-${segments[2]}-${segments[3]}-${segments[4]}"

        return IdentityUUID(UUID.fromString(prefixedString))
    }

    /**
     * Common test values
     */
    object Common {
        val DEFAULT = IdentityUUID(UUID.fromString("00000000-0000-0000-0000-000000000001"))
        val TENANT_1 = IdentityUUID(UUID.fromString("10000000-0000-0000-0000-000000000001"))
        val TENANT_2 = IdentityUUID(UUID.fromString("20000000-0000-0000-0000-000000000001"))
        val BUSINESS_UNIT_1 = IdentityUUID(UUID.fromString("30000000-0000-0000-0000-000000000001"))
        val USER_1 = IdentityUUID(UUID.fromString("40000000-0000-0000-0000-000000000001"))
        val RESOURCE_1 = IdentityUUID(UUID.fromString("50000000-0000-0000-0000-000000000001"))

        /**
         * Generate test UUIDs with recognizable patterns
         */
        fun forTenant(tenantId: Long) = generateDeterministic(tenantId + 1000L)
        fun forUser(userId: Long) = generateDeterministic(userId + 4000L)
        fun forResource(resourceId: Long) = generateDeterministic(resourceId + 5000L)
    }
}
