package net.blugrid.platform.serialization

import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.extension.read

inline fun <reified T : Any> String.readAs(path: String): T? =
    JsonPath.parse(this)?.read(path)

fun String.read(path: String): String? =
    JsonPath.parse(this)?.read(path)
