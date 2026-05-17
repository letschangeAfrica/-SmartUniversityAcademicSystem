-- ═══════════════════════════════════════════════════════════════════════════════
--  SUAS  –  Demo seed data
--  60 students · 10 lecturers · 10 courses · enrolments · attendance · evals
--  Run AFTER suas_db_v1 → suas_db_v6
--  Safe to re-run: uses INSERT IGNORE throughout
-- ═══════════════════════════════════════════════════════════════════════════════
USE suas_db;

-- ─── 1. LECTURERS (10) ───────────────────────────────────────────────────────
--  Login: username  /  lecturer123
INSERT IGNORE INTO users (username, full_name, password_hash, role, is_active) VALUES
('lecturer01','Dr. James Anderson',   SHA2('lecturer123',256),'LECTURER',TRUE),
('lecturer02','Dr. Sarah Williams',   SHA2('lecturer123',256),'LECTURER',TRUE),
('lecturer03','Dr. Michael Brown',    SHA2('lecturer123',256),'LECTURER',TRUE),
('lecturer04','Dr. Amina Diallo',     SHA2('lecturer123',256),'LECTURER',TRUE),
('lecturer05','Dr. Robert Taylor',    SHA2('lecturer123',256),'LECTURER',TRUE),
('lecturer06','Dr. Jessica Wilson',   SHA2('lecturer123',256),'LECTURER',TRUE),
('lecturer07','Dr. David Martinez',   SHA2('lecturer123',256),'LECTURER',TRUE),
('lecturer08','Dr. Linda Johnson',    SHA2('lecturer123',256),'LECTURER',TRUE),
('lecturer09','Dr. Emmanuel Okafor',  SHA2('lecturer123',256),'LECTURER',TRUE),
('lecturer10','Dr. Maria Santos',     SHA2('lecturer123',256),'LECTURER',TRUE);

-- ─── 2. ASSIGN LECTURERS TO EXISTING COURSES + RAISE CAPACITY ────────────────
UPDATE courses SET max_students = 40;
UPDATE courses SET lecturer_id = (SELECT id FROM users WHERE username = 'lecturer01') WHERE code = 'CS101';
UPDATE courses SET lecturer_id = (SELECT id FROM users WHERE username = 'lecturer02') WHERE code = 'DB201';
UPDATE courses SET lecturer_id = (SELECT id FROM users WHERE username = 'lecturer03') WHERE code = 'SE301';
UPDATE courses SET lecturer_id = (SELECT id FROM users WHERE username = 'lecturer04') WHERE code = 'DS401';

-- ─── 3. NEW COURSES (6) ──────────────────────────────────────────────────────
INSERT IGNORE INTO courses (code, name, lecturer_id, credits, max_students)
SELECT 'WEB501','Web Development',             id,3,40 FROM users WHERE username='lecturer05';
INSERT IGNORE INTO courses (code, name, lecturer_id, credits, max_students)
SELECT 'AI601','Artificial Intelligence',      id,3,35 FROM users WHERE username='lecturer06';
INSERT IGNORE INTO courses (code, name, lecturer_id, credits, max_students)
SELECT 'NET701','Computer Networks',           id,3,40 FROM users WHERE username='lecturer07';
INSERT IGNORE INTO courses (code, name, lecturer_id, credits, max_students)
SELECT 'OS801','Operating Systems',            id,3,40 FROM users WHERE username='lecturer08';
INSERT IGNORE INTO courses (code, name, lecturer_id, credits, max_students)
SELECT 'MATH101','Mathematics for Computing',  id,4,45 FROM users WHERE username='lecturer09';
INSERT IGNORE INTO courses (code, name, lecturer_id, credits, max_students)
SELECT 'STAT201','Statistics and Probability', id,3,40 FROM users WHERE username='lecturer10';

-- ─── 4. STUDENTS (60) ────────────────────────────────────────────────────────
--  Login: username  /  student123
INSERT IGNORE INTO users (username, full_name, password_hash, role, is_active) VALUES
('student01','Alice Johnson',       SHA2('student123',256),'STUDENT',TRUE),
('student02','Brian Smith',         SHA2('student123',256),'STUDENT',TRUE),
('student03','Catherine Mbeki',     SHA2('student123',256),'STUDENT',TRUE),
('student04','Daniel Mensah',       SHA2('student123',256),'STUDENT',TRUE),
('student05','Elena Rodriguez',     SHA2('student123',256),'STUDENT',TRUE),
('student06','Frank Owusu',         SHA2('student123',256),'STUDENT',TRUE),
('student07','Grace Kim',           SHA2('student123',256),'STUDENT',TRUE),
('student08','Henry Muller',        SHA2('student123',256),'STUDENT',TRUE),
('student09','Isabelle Traore',     SHA2('student123',256),'STUDENT',TRUE),
('student10','James Chen',          SHA2('student123',256),'STUDENT',TRUE),
('student11','Karen Nwosu',         SHA2('student123',256),'STUDENT',TRUE),
('student12','Liam Brien',          SHA2('student123',256),'STUDENT',TRUE),
('student13','Mia Fernandez',       SHA2('student123',256),'STUDENT',TRUE),
('student14','Noah Asante',         SHA2('student123',256),'STUDENT',TRUE),
('student15','Olivia Petrov',       SHA2('student123',256),'STUDENT',TRUE),
('student16','Patrick Kamau',       SHA2('student123',256),'STUDENT',TRUE),
('student17','Quinn Zhang',         SHA2('student123',256),'STUDENT',TRUE),
('student18','Rachel Adeyemi',      SHA2('student123',256),'STUDENT',TRUE),
('student19','Samuel Wilson',       SHA2('student123',256),'STUDENT',TRUE),
('student20','Tanya Mokoena',       SHA2('student123',256),'STUDENT',TRUE),
('student21','Usman Ibrahim',       SHA2('student123',256),'STUDENT',TRUE),
('student22','Victoria Santos',     SHA2('student123',256),'STUDENT',TRUE),
('student23','William Nakamura',    SHA2('student123',256),'STUDENT',TRUE),
('student24','Xena Osei',           SHA2('student123',256),'STUDENT',TRUE),
('student25','Yusuf Hassan',        SHA2('student123',256),'STUDENT',TRUE),
('student26','Zoe Anderson',        SHA2('student123',256),'STUDENT',TRUE),
('student27','Aaron Mbenga',        SHA2('student123',256),'STUDENT',TRUE),
('student28','Beatrice Sow',        SHA2('student123',256),'STUDENT',TRUE),
('student29','Carlos Hernandez',    SHA2('student123',256),'STUDENT',TRUE),
('student30','Diana Ngozi',         SHA2('student123',256),'STUDENT',TRUE),
('student31','Edward Patel',        SHA2('student123',256),'STUDENT',TRUE),
('student32','Florence Diop',       SHA2('student123',256),'STUDENT',TRUE),
('student33','George Kwame',        SHA2('student123',256),'STUDENT',TRUE),
('student34','Hannah Berg',         SHA2('student123',256),'STUDENT',TRUE),
('student35','Ibrahim Coulibaly',   SHA2('student123',256),'STUDENT',TRUE),
('student36','Julia Moreau',        SHA2('student123',256),'STUDENT',TRUE),
('student37','Kevin Okonkwo',       SHA2('student123',256),'STUDENT',TRUE),
('student38','Layla Rashid',        SHA2('student123',256),'STUDENT',TRUE),
('student39','Marcus Thompson',     SHA2('student123',256),'STUDENT',TRUE),
('student40','Natalie Bergmann',    SHA2('student123',256),'STUDENT',TRUE),
('student41','Omar Balde',          SHA2('student123',256),'STUDENT',TRUE),
('student42','Priya Sharma',        SHA2('student123',256),'STUDENT',TRUE),
('student43','Quentin Leblanc',     SHA2('student123',256),'STUDENT',TRUE),
('student44','Rita Eze',            SHA2('student123',256),'STUDENT',TRUE),
('student45','Stefan Nikolov',      SHA2('student123',256),'STUDENT',TRUE),
('student46','Tunde Adeleke',       SHA2('student123',256),'STUDENT',TRUE),
('student47','Uma Krishnan',        SHA2('student123',256),'STUDENT',TRUE),
('student48','Valeria Castro',      SHA2('student123',256),'STUDENT',TRUE),
('student49','Wesley Amponsah',     SHA2('student123',256),'STUDENT',TRUE),
('student50','Xiomara Torres',      SHA2('student123',256),'STUDENT',TRUE),
('student51','Yemi Adebayo',        SHA2('student123',256),'STUDENT',TRUE),
('student52','Zara Nkosi',          SHA2('student123',256),'STUDENT',TRUE),
('student53','Alex Fontaine',       SHA2('student123',256),'STUDENT',TRUE),
('student54','Bianca Mensah',       SHA2('student123',256),'STUDENT',TRUE),
('student55','Chloe Dupont',        SHA2('student123',256),'STUDENT',TRUE),
('student56','Dayo Afolabi',        SHA2('student123',256),'STUDENT',TRUE),
('student57','Eve Hoffman',         SHA2('student123',256),'STUDENT',TRUE),
('student58','Felix Nkemdirim',     SHA2('student123',256),'STUDENT',TRUE),
('student59','Gina Laurent',        SHA2('student123',256),'STUDENT',TRUE),
('student60','Hamid Bouchard',      SHA2('student123',256),'STUDENT',TRUE);

-- ─── 5. ENROLMENTS WITH GRADES ───────────────────────────────────────────────
--  grade formula: 45 + MOD(student_id*11 + course_id*17, 51)  →  range 45–95
--  Group A (01-10): CS101, DB201, SE301
INSERT IGNORE INTO enrolments (student_id, course_id, grade)
SELECT u.id, c.id, (45 + MOD(u.id*11 + c.id*17, 51))
FROM users u JOIN courses c ON c.code IN ('CS101','DB201','SE301')
WHERE u.username IN ('student01','student02','student03','student04','student05',
                     'student06','student07','student08','student09','student10');
--  Group B (11-20): DS401, WEB501, AI601
INSERT IGNORE INTO enrolments (student_id, course_id, grade)
SELECT u.id, c.id, (45 + MOD(u.id*11 + c.id*17, 51))
FROM users u JOIN courses c ON c.code IN ('DS401','WEB501','AI601')
WHERE u.username IN ('student11','student12','student13','student14','student15',
                     'student16','student17','student18','student19','student20');
--  Group C (21-30): NET701, OS801, MATH101
INSERT IGNORE INTO enrolments (student_id, course_id, grade)
SELECT u.id, c.id, (45 + MOD(u.id*11 + c.id*17, 51))
FROM users u JOIN courses c ON c.code IN ('NET701','OS801','MATH101')
WHERE u.username IN ('student21','student22','student23','student24','student25',
                     'student26','student27','student28','student29','student30');
--  Group D (31-40): STAT201, CS101, DB201
INSERT IGNORE INTO enrolments (student_id, course_id, grade)
SELECT u.id, c.id, (45 + MOD(u.id*11 + c.id*17, 51))
FROM users u JOIN courses c ON c.code IN ('STAT201','CS101','DB201')
WHERE u.username IN ('student31','student32','student33','student34','student35',
                     'student36','student37','student38','student39','student40');
--  Group E (41-50): SE301, DS401, WEB501
INSERT IGNORE INTO enrolments (student_id, course_id, grade)
SELECT u.id, c.id, (45 + MOD(u.id*11 + c.id*17, 51))
FROM users u JOIN courses c ON c.code IN ('SE301','DS401','WEB501')
WHERE u.username IN ('student41','student42','student43','student44','student45',
                     'student46','student47','student48','student49','student50');
--  Group F (51-60): AI601, NET701, OS801
INSERT IGNORE INTO enrolments (student_id, course_id, grade)
SELECT u.id, c.id, (45 + MOD(u.id*11 + c.id*17, 51))
FROM users u JOIN courses c ON c.code IN ('AI601','NET701','OS801')
WHERE u.username IN ('student51','student52','student53','student54','student55',
                     'student56','student57','student58','student59','student60');

-- ─── 6. CLASS SESSIONS (4 per course × 4 courses = 16 sessions) ──────────────
-- CS101
INSERT IGNORE INTO class_sessions (course_id,session_date,topic,created_by)
SELECT c.id,'2024-09-09','Introduction and Course Overview',u.id FROM courses c,users u WHERE c.code='CS101' AND u.username='lecturer01';
INSERT IGNORE INTO class_sessions (course_id,session_date,topic,created_by)
SELECT c.id,'2024-09-16','Programming Fundamentals',u.id FROM courses c,users u WHERE c.code='CS101' AND u.username='lecturer01';
INSERT IGNORE INTO class_sessions (course_id,session_date,topic,created_by)
SELECT c.id,'2024-09-23','Data Types and Control Flow',u.id FROM courses c,users u WHERE c.code='CS101' AND u.username='lecturer01';
INSERT IGNORE INTO class_sessions (course_id,session_date,topic,created_by)
SELECT c.id,'2024-09-30','Functions and Modular Design',u.id FROM courses c,users u WHERE c.code='CS101' AND u.username='lecturer01';
-- DB201
INSERT IGNORE INTO class_sessions (course_id,session_date,topic,created_by)
SELECT c.id,'2024-09-10','Relational Model and SQL Basics',u.id FROM courses c,users u WHERE c.code='DB201' AND u.username='lecturer02';
INSERT IGNORE INTO class_sessions (course_id,session_date,topic,created_by)
SELECT c.id,'2024-09-17','Joins and Subqueries',u.id FROM courses c,users u WHERE c.code='DB201' AND u.username='lecturer02';
INSERT IGNORE INTO class_sessions (course_id,session_date,topic,created_by)
SELECT c.id,'2024-09-24','Normalization',u.id FROM courses c,users u WHERE c.code='DB201' AND u.username='lecturer02';
INSERT IGNORE INTO class_sessions (course_id,session_date,topic,created_by)
SELECT c.id,'2024-10-01','Transactions and Concurrency',u.id FROM courses c,users u WHERE c.code='DB201' AND u.username='lecturer02';
-- SE301
INSERT IGNORE INTO class_sessions (course_id,session_date,topic,created_by)
SELECT c.id,'2024-09-11','Software Development Life Cycle',u.id FROM courses c,users u WHERE c.code='SE301' AND u.username='lecturer03';
INSERT IGNORE INTO class_sessions (course_id,session_date,topic,created_by)
SELECT c.id,'2024-09-18','Requirements Engineering',u.id FROM courses c,users u WHERE c.code='SE301' AND u.username='lecturer03';
INSERT IGNORE INTO class_sessions (course_id,session_date,topic,created_by)
SELECT c.id,'2024-09-25','Design Patterns',u.id FROM courses c,users u WHERE c.code='SE301' AND u.username='lecturer03';
INSERT IGNORE INTO class_sessions (course_id,session_date,topic,created_by)
SELECT c.id,'2024-10-02','Testing and Quality Assurance',u.id FROM courses c,users u WHERE c.code='SE301' AND u.username='lecturer03';
-- DS401
INSERT IGNORE INTO class_sessions (course_id,session_date,topic,created_by)
SELECT c.id,'2024-09-12','Arrays and Linked Lists',u.id FROM courses c,users u WHERE c.code='DS401' AND u.username='lecturer04';
INSERT IGNORE INTO class_sessions (course_id,session_date,topic,created_by)
SELECT c.id,'2024-09-19','Stacks and Queues',u.id FROM courses c,users u WHERE c.code='DS401' AND u.username='lecturer04';
INSERT IGNORE INTO class_sessions (course_id,session_date,topic,created_by)
SELECT c.id,'2024-09-26','Trees and Graphs',u.id FROM courses c,users u WHERE c.code='DS401' AND u.username='lecturer04';
INSERT IGNORE INTO class_sessions (course_id,session_date,topic,created_by)
SELECT c.id,'2024-10-03','Sorting and Searching Algorithms',u.id FROM courses c,users u WHERE c.code='DS401' AND u.username='lecturer04';

-- ─── 7. ATTENDANCE (~80% present, 20% absent) ────────────────────────────────
INSERT IGNORE INTO attendance (session_id, student_id, status)
SELECT s.id, e.student_id,
  CASE WHEN MOD(e.student_id + s.id * 3, 5) = 0 THEN 'ABSENT' ELSE 'PRESENT' END
FROM class_sessions s
JOIN courses c  ON c.id  = s.course_id
JOIN enrolments e ON e.course_id = c.id
WHERE c.code IN ('CS101','DB201','SE301','DS401');

-- ─── 8. EVALUATIONS (students 01–30 evaluate enrolled courses) ───────────────
--  ratings: 3, 4, or 5  (always positive – realistic for submitted evals)
INSERT IGNORE INTO evaluations (student_id, course_id, lecturer_id, rating, comment)
SELECT u.id, c.id, c.lecturer_id,
  (3 + MOD(u.id * 3 + c.id, 3)),
  CASE MOD(u.id + c.id, 4)
    WHEN 0 THEN 'Excellent lecturer – very engaging and explains concepts clearly.'
    WHEN 1 THEN 'Good teaching style. Practical examples make the course easy to follow.'
    WHEN 2 THEN 'Very knowledgeable and always available for questions after class.'
    ELSE NULL
  END
FROM users u
JOIN enrolments e ON e.student_id = u.id
JOIN courses c    ON c.id = e.course_id
WHERE u.username IN (
  'student01','student02','student03','student04','student05',
  'student06','student07','student08','student09','student10',
  'student11','student12','student13','student14','student15',
  'student16','student17','student18','student19','student20',
  'student21','student22','student23','student24','student25',
  'student26','student27','student28','student29','student30'
) AND c.lecturer_id IS NOT NULL;

-- ─── SUMMARY ─────────────────────────────────────────────────────────────────
SELECT 'Users'        AS entity, COUNT(*) AS total FROM users
UNION ALL SELECT 'Lecturers', COUNT(*) FROM users WHERE role='LECTURER'
UNION ALL SELECT 'Students',  COUNT(*) FROM users WHERE role='STUDENT'
UNION ALL SELECT 'Courses',   COUNT(*) FROM courses
UNION ALL SELECT 'Enrolments',COUNT(*) FROM enrolments
UNION ALL SELECT 'Sessions',  COUNT(*) FROM class_sessions
UNION ALL SELECT 'Attendance',COUNT(*) FROM attendance
UNION ALL SELECT 'Evaluations',COUNT(*) FROM evaluations;
