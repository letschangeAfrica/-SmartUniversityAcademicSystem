# Smart University Academic System (SUAS)

A desktop academic management application built with **JavaFX 21** and **MySQL 8**, covering the full student lifecycle — from course registration and timetabling through attendance tracking and lecturer evaluation.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Database Setup](#database-setup)
- [Running the Application](#running-the-application)
- [Default Credentials](#default-credentials)
- [Project Structure](#project-structure)
- [Modules](#modules)
- [Screenshots](#screenshots)

---

## Features

- Role-based login — **Student**, **Lecturer**, **Admin**
- Course registration with credit-limit enforcement
- Grade management (editable by lecturers, read-only for students)
- Weekly timetable with semester filter
- Attendance marking per class session with 75% threshold warnings
- CSV attendance export
- Anonymous 5-star lecturer evaluation with comment feedback
- Admin overview with charts and system-wide statistics
- Animated UI — page transitions, staggered cards, toast notifications

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 (Eclipse Temurin JDK) |
| UI Framework | JavaFX 21 |
| Database | MySQL 8.0 |
| JDBC Driver | mysql-connector-j 9.7.0 |
| IDE | NetBeans 21 |
| Build | Apache Ant (NetBeans default) |

---

## Prerequisites

1. **JDK 21** — [Eclipse Temurin](https://adoptium.net/)
2. **JavaFX SDK 21** — extract to `C:\javafx-sdk-21\javafx-sdk-21.0.5\`
3. **MySQL 8.0** — running locally on port 3306
4. **NetBeans 21** (or any IDE that supports Ant projects)

---

## Database Setup

### 1. Create the database and schema

Run the migration scripts in order against your MySQL instance:

```bash
mysql -u root -p < suas_db_v1.sql
mysql -u root -p < suas_db_v2.sql
mysql -u root -p < suas_db_v3.sql
mysql -u root -p < suas_db_v4.sql
mysql -u root -p < suas_db_v5.sql   # Attendance tables
mysql -u root -p < suas_db_v6.sql   # Evaluation table
```

### 2. Load demo data (optional but recommended)

```bash
mysql -u root -p suas_db < suas_db_seed.sql
```

This seeds the database with:
- 60 students (`student01`–`student60`)
- 10 lecturers (`lecturer01`–`lecturer10`)
- 10 courses with enrolments, grades, attendance records, and evaluations

### 3. Connection settings

The application connects using:

| Setting | Value |
|---------|-------|
| Host | `localhost:3306` |
| Database | `suas_db` |
| Username | `root` |
| Password | `letschange` |

To change these, edit `src/smartuniversityacademicsystem/db/DatabaseConnection.java`.

---

## Running the Application

### In NetBeans

1. Open the project folder in NetBeans (`File → Open Project`)
2. Right-click the project → **Properties → Run**
3. Set VM Options:
   ```
   --module-path "C:/javafx-sdk-21/javafx-sdk-21.0.5/lib" --add-modules javafx.controls,javafx.graphics,javafx.base
   ```
4. Press `Shift+F11` to **Clean and Build**, then `F6` to **Run**

### From the command line

```bash
java --module-path "C:/javafx-sdk-21/javafx-sdk-21.0.5/lib" \
     --add-modules javafx.controls,javafx.graphics,javafx.base \
     -cp "dist/Smartuniversityacademicsystem.jar;lib/mysql-connector-j-9.7.0.jar" \
     smartuniversityacademicsystem.Smartuniversityacademicsystem
```

---

## Default Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Lecturer | `lecturer01` … `lecturer10` | `lecturer123` |
| Student | `student01` … `student60` | `student123` |

Passwords are stored as SHA-256 hashes in the database.

---

## Project Structure

```
src/smartuniversityacademicsystem/
├── Smartuniversityacademicsystem.java   # Entry point
├── dashboard/
│   ├── AdminDashboard.java
│   ├── LecturerDashboard.java
│   └── StudentDashboard.java
├── db/
│   ├── DatabaseConnection.java          # Singleton JDBC connection
│   ├── AdminDAO.java
│   ├── StudentDAO.java
│   ├── LecturerDAO.java
│   ├── UserDAO.java
│   ├── CourseRegistrationDAO.java
│   ├── TimetableDAO.java
│   ├── AttendanceDAO.java
│   └── EvaluationDAO.java
├── model/                               # Plain data objects
│   ├── User.java
│   ├── Course.java
│   ├── Enrolment.java
│   ├── AttendanceRecord.java
│   ├── AttendanceSummary.java
│   ├── SessionRecord.java
│   ├── EvaluationRecord.java
│   ├── LecturerRating.java
│   ├── EvaluationFeedback.java
│   └── ...
├── scheduling/
│   └── TimetableGenerator.java
├── util/
│   └── UIUtils.java                     # Animations, avatar, toast, row hover
└── view/
    ├── LoginView.java
    └── TimetableGridView.java
```

---

## Modules

### 1. Authentication
- SHA-256 password hashing
- Role-based routing on login (Student / Lecturer / Admin portals)
- Active/inactive account support

### 2. Course Management *(Admin)*
- Create and delete courses
- Assign lecturers to courses
- View enrolment reports

### 3. Course Registration *(Student)*
- Browse available courses with capacity indicators
- Register and drop courses
- Credit limit enforced (max 21 credits)

### 4. Grade Management
- Lecturers edit grades inline via double-click table cells
- Students view grades with letter band (A+ → F) and GPA average

### 5. Timetable
- Admin generates timetable entries (course, room, day, time)
- Lecturers and students each see their own filtered weekly grid

### 6. Attendance Management
- Lecturers create class sessions with a date and topic
- Click-to-toggle Present/Absent per student; batch save
- Students see per-course attendance % with animated ProgressBar
- Warning banner for courses below the 75% threshold
- Export per-course attendance as CSV

### 7. Lecturer Evaluation
- Students submit an anonymous 1–5 star rating + optional comment per enrolled course
- Lecturers see their aggregate stats, star-distribution BarChart, and anonymous feedback
- Admin sees all lecturers ranked by average rating

---

## Screenshots

> Run the application and log in with the credentials above to explore each portal.

---

## Repository

[https://github.com/letschangeAfrica/-SmartUniversityAcademicSystem](https://github.com/letschangeAfrica/-SmartUniversityAcademicSystem)
