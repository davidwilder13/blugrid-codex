import Mustache from 'mustache'
import { MustacheListItem } from '../../../../../utils/index.js'

export interface DatabaseColumn {
    name: string;
    dataType: string;
    defaultValue?: string;
}

export interface DatabaseIndex {
    name: string;
    columns: MustacheListItem<string>[];
    unique?: boolean;
}

export interface CreateTableSQLProps {
    name: string;
    columns: MustacheListItem<DatabaseColumn>[];
    indexes?: MustacheListItem<DatabaseIndex>[];
    baseTables?: MustacheListItem<string>[];          // e.g., ['shared_common']
    isPartitioned?: boolean;                    // defaults to false
    scope: 'generic' | 'unscoped' | 'tenantScoped' | 'businessUnitScoped';
}

// --- TEMPLATES ---

// language=mustache
const createColumnsTableTemplate = String.raw`
CREATE TABLE IF NOT EXISTS {{name}}_columns (
{{#columns}}
    {{name}} {{dataType}}{{#defaultValue}} DEFAULT {{defaultValue}}{{/defaultValue}}{{^last}},{{/last}}
{{/columns}}
);
`

// language=mustache
const genericTableTemplate = String.raw`
CREATE TABLE IF NOT EXISTS {{NAME}} (
type T_TABLE_NAME NOT NULL DEFAULT '{{NAME}}',
{{#isPartitioned}}CHECK (FALSE) NO INHERIT{{/isPartitioned}}{{^isPartitioned}}CONSTRAINT pk_{{name}} PRIMARY KEY (id){{/isPartitioned}}
)
INHERITS (
_common_unscoped_resource_columns,
{{#baseTables}}{{.}},
{{/baseTables}}{{name}}_columns
)
WITHOUT OIDS;
`

// language=mustache
const unscopedTableTemplate = genericTableTemplate.replace(
    '_common_unscoped_resource_columns',
    '_common_unscoped_resource_columns,\n  _common_audit_columns',
)

// language=mustache
const tenantScopedTableTemplate = genericTableTemplate.replace(
    '_common_unscoped_resource_columns',
    '_common_tenant_resource_columns,\n  _common_audit_columns',
)

// language=mustache
const businessUnitScopedTableTemplate = genericTableTemplate.replace(
    '_common_unscoped_resource_columns',
    '_common_business_unit_resource_columns,\n  _common_audit_columns',
)

// language=mustache
const genericIndexesTemplate = String.raw`
CREATE UNIQUE INDEX IF NOT EXISTS ak_{{name}}_uuid ON {{name}} USING btree (uuid);
{{#indexes}}
    CREATE {{#unique}}UNIQUE {{/unique}}INDEX IF NOT EXISTS ak_{{../name}}_{{name}} ON {{../name}} USING btree ({{#columns}}{{.}}{{^last}}, {{/last}}{{/columns}});
{{/indexes}}
`

// language=mustache
const tenantIndexesExtra = String.raw`
CREATE INDEX IF NOT EXISTS idx_{{name}}_tenant_id ON {{name}} USING btree (tenant_id);
CREATE INDEX IF NOT EXISTS idx_{{name}}_tenant_id_expiry ON {{name}} USING btree (tenant_id, expiry_timestamp);
`

// language=mustache
const businessUnitIndexesExtra = String.raw`
CREATE INDEX IF NOT EXISTS idx_{{name}}_tenant_id ON {{name}} USING btree (tenant_id);
CREATE INDEX IF NOT EXISTS idx_{{name}}_business_unit_id ON {{name}} USING btree (business_unit_id);
CREATE INDEX IF NOT EXISTS idx_{{name}}_tenant_id_expiry ON {{name}} USING btree (tenant_id, expiry_timestamp);
`

// language=mustache
const insertAuditTriggerTemplate = String.raw`
DROP TRIGGER IF EXISTS trig_{{name}}_insert_audit ON {{name}};

CREATE TRIGGER trig_{{name}}_insert_audit
BEFORE INSERT ON {{name}}
FOR EACH ROW
EXECUTE PROCEDURE proc_trig_insert_audit_columns();
`

// language=mustache
const updateAuditTriggerTemplate = String.raw`
DROP TRIGGER IF EXISTS trig_{{name}}_update_audit ON {{name}};

CREATE TRIGGER trig_{{name}}_update_audit
BEFORE UPDATE ON {{name}}
FOR EACH ROW
EXECUTE PROCEDURE proc_trig_update_audit_columns();
`

export const CreateTableSQLTemplate = (props: CreateTableSQLProps): string => {
    const { name, columns, indexes = [], scope, baseTables = [], isPartitioned = false } = props

    const context = {
        name,
        NAME: name.toUpperCase(),
        columns,
        indexes,
        baseTables,
        isPartitioned,
    }

    const output: string[] = []

    // 1. Column table
    output.push(Mustache.render(createColumnsTableTemplate, context))

    // 2. Main table
    const tableTemplate = {
        generic: genericTableTemplate,
        unscoped: unscopedTableTemplate,
        tenantScoped: tenantScopedTableTemplate,
        businessUnitScoped: businessUnitScopedTableTemplate,
    }[scope]

    output.push(Mustache.render(tableTemplate, context))

    // 3. Indexes
    output.push(Mustache.render(genericIndexesTemplate, context))

    if (scope === 'tenantScoped') {
        output.push(Mustache.render(tenantIndexesExtra, context))
    }

    if (scope === 'businessUnitScoped') {
        output.push(Mustache.render(businessUnitIndexesExtra, context))
    }

    // 4. Triggers
    if (scope !== 'generic') {
        output.push(Mustache.render(insertAuditTriggerTemplate, context))
        output.push(Mustache.render(updateAuditTriggerTemplate, context))
    }

    return output.join('\n').trim()
}
