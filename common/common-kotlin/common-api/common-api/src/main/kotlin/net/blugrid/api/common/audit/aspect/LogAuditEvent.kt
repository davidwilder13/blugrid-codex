package net.blugrid.api.common.audit.aspect

import io.micronaut.aop.Around
import io.micronaut.context.annotation.AliasFor
import net.blugrid.api.common.model.audit.AuditEventType
import net.blugrid.api.common.model.audit.AuditEventType.CREATE
import java.lang.annotation.Inherited
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION


@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(CLASS, FUNCTION)
@Around
annotation class LogAuditEvent(
    @get:AliasFor(annotation = EventType::class, member = "value")
    val eventType: AuditEventType = CREATE,
)

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Inherited
annotation class EventType(
    val value: AuditEventType = CREATE,
)