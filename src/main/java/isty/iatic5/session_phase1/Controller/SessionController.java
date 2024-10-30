package isty.iatic5.session_phase1.Controller;

import isty.iatic5.session_phase1.HelloApplication;
import isty.iatic5.session_phase1.Model.Classe;
import isty.iatic5.session_phase1.Model.Creneau;
import isty.iatic5.session_phase1.Model.UniteEnseignement;
import isty.iatic5.session_phase1.Services.DBConnexion;
import isty.iatic5.session_phase1.Services.SessionImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;
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
    @FXML
    private TableColumn<Creneau, Void> actionColumn;


    @FXML
    private void initialize() {

        SessionImpl sessionService = new SessionImpl();

        // Ajouter un écouteur d'action pour les ComboBox
        ueComboBox.setOnAction(event -> updateCreneaux());
        classComboBox.setOnAction(event -> updateCreneaux());


        // Set up the Class ComboBox to show available classes on click
        classComboBox.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) { // Single click to show available classes
                afficherClasses(); // Populate the ComboBox with classes
            }
            if (classComboBox.getValue() == null) {
                creneauxTableView.getItems().clear(); // Clear the table if nothing is selected
            }
        });

        // Set up the UE ComboBox to show available UE on click
        ueComboBox.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) { // Single click to show available UE
                afficherUE(); // Populate the ComboBox with UE
            }
            if (ueComboBox.getValue() == null) {
                creneauxTableView.getItems().clear(); // Clear the table if nothing is selected
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
            // Access the Creneau object from the cell data and get its Statut
            return new SimpleStringProperty(cellData.getValue().getStatut());
        });

        statusColumn.setCellFactory(column -> new TableCell<Creneau, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if (status.equals("Indisponible")) {
                        setStyle("-fx-background-color: red; -fx-text-fill: white;"); // Style pour "Réservé"
                    } else if (status.equals("Réservé")){
                        setStyle("-fx-background-color: orange; -fx-text-fill: white;"); // Style pour "Réservé"
                    }else {
                        setStyle("-fx-background-color: green; -fx-text-fill: white;"); // Style pour "Disponible"
                    }
                }
            }
        });

        actionColumn.setCellFactory(column -> new TableCell<Creneau, Void>() {
            private final Label actionLabel = new Label();


            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Creneau creneau = getTableView().getItems().get(getIndex());
                if (creneau.getStatut().equals("Réservé")) {
                    actionLabel.setText("Supprimer Session");
                    actionLabel.setStyle("-fx-text-fill: orange; -fx-underline: true;");
                    actionLabel.setOnMouseClicked(event -> {
                        // Exemple d'identifiant de session, idUE, idClasse et idCreneau
                        int idUE = ueComboBox.getValue().getIdUE();
                        int idClasse = classComboBox.getValue().getIdClasse();
                        int idCreneau = creneau.getIdCreneau();


                        sessionService.deleteSession(sessionService.GetIdSession(idUE, idClasse, idCreneau));
                        updateCreneaux();
                    });
                } else if (creneau.getStatut().equals("Disponible")) {
                    actionLabel.setText("Créer Session");
                    actionLabel.setStyle("-fx-text-fill: green; -fx-underline: true;");
                    actionLabel.setOnMouseClicked(event -> {
                        int lastId = sessionService.getLastSessionId();
                        String identifiant = "session"+ (lastId+1);  // Remplacez par un identifiant unique
                        int idUE = ueComboBox.getValue().getIdUE();
                        int idClasse = classComboBox.getValue().getIdClasse();
                        int idCreneau = creneau.getIdCreneau();


                        sessionService.createSession(identifiant, idUE, idClasse, idCreneau);
                        updateCreneaux();
                    });
                }else {
                    // Afficher un message pour les créneaux déjà utilisés
                    actionLabel.setText("Ce créneau est déjà utilisé par une autre session");
                    actionLabel.setStyle("-fx-text-fill: red;");
                    actionLabel.setOnMouseClicked(null); // Désactiver le clic sur ce texte
                }

                setGraphic(actionLabel);
            }
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
                creneau.setStatut(isCreneauReserved(idCreneau, idClasse, idUe));
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

    private String isCreneauReserved(int idCreneau, int idClasse, int idUe) {
        // Récupérer la liste de toutes les sessions
        DBConnexion dbConnexion = new DBConnexion();
        String sql = "SELECT id_ue, id_classe, id_creneau FROM session";

        try (Connection conn = dbConnexion.getConnection(); // Connection to the database
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int reservedIdUE = rs.getInt("id_ue");
                int reservedIdClasse = rs.getInt("id_classe");
                int reservedIdCreneau = rs.getInt("id_creneau");

                // Check if the creneau is reserved
                if (reservedIdClasse == idClasse && reservedIdCreneau == idCreneau && reservedIdUE == idUe) {
                    return "Réservé"; // The creneau is reserved for the same UE and Classe
                }
                else if ((reservedIdClasse == idClasse && reservedIdCreneau == idCreneau && reservedIdUE != idUe) ||
                        (reservedIdUE == idUe && reservedIdCreneau == idCreneau && reservedIdClasse != idClasse)) {
                    return "Indisponible"; // The creneau is unavailable due to conflict
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
        return "Disponible"; // The creneau is available
    }
    @FXML
    private void goToHome() throws IOException {

        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/View/accueil-view.fxml")); // Remplacez par le chemin réel de la vue d'accueil
        Parent root = loader.load();
        // Obtenez la scène actuelle et changez-la
        creneauxTableView.getScene().setRoot(root);

    }

}