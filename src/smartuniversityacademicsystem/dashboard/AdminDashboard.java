package smartuniversityacademicsystem.dashboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import smartuniversityacademicsystem.db.AdminDAO;
import smartuniversityacademicsystem.db.AttendanceDAO;
import smartuniversityacademicsystem.db.TimetableDAO;
import smartuniversityacademicsystem.model.*;
import smartuniversityacademicsystem.scheduling.TimetableGenerator;
import smartuniversityacademicsystem.view.LoginView;
import smartuniversityacademicsystem.view.TimetableGridView;

import java.util.List;
import java.util.Optional;

public class AdminDashboard {

    private final Stage    stage;
    private final User     user;
    private final AdminDAO     dao   = new AdminDAO();
    private final TimetableDAO  ttDao  = new TimetableDAO();
    private final AttendanceDAO attDao = new AttendanceDAO();
    private final StackPane contentArea = new StackPane();
    private Button activeBtn;

    public AdminDashboard(Stage stage, User user) {
        this.stage = stage;
        this.user  = user;
    }

    public void show() {
        stage.setTitle("SUAS – Admin Portal");

        BorderPane root = new BorderPane();
        root.setLeft(buildSidebar());
        root.setCenter(contentArea);
        root.setStyle("-fx-background-color: #0F172A;");

        showHome();

        stage.setScene(new Scene(root, 1020, 660));
        stage.setMinWidth(860);
        stage.setMinHeight(520);
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

        Label roleLabel = new Label("Administrator");
        roleLabel.setFont(Font.font("Segoe UI", 11));
        roleLabel.setTextFill(Color.web("#64748B"));

        VBox userBox = new VBox(3, nameLabel, roleLabel);
        userBox.setPadding(new Insets(10, 20, 16, 20));

        Button homeBtn       = navButton("  Home");
        Button usersBtn      = navButton("  User Management");
        Button coursesBtn    = navButton("  Course Management");
        Button timetableBtn  = navButton("  Timetable");
        Button reportsBtn    = navButton("  Reports");
        Button attendanceBtn = navButton("  Attendance");

        homeBtn.setOnAction(e       -> { setActive(homeBtn);       showHome(); });
        usersBtn.setOnAction(e      -> { setActive(usersBtn);      showUsers(); });
        coursesBtn.setOnAction(e    -> { setActive(coursesBtn);    showCourses(); });
        timetableBtn.setOnAction(e  -> { setActive(timetableBtn);  showTimetableManager(); });
        reportsBtn.setOnAction(e    -> { setActive(reportsBtn);    showReports(); });
        attendanceBtn.setOnAction(e -> { setActive(attendanceBtn); showAttendanceOverview(); });

        setActive(homeBtn);

        VBox navBox = new VBox(4, homeBtn, usersBtn, coursesBtn, timetableBtn, reportsBtn, attendanceBtn);
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
        sidebar.setPrefWidth(210);
        sidebar.setStyle("-fx-background-color: #1E293B;");
        return sidebar;
    }

    // ── Views ─────────────────────────────────────────────────────────────────

    private void showHome() {
        VBox pane = new VBox(24);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("System Overview"));

        HBox statsRow = new HBox(14);
        pane.getChildren().add(statsRow);

        new Thread(() -> {
            try {
                int[] s = dao.getSystemStats(); // [students, lecturers, admins, courses, enrolments]
                javafx.application.Platform.runLater(() -> statsRow.getChildren().addAll(
                    statCard("Students",    String.valueOf(s[0]), "#2563EB"),
                    statCard("Lecturers",   String.valueOf(s[1]), "#059669"),
                    statCard("Admins",      String.valueOf(s[2]), "#7C3AED"),
                    statCard("Courses",     String.valueOf(s[3]), "#D97706"),
                    statCard("Enrolments",  String.valueOf(s[4]), "#0891B2")
                ));
            } catch (Exception ignored) {}
        }).start();

        // quick reports preview
        pane.getChildren().add(sectionTitle("Course Enrolment Summary"));
        TableView<CourseReport> table = buildReportTable();
        table.setMaxHeight(280);
        loadReportData(table);
        pane.getChildren().add(table);

        setContent(pane);
    }

    // ── User Management ───────────────────────────────────────────────────────

    private void showUsers() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("User Management"));

        ObservableList<UserRecord> data = FXCollections.observableArrayList();

        TableView<UserRecord> table = new TableView<>(data);
        table.setStyle(tableStyle() + " -fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<UserRecord, String>  unCol     = col("Username",  "username",  130);
        TableColumn<UserRecord, String>  nameCol   = col("Full Name", "fullName",    0);
        TableColumn<UserRecord, String>  roleCol   = col("Role",      "role",       100);
        TableColumn<UserRecord, String>  statusCol = col("Status",    "status",      90);

        // colour-code the status cell
        statusCol.setCellFactory(tc -> new TableCell<UserRecord, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setTextFill("Active".equals(item) ? Color.web("#4ADE80") : Color.web("#FCA5A5"));
            }
        });

        table.getColumns().addAll(unCol, nameCol, roleCol, statusCol);

        // toolbar
        Button addBtn    = actionButton("+ Add User",   "#2563EB");
        Button toggleBtn = actionButton("Toggle Active","#D97706");

        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Segoe UI", 12));
        statusLabel.setVisible(false);

        addBtn.setOnAction(e -> {
            showAddUserDialog().ifPresent(values -> {
                new Thread(() -> {
                    try {
                        dao.createUser(values[0], values[1], values[2], values[3]);
                        javafx.application.Platform.runLater(() -> {
                            showStatus(statusLabel, "User '" + values[0] + "' created successfully.", true);
                            refreshUsers(data);
                        });
                    } catch (Exception ex) {
                        javafx.application.Platform.runLater(() ->
                            showStatus(statusLabel, "Error: " + ex.getMessage(), false)
                        );
                    }
                }).start();
            });
        });

        toggleBtn.setOnAction(e -> {
            UserRecord selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showStatus(statusLabel, "Select a user first.", false);
                return;
            }
            if ("ADMIN".equals(selected.getRole()) && selected.getId() == user.getId()) {
                showStatus(statusLabel, "You cannot deactivate your own account.", false);
                return;
            }
            boolean newState = !selected.isActive();
            new Thread(() -> {
                try {
                    dao.toggleUserActive(selected.getId(), newState);
                    javafx.application.Platform.runLater(() -> {
                        selected.setActive(newState);
                        table.refresh();
                        showStatus(statusLabel,
                            selected.getFullName() + " is now " + (newState ? "Active" : "Inactive") + ".",
                            true);
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() ->
                        showStatus(statusLabel, "Error: " + ex.getMessage(), false)
                    );
                }
            }).start();
        });

        HBox toolbar = new HBox(10, addBtn, toggleBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        refreshUsers(data);
        VBox.setVgrow(table, Priority.ALWAYS);
        pane.getChildren().addAll(toolbar, statusLabel, table);
        setContent(pane);
    }

    // ── Course Management ─────────────────────────────────────────────────────

    private void showCourses() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("Course Management"));

        ObservableList<Course> data = FXCollections.observableArrayList();

        TableView<Course> table = new TableView<>(data);
        table.setStyle(tableStyle() + " -fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Course, String> codeCol = col("Code",        "code",         90);
        TableColumn<Course, String> nameCol = col("Course Name", "name",          0);
        TableColumn<Course, String> lecCol  = col("Lecturer",    "lecturerName",180);

        table.getColumns().addAll(codeCol, nameCol, lecCol);

        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Segoe UI", 12));
        statusLabel.setVisible(false);

        Button addBtn = actionButton("+ Add Course", "#2563EB");
        Button delBtn = actionButton("Delete Course", "#DC2626");

        addBtn.setOnAction(e -> {
            showAddCourseDialog().ifPresent(values -> {
                // values[0]=code, values[1]=name, values[2]=lecturerId (may be null)
                new Thread(() -> {
                    try {
                        Integer lecId = values[2].isEmpty() ? null : Integer.parseInt(values[2]);
                        dao.createCourse(values[0], values[1], lecId);
                        javafx.application.Platform.runLater(() -> {
                            showStatus(statusLabel, "Course '" + values[0] + "' created.", true);
                            refreshCourses(data);
                        });
                    } catch (Exception ex) {
                        javafx.application.Platform.runLater(() ->
                            showStatus(statusLabel, "Error: " + ex.getMessage(), false)
                        );
                    }
                }).start();
            });
        });

        delBtn.setOnAction(e -> {
            Course selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showStatus(statusLabel, "Select a course first.", false); return; }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete course " + selected.getCode() + "?\nAll enrolments will be removed.",
                ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Confirm Delete");
            styleDialog(confirm);
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.YES) {
                    new Thread(() -> {
                        try {
                            dao.deleteCourse(selected.getId());
                            javafx.application.Platform.runLater(() -> {
                                showStatus(statusLabel, "Course deleted.", true);
                                refreshCourses(data);
                            });
                        } catch (Exception ex) {
                            javafx.application.Platform.runLater(() ->
                                showStatus(statusLabel, "Error: " + ex.getMessage(), false)
                            );
                        }
                    }).start();
                }
            });
        });

        HBox toolbar = new HBox(10, addBtn, delBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        refreshCourses(data);
        VBox.setVgrow(table, Priority.ALWAYS);
        pane.getChildren().addAll(toolbar, statusLabel, table);
        setContent(pane);
    }

    // ── Timetable Manager ─────────────────────────────────────────────────────

    private void showTimetableManager() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("Timetable Management"));

        // Semester selector + generate button
        ComboBox<Semester> semBox = new ComboBox<>();
        semBox.setPromptText("Select semester...");
        semBox.setStyle(
            "-fx-background-color: #1E293B; -fx-text-fill: #F1F5F9;" +
            "-fx-border-color: #475569; -fx-border-radius: 8; -fx-background-radius: 8;"
        );

        Button generateBtn = actionButton("Auto-Generate Timetable", "#2563EB");
        Button viewBtn     = actionButton("View Timetable",           "#059669");

        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Segoe UI", 12));
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(700);
        statusLabel.setVisible(false);

        StackPane gridHolder = new StackPane();
        VBox.setVgrow(gridHolder, Priority.ALWAYS);

        // Load semesters
        new Thread(() -> {
            try {
                List<Semester> semesters = ttDao.getSemesters();
                Semester active = ttDao.getActiveSemester();
                javafx.application.Platform.runLater(() -> {
                    semBox.getItems().setAll(semesters);
                    if (active != null) semBox.setValue(active);
                });
            } catch (Exception ignored) {}
        }).start();

        generateBtn.setOnAction(e -> {
            Semester sem = semBox.getValue();
            if (sem == null) { showStatus(statusLabel, "Select a semester first.", false); return; }

            generateBtn.setDisable(true);
            generateBtn.setText("Generating...");
            gridHolder.getChildren().clear();

            new Thread(() -> {
                try {
                    TimetableGenerator gen = new TimetableGenerator(ttDao);
                    TimetableGenerator.GenerationResult result = gen.generate(sem.getId());

                    List<TimetableEntry> entries = ttDao.getTimetableBySemester(sem.getId());

                    javafx.application.Platform.runLater(() -> {
                        generateBtn.setDisable(false);
                        generateBtn.setText("Auto-Generate Timetable");

                        StringBuilder msg = new StringBuilder();
                        msg.append(result.scheduled).append(" courses scheduled successfully.");
                        if (!result.conflicts.isEmpty()) {
                            msg.append("\n\nConflicts (").append(result.conflicts.size()).append("):");
                            result.conflicts.forEach(c -> msg.append("\n  • ").append(c));
                        }
                        showStatus(statusLabel, msg.toString(), result.conflicts.isEmpty());
                        gridHolder.getChildren().setAll(new TimetableGridView().buildWithFilter(entries));
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> {
                        generateBtn.setDisable(false);
                        generateBtn.setText("Auto-Generate Timetable");
                        showStatus(statusLabel, "Error: " + ex.getMessage(), false);
                    });
                }
            }).start();
        });

        viewBtn.setOnAction(e -> {
            Semester sem = semBox.getValue();
            if (sem == null) { showStatus(statusLabel, "Select a semester first.", false); return; }
            gridHolder.getChildren().clear();
            new Thread(() -> {
                try {
                    List<TimetableEntry> entries = ttDao.getTimetableBySemester(sem.getId());
                    javafx.application.Platform.runLater(() ->
                        gridHolder.getChildren().setAll(new TimetableGridView().buildWithFilter(entries))
                    );
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() ->
                        showStatus(statusLabel, "Error: " + ex.getMessage(), false)
                    );
                }
            }).start();
        });

        Label hint = new Label(
            "Auto-Generate replaces previously generated slots (manual entries are preserved). " +
            "Courses are sorted by enrolment size — largest classes get priority slots."
        );
        hint.setFont(Font.font("Segoe UI", javafx.scene.text.FontPosture.ITALIC, 12));
        hint.setTextFill(Color.web("#64748B"));
        hint.setWrapText(true);

        HBox toolbar = new HBox(10, semBox, generateBtn, viewBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        pane.getChildren().addAll(toolbar, hint, statusLabel, gridHolder);
        setContent(pane);
    }

    // ── Reports ───────────────────────────────────────────────────────────────

    private void showReports() {
        VBox pane = new VBox(20);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("Enrolment & Grade Report"));

        Label sub = new Label("Overview of enrolment, average grades and pass rates per course.");
        sub.setFont(Font.font("Segoe UI", 13));
        sub.setTextFill(Color.web("#94A3B8"));

        TableView<CourseReport> table = buildReportTable();
        loadReportData(table);
        VBox.setVgrow(table, Priority.ALWAYS);
        pane.getChildren().addAll(sub, table);
        setContent(pane);
    }

    // ── Dialogs ───────────────────────────────────────────────────────────────

    private Optional<String[]> showAddUserDialog() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Create a new system user");
        styleDialog(dialog);

        ButtonType saveBtn = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField     usernameField = dialogField("e.g. jdoe2024");
        TextField     nameField     = dialogField("e.g. John Doe");
        PasswordField passField     = new PasswordField();
        passField.setPromptText("Min 6 characters");
        passField.setStyle(fieldStyle());

        ComboBox<String> roleBox = new ComboBox<>(
            FXCollections.observableArrayList("STUDENT", "LECTURER", "ADMIN")
        );
        roleBox.setValue("STUDENT");
        roleBox.setStyle(fieldStyle());
        roleBox.setPrefWidth(220);

        grid.add(dialogLabel("Username"),  0, 0); grid.add(usernameField, 1, 0);
        grid.add(dialogLabel("Full Name"), 0, 1); grid.add(nameField,     1, 1);
        grid.add(dialogLabel("Password"),  0, 2); grid.add(passField,     1, 2);
        grid.add(dialogLabel("Role"),      0, 3); grid.add(roleBox,       1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setStyle("-fx-background-color: #1E293B;");

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                String u = usernameField.getText().trim();
                String n = nameField.getText().trim();
                String p = passField.getText();
                String r = roleBox.getValue();
                if (u.isEmpty() || n.isEmpty() || p.isEmpty()) return null;
                return new String[]{u, p, n, r};
            }
            return null;
        });

        return dialog.showAndWait().filter(v -> v != null);
    }

    private Optional<String[]> showAddCourseDialog() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Add New Course");
        dialog.setHeaderText("Create a new course");
        styleDialog(dialog);

        ButtonType saveBtn = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        TextField codeField = dialogField("e.g. CS101");
        TextField nameField = dialogField("e.g. Introduction to Programming");

        ComboBox<UserRecord> lecBox = new ComboBox<>();
        lecBox.setPromptText("Select lecturer (optional)");
        lecBox.setStyle(fieldStyle());
        lecBox.setPrefWidth(220);
        lecBox.setCellFactory(lv -> lecturerCell());
        lecBox.setButtonCell(lecturerCell());

        new Thread(() -> {
            try {
                List<UserRecord> lecs = dao.getLecturers();
                javafx.application.Platform.runLater(() ->
                    lecBox.setItems(FXCollections.observableArrayList(lecs))
                );
            } catch (Exception ignored) {}
        }).start();

        grid.add(dialogLabel("Course Code"), 0, 0); grid.add(codeField, 1, 0);
        grid.add(dialogLabel("Course Name"), 0, 1); grid.add(nameField, 1, 1);
        grid.add(dialogLabel("Lecturer"),    0, 2); grid.add(lecBox,    1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setStyle("-fx-background-color: #1E293B;");

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                String code = codeField.getText().trim();
                String name = nameField.getText().trim();
                if (code.isEmpty() || name.isEmpty()) return null;
                UserRecord lec = lecBox.getValue();
                String lecId = lec == null ? "" : String.valueOf(lec.getId());
                return new String[]{code, name, lecId};
            }
            return null;
        });

        return dialog.showAndWait().filter(v -> v != null);
    }

    // ── Data loaders ──────────────────────────────────────────────────────────

    private void refreshUsers(ObservableList<UserRecord> data) {
        new Thread(() -> {
            try {
                List<UserRecord> users = dao.getAllUsers();
                javafx.application.Platform.runLater(() -> data.setAll(users));
            } catch (Exception ignored) {}
        }).start();
    }

    private void refreshCourses(ObservableList<Course> data) {
        new Thread(() -> {
            try {
                List<Course> courses = dao.getAllCourses();
                javafx.application.Platform.runLater(() -> data.setAll(courses));
            } catch (Exception ignored) {}
        }).start();
    }

    private TableView<CourseReport> buildReportTable() {
        TableView<CourseReport> table = new TableView<>();
        table.setStyle(tableStyle() + " -fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<CourseReport, String> codeCol  = col("Code",       "code",                80);
        TableColumn<CourseReport, String> nameCol  = col("Course",     "name",                 0);
        TableColumn<CourseReport, String> lecCol   = col("Lecturer",   "lecturerName",        160);
        TableColumn<CourseReport, String> enrCol   = col("Enrolled",   "enrolled",             80);
        TableColumn<CourseReport, String> avgCol   = col("Avg Grade",  "averageGradeDisplay", 100);
        TableColumn<CourseReport, String> passCol  = col("Pass Rate",  "passRate",             90);

        table.getColumns().addAll(codeCol, nameCol, lecCol, enrCol, avgCol, passCol);
        return table;
    }

    private void loadReportData(TableView<CourseReport> table) {
        new Thread(() -> {
            try {
                List<CourseReport> report = dao.getEnrollmentReport();
                javafx.application.Platform.runLater(() ->
                    table.setItems(FXCollections.observableArrayList(report))
                );
            } catch (Exception ignored) {}
        }).start();
    }

    // ── UI helpers ────────────────────────────────────────────────────────────

    private void setContent(javafx.scene.Node node) {
        contentArea.getChildren().setAll(node);
    }

    private <S, T> TableColumn<S, T> col(String title, String prop, double maxW) {
        TableColumn<S, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        if (maxW > 0) c.setMaxWidth(maxW);
        return c;
    }

    private VBox statCard(String label, String value, String color) {
        Label valLbl = new Label(value);
        valLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        valLbl.setTextFill(Color.web(color));

        Label lbl = new Label(label);
        lbl.setFont(Font.font("Segoe UI", 12));
        lbl.setTextFill(Color.web("#94A3B8"));

        VBox card = new VBox(6, valLbl, lbl);
        card.setPadding(new Insets(18));
        card.setPrefWidth(150);
        card.setStyle(
            "-fx-background-color: #1E293B; -fx-background-radius: 12;" +
            "-fx-border-color: #334155; -fx-border-radius: 12;"
        );
        return card;
    }

    private Button actionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefHeight(36);
        btn.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        btn.setStyle(
            "-fx-background-color:" + color + "; -fx-text-fill: white;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 0 16 0 16;"
        );
        return btn;
    }

    private Button navButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(186);
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

    private String fieldStyle() {
        return "-fx-background-color: #0F172A; -fx-text-fill: #F1F5F9;" +
               "-fx-prompt-text-fill: #475569; -fx-border-color: #334155;" +
               "-fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 6 10;";
    }

    private TextField dialogField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(220);
        tf.setStyle(fieldStyle());
        return tf;
    }

    private Label dialogLabel(String text) {
        Label lbl = new Label(text);
        lbl.setTextFill(Color.web("#94A3B8"));
        lbl.setFont(Font.font("Segoe UI", 13));
        return lbl;
    }

    private void showStatus(Label label, String msg, boolean success) {
        label.setText(msg);
        label.setTextFill(Color.web(success ? "#4ADE80" : "#FCA5A5"));
        label.setVisible(true);
    }

    private void styleDialog(Dialog<?> dialog) {
        dialog.getDialogPane().setStyle("-fx-background-color: #1E293B;");
    }

    private ListCell<UserRecord> lecturerCell() {
        return new ListCell<UserRecord>() {
            @Override
            protected void updateItem(UserRecord u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? null : u.getFullName());
                setStyle("-fx-text-fill: #F1F5F9; -fx-background-color: #1E293B;");
            }
        };
    }

    // ── Attendance overview ───────────────────────────────────────────────────

    private void showAttendanceOverview() {
        VBox pane = new VBox(16);
        pane.setPadding(new Insets(30));
        pane.getChildren().add(sectionTitle("Attendance Overview"));

        Label sub = new Label("Select a course to view per-student attendance statistics.");
        sub.setFont(Font.font("Segoe UI", 13));
        sub.setTextFill(Color.web("#94A3B8"));

        ComboBox<Course> courseBox = new ComboBox<>();
        courseBox.setPromptText("Select course...");
        courseBox.setPrefWidth(300);
        courseBox.setStyle(
            "-fx-background-color: #1E293B; -fx-text-fill: #F1F5F9;" +
            "-fx-border-color: #475569; -fx-border-radius: 8; -fx-background-radius: 8;"
        );
        courseBox.setCellFactory(lv -> attCourseCell());
        courseBox.setButtonCell(attCourseCell());

        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Segoe UI", 12));
        statusLabel.setVisible(false);

        TableView<AttendanceSummary> table = new TableView<>();
        table.setStyle(tableStyle() + " -fx-font-size: 13px;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<AttendanceSummary, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));

        TableColumn<AttendanceSummary, String> attCol = new TableColumn<>("Attended");
        attCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getAttended())));
        attCol.setMaxWidth(90);

        TableColumn<AttendanceSummary, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getTotal())));
        totalCol.setMaxWidth(80);

        TableColumn<AttendanceSummary, String> pctCol = new TableColumn<>("Attendance %");
        pctCol.setCellValueFactory(new PropertyValueFactory<>("percentageDisplay"));
        pctCol.setMaxWidth(120);

        TableColumn<AttendanceSummary, Double> barCol = new TableColumn<>("Progress");
        barCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPercentage()).asObject());
        barCol.setMinWidth(160);
        barCol.setCellFactory(tc -> new TableCell<AttendanceSummary, Double>() {
            private final ProgressBar bar = new ProgressBar();
            { bar.setPrefWidth(140); bar.setPrefHeight(16); }
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                double p = item / 100.0;
                bar.setProgress(p);
                bar.setStyle(p < 0.75 ? "-fx-accent: #EF4444;" : "-fx-accent: #22C55E;");
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

        table.getColumns().addAll(studentCol, attCol, totalCol, pctCol, barCol, warnCol);

        courseBox.setOnAction(e -> {
            Course selected = courseBox.getValue();
            if (selected == null) return;
            table.getItems().clear();
            new Thread(() -> {
                try {
                    List<AttendanceSummary> summaries = attDao.getCourseAttendanceSummary(selected.getId());
                    javafx.application.Platform.runLater(() -> {
                        table.setItems(FXCollections.observableArrayList(summaries));
                        if (summaries.isEmpty())
                            showStatus(statusLabel, "No attendance records yet for this course.", true);
                        else
                            statusLabel.setVisible(false);
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() ->
                        showStatus(statusLabel, "Error: " + ex.getMessage(), false));
                }
            }).start();
        });

        Button exportBtn = actionButton("Export CSV", "#7C3AED");
        exportBtn.setOnAction(e -> {
            Course selected = courseBox.getValue();
            if (selected == null) { showStatus(statusLabel, "Select a course to export.", false); return; }
            FileChooser fc = new FileChooser();
            fc.setTitle("Save Attendance Report");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            fc.setInitialFileName("attendance_" + selected.getCode() + ".csv");
            java.io.File file = fc.showSaveDialog(stage);
            if (file == null) return;
            new Thread(() -> {
                try {
                    String csv = attDao.exportCourseAttendanceCSV(selected.getId(), selected.getCode());
                    java.nio.file.Files.writeString(file.toPath(), csv);
                    javafx.application.Platform.runLater(() ->
                        showStatus(statusLabel, "Exported to " + file.getName(), true));
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() ->
                        showStatus(statusLabel, "Export failed: " + ex.getMessage(), false));
                }
            }).start();
        });

        new Thread(() -> {
            try {
                List<Course> courses = dao.getAllCourses();
                javafx.application.Platform.runLater(() ->
                    courseBox.setItems(FXCollections.observableArrayList(courses)));
            } catch (Exception ignored) {}
        }).start();

        HBox toolbar = new HBox(10, courseBox, exportBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        pane.getChildren().addAll(sub, toolbar, statusLabel, table);
        setContent(pane);
    }

    private ListCell<Course> attCourseCell() {
        return new ListCell<Course>() {
            @Override
            protected void updateItem(Course c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getCode() + " – " + c.getName());
                setStyle("-fx-text-fill: #F1F5F9; -fx-background-color: #1E293B;");
            }
        };
    }
}
