package smartuniversityacademicsystem.model;

public class CourseReport {
    private String code;
    private String name;
    private String lecturerName;
    private int    enrolled;
    private Double averageGrade;
    private int    passCount;

    public CourseReport(String code, String name, String lecturerName,
                        int enrolled, Double averageGrade, int passCount) {
        this.code         = code;
        this.name         = name;
        this.lecturerName = lecturerName;
        this.enrolled     = enrolled;
        this.averageGrade = averageGrade;
        this.passCount    = passCount;
    }

    public String getCode()         { return code; }
    public String getName()         { return name; }
    public String getLecturerName() { return lecturerName; }
    public int    getEnrolled()     { return enrolled; }

    public String getAverageGradeDisplay() {
        return (averageGrade == null || averageGrade == 0) ? "N/A"
               : String.format("%.1f%%", averageGrade);
    }

    public String getPassRate() {
        if (enrolled == 0) return "N/A";
        return String.format("%.0f%%", (passCount * 100.0) / enrolled);
    }
}
