-- Module 4: Attendance Management
-- Run this script against suas_db AFTER running v1-v4 scripts.

USE suas_db;

CREATE TABLE IF NOT EXISTS class_sessions (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    course_id    INT NOT NULL,
    session_date DATE NOT NULL,
    topic        VARCHAR(255),
    created_by   INT,
    FOREIGN KEY (course_id)  REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id)   ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS attendance (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL,
    student_id INT NOT NULL,
    status     ENUM('PRESENT','ABSENT') NOT NULL DEFAULT 'ABSENT',
    marked_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES class_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id)          ON DELETE CASCADE,
    UNIQUE KEY uq_session_student (session_id, student_id)
);
