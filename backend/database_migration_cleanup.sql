-- Database Cleanup Migration Script
-- This script fixes schema issues to align with the updated entity definitions
-- Run this script manually on your PostgreSQL database

-- ============================================================================
-- 1. Remove duplicate 'text' column from reviews table (if it exists)
-- ============================================================================
-- Check if the column exists first, then drop it
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'reviews'
        AND column_name = 'text'
    ) THEN
        ALTER TABLE reviews DROP COLUMN text;
        RAISE NOTICE 'Dropped duplicate "text" column from reviews table';
    ELSE
        RAISE NOTICE 'Column "text" does not exist in reviews table, skipping';
    END IF;
END $$;

-- ============================================================================
-- 2. Change favorite_picks.tmdb_id from INTEGER to BIGINT
-- ============================================================================
-- This ensures consistency with other tables that reference TMDB IDs
ALTER TABLE favorite_picks
ALTER COLUMN tmdb_id TYPE BIGINT;

-- ============================================================================
-- 3. Change higher_lower_questions.tmdb_id from INTEGER to BIGINT
-- ============================================================================
ALTER TABLE higher_lower_questions
ALTER COLUMN tmdb_id TYPE BIGINT;

-- ============================================================================
-- 4. Add CHECK constraints for media_type validation
-- ============================================================================
-- Note: These constraints are defined in JPA entities with @Check annotation
-- Hibernate will automatically add them when ddl-auto=update runs
-- Including them here for manual execution if needed

-- Add CHECK constraint to reviews table (if not already exists)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'reviews_media_type_check'
    ) THEN
        ALTER TABLE reviews
        ADD CONSTRAINT reviews_media_type_check
        CHECK (media_type IN ('movie', 'tv'));
        RAISE NOTICE 'Added media_type CHECK constraint to reviews table';
    END IF;
END $$;

-- Add CHECK constraint for rating in reviews table (if not already exists)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'reviews_rating_check'
    ) THEN
        ALTER TABLE reviews
        ADD CONSTRAINT reviews_rating_check
        CHECK (rating IS NULL OR (rating >= 1 AND rating <= 10));
        RAISE NOTICE 'Added rating CHECK constraint to reviews table';
    END IF;
END $$;

-- Add CHECK constraint to favorites table (if not already exists)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'favorites_media_type_check'
    ) THEN
        ALTER TABLE favorites
        ADD CONSTRAINT favorites_media_type_check
        CHECK (media_type IN ('movie', 'tv'));
        RAISE NOTICE 'Added media_type CHECK constraint to favorites table';
    END IF;
END $$;

-- Add CHECK constraint to watchlist table (if not already exists)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'watchlist_media_type_check'
    ) THEN
        ALTER TABLE watchlist
        ADD CONSTRAINT watchlist_media_type_check
        CHECK (media_type IN ('movie', 'tv'));
        RAISE NOTICE 'Added media_type CHECK constraint to watchlist table';
    END IF;
END $$;

-- Add CHECK constraint to favorite_picks table (if not already exists)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'favorite_picks_media_type_check'
    ) THEN
        ALTER TABLE favorite_picks
        ADD CONSTRAINT favorite_picks_media_type_check
        CHECK (media_type IN ('movie', 'tv'));
        RAISE NOTICE 'Added media_type CHECK constraint to favorite_picks table';
    END IF;
END $$;

-- Add CHECK constraint to higher_lower_questions table (if not already exists)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'higher_lower_questions_media_type_check'
    ) THEN
        ALTER TABLE higher_lower_questions
        ADD CONSTRAINT higher_lower_questions_media_type_check
        CHECK (media_type IN ('movie', 'tv'));
        RAISE NOTICE 'Added media_type CHECK constraint to higher_lower_questions table';
    END IF;
END $$;

-- ============================================================================
-- 5. Verify UNIQUE constraints (these should already exist from JPA)
-- ============================================================================
-- List all unique constraints for verification
SELECT
    conname AS constraint_name,
    conrelid::regclass AS table_name
FROM pg_constraint
WHERE contype = 'u'
AND conrelid::regclass::text IN ('reviews', 'favorites', 'watchlist')
ORDER BY table_name, constraint_name;

-- ============================================================================
-- Migration Complete
-- ============================================================================
-- Summary of changes:
-- 1. Removed duplicate 'text' column from reviews table (if existed)
-- 2. Changed favorite_picks.tmdb_id from INTEGER to BIGINT
-- 3. Changed higher_lower_questions.tmdb_id from INTEGER to BIGINT
-- 4. Added CHECK constraints for media_type validation on all relevant tables
-- 5. Added CHECK constraint for rating validation on reviews table
--
-- Next steps:
-- 1. Verify the migration completed successfully by reviewing the output
-- 2. Test the application with the updated schema
-- 3. Run the backend with spring.jpa.hibernate.ddl-auto=validate to ensure
--    entity definitions match the database schema
