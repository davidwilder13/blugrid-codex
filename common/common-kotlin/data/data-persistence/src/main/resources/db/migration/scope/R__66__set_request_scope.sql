-- -----------------------------------------------------------------------------
-- Functions: set_tenant_scope, set_session_scope, set_business_unit_scope,
--            set_tenant_session (overloads), set_business_unit_session (overloads)
-- Description: Configures database session settings for tenant, session, and business unit scope variables
--              and optionally adjusts search_path for combined scope settings.
-- -----------------------------------------------------------------------------
-- Examples:
--   SELECT set_tenant_scope('1');                    -- returns 1 and sets tenant.id
--   SELECT set_tenant_session('public', '1', '123'); -- sets search_path, tenant.id, session.id
CREATE OR REPLACE FUNCTION set_tenant_scope(
    IN tenant_id         TEXT
) RETURNS INT AS
$body$
DECLARE
    TENANT_ID_CONFIG         CONSTANT TEXT = 'tenant.id';
BEGIN
    PERFORM SET_CONFIG(TENANT_ID_CONFIG, QUOTE_IDENT(tenant_id :: TEXT), FALSE);
    RETURN 1;
END;
$body$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION set_session_scope(
    IN session_id         TEXT
) RETURNS INT AS
$body$
DECLARE
    SESSION_ID_CONFIG         CONSTANT TEXT = 'session.id';
BEGIN
    PERFORM SET_CONFIG(SESSION_ID_CONFIG, QUOTE_IDENT(session_id :: TEXT), FALSE);
    RETURN 1;
END;
$body$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION set_business_unit_scope(
    IN business_unit_id         TEXT
) RETURNS INT AS
$body$
DECLARE
    BUSINESS_UNIT_ID_CONFIG  CONSTANT TEXT = 'business_unit.id';
BEGIN
    PERFORM SET_CONFIG(BUSINESS_UNIT_ID_CONFIG, QUOTE_IDENT(business_unit_id :: TEXT), FALSE);
    RETURN 1;
END;
$body$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION set_tenant_session(
    IN search_path       TEXT,
    IN tenant_id         TEXT
) RETURNS INT AS
$body$
BEGIN
    PERFORM pg_catalog.set_config('search_path', search_path, false);
    PERFORM set_tenant_scope(tenant_id);
    RETURN 1;
END;
$body$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION set_tenant_session(
    IN search_path       TEXT,
    IN tenant_id         TEXT,
    IN session_id        TEXT
) RETURNS INT AS
$body$
BEGIN
    PERFORM pg_catalog.set_config('search_path', search_path, false);
    PERFORM set_tenant_scope(tenant_id);
    PERFORM set_session_scope(session_id);
    RETURN 1;
END;
$body$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION set_business_unit_session(
    IN search_path       TEXT,
    IN tenant_id         TEXT,
    IN business_unit_id  TEXT
) RETURNS INT AS
$body$
BEGIN
    PERFORM pg_catalog.set_config('search_path', search_path, false);
    PERFORM set_tenant_scope(tenant_id);
    PERFORM set_business_unit_scope(business_unit_id);
    RETURN 1;
END;
$body$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION set_business_unit_session(
    IN search_path       TEXT,
    IN tenant_id         TEXT,
    IN business_unit_id  TEXT,
    IN session_id        TEXT
) RETURNS INT AS
$body$
BEGIN
    PERFORM pg_catalog.set_config('search_path', search_path, false);
    PERFORM set_tenant_scope(tenant_id);
    PERFORM set_business_unit_scope(business_unit_id);
    PERFORM set_session_scope(session_id);
    RETURN 1;
END;
$body$ LANGUAGE plpgsql;
