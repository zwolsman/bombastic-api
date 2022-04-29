ALTER TABLE profiles
    RENAME COLUMN points TO bits;

ALTER TABLE profiles
    RENAME COLUMN points_earned TO bits_earned;

ALTER TABLE profiles
    DROP COLUMN balance_in_eur;
