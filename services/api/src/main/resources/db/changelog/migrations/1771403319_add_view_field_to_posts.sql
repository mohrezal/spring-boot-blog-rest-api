--liquibase formatted sql
--changeset mohammadreza:add_view_field_to_posts context:schema splitStatements:false
ALTER TABLE posts ADD COLUMN view_count BIGINT NOT NULL DEFAULT 0;

--rollback ALTER TABLE posts DROP COLUMN view_count;
