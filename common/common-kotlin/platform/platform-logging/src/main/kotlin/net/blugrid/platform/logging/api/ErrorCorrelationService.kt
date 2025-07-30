package net.blugrid.platform.logging.api

import org.slf4j.MDC
import jakarta.inject.Singleton
import java.util.UUID

@Singleton
class ErrorCorrelationService {
    
    fun generateCorrelationId(): String = UUID.randomUUID().toString()
    
    fun withCorrelation(operation: (String) -> Unit) {
        val correlationId = generateCorrelationId()
        MDC.put("correlationId", correlationId)
        try {
            operation(correlationId)
        } finally {
            MDC.remove("correlationId")
        }
    }
}
