/*
 ID generator
 */
CREATE SEQUENCE public.global_id_seq;
ALTER SEQUENCE public.global_id_seq OWNER TO postgres;

CREATE OR REPLACE FUNCTION public.id_generator()
    RETURNS bigint
    LANGUAGE 'plpgsql'
AS
$BODY$
DECLARE
    our_epoch  bigint := 1314220021721;
    seq_id     bigint;
    now_millis bigint;
    -- the id of this DB shard, must be set for each
    -- schema shard you have - you could pass this as a parameter too
    shard_id   int    := 1;
    result     bigint := 0;
BEGIN
    SELECT nextval('public.global_id_seq') % 1024 INTO seq_id;

    SELECT FLOOR(EXTRACT(EPOCH FROM clock_timestamp()) * 1000) INTO now_millis;
    result := (now_millis - our_epoch) << 23;
    result := result | (shard_id << 10);
    result := result | (seq_id);
    return result;
END;
$BODY$;

ALTER FUNCTION public.id_generator() OWNER TO postgres;

/*
 TABLE
 */
CREATE TABLE games
(
    id          BIGINT not null DEFAULT id_generator(),
    tiles       TEXT[],
    initial_bet INT,
    color_id    INT,
    secret      TEXT,
    cashed_out  BOOL            DEFAULT FALSE,
    primary key (id)
);