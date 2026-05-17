package smartuniversityacademicsystem.model;

public class User {

    private int id;
    private String username;
    private String fullName;
    private Role role;

    public User(int id, String username, String fullName, Role role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public int getId()         { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public Role getRole()       { return role; }
}
