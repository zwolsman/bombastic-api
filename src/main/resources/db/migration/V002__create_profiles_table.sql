CREATE TABLE profiles
(
    id                  SERIAL         NOT NULL,
    name                TEXT           NOT NULL,
    email               TEXT           NOT NULL,
    points              INT            NOT NULL DEFAULT 0 CHECK (points >= 0),
    euros_spent         NUMERIC(12, 2) NOT NULL DEFAULT 0,
    games_played        INT            NOT NULL DEFAULT 0,
    points_earned       INT            NOT NULL DEFAULT 0,
    apple_user_id       TEXT                    DEFAULT NULL,
    apple_refresh_token TEXT                    DEFAULT NULL,
    apple_access_token  TEXT                    DEFAULT NULL,
    primary key (id)
);

CREATE UNIQUE INDEX unique_apple_user_id
    ON profiles
        (apple_user_id)
    WHERE apple_user_id IS NOT NULL;