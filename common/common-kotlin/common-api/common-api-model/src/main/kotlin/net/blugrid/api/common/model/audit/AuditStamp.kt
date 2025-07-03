package net.blugrid.api.common.model.audit

import io.swagger.v3.oas.annotations.media.Schema
import net.blugrid.common.domain.IdentityID
import java.time.LocalDateTime

@Schema(description = "Stamp of a user session and time of action.")
data class AuditStamp(
    val sessionId: IdentityID?,
    val session: Any?,
    val timestamp: LocalDateTime?
)
