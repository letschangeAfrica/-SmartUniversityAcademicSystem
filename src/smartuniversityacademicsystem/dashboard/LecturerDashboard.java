package smartuniversityacademicsystem.dashboard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import smartuniversityacademicsystem.model.User;
import smartuniversityacademicsystem.view.LoginView;

public class LecturerDashboard {

    private final Stage stage;
    private final User  user;

    public LecturerDashboard(Stage stage, User user) {
        this.stage = stage;
        this.user  = user;
    }

    public void show() {
        stage.setTitle("SUAS – Lecturer Portal");

        Label welcome = new Label("Welcome, " + user.getFullName());
        welcome.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        welcome.setTextFill(Color.web("#F1F5F9"));

        Label role = new Label("Role: Lecturer");
        role.setFont(Font.font("Segoe UI", 13));
        role.setTextFill(Color.web("#94A3B8"));

        Label placeholder = new Label(
            "Lecturer features coming soon:\n" +
            "  • Manage course materials\n" +
            "  • Record & publish grades\n" +
            "  • View class rosters\n" +
            "  • Post announcements"
        );
        placeholder.setFont(Font.font("Segoe UI", 13));
        placeholder.setTextFill(Color.web("#CBD5E1"));
        placeholder.setLineSpacing(6);

        Button logoutBtn = logoutButton();

        VBox root = new VBox(16, welcome, role, placeholder, logoutBtn);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_LEFT);
        root.setStyle("-fx-background-color: #0F172A;");

        stage.setScene(new Scene(root, 600, 420));
        stage.setResizable(true);
        stage.show();
    }

    private Button logoutButton() {
        Button btn = new Button("Logout");
        btn.setStyle(
            "-fx-background-color: #DC2626; -fx-text-fill: white;" +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 13px;"
        );
        btn.setOnAction(e -> new LoginView(stage).show());
        return btn;
    }
}
