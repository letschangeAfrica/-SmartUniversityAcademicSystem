package smartuniversityacademicsystem.model;

public class TimetableEntry {
    private String courseCode;
    private String courseName;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String venue;

    public TimetableEntry(String courseCode, String courseName,
                          String dayOfWeek, String startTime,
                          String endTime, String venue) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.dayOfWeek  = dayOfWeek;
        this.startTime  = startTime;
        this.endTime    = endTime;
        this.venue      = venue;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getDayOfWeek()  { return dayOfWeek; }
    public String getStartTime()  { return startTime; }
    public String getEndTime()    { return endTime; }
    public String getVenue()      { return venue; }
}
