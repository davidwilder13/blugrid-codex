-- -----------------------------------------------------------------------------
-- Function: reset_request_scope
-- Description: Resets all request-scope settings (tenant.id, session.id, operator.party.id, business_unit.id) to 0.
-- -----------------------------------------------------------------------------
-- Examples:
--   SELECT reset_request_scope();  -- resets scopes and returns 1
CREATE OR REPLACE FUNCTION reset_request_scope(
) RETURNS INT AS
$body$
DECLARE
    CLIENT_ID_CONFIG         CONSTANT TEXT = 'tenant.id';
    SESSION_ID_CONFIG        CONSTANT TEXT = 'session.id';
    OPERATOR_PARTY_ID_CONFIG CONSTANT TEXT = 'operator.party.id';
    BUSINESS_UNIT_ID_CONFIG  CONSTANT TEXT = 'business_unit.id';
BEGIN
    PERFORM set_config(CLIENT_ID_CONFIG, 0::TEXT, FALSE);
    PERFORM set_config(SESSION_ID_CONFIG, 0::TEXT, FALSE);
    PERFORM set_config(OPERATOR_PARTY_ID_CONFIG, 0::TEXT, FALSE);
    PERFORM set_config(BUSINESS_UNIT_ID_CONFIG, 0::TEXT, FALSE);
    RETURN 1;
END;
$body$ LANGUAGE plpgsql;
