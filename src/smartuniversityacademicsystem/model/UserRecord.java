package smartuniversityacademicsystem.model;

public class UserRecord {
    private int     id;
    private String  username;
    private String  fullName;
    private String  role;
    private boolean active;

    public UserRecord(int id, String username, String fullName, String role, boolean active) {
        this.id       = id;
        this.username = username;
        this.fullName = fullName;
        this.role     = role;
        this.active   = active;
    }

    public int     getId()        { return id; }
    public String  getUsername()  { return username; }
    public String  getFullName()  { return fullName; }
    public String  getRole()      { return role; }
    public boolean isActive()     { return active; }
    public void    setActive(boolean active) { this.active = active; }
    public String  getStatus()    { return active ? "Active" : "Inactive"; }
}
