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
import smartuniversityacademicsystem.db.CourseRegistrationDAO;
import smartuniversityacademicsystem.db.StudentDAO;
import smartuniversityacademicsystem.db.TimetableDAO;
import smartuniversityacademicsystem.model.*;
import smartuniversityacademicsystem.view.LoginView;
import smartuniversityacademicsystem.view.TimetableGridView;

import java.util.Arrays;
import java.util.List;
import javafx.animation.*;
import javafx.util.Duration;
import smartuniversityacademicsystem.db.AttendanceDAO;
import smartuniversityacademicsystem.db.EvaluationDAO;
import smartuniversityacademicsystem.model.AttendanceSummary;
import smartuniversityacademicsystem.model.EvaluationRecord;
import smartuniversityacademicsystem.util.UIUtils;

public class StudentDashboard {

    private final Stage        stage;
    private final User         user;
    private final StudentDAO             dao    = new StudentDAO();
    private final TimetableDAO           ttDao  = new TimetableDAO();
    private final CourseRegistrationDAO  regDao  = new CourseRegistrationDAO();
    private final AttendanceDAO          attDao   = new AttendanceDAO();
    private final EvaluationDAO          evalDao  = new EvaluationDAO();
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

        StackPane avatar = UIUtils.avatarCircle(user.getFullName(), "#2563EB");

        VBox userBox = new VBox(6, avatar, nameLabel, roleLabel);
        userBox.setPadding(new Insets(10, 20, 16, 20));

        Separator sep2 = separator();

        // nav buttons
        Button homeBtn       = navButton("  Home",           "home");
        Button registerBtn   = navButton("  Registration",   "register");
        Button coursesBtn    = navButton("  My Courses",     "courses");
        Button gradesBtn     = navButton("  My Grades",      "grades");
        Button timetableBtn  = navButton("  Timetable",      "timetable");
        Button attendanceBtn = navButton("  My Attendance",  "attendance");
        Button evaluateBtn   = navButton("  Evaluate",       "evaluate");

        homeBtn.setOnAction(e       -> { setActive(homeBtn);       showHome(); });
        registerBtn.setOnAction(e   -> { setActive(registerBtn);   showRegistration(); });
        coursesBtn.setOnAction(e    -> { setActive(coursesBtn);    showCourses(); });
        gradesBtn.setOnAction(e     -> { setActive(gradesBtn);     showGrades(); });
        timetableBtn.setOnAction(e  -> { setActive(timetableBtn);  showTimetable(); });
        attendanceBtn.setOnAction(e -> { setActive(attendanceBtn); showAttendance(); });
        evaluateBtn.setOnAction(e   -> { setActive(evaluateBtn);   showEvaluate(); });

        setActive(homeBtn);

        VBox navBox = new VBox(4, homeBtn, registerBtn, coursesBtn, gradesBtn, timetableBtn, attendanceBtn, evaluateBtn);
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

        VBox sidebar = new VBox(UIUtils.sidebarAccent("#2563EB"), brandBox, sep1, userBox, sep2, navBox, spacer, sep3, logoutBox);
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

    private void showRegistration() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("Course Registration"));

        // ── Credit summary label ──────────────────────────────────────────────
        Label creditLabel = new Label("Loading credits...");
        creditLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        creditLabel.setTextFill(Color.web("#38BDF8"));

        // ── Available courses table ───────────────────────────────────────────
        Label availTitle = new Label("Available Courses");
        availTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        availTitle.setTextFill(Color.web("#94A3B8"));

        TableView<AvailableCourse> availTable = new TableView<>();
        availTable.setStyle(tableStyle() + " -fx-font-size: 13px;");
        availTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        availTable.setPrefHeight(220);

        TableColumn<AvailableCourse, String> acCode  = new TableColumn<>("Code");
        acCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        acCode.setMaxWidth(80);

        TableColumn<AvailableCourse, String> acName  = new TableColumn<>("Course Name");
        acName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<AvailableCourse, String> acLec   = new TableColumn<>("Lecturer");
        acLec.setCellValueFactory(new PropertyValueFactory<>("lecturerName"));
        acLec.setMaxWidth(150);

        TableColumn<AvailableCourse, String> acCred  = new TableColumn<>("Credits");
        acCred.setCellValueFactory(new PropertyValueFactory<>("credits"));
        acCred.setMaxWidth(70);

        TableColumn<AvailableCourse, String> acCap   = new TableColumn<>("Capacity");
        acCap.setCellValueFactory(new PropertyValueFactory<>("capacityDisplay"));
        acCap.setMaxWidth(90);

        TableColumn<AvailableCourse, String> acStat  = new TableColumn<>("Status");
        acStat.setCellValueFactory(new PropertyValueFactory<>("statusDisplay"));
        acStat.setMaxWidth(100);
        acStat.setCellFactory(tc -> new TableCell<AvailableCourse, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setTextFill(item.equals("Full") ? Color.web("#FCA5A5") : Color.web("#4ADE80"));
            }
        });

        availTable.getColumns().addAll(acCode, acName, acLec, acCred, acCap, acStat);

        // ── Enrolled courses table ────────────────────────────────────────────
        Label enrolledTitle = new Label("My Enrolled Courses");
        enrolledTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        enrolledTitle.setTextFill(Color.web("#94A3B8"));

        TableView<Enrolment> enrolledTable = new TableView<>();
        enrolledTable.setStyle(tableStyle() + " -fx-font-size: 13px;");
        enrolledTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        enrolledTable.setPrefHeight(180);

        TableColumn<Enrolment, String> ecCode  = new TableColumn<>("Code");
        ecCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        ecCode.setMaxWidth(80);

        TableColumn<Enrolment, String> ecName  = new TableColumn<>("Course (Credits)");
        ecName.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        TableColumn<Enrolment, String> ecGrade = new TableColumn<>("Grade");
        ecGrade.setCellValueFactory(new PropertyValueFactory<>("gradeDisplay"));
        ecGrade.setMaxWidth(100);

        enrolledTable.getColumns().addAll(ecCode, ecName, ecGrade);

        // ── Status label ──────────────────────────────────────────────────────
        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Segoe UI", 12));
        statusLabel.setWrapText(true);
        statusLabel.setVisible(false);

        // ── Buttons ───────────────────────────────────────────────────────────
        Button registerBtn = new Button("Register Selected");
        registerBtn.setStyle(
            "-fx-background-color: #2563EB; -fx-text-fill: white;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 13px; -fx-padding: 6 16;"
        );

        Button dropBtn = new Button("Drop Selected");
        dropBtn.setStyle(
            "-fx-background-color: #DC2626; -fx-text-fill: white;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 13px; -fx-padding: 6 16;"
        );

        HBox btnBar = new HBox(10, registerBtn, dropBtn);
        btnBar.setAlignment(Pos.CENTER_LEFT);

        // ── Data loader helper ────────────────────────────────────────────────
        Runnable refreshAll = () -> new Thread(() -> {
            try {
                List<AvailableCourse> avail    = regDao.getAvailableCourses(user.getId());
                List<Enrolment>       enrolled = regDao.getEnrolledCoursesWithCredits(user.getId());
                int                   credits  = regDao.getTotalCredits(user.getId());
                javafx.application.Platform.runLater(() -> {
                    availTable.setItems(FXCollections.observableArrayList(avail));
                    enrolledTable.setItems(FXCollections.observableArrayList(enrolled));
                    creditLabel.setText("Enrolled Credits: " + credits + " / 21");
                    statusLabel.setVisible(false);
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() ->
                    showDbError(pane, ex)
                );
            }
        }).start();

        refreshAll.run();

        // ── Register action ───────────────────────────────────────────────────
        registerBtn.setOnAction(e -> {
            AvailableCourse selected = availTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showRegStatus(statusLabel, "Select a course from the Available list first.", false);
                return;
            }
            new Thread(() -> {
                try {
                    String error = regDao.checkRegistration(user.getId(), selected.getId());
                    javafx.application.Platform.runLater(() -> {
                        if (error != null) {
                            showRegStatus(statusLabel, error, false);
                        } else {
                            try {
                                regDao.registerCourse(user.getId(), selected.getId());
                                showRegStatus(statusLabel,
                                    "Successfully registered for " + selected.getCode() + ".", true);
                                refreshAll.run();
                            } catch (Exception ex) {
                                showRegStatus(statusLabel, "Error: " + ex.getMessage(), false);
                            }
                        }
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() ->
                        showRegStatus(statusLabel, "Error: " + ex.getMessage(), false)
                    );
                }
            }).start();
        });

        // ── Drop action ───────────────────────────────────────────────────────
        dropBtn.setOnAction(e -> {
            Enrolment selected = enrolledTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showRegStatus(statusLabel, "Select a course from My Enrolled list to drop.", false);
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Drop " + selected.getCourseCode() + "? Your grade will be lost.",
                ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Confirm Drop");
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    new Thread(() -> {
                        try {
                            regDao.dropCourse(user.getId(), selected.getCourseCode());
                            javafx.application.Platform.runLater(() -> {
                                showRegStatus(statusLabel,
                                    selected.getCourseCode() + " dropped successfully.", true);
                                refreshAll.run();
                            });
                        } catch (Exception ex) {
                            javafx.application.Platform.runLater(() ->
                                showRegStatus(statusLabel, "Error: " + ex.getMessage(), false)
                            );
                        }
                    }).start();
                }
            });
        });

        pane.getChildren().addAll(
            creditLabel,
            availTitle, availTable,
            enrolledTitle, enrolledTable,
            btnBar, statusLabel
        );
        setContent(pane);
    }

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
        UIUtils.staggerIn(new java.util.ArrayList<javafx.scene.Node>(cards.getChildren()), 110);

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
                        gridHolder.getChildren().setAll(new TimetableGridView().buildWithFilter(entries))
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
        UIUtils.animateIn(node);
    }

    private TableView<Enrolment> buildGradesTable() {
        TableView<Enrolment> table = new TableView<>();
        table.setStyle(tableStyle());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setRowFactory(UIUtils.<Enrolment>hoverRowFactory());

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
        return UIUtils.statCard(label, value, color, 200);
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

    private void showRegStatus(Label label, String msg, boolean success) {
        label.setText(msg);
        label.setTextFill(Color.web(success ? "#4ADE80" : "#FCA5A5"));
        label.setVisible(true);
    }

    private void showDbError(VBox pane, Exception ex) {
        Label err = new Label("Could not load data: " + ex.getMessage());
        err.setTextFill(Color.web("#FCA5A5"));
        pane.getChildren().add(err);
    }

    // ── Attendance view ───────────────────────────────────────────────────────

    private void showAttendance() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("My Attendance"));

        Label warningLabel = new Label();
        warningLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        warningLabel.setTextFill(Color.web("#FCA5A5"));
        warningLabel.setWrapText(true);
        warningLabel.setPadding(new Insets(10, 14, 10, 14));
        warningLabel.setStyle("-fx-background-color: #450A0A; -fx-background-radius: 8;");
        warningLabel.setVisible(false);

        TableView<AttendanceSummary> table = new TableView<>();
        table.setStyle(tableStyle() + " -fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setRowFactory(UIUtils.<AttendanceSummary>hoverRowFactory());
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<AttendanceSummary, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        codeCol.setMaxWidth(90);

        TableColumn<AttendanceSummary, String> nameCol = new TableColumn<>("Course");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        TableColumn<AttendanceSummary, String> attCol = new TableColumn<>("Attended");
        attCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getAttended())));
        attCol.setMaxWidth(90);

        TableColumn<AttendanceSummary, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getTotal())));
        totalCol.setMaxWidth(80);

        TableColumn<AttendanceSummary, String> pctCol = new TableColumn<>("%");
        pctCol.setCellValueFactory(new PropertyValueFactory<>("percentageDisplay"));
        pctCol.setMaxWidth(80);

        TableColumn<AttendanceSummary, Double> barCol = new TableColumn<>("Attendance");
        barCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPercentage()).asObject());
        barCol.setMinWidth(150);
        barCol.setCellFactory(tc -> new TableCell<AttendanceSummary, Double>() {
            private final ProgressBar bar = new ProgressBar(0);
            { bar.setPrefWidth(130); bar.setPrefHeight(16); }
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                double p = item / 100.0;
                bar.setStyle(p < 0.75 ? "-fx-accent: #EF4444;" : "-fx-accent: #22C55E;");
                UIUtils.animateProgress(bar, p);
                setGraphic(bar);
            }
        });

        TableColumn<AttendanceSummary, String> warnCol = new TableColumn<>("Status");
        warnCol.setCellValueFactory(new PropertyValueFactory<>("warning"));
        warnCol.setMaxWidth(180);
        warnCol.setCellFactory(tc -> new TableCell<AttendanceSummary, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setTextFill(item.startsWith("WARNING") ? Color.web("#FCA5A5") : Color.web("#4ADE80"));
            }
        });

        table.getColumns().addAll(codeCol, nameCol, attCol, totalCol, pctCol, barCol, warnCol);

        new Thread(() -> {
            try {
                List<AttendanceSummary> summaries = attDao.getStudentAttendanceSummary(user.getId());
                javafx.application.Platform.runLater(() -> {
                    table.setItems(FXCollections.observableArrayList(summaries));
                    long below = summaries.stream()
                        .filter(s -> s.getTotal() > 0 && s.getPercentage() < 75)
                        .count();
                    if (below > 0) {
                        warningLabel.setText(
                            "⚠  Warning: " + below + " course(s) below the 75% attendance threshold. " +
                            "You may be barred from sitting exams."
                        );
                        warningLabel.setVisible(true);
                    }
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> showDbError(pane, ex));
            }
        }).start();

        pane.getChildren().addAll(warningLabel, table);
        setContent(pane);
    }

    // ── Evaluate view ─────────────────────────────────────────────────────────

    private void showEvaluate() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("Evaluate Lecturers"));

        Label hint = new Label("Rate your lecturers based on teaching performance. Evaluations are anonymous.");
        hint.setFont(Font.font("Segoe UI", FontPosture.ITALIC, 12));
        hint.setTextFill(Color.web("#64748B"));
        hint.setWrapText(true);

        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Segoe UI", 12));
        statusLabel.setVisible(false);
        statusLabel.setWrapText(true);

        // ── Course list table ─────────────────────────────────────────────
        TableView<EvaluationRecord> table = new TableView<>();
        table.setStyle(tableStyle() + " -fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setRowFactory(UIUtils.<EvaluationRecord>hoverRowFactory());
        table.setPrefHeight(200);

        TableColumn<EvaluationRecord, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        codeCol.setMaxWidth(80);

        TableColumn<EvaluationRecord, String> nameCol = new TableColumn<>("Course");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        TableColumn<EvaluationRecord, String> lecCol = new TableColumn<>("Lecturer");
        lecCol.setCellValueFactory(new PropertyValueFactory<>("lecturerName"));
        lecCol.setMaxWidth(160);

        TableColumn<EvaluationRecord, String> ratingCol = new TableColumn<>("Your Rating");
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("ratingDisplay"));
        ratingCol.setMaxWidth(200);
        ratingCol.setCellFactory(tc -> new TableCell<EvaluationRecord, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setTextFill("Not yet rated".equals(item) ? Color.web("#64748B") : Color.web("#F59E0B"));
            }
        });

        table.getColumns().addAll(codeCol, nameCol, lecCol, ratingCol);

        // ── Evaluation form ───────────────────────────────────────────────
        VBox formCard = new VBox(12);
        formCard.setPadding(new Insets(16));
        formCard.setStyle(
            "-fx-background-color: #1E293B; -fx-background-radius: 12;" +
            "-fx-border-color: #334155; -fx-border-radius: 12;"
        );
        formCard.setVisible(false);
        formCard.setManaged(false);

        Label formTitle = new Label();
        formTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        formTitle.setTextFill(Color.web("#F1F5F9"));

        Label formLecturer = new Label();
        formLecturer.setFont(Font.font("Segoe UI", 13));
        formLecturer.setTextFill(Color.web("#94A3B8"));

        Label ratingPrompt = new Label("Your Rating:");
        ratingPrompt.setTextFill(Color.web("#94A3B8"));
        ratingPrompt.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));

        // Star widget
        Label[] starLabels = new Label[5];
        int[]   selectedRating = {0};

        HBox starBox = new HBox(6);
        starBox.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < 5; i++) {
            final int val = i + 1;
            Label star = new Label("☆");
            star.setFont(Font.font("Segoe UI", 34));
            star.setTextFill(Color.web("#F59E0B"));
            star.setStyle("-fx-cursor: hand;");
            starLabels[i] = star;
            star.setOnMouseEntered(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), star);
                st.setToX(1.35); st.setToY(1.35); st.play();
                updateStarDisplay(starLabels, val);
            });
            star.setOnMouseExited(e -> {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), star);
                st.setToX(1.0); st.setToY(1.0); st.play();
            });
            star.setOnMouseClicked(e -> { selectedRating[0] = val; updateStarDisplay(starLabels, val); });
            starBox.getChildren().add(star);
        }
        starBox.setOnMouseExited(e -> updateStarDisplay(starLabels, selectedRating[0]));

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Add a comment (optional)...");
        commentArea.setPrefRowCount(3);
        commentArea.setStyle(
            "-fx-control-inner-background: #0F172A; -fx-text-fill: #F1F5F9;" +
            "-fx-prompt-text-fill: #475569; -fx-border-color: #334155;" +
            "-fx-border-radius: 6; -fx-background-radius: 6;"
        );

        Button submitBtn = new Button("Submit Evaluation");
        submitBtn.setStyle(
            "-fx-background-color: #2563EB; -fx-text-fill: white;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 13px; -fx-padding: 6 18;"
        );

        formCard.getChildren().addAll(formTitle, formLecturer, ratingPrompt, starBox, commentArea, submitBtn);

        // Row selection → populate form
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, rec) -> {
            if (rec == null) { formCard.setVisible(false); formCard.setManaged(false); return; }
            formCard.setVisible(true);
            formCard.setManaged(true);
            formTitle.setText("Evaluating: " + rec.getCourseCode() + " — " + rec.getCourseName());
            formLecturer.setText("Lecturer: " + rec.getLecturerName());
            selectedRating[0] = rec.getCurrentRating();
            updateStarDisplay(starLabels, selectedRating[0]);
            commentArea.setText(rec.getCurrentComment());
            submitBtn.setText(rec.isSubmitted() ? "Update Evaluation" : "Submit Evaluation");
            statusLabel.setVisible(false);
        });

        submitBtn.setOnAction(e -> {
            EvaluationRecord rec = table.getSelectionModel().getSelectedItem();
            if (rec == null) return;
            if (selectedRating[0] == 0) {
                showRegStatus(statusLabel, "Please select a star rating (1–5).", false);
                return;
            }
            if (rec.getLecturerId() == 0) {
                showRegStatus(statusLabel, "This course has no assigned lecturer to evaluate.", false);
                return;
            }
            int    rating  = selectedRating[0];
            String comment = commentArea.getText().trim();
            new Thread(() -> {
                try {
                    evalDao.submitEvaluation(user.getId(), rec.getCourseId(), rec.getLecturerId(), rating, comment);
                    javafx.application.Platform.runLater(() -> {
                        rec.setCurrentRating(rating);
                        rec.setCurrentComment(comment);
                        table.refresh();
                        submitBtn.setText("Update Evaluation");
                        statusLabel.setVisible(false);
                        UIUtils.toast(contentArea, "Evaluation submitted successfully!", true);
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() ->
                        showRegStatus(statusLabel, "Error: " + ex.getMessage(), false));
                }
            }).start();
        });

        // Load enrolled courses with evaluation status
        new Thread(() -> {
            try {
                List<EvaluationRecord> records = evalDao.getStudentEvaluations(user.getId());
                javafx.application.Platform.runLater(() ->
                    table.setItems(FXCollections.observableArrayList(records)));
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> showDbError(pane, ex));
            }
        }).start();

        VBox.setVgrow(table, Priority.SOMETIMES);
        pane.getChildren().addAll(hint, statusLabel, table, formCard);
        setContent(pane);
    }

    private void updateStarDisplay(Label[] labels, int rating) {
        for (int i = 0; i < labels.length; i++) {
            labels[i].setText(i < rating ? "★" : "☆");
        }
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
