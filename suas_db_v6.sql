-- Module 5: Lecturer Evaluation
-- Run after suas_db_v1 through v5.

USE suas_db;

CREATE TABLE IF NOT EXISTS evaluations (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    student_id   INT NOT NULL,
    course_id    INT NOT NULL,
    lecturer_id  INT NOT NULL,
    rating       INT NOT NULL,
    comment      TEXT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id)  REFERENCES users(id)   ON DELETE CASCADE,
    FOREIGN KEY (course_id)   REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (lecturer_id) REFERENCES users(id)   ON DELETE CASCADE,
    UNIQUE KEY uq_student_course (student_id, course_id),
    CONSTRAINT chk_rating CHECK (rating BETWEEN 1 AND 5)
);
