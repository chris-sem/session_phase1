package isty.iatic5.session_phase1.Controller;

import isty.iatic5.session_phase1.Application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.io.IOException;

public class AccueilController {

    @FXML
    private void goToCreneau(ActionEvent event) throws IOException {
        navigateTo("creneau-view.fxml", event);
    }

    @FXML
    private void goToClasse(ActionEvent event) throws IOException {
        //System.out.println("Page 2 is not yet available.");
        navigateTo("classe-view.fxml", event); // Remplacez par le fichier FXML de la page 2
    }

    @FXML
    private void goToUe(ActionEvent event) throws IOException {
        //System.out.println("Page 3 is not yet available.");
        navigateTo("ue-view.fxml", event); // Remplacez par le fichier FXML de la page 3
    }

    @FXML
    private void goToSession(ActionEvent event) throws IOException {
        //System.out.println("Page 4 is not yet available.");
        navigateTo("session-view.fxml", event); // Remplacez par le fichier FXML de la page 4
    }

    private void navigateTo(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/View/" + fxmlFile));
        Parent root = fxmlLoader.load(); // Charge le fichier FXML
        Scene scene = new Scene(root, 1000, 750);
        // Récupère le stage à partir d'un des contrôles de la scène actuelle
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void quitApplication() {
        System.exit(0); // Quitte l'application
    }


}

