-- -----------------------------------------------------------------------------
-- Function: is_nil
-- Description: Checks whether a text input is NULL or empty after trimming.
-- -----------------------------------------------------------------------------
-- Examples:
--   SELECT is_nil(NULL);   -- true
--   SELECT is_nil('');     -- true
--   SELECT is_nil('   ');  -- true
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
