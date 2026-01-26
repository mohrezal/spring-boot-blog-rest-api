--liquibase formatted sql
--changeset mohammadreza:drop_avatr_url_add_avatar_column context:schema splitStatements:false

ALTER TABLE users ADD COLUMN avatar_id UUID REFERENCES storage(id);
ALTER TABLE users DROP COLUMN avatar_url;

--rollback ALTER TABLE users DROP COLUMN avatar_id;
--rollback ALTER TABLE users ADD COLUMN avatar_url TEXT;
