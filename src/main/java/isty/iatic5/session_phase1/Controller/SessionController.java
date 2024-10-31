package isty.iatic5.session_phase1.Controller;

import isty.iatic5.session_phase1.HelloApplication;
import isty.iatic5.session_phase1.Services.ISession;
import isty.iatic5.session_phase1.Services.SessionImpl;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import isty.iatic5.session_phase1.Model.Classe;
import isty.iatic5.session_phase1.Model.UniteEnseignement;

import java.io.IOException;

public class SessionController {

    @FXML
    private TextField rechercheClasseField;
    @FXML
    private TextField rechercheUEField;
    @FXML
    private TableView<Classe> classeTableView;
    @FXML
    private TableColumn<Classe, Integer> classeIdColumn;
    @FXML
    private TableColumn<Classe, Integer> classePromotionColumn;
    @FXML
    private TableColumn<Classe, Classe.Specialite> classeSpecialiteColumn;
    @FXML
    private TableView<UniteEnseignement> ueTableView;
    @FXML
    private TableColumn<UniteEnseignement, Integer> ueIdColumn;
    @FXML
    private TableColumn<UniteEnseignement, String> ueCodeColumn;
    @FXML
    private TableColumn<UniteEnseignement, String> ueDesignationColumn;
    @FXML
    private Label selectionClasseLabel;
    @FXML
    private Label selectionUELabel;
    @FXML
    private Button gererSessionsButton;
    @FXML
    private Pane contentPane;

    private final ISession sessionInterface = new SessionImpl();

    private ObservableList<Classe> classeData = FXCollections.observableArrayList();
    private ObservableList<UniteEnseignement> ueData = FXCollections.observableArrayList();

    private Classe classeSelectionnee;
    private UniteEnseignement ueSelectionnee;

    @FXML
    private void initialize() {
        setupTableColumns();
        loadClasseData();
        loadUEData(); // Charger toutes les UEs dès l'initialisation
        gererSessionsButton.setDisable(true);

        // Filtrage dynamique pour le champ de recherche des classes
        rechercheClasseField.textProperty().addListener((observable, oldValue, newValue) -> {
            classeTableView.setItems(filterClasseData(newValue));
        });

        // Filtrage dynamique pour le champ de recherche des UEs
        rechercheUEField.textProperty().addListener((observable, oldValue, newValue) -> {
            ueTableView.setItems(filterUEData(newValue));
        });

        // Listener pour la sélection d'une classe
        classeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedClasse) -> {
            classeSelectionnee = selectedClasse;
            if (selectedClasse != null) {
                selectionClasseLabel.setText("Classe sélectionnée: " + selectedClasse.getPromotion() + " - " + selectedClasse.getSpecialite());
            } else {
                selectionClasseLabel.setText("Aucune classe sélectionnée");
            }
            updateGererSessionsButton();
        });

        // Listener pour la sélection d'une UE
        ueTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedUE) -> {
            ueSelectionnee = selectedUE;
            if (selectedUE != null) {
                selectionUELabel.setText("UE sélectionnée: " + selectedUE.getCode() + " - " + selectedUE.getDesignation());
            } else {
                selectionUELabel.setText("Aucune UE sélectionnée");
            }
            updateGererSessionsButton();
        });
    }

    private void setupTableColumns() {
        // Configuration des colonnes de la table des classes
        classeIdColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getIdClasse()));
        classePromotionColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getPromotion()));
        classeSpecialiteColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getSpecialite()));

        // Configuration des colonnes de la table des unités d'enseignement
        ueIdColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getIdUE()));
        ueCodeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCode()));
        ueDesignationColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDesignation()));
    }

    private void loadClasseData() {
        classeData = sessionInterface.getClasse();
        classeTableView.setItems(classeData);
    }

    private void loadUEData() {
        ueData = sessionInterface.getUniteEnseignement(); // Charger toutes les UEs sans filtrage par classe
        ueTableView.setItems(ueData);
    }

    private ObservableList<Classe> filterClasseData(String query) {
        if (query == null || query.isEmpty()) {
            return classeData; // Retourne toutes les classes si la requête est vide
        }
        String lowerCaseQuery = query.toLowerCase();
        ObservableList<Classe> filteredData = FXCollections.observableArrayList();
        for (Classe classe : classeData) {
            if (String.valueOf(classe.getPromotion()).contains(lowerCaseQuery) ||
                    classe.getSpecialite().toString().toLowerCase().contains(lowerCaseQuery)) {
                filteredData.add(classe);
            }
        }
        return filteredData;
    }

    private ObservableList<UniteEnseignement> filterUEData(String query) {
        if (query == null || query.isEmpty()) {
            return ueData; // Retourne toutes les UEs si la requête est vide
        }
        String lowerCaseQuery = query.toLowerCase();
        ObservableList<UniteEnseignement> filteredData = FXCollections.observableArrayList();
        for (UniteEnseignement ue : ueData) {
            if (ue.getCode().toLowerCase().contains(lowerCaseQuery) ||
                    ue.getDesignation().toLowerCase().contains(lowerCaseQuery)) {
                filteredData.add(ue);
            }
        }
        return filteredData;
    }

    private void updateGererSessionsButton() {
        boolean enableGererSessions = classeSelectionnee != null && ueSelectionnee != null;
        gererSessionsButton.setDisable(!enableGererSessions);
    }

    @FXML
    private void gererSessionsButton() {
        try {
            // Charger le FXML du CreneauController
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/creneau-session-view.fxml"));
            Pane root = loader.load();

            // Récupérer le contrôleur associé
            CreneauSessionController gestionCreneauSessionController = loader.getController();

            // Passer les valeurs nécessaires
            gestionCreneauSessionController.setUpdate(true);
            gestionCreneauSessionController.setIdClasse(classeSelectionnee.getIdClasse());
            gestionCreneauSessionController.setIdUE(ueSelectionnee.getIdUE());

            // Appeler la méthode pour initialiser le contrôleur après avoir défini les valeurs
            gestionCreneauSessionController.initializeAfterSettingValues();

            // Charger la vue des créneaux dans la zone contentPane de la fenêtre principale
            contentPane.getChildren().clear();

            // Centrer le contenu dans le contentPane en utilisant StackPane
            StackPane stackPane = new StackPane(root);
            stackPane.setPrefSize(contentPane.getWidth(), contentPane.getHeight());

            StackPane.setAlignment(root, Pos.CENTER); // Centrer le contenu au milieu du StackPane

            contentPane.getChildren().add(stackPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void goToHome() throws IOException {

        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("/View/accueil-view.fxml")); // Remplacez par le chemin réel de la vue d'accueil
        Parent root = loader.load();
        // Obtenez la scène actuelle et changez-la
        classeTableView.getScene().setRoot(root);

    }
}
