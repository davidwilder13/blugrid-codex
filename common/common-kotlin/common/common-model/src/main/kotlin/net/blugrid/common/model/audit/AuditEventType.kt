package net.blugrid.common.model.audit

import com.fasterxml.jackson.annotation.JsonProperty

enum class AuditEventType {
    @JsonProperty("CREATE")
    CREATE,

    @JsonProperty("UPDATE")
    UPDATE,

    @JsonProperty("DELETE")
    DELETE
}
