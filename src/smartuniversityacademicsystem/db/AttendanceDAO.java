package smartuniversityacademicsystem.db;

import java.sql.*;
import java.util.*;
import smartuniversityacademicsystem.model.*;

public class AttendanceDAO {

    // ── Sessions ──────────────────────────────────────────────────────────────

    /** Creates a new class session and returns the generated ID. */
    public int createSession(int courseId, String date, String topic, int lecturerId)
            throws SQLException {
        String sql = "INSERT INTO class_sessions (course_id, session_date, topic, created_by)" +
                     " VALUES (?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, courseId);
            ps.setString(2, date);
            ps.setString(3, topic.isEmpty() ? null : topic);
            ps.setInt(4, lecturerId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public List<SessionRecord> getCourseSessions(int courseId) throws SQLException {
        String sql =
            "SELECT s.id, s.course_id," +
            "  DATE_FORMAT(s.session_date,'%Y-%m-%d') AS sd," +
            "  s.topic," +
            "  (SELECT COUNT(*) FROM attendance a WHERE a.session_id=s.id AND a.status='PRESENT') AS present_cnt," +
            "  (SELECT COUNT(*) FROM attendance a WHERE a.session_id=s.id) AS total_cnt " +
            "FROM class_sessions s " +
            "WHERE s.course_id=? ORDER BY s.session_date DESC";
        List<SessionRecord> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SessionRecord(
                        rs.getInt("id"), rs.getInt("course_id"),
                        rs.getString("sd"), rs.getString("topic"),
                        rs.getInt("present_cnt"), rs.getInt("total_cnt")
                    ));
                }
            }
        }
        return list;
    }

    // ── Mark attendance ───────────────────────────────────────────────────────

    /**
     * Returns all students enrolled in the course, pre-populated with their
     * current status for the given session (ABSENT by default if not yet marked).
     */
    public List<AttendanceRecord> getSessionAttendance(int sessionId, int courseId)
            throws SQLException {
        String sql =
            "SELECT u.id, u.full_name, u.username," +
            "  COALESCE(a.status,'ABSENT') AS att_status " +
            "FROM enrolments e " +
            "JOIN users u ON u.id = e.student_id " +
            "LEFT JOIN attendance a ON a.session_id=? AND a.student_id=e.student_id " +
            "WHERE e.course_id=? ORDER BY u.full_name";
        List<AttendanceRecord> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new AttendanceRecord(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("att_status")
                    ));
                }
            }
        }
        return list;
    }

    /** Upserts all attendance records for a session in one batch. */
    public void saveAllAttendance(int sessionId, List<AttendanceRecord> records)
            throws SQLException {
        String sql =
            "INSERT INTO attendance (session_id, student_id, status) VALUES (?,?,?)" +
            " ON DUPLICATE KEY UPDATE status=VALUES(status)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (AttendanceRecord r : records) {
                ps.setInt(1, sessionId);
                ps.setInt(2, r.getStudentId());
                ps.setString(3, r.getStatus());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // ── Summaries ─────────────────────────────────────────────────────────────

    /** Per-course attendance summary for a student (student portal view). */
    public List<AttendanceSummary> getStudentAttendanceSummary(int studentId)
            throws SQLException {
        String sql =
            "SELECT c.code, c.name," +
            "  COUNT(s.id) AS total_sessions," +
            "  COALESCE(SUM(CASE WHEN a.status='PRESENT' THEN 1 ELSE 0 END),0) AS attended " +
            "FROM enrolments e " +
            "JOIN courses c ON c.id = e.course_id " +
            "LEFT JOIN class_sessions s ON s.course_id = e.course_id " +
            "LEFT JOIN attendance a ON a.session_id=s.id AND a.student_id=e.student_id " +
            "WHERE e.student_id=? " +
            "GROUP BY c.id, c.code, c.name ORDER BY c.code";
        List<AttendanceSummary> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new AttendanceSummary(
                        rs.getString("code"), rs.getString("name"),
                        rs.getInt("attended"), rs.getInt("total_sessions")
                    ));
                }
            }
        }
        return list;
    }

    /** Per-student attendance summary for a course (lecturer / admin view).
     *  courseCode field of AttendanceSummary holds the student's full name. */
    public List<AttendanceSummary> getCourseAttendanceSummary(int courseId)
            throws SQLException {
        String sql =
            "SELECT u.full_name AS student_name," +
            "  COUNT(s.id) AS total_sessions," +
            "  COALESCE(SUM(CASE WHEN a.status='PRESENT' THEN 1 ELSE 0 END),0) AS attended " +
            "FROM enrolments e " +
            "JOIN users u ON u.id = e.student_id " +
            "LEFT JOIN class_sessions s ON s.course_id = e.course_id " +
            "LEFT JOIN attendance a ON a.session_id=s.id AND a.student_id=e.student_id " +
            "WHERE e.course_id=? " +
            "GROUP BY e.student_id, u.full_name ORDER BY u.full_name";
        List<AttendanceSummary> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new AttendanceSummary(
                        rs.getString("student_name"), "",
                        rs.getInt("attended"), rs.getInt("total_sessions")
                    ));
                }
            }
        }
        return list;
    }

    // ── QR helpers ───────────────────────────────────────────────────────────

    /**
     * Looks up a student by username and checks they are enrolled in the course.
     * Returns the student's user row, or null if not found / not enrolled.
     */
    public smartuniversityacademicsystem.model.User getEnrolledStudentByUsername(
            String username, int courseId) throws SQLException {
        String sql =
            "SELECT u.id, u.username, u.full_name " +
            "FROM users u " +
            "JOIN enrolments e ON e.student_id = u.id " +
            "WHERE u.username = ? AND e.course_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new smartuniversityacademicsystem.model.User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        smartuniversityacademicsystem.model.Role.STUDENT
                    );
                }
            }
        }
        return null;
    }

    /**
     * Marks a single student PRESENT for the given session.
     * Uses INSERT … ON DUPLICATE KEY UPDATE so it is safe to call multiple times.
     */
    public void markStudentPresent(int sessionId, int studentId) throws SQLException {
        String sql =
            "INSERT INTO attendance (session_id, student_id, status) VALUES (?, ?, 'PRESENT')" +
            " ON DUPLICATE KEY UPDATE status = 'PRESENT'";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            ps.setInt(2, studentId);
            ps.executeUpdate();
        }
    }

    // ── Export ────────────────────────────────────────────────────────────────

    public String exportCourseAttendanceCSV(int courseId, String courseCode)
            throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("Course,Student,Sessions Attended,Total Sessions,Attendance %\n");
        for (AttendanceSummary s : getCourseAttendanceSummary(courseId)) {
            sb.append(courseCode).append(",")
              .append(s.getCourseCode()).append(",")
              .append(s.getAttended()).append(",")
              .append(s.getTotal()).append(",")
              .append(s.getPercentageDisplay()).append("\n");
        }
        return sb.toString();
    }
}
