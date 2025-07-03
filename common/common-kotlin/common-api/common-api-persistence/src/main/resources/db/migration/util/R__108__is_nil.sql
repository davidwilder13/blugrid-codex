CREATE OR REPLACE FUNCTION is_nil(
    IN input    TEXT,
    OUT is_nil BOOLEAN
) RETURNS BOOLEAN AS
$body$
DECLARE
BEGIN
    is_nil = coalesce(TRIM(input), '') = '';

END;
$body$
IMMUTABLE
    LANGUAGE 'plpgsql';
