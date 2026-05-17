package smartuniversityacademicsystem.model;

import javafx.beans.property.SimpleStringProperty;

/** Represents one student's attendance status within a single class session. */
public class AttendanceRecord {

    private final int studentId;
    private final SimpleStringProperty fullName;
    private final SimpleStringProperty username;
    private final SimpleStringProperty status;   // "PRESENT" or "ABSENT"

    public AttendanceRecord(int studentId, String fullName, String username, String status) {
        this.studentId = studentId;
        this.fullName  = new SimpleStringProperty(fullName);
        this.username  = new SimpleStringProperty(username);
        this.status    = new SimpleStringProperty(status != null ? status : "ABSENT");
    }

    public int    getStudentId() { return studentId; }
    public String getFullName()  { return fullName.get(); }
    public String getUsername()  { return username.get(); }
    public String getStatus()    { return status.get(); }
    public void   setStatus(String s) { status.set(s); }

    public SimpleStringProperty fullNameProperty() { return fullName; }
    public SimpleStringProperty usernameProperty() { return username; }
    public SimpleStringProperty statusProperty()   { return status; }
}
