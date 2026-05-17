package smartuniversityacademicsystem.dashboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import smartuniversityacademicsystem.db.LecturerDAO;
import smartuniversityacademicsystem.db.TimetableDAO;
import smartuniversityacademicsystem.model.*;
import smartuniversityacademicsystem.view.LoginView;
import smartuniversityacademicsystem.view.TimetableGridView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.stage.FileChooser;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import smartuniversityacademicsystem.db.AttendanceDAO;
import smartuniversityacademicsystem.db.EvaluationDAO;
import smartuniversityacademicsystem.model.EvaluationFeedback;
import smartuniversityacademicsystem.model.LecturerRating;
import smartuniversityacademicsystem.model.AttendanceRecord;
import smartuniversityacademicsystem.model.SessionRecord;
import smartuniversityacademicsystem.util.UIUtils;

public class LecturerDashboard {

    private final Stage       stage;
    private final User        user;
    private final LecturerDAO  dao   = new LecturerDAO();
    private final TimetableDAO  ttDao  = new TimetableDAO();
    private final AttendanceDAO  attDao  = new AttendanceDAO();
    private final EvaluationDAO  evalDao = new EvaluationDAO();
    private final StackPane   contentArea = new StackPane();
    private Button            activeBtn;

    public LecturerDashboard(Stage stage, User user) {
        this.stage = stage;
        this.user  = user;
    }

    public void show() {
        stage.setTitle("SUAS – Lecturer Portal");

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

        Label nameLabel = new Label(user.getFullName());
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        nameLabel.setTextFill(Color.web("#F1F5F9"));
        nameLabel.setWrapText(true);

        Label roleLabel = new Label("Lecturer");
        roleLabel.setFont(Font.font("Segoe UI", 11));
        roleLabel.setTextFill(Color.web("#64748B"));

        StackPane avatar = UIUtils.avatarCircle(user.getFullName(), "#059669");

        VBox userBox = new VBox(6, avatar, nameLabel, roleLabel);
        userBox.setPadding(new Insets(10, 20, 16, 20));

        Button homeBtn      = navButton("  Home");
        Button coursesBtn   = navButton("  My Courses");
        Button gradesBtn    = navButton("  Manage Grades");
        Button studentsBtn  = navButton("  All Students");
        Button timetableBtn = navButton("  My Timetable");

        homeBtn.setOnAction(e      -> { setActive(homeBtn);      showHome(); });
        coursesBtn.setOnAction(e   -> { setActive(coursesBtn);   showCourses(); });
        gradesBtn.setOnAction(e    -> { setActive(gradesBtn);    showGradeManager(); });
        studentsBtn.setOnAction(e  -> { setActive(studentsBtn);  showAllStudents(); });
        timetableBtn.setOnAction(e -> { setActive(timetableBtn); showTimetable(); });

        Button attendanceBtn  = navButton("  Attendance");
        Button evaluationsBtn = navButton("  My Evaluations");
        attendanceBtn.setOnAction(e  -> { setActive(attendanceBtn);  showAttendance(); });
        evaluationsBtn.setOnAction(e -> { setActive(evaluationsBtn); showMyEvaluations(); });

        setActive(homeBtn);

        VBox navBox = new VBox(4, homeBtn, coursesBtn, gradesBtn, studentsBtn, timetableBtn, attendanceBtn, evaluationsBtn);
        navBox.setPadding(new Insets(8, 12, 8, 12));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

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

        VBox sidebar = new VBox(
            UIUtils.sidebarAccent("#059669"), brandBox, separator(), userBox, separator(),
            navBox, spacer, separator(), logoutBox
        );
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: #1E293B;");
        return sidebar;
    }

    // ── Views ─────────────────────────────────────────────────────────────────

    private void showHome() {
        VBox pane = new VBox(20);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("Overview"));

        HBox cards = new HBox(16);
        pane.getChildren().add(cards);

        new Thread(() -> {
            try {
                int    courses  = dao.getCourseCount(user.getId());
                int    students = dao.getTotalStudents(user.getId());
                double avg      = dao.getOverallAverage(user.getId());
                javafx.application.Platform.runLater(() -> {
                    cards.getChildren().addAll(
                        statCard("Courses Teaching",  String.valueOf(courses),  "#2563EB"),
                        statCard("Total Students",    String.valueOf(students), "#059669"),
                        statCard("Class Average",
                            avg > 0 ? String.format("%.1f%%", avg) : "N/A",   "#7C3AED")
                    );
                    UIUtils.staggerIn(new ArrayList<javafx.scene.Node>(cards.getChildren()), 110);
                });
            } catch (Exception ignored) {}
        }).start();

        // courses preview table
        pane.getChildren().add(sectionTitle("My Courses"));
        TableView<Course> table = buildCoursesTable();
        loadCoursesData(table);
        table.setMaxHeight(220);
        pane.getChildren().add(table);

        setContent(pane);
    }

    private void showCourses() {
        VBox pane = new VBox(20);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("My Courses"));

        TableView<Course> table = buildCoursesTable();
        loadCoursesData(table);
        VBox.setVgrow(table, Priority.ALWAYS);
        pane.getChildren().add(table);
        setContent(pane);
    }

    private void showGradeManager() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("Manage Grades"));

        // Course selector
        ComboBox<Course> courseBox = new ComboBox<>();
        courseBox.setPromptText("Select a course...");
        courseBox.setPrefWidth(320);
        courseBox.setStyle(
            "-fx-background-color: #1E293B; -fx-text-fill: #F1F5F9;" +
            "-fx-border-color: #475569; -fx-border-radius: 8; -fx-background-radius: 8;"
        );
        courseBox.setCellFactory(lv -> courseCell());
        courseBox.setButtonCell(courseCell());

        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Segoe UI", 12));
        statusLabel.setVisible(false);

        // Grades table (editable)
        TableView<StudentRecord> table = new TableView<>();
        table.setEditable(true);
        table.setStyle(tableStyle() + " -fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setRowFactory(UIUtils.<StudentRecord>hoverRowFactory());

        TableColumn<StudentRecord, String> nameCol = new TableColumn<>("Student Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<StudentRecord, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        userCol.setMaxWidth(120);

        TableColumn<StudentRecord, String> gradeCol = new TableColumn<>("Grade (%) — click to edit");
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("gradeDisplay"));
        gradeCol.setEditable(true);
        gradeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        gradeCol.setMaxWidth(200);
        gradeCol.setOnEditCommit(event -> {
            StudentRecord sr = event.getRowValue();
            String raw = event.getNewValue().trim().replace("%", "");
            try {
                double val = Double.parseDouble(raw);
                if (val < 0 || val > 100) throw new NumberFormatException();
                sr.setGrade(val);
                new Thread(() -> {
                    try {
                        dao.updateGrade(sr.getStudentId(), sr.getCourseId(), val);
                        javafx.application.Platform.runLater(() -> {
                            showStatus(statusLabel, "Grade saved for " + sr.getFullName(), true);
                            table.refresh();
                        });
                    } catch (Exception ex) {
                        javafx.application.Platform.runLater(() ->
                            showStatus(statusLabel, "Save failed: " + ex.getMessage(), false)
                        );
                    }
                }).start();
            } catch (NumberFormatException e) {
                showStatus(statusLabel, "Invalid grade. Enter a number between 0 and 100.", false);
                table.refresh();
            }
        });

        table.getColumns().addAll(nameCol, userCol, gradeCol);

        // Load students when course is selected
        courseBox.setOnAction(e -> {
            Course selected = courseBox.getValue();
            if (selected == null) return;
            table.getItems().clear();
            new Thread(() -> {
                try {
                    List<StudentRecord> students = dao.getStudentsInCourse(selected.getId());
                    javafx.application.Platform.runLater(() -> {
                        table.setItems(FXCollections.observableArrayList(students));
                        if (students.isEmpty())
                            showStatus(statusLabel, "No students enrolled in this course.", true);
                        else
                            statusLabel.setVisible(false);
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() ->
                        showStatus(statusLabel, "Error loading students: " + ex.getMessage(), false)
                    );
                }
            }).start();
        });

        // populate course dropdown
        new Thread(() -> {
            try {
                List<Course> courses = dao.getCourses(user.getId());
                javafx.application.Platform.runLater(() ->
                    courseBox.setItems(FXCollections.observableArrayList(courses))
                );
            } catch (Exception ignored) {}
        }).start();

        Label hint = new Label("Double-click a grade cell to edit it, then press Enter to save.");
        hint.setFont(Font.font("Segoe UI", FontPosture.ITALIC, 12));
        hint.setTextFill(Color.web("#64748B"));

        HBox toolbar = new HBox(12, courseBox);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        VBox.setVgrow(table, Priority.ALWAYS);
        pane.getChildren().addAll(toolbar, hint, statusLabel, table);
        setContent(pane);
    }

    private void showAllStudents() {
        VBox pane = new VBox(20);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("All Enrolled Students"));

        TableView<StudentRecord> table = new TableView<>();
        table.setStyle(tableStyle() + " -fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setRowFactory(UIUtils.<StudentRecord>hoverRowFactory());

        TableColumn<StudentRecord, String> nameCol  = new TableColumn<>("Student Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<StudentRecord, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(new PropertyValueFactory<>("username")); // holds "CODE – username"
        courseCol.setMaxWidth(180);

        TableColumn<StudentRecord, String> gradeCol = new TableColumn<>("Grade");
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("gradeDisplay"));
        gradeCol.setMaxWidth(120);

        table.getColumns().addAll(nameCol, courseCol, gradeCol);

        new Thread(() -> {
            try {
                List<StudentRecord> students = dao.getAllStudents(user.getId());
                javafx.application.Platform.runLater(() ->
                    table.setItems(FXCollections.observableArrayList(students))
                );
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> {
                    Label err = new Label("Error: " + ex.getMessage());
                    err.setTextFill(Color.web("#FCA5A5"));
                    pane.getChildren().add(err);
                });
            }
        }).start();

        VBox.setVgrow(table, Priority.ALWAYS);
        pane.getChildren().add(table);
        setContent(pane);
    }

    private void showTimetable() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("My Teaching Timetable"));

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
                    List<TimetableEntry> entries = ttDao.getTimetableForLecturer(user.getId(), sem.getId());
                    javafx.application.Platform.runLater(() ->
                        gridHolder.getChildren().setAll(new TimetableGridView().buildWithFilter(entries))
                    );
                } catch (Exception ex) { /* ignore */ }
            }).start();
        });

        new Thread(() -> {
            try {
                List<Semester> semesters = ttDao.getSemesters();
                Semester active = ttDao.getActiveSemester();
                javafx.application.Platform.runLater(() -> {
                    semBox.getItems().setAll(semesters);
                    if (active != null) { semBox.setValue(active); semBox.fireEvent(new javafx.event.ActionEvent()); }
                });
            } catch (Exception ignored) {}
        }).start();

        pane.getChildren().addAll(semBox, gridHolder);
        setContent(pane);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private TableView<Course> buildCoursesTable() {
        TableView<Course> table = new TableView<>();
        table.setStyle(tableStyle() + " -fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setRowFactory(UIUtils.<Course>hoverRowFactory());

        TableColumn<Course, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        codeCol.setMaxWidth(100);

        TableColumn<Course, String> nameCol = new TableColumn<>("Course Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.getColumns().addAll(codeCol, nameCol);
        return table;
    }

    private void loadCoursesData(TableView<Course> table) {
        new Thread(() -> {
            try {
                List<Course> courses = dao.getCourses(user.getId());
                javafx.application.Platform.runLater(() ->
                    table.setItems(FXCollections.observableArrayList(courses))
                );
            } catch (Exception ignored) {}
        }).start();
    }

    private ListCell<Course> courseCell() {
        return new ListCell<Course>() {
            @Override
            protected void updateItem(Course c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getCode() + " – " + c.getName());
                setStyle("-fx-text-fill: #F1F5F9; -fx-background-color: #1E293B;");
            }
        };
    }

    private void showStatus(Label label, String msg, boolean success) {
        label.setText(msg);
        label.setTextFill(Color.web(success ? "#4ADE80" : "#FCA5A5"));
        label.setVisible(true);
    }

    private void setContent(javafx.scene.Node node) {
        contentArea.getChildren().setAll(node);
        UIUtils.animateIn(node);
    }

    private Button navButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(176);
        btn.setPrefHeight(38);
        btn.setFont(Font.font("Segoe UI", 13));
        btn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #94A3B8;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER-LEFT;"
        );
        btn.setOnMouseEntered(e -> { if (btn != activeBtn) btn.setStyle(
            "-fx-background-color: #334155; -fx-text-fill: #F1F5F9;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER-LEFT;"); });
        btn.setOnMouseExited(e  -> { if (btn != activeBtn) btn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #94A3B8;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-alignment: CENTER-LEFT;"); });
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

    // ── My Evaluations view ───────────────────────────────────────────────────

    private void showMyEvaluations() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("My Evaluations"));

        HBox statsRow   = new HBox(16);
        StackPane chartHolder = new StackPane();
        chartHolder.setMinHeight(230);

        // Feedback table (anonymous comments)
        TableView<EvaluationFeedback> feedbackTable = new TableView<>();
        feedbackTable.setStyle(tableStyle() + " -fx-font-size: 13px;");
        feedbackTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        feedbackTable.setRowFactory(UIUtils.<EvaluationFeedback>hoverRowFactory());
        VBox.setVgrow(feedbackTable, Priority.ALWAYS);

        TableColumn<EvaluationFeedback, String> ratCol = new TableColumn<>("Rating");
        ratCol.setCellValueFactory(new PropertyValueFactory<>("ratingDisplay"));
        ratCol.setMaxWidth(130);
        ratCol.setCellFactory(tc -> new TableCell<EvaluationFeedback, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setTextFill(Color.web("#F59E0B"));
            }
        });

        TableColumn<EvaluationFeedback, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("submittedDate"));
        dateCol.setMaxWidth(110);

        TableColumn<EvaluationFeedback, String> cmtCol = new TableColumn<>("Comment");
        cmtCol.setCellValueFactory(new PropertyValueFactory<>("comment"));

        feedbackTable.getColumns().addAll(ratCol, dateCol, cmtCol);

        new Thread(() -> {
            try {
                LecturerRating stats    = evalDao.getLecturerStats(user.getId());
                List<EvaluationFeedback> fb = evalDao.getLecturerFeedback(user.getId());
                javafx.application.Platform.runLater(() -> {
                    statsRow.getChildren().addAll(
                        statCard("Average Rating",     stats.getAvgRatingDisplay(),           "#F59E0B"),
                        statCard("Total Evaluations",  String.valueOf(stats.getTotalEvaluations()), "#2563EB"),
                        statCard("Overall Score",      stats.getStarsDisplay(),               "#059669")
                    );

                    // Rating distribution BarChart
                    CategoryAxis xAxis = new CategoryAxis();
                    NumberAxis   yAxis = new NumberAxis();
                    xAxis.setLabel("Stars");
                    yAxis.setLabel("Count");
                    BarChart<String, Number> chart = new BarChart<String, Number>(xAxis, yAxis);
                    chart.setTitle("Rating Distribution");
                    chart.setLegendVisible(false);
                    chart.setPrefHeight(220);
                    chart.setAnimated(false);

                    XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
                    series.getData().add(new XYChart.Data<String, Number>("1★", stats.getCount1()));
                    series.getData().add(new XYChart.Data<String, Number>("2★", stats.getCount2()));
                    series.getData().add(new XYChart.Data<String, Number>("3★", stats.getCount3()));
                    series.getData().add(new XYChart.Data<String, Number>("4★", stats.getCount4()));
                    series.getData().add(new XYChart.Data<String, Number>("5★", stats.getCount5()));
                    chart.getData().add(series);
                    chartHolder.getChildren().setAll(chart);

                    feedbackTable.setItems(FXCollections.observableArrayList(fb));
                });
            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> {
                    Label err = new Label("Error: " + ex.getMessage());
                    err.setTextFill(Color.web("#FCA5A5"));
                    statsRow.getChildren().add(err);
                });
            }
        }).start();

        pane.getChildren().addAll(
            statsRow,
            sectionTitle("Rating Distribution"),
            chartHolder,
            sectionTitle("Student Feedback (Anonymous)"),
            feedbackTable
        );
        setContent(pane);
    }

    // ── Attendance view ───────────────────────────────────────────────────────

    private void showAttendance() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("Manage Attendance"));

        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Segoe UI", 12));
        statusLabel.setVisible(false);
        statusLabel.setWrapText(true);

        // ── Attendance table ──────────────────────────────────────────────
        TableView<AttendanceRecord> attTable = new TableView<>();
        attTable.setStyle(tableStyle() + " -fx-font-size: 13px;");
        attTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        attTable.setRowFactory(UIUtils.<AttendanceRecord>hoverRowFactory());
        VBox.setVgrow(attTable, Priority.ALWAYS);

        TableColumn<AttendanceRecord, String> nameCol = new TableColumn<>("Student Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<AttendanceRecord, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        userCol.setMaxWidth(140);

        TableColumn<AttendanceRecord, String> statCol = new TableColumn<>("Status — click to toggle");
        statCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statCol.setMaxWidth(200);
        statCol.setCellFactory(tc -> new TableCell<AttendanceRecord, String>() {
            private final Button btn = new Button();
            {
                btn.setPrefWidth(110);
                btn.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
                btn.setOnAction(e -> {
                    AttendanceRecord rec = getTableRow().getItem();
                    if (rec == null) return;
                    rec.setStatus("PRESENT".equals(rec.getStatus()) ? "ABSENT" : "PRESENT");
                    getTableView().refresh();
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                btn.setText(item);
                btn.setStyle("PRESENT".equals(item)
                    ? "-fx-background-color: #166534; -fx-text-fill: #4ADE80; -fx-background-radius: 6; -fx-cursor: hand;"
                    : "-fx-background-color: #7F1D1D; -fx-text-fill: #FCA5A5; -fx-background-radius: 6; -fx-cursor: hand;"
                );
                setGraphic(btn);
                setText(null);
            }
        });

        attTable.getColumns().addAll(nameCol, userCol, statCol);

        // ── Session ComboBox ──────────────────────────────────────────────
        ComboBox<SessionRecord> sessionBox = new ComboBox<>();
        sessionBox.setPromptText("Select session...");
        sessionBox.setPrefWidth(260);
        sessionBox.setStyle(
            "-fx-background-color: #1E293B; -fx-text-fill: #F1F5F9;" +
            "-fx-border-color: #475569; -fx-border-radius: 8; -fx-background-radius: 8;"
        );
        sessionBox.setOnAction(e -> {
            SessionRecord sess = sessionBox.getValue();
            if (sess == null) return;
            attTable.getItems().clear();
            new Thread(() -> {
                try {
                    List<AttendanceRecord> recs = attDao.getSessionAttendance(sess.getId(), sess.getCourseId());
                    javafx.application.Platform.runLater(() -> {
                        attTable.setItems(FXCollections.observableArrayList(recs));
                        if (recs.isEmpty()) showStatus(statusLabel, "No students enrolled in this course.", true);
                        else statusLabel.setVisible(false);
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() ->
                        showStatus(statusLabel, "Error: " + ex.getMessage(), false));
                }
            }).start();
        });

        // ── Course ComboBox ───────────────────────────────────────────────
        ComboBox<Course> courseBox = new ComboBox<>();
        courseBox.setPromptText("Select course...");
        courseBox.setPrefWidth(280);
        courseBox.setStyle(
            "-fx-background-color: #1E293B; -fx-text-fill: #F1F5F9;" +
            "-fx-border-color: #475569; -fx-border-radius: 8; -fx-background-radius: 8;"
        );
        courseBox.setCellFactory(lv -> courseCell());
        courseBox.setButtonCell(courseCell());
        courseBox.setOnAction(e -> {
            Course course = courseBox.getValue();
            if (course == null) return;
            sessionBox.getItems().clear();
            attTable.getItems().clear();
            new Thread(() -> {
                try {
                    List<SessionRecord> sessions = attDao.getCourseSessions(course.getId());
                    javafx.application.Platform.runLater(() -> {
                        sessionBox.setItems(FXCollections.observableArrayList(sessions));
                        if (!sessions.isEmpty()) {
                            sessionBox.setValue(sessions.get(0));
                            sessionBox.fireEvent(new javafx.event.ActionEvent());
                        }
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() ->
                        showStatus(statusLabel, "Error: " + ex.getMessage(), false));
                }
            }).start();
        });

        new Thread(() -> {
            try {
                List<Course> courses = dao.getCourses(user.getId());
                javafx.application.Platform.runLater(() ->
                    courseBox.setItems(FXCollections.observableArrayList(courses)));
            } catch (Exception ignored) {}
        }).start();

        // ── Buttons ───────────────────────────────────────────────────────
        Button newSessionBtn = new Button("+ New Session");
        newSessionBtn.setStyle(
            "-fx-background-color: #059669; -fx-text-fill: white;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 6 12;"
        );
        newSessionBtn.setOnAction(e -> {
            Course course = courseBox.getValue();
            if (course == null) { showStatus(statusLabel, "Select a course first.", false); return; }
            showNewSessionDialog(course).ifPresent(vals -> {
                new Thread(() -> {
                    try {
                        int sid = attDao.createSession(course.getId(), vals[0], vals[1], user.getId());
                        List<SessionRecord> sessions = attDao.getCourseSessions(course.getId());
                        javafx.application.Platform.runLater(() -> {
                            sessionBox.setItems(FXCollections.observableArrayList(sessions));
                            sessions.stream().filter(s -> s.getId() == sid).findFirst().ifPresent(s -> {
                                sessionBox.setValue(s);
                                sessionBox.fireEvent(new javafx.event.ActionEvent());
                            });
                            showStatus(statusLabel, "Session created. Mark attendance and save.", true);
                        });
                    } catch (Exception ex) {
                        javafx.application.Platform.runLater(() ->
                            showStatus(statusLabel, "Error: " + ex.getMessage(), false));
                    }
                }).start();
            });
        });

        Button allPresentBtn = new Button("All Present");
        allPresentBtn.setStyle(
            "-fx-background-color: #166534; -fx-text-fill: #4ADE80;" +
            "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 12;"
        );
        allPresentBtn.setOnAction(e -> {
            attTable.getItems().forEach(r -> r.setStatus("PRESENT"));
            attTable.refresh();
        });

        Button allAbsentBtn = new Button("All Absent");
        allAbsentBtn.setStyle(
            "-fx-background-color: #7F1D1D; -fx-text-fill: #FCA5A5;" +
            "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 12;"
        );
        allAbsentBtn.setOnAction(e -> {
            attTable.getItems().forEach(r -> r.setStatus("ABSENT"));
            attTable.refresh();
        });

        Button saveBtn = new Button("Save Attendance");
        saveBtn.setStyle(
            "-fx-background-color: #2563EB; -fx-text-fill: white;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 13px; -fx-padding: 6 16;"
        );
        saveBtn.setOnAction(e -> {
            SessionRecord sess = sessionBox.getValue();
            if (sess == null) { showStatus(statusLabel, "Select a session first.", false); return; }
            List<AttendanceRecord> recs = new ArrayList<>(attTable.getItems());
            if (recs.isEmpty()) { showStatus(statusLabel, "No students to save.", false); return; }
            new Thread(() -> {
                try {
                    attDao.saveAllAttendance(sess.getId(), recs);
                    javafx.application.Platform.runLater(() ->
                        showStatus(statusLabel, "Attendance saved for " + recs.size() + " student(s).", true));
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() ->
                        showStatus(statusLabel, "Save failed: " + ex.getMessage(), false));
                }
            }).start();
        });

        Button exportBtn = new Button("Export CSV");
        exportBtn.setStyle(
            "-fx-background-color: #7C3AED; -fx-text-fill: white;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 13px; -fx-padding: 6 16;"
        );
        exportBtn.setOnAction(e -> {
            Course course = courseBox.getValue();
            if (course == null) { showStatus(statusLabel, "Select a course to export.", false); return; }
            FileChooser fc = new FileChooser();
            fc.setTitle("Save Attendance Report");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            fc.setInitialFileName("attendance_" + course.getCode() + ".csv");
            java.io.File file = fc.showSaveDialog(stage);
            if (file == null) return;
            new Thread(() -> {
                try {
                    String csv = attDao.exportCourseAttendanceCSV(course.getId(), course.getCode());
                    java.nio.file.Files.writeString(file.toPath(), csv);
                    javafx.application.Platform.runLater(() ->
                        showStatus(statusLabel, "Exported to " + file.getName(), true));
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() ->
                        showStatus(statusLabel, "Export failed: " + ex.getMessage(), false));
                }
            }).start();
        });

        // ── Layout ────────────────────────────────────────────────────────
        Label cLabel = new Label("Course:");
        cLabel.setTextFill(Color.web("#94A3B8"));
        cLabel.setFont(Font.font("Segoe UI", 13));

        Label sLabel = new Label("Session:");
        sLabel.setTextFill(Color.web("#94A3B8"));
        sLabel.setFont(Font.font("Segoe UI", 13));

        HBox selRow = new HBox(10, cLabel, courseBox, sLabel, sessionBox, newSessionBtn);
        selRow.setAlignment(Pos.CENTER_LEFT);

        HBox actRow = new HBox(10, allPresentBtn, allAbsentBtn, saveBtn, exportBtn);
        actRow.setAlignment(Pos.CENTER_LEFT);

        pane.getChildren().addAll(selRow, statusLabel, actRow, attTable);
        setContent(pane);
    }

    private Optional<String[]> showNewSessionDialog(Course course) {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("New Class Session");
        dialog.setHeaderText("Session for " + course.getCode() + " – " + course.getName());
        dialog.getDialogPane().setStyle("-fx-background-color: #1E293B;");

        ButtonType createBtn = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);

        javafx.scene.control.DatePicker datePicker =
            new javafx.scene.control.DatePicker(java.time.LocalDate.now());
        datePicker.setPrefWidth(220);
        datePicker.setStyle(
            "-fx-background-color: #0F172A; -fx-text-fill: #F1F5F9;" +
            "-fx-border-color: #334155; -fx-border-radius: 6; -fx-background-radius: 6;"
        );

        TextField topicField = new TextField();
        topicField.setPromptText("e.g. Introduction to SQL  (optional)");
        topicField.setPrefWidth(220);
        topicField.setStyle(
            "-fx-background-color: #0F172A; -fx-text-fill: #F1F5F9;" +
            "-fx-prompt-text-fill: #475569; -fx-border-color: #334155;" +
            "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 10;"
        );

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        Label dateLabel = new Label("Date");
        dateLabel.setTextFill(Color.web("#94A3B8"));
        dateLabel.setFont(Font.font("Segoe UI", 13));

        Label topicLabel = new Label("Topic");
        topicLabel.setTextFill(Color.web("#94A3B8"));
        topicLabel.setFont(Font.font("Segoe UI", 13));

        grid.add(dateLabel,  0, 0); grid.add(datePicker, 1, 0);
        grid.add(topicLabel, 0, 1); grid.add(topicField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == createBtn && datePicker.getValue() != null)
                return new String[]{datePicker.getValue().toString(), topicField.getText().trim()};
            return null;
        });

        return dialog.showAndWait().filter(v -> v != null);
    }
}
