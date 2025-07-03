package net.blugrid.api.security.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import net.blugrid.api.session.model.BusinessUnitSession
import net.blugrid.api.session.model.GuestSession
import net.blugrid.api.session.model.SessionType
import net.blugrid.api.session.model.TenantSession

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
