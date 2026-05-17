package smartuniversityacademicsystem.model;

public class Course {
    private int    id;
    private String code;
    private String name;
    private String lecturerName;

    public Course(int id, String code, String name, String lecturerName) {
        this.id           = id;
        this.code         = code;
        this.name         = name;
        this.lecturerName = lecturerName;
    }

    public int    getId()           { return id; }
    public String getCode()         { return code; }
    public String getName()         { return name; }
    public String getLecturerName() { return lecturerName; }
}
