-- ==============================================================================
-- V3: Create all core ATS tables.
-- Matches physical schema in ats_db (PostgreSQL 15+).
-- All tables use BIGINT GENERATED ALWAYS AS IDENTITY as PK (not UUID).
-- Audit columns: created_at, updated_at, created_by, updated_by, is_deleted, deleted_at
-- ==============================================================================

-- ============================================================
-- [1] Independent: departments
-- ============================================================
CREATE TABLE IF NOT EXISTS departments (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    parent_id        BIGINT        REFERENCES departments(id),
    department_name  VARCHAR(255)  NOT NULL,
    description      TEXT,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    is_deleted       BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at       TIMESTAMPTZ
);

-- ============================================================
-- [2] Independent: skills
-- ============================================================
CREATE TABLE IF NOT EXISTS skills (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    skill_name       VARCHAR(150)  NOT NULL,
    category         VARCHAR(150),
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    is_deleted       BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at       TIMESTAMPTZ
);

-- ============================================================
-- [3] Independent: pipeline_stages
-- ============================================================
CREATE TABLE IF NOT EXISTS pipeline_stages (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    stage_name       VARCHAR(100)  NOT NULL,
    stage_order      INTEGER       NOT NULL,
    color            VARCHAR(30),
    is_default       BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    is_deleted       BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at       TIMESTAMPTZ
);

-- ============================================================
-- [4] Independent: email_templates
-- ============================================================
CREATE TABLE IF NOT EXISTS email_templates (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    template_name    VARCHAR(255)  NOT NULL,
    type             VARCHAR(50)   NOT NULL,
    subject          VARCHAR(500)  NOT NULL,
    body_html        TEXT          NOT NULL,
    placeholders     TEXT,
    is_active        BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    is_deleted       BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at       TIMESTAMPTZ
);

-- ============================================================
-- [5] Independent: interview_templates
-- ============================================================
CREATE TABLE IF NOT EXISTS interview_templates (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    template_name    VARCHAR(255)  NOT NULL,
    file_path        VARCHAR(1000) NOT NULL,
    is_active        BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    is_deleted       BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at       TIMESTAMPTZ
);

-- ============================================================
-- [6] Independent: candidates
-- ============================================================
CREATE TABLE IF NOT EXISTS candidates (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name        VARCHAR(255)  NOT NULL,
    email            VARCHAR(255)  NOT NULL,
    password_hash    VARCHAR(255),
    auth_provider    VARCHAR(50),
    oauth_provider_id VARCHAR(255),
    status           VARCHAR(50)   NOT NULL DEFAULT 'ACTIVE',
    phone            VARCHAR(30),
    source           VARCHAR(150),
    utm_source       VARCHAR(150),
    utm_medium       VARCHAR(150),
    utm_campaign     VARCHAR(255),
    is_duplicate     BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    is_deleted       BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at       TIMESTAMPTZ
);

-- ============================================================
-- [7] Dependent: users (FK → departments)
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    department_id    BIGINT        REFERENCES departments(id),
    full_name        VARCHAR(255)  NOT NULL,
    email            VARCHAR(255)  NOT NULL,
    password_hash    VARCHAR(255),
    phone            VARCHAR(30),
    role             VARCHAR(50)   NOT NULL,   -- Enum: SYSTEM_ADMIN, RECRUITER, HIRING_MANAGER
    sso_provider_id  VARCHAR(255),
    status           VARCHAR(50)   NOT NULL,   -- Enum: ACTIVE, INACTIVE
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    is_deleted       BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at       TIMESTAMPTZ
);

-- ============================================================
-- [8] Dependent: sso_configurations
-- ============================================================
CREATE TABLE IF NOT EXISTS sso_configurations (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    provider_type    VARCHAR(50)   NOT NULL,   -- Enum: LDAP, GOOGLE, AZURE_AD
    ldap_url         VARCHAR(500),
    base_dn          VARCHAR(500),
    bind_user        VARCHAR(255),
    is_active        BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    is_deleted       BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at       TIMESTAMPTZ
);

-- ============================================================
-- [9] Dependent: jobs (FK → departments, users)
-- ============================================================
CREATE TABLE IF NOT EXISTS jobs (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    department_id    BIGINT        NOT NULL REFERENCES departments(id),
    recruiter_id     BIGINT        NOT NULL REFERENCES users(id),
    title            VARCHAR(500)  NOT NULL,
    description      TEXT          NOT NULL,
    location         VARCHAR(500),
    salary_min       NUMERIC(15,2),
    salary_max       NUMERIC(15,2),
    status           VARCHAR(50)   NOT NULL,   -- Enum: DRAFT, PUBLISHED, CLOSED
    utm_source       VARCHAR(150),
    utm_medium       VARCHAR(150),
    deadline         TIMESTAMPTZ,
    published_at     TIMESTAMPTZ,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    is_deleted       BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at       TIMESTAMPTZ
);

-- ============================================================
-- [10] Junction and dependent tables
-- ============================================================
CREATE TABLE IF NOT EXISTS candidate_skills (
    candidate_id  BIGINT NOT NULL REFERENCES candidates(id) ON DELETE CASCADE,
    skill_id      BIGINT NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    PRIMARY KEY (candidate_id, skill_id)
);

CREATE TABLE IF NOT EXISTS cvs (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    candidate_id     BIGINT        NOT NULL REFERENCES candidates(id) ON DELETE CASCADE,
    file_path        VARCHAR(1000) NOT NULL,
    file_type        VARCHAR(50)   NOT NULL,
    parsed_data      JSONB,
    parse_status     VARCHAR(50),
    uploaded_at      TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    is_deleted       BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at       TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS job_skills (
    job_id    BIGINT NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    skill_id  BIGINT NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    PRIMARY KEY (job_id, skill_id)
);

CREATE TABLE IF NOT EXISTS applications (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    job_id            BIGINT        NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    candidate_id      BIGINT        NOT NULL REFERENCES candidates(id) ON DELETE CASCADE,
    cv_id             BIGINT        REFERENCES cvs(id),
    department_id     BIGINT        REFERENCES departments(id),
    transferred_from  BIGINT        REFERENCES applications(id),
    pipeline_stage_id BIGINT        NOT NULL REFERENCES pipeline_stages(id),
    status            VARCHAR(50)   NOT NULL,
    applied_at        TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at        TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    is_deleted        BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at        TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS notifications (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id          BIGINT        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type             VARCHAR(50)   NOT NULL,
    title            VARCHAR(500)  NOT NULL,
    message          TEXT          NOT NULL,
    is_read          BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    is_deleted       BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at       TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS interviews (
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    application_id        BIGINT        NOT NULL REFERENCES applications(id),
    interview_template_id BIGINT        REFERENCES interview_templates(id),
    interview_type        VARCHAR(50)   NOT NULL,
    scheduled_at          TIMESTAMPTZ   NOT NULL,
    meeting_link          VARCHAR(1000),
    status                VARCHAR(50)   NOT NULL,
    duration_minutes      INTEGER,
    feedback              TEXT,
    result                VARCHAR(50),
    interviewer_name      VARCHAR(255),
    interviewer_email     VARCHAR(255),
    notes_file_path       VARCHAR(1000),
    created_at            TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by            VARCHAR(255),
    updated_by            VARCHAR(255),
    is_deleted            BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at            TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS interview_interviewers (
    interview_id  BIGINT NOT NULL REFERENCES interviews(id) ON DELETE CASCADE,
    user_id       BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (interview_id, user_id)
);

CREATE TABLE IF NOT EXISTS stage_transitions (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    application_id BIGINT        NOT NULL REFERENCES applications(id),
    from_stage_id  BIGINT        REFERENCES pipeline_stages(id),
    to_stage_id    BIGINT        NOT NULL REFERENCES pipeline_stages(id),
    notes          TEXT,
    moved_at       TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT        REFERENCES users(id),
    updated_by     VARCHAR(255),
    is_deleted     BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at     TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS evaluation_notes (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    application_id BIGINT        NOT NULL REFERENCES applications(id),
    content        TEXT          NOT NULL,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT        REFERENCES users(id),
    updated_by     VARCHAR(255),
    is_deleted     BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at     TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS email_logs (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    application_id    BIGINT        REFERENCES applications(id),
    email_template_id BIGINT        REFERENCES email_templates(id),
    recipient_email   VARCHAR(255)  NOT NULL,
    status            VARCHAR(50)   NOT NULL,
    sent_at           TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at        TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    is_deleted        BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at        TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS actionable_email_tokens (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    application_id BIGINT        NOT NULL REFERENCES applications(id),
    token_hash     VARCHAR(512)  NOT NULL,
    action_type    VARCHAR(50)   NOT NULL,
    expires_at     TIMESTAMPTZ   NOT NULL,
    used_at        TIMESTAMPTZ,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     VARCHAR(255),
    updated_by     VARCHAR(255),
    is_deleted     BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at     TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    entity_type VARCHAR(150)  NOT NULL,
    entity_id   BIGINT        NOT NULL,
    action      VARCHAR(50)   NOT NULL,
    changes     JSONB,
    ip_address  VARCHAR(50),
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    is_deleted  BOOLEAN       NOT NULL DEFAULT FALSE,
    deleted_at  TIMESTAMPTZ
);

-- ============================================================
-- Indexes
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_users_email       ON users(email)          WHERE is_deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_users_department  ON users(department_id);
CREATE INDEX IF NOT EXISTS idx_users_role        ON users(role);

CREATE INDEX IF NOT EXISTS idx_candidates_status       ON candidates(status);
CREATE INDEX IF NOT EXISTS idx_candidates_is_duplicate ON candidates(is_duplicate) WHERE is_duplicate = TRUE;

CREATE INDEX IF NOT EXISTS idx_jobs_status       ON jobs(status)          WHERE is_deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_jobs_department   ON jobs(department_id);
CREATE INDEX IF NOT EXISTS idx_jobs_recruiter    ON jobs(recruiter_id);
CREATE INDEX IF NOT EXISTS idx_jobs_published_at ON jobs(published_at DESC) WHERE status = 'PUBLISHED';

CREATE INDEX IF NOT EXISTS idx_applications_job       ON applications(job_id);
CREATE INDEX IF NOT EXISTS idx_applications_candidate ON applications(candidate_id);
CREATE INDEX IF NOT EXISTS idx_applications_stage     ON applications(pipeline_stage_id);
CREATE INDEX IF NOT EXISTS idx_applications_status    ON applications(status)    WHERE is_deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_cvs_candidate     ON cvs(candidate_id);
CREATE INDEX IF NOT EXISTS idx_cvs_parse_status  ON cvs(parse_status)     WHERE parse_status = 'PENDING';

CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications(user_id) WHERE is_read = FALSE;

CREATE INDEX IF NOT EXISTS idx_interviews_app      ON interviews(application_id);
CREATE INDEX IF NOT EXISTS idx_interviews_scheduled ON interviews(scheduled_at) WHERE is_deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_email_logs_app    ON email_logs(application_id);
CREATE INDEX IF NOT EXISTS idx_email_logs_status ON email_logs(status);

CREATE INDEX IF NOT EXISTS idx_audit_logs_entity     ON audit_logs(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON audit_logs(created_at DESC);

CREATE INDEX IF NOT EXISTS idx_stage_transitions_app ON stage_transitions(application_id);

-- Unique indexes (soft-delete aware)
CREATE UNIQUE INDEX IF NOT EXISTS uq_users_email          ON users(email)          WHERE is_deleted = FALSE;
CREATE UNIQUE INDEX IF NOT EXISTS uq_candidates_email     ON candidates(email)     WHERE is_deleted = FALSE;
CREATE UNIQUE INDEX IF NOT EXISTS uq_skills_name          ON skills(skill_name)    WHERE is_deleted = FALSE;
CREATE UNIQUE INDEX IF NOT EXISTS uq_email_templates_name ON email_templates(template_name) WHERE is_deleted = FALSE;
CREATE UNIQUE INDEX IF NOT EXISTS uq_interview_templates_name ON interview_templates(template_name) WHERE is_deleted = FALSE;
CREATE UNIQUE INDEX IF NOT EXISTS uq_actionable_tokens_hash   ON actionable_email_tokens(token_hash) WHERE is_deleted = FALSE;
CREATE UNIQUE INDEX IF NOT EXISTS uq_applications_job_candidate ON applications(job_id, candidate_id) WHERE is_deleted = FALSE;
