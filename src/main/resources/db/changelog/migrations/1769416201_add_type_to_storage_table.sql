--liquibase formatted sql
--changeset mohammadreza:add_type_to_storage_table  context:schema splitStatements:false

ALTER TABLE storage ADD COLUMN type VARCHAR(20) NOT NULL DEFAULT 'MEDIA';

--rollback -- Your rollback SQL here
