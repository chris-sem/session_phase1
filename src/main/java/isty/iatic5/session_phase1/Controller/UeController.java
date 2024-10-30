package isty.iatic5.session_phase1.Controller;

import isty.iatic5.session_phase1.HelloApplication;
import isty.iatic5.session_phase1.Model.UniteEnseignement;
import isty.iatic5.session_phase1.Services.ISession;
import isty.iatic5.session_phase1.Services.SessionImpl;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class UeController {

    // Déclaration des éléments de la vue
    @FXML
    private TableView<UniteEnseignement> ueTableView;
    @FXML
    private TableColumn<UniteEnseignement, Integer> idColumn;
    @FXML
    private TableColumn<UniteEnseignement, String> codeColumn;
    @FXML
    private TableColumn<UniteEnseignement, String> designationColumn;

    // Déclaration des champs de saisie
    @FXML
    private TextField code;
    @FXML
    private TextField designation;

    // Instance de la classe implémentant l'interface ISession
    private final ISession sessionService = new SessionImpl();
    // Liste observable pour stocker les UE
    private ObservableList<UniteEnseignement> ueList = FXCollections.observableArrayList();

    // Méthode pour créer une UE
    @FXML
    private void createUE() {
        // Récupération des valeurs saisies
        String codeValue = code.getText();
        String designationValue = designation.getText();

        // Vérification que les champs ne sont pas vides
        if (codeValue.isEmpty() || designationValue.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        // Appel à la méthode createUE
        int result = sessionService.createUE(codeValue, designationValue);

        // Vérification du résultat de l'opération
        if (result != -1) {
            loadUEData();  // Actualise les données dans la TableView
            showAlert("Succès", "Unité d'Enseignement créée avec succès!");
            code.clear();
            designation.clear();
        } else {
            showAlert("Erreur", "Échec de la création de l'Unité d'Enseignement.");
        }
    }

    // Méthode pour supprimer l'UE sélectionnée avec une demande deconfirmation
    @FXML
    private void deleteUE() {
        // Récupération de l'UE sélectionnée
        UniteEnseignement selectedUE = ueTableView.getSelectionModel().getSelectedItem();

        if (selectedUE != null) {
            // Création de l'alerte de confirmation
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation de suppression");
            confirmationAlert.setHeaderText("Vous êtes sur le point de supprimer l'Unité d'Enseignement suivante :");
            confirmationAlert.setContentText("Code: " + selectedUE.getCode() + "\nDésignation: " + selectedUE.getDesignation());

            // Ajout des boutons "OK" et "Annuler" dans l'alerte
            ButtonType buttonTypeOK = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmationAlert.getButtonTypes().setAll(buttonTypeOK, buttonTypeCancel);

            // Afficher l'alerte et attendre la réponse de l'utilisateur
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == buttonTypeOK) {
                    // Si l'utilisateur confirme, on procède à la suppression
                    int result = sessionService.deleteUE(selectedUE.getIdUE());

                    if (result > 0) {
                        ueList.remove(selectedUE); // Supprime de la liste observable pour actualiser la table
                        showAlert("Succès", "Unité d'Enseignement supprimée avec succès !");
                    } else {
                        showAlert("Erreur", "Échec de la suppression de l'Unité d'Enseignement.");
                    }
                } else {
                    showAlert("Info", "Suppression annulée.");
                }
            });
        } else {
            showAlert("Erreur", "Veuillez sélectionner une Unité d'Enseignement à supprimer.");
        }
    }

    // Méthode pour initialiser le TableView i.e lier les colonnes aux attributs de UniteEnseignement
    @FXML

    public void initialize() {
        // Configuration des colonnes de la TableView
        idColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getIdUE()));
        codeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCode()));
        designationColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDesignation()));

        // Chargement initial des données
        loadUEData();

        // Ajout d'un listener pour détecter le changement de sélection
        ueTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Met à jour les champs de saisie avec les valeurs de la ligne sélectionnée
                code.setText(newValue.getCode());
                designation.setText(newValue.getDesignation());
            }
        });
    }

    // Méthode pour charger les UE et les afficher dans le TableView
    private void loadUEData() {
        // Récupération des données des UE
        List<UniteEnseignement> ueData = sessionService.getAllUEs();
        if (ueData != null) {
            ueList.setAll(ueData); // Actualise la ObservableList avec les nouvelles données
            ueTableView.setItems(ueList); // Lie la liste à la TableView
        } else {
            showAlert("Erreur", "Impossible de charger les données des unités d'enseignement.");
        }
    }

    // Méthode pour retourner à la page d'accueil
    @FXML
    private void goToHome() throws IOException {

        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/View/accueil-view.fxml")); // Remplacez par le chemin réel de la vue d'accueil
        Parent root = loader.load();
        // Obtenez la scène actuelle et changez-la
        ueTableView.getScene().setRoot(root);

    }


    // Méthode pour afficher les alertes
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}