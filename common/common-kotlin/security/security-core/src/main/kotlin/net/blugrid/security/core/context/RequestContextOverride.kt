@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package net.blugrid.security.core.context

import io.micronaut.http.HttpRequest
import io.micronaut.http.context.ServerRequestContext
import java.io.Closeable
import java.util.Optional

abstract class IdOverride(private val attributeName: String) : Closeable {

    var value: String
        get() = currentRequestContext().getStringAttribute(attributeName)
            .orElseThrow {
                IllegalStateException("No $attributeName override found")
            }
        set(value) {
            currentRequestContext().setStringAttribute(attributeName, value)
        }

    fun hasOverride(): Boolean = currentRequestContext().getStringAttribute(attributeName).isPresent

    override fun close() {
        currentRequestContext().removeStringAttribute(attributeName)
    }

    private fun currentRequestContext() = ServerRequestContext.currentRequest<Any?>()
    private fun Optional<HttpRequest<Any>>.getStringAttribute(name: String) = flatMap { it.getAttribute(name, String::class.java) }
    private fun Optional<HttpRequest<Any>>.setStringAttribute(name: String, value: String) = get().setAttribute(name, value)
    private fun Optional<HttpRequest<Any>>.removeStringAttribute(name: String) = flatMap { it.removeAttribute(name, String::class.java) }
}

object IsUnscoped : Closeable {

    private val attributeName = "isUnscoped"

    var value: Boolean
        get() = currentRequestContext().getBooleanAttribute(attributeName)
            .orElseThrow {
                IllegalStateException("No $attributeName override found")
            }
        set(value) {
            currentRequestContext().setBooleanAttribute(attributeName, value)
        }

    fun isSet(): Boolean = currentRequestContext().getBooleanAttribute(attributeName).isPresent

    override fun close() {
        currentRequestContext().removeBooleanAttribute(attributeName)
    }

    private fun currentRequestContext() = ServerRequestContext.currentRequest<Any?>()
    private fun Optional<HttpRequest<Any>>.getBooleanAttribute(name: String) = flatMap { it.getAttribute(name, Boolean::class.java) }
    private fun Optional<HttpRequest<Any>>.setBooleanAttribute(name: String, value: Boolean) = get().setAttribute(name, value)
    private fun Optional<HttpRequest<Any>>.removeBooleanAttribute(name: String) = flatMap { it.removeAttribute(name, Boolean::class.java) }
}

object TenantIdOverride : IdOverride("tenantIdOverride")
object BusinessUnitIdOverride : IdOverride("businessUnitIdOverride")
