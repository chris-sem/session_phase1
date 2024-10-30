package isty.iatic5.session_phase1.Controller;

import isty.iatic5.session_phase1.HelloApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

public class SessionController {

    @FXML
    private TableView<String[]> tableView;

    @FXML
    private void goToHome() throws IOException {

        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/View/accueil-view.fxml")); // Remplacez par le chemin réel de la vue d'accueil
        Parent root = loader.load();
        // Obtenez la scène actuelle et changez-la
        tableView.getScene().setRoot(root);

    }
}
