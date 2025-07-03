package net.blugrid.api.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

@MicronautTest
class SerdeTests {

    val ServiceEntryJson = "{\n" +
        "      \"ID\": \"rest-api:8080\",\n" +
        "      \"Service\": \"rest-api\",\n" +
        "      \"Tags\": [],\n" +
        "      \"Address\": \"fedora\",\n" +
        "      \"Meta\": null,\n" +
        "      \"Port\": 8080,\n" +
        "      \"Weights\": {\n" +
        "        \"Passing\": 1,\n" +
        "        \"Warning\": 1\n" +
        "      },\n" +
        "      \"EnableTagOverride\": false,\n" +
        "      \"Proxy\": {\n" +
        "        \"MeshGateway\": {},\n" +
        "        \"Expose\": {}\n" +
        "      },\n" +
        "      \"Connect\": {},\n" +
        "      \"CreateIndex\": 177,\n" +
        "      \"ModifyIndex\": 177\n" +
        "    }"

    data class testSalesReportPeriod(
        val qty: BigDecimal = BigDecimal.ZERO,
        val reportPeriodId: Long
    )

    data class testSalesOptionalReportPeriod(
        val qty: BigDecimal = BigDecimal.ZERO,
        val reportPeriodId: Long? = null
    )

//    @Test
//    fun `Test Consul Service Entry`(objectMapper: ObjectMapper) {
//        val consulServiceEntry = objectMapper.readValue(ServiceEntryJson, ServiceEntry::class.java)
//        Assertions.assertNotNull(consulServiceEntry)
//    }

    @Test
    fun `Test Sales Entry`(objectMapper: ObjectMapper) {
        assertThrows<MismatchedInputException> { objectMapper.readValue("{\"qty\":2}", testSalesReportPeriod::class.java) }
    }

    @Test
    fun `Test Sales Optional Entry`(objectMapper: ObjectMapper) {
        val testSalesReportPeriod = objectMapper.readValue("{\"qty\":2}", testSalesOptionalReportPeriod::class.java)
        Assertions.assertNotNull(testSalesReportPeriod)
        Assertions.assertNull(testSalesReportPeriod.reportPeriodId)
    }
}
