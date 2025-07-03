package net.blugrid.api.common.persistence.audit

import io.micronaut.data.annotation.Embeddable
import io.micronaut.data.annotation.Version
import jakarta.persistence.Column
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Embeddable
class AuditEmbeddable : AuditableEntityFields {
    @CreationTimestamp
    @Column(name = "created_timestamp", updatable = false)
    override var createdTimestamp: LocalDateTime? = null

    @UpdateTimestamp
    @Column(name = "last_changed_timestamp")
    override var lastChangedTimestamp: LocalDateTime? = null

    @Column(name = "created_by_session_id", updatable = false)
    override var createdBySessionId: Long? = null

    @Column(name = "last_changed_by_session_id")
    override var lastChangedBySessionId: Long? = null

    @Version
    @Column(name = "version")
    override var version: Int = 0

    fun applyCreateAudit(sessionId: Long?) {
        createdBySessionId = sessionId
        lastChangedBySessionId = sessionId
    }

    fun applyUpdateAudit(sessionId: Long?) {
        lastChangedBySessionId = sessionId
    }
}

interface AuditableEntityFields {
    var createdTimestamp: LocalDateTime?
    var lastChangedTimestamp: LocalDateTime?
    var createdBySessionId: Long?
    var lastChangedBySessionId: Long?
    var version: Int
}
