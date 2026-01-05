--liquibase formatted sql
--changeset mohammadreza:set_avatar_url_not_null_in_posts
ALTER TABLE posts ALTER COLUMN avatar_url SET NOT NULL;

--rollback ALTER TABLE posts ALTER COLUMN avatar_url DROP NOT NULL;