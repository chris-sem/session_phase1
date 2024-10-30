package isty.iatic5.session_phase1.Controller;

import isty.iatic5.session_phase1.HelloApplication;
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
    private TableColumn<Classe, Integer> promotionColumn;

    @FXML
    private TableColumn<Classe, String> specialiteColumn;

    @FXML
    private TextField promotionField;

    @FXML
    private ComboBox<String> specialiteComboBox;

    private final SessionImpl sessionImpl = new SessionImpl();

    @FXML
    public void initialize() {
        // Initialisation de la table avec les colonnes
        promotionColumn.setCellValueFactory(new PropertyValueFactory<>("promotion"));
        specialiteColumn.setCellValueFactory(new PropertyValueFactory<>("specialite"));
        // Charger les données dans la table
        loadClasses();
        specialiteComboBox.getItems().addAll(Arrays.stream(Specialite.values())
                .map(Specialite::name)
                .toArray(String[]::new));
        //specialiteComboBox.getItems().addAll(Arrays.toString(Specialite.values()));
    }

    private void loadClasses() {
        ObservableList<Classe> classes = sessionImpl.getClasse();
        tableClasse.setItems(classes);
    }

    @FXML
    private void addClasse() {
        int promotion = Integer.parseInt(promotionField.getText());
        Classe.Specialite specialite = Classe.Specialite.valueOf(specialiteComboBox.getValue());

        // Ajout de la classe à la base de données
        sessionImpl.createClasse(promotion, specialite);
        loadClasses(); // Recharger les données
    }

    @FXML
    private void removeClasse() {
        Classe selectedClasse = tableClasse.getSelectionModel().getSelectedItem();
        if (selectedClasse != null) {
            // Supposons que l'identifiant de la classe soit stocké dans un attribut 'id'
            int idClasse = selectedClasse.getIdClasse();
            System.out.println("Id de la classe à supprimer" + idClasse);
            sessionImpl.deleteClasse(idClasse); // Appel à la méthode de suppression
            loadClasses(); // Recharger les données après suppression
        }
    }

    @FXML
    private void goToHome() throws IOException {

        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/View/accueil-view.fxml")); // Remplacez par le chemin réel de la vue d'accueil
        Parent root = loader.load();
        // Obtenez la scène actuelle et changez-la
        tableClasse.getScene().setRoot(root);

    }
}