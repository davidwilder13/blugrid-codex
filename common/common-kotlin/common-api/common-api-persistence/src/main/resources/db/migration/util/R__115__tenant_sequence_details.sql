CREATE OR REPLACE FUNCTION tenant_sequence_details(
    IN in_tenant_id BIGINT
)
    RETURNS TABLE
            (
                min_id BIGINT,
                max_id BIGINT
            )
AS
$$
DECLARE
    -- integer	4 bytes	-2147483648 to +2147483647
    -- bigint	8 bytes	-9223372036854775808 to 9223372036854775807

    the_shard_length    CONSTANT INT := 6; -- supports 1 million tenantIds 1 to 999,9999
    in_tenant_id_length CONSTANT INT := 8; -- supports 100 million resourceIds 1 - 99,999,999
BEGIN
    -- set statement variables
    RETURN QUERY SELECT (in_tenant_id * (10 ^ (in_tenant_id_length)))::BIGINT             AS min_id,
                        (((in_tenant_id + 1) * (10 ^ (in_tenant_id_length))) - 1)::BIGINT AS max_id;
END;
$$
    IMMUTABLE LANGUAGE plpgsql;

