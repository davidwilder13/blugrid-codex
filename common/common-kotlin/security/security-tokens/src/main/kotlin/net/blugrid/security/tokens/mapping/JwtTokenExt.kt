package net.blugrid.security.tokens.mapping

import net.blugrid.platform.serialization.platformObjectMapper

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> T.toJWTRawMap(): Map<out String, Any> =
    platformObjectMapper.convertValue(this, Map::class.java) as Map<out String, Any>