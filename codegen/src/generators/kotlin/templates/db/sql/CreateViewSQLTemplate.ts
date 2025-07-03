import Mustache from 'mustache'

export interface CreateViewSQLProps {
    name: string;
    type: 'generic' | 'unscoped' | 'tenantScoped' | 'businessUnitScoped';
    unscoped?: boolean;
}

// --- TEMPLATES ---

// language=mustache
const genericViewTemplate = String.raw`
DROP VIEW IF EXISTS vw_{{name}} CASCADE;
CREATE OR REPLACE VIEW vw_{{name}}
AS
SELECT *
FROM {{name}};
`

// language=mustache
const unscopedViewTemplate = String.raw`
DROP VIEW IF EXISTS vw_{{name}} CASCADE;
CREATE OR REPLACE VIEW vw_{{name}} WITH (security_barrier)
AS
SELECT *
FROM {{name}}
WHERE expiry_timestamp > now()
WITH CHECK OPTION;
`

// language=mustache
const unscopedReadonlyViewTemplate = String.raw`
DROP VIEW IF EXISTS vw_{{name}}_unscoped CASCADE;
CREATE OR REPLACE VIEW vw_{{name}}_unscoped
AS
SELECT *
FROM {{name}}
WHERE expiry_timestamp > now()
WITH CHECK OPTION;
`

// language=mustache
const tenantScopedViewTemplate = String.raw`
DROP VIEW IF EXISTS vw_{{name}} CASCADE;
CREATE OR REPLACE VIEW vw_{{name}} WITH (security_barrier)
AS
SELECT *
FROM {{name}}
WHERE
  expiry_timestamp > now()
WITH CHECK OPTION;
`

// language=mustache
const businessUnitScopedViewTemplate = String.raw`
DROP VIEW IF EXISTS vw_{{name}} CASCADE;
CREATE OR REPLACE VIEW vw_{{name}} WITH (security_barrier)
AS
SELECT *
FROM {{name}}
WHERE
  {{#unscoped}}tenant_id IN (get_tenant_scope(), get_business_unit_tenant_scope()) AND{{/unscoped}} expiry_timestamp > now()
WITH CHECK OPTION;
`

// language=mustache
const dropInsertTriggerTemplate = String.raw`
DROP TRIGGER IF EXISTS trig_{{name}}_insert ON vw_{{name}};
`

// language=mustache
const createInsertTriggerTemplate = String.raw`
CREATE TRIGGER trig_{{name}}_insert
    INSTEAD OF INSERT
    ON vw_{{name}}
    FOR EACH ROW
EXECUTE PROCEDURE proc_trig_{{name}}_insert();
`

// language=mustache
const insertTriggerFnUnscopedTemplate = String.raw`
CREATE OR REPLACE FUNCTION proc_trig_{{name}}_insert()
RETURNS TRIGGER AS $body$
DECLARE
BEGIN
    new.expiry_timestamp = 'infinity';
    new.version = 0;
    new.type = '{{NAME}}';

    EXECUTE 'INSERT INTO {{NAME}} VALUES ($1.*)' USING new;
    RETURN new;
END;
$body$ LANGUAGE 'plpgsql';
`

// language=mustache
const insertTriggerFnTenantTemplate = String.raw`
CREATE OR REPLACE FUNCTION proc_trig_{{name}}_insert()
RETURNS TRIGGER AS $body$
DECLARE
BEGIN
    new.expiry_timestamp = 'infinity';
    new.version = 0;
    new.tenant_id = get_tenant_scope();
    new.type = '{{NAME}}';

    EXECUTE 'INSERT INTO {{NAME}} VALUES ($1.*)' USING new;
    RETURN new;
END;
$body$ LANGUAGE 'plpgsql';
`

// language=mustache
const insertTriggerFnBusinessUnitTemplate = String.raw`
CREATE OR REPLACE FUNCTION proc_trig_{{name}}_insert()
RETURNS TRIGGER AS $body$
DECLARE
BEGIN
    new.expiry_timestamp = 'infinity';
    new.version = 0;
    new.tenant_id = get_business_unit_tenant_scope();
    new.business_unit_id = get_business_unit_scope();
    new.type = '{{NAME}}';

    EXECUTE 'INSERT INTO {{NAME}} VALUES ($1.*)' USING new;
    RETURN new;
END;
$body$ LANGUAGE 'plpgsql';
`

export const CreateViewSQLTemplate = ({ name, type, unscoped = false }: CreateViewSQLProps): string => {
    const context = {
        name,
        NAME: name.toUpperCase(),
        unscoped,
    }

    switch (type) {
        case 'generic':
            return Mustache.render(genericViewTemplate, context)

        case 'unscoped':
            return [
                Mustache.render(unscopedViewTemplate, context),
                Mustache.render(insertTriggerFnUnscopedTemplate, context),
                Mustache.render(dropInsertTriggerTemplate, context),
                Mustache.render(createInsertTriggerTemplate, context),
            ].join('\n\n')

        case 'tenantScoped':
            return [
                Mustache.render(tenantScopedViewTemplate, context),
                Mustache.render(insertTriggerFnTenantTemplate, context),
                Mustache.render(dropInsertTriggerTemplate, context),
                Mustache.render(createInsertTriggerTemplate, context),
                Mustache.render(unscopedReadonlyViewTemplate, context),
            ].join('\n\n')

        case 'businessUnitScoped':
            return [
                Mustache.render(businessUnitScopedViewTemplate, context),
                Mustache.render(insertTriggerFnBusinessUnitTemplate, context),
                Mustache.render(dropInsertTriggerTemplate, context),
                Mustache.render(createInsertTriggerTemplate, context),
                Mustache.render(unscopedReadonlyViewTemplate, context),
            ].join('\n')
    }
}
