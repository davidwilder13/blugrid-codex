CREATE OR REPLACE FUNCTION is_not_nil(
    IN input    TEXT,
    OUT is_not_nil BOOLEAN
) RETURNS BOOLEAN AS
$body$
DECLARE
BEGIN
    is_not_nil = coalesce(TRIM(input), '') != '';

END;
$body$
IMMUTABLE
    LANGUAGE 'plpgsql';
