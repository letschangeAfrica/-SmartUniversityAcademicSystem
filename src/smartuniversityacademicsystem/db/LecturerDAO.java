package smartuniversityacademicsystem.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import smartuniversityacademicsystem.model.Course;
import smartuniversityacademicsystem.model.StudentRecord;

public class LecturerDAO {

    public List<Course> getCourses(int lecturerId) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql =
            "SELECT id, code, name FROM courses WHERE lecturer_id = ? ORDER BY code";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Course(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        ""
                    ));
                }
            }
        }
        return list;
    }

    public List<StudentRecord> getStudentsInCourse(int courseId) throws SQLException {
        List<StudentRecord> list = new ArrayList<>();
        String sql =
            "SELECT u.id, u.username, u.full_name, e.grade " +
            "FROM enrolments e " +
            "JOIN users u ON e.student_id = u.id " +
            "WHERE e.course_id = ? ORDER BY u.full_name";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double g = rs.getDouble("grade");
                    list.add(new StudentRecord(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.wasNull() ? null : g,
                        courseId
                    ));
                }
            }
        }
        return list;
    }

    public List<StudentRecord> getAllStudents(int lecturerId) throws SQLException {
        List<StudentRecord> list = new ArrayList<>();
        String sql =
            "SELECT DISTINCT u.id, u.username, u.full_name, e.grade, c.id AS course_id, c.code " +
            "FROM enrolments e " +
            "JOIN users u ON e.student_id = u.id " +
            "JOIN courses c ON e.course_id = c.id " +
            "WHERE c.lecturer_id = ? ORDER BY c.code, u.full_name";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double g = rs.getDouble("grade");
                    StudentRecord sr = new StudentRecord(
                        rs.getInt("id"),
                        rs.getString("code") + " – " + rs.getString("username"),
                        rs.getString("full_name"),
                        rs.wasNull() ? null : g,
                        rs.getInt("course_id")
                    );
                    list.add(sr);
                }
            }
        }
        return list;
    }

    public void updateGrade(int studentId, int courseId, Double grade) throws SQLException {
        String sql =
            "UPDATE enrolments SET grade = ? WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            if (grade == null) ps.setNull(1, Types.DECIMAL);
            else               ps.setDouble(1, grade);
            ps.setInt(2, studentId);
            ps.setInt(3, courseId);
            ps.executeUpdate();
        }
    }

    public int getCourseCount(int lecturerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM courses WHERE lecturer_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public int getTotalStudents(int lecturerId) throws SQLException {
        String sql =
            "SELECT COUNT(DISTINCT e.student_id) " +
            "FROM enrolments e JOIN courses c ON e.course_id = c.id " +
            "WHERE c.lecturer_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public double getOverallAverage(int lecturerId) throws SQLException {
        String sql =
            "SELECT AVG(e.grade) FROM enrolments e " +
            "JOIN courses c ON e.course_id = c.id " +
            "WHERE c.lecturer_id = ? AND e.grade IS NOT NULL";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double v = rs.getDouble(1);
                    return rs.wasNull() ? 0.0 : v;
                }
            }
        }
        return 0.0;
    }
}
