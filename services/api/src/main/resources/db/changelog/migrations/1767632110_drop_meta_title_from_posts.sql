--liquibase formatted sql
--changeset mohammadreza:drop_meta_title_from_posts
ALTER TABLE posts DROP COLUMN meta_title;

--rollback ALTER TABLE posts ADD COLUMN meta_title VARCHAR(150);