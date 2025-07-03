package net.blugrid.api.security.jwt.mapping

import net.blugrid.api.security.authentication.model.BusinessUnitAuthentication
import net.blugrid.api.security.authentication.model.DecoratedAuthentication
import net.blugrid.api.security.authentication.model.GuestAuthentication
import net.blugrid.api.security.authentication.model.TenantAuthentication
import net.blugrid.api.security.jwt.model.JwtToken

fun DecoratedAuthentication.toJwtToken(): JwtToken = let { source ->
    when (source) {
        is GuestAuthentication -> source.toGuestJwtToken()
        is TenantAuthentication -> source.toTenantJwtToken()
        is BusinessUnitAuthentication -> source.toBusinessUnitJwtToken()
        else -> throw UnsupportedOperationException(source.toString())
    }
}

fun GuestAuthentication.toGuestJwtToken(
    attributes: MutableMap<String, Any> = mutableMapOf(),
    roles: MutableList<String> = mutableListOf()
): JwtToken {
    val source = this
    return JwtToken(
        authenticationType = source.authenticationType,
        user = source.user,
        session = source.session,
        expirationTime = source.expirationTime,
        roles = roles,
        attributes = attributes,
    )
}

fun TenantAuthentication.toTenantJwtToken(
    attributes: MutableMap<String, Any> = mutableMapOf(),
    roles: MutableList<String> = mutableListOf()
): JwtToken {
    val source = this
    return JwtToken(
        authenticationType = source.authenticationType,
        organisation = source.organisation,
        user = source.user,
        session = source.session,
        expirationTime = source.expirationTime,
        roles = roles,
        attributes = attributes,
    )
}

fun BusinessUnitAuthentication.toBusinessUnitJwtToken(
    attributes: MutableMap<String, Any> = mutableMapOf(),
    roles: MutableList<String> = mutableListOf()
): JwtToken {
    val source = this
    return JwtToken(
        authenticationType = source.authenticationType,
        organisation = source.organisation,
        user = source.user,
        session = source.session,
        expirationTime = source.expirationTime,
        roles = roles,
        attributes = attributes,
    )
}

