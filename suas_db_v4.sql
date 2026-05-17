-- ============================================================
--  SUAS – Database Update v4 (Course Registration Module)
-- ============================================================
USE suas_db;

-- ── Add credits and max capacity to courses ──────────────────
ALTER TABLE courses
    ADD COLUMN credits      INT NOT NULL DEFAULT 3 AFTER name,
    ADD COLUMN max_students INT NOT NULL DEFAULT 30 AFTER credits;

-- Set realistic values for existing seed courses
UPDATE courses SET credits = 4, max_students = 35 WHERE code = 'CS101';
UPDATE courses SET credits = 3, max_students = 30 WHERE code = 'DB201';
UPDATE courses SET credits = 3, max_students = 30 WHERE code = 'SE301';
UPDATE courses SET credits = 4, max_students = 25 WHERE code = 'DS401';
