--liquibase formatted sql
--changeset mohammadreza:rename_meta_description_to_description
ALTER TABLE posts RENAME COLUMN meta_description TO description;

--rollback ALTER TABLE posts RENAME COLUMN description TO meta_description;
