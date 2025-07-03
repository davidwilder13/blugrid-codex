-- -----------------------------------------------------------------------------
-- Function: get_tenant_scope
-- Description: Retrieves the tenant_id from the database setting 'tenant.id', raising an exception if not set and returning 0 if undefined.
-- -----------------------------------------------------------------------------
-- Examples:
--   SELECT get_tenant_scope();  -- returns the configured tenant_id or 0 if undefined
CREATE OR REPLACE FUNCTION get_tenant_scope(
    OUT tenant_id BIGINT
) RETURNS BIGINT AS
$body$
DECLARE
    CLIENT_ID_CONFIG         CONSTANT TEXT = 'tenant.id';
    the_tenant_id TEXT;
BEGIN
    SELECT current_setting(CLIENT_ID_CONFIG) INTO the_tenant_id;

    IF (the_tenant_id IS NULL) THEN
        RAISE EXCEPTION 'The tenantId scope is not set' USING HINT = 'The database current_setting tenant.id has not been set';
    END IF;

    the_tenant_id = REPLACE(the_tenant_id, '"', '');

    tenant_id = text_to_bigint(the_tenant_id :: TEXT);
EXCEPTION
    WHEN undefined_object THEN tenant_id = 0;

END;
$body$
   STABLE LANGUAGE plpgsql;
