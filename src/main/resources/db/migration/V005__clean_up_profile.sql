ALTER TABLE postgres.public.profiles
    RENAME COLUMN points TO bits;

ALTER TABLE postgres.public.profiles
    RENAME COLUMN points_earned TO bits_earned;

ALTER TABLE postgres.public.profiles
    DROP COLUMN balance_in_eur;
