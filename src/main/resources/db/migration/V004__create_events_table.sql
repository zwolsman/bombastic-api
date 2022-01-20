CREATE TYPE event AS ENUM (
    'NEW_GAME',
    'POINTS',
    'BOMB',
    'CASH_OUT'
    );

CREATE TABLE events
(
    id         BIGINT    not null DEFAULT id_generator(),
    game_id    BIGINT    not null references games (id),
    timestamp  TIMESTAMP not null DEFAULT now(),
    type       event     not null,
    parameters jsonb,
    primary key (id)
);
