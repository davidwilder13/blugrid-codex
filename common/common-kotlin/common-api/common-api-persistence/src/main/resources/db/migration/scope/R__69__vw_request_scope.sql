DROP VIEW IF EXISTS vw_request_scope CASCADE;
CREATE OR REPLACE VIEW vw_request_scope
AS

SELECT
    get_tenant_scope() AS tenant_id,
    get_session_scope() AS session_id,
    get_operator_party_scope() AS operator_party_id,
    get_business_unit_scope() AS business_unit_id,
    get_business_unit_tenant_scope() AS business_unit_tenant_id
;

