--liquibase formatted sql
--changeset mohammadreza:enable_pg_trgm_extension context:schema splitStatements:false

CREATE EXTENSION IF NOT EXISTS pg_trgm;

--rollback DROP EXTENSION IF EXISTS pg_trgm;
