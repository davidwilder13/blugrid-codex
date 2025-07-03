CREATE OR REPLACE FUNCTION text_to_bigint(
    v_input TEXT
) RETURNS BIGINT AS
$$
DECLARE
    v_int_value BIGINT DEFAULT NULL;
BEGIN
    BEGIN
        v_int_value := v_input :: BIGINT;
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'Invalid integer value: "%".  Returning NULL.', v_input;
        RETURN NULL;
    END;
    RETURN v_int_value;
END;
$$ LANGUAGE plpgsql;
