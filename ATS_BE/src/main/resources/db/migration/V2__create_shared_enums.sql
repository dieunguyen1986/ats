-- ==============================================================================
-- V2: Enable PostgreSQL extensions required by the ATS application.
-- This is the baseline migration — matches ats_db production state.
-- ==============================================================================
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
