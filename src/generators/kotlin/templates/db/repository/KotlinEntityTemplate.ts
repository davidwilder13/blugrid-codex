import Mustache from 'mustache';
import { MustacheListItem } from '../../../../../utils/index.js'

export type KotlinEntityType =
    | 'generic'
    | 'unscoped'
    | 'tenantScoped'
    | 'businessUnitScoped';

export interface KotlinEntityField {
    name: string;
    type: string;
    columnName: string;
    nullable: boolean;
    updatable?: boolean; // optional: defaults to true
}

export interface KotlinEntityTemplateProps {
    packageName: string;         // e.g., 'net.blugrid.api.organisation'
    entityName: string;          // e.g., 'Organisation'
    tableName: string;           // e.g., 'vw_organisation'
    sequenceName: string;        // e.g., 'organisation-sequence'
    generatorStrategy: string;   // e.g., 'net.blugrid.api.db.GlobalTenantSequenceGenerator'
    fields: MustacheListItem<KotlinEntityField>[];
    entityType: KotlinEntityType;

    /**
     * Whether the entity should include audit fields (i.e., `@Embedded audit: EmbeddedAuditEntity`)
     * This is usually true for all resource entities except generic.
     */
    isAudited: boolean;
}

// language=mustache
const template =
    String.raw`package {{packageName}}.repository.model

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.SEQUENCE
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table{{#hasAudit}}
import net.blugrid.api.common.repository.model.EmbeddedAuditEntity{{/hasAudit}}
import net.blugrid.api.common.repository.model.{{extends}}
import net.blugrid.api.util.kotlinEquals
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.Objects
import java.util.UUID

@Entity
@Table(name = "{{tableName}}")
class {{entityName}}Entity(

    @Id
    @GeneratedValue(strategy = SEQUENCE, generator = "{{sequenceName}}")
    @GenericGenerator(name = "{{sequenceName}}", strategy = "{{generatorStrategy}}")
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    override var id: Long? = null,

    @Column(name = "uuid", updatable = false)
    override var uuid: UUID,

    {{#fields}}
    @Column(name = "{{columnName}}"{{#nullable}}, nullable = true{{/nullable}}{{^nullable}}, nullable = false{{/nullable}}{{^updatable}}, updatable = false{{/updatable}})
    var {{name}}: {{type}}{{#nullable}}? = null{{/nullable}}{{^last}},{{/last}}
    
    {{/fields}}
) : {{extends}}<{{entityName}}Entity> {
    {{#hasAudit}}
        
    @Embedded
    override var audit: EmbeddedAuditEntity = EmbeddedAuditEntity()
    {{/hasAudit}}
    {{#hasPermission}}
        
    @Transient
    override val permission: {{permissionType}}? = null
    {{/hasPermission}}
    {{#hasAudit}}
        
    @PrePersist
    fun prePersist() {
        audit.prePersist()
    }

    @PreUpdate
    fun preUpdate() {
        audit.preUpdate()
    }
    {{/hasAudit}}
    
    companion object {
        private val equalsProperties = arrayOf(
{{#fields}}
            {{entityName}}Entity::{{name}}{{^last}},{{/last}}
{{/fields}}
        )
    }

    override fun update(update: {{entityName}}Entity): {{entityName}}Entity {
{{#fields}}
        this.{{name}} = update.{{name}}
{{/fields}}
        return this
    }

    override fun equals(other: Any?) = kotlinEquals(other = other, properties = equalsProperties)

    override fun hashCode() = Objects.hash(
        uuid{{#fields}}, {{name}}{{/fields}}
    )
}
`

export const KotlinEntityTemplate = (props: KotlinEntityTemplateProps): string => {
    const context = {
        ...props,
        extends: resolveEntityExtends(props.entityType),
        hasAudit: ['unscoped', 'tenantScoped', 'businessUnitScoped'].includes(props.entityType),
        hasPermission: props.entityType === 'tenantScoped' || props.entityType === 'businessUnitScoped',
        permissionType:
            props.entityType === 'tenantScoped'
                ? 'ITenantPermissionEntity'
                : props.entityType === 'businessUnitScoped'
                    ? 'IBusinessUnitPermissionEntity'
                    : null,
    };
    return Mustache.render(template, context)
}

function resolveEntityExtends(type: KotlinEntityType): string {
    switch (type) {
        case 'unscoped': return 'UnscopedResourceEntity';
        case 'tenantScoped': return 'TenantResourceEntity';
        case 'businessUnitScoped': return 'BusinessUnitResourceEntity';
        default: return 'GenericEntity';
    }
}
