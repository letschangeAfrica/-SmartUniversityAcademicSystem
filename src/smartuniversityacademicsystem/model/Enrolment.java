package smartuniversityacademicsystem.model;

public class Enrolment {
    private String courseCode;
    private String courseName;
    private Double grade;

    public Enrolment(String courseCode, String courseName, Double grade) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.grade      = grade;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public Double getGrade()      { return grade; }

    public String getLetterGrade() {
        if (grade == null) return "N/A";
        if (grade >= 90)   return "A+";
        if (grade >= 85)   return "A";
        if (grade >= 80)   return "A-";
        if (grade >= 75)   return "B+";
        if (grade >= 70)   return "B";
        if (grade >= 65)   return "B-";
        if (grade >= 60)   return "C+";
        if (grade >= 55)   return "C";
        if (grade >= 50)   return "D";
        return "F";
    }

    public String getGradeDisplay() {
        return grade == null ? "Not graded" : String.format("%.1f%%", grade);
    }
}
