-- ============================================================
--  SUAS – Database Update v3 (Timetable Generation Module)
-- ============================================================
USE suas_db;

-- ── Rooms ────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS rooms (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(60) NOT NULL UNIQUE,
    capacity INT NOT NULL DEFAULT 30
);

INSERT INTO rooms (name, capacity) VALUES
    ('Lab A101',  30), ('Lab A102',  30),
    ('Room B203', 40), ('Room B204', 40),
    ('Hall C01',  80), ('Hall C02',  80),
    ('Room D105', 35)
ON DUPLICATE KEY UPDATE name = name;

-- ── Semesters ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS semesters (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(60) NOT NULL UNIQUE,
    start_date DATE,
    end_date   DATE,
    active     BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO semesters (name, start_date, end_date, active) VALUES
    ('Semester 1 - 2026', '2026-02-03', '2026-06-30', TRUE),
    ('Semester 2 - 2026', '2026-07-14', '2026-11-30', FALSE)
ON DUPLICATE KEY UPDATE name = name;

-- ── Update timetable table ────────────────────────────────────
ALTER TABLE timetable
    ADD COLUMN room_id        INT     NULL AFTER venue,
    ADD COLUMN semester_id    INT     NULL AFTER room_id,
    ADD COLUMN auto_generated BOOLEAN NOT NULL DEFAULT FALSE AFTER semester_id,
    ADD CONSTRAINT fk_tt_room     FOREIGN KEY (room_id)     REFERENCES rooms(id)     ON DELETE SET NULL,
    ADD CONSTRAINT fk_tt_semester FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE SET NULL;

-- Link existing seed data to rooms and semester 1
UPDATE timetable t
JOIN rooms r ON r.name = t.venue
JOIN semesters s ON s.name = 'Semester 1 - 2026'
SET t.room_id = r.id, t.semester_id = s.id, t.auto_generated = FALSE;
