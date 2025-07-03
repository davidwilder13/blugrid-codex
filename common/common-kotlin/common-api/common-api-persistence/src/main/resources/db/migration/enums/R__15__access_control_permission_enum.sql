CREATE DOMAIN t_global_access_control_permission_type AS VARCHAR(20) CHECK (value IN ('READ', 'WRITE', 'NONE'));
