--liquibase formatted sql
--changeset mohammadreza:add_version_to_posts
ALTER TABLE posts ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

--rollback ALTER TABLE posts DROP COLUMN version;
