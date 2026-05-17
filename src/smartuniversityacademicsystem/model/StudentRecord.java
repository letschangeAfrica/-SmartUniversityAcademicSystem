package smartuniversityacademicsystem.model;

import javafx.beans.property.*;

public class StudentRecord {
    private final IntegerProperty studentId   = new SimpleIntegerProperty();
    private final StringProperty  username    = new SimpleStringProperty();
    private final StringProperty  fullName    = new SimpleStringProperty();
    private final DoubleProperty  grade       = new SimpleDoubleProperty(Double.NaN);
    private final IntegerProperty courseId    = new SimpleIntegerProperty();

    public StudentRecord(int studentId, String username, String fullName,
                         Double grade, int courseId) {
        this.studentId.set(studentId);
        this.username.set(username);
        this.fullName.set(fullName);
        this.grade.set(grade == null ? Double.NaN : grade);
        this.courseId.set(courseId);
    }

    public int    getStudentId()  { return studentId.get(); }
    public String getUsername()   { return username.get(); }
    public String getFullName()   { return fullName.get(); }
    public int    getCourseId()   { return courseId.get(); }

    public Double getGrade() {
        double v = grade.get();
        return Double.isNaN(v) ? null : v;
    }
    public void setGrade(Double v) {
        grade.set(v == null ? Double.NaN : v);
    }

    public String getGradeDisplay() {
        double v = grade.get();
        return Double.isNaN(v) ? "Not graded" : String.format("%.1f%%", v);
    }
}
