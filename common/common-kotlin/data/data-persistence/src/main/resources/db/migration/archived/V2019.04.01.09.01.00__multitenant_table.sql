-- -----------------------------------------------------------------------------
-- Table: _multitenant_table
-- Description: Tracks multi-tenant partition metadata including ID ranges, current values, and expiry timestamps.
-- -----------------------------------------------------------------------------
-- Examples:
--   SELECT * FROM _multitenant_table WHERE name = 'orders';
create table _multitenant_table
(
    name                   CHARACTER VARYING(128) not null,
    description            CHARACTER VARYING,
    tenant_id              BIGINT,
    id_min                 BIGINT,
    id_max                 BIGINT,
    id_current_val         BIGINT,
    created_timestamp      TIMESTAMP                       default now(),
    last_changed_timestamp TIMESTAMP                       default now(),
    expiry_timestamp       TIMESTAMP              not null default 'infinity',

    constraint pk_multitenant_table primary key (name)
) without OIDS;
