package net.blugrid.api.common.audit.aspect

import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import jakarta.inject.Singleton
import net.blugrid.api.common.audit.service.AuditEventEmitterService
import net.blugrid.api.logging.logger
import net.blugrid.api.common.model.audit.AuditEventType
import net.blugrid.api.common.model.resource.BusinessUnitResource
import net.blugrid.api.common.model.resource.GenericAuditedResource
import net.blugrid.api.common.model.resource.ResourceType
import net.blugrid.api.common.model.resource.TenantResource
import net.blugrid.api.common.model.resource.UnscopedResource
import net.blugrid.api.json.objectToJson

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

        if (resource is GenericAuditedResource<*>) {
            val audit = resource.audit ?: run {
                log.warn("Audit information is null for resource: $resourceType")
                throw IllegalStateException("Missing audit data in resource")
            }

            val sessionId = audit.lastChangedBySessionId ?: run {
                log.warn("Session ID is null for resource: $resourceType")
                throw IllegalStateException("Session ID is null in audit data")
            }

            val version = audit.version
            val lastChangedTimestamp = audit.lastChangedTimestamp ?: run {
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
            is GenericAuditedResource<*> -> resource.resourceType
            else -> {
                log.warn("Resource is not a GenericAuditedResource: $resource")
                throw IllegalArgumentException("Invalid resource type")
            }
        }
    }

    private fun getTenantId(resource: Any): Long {
        return when (resource) {
            is TenantResource<*> -> resource.permission?.tenantId ?: logAndThrow("TenantResource permission is null")
            is BusinessUnitResource<*> -> resource.permission?.tenantId ?: logAndThrow("BusinessUnitResource permission is null")
            is UnscopedResource<*> -> 1L
            else -> logAndThrow("Unknown resource type for tenantId")
        }
    }

    private fun logAndThrow(message: String): Nothing {
        log.error(message)
        throw IllegalStateException(message)
    }
}
