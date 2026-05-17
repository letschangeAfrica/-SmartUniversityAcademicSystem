package smartuniversityacademicsystem.model;

public class AvailableCourse {
    private int    id;
    private String code;
    private String name;
    private String lecturerName;
    private int    credits;
    private int    enrolled;
    private int    maxStudents;

    public AvailableCourse(int id, String code, String name, String lecturerName,
                           int credits, int enrolled, int maxStudents) {
        this.id           = id;
        this.code         = code;
        this.name         = name;
        this.lecturerName = lecturerName;
        this.credits      = credits;
        this.enrolled     = enrolled;
        this.maxStudents  = maxStudents;
    }

    public int    getId()             { return id; }
    public String getCode()           { return code; }
    public String getName()           { return name; }
    public String getLecturerName()   { return lecturerName; }
    public int    getCredits()        { return credits; }
    public int    getEnrolled()       { return enrolled; }
    public int    getMaxStudents()    { return maxStudents; }
    public boolean isFull()           { return enrolled >= maxStudents; }

    public String getCapacityDisplay() {
        return enrolled + " / " + maxStudents;
    }

    public String getStatusDisplay() {
        if (isFull()) return "Full";
        int remaining = maxStudents - enrolled;
        return remaining + " seat" + (remaining == 1 ? "" : "s") + " left";
    }
}
