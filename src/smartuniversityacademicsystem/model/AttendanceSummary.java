package smartuniversityacademicsystem.model;

/**
 * Attendance summary for one (entity, course/student) pair.
 *
 * Student view  – courseCode = course code,  courseName = course name
 * Lecturer/Admin view – courseCode = student full name, courseName = ""
 */
public class AttendanceSummary {

    private final String courseCode;
    private final String courseName;
    private final int    attended;
    private final int    total;
    private final double percentage;

    public AttendanceSummary(String courseCode, String courseName, int attended, int total) {
        this.courseCode  = courseCode;
        this.courseName  = courseName != null ? courseName : "";
        this.attended    = attended;
        this.total       = total;
        this.percentage  = total == 0 ? 0.0 : (attended * 100.0 / total);
    }

    public String getCourseCode()  { return courseCode; }
    public String getCourseName()  { return courseName; }
    public int    getAttended()    { return attended; }
    public int    getTotal()       { return total; }
    public double getPercentage()  { return percentage; }

    public String getPercentageDisplay() {
        return total == 0 ? "N/A" : String.format("%.1f%%", percentage);
    }

    public String getWarning() {
        if (total == 0) return "No sessions yet";
        return percentage < 75 ? "WARNING: Below 75%" : "OK";
    }
}
