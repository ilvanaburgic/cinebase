-- Create favorites table
CREATE TABLE IF NOT EXISTS favorites (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    tmdb_id BIGINT NOT NULL,
    media_type VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    poster_path VARCHAR(255),
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_tmdb_media UNIQUE (user_id, tmdb_id, media_type)
);

-- Create index for faster queries
CREATE INDEX IF NOT EXISTS idx_favorites_user_id ON favorites(user_id);
CREATE INDEX IF NOT EXISTS idx_favorites_added_at ON favorites(added_at DESC);
