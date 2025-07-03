package net.blugrid.api.common.utils

fun List<Any>.toTextArray(): String = this.joinToString(",", "{", "}") { it.toString() }