-- Fix reviews table to allow NULL for updated_at
-- This is needed for existing reviews created before the fix

ALTER TABLE reviews ALTER COLUMN updated_at DROP NOT NULL;

-- Update existing reviews to set updated_at = created_at where NULL
UPDATE reviews SET updated_at = created_at WHERE updated_at IS NULL;
