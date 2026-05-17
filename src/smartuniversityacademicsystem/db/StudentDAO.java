package smartuniversityacademicsystem.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import smartuniversityacademicsystem.model.Course;
import smartuniversityacademicsystem.model.Enrolment;
import smartuniversityacademicsystem.model.TimetableEntry;

public class StudentDAO {

    public List<Course> getEnrolledCourses(int studentId) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql =
            "SELECT c.id, c.code, c.name, COALESCE(u.full_name,'Unassigned') AS lecturer " +
            "FROM courses c " +
            "JOIN enrolments e ON c.id = e.course_id " +
            "LEFT JOIN users u ON c.lecturer_id = u.id " +
            "WHERE e.student_id = ? ORDER BY c.code";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Course(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("lecturer")
                    ));
                }
            }
        }
        return list;
    }

    public List<Enrolment> getGrades(int studentId) throws SQLException {
        List<Enrolment> list = new ArrayList<>();
        String sql =
            "SELECT c.code, c.name, e.grade " +
            "FROM enrolments e " +
            "JOIN courses c ON e.course_id = c.id " +
            "WHERE e.student_id = ? ORDER BY c.code";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double g = rs.getDouble("grade");
                    list.add(new Enrolment(
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.wasNull() ? null : g
                    ));
                }
            }
        }
        return list;
    }

    public List<TimetableEntry> getTimetable(int studentId) throws SQLException {
        List<TimetableEntry> list = new ArrayList<>();
        String sql =
            "SELECT c.code, c.name, t.day_of_week, " +
            "TIME_FORMAT(t.start_time,'%H:%i') AS start_time, " +
            "TIME_FORMAT(t.end_time,'%H:%i') AS end_time, t.venue " +
            "FROM timetable t " +
            "JOIN courses c ON t.course_id = c.id " +
            "JOIN enrolments e ON c.id = e.course_id " +
            "WHERE e.student_id = ? " +
            "ORDER BY FIELD(t.day_of_week,'Monday','Tuesday','Wednesday','Thursday','Friday'), t.start_time";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new TimetableEntry(
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("day_of_week"),
                        rs.getString("start_time"),
                        rs.getString("end_time"),
                        rs.getString("venue")
                    ));
                }
            }
        }
        return list;
    }

    public int getEnrolledCount(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM enrolments WHERE student_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public double getAverageGrade(int studentId) throws SQLException {
        String sql = "SELECT AVG(grade) FROM enrolments WHERE student_id = ? AND grade IS NOT NULL";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble(1);
                    return rs.wasNull() ? 0.0 : avg;
                }
            }
        }
        return 0.0;
    }
}
