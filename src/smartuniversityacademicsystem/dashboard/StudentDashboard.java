package smartuniversityacademicsystem.dashboard;

import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import smartuniversityacademicsystem.db.StudentDAO;
import smartuniversityacademicsystem.db.TimetableDAO;
import smartuniversityacademicsystem.model.*;
import smartuniversityacademicsystem.view.LoginView;
import smartuniversityacademicsystem.view.TimetableGridView;

import java.util.List;

public class StudentDashboard {

    private final Stage        stage;
    private final User         user;
    private final StudentDAO   dao    = new StudentDAO();
    private final TimetableDAO ttDao  = new TimetableDAO();
    private final StackPane  contentArea = new StackPane();

    // sidebar button references for active state
    private Button activeBtn;

    public StudentDashboard(Stage stage, User user) {
        this.stage = stage;
        this.user  = user;
    }

    public void show() {
        stage.setTitle("SUAS – Student Portal");

        BorderPane root = new BorderPane();
        root.setLeft(buildSidebar());
        root.setCenter(contentArea);
        root.setStyle("-fx-background-color: #0F172A;");

        showHome();

        stage.setScene(new Scene(root, 960, 620));
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.show();
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────

    private VBox buildSidebar() {
        Label appTitle = new Label("SUAS");
        appTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        appTitle.setTextFill(Color.WHITE);

        Label appSub = new Label("Academic System");
        appSub.setFont(Font.font("Segoe UI", 11));
        appSub.setTextFill(Color.web("#64748B"));

        VBox brandBox = new VBox(2, appTitle, appSub);
        brandBox.setPadding(new Insets(24, 20, 20, 20));

        Separator sep1 = separator();

        // user info
        Label nameLabel = new Label(user.getFullName());
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        nameLabel.setTextFill(Color.web("#F1F5F9"));
        nameLabel.setWrapText(true);

        Label roleLabel = new Label("Student");
        roleLabel.setFont(Font.font("Segoe UI", 11));
        roleLabel.setTextFill(Color.web("#64748B"));

        VBox userBox = new VBox(3, nameLabel, roleLabel);
        userBox.setPadding(new Insets(10, 20, 16, 20));

        Separator sep2 = separator();

        // nav buttons
        Button homeBtn      = navButton("  Home",       "home");
        Button coursesBtn   = navButton("  My Courses", "courses");
        Button gradesBtn    = navButton("  My Grades",  "grades");
        Button timetableBtn = navButton("  Timetable",  "timetable");

        homeBtn.setOnAction(e      -> { setActive(homeBtn);      showHome(); });
        coursesBtn.setOnAction(e   -> { setActive(coursesBtn);   showCourses(); });
        gradesBtn.setOnAction(e    -> { setActive(gradesBtn);    showGrades(); });
        timetableBtn.setOnAction(e -> { setActive(timetableBtn); showTimetable(); });

        setActive(homeBtn);

        VBox navBox = new VBox(4, homeBtn, coursesBtn, gradesBtn, timetableBtn);
        navBox.setPadding(new Insets(8, 12, 8, 12));

        // spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Separator sep3 = separator();

        Button logoutBtn = new Button("  Logout");
        logoutBtn.setPrefWidth(176);
        logoutBtn.setPrefHeight(38);
        logoutBtn.setFont(Font.font("Segoe UI", 13));
        logoutBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #EF4444;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER-LEFT;"
        );
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(
            "-fx-background-color: #450A0A; -fx-text-fill: #FCA5A5;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER-LEFT;"
        ));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #EF4444;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER-LEFT;"
        ));
        logoutBtn.setOnAction(e -> new LoginView(stage).show());

        VBox logoutBox = new VBox(logoutBtn);
        logoutBox.setPadding(new Insets(8, 12, 16, 12));

        VBox sidebar = new VBox(brandBox, sep1, userBox, sep2, navBox, spacer, sep3, logoutBox);
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: #1E293B;");

        return sidebar;
    }

    private Button navButton(String text, String id) {
        Button btn = new Button(text);
        btn.setId(id);
        btn.setPrefWidth(176);
        btn.setPrefHeight(38);
        btn.setFont(Font.font("Segoe UI", 13));
        btn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #94A3B8;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER-LEFT;"
        );
        btn.setOnMouseEntered(e -> {
            if (btn != activeBtn) btn.setStyle(
                "-fx-background-color: #334155; -fx-text-fill: #F1F5F9;" +
                "-fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER-LEFT;"
            );
        });
        btn.setOnMouseExited(e -> {
            if (btn != activeBtn) btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #94A3B8;" +
                "-fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER-LEFT;"
            );
        });
        return btn;
    }

    private void setActive(Button btn) {
        if (activeBtn != null) activeBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #94A3B8;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER-LEFT;"
        );
        activeBtn = btn;
        btn.setStyle(
            "-fx-background-color: #2563EB; -fx-text-fill: white;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER-LEFT;"
        );
    }

    // ── Views ─────────────────────────────────────────────────────────────────

    private void showHome() {
        VBox pane = new VBox(20);
        pane.setPadding(new Insets(30));

        Label title = sectionTitle("Overview");

        // stats cards
        int enrolled = 0;
        double avg   = 0.0;
        try {
            enrolled = dao.getEnrolledCount(user.getId());
            avg      = dao.getAverageGrade(user.getId());
        } catch (Exception ex) { /* show zeros on error */ }

        HBox cards = new HBox(16,
            statCard("Enrolled Courses", String.valueOf(enrolled), "#2563EB"),
            statCard("Average Grade",    avg > 0 ? String.format("%.1f%%", avg) : "N/A", "#059669"),
            statCard("Academic Status",  avg >= 50 ? "Good Standing" : "At Risk",
                                         avg >= 50 ? "#7C3AED" : "#DC2626")
        );

        // recent grades preview
        Label recentTitle = sectionTitle("Recent Grades");
        TableView<Enrolment> table = buildGradesTable();
        loadGradesData(table);
        table.setMaxHeight(220);

        pane.getChildren().addAll(title, cards, recentTitle, table);
        setContent(pane);
    }

    private void showCourses() {
        VBox pane = new VBox(20);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("My Enrolled Courses"));

        TableView<Course> table = new TableView<>();
        table.setStyle(tableStyle());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Course, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        codeCol.setMaxWidth(100);

        TableColumn<Course, String> nameCol = new TableColumn<>("Course Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Course, String> lecCol = new TableColumn<>("Lecturer");
        lecCol.setCellValueFactory(new PropertyValueFactory<>("lecturerName"));

        table.getColumns().addAll(codeCol, nameCol, lecCol);
        styleTableColumns(table);

        new Thread(() -> {
            try {
                List<Course> courses = dao.getEnrolledCourses(user.getId());
                javafx.application.Platform.runLater(() ->
                    table.setItems(FXCollections.observableArrayList(courses))
                );
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> showDbError(pane, ex));
            }
        }).start();

        VBox.setVgrow(table, Priority.ALWAYS);
        pane.getChildren().add(table);
        setContent(pane);
    }

    private void showGrades() {
        VBox pane = new VBox(20);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("My Grades"));

        // GPA bar
        HBox gpaBox = new HBox();
        gpaBox.setAlignment(Pos.CENTER_LEFT);
        new Thread(() -> {
            try {
                double avg = dao.getAverageGrade(user.getId());
                javafx.application.Platform.runLater(() -> {
                    Label gpaLabel = new Label(
                        String.format("Overall Average:  %.1f%%   (%s)", avg, gpaLetterBand(avg))
                    );
                    gpaLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                    gpaLabel.setTextFill(Color.web("#38BDF8"));
                    gpaBox.getChildren().add(gpaLabel);
                });
            } catch (Exception ignored) {}
        }).start();

        TableView<Enrolment> table = buildGradesTable();
        loadGradesData(table);

        VBox.setVgrow(table, Priority.ALWAYS);
        pane.getChildren().addAll(gpaBox, table);
        setContent(pane);
    }

    private void showTimetable() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("Weekly Timetable"));

        // Semester filter
        ComboBox<Semester> semBox = new ComboBox<>();
        semBox.setPromptText("Select semester...");
        semBox.setStyle(
            "-fx-background-color: #1E293B; -fx-text-fill: #F1F5F9;" +
            "-fx-border-color: #475569; -fx-border-radius: 8; -fx-background-radius: 8;"
        );

        StackPane gridHolder = new StackPane();
        VBox.setVgrow(gridHolder, Priority.ALWAYS);

        semBox.setOnAction(e -> {
            Semester sem = semBox.getValue();
            if (sem == null) return;
            gridHolder.getChildren().clear();
            new Thread(() -> {
                try {
                    List<TimetableEntry> entries = ttDao.getTimetableForStudent(user.getId(), sem.getId());
                    javafx.application.Platform.runLater(() ->
                        gridHolder.getChildren().setAll(new TimetableGridView().build(entries))
                    );
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> showDbError(pane, ex));
                }
            }).start();
        });

        // Load semesters and auto-select active
        new Thread(() -> {
            try {
                List<Semester> semesters = ttDao.getSemesters();
                Semester active = ttDao.getActiveSemester();
                javafx.application.Platform.runLater(() -> {
                    semBox.getItems().setAll(semesters);
                    if (active != null) {
                        semBox.setValue(active);
                        semBox.fireEvent(new javafx.event.ActionEvent());
                    }
                });
            } catch (Exception ignored) {}
        }).start();

        pane.getChildren().addAll(semBox, gridHolder);
        setContent(pane);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void setContent(javafx.scene.Node node) {
        contentArea.getChildren().setAll(node);
    }

    private TableView<Enrolment> buildGradesTable() {
        TableView<Enrolment> table = new TableView<>();
        table.setStyle(tableStyle());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Enrolment, String> codeCol   = col("Code",   "courseCode",  90);
        TableColumn<Enrolment, String> nameCol   = col("Course", "courseName",   0);
        TableColumn<Enrolment, String> gradeCol  = col("Grade",  "gradeDisplay",100);
        TableColumn<Enrolment, String> letterCol = col("Letter", "letterGrade",  80);

        table.getColumns().addAll(codeCol, nameCol, gradeCol, letterCol);
        styleTableColumns(table);
        return table;
    }

    private void loadGradesData(TableView<Enrolment> table) {
        new Thread(() -> {
            try {
                List<Enrolment> grades = dao.getGrades(user.getId());
                javafx.application.Platform.runLater(() ->
                    table.setItems(FXCollections.observableArrayList(grades))
                );
            } catch (Exception ignored) {}
        }).start();
    }

    private <S, T> TableColumn<S, T> col(String title, String property, double maxWidth) {
        TableColumn<S, T> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        if (maxWidth > 0) col.setMaxWidth(maxWidth);
        return col;
    }

    private VBox statCard(String label, String value, String color) {
        Label valLabel = new Label(value);
        valLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        valLabel.setTextFill(Color.web(color));

        Label lbl = new Label(label);
        lbl.setFont(Font.font("Segoe UI", 12));
        lbl.setTextFill(Color.web("#94A3B8"));

        VBox card = new VBox(6, valLabel, lbl);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setStyle(
            "-fx-background-color: #1E293B;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: #334155;" +
            "-fx-border-radius: 12;"
        );
        return card;
    }

    private Label sectionTitle(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lbl.setTextFill(Color.web("#F1F5F9"));
        return lbl;
    }

    private Separator separator() {
        Separator s = new Separator();
        s.setStyle("-fx-background-color: #334155;");
        return s;
    }

    private String tableStyle() {
        return "-fx-background-color: #1E293B; -fx-text-fill: #F1F5F9;" +
               "-fx-border-color: #334155; -fx-border-radius: 8; -fx-background-radius: 8;";
    }

    private void styleTableColumns(TableView<?> table) {
        table.setStyle(tableStyle() + " -fx-font-size: 13px;");
    }

    private void showDbError(VBox pane, Exception ex) {
        Label err = new Label("Could not load data: " + ex.getMessage());
        err.setTextFill(Color.web("#FCA5A5"));
        pane.getChildren().add(err);
    }

    private String gpaLetterBand(double avg) {
        if (avg >= 90) return "A+";
        if (avg >= 85) return "A";
        if (avg >= 80) return "A-";
        if (avg >= 75) return "B+";
        if (avg >= 70) return "B";
        if (avg >= 65) return "B-";
        if (avg >= 60) return "C+";
        if (avg >= 55) return "C";
        if (avg >= 50) return "D";
        return "F";
    }
}
