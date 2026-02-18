--liquibase formatted sql
--changeset mohammadreza:add_post_views_table context:schema splitStatements:false
--validCheckSum: 9:d66198db73d521368aa1b61ad1cb2a2c

CREATE TABLE IF NOT EXISTS post_views (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    post_id UUID NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    viewer_hash TEXT NOT NULL,
    visitor_id_hash TEXT NOT NULL,
    user_id UUID NULL REFERENCES users(id) ON DELETE SET NULL,

    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_post_views_post_id_viewer_hash UNIQUE (post_id, viewer_hash)

);

CREATE INDEX idx_post_views_post_id_created_at ON post_views(post_id, created_at DESC);
CREATE INDEX idx_post_views_visitor_id_hash ON post_views(visitor_id_hash);
CREATE INDEX idx_posts_views_partial_user_id ON post_views(user_id) WHERE user_id IS NOT NULL;
--rollback DROP TABLE IF EXISTS post_views;
