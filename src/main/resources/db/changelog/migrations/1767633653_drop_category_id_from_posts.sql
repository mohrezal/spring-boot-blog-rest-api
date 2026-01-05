--liquibase formatted sql
--changeset mohammadreza:drop_category_id_from_posts
ALTER TABLE posts DROP COLUMN category_id;
--rollback ALTER TABLE posts ADD COLUMN category_id UUID;
--rollback ALTER TABLE posts ADD CONSTRAINT fk_posts_category FOREIGN KEY (category_id) REFERENCES categories(id);
--rollback CREATE INDEX idx_posts_category_id ON posts(category_id);
