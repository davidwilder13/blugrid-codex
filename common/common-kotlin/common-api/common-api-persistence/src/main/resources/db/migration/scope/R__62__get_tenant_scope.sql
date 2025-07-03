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
