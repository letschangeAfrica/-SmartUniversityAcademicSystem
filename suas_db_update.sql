-- ============================================================
--  SUAS – Database Update v2 (run this after suas_db.sql)
-- ============================================================
USE suas_db;

-- ── Extra courses ────────────────────────────────────────────
INSERT INTO courses (code, name, lecturer_id) VALUES
  ('CS101', 'Introduction to Programming',  2),
  ('DB201', 'Database Systems',             2),
  ('SE301', 'Software Engineering',         2),
  ('DS401', 'Data Structures & Algorithms', 2)
ON DUPLICATE KEY UPDATE code = code;

-- ── Enrol student1 in all courses with grades ────────────────
INSERT INTO enrolments (student_id, course_id, grade)
SELECT 3, id, grade FROM (
  SELECT id, 78.5 AS grade FROM courses WHERE code = 'CS101'
  UNION ALL
  SELECT id, 85.0         FROM courses WHERE code = 'DB201'
  UNION ALL
  SELECT id, 91.0         FROM courses WHERE code = 'SE301'
  UNION ALL
  SELECT id, 72.0         FROM courses WHERE code = 'DS401'
) g
ON DUPLICATE KEY UPDATE grade = VALUES(grade);

-- ── Timetable table ──────────────────────────────────────────
CREATE TABLE IF NOT EXISTS timetable (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    course_id   INT NOT NULL,
    day_of_week ENUM('Monday','Tuesday','Wednesday','Thursday','Friday') NOT NULL,
    start_time  TIME NOT NULL,
    end_time    TIME NOT NULL,
    venue       VARCHAR(60) NOT NULL DEFAULT 'TBA',
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- ── Timetable seed data ──────────────────────────────────────
INSERT INTO timetable (course_id, day_of_week, start_time, end_time, venue)
SELECT id, 'Monday',    '08:00', '10:00', 'Lab A101' FROM courses WHERE code = 'CS101'
UNION ALL
SELECT id, 'Wednesday', '10:00', '12:00', 'Lab A101' FROM courses WHERE code = 'CS101'
UNION ALL
SELECT id, 'Tuesday',   '08:00', '10:00', 'Room B203' FROM courses WHERE code = 'DB201'
UNION ALL
SELECT id, 'Thursday',  '14:00', '16:00', 'Room B203' FROM courses WHERE code = 'DB201'
UNION ALL
SELECT id, 'Monday',    '14:00', '16:00', 'Hall C01'  FROM courses WHERE code = 'SE301'
UNION ALL
SELECT id, 'Friday',    '08:00', '10:00', 'Hall C01'  FROM courses WHERE code = 'SE301'
UNION ALL
SELECT id, 'Wednesday', '14:00', '16:00', 'Lab A102'  FROM courses WHERE code = 'DS401'
UNION ALL
SELECT id, 'Friday',    '10:00', '12:00', 'Lab A102'  FROM courses WHERE code = 'DS401';



