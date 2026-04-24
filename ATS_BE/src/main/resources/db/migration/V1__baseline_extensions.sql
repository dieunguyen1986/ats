-- Enable pgcrypto for gen_random_uuid() if the database version is < 13
-- For PG13+ gen_random_uuid() is built-in, but this ensures backwards compatibility.
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
