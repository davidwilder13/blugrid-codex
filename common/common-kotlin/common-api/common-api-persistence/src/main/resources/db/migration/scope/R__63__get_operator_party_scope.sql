CREATE OR REPLACE FUNCTION get_operator_party_scope(
    OUT operator_party_id BIGINT
) RETURNS BIGINT AS
$body$
DECLARE
    OPERATOR_PARTY_ID_CONFIG CONSTANT TEXT = 'operator.party.id';
    the_operator_party_id TEXT;
BEGIN
    SELECT current_setting(OPERATOR_PARTY_ID_CONFIG) INTO the_operator_party_id;

    IF (the_operator_party_id IS NULL) THEN
        RAISE EXCEPTION 'The operator_party_id scope is not set' USING HINT = 'The database current_setting operator.party.id has not been set';
    END IF;

    the_operator_party_id = REPLACE(the_operator_party_id, '"', '');

    operator_party_id = text_to_bigint(the_operator_party_id :: TEXT);
EXCEPTION
    WHEN undefined_object THEN operator_party_id = 0;

END;
$body$ LANGUAGE plpgsql;
