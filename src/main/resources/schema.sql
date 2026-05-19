-- ============================================================
--  SkillSphere — Complete PostgreSQL Schema
--  Compatible with: Spring Boot + Spring Data JPA
--  Naming convention: snake_case tables, BIGSERIAL PKs
-- ============================================================

-- ── Extensions ──────────────────────────────────────────────
CREATE EXTENSION IF NOT EXISTS "pgcrypto";   -- gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS "citext";     -- case-insensitive email

-- ============================================================
-- 0. COMMON: audit columns applied via triggers / @PrePersist
-- ============================================================
-- All tables include: created_at, updated_at, is_deleted

-- ============================================================
-- 1. USERS & ROLES
-- ============================================================

CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255)        NOT NULL,
    email       CITEXT UNIQUE       NOT NULL,
    ldap        VARCHAR(255) UNIQUE,
    password    VARCHAR(255),                      -- null for SSO/LDAP users
    is_active   BOOLEAN             NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ         NOT NULL DEFAULT NOW(),
    is_deleted  BOOLEAN             NOT NULL DEFAULT FALSE
);

-- Roles: ADMIN | INSTRUCTOR | LEARNER  (multi-role via join table)
CREATE TABLE user_roles (
    user_id     BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role        VARCHAR(50) NOT NULL CHECK (role IN ('ADMIN','INSTRUCTOR','LEARNER')),
    PRIMARY KEY (user_id, role)
);

CREATE TABLE instructors (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title               VARCHAR(255),
    bio                 TEXT,
    experience_years    INT DEFAULT 0,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    is_deleted          BOOLEAN     NOT NULL DEFAULT FALSE
);

-- ============================================================
-- 2. CATEGORIES
-- ============================================================

CREATE TABLE categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) UNIQUE NOT NULL,
    slug        VARCHAR(100) UNIQUE NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    is_deleted  BOOLEAN     NOT NULL DEFAULT FALSE
);

-- Seed data
INSERT INTO categories (name, slug) VALUES
  ('Schema',            'schema'),
  ('ETL',               'etl'),
  ('KETL',              'ketl'),
  ('Cybersecurity',     'cybersecurity'),
  ('Data Science',      'data-science'),
  ('Leadership',        'leadership'),
  ('Project Management','project-management'),
  ('Communication',     'communication'),
  ('Mandatory',         'mandatory');

-- ============================================================
-- 3. COURSES
-- ============================================================

CREATE TABLE courses (
    id                  BIGSERIAL PRIMARY KEY,
    slug                VARCHAR(255) UNIQUE NOT NULL,
    title               VARCHAR(255)        NOT NULL,
    tagline             VARCHAR(500),
    description         TEXT,
    thumbnail_url       VARCHAR(500),
    level               VARCHAR(50) NOT NULL
                            CHECK (level IN ('Beginner','Intermediate','Advanced','Mandatory')),
    category_id         BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    instructor_id       BIGINT REFERENCES instructors(id) ON DELETE SET NULL,
    duration_minutes    INT          DEFAULT 0,
    is_mandatory        BOOLEAN      NOT NULL DEFAULT FALSE,
    is_published        BOOLEAN      NOT NULL DEFAULT FALSE,
    enrolled_count      INT          NOT NULL DEFAULT 0,
    rating              NUMERIC(3,2) DEFAULT 0.0,
    review_count        INT          NOT NULL DEFAULT 0,
    certificate_enabled BOOLEAN      NOT NULL DEFAULT TRUE,
    -- Assessment weightage (1.0 = full, 0.5 = legacy)
    assessment_weightage NUMERIC(3,2) NOT NULL DEFAULT 1.0,
    last_updated_at     TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    is_deleted          BOOLEAN     NOT NULL DEFAULT FALSE
);

-- ============================================================
-- 4. COURSE SECTIONS & LESSONS
-- ============================================================

CREATE TABLE course_sections (
    id          BIGSERIAL PRIMARY KEY,
    course_id   BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    title       VARCHAR(255) NOT NULL,
    position    INT          NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    is_deleted  BOOLEAN     NOT NULL DEFAULT FALSE
);

CREATE TABLE lessons (
    id                  BIGSERIAL PRIMARY KEY,
    section_id          BIGINT NOT NULL REFERENCES course_sections(id) ON DELETE CASCADE,
    title               VARCHAR(255) NOT NULL,
    description         TEXT,
    type                VARCHAR(50)  NOT NULL
                            CHECK (type IN ('VIDEO','READING','QUIZ','ASSIGNMENT')),
    video_url           VARCHAR(500),
    content             TEXT,                          -- rich text / markdown for readings
    duration_seconds    INT          DEFAULT 0,        -- store seconds, display as mm:ss
    is_free             BOOLEAN      NOT NULL DEFAULT FALSE,
    is_published        BOOLEAN      NOT NULL DEFAULT FALSE,
    position            INT          NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    is_deleted          BOOLEAN     NOT NULL DEFAULT FALSE
);

-- ============================================================
-- 5. ENROLLMENTS
-- ============================================================

CREATE TABLE enrollments (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    course_id           BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    enrolled_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    progress_pct        INT         NOT NULL DEFAULT 0 CHECK (progress_pct BETWEEN 0 AND 100),
    completed           BOOLEAN     NOT NULL DEFAULT FALSE,
    completed_at        TIMESTAMPTZ,
    -- Wishlist / saved (not yet enrolled)
    is_wishlist         BOOLEAN     NOT NULL DEFAULT FALSE,
    -- Lead/PgM approval required for wishlist promotion
    wishlist_approved   BOOLEAN,
    approved_by         BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, course_id)
);

-- ============================================================
-- 6. LESSON PROGRESS
-- ============================================================

CREATE TABLE lesson_progress (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id)   ON DELETE CASCADE,
    lesson_id           BIGINT NOT NULL REFERENCES lessons(id) ON DELETE CASCADE,
    completed           BOOLEAN     NOT NULL DEFAULT FALSE,
    completed_at        TIMESTAMPTZ,
    watch_time_seconds  INT         NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, lesson_id)
);

-- ============================================================
-- 7. ASSESSMENTS (quizzes tied to a course/module)
-- ============================================================

CREATE TABLE assessments (
    id                  BIGSERIAL PRIMARY KEY,
    course_id           BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    lesson_id           BIGINT REFERENCES lessons(id) ON DELETE SET NULL,  -- optional: linked to a lesson
    title               VARCHAR(255) NOT NULL,
    total_points        INT          NOT NULL DEFAULT 100,
    passing_pct         INT          NOT NULL DEFAULT 60,    -- pass threshold %
    max_attempts        INT          NOT NULL DEFAULT 3,     -- SkillSphere rule: 3 for quiz, 5 for assessment
    weightage           NUMERIC(3,2) NOT NULL DEFAULT 1.0,   -- 0.5 for legacy processes
    is_final            BOOLEAN      NOT NULL DEFAULT FALSE, -- is this the final assignment?
    due_date            TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    is_deleted          BOOLEAN     NOT NULL DEFAULT FALSE
);

CREATE TABLE assessment_questions (
    id              BIGSERIAL PRIMARY KEY,
    assessment_id   BIGINT NOT NULL REFERENCES assessments(id) ON DELETE CASCADE,
    question_text   TEXT         NOT NULL,
    position        INT          NOT NULL DEFAULT 0,
    points          INT          NOT NULL DEFAULT 1,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE question_options (
    id              BIGSERIAL PRIMARY KEY,
    question_id     BIGINT NOT NULL REFERENCES assessment_questions(id) ON DELETE CASCADE,
    option_text     TEXT    NOT NULL,
    is_correct      BOOLEAN NOT NULL DEFAULT FALSE,
    position        INT     NOT NULL DEFAULT 0
);

-- ============================================================
-- 8. ASSESSMENT ATTEMPTS
-- ============================================================

CREATE TABLE assessment_attempts (
    id                  BIGSERIAL PRIMARY KEY,
    assessment_id       BIGINT NOT NULL REFERENCES assessments(id)  ON DELETE CASCADE,
    user_id             BIGINT NOT NULL REFERENCES users(id)         ON DELETE CASCADE,
    attempt_number      INT          NOT NULL,
    score               NUMERIC(5,2) NOT NULL DEFAULT 0,    -- raw score
    score_pct           NUMERIC(5,2) NOT NULL DEFAULT 0,    -- percentage
    weighted_points     NUMERIC(7,2) NOT NULL DEFAULT 0,    -- score * weightage
    passed              BOOLEAN      NOT NULL DEFAULT FALSE,
    started_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    submitted_at        TIMESTAMPTZ,
    -- Lead notification: triggered when attempt_number >= max_attempts and not passed
    lead_notified       BOOLEAN      NOT NULL DEFAULT FALSE,
    lead_notified_at    TIMESTAMPTZ,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE (assessment_id, user_id, attempt_number)
);

CREATE TABLE attempt_answers (
    id              BIGSERIAL PRIMARY KEY,
    attempt_id      BIGINT NOT NULL REFERENCES assessment_attempts(id)   ON DELETE CASCADE,
    question_id     BIGINT NOT NULL REFERENCES assessment_questions(id)  ON DELETE CASCADE,
    selected_option BIGINT REFERENCES question_options(id) ON DELETE SET NULL,
    is_correct      BOOLEAN NOT NULL DEFAULT FALSE,
    points_earned   NUMERIC(5,2) NOT NULL DEFAULT 0
);

-- ============================================================
-- 9. USER POINTS & LEADERBOARD
-- ============================================================

CREATE TABLE user_points (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    source_type     VARCHAR(50) NOT NULL
                        CHECK (source_type IN ('ASSESSMENT','BADGE','COURSE_COMPLETION','BONUS')),
    source_id       BIGINT,                         -- FK to assessment_attempts or badge etc.
    points_raw      NUMERIC(7,2) NOT NULL DEFAULT 0,
    weightage       NUMERIC(3,2) NOT NULL DEFAULT 1.0,
    points_weighted NUMERIC(7,2) NOT NULL DEFAULT 0, -- points_raw * weightage
    description     VARCHAR(255),
    earned_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Materialized view for fast leaderboard queries (refresh periodically)
CREATE MATERIALIZED VIEW leaderboard AS
SELECT
    u.id            AS user_id,
    u.name,
    u.email,
    COALESCE(SUM(p.points_weighted), 0)  AS total_points,
    RANK() OVER (ORDER BY COALESCE(SUM(p.points_weighted), 0) DESC) AS rank
FROM users u
LEFT JOIN user_points p ON p.user_id = u.id
WHERE u.is_deleted = FALSE
GROUP BY u.id, u.name, u.email;

CREATE UNIQUE INDEX ON leaderboard (user_id);

-- ============================================================
-- 10. BADGES & CERTIFICATES
-- ============================================================

CREATE TABLE badges (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) UNIQUE NOT NULL,
    tier        VARCHAR(50) NOT NULL CHECK (tier IN ('Bronze','Silver','Gold')),
    description TEXT,
    icon_url    VARCHAR(500),
    -- Which level of course triggers this badge
    course_level VARCHAR(50) CHECK (course_level IN ('Beginner','Intermediate','Advanced')),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Seed badge tiers per the spec:
-- Bronze = Beginner, Silver = Intermediate, Gold = Advanced
INSERT INTO badges (name, tier, description, course_level) VALUES
  ('Beginner Achiever',     'Bronze', 'Completed a beginner-level course',       'Beginner'),
  ('Intermediate Achiever', 'Silver', 'Completed an intermediate-level course',  'Intermediate'),
  ('Advanced Achiever',     'Gold',   'Completed an advanced-level course',       'Advanced');

CREATE TABLE user_badges (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    badge_id    BIGINT NOT NULL REFERENCES badges(id) ON DELETE CASCADE,
    course_id   BIGINT REFERENCES courses(id) ON DELETE SET NULL,
    earned_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, badge_id, course_id)
);

CREATE TABLE certificates (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
    course_id       BIGINT NOT NULL REFERENCES courses(id)  ON DELETE CASCADE,
    certificate_url VARCHAR(500),
    issued_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, course_id)
);

-- ============================================================
-- 11. REVIEWS
-- ============================================================

CREATE TABLE reviews (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
    course_id   BIGINT NOT NULL REFERENCES courses(id)  ON DELETE CASCADE,
    rating      INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment     TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, course_id)
);

-- Trigger: keep courses.rating in sync after every insert/update/delete on reviews
CREATE OR REPLACE FUNCTION refresh_course_rating()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    UPDATE courses SET
        rating       = (SELECT ROUND(AVG(rating)::NUMERIC, 2) FROM reviews WHERE course_id = COALESCE(NEW.course_id, OLD.course_id)),
        review_count = (SELECT COUNT(*) FROM reviews WHERE course_id = COALESCE(NEW.course_id, OLD.course_id))
    WHERE id = COALESCE(NEW.course_id, OLD.course_id);
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_course_rating
AFTER INSERT OR UPDATE OR DELETE ON reviews
FOR EACH ROW EXECUTE FUNCTION refresh_course_rating();

-- ============================================================
-- 12. NOTIFICATIONS (in-app + lead alerts)
-- ============================================================

CREATE TABLE notifications (
    id              BIGSERIAL PRIMARY KEY,
    recipient_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    sender_id       BIGINT REFERENCES users(id) ON DELETE SET NULL,   -- null = system
    type            VARCHAR(80) NOT NULL
                        CHECK (type IN (
                          'ASSESSMENT_REMINDER',
                          'ASSESSMENT_OVERDUE',
                          'ATTEMPT_LIMIT_BREACH',   -- lead notification
                          'LEAD_NOTIFIED',           -- learner sees this
                          'NEW_CONTENT',
                          'BADGE_EARNED',
                          'CERTIFICATE_READY',
                          'ENROLLMENT_APPROVED'
                        )),
    title           VARCHAR(255) NOT NULL,
    body            TEXT,
    reference_type  VARCHAR(80),    -- e.g. 'ASSESSMENT'
    reference_id    BIGINT,         -- e.g. assessment_attempts.id
    is_read         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ============================================================
-- 13. DOUBT SESSIONS (live Q&A per the learning path spec)
-- ============================================================

CREATE TABLE doubt_sessions (
    id              BIGSERIAL PRIMARY KEY,
    course_id       BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    title           VARCHAR(255) NOT NULL,
    session_type    VARCHAR(50) NOT NULL CHECK (session_type IN ('LIVE','RECORDED')),
    scheduled_at    TIMESTAMPTZ,
    duration_minutes INT,
    meeting_url     VARCHAR(500),
    recording_url   VARCHAR(500),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE doubt_session_attendees (
    session_id  BIGINT NOT NULL REFERENCES doubt_sessions(id) ON DELETE CASCADE,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    attended    BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (session_id, user_id)
);

-- ============================================================
-- 14. COMMUNITY FORUM
-- ============================================================

CREATE TABLE forum_threads (
    id          BIGSERIAL PRIMARY KEY,
    course_id   BIGINT REFERENCES courses(id) ON DELETE SET NULL,
    category_id BIGINT REFERENCES categories(id) ON DELETE SET NULL,
    author_id   BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title       VARCHAR(500) NOT NULL,
    body        TEXT         NOT NULL,
    reply_count INT          NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    is_deleted  BOOLEAN     NOT NULL DEFAULT FALSE
);

CREATE TABLE forum_replies (
    id          BIGSERIAL PRIMARY KEY,
    thread_id   BIGINT NOT NULL REFERENCES forum_threads(id) ON DELETE CASCADE,
    author_id   BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    body        TEXT    NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    is_deleted  BOOLEAN     NOT NULL DEFAULT FALSE
);

-- Keep reply_count in sync
CREATE OR REPLACE FUNCTION refresh_thread_reply_count()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    UPDATE forum_threads SET
        reply_count = (SELECT COUNT(*) FROM forum_replies WHERE thread_id = COALESCE(NEW.thread_id, OLD.thread_id) AND is_deleted = FALSE)
    WHERE id = COALESCE(NEW.thread_id, OLD.thread_id);
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_thread_replies
AFTER INSERT OR DELETE ON forum_replies
FOR EACH ROW EXECUTE FUNCTION refresh_thread_reply_count();

-- ============================================================
-- 15. INDEXES for performance
-- ============================================================

-- Enrollments
CREATE INDEX idx_enrollments_user    ON enrollments(user_id);
CREATE INDEX idx_enrollments_course  ON enrollments(course_id);

-- Lesson progress
CREATE INDEX idx_lp_user             ON lesson_progress(user_id);
CREATE INDEX idx_lp_lesson           ON lesson_progress(lesson_id);

-- Assessment attempts
CREATE INDEX idx_attempts_assessment ON assessment_attempts(assessment_id);
CREATE INDEX idx_attempts_user       ON assessment_attempts(user_id);
CREATE INDEX idx_attempts_notified   ON assessment_attempts(lead_notified) WHERE lead_notified = FALSE;

-- Points
CREATE INDEX idx_points_user         ON user_points(user_id);

-- Notifications
CREATE INDEX idx_notif_recipient     ON notifications(recipient_id, is_read);

-- Courses
CREATE INDEX idx_courses_category    ON courses(category_id);
CREATE INDEX idx_courses_instructor  ON courses(instructor_id);
CREATE INDEX idx_courses_published   ON courses(is_published) WHERE is_published = TRUE;

-- Lessons
CREATE INDEX idx_lessons_section     ON lessons(section_id);
CREATE INDEX idx_sections_course     ON course_sections(course_id);

-- Forum
CREATE INDEX idx_forum_threads_course ON forum_threads(course_id);

-- ============================================================
-- 16. updated_at auto-trigger (applies to all tables)
-- ============================================================

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$;

-- Apply to each table that has updated_at
DO $do$ DECLARE tbl TEXT;
BEGIN
  FOR tbl IN SELECT unnest(ARRAY[
    'users','instructors','categories','courses','course_sections',
    'lessons','enrollments','lesson_progress','assessments',
    'assessment_questions','assessment_attempts','reviews',
    'notifications','doubt_sessions','forum_threads','forum_replies'
  ]) LOOP
    EXECUTE FORMAT(
      'CREATE TRIGGER trg_updated_at BEFORE UPDATE ON %I
       FOR EACH ROW EXECUTE FUNCTION set_updated_at()', tbl
    );
  END LOOP;
END $do$;

-- ============================================================
-- END OF SCHEMA
-- ============================================================
