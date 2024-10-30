package isty.iatic5.session_phase1.Controller;

import isty.iatic5.session_phase1.Model.Creneau;
import isty.iatic5.session_phase1.Services.DBConnexion;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    @FXML
    private TableView<Creneau> creneauxTableView;
    @FXML
    private TableColumn<Creneau, String> dayColumn;
    @FXML
    private TableColumn<Creneau, String> startTimeColumn;
    @FXML
    private TableColumn<Creneau, String> endTimeColumn;


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
            selectedUE = ueComboBox.getValue();// Store selected UE
            if (selectedUE != null && !(selectedUE.isEmpty())) {
                System.out.println(getUEIdByName(selectedUE));
            }
            checkAndDisplayTimeSlots();          // Check if both are selected
        });

        // Set up action for Class ComboBox
        classComboBox.setOnAction(event -> {
            selectedClass = classComboBox.getValue();
            if (selectedClass != null && !(selectedClass.isEmpty())) {
                System.out.println(getClassIdByName(selectedClass));
            }
            checkAndDisplayTimeSlots();               // Check if both are selected
        });

        // Define a formatter for date and time
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        // Set up the Day column to show only the date part of 'debut'
        dayColumn.setCellValueFactory(cellData -> {
            LocalDateTime debut = cellData.getValue().getDebut();
            return new SimpleStringProperty(debut.format(dateFormatter));
        });

        // Set up the Debut column to show only the time part of 'debut'
        startTimeColumn.setCellValueFactory(cellData -> {
            LocalDateTime debut = cellData.getValue().getDebut();
            return new SimpleStringProperty(debut.format(timeFormatter));
        });

        // Set up the Fin column to show only the time part of 'fin'
        endTimeColumn.setCellValueFactory(cellData -> {
            LocalDateTime fin = cellData.getValue().getFin();
            return new SimpleStringProperty(fin.format(timeFormatter));
        });


    }

    // Method to check selections and display time slots
    private void checkAndDisplayTimeSlots() {
        // Check if both selectedClass and selectedUE are not null
        if (selectedClass != null && selectedUE != null) {
            displayAvailableCreneaux(); // Call to display time slots
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

    private void displayAvailableCreneaux() {
        DBConnexion dbConnexion = new DBConnexion();
        String sql = "SELECT id, debut, fin FROM creneau";  // Adjust table and column names as needed

        try {
            dbConnexion.initPrepar(sql);
            ResultSet rs = dbConnexion.executeSelect();
            ObservableList<Creneau> creneauxList = FXCollections.observableArrayList();

            while (rs.next()) {
                int idCreneau = rs.getInt("id");
                LocalDateTime debut = rs.getTimestamp("debut").toLocalDateTime();
                LocalDateTime fin = rs.getTimestamp("fin").toLocalDateTime();

                creneauxList.add(new Creneau(idCreneau, debut, fin));
            }

            creneauxTableView.setItems(creneauxList); // Display in TableView
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load available time slots.").showAndWait();
        } finally {
            dbConnexion.closeConnection();
        }
    }

    private boolean isCreneauReserved(int idCreneau, int idClasse, int idUe) {
        // Récupérer la liste de toutes les sessions
        DBConnexion dbConnexion = new DBConnexion();
        String sql = "SELECT id_ue, id_classe, id_creneau FROM session";

        try (Connection conn = dbConnexion.getConnection(); // Supposons que DBConnexion a une méthode pour obtenir une connexion
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int reservedIdUE = rs.getInt("id_ue"); // Récupérer l'id_ue de la session
                int reservedIdClasse = rs.getInt("id_classe"); // Récupérer l'id_classe de la session
                int reservedIdCreneau = rs.getInt("id_creneau"); // Récupérer l'id_creneau de la session

                // Vérifiez si le créneau est déjà réservé pour la classe ou l'UE
                if ((reservedIdClasse == idClasse && reservedIdCreneau == idCreneau) ||
                        (reservedIdUE == idUe && reservedIdCreneau == idCreneau)) {
                    return true; // Le créneau est réservé
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Gérez les exceptions de manière appropriée
        }
        return false; // Le créneau est disponible
    }

    //works
    // Method to get the ID of a UE based on its name
    public int getUEIdByName(String ueName) {
        int ueId = -1; // Default value if not found
        DBConnexion dbConnexion = new DBConnexion(); // Assuming DBConnexion is your database connection class

        String sql = "SELECT id FROM unite_enseignement WHERE designation = ?"; // Replace 'name' with the actual column name in your ue table

        try (Connection connection = dbConnexion.getConnection(); // Get connection from your DBConnexion class
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Set the parameter for the prepared statement
            preparedStatement.setString(1, ueName);

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if a result was returned
            if (resultSet.next()) {
                ueId = resultSet.getInt("id"); // Get the id from the result set
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions properly in a real application
        }

        return ueId; // Return the found ID, or -1 if not found
    }



    //works
    public int getClassIdByName(String Spec_Prom) {
        int classId = -1; // Default value if not found
        DBConnexion dbConnexion = new DBConnexion(); // Assuming DBConnexion is your database connection class

        // Split the display text into specialization and promotion
        String[] parts = Spec_Prom.split(" - ");
        if (parts.length != 2) {
            return classId; // Invalid format, return default
        }

        String specialite = parts[0].trim(); // Get specialization
        int promotion;
        try {
            promotion = Integer.parseInt(parts[1].trim()); // Parse promotion
        } catch (NumberFormatException e) {
            e.printStackTrace(); // Handle exception if parsing fails
            return classId; // Return default if parsing fails
        }

        String sql = "SELECT id FROM classe WHERE specialite = ? AND promotion = ?"; // SQL query to find class by specialite and promotion

        try (Connection connection = dbConnexion.getConnection(); // Get connection from your DBConnexion class
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Set parameters for the prepared statement
            preparedStatement.setString(1, specialite); // Set the specialization
            preparedStatement.setInt(2, promotion); // Set the promotion

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if a result was returned
            if (resultSet.next()) {
                classId = resultSet.getInt("id"); // Get the id from the result set
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions properly in a real application
        }

        return classId; // Return the found ID, or -1 if not found
    }



    public void setStage(Stage stage) {
        // Configurations supplémentaires pour la scène si nécessaire
        stage.setTitle("Gestion des Sessions");
    }
}
