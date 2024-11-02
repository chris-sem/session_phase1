package isty.iatic5.session_phase1.Controller;

import isty.iatic5.session_phase1.HelloApplication;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import isty.iatic5.session_phase1.Services.ISession;
import isty.iatic5.session_phase1.Services.SessionImpl;
import isty.iatic5.session_phase1.Model.Classe;
import isty.iatic5.session_phase1.Model.Classe.Specialite;
import javafx.scene.control.cell.PropertyValueFactory;

public class ClasseController {
    @FXML
    private TableView<Classe> tableClasse;

    @FXML
    private TableColumn<Classe, Integer> idColumn;

    @FXML
    private TableColumn<Classe, Integer> promotionColumn;

    @FXML
    private TableColumn<Classe, Classe.Specialite> specialiteColumn;
    //private TableColumn<Classe, String> specialiteColumn;

    @FXML
    private TextField promotionField;

    @FXML
    private ComboBox<Classe.Specialite> specialiteComboBox;
    //private ComboBox<String> specialiteComboBox;

    private ObservableList<Classe> classeList = FXCollections.observableArrayList();
    private final SessionImpl sessionImpl = new SessionImpl();

    @FXML
    public void initialize() {
        // Initialisation de la table avec les colonnes
        idColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getIdClasse()));
        promotionColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPromotion()));
        specialiteColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getSpecialite()));
        //promotionColumn.setCellValueFactory(new PropertyValueFactory<>("promotion"));
        //specialiteColumn.setCellValueFactory(new PropertyValueFactory<>("specialite"));
        // Charger les données dans la table
        loadClasses();
        tableClasse.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Met à jour les champs de saisie avec les valeurs de la ligne sélectionnée
                promotionColumn.setText(String.valueOf(newValue.getPromotion()));
                specialiteComboBox.setValue(newValue.getSpecialite());
            }
        });
        specialiteComboBox.setItems(FXCollections.observableArrayList(Classe.Specialite.values()));
    }

    private void loadClasses() {
        ObservableList<Classe> classes = sessionImpl.getClasse();
        tableClasse.setItems(classes);
    }

    @FXML
    private void addClasse() {
        String promotion = promotionField.getText();
        Classe.Specialite specialite = specialiteComboBox.getValue();
        if (promotion.isEmpty() || specialite == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }
        // Conversion de la promotion en entier
        int promotionInt;
        try {
            promotionInt = Integer.parseInt(promotion);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La promotion doit être un nombre.");
            return;
        }
        int result = sessionImpl.createClasse(promotionInt, specialite);
        // Vérification du résultat de l'opération
        if (result > -1) {
            loadClasses();  // Actualise les données dans la TableView
            showAlert("Succès", "Classe créée avec succès!");
            promotionField.clear();
            specialiteComboBox.setValue(null);
        } else if (result == -2) {
            showAlert("Erreur", "La classe que vous souhaitez créer existe déja.");
        }
        else {
            showAlert("Erreur", "Échec de la création de la Classe.");
        }
        // Ajout de la classe à la base de données
        sessionImpl.createClasse(promotionInt, specialite);
        loadClasses(); // Recharger les données
    }

    @FXML
    private void removeClasse() {
        Classe selectedClasse = tableClasse.getSelectionModel().getSelectedItem();

        if (selectedClasse != null) {
            // Création de l'alerte de confirmation
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation de suppression");
            confirmationAlert.setHeaderText("Vous êtes sur le point de supprimer la Classe suivante :");
            confirmationAlert.setContentText("Promotion: " + selectedClasse.getPromotion() + "\nSpécialité: " + selectedClasse.getSpecialite());

            // Ajout des boutons "OK" et "Annuler" dans l'alerte
            ButtonType buttonTypeOK = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmationAlert.getButtonTypes().setAll(buttonTypeOK, buttonTypeCancel);

            // Afficher l'alerte et attendre la réponse de l'utilisateur
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == buttonTypeOK) {
                    // Si l'utilisateur confirme, on procède à la suppression
                    int result = sessionImpl.deleteClasse(selectedClasse.getIdClasse());

                    if (result > 0) {
                        classeList.remove(selectedClasse); // Supprime de la liste observable pour actualiser la table
                        showAlert("Succès", "Classe supprimée avec succès !");
                    } else {
                        showAlert("Erreur", "Échec de la suppression de la Classe.");
                    }
                } else {
                    showAlert("Info", "Suppression annulée.");
                }
            });
        } else {
            showAlert("Erreur", "Veuillez sélectionner une Classe à supprimer.");
        }
        loadClasses(); // Recharger les données
    }

    @FXML
    private void goToHome() throws IOException {

        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/View/accueil-view.fxml")); // Remplacez par le chemin réel de la vue d'accueil
        Parent root = loader.load();
        // Obtenez la scène actuelle et changez-la
        tableClasse.getScene().setRoot(root);

    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}