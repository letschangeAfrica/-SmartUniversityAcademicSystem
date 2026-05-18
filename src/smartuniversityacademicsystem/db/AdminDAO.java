package smartuniversityacademicsystem.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import smartuniversityacademicsystem.model.Course;
import smartuniversityacademicsystem.model.CourseReport;
import smartuniversityacademicsystem.model.UserRecord;

public class AdminDAO {

    // ── Users ─────────────────────────────────────────────────────────────────

    public List<UserRecord> getAllUsers() throws SQLException {
        List<UserRecord> list = new ArrayList<>();
        String sql = "SELECT id, username, full_name, role, active FROM users ORDER BY role, full_name";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new UserRecord(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("role"),
                    rs.getBoolean("active")
                ));
            }
        }
        return list;
    }

    public void createUser(String username, String password,
                           String fullName, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password, full_name, role) " +
                     "VALUES (?, SHA2(?, 256), ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, fullName);
            ps.setString(4, role.toUpperCase());
            ps.executeUpdate();
        }
    }

    public void toggleUserActive(int userId, boolean active) throws SQLException {
        String sql = "UPDATE users SET active = ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    // ── Courses ───────────────────────────────────────────────────────────────

    public List<Course> getAllCourses() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql =
            "SELECT c.id, c.code, c.name, COALESCE(u.full_name,'Unassigned') AS lecturer " +
            "FROM courses c LEFT JOIN users u ON c.lecturer_id = u.id ORDER BY c.code";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Course(
                    rs.getInt("id"),
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("lecturer")
                ));
            }
        }
        return list;
    }

    public void createCourse(String code, String name, Integer lecturerId) throws SQLException {
        String sql = "INSERT INTO courses (code, name, lecturer_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, code.toUpperCase().trim());
            ps.setString(2, name.trim());
            if (lecturerId == null) ps.setNull(3, Types.INTEGER);
            else                    ps.setInt(3, lecturerId);
            ps.executeUpdate();
        }
    }

    public void assignLecturer(int courseId, Integer lecturerId) throws SQLException {
        String sql = "UPDATE courses SET lecturer_id = ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            if (lecturerId == null) ps.setNull(1, Types.INTEGER);
            else                    ps.setInt(1, lecturerId);
            ps.setInt(2, courseId);
            ps.executeUpdate();
        }
    }

    public void deleteCourse(int courseId) throws SQLException {
        // remove enrolments first, then course
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM enrolments WHERE course_id = ?")) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM timetable WHERE course_id = ?")) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("DELETE FROM courses WHERE id = ?")) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
        }
    }

    public List<UserRecord> getLecturers() throws SQLException {
        List<UserRecord> list = new ArrayList<>();
        String sql = "SELECT id, username, full_name FROM users WHERE role='LECTURER' AND active=TRUE ORDER BY full_name";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new UserRecord(
                    rs.getInt("id"), rs.getString("username"),
                    rs.getString("full_name"), "LECTURER", true
                ));
            }
        }
        return list;
    }

    // ── Stats ─────────────────────────────────────────────────────────────────

    public int[] getSystemStats() throws SQLException {
        int students = 0, lecturers = 0, admins = 0, courses = 0, enrolments = 0;
        String sql =
            "SELECT " +
            "  SUM(role='STUDENT')  AS students," +
            "  SUM(role='LECTURER') AS lecturers," +
            "  SUM(role='ADMIN')    AS admins " +
            "FROM users WHERE active=TRUE";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                students  = rs.getInt("students");
                lecturers = rs.getInt("lecturers");
                admins    = rs.getInt("admins");
            }
        }
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT COUNT(*) FROM courses");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) courses = rs.getInt(1);
        }
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement("SELECT COUNT(*) FROM enrolments");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) enrolments = rs.getInt(1);
        }
        return new int[]{students, lecturers, admins, courses, enrolments};
    }

    // ── Reports ───────────────────────────────────────────────────────────────

    public List<CourseReport> getEnrollmentReport() throws SQLException {
        List<CourseReport> list = new ArrayList<>();
        String sql =
            "SELECT c.code, c.name, COALESCE(u.full_name,'Unassigned') AS lecturer, " +
            "  COUNT(e.id) AS enrolled, " +
            "  AVG(e.grade) AS avg_grade, " +
            "  SUM(CASE WHEN e.grade >= 50 THEN 1 ELSE 0 END) AS pass_count " +
            "FROM courses c " +
            "LEFT JOIN enrolments e ON c.id = e.course_id " +
            "LEFT JOIN users u ON c.lecturer_id = u.id " +
            "GROUP BY c.id ORDER BY c.code";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                double avg = rs.getDouble("avg_grade");
                list.add(new CourseReport(
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("lecturer"),
                    rs.getInt("enrolled"),
                    rs.wasNull() ? null : avg,
                    rs.getInt("pass_count")
                ));
            }
        }
        return list;
    }
}
