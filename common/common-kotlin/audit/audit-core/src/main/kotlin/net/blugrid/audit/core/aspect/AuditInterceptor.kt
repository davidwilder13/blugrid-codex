package net.blugrid.audit.core.aspect

import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import jakarta.inject.Singleton
import net.blugrid.audit.core.annotation.LogAuditEvent
import net.blugrid.audit.core.service.AuditEventEmitterService
import net.blugrid.common.model.audit.AuditEventType
import net.blugrid.common.model.resource.BaseAuditedResource
import net.blugrid.common.model.resource.BaseBusinessUnitResource
import net.blugrid.common.model.resource.BaseTenantResource
import net.blugrid.common.model.resource.ResourceType
import net.blugrid.common.model.resource.UnscopedResource
import net.blugrid.platform.serialization.objectToJson
import net.blugrid.platform.logging.logger
import net.blugrid.common.domain.IdentityID

@Singleton
@InterceptorBean(LogAuditEvent::class)
class AuditInterceptor(
    private val eventEmitterService: AuditEventEmitterService
) : MethodInterceptor<Any, Any> {

    private val log = logger()

    override fun intercept(context: MethodInvocationContext<Any, Any>): Any {
        val resource = context.proceed()

        val eventType = context.getEventType()
        val resourceType = getResourceType(resource)
        val tenantId = getTenantId(resource)

        log.trace("Intercepted audit $eventType event for $resourceType resource:  ${objectToJson(resource)}")

        if (resource is BaseAuditedResource<*>) {
            val audit = resource.audit ?: run {
                log.warn("Audit information is null for resource: $resourceType")
                throw IllegalStateException("Missing audit data in resource")
            }

            val sessionId = audit.lastChanged?.sessionId ?: run {
                log.warn("Session ID is null for resource: $resourceType")
                throw IllegalStateException("Session ID is null in audit data")
            }

            val version = audit.version
            val lastChangedTimestamp = audit.lastChanged?.timestamp ?: run {
                log.warn("LastChangedTimestamp is null for resource: $resourceType")
                throw IllegalStateException("LastChangedTimestamp is null in audit data")
            }

            eventEmitterService.publishEvent(
                eventType = eventType,
                resourceType = resourceType,
                resource = resource,
                resourceId = resource.id,
                tenantId = tenantId,
                sessionId = sessionId,
                version = version,
                localDateTime = lastChangedTimestamp
            )
        } else {
            log.trace("Resource is not of type GenericAuditedResource: $resource")
        }

        return resource
    }

    private fun MethodInvocationContext<Any, Any>.getEventType(): AuditEventType {
        return this.getAnnotation(LogAuditEvent::class.java)
            ?.values
            ?.get("eventType")
            ?.toString()
            ?.let { AuditEventType.valueOf(it) }
            ?: AuditEventType.CREATE
    }

    private fun getResourceType(resource: Any): ResourceType {
        return when (resource) {
            is BaseAuditedResource<*> -> resource.resourceType
            else -> {
                log.warn("Resource is not a GenericAuditedResource: $resource")
                throw IllegalArgumentException("Invalid resource type")
            }
        }
    }

    private fun getTenantId(resource: Any): IdentityID {
        return when (resource) {
            is BaseTenantResource<*> -> resource.scope?.tenantId ?: logAndThrow("TenantResource permission is null")
            is BaseBusinessUnitResource<*> -> resource.scope?.tenantId ?: logAndThrow("BusinessUnitResource permission is null")
            is UnscopedResource<*> -> IdentityID(1L)
            else -> logAndThrow("Unknown resource type for tenantId")
        }
    }

    private fun logAndThrow(message: String): Nothing {
        log.error(message)
        throw IllegalStateException(message)
    }
}
