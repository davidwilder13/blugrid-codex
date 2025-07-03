package net.blugrid.api.logging

import org.slf4j.LoggerFactory
import kotlin.reflect.full.companionObject

inline fun <reified R : Any> R.logger() = logger(R::class.java)

fun logger(clazz: Class<*>) = LoggerFactory.getLogger(unwrapCompanionClass(clazz))

// unwrap companion class to enclosing class given a Java Class
private fun <T : Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
    return if (ofClass.enclosingClass != null && ofClass.enclosingClass.kotlin.companionObject?.java == ofClass) {
        ofClass.enclosingClass
    } else {
        ofClass
    }
}
