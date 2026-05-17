package smartuniversityacademicsystem.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import smartuniversityacademicsystem.db.UserDAO;
import smartuniversityacademicsystem.model.User;
import smartuniversityacademicsystem.dashboard.AdminDashboard;
import smartuniversityacademicsystem.dashboard.LecturerDashboard;
import smartuniversityacademicsystem.dashboard.StudentDashboard;

public class LoginView {

    private final Stage stage;

    public LoginView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        stage.setTitle("SUAS – Login");

        // ── Header ──────────────────────────────────────────────────────────
        Label titleLabel = new Label("Smart University");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.WHITE);

        Label subTitle = new Label("Academic System");
        subTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subTitle.setTextFill(Color.web("#CBD5E1"));

        VBox header = new VBox(4, titleLabel, subTitle);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(0, 0, 28, 0));

        // ── Form fields ──────────────────────────────────────────────────────
        Label usernameLabel = fieldLabel("Username");
        TextField usernameField = styledTextField("Enter your username");

        Label passwordLabel = fieldLabel("Password");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        styleInput(passwordField);

        // ── Error message ─────────────────────────────────────────────────────
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.web("#FCA5A5"));
        errorLabel.setFont(Font.font("Segoe UI", 12));
        errorLabel.setVisible(false);
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(300);

        // ── Login button ──────────────────────────────────────────────────────
        Button loginBtn = new Button("Login");
        loginBtn.setPrefWidth(300);
        loginBtn.setPrefHeight(42);
        loginBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        loginBtn.setStyle(
            "-fx-background-color: #2563EB;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle(
            "-fx-background-color: #1D4ED8;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle(
            "-fx-background-color: #2563EB;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        ));

        // ── Login action ──────────────────────────────────────────────────────
        Runnable doLogin = () -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showError(errorLabel, "Please enter both username and password.");
                return;
            }

            loginBtn.setDisable(true);
            loginBtn.setText("Logging in…");

            new Thread(() -> {
                try {
                    UserDAO dao = new UserDAO();
                    User user = dao.authenticate(username, password);
                    javafx.application.Platform.runLater(() -> {
                        loginBtn.setDisable(false);
                        loginBtn.setText("Login");
                        if (user == null) {
                            showError(errorLabel, "Invalid username or password.");
                            passwordField.clear();
                        } else {
                            openDashboard(user);
                        }
                    });
                } catch (Exception ex) {
                    javafx.application.Platform.runLater(() -> {
                        loginBtn.setDisable(false);
                        loginBtn.setText("Login");
                        showError(errorLabel, "Connection error: " + ex.getMessage());
                    });
                }
            }).start();
        };

        loginBtn.setOnAction(e -> doLogin.run());
        passwordField.setOnAction(e -> doLogin.run());
        usernameField.setOnAction(e -> passwordField.requestFocus());

        // ── Card ──────────────────────────────────────────────────────────────
        VBox card = new VBox(10,
            header,
            usernameLabel, usernameField,
            passwordLabel, passwordField,
            errorLabel,
            loginBtn
        );
        card.setPadding(new Insets(40, 40, 40, 40));
        card.setMaxWidth(380);
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle(
            "-fx-background-color: #1E293B;" +
            "-fx-background-radius: 16;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 24, 0, 0, 8);"
        );

        // ── Root ──────────────────────────────────────────────────────────────
        StackPane root = new StackPane(card);
        root.setBackground(new Background(
            new BackgroundFill(Color.web("#0F172A"), CornerRadii.EMPTY, Insets.EMPTY)
        ));
        root.setPadding(new Insets(40));

        Scene scene = new Scene(root, 520, 540);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        usernameField.requestFocus();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Label fieldLabel(String text) {
        Label lbl = new Label(text);
        lbl.setTextFill(Color.web("#94A3B8"));
        lbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        VBox.setMargin(lbl, new Insets(8, 0, 2, 0));
        return lbl;
    }

    private TextField styledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        styleInput(tf);
        return tf;
    }

    private void styleInput(javafx.scene.control.TextInputControl input) {
        input.setPrefHeight(40);
        input.setMaxWidth(300);
        input.setStyle(
            "-fx-background-color: #334155;" +
            "-fx-text-fill: #F1F5F9;" +
            "-fx-prompt-text-fill: #64748B;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #475569;" +
            "-fx-border-radius: 8;" +
            "-fx-padding: 0 12 0 12;" +
            "-fx-font-size: 13px;"
        );
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }

    private void openDashboard(User user) {
        switch (user.getRole()) {
            case STUDENT:  new StudentDashboard(stage, user).show();  break;
            case LECTURER: new LecturerDashboard(stage, user).show(); break;
            case ADMIN:    new AdminDashboard(stage, user).show();    break;
        }
    }
}
