package isty.iatic5.session_phase1.Controller;

import isty.iatic5.session_phase1.Model.Classe;
import isty.iatic5.session_phase1.Model.Creneau;
import isty.iatic5.session_phase1.Model.UniteEnseignement;
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
    private ComboBox<UniteEnseignement> ueComboBox;
    @FXML
    private ComboBox<Classe> classComboBox;
    @FXML
    private TableView<Creneau> creneauxTableView;
    @FXML
    private TableColumn<Creneau, String> dayColumn;
    @FXML
    private TableColumn<Creneau, String> startTimeColumn;
    @FXML
    private TableColumn<Creneau, String> endTimeColumn;
    @FXML
    TableColumn<Creneau, String> statusColumn;




    // Variables to hold selected class and UE
    private String selectedClass = null;
    private String selectedUE = null;


    @FXML
    private void initialize() {
        // Ajouter un écouteur d'action pour les ComboBox
        ueComboBox.setOnAction(event -> updateCreneaux());
        classComboBox.setOnAction(event -> updateCreneaux());

        // Set up the Class ComboBox to show available classes on click
        classComboBox.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) { // Single click to show available classes
                afficherClasses(); // Populate the ComboBox with classes
            }
        });

        // Set up the UE ComboBox to show available UE on click
        ueComboBox.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) { // Single click to show available UE
                afficherUE(); // Populate the ComboBox with UE
            }
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

        statusColumn.setCellValueFactory(cellData -> {
            return new SimpleStringProperty(cellData.getValue().isReserved() ? "Réservé" : "Disponible");
        });
    }


    private void updateCreneaux() {
        UniteEnseignement selectedUE = ueComboBox.getValue();
        Classe selectedClass = classComboBox.getValue();

        if (selectedUE != null && selectedClass != null) {
            displayAvailableCreneaux(selectedClass.getIdClasse(), selectedUE.getIdUE());
            creneauxTableView.refresh();
        }
    }


    @FXML
    private void afficherUE() {
        DBConnexion dbConnexion = new DBConnexion();
        // Update SQL query to select UE data (include idUE, code, and designation)
        String sql = "SELECT id, code, designation FROM unite_enseignement"; // Update table and column names as needed

        try {
            dbConnexion.initPrepar(sql); // Initialize the PreparedStatement with the SQL query
            ResultSet rs = dbConnexion.executeSelect(); // Execute the select query

            // Clear the ComboBox before adding new items
            ueComboBox.getItems().clear();

            while (rs.next()) {
                int idUE = rs.getInt("id"); // Get UE ID
                String code = rs.getString("code"); // Get UE code (if needed)
                String designation = rs.getString("designation"); // Get UE designation

                // Create a new UniteEnseignement object and add it to the ComboBox
                UniteEnseignement ue = new UniteEnseignement(idUE, code, designation);
                ueComboBox.getItems().add(ue);
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

    private void afficherClasses() {
        DBConnexion dbConnexion = new DBConnexion();
        String sql = "SELECT id, specialite, promotion FROM classe";

        try {
            dbConnexion.initPrepar(sql);
            ResultSet rs = dbConnexion.executeSelect();

            classComboBox.getItems().clear(); // Clear previous items

            while (rs.next()) {
                int id = rs.getInt("id"); // Get the ID
                String specialiteString = rs.getString("specialite"); // Get specialité
                int promotion = rs.getInt("promotion"); // Get promotion

                Classe.Specialite specialite = Classe.Specialite.valueOf(specialiteString.toUpperCase());

                // Create a new Classe object and add it to the ComboBox
                Classe classe = new Classe(id, promotion, specialite);
                classComboBox.getItems().add(classe);
            }

            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load class data.");
            alert.showAndWait();
        } finally {
            dbConnexion.closeConnection();
        }
    }

    private void displayAvailableCreneaux(int idClasse, int idUe) {
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

                Creneau creneau = new Creneau(idCreneau, debut, fin);
                creneau.setReserved(isCreneauReserved(idCreneau, idClasse, idUe));
                creneauxList.add(creneau);
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


    public void setStage(Stage stage) {
        // Configurations supplémentaires pour la scène si nécessaire
        stage.setTitle("Gestion des Sessions");
    }
}