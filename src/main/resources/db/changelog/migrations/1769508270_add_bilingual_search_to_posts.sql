--liquibase formatted sql
--changeset mohammadreza:add_search_vector_and_tsvector_column_to_posts_table  context:schema splitStatements:false
DO $$
BEGIN
      IF NOT EXISTS (SELECT 1 FROM pg_ts_config WHERE cfgname = 'persian') THEN
          CREATE TEXT SEARCH CONFIGURATION persian (COPY = simple);
END IF;
END $$;


ALTER TABLE posts ADD COLUMN language VARCHAR(100) DEFAULT 'OTHER';

CREATE OR REPLACE FUNCTION posts_language_to_config(language text)
RETURNS regconfig AS $$
BEGIN
    CASE LOWER(language)
        WHEN 'english' THEN RETURN 'english'::regconfig;
        WHEN 'persian' THEN RETURN 'persian'::regconfig;
        ELSE RETURN 'simple'::regconfig;
    END CASE;
END;

  $$ LANGUAGE plpgsql IMMUTABLE;

ALTER TABLE posts ADD COLUMN search_vector tsvector
    GENERATED ALWAYS AS (
        setweight(to_tsvector(posts_language_to_config(language), COALESCE(title, '')), 'A') ||
        setweight(to_tsvector(posts_language_to_config(language), COALESCE(description, '')), 'D')
        ) STORED;


CREATE INDEX idx_posts_search ON posts USING GIN (search_vector);
CREATE INDEX idx_posts_title_trigram ON posts USING GIN (title gin_trgm_ops);

--rollback DROP INDEX IF EXISTS idx_posts_search;
--rollback ALTER TABLE posts DROP COLUMN IF EXISTS search_vector;
--rollback DROP FUNCTION IF EXISTS posts_language_to_config(text);
--rollback ALTER TABLE posts DROP COLUMN IF EXISTS language;
--rollback DROP TEXT SEARCH CONFIGURATION IF EXISTS persian;