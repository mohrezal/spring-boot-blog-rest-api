--liquibase formatted sql
--changeset mohammadreza:create_post_categories_table
CREATE TABLE post_categories(
   post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
   category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
   PRIMARY KEY (post_id, category_id)
);

CREATE INDEX idx_post_categories_category_id ON post_categories(category_id);

--rollback DROP TABLE IF EXISTS post_categories CASCADE;
--rollback DROP INDEX IF EXISTS idx_post_categories_category_id;