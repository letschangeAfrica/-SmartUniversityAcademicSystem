package smartuniversityacademicsystem.model;

/** One enrolled course the student can evaluate (or has already evaluated). */
public class EvaluationRecord {

    private final int    courseId;
    private final String courseCode;
    private final String courseName;
    private final int    lecturerId;
    private final String lecturerName;
    private int    currentRating;   // 0 = not yet submitted
    private String currentComment;

    public EvaluationRecord(int courseId, String courseCode, String courseName,
                             int lecturerId, String lecturerName,
                             int currentRating, String currentComment) {
        this.courseId       = courseId;
        this.courseCode     = courseCode;
        this.courseName     = courseName;
        this.lecturerId     = lecturerId;
        this.lecturerName   = lecturerName != null ? lecturerName : "TBA";
        this.currentRating  = currentRating;
        this.currentComment = currentComment != null ? currentComment : "";
    }

    public int    getCourseId()       { return courseId; }
    public String getCourseCode()     { return courseCode; }
    public String getCourseName()     { return courseName; }
    public int    getLecturerId()     { return lecturerId; }
    public String getLecturerName()   { return lecturerName; }
    public int    getCurrentRating()  { return currentRating; }
    public String getCurrentComment() { return currentComment; }

    public void setCurrentRating(int r)  { currentRating  = r; }
    public void setCurrentComment(String c) { currentComment = c; }

    public boolean isSubmitted() { return currentRating > 0; }

    public String getRatingDisplay() {
        if (currentRating == 0) return "Not yet rated";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(i < currentRating ? "★" : "☆");
        return sb + " (" + currentRating + "/5)";
    }
}
