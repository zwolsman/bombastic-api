-- Custom types are a pain to set-up with R2DBC
--
-- CREATE TYPE event AS ENUM (
--     'NEW_GAME',
--     'POINTS',
--     'BOMB',
--     'CASH_OUT'
--     );

CREATE TABLE events
(
    id        BIGINT    not null DEFAULT id_generator(),
    timestamp TIMESTAMP not null DEFAULT now(),
    type      TEXT      not null,
    metadata  jsonb,
    primary key (id)
);
