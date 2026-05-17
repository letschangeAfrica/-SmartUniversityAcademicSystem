package smartuniversityacademicsystem.model;

public class Course {
    private int    id;
    private String code;
    private String name;
    private String lecturerName;
    private int    lecturerId;
    private int    enrolledCount;

    // existing usages (no lecturerId)
    public Course(int id, String code, String name, String lecturerName) {
        this(id, code, name, lecturerName, 0, 0);
    }

    // full constructor used by scheduler
    public Course(int id, String code, String name, String lecturerName,
                  int lecturerId, int enrolledCount) {
        this.id            = id;
        this.code          = code;
        this.name          = name;
        this.lecturerName  = lecturerName;
        this.lecturerId    = lecturerId;
        this.enrolledCount = enrolledCount;
    }

    public int    getId()            { return id; }
    public String getCode()          { return code; }
    public String getName()          { return name; }
    public String getLecturerName()  { return lecturerName; }
    public int    getLecturerId()    { return lecturerId; }
    public int    getEnrolledCount() { return enrolledCount; }
}
