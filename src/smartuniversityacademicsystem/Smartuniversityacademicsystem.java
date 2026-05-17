package smartuniversityacademicsystem;

import javafx.application.Application;
import javafx.stage.Stage;
import smartuniversityacademicsystem.view.LoginView;

public class Smartuniversityacademicsystem extends Application {

    @Override
    public void start(Stage primaryStage) {
        new LoginView(primaryStage).show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
