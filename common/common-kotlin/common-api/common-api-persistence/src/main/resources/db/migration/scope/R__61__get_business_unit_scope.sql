CREATE OR REPLACE FUNCTION get_business_unit_scope(
    OUT business_unit_id BIGINT
) RETURNS BIGINT AS
$body$
DECLARE
    BUSINESS_UNIT_ID_CONFIG  CONSTANT TEXT = 'business_unit.id';
    the_business_unit_id TEXT;
BEGIN
    SELECT current_setting(BUSINESS_UNIT_ID_CONFIG) INTO the_business_unit_id;

    IF (the_business_unit_id IS NULL) THEN
        RAISE EXCEPTION 'The business_unitId scope is not set' USING HINT = 'The database current_setting business_unit.id has not been set';
    END IF;

    the_business_unit_id = REPLACE(the_business_unit_id, '"', '');

    business_unit_id = text_to_bigint(the_business_unit_id :: TEXT);
EXCEPTION
    WHEN undefined_object THEN business_unit_id = 0;

END;
$body$ LANGUAGE plpgsql;

