package net.blugrid.data.persistence.util

fun List<Any>.toTextArray(): String = this.joinToString(",", "{", "}") { it.toString() }
