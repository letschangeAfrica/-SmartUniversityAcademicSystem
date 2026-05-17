package smartuniversityacademicsystem.db;

import java.sql.*;
import java.util.*;
import smartuniversityacademicsystem.model.*;

public class EvaluationDAO {

    // ── Submit / update ───────────────────────────────────────────────────────

    public void submitEvaluation(int studentId, int courseId, int lecturerId,
                                  int rating, String comment) throws SQLException {
        String sql =
            "INSERT INTO evaluations (student_id, course_id, lecturer_id, rating, comment)" +
            " VALUES (?,?,?,?,?)" +
            " ON DUPLICATE KEY UPDATE rating=VALUES(rating), comment=VALUES(comment)," +
            "   submitted_at=CURRENT_TIMESTAMP";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.setInt(3, lecturerId);
            ps.setInt(4, rating);
            ps.setString(5, (comment == null || comment.isEmpty()) ? null : comment);
            ps.executeUpdate();
        }
    }

    // ── Student view ──────────────────────────────────────────────────────────

    /** All courses the student is enrolled in, with their current evaluation status. */
    public List<EvaluationRecord> getStudentEvaluations(int studentId) throws SQLException {
        String sql =
            "SELECT c.id AS cid, c.code, c.name AS cname," +
            "  COALESCE(u.id,0) AS lid," +
            "  COALESCE(u.full_name,'TBA') AS lname," +
            "  COALESCE(ev.rating,0) AS rating," +
            "  COALESCE(ev.comment,'') AS comment " +
            "FROM enrolments en " +
            "JOIN courses c ON c.id = en.course_id " +
            "LEFT JOIN users u ON u.id = c.lecturer_id " +
            "LEFT JOIN evaluations ev" +
            "  ON ev.student_id=en.student_id AND ev.course_id=c.id " +
            "WHERE en.student_id=? ORDER BY c.code";
        List<EvaluationRecord> list = new ArrayList<EvaluationRecord>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new EvaluationRecord(
                        rs.getInt("cid"), rs.getString("code"), rs.getString("cname"),
                        rs.getInt("lid"), rs.getString("lname"),
                        rs.getInt("rating"), rs.getString("comment")
                    ));
                }
            }
        }
        return list;
    }

    // ── Lecturer view ─────────────────────────────────────────────────────────

    public LecturerRating getLecturerStats(int lecturerId) throws SQLException {
        String sql =
            "SELECT u.id, u.full_name," +
            "  COUNT(e.id) AS total," +
            "  COALESCE(AVG(e.rating),0) AS avg_r," +
            "  COALESCE(SUM(CASE WHEN e.rating=1 THEN 1 ELSE 0 END),0) AS c1," +
            "  COALESCE(SUM(CASE WHEN e.rating=2 THEN 1 ELSE 0 END),0) AS c2," +
            "  COALESCE(SUM(CASE WHEN e.rating=3 THEN 1 ELSE 0 END),0) AS c3," +
            "  COALESCE(SUM(CASE WHEN e.rating=4 THEN 1 ELSE 0 END),0) AS c4," +
            "  COALESCE(SUM(CASE WHEN e.rating=5 THEN 1 ELSE 0 END),0) AS c5 " +
            "FROM users u " +
            "LEFT JOIN evaluations e ON e.lecturer_id=u.id " +
            "WHERE u.id=? GROUP BY u.id, u.full_name";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new LecturerRating(
                        rs.getInt("id"), rs.getString("full_name"),
                        rs.getDouble("avg_r"), rs.getInt("total"),
                        rs.getInt("c1"), rs.getInt("c2"), rs.getInt("c3"),
                        rs.getInt("c4"), rs.getInt("c5")
                    );
                }
            }
        }
        return new LecturerRating(lecturerId, "", 0, 0, 0, 0, 0, 0, 0);
    }

    /** Anonymous comments written for this lecturer (no student names). */
    public List<EvaluationFeedback> getLecturerFeedback(int lecturerId) throws SQLException {
        String sql =
            "SELECT e.rating," +
            "  DATE_FORMAT(e.submitted_at,'%Y-%m-%d') AS edate," +
            "  e.comment " +
            "FROM evaluations e " +
            "WHERE e.lecturer_id=? AND e.comment IS NOT NULL AND TRIM(e.comment)!='' " +
            "ORDER BY e.submitted_at DESC";
        List<EvaluationFeedback> list = new ArrayList<EvaluationFeedback>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new EvaluationFeedback(
                        rs.getInt("rating"),
                        rs.getString("edate"),
                        rs.getString("comment")
                    ));
                }
            }
        }
        return list;
    }

    // ── Admin view ────────────────────────────────────────────────────────────

    /** Stats for every lecturer, sorted best-rated first. */
    public List<LecturerRating> getAllLecturerStats() throws SQLException {
        String sql =
            "SELECT u.id, u.full_name," +
            "  COUNT(e.id) AS total," +
            "  COALESCE(AVG(e.rating),0) AS avg_r," +
            "  COALESCE(SUM(CASE WHEN e.rating=1 THEN 1 ELSE 0 END),0) AS c1," +
            "  COALESCE(SUM(CASE WHEN e.rating=2 THEN 1 ELSE 0 END),0) AS c2," +
            "  COALESCE(SUM(CASE WHEN e.rating=3 THEN 1 ELSE 0 END),0) AS c3," +
            "  COALESCE(SUM(CASE WHEN e.rating=4 THEN 1 ELSE 0 END),0) AS c4," +
            "  COALESCE(SUM(CASE WHEN e.rating=5 THEN 1 ELSE 0 END),0) AS c5 " +
            "FROM users u " +
            "LEFT JOIN evaluations e ON e.lecturer_id=u.id " +
            "WHERE u.role='LECTURER' " +
            "GROUP BY u.id, u.full_name " +
            "ORDER BY avg_r DESC, total DESC";
        List<LecturerRating> list = new ArrayList<LecturerRating>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new LecturerRating(
                        rs.getInt("id"), rs.getString("full_name"),
                        rs.getDouble("avg_r"), rs.getInt("total"),
                        rs.getInt("c1"), rs.getInt("c2"), rs.getInt("c3"),
                        rs.getInt("c4"), rs.getInt("c5")
                    ));
                }
            }
        }
        return list;
    }
}
