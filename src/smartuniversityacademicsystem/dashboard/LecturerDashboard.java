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
import smartuniversityacademicsystem.model.*;
import smartuniversityacademicsystem.view.LoginView;

import java.util.List;

public class LecturerDashboard {

    private final Stage       stage;
    private final User        user;
    private final LecturerDAO dao = new LecturerDAO();
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

        VBox userBox = new VBox(3, nameLabel, roleLabel);
        userBox.setPadding(new Insets(10, 20, 16, 20));

        Button homeBtn     = navButton("  Home");
        Button coursesBtn  = navButton("  My Courses");
        Button gradesBtn   = navButton("  Manage Grades");
        Button studentsBtn = navButton("  All Students");

        homeBtn.setOnAction(e     -> { setActive(homeBtn);     showHome(); });
        coursesBtn.setOnAction(e  -> { setActive(coursesBtn);  showCourses(); });
        gradesBtn.setOnAction(e   -> { setActive(gradesBtn);   showGradeManager(); });
        studentsBtn.setOnAction(e -> { setActive(studentsBtn); showAllStudents(); });

        setActive(homeBtn);

        VBox navBox = new VBox(4, homeBtn, coursesBtn, gradesBtn, studentsBtn);
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
            brandBox, separator(), userBox, separator(),
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
                javafx.application.Platform.runLater(() -> cards.getChildren().addAll(
                    statCard("Courses Teaching",  String.valueOf(courses),  "#2563EB"),
                    statCard("Total Students",    String.valueOf(students), "#059669"),
                    statCard("Class Average",
                        avg > 0 ? String.format("%.1f%%", avg) : "N/A",   "#7C3AED")
                ));
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
        hint.setFont(Font.font("Segoe UI", FontStyle.ITALIC, 12));
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

    // ── Helpers ───────────────────────────────────────────────────────────────

    private TableView<Course> buildCoursesTable() {
        TableView<Course> table = new TableView<>();
        table.setStyle(tableStyle() + " -fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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
            "-fx-background-color: #1E293B; -fx-background-radius: 12;" +
            "-fx-border-color: #334155; -fx-border-radius: 12;"
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
}
