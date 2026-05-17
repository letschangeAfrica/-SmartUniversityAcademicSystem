package smartuniversityacademicsystem.model;

/** Represents one class session (date + topic) belonging to a course. */
public class SessionRecord {

    private final int    id;
    private final int    courseId;
    private final String sessionDate;
    private final String topic;
    private final int    presentCount;
    private final int    totalCount;

    public SessionRecord(int id, int courseId, String sessionDate,
                         String topic, int presentCount, int totalCount) {
        this.id           = id;
        this.courseId     = courseId;
        this.sessionDate  = sessionDate;
        this.topic        = topic != null ? topic : "";
        this.presentCount = presentCount;
        this.totalCount   = totalCount;
    }

    public int    getId()           { return id; }
    public int    getCourseId()     { return courseId; }
    public String getSessionDate()  { return sessionDate; }
    public String getTopic()        { return topic; }
    public int    getPresentCount() { return presentCount; }
    public int    getTotalCount()   { return totalCount; }

    @Override
    public String toString() {
        String label = sessionDate;
        if (!topic.isEmpty()) label += "  –  " + topic;
        label += "  (" + presentCount + "/" + totalCount + " present)";
        return label;
    }
}
