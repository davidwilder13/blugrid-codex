-- -----------------------------------------------------------------------------
-- Function: get_session_scope
-- Description: Retrieves the session_id from the database setting 'session.id', raising an exception if not set and returning 0 if undefined.
-- -----------------------------------------------------------------------------
-- Examples:
--   SELECT get_session_scope();  -- returns the configured session_id or 0 if undefined
CREATE OR REPLACE FUNCTION get_session_scope(
    OUT session_id BIGINT
) RETURNS BIGINT AS
$body$
DECLARE
    SESSION_ID_CONFIG        CONSTANT TEXT = 'session.id';
    the_session_id TEXT;
BEGIN
    SELECT current_setting(SESSION_ID_CONFIG) INTO the_session_id;

    IF (the_session_id IS NULL) THEN
        RAISE EXCEPTION 'The sessionId scope is not set' USING HINT = 'The database current_setting session.id has not been set';
    END IF;

    the_session_id = REPLACE(the_session_id, '"', '');

    session_id = text_to_bigint(the_session_id :: TEXT);
EXCEPTION
    WHEN undefined_object THEN session_id = 0;

END;
$body$ LANGUAGE plpgsql;
