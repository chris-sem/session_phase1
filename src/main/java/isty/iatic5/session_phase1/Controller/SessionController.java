package isty.iatic5.session_phase1.Controller;

import isty.iatic5.session_phase1.Services.DBConnexion;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionController {
    private boolean isUEVisible = false;
    private boolean isClasseVisible = false;

    @FXML
    private Button ueButton;
    @FXML
    private Button classButton;
    @FXML
    private ListView<String> listView;

    @FXML
    private void handleListClick() {
        String selectedUE = listView.getSelectionModel().getSelectedItem();
        if (selectedUE != null) {
            listView.getItems().clear(); // Clear the list view after selection
            // Optionally, show a message or perform another action

        }
    }

    @FXML
    private void initialize() {
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click to select
                handleListClick();
            }
        });
    }

    @FXML
    private void afficherUE() {
        DBConnexion dbConnexion = new DBConnexion();
        String sql = "SELECT designation FROM unite_enseignement"; // Replace 'ue_name' and 'ue_table' with your actual column and table names

        try {
            listView.getItems().clear(); // Clear the existing items
            dbConnexion.initPrepar(sql); // Initialize the PreparedStatement with the SQL query

            ResultSet rs = dbConnexion.executeSelect(); // Execute the select query

            while (rs.next()) {
                String ueName = rs.getString("designation"); // Replace 'ue_name' with your actual column name
                listView.getItems().add(ueName); // Add the UE name to the ListView
            }

            rs.close(); // Close the ResultSet
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception, maybe show an alert to the user
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load UE data.");
            alert.showAndWait();
        } finally {
            dbConnexion.closeConnection(); // Close the database connection
        }
    }

    @FXML
    private void afficherClasses() {
        DBConnexion dbConnexion = new DBConnexion();
        // Update SQL query to select both 'spécialité' and 'promotion'
        String sql = "SELECT specialite, promotion FROM classe"; // Replace with your actual column names and table name

        try {
            listView.getItems().clear(); // Clear the existing items
            dbConnexion.initPrepar(sql); // Initialize the PreparedStatement with the SQL query

            ResultSet rs = dbConnexion.executeSelect(); // Execute the select query

            while (rs.next()) {

                String specialite = rs.getString("specialite"); // Get specialité
                String promotion = rs.getString("promotion"); // Get promotion

                // Combine className, specialite, and promotion into a single string
                String displayText = String.format("%s %s", specialite, promotion);
                listView.getItems().add(displayText); // Add the combined string to the ListView
            }

            rs.close(); // Close the ResultSet
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception, maybe show an alert to the user
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load UE data.");
            alert.showAndWait();
        } finally {
            dbConnexion.closeConnection(); // Close the database connection
        }
    }


    public void setStage(Stage stage) {
        // Configurations supplémentaires pour la scène si nécessaire
        stage.setTitle("Gestion des Sessions");
    }
}
