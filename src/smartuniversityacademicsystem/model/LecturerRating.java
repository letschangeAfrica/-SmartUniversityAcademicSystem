package smartuniversityacademicsystem.model;

/** Aggregated evaluation stats for one lecturer. */
public class LecturerRating {

    private final int    lecturerId;
    private final String lecturerName;
    private final double avgRating;
    private final int    totalEvaluations;
    private final int    count1, count2, count3, count4, count5;

    public LecturerRating(int lecturerId, String lecturerName, double avgRating,
                          int total, int c1, int c2, int c3, int c4, int c5) {
        this.lecturerId       = lecturerId;
        this.lecturerName     = lecturerName;
        this.avgRating        = avgRating;
        this.totalEvaluations = total;
        this.count1 = c1; this.count2 = c2; this.count3 = c3;
        this.count4 = c4; this.count5 = c5;
    }

    public int    getLecturerId()       { return lecturerId; }
    public String getLecturerName()     { return lecturerName; }
    public double getAvgRating()        { return avgRating; }
    public int    getTotalEvaluations() { return totalEvaluations; }
    public int    getCount1()           { return count1; }
    public int    getCount2()           { return count2; }
    public int    getCount3()           { return count3; }
    public int    getCount4()           { return count4; }
    public int    getCount5()           { return count5; }

    public String getAvgRatingDisplay() {
        return totalEvaluations == 0 ? "No ratings yet"
                                     : String.format("%.1f / 5.0", avgRating);
    }

    public String getStarsDisplay() {
        if (totalEvaluations == 0) return "—";
        int rounded = (int) Math.round(avgRating);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(i < rounded ? "★" : "☆");
        return sb.toString();
    }
}
