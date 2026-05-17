package smartuniversityacademicsystem.model;

public class Semester {
    private int     id;
    private String  name;
    private String  startDate;
    private String  endDate;
    private boolean active;

    public Semester(int id, String name, String startDate, String endDate, boolean active) {
        this.id        = id;
        this.name      = name;
        this.startDate = startDate;
        this.endDate   = endDate;
        this.active    = active;
    }

    public int     getId()        { return id; }
    public String  getName()      { return name; }
    public String  getStartDate() { return startDate; }
    public String  getEndDate()   { return endDate; }
    public boolean isActive()     { return active; }

    @Override public String toString() { return name; }
}
