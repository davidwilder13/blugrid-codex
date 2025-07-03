CREATE TABLE _multitenant_table (
    name                   T_NAME NOT NULL,
    description            T_DESCRIPTION,
    tenant_id              T_IDENTITY,
    id_min                 T_IDENTITY,
    id_max                 T_IDENTITY,
    id_current_val         T_IDENTITY,
    created_timestamp      T_TIMESTAMP DEFAULT now(),
    last_changed_timestamp T_TIMESTAMP DEFAULT now(),
    expiry_timestamp       T_TIMESTAMP NOT NULL DEFAULT 'infinity',

    CONSTRAINT pk_multitenant_table PRIMARY KEY (name)
)
WITHOUT OIDS;
