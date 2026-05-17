package smartuniversityacademicsystem.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import smartuniversityacademicsystem.model.AvailableCourse;
import smartuniversityacademicsystem.model.Enrolment;

public class CourseRegistrationDAO {

    /** Courses the student is NOT yet enrolled in, with live capacity info. */
    public List<AvailableCourse> getAvailableCourses(int studentId) throws SQLException {
        List<AvailableCourse> list = new ArrayList<>();
        String sql =
            "SELECT c.id, c.code, c.name, c.credits, c.max_students, " +
            "  COALESCE(u.full_name,'Unassigned') AS lecturer, " +
            "  COUNT(e.id) AS enrolled " +
            "FROM courses c " +
            "LEFT JOIN users u ON c.lecturer_id = u.id " +
            "LEFT JOIN enrolments e ON c.id = e.course_id " +
            "WHERE c.id NOT IN " +
            "  (SELECT course_id FROM enrolments WHERE student_id = ?) " +
            "GROUP BY c.id ORDER BY c.code";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new AvailableCourse(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("lecturer"),
                        rs.getInt("credits"),
                        rs.getInt("enrolled"),
                        rs.getInt("max_students")
                    ));
                }
            }
        }
        return list;
    }

    /** Courses the student IS enrolled in, with credits and grade. */
    public List<Enrolment> getEnrolledCoursesWithCredits(int studentId) throws SQLException {
        List<Enrolment> list = new ArrayList<>();
        String sql =
            "SELECT c.code, c.name, c.credits, e.grade " +
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
                        rs.getString("name") + "  (" + rs.getInt("credits") + " cr)",
                        rs.wasNull() ? null : g
                    ));
                }
            }
        }
        return list;
    }

    /** Total enrolled credits for the student. */
    public int getTotalCredits(int studentId) throws SQLException {
        String sql =
            "SELECT COALESCE(SUM(c.credits),0) " +
            "FROM enrolments e JOIN courses c ON e.course_id = c.id " +
            "WHERE e.student_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Validate capacity before registering.
     * Returns null if OK, or an error message string if not.
     */
    public String checkRegistration(int studentId, int courseId) throws SQLException {
        // 1. Capacity check
        String capSql =
            "SELECT c.max_students, COUNT(e.id) AS enrolled, c.credits " +
            "FROM courses c LEFT JOIN enrolments e ON c.id = e.course_id " +
            "WHERE c.id = ? GROUP BY c.id";
        int credits = 0;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(capSql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int max = rs.getInt("max_students");
                    int enrolled = rs.getInt("enrolled");
                    credits = rs.getInt("credits");
                    if (enrolled >= max)
                        return "This course is full (" + enrolled + "/" + max + " students).";
                }
            }
        }

        // 2. Credit limit check (max 21 credits per semester)
        int currentCredits = getTotalCredits(studentId);
        if (currentCredits + credits > 21)
            return "Credit limit exceeded. You have " + currentCredits +
                   " credits. Adding " + credits + " would exceed the 21-credit limit.";

        // 3. Timetable conflict check (student already has a class at this time)
        String conflictSql =
            "SELECT COUNT(*) FROM timetable t1 " +
            "JOIN timetable t2 ON t1.day_of_week = t2.day_of_week " +
            "  AND NOT (t1.end_time <= t2.start_time OR t1.start_time >= t2.end_time) " +
            "JOIN enrolments e ON t1.course_id = e.course_id " +
            "WHERE e.student_id = ? AND t2.course_id = ? " +
            "AND t1.semester_id = t2.semester_id";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(conflictSql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0)
                    return "Timetable conflict: this course overlaps with one you are already enrolled in.";
            }
        }

        return null; // all checks passed
    }

    public void registerCourse(int studentId, int courseId) throws SQLException {
        String sql = "INSERT INTO enrolments (student_id, course_id) VALUES (?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.executeUpdate();
        }
    }

    public void dropCourse(int studentId, String courseCode) throws SQLException {
        String sql =
            "DELETE e FROM enrolments e " +
            "JOIN courses c ON e.course_id = c.id " +
            "WHERE e.student_id = ? AND c.code = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setString(2, courseCode);
            ps.executeUpdate();
        }
    }
}
