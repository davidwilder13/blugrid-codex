package net.blugrid.security.core.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import net.blugrid.security.core.session.BusinessUnitSession
import net.blugrid.security.core.session.GuestSession
import net.blugrid.security.core.session.SessionType
import net.blugrid.security.core.session.TenantSession

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "sessionType"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = GuestSession::class, name = "GUEST"),
    JsonSubTypes.Type(value = TenantSession::class, name = "WEB_APPLICATION"),
    JsonSubTypes.Type(value = BusinessUnitSession::class, name = "BUSINESS_UNIT")
)
interface BaseAuthenticatedSession {
    val sessionId: String
    val sessionType: SessionType
    val userId: String
    val webApplicationId: String
}
