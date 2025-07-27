-- -----------------------------------------------------------------------------
-- Function: is_not_nil
-- Description: Checks whether a text input is not NULL and not empty after trimming.
-- -----------------------------------------------------------------------------
-- Examples:
--   SELECT is_not_nil('foo');   -- true
--   SELECT is_not_nil('');      -- false
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
