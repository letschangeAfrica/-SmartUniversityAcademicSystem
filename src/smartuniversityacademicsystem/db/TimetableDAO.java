package smartuniversityacademicsystem.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import smartuniversityacademicsystem.model.*;

public class TimetableDAO {

    // ── Semesters ─────────────────────────────────────────────────────────────

    public List<Semester> getSemesters() throws SQLException {
        List<Semester> list = new ArrayList<>();
        String sql = "SELECT id, name, start_date, end_date, active FROM semesters ORDER BY id DESC";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Semester(
                    rs.getInt("id"), rs.getString("name"),
                    rs.getString("start_date"), rs.getString("end_date"),
                    rs.getBoolean("active")
                ));
            }
        }
        return list;
    }

    public Semester getActiveSemester() throws SQLException {
        String sql = "SELECT id, name, start_date, end_date, active FROM semesters WHERE active=TRUE LIMIT 1";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Semester(rs.getInt("id"), rs.getString("name"),
                    rs.getString("start_date"), rs.getString("end_date"), true);
            }
        }
        return null;
    }

    // ── Rooms ─────────────────────────────────────────────────────────────────

    public List<Room> getRooms() throws SQLException {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT id, name, capacity FROM rooms ORDER BY name";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Room(rs.getInt("id"), rs.getString("name"), rs.getInt("capacity")));
            }
        }
        return list;
    }

    // ── Timetable entries ─────────────────────────────────────────────────────

    public List<TimetableEntry> getTimetableBySemester(int semesterId) throws SQLException {
        List<TimetableEntry> list = new ArrayList<>();
        String sql =
            "SELECT c.code, c.name, t.day_of_week, " +
            "TIME_FORMAT(t.start_time,'%H:%i') AS start_t, " +
            "TIME_FORMAT(t.end_time,'%H:%i') AS end_t, " +
            "COALESCE(r.name, t.venue, 'TBA') AS venue " +
            "FROM timetable t " +
            "JOIN courses c ON t.course_id = c.id " +
            "LEFT JOIN rooms r ON t.room_id = r.id " +
            "WHERE t.semester_id = ? " +
            "ORDER BY FIELD(t.day_of_week,'Monday','Tuesday','Wednesday','Thursday','Friday'), t.start_time";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, semesterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new TimetableEntry(
                        rs.getString("code"), rs.getString("name"),
                        rs.getString("day_of_week"), rs.getString("start_t"),
                        rs.getString("end_t"), rs.getString("venue")
                    ));
                }
            }
        }
        return list;
    }

    public List<TimetableEntry> getTimetableForStudent(int studentId, int semesterId) throws SQLException {
        List<TimetableEntry> list = new ArrayList<>();
        String sql =
            "SELECT c.code, c.name, t.day_of_week, " +
            "TIME_FORMAT(t.start_time,'%H:%i') AS start_t, " +
            "TIME_FORMAT(t.end_time,'%H:%i') AS end_t, " +
            "COALESCE(r.name, t.venue,'TBA') AS venue " +
            "FROM timetable t " +
            "JOIN courses c ON t.course_id = c.id " +
            "JOIN enrolments e ON c.id = e.course_id " +
            "LEFT JOIN rooms r ON t.room_id = r.id " +
            "WHERE e.student_id = ? AND t.semester_id = ? " +
            "ORDER BY FIELD(t.day_of_week,'Monday','Tuesday','Wednesday','Thursday','Friday'), t.start_time";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, semesterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new TimetableEntry(
                        rs.getString("code"), rs.getString("name"),
                        rs.getString("day_of_week"), rs.getString("start_t"),
                        rs.getString("end_t"), rs.getString("venue")
                    ));
                }
            }
        }
        return list;
    }

    public List<TimetableEntry> getTimetableForLecturer(int lecturerId, int semesterId) throws SQLException {
        List<TimetableEntry> list = new ArrayList<>();
        String sql =
            "SELECT c.code, c.name, t.day_of_week, " +
            "TIME_FORMAT(t.start_time,'%H:%i') AS start_t, " +
            "TIME_FORMAT(t.end_time,'%H:%i') AS end_t, " +
            "COALESCE(r.name, t.venue,'TBA') AS venue " +
            "FROM timetable t " +
            "JOIN courses c ON t.course_id = c.id " +
            "LEFT JOIN rooms r ON t.room_id = r.id " +
            "WHERE c.lecturer_id = ? AND t.semester_id = ? " +
            "ORDER BY FIELD(t.day_of_week,'Monday','Tuesday','Wednesday','Thursday','Friday'), t.start_time";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            ps.setInt(2, semesterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new TimetableEntry(
                        rs.getString("code"), rs.getString("name"),
                        rs.getString("day_of_week"), rs.getString("start_t"),
                        rs.getString("end_t"), rs.getString("venue")
                    ));
                }
            }
        }
        return list;
    }

    // ── Courses for scheduling ────────────────────────────────────────────────

    public List<Course> getCoursesForScheduling() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql =
            "SELECT c.id, c.code, c.name, " +
            "COALESCE(u.full_name,'Unassigned') AS lecturer, " +
            "COALESCE(c.lecturer_id, 0) AS lecturer_id, " +
            "COUNT(e.id) AS enrolled " +
            "FROM courses c " +
            "LEFT JOIN users u ON c.lecturer_id = u.id " +
            "LEFT JOIN enrolments e ON c.id = e.course_id " +
            "GROUP BY c.id ORDER BY enrolled DESC";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Course(
                    rs.getInt("id"), rs.getString("code"), rs.getString("name"),
                    rs.getString("lecturer"), rs.getInt("lecturer_id"),
                    rs.getInt("enrolled")
                ));
            }
        }
        return list;
    }

    // ── Save / clear generated entries ────────────────────────────────────────

    public void clearAutoGenerated(int semesterId) throws SQLException {
        String sql = "DELETE FROM timetable WHERE semester_id = ? AND auto_generated = TRUE";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, semesterId);
            ps.executeUpdate();
        }
    }

    public void saveGeneratedEntry(int courseId, String day, String startTime,
                                   String endTime, int roomId, int semesterId) throws SQLException {
        String sql =
            "INSERT INTO timetable (course_id, day_of_week, start_time, end_time, venue, " +
            "room_id, semester_id, auto_generated) VALUES (?,?,?,?,?,?,?,TRUE)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setString(2, day);
            ps.setString(3, startTime);
            ps.setString(4, endTime);
            ps.setString(5, "");        // venue filled from room join
            ps.setInt(6, roomId);
            ps.setInt(7, semesterId);
            ps.executeUpdate();
        }
    }

    // ── Conflict detection ────────────────────────────────────────────────────

    public boolean hasLecturerConflict(int lecturerId, String day,
                                       String start, String end,
                                       int semesterId) throws SQLException {
        if (lecturerId == 0) return false;
        String sql =
            "SELECT COUNT(*) FROM timetable t " +
            "JOIN courses c ON t.course_id = c.id " +
            "WHERE c.lecturer_id = ? AND t.day_of_week = ? AND t.semester_id = ? " +
            "AND NOT (t.end_time <= ? OR t.start_time >= ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            ps.setString(2, day);
            ps.setInt(3, semesterId);
            ps.setString(4, start);
            ps.setString(5, end);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean hasRoomConflict(int roomId, String day,
                                   String start, String end,
                                   int semesterId) throws SQLException {
        String sql =
            "SELECT COUNT(*) FROM timetable " +
            "WHERE room_id = ? AND day_of_week = ? AND semester_id = ? " +
            "AND NOT (end_time <= ? OR start_time >= ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setString(2, day);
            ps.setInt(3, semesterId);
            ps.setString(4, start);
            ps.setString(5, end);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
