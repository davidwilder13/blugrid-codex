-- -----------------------------------------------------------------------------
-- Function: get_business_unit_tenant_scope
-- Description: Retrieves the tenant_id for the current business unit scope, if the business_unit table exists.
-- -----------------------------------------------------------------------------
-- Examples:
--   SELECT get_business_unit_tenant_scope();  -- returns the tenant_id of the current business unit or NULL
CREATE OR REPLACE FUNCTION get_business_unit_tenant_scope(
    OUT business_unit_tenant_id BIGINT
) RETURNS BIGINT AS
$body$
BEGIN
    IF (table_exists('business_unit') IS TRUE) THEN
        SELECT bu.tenant_id FROM business_unit bu WHERE bu.id = get_business_unit_scope()
        INTO business_unit_tenant_id;
    END IF;
END
$body$ LANGUAGE plpgsql;
