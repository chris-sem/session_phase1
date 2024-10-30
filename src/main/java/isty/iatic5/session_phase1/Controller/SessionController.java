package isty.iatic5.session_phase1.Controller;

import isty.iatic5.session_phase1.Services.DBConnexion;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionController {
    private boolean isUEVisible = false;
    private boolean isClasseVisible = false;

    @FXML
    private ListView<String> timeSlotListView; // Add this ListView for time slots

    @FXML
    private Button ueButton;
    @FXML
    private Button classButton;
    @FXML
    private ListView<String> listView;
    @FXML
    private ComboBox<String> ueComboBox;
    @FXML
    private ComboBox<String> classComboBox;

    // Variables to hold selected class and UE
    private String selectedClass = null;
    private String selectedUE = null;

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
        // Set up action for Class ComboBox
        classComboBox.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) { // Single click to show available classes
                afficherClasses(); // Populate the ComboBox and ListView
            }
        });

        ueComboBox.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) { // Single click to show available ue
                afficherUE(); // Populate the ComboBox and ListView
            }
        });

        // Set up action for UE ComboBox
        ueComboBox.setOnAction(event -> {
            selectedUE = ueComboBox.getValue(); // Store selected UE
            checkAndDisplayTimeSlots();          // Check if both are selected
        });

        // Set up action for Class ComboBox
        classComboBox.setOnAction(event -> {
            selectedClass = classComboBox.getValue(); // Store selected class
            checkAndDisplayTimeSlots();               // Check if both are selected
        });



        // Set up action for time slot ListView

    }

    // Method to check selections and display time slots
    private void checkAndDisplayTimeSlots() {
        // Check if both selectedClass and selectedUE are not null
        if (selectedClass != null && selectedUE != null) {
            afficherCreneaux(); // Call to display time slots
        }
    }

    @FXML
    private void afficherUE() {
        DBConnexion dbConnexion = new DBConnexion();
        // Update SQL query to select UE data (replace 'ue_name' and 'ue_code' with actual column names)
        String sql = "SELECT designation FROM unite_enseignement"; // Update table and column names as needed

        try {
            dbConnexion.initPrepar(sql); // Initialize the PreparedStatement with the SQL query
            ResultSet rs = dbConnexion.executeSelect(); // Execute the select query

            // Clear the ComboBox before adding new items
            ueComboBox.getItems().clear();

            while (rs.next()) {
                String ueName = rs.getString("designation"); // Get UE name

                // Add the combined string to the ComboBox
                ueComboBox.getItems().add(ueName);
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
            dbConnexion.initPrepar(sql); // Initialize the PreparedStatement with the SQL query

            ResultSet rs = dbConnexion.executeSelect(); // Execute the select query

            // Clear the ComboBox before adding new items
            classComboBox.getItems().clear();

            while (rs.next()) {

                String specialite = rs.getString("specialite"); // Get specialité
                String promotion = rs.getString("promotion"); // Get promotion

                // Combine spécialité and promotion into a single string for display
                String displayText = String.format("%s - %s", specialite, promotion); // Example format: "Speciality - Promotion"

                // Add the combined string to the ComboBox
                classComboBox.getItems().add(displayText);
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
    private void afficherCreneaux() {
        DBConnexion dbConnexion = new DBConnexion();
        // SQL query to fetch all available time slots
        String sql = "SELECT debut, fin FROM creneau"; // Adjust the column names as necessary

        try {
            timeSlotListView.getItems().clear(); // Clear existing items in the ListView
            dbConnexion.initPrepar(sql);

            ResultSet rs = dbConnexion.executeSelect();

            while (rs.next()) {
                // Assuming you want to display both start and end dates as time slots
                String creneau = "Start: " + rs.getString("date_debut") + " - End: " + rs.getString("date_fin");
                timeSlotListView.getItems().add(creneau); // Add creneau to ListView
            }

            rs.close(); // Close ResultSet
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load available time slots.");
            alert.showAndWait();
        } finally {
            dbConnexion.closeConnection(); // Close DB connection
        }
    }

    public void setStage(Stage stage) {
        // Configurations supplémentaires pour la scène si nécessaire
        stage.setTitle("Gestion des Sessions");
    }
}
