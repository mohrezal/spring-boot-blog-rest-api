--liquibase formatted sql
--changeset mohammadreza:create_user_follows_table context:schema splitStatements:false
CREATE TABLE user_follows (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

    follower_id  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    followed_id  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_user_follows_follower_followed UNIQUE (follower_id, followed_id),
    CONSTRAINT chk_user_follows_no_self_follow CHECK (follower_id != followed_id)
    );

CREATE INDEX idx_user_follows_follower_id ON user_follows(follower_id);
CREATE INDEX idx_user_follows_followed_id ON user_follows(followed_id);

--rollback DROP INDEX IF EXISTS idx_user_follows_followed_id;
--rollback DROP INDEX IF EXISTS idx_user_follows_follower_id;
--rollback DROP TABLE IF EXISTS user_follows CASCADE;
