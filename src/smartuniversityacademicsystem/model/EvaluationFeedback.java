package smartuniversityacademicsystem.model;

/** A single anonymous comment left by a student for a lecturer. */
public class EvaluationFeedback {

    private final int    rating;
    private final String submittedDate;
    private final String comment;

    public EvaluationFeedback(int rating, String submittedDate, String comment) {
        this.rating        = rating;
        this.submittedDate = submittedDate;
        this.comment       = comment != null ? comment : "";
    }

    public int    getRating()        { return rating; }
    public String getSubmittedDate() { return submittedDate; }
    public String getComment()       { return comment; }

    public String getRatingDisplay() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(i < rating ? "★" : "☆");
        return sb.toString();
    }
}
