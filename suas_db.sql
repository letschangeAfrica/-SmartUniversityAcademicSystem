-- ============================================================
--  Smart University Academic System – Database Setup
--  Run this in MySQL Workbench or via: mysql -u root -p < suas_db.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS suas_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;



USE suas_db;


-- ── Users ────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(64)  NOT NULL,          -- SHA-256 hex digest
    full_name  VARCHAR(120) NOT NULL,
    role       ENUM('STUDENT','LECTURER','ADMIN') NOT NULL,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ── Seed users (password = "password123" for all) ────────────
-- SHA2('password123', 256) = ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f
INSERT INTO users (username, password, full_name, role) VALUES
  ('admin1',    SHA2('password123', 256), 'Alice Admin',     'ADMIN'),
  ('lecturer1', SHA2('password123', 256), 'Bob Lecturer',    'LECTURER'),
  ('student1',  SHA2('password123', 256), 'Charlie Student', 'STUDENT')
ON DUPLICATE KEY UPDATE username = username;

-- ── Courses (stub – expand as needed) ────────────────────────
CREATE TABLE IF NOT EXISTS courses (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    code        VARCHAR(20)  NOT NULL UNIQUE,
    name        VARCHAR(120) NOT NULL,
    lecturer_id INT,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (lecturer_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ── Enrolments ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS enrolments (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id  INT NOT NULL,
    grade      DECIMAL(5,2),
    enrolled_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_enrolment (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (course_id)  REFERENCES courses(id)
);

USE
suas_db;
 
SHOW TABLES;
 
SELECT id, username, full_name, role, active FROM users;

            