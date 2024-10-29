package isty.iatic5.session_phase1.Controller;

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

public class UEController {

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

    // Méthode pour supprimer l'UE sélectionnée
    @FXML
    private void deleteUE() {
        // Récupération de l'UE sélectionnée
        UniteEnseignement selectedUE = ueTableView.getSelectionModel().getSelectedItem();

        if (selectedUE != null) {
            // Appel de la méthode deleteUE pour supprimer de la base de données
            int result = sessionService.deleteUE(selectedUE.getIdUE());

            if (result > 0) {
                ueList.remove(selectedUE); // Supprime de la liste observable pour actualiser la table
                showAlert("Succès", "Unité d'Enseignement supprimée avec succès !");
            } else {
                showAlert("Erreur", "Échec de la suppression de l'Unité d'Enseignement.");
            }
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
    private void goHome() {
        try {
            // Charger le fichier FXML de la page d'accueil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/creneau-view.fxml"));
            Parent homeRoot = loader.load();

            // Récupérer la scène actuelle et charger la nouvelle
            Stage stage = (Stage) ueTableView.getScene().getWindow(); // Utilise ueTableView pour obtenir la fenêtre actuelle
            stage.setScene(new Scene(homeRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
