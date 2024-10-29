package isty.iatic5.session_phase1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML file for the session management view
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/View/session-view.fxml")); // Adjust the path as needed
        Scene scene = new Scene(fxmlLoader.load(), 600, 400); // Set the width and height for your window
        stage.setTitle("Gestion des Sessions"); // Set the window title
        stage.setScene(scene); // Set the scene to the stage
        stage.show(); // Display the window
    }

    public static void main(String[] args) {
        System.out.println("Bienvenue dans la gestion des sessions!");
        launch(args); // Launch the JavaFX application
    }
}