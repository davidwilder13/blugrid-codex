package net.blugrid.api.common.persistence.util

fun List<Any>.toTextArray(): String = this.joinToString(",", "{", "}") { it.toString() }
