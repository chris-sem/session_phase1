package isty.iatic5.session_phase1.Controller;

import isty.iatic5.session_phase1.Fonctionnalites.Model.Classe;
import isty.iatic5.session_phase1.Fonctionnalites.Model.Creneau;
import isty.iatic5.session_phase1.Fonctionnalites.Model.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static isty.iatic5.session_phase1.Application.Main.sessionImpl;

public class CreneauSessionController {

    @FXML
    private TableView<String[]> tableView;

    @FXML
    private TextField numeroSemaineField;

    @FXML
    private TextField numeroAnneeField;

    @FXML
    private Label dateRangeLabel;

    @FXML
    private Button checkModificationsButton;

    private int numero_semaine;  // Contient le numéro de la semaine
    private int numero_annee;    // Contient l'année

    private LocalDate datePivot; // Date pivot à partir de laquelle calculer les valeurs
    private LocalDate premierLundiDeLaSemaine; // Stocke la date du premier lundi de la semaine

    // Listes pour gérer la disponibilité et les éléments déjà pris
    private List<Creneau> disponibilite;
    private List<Creneau> disponibiliteInitial;
    private List<Creneau> dejaPris;
    private List<Creneau> dejaPrisInitial;
    private List<Creneau> nouveauxElements_dejaPris;
    private List<Creneau> elementsManquants_dejaPris;
    private List<Creneau> nouveauxElements_disponibilite;
    private List<Creneau> elementsManquants_disponibilite;

    private boolean update = true;
    private int idClasse;
    private int idUE;

    @FXML
    private void initialize() {

    }

    public void initializeAfterSettingValues(){

        double tableWidth = 630.0; // Largeur totale souhaitée pour la TableView (correspondant à 7 colonnes)
        double columnWidth = tableWidth / 7;

        setupVariables(0, 0, 0);

        // Synchroniser les champs avec les valeurs par défaut
        numeroSemaineField.setText(String.valueOf(numero_semaine));
        numeroAnneeField.setText(String.valueOf(numero_annee));

        // Désactiver la sélection de ligne complète
        tableView.getSelectionModel().setCellSelectionEnabled(true);
        tableView.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.SINGLE);

        // Créer les colonnes (7 colonnes)
        for (int col = 0; col < 7; col++) {
            TableColumn<String[], String> tableColumn = new TableColumn<>(getNomJourAvecNumero(col + 1));
            tableColumn.setPrefWidth(columnWidth);

            final int columnIndex = col;

            // Colorier les cellules en fonction des coordonnées dans disponibilite et dejaPris
            tableColumn.setCellFactory(new Callback<>() {
                @Override
                public TableCell<String[], String> call(TableColumn<String[], String> param) {
                    return new TableCell<>() {
                        {
                            setOnMouseClicked(event -> {
                                if (!isEmpty()) {
                                    int rowIndex = getTableRow().getIndex() + 1;
                                    int colIndex = columnIndex + 1;

                                    // Trouver l'objet Creneau exact
                                    Creneau creneauExact = trouverCreneauExact(rowIndex, colIndex);

                                    if (creneauExact != null) {
                                        checkModificationsButton.setDisable(false);

                                        if (!update) {
                                            // Mode sans update : gérer uniquement la liste "disponibilite"
                                            if (disponibilite.contains(creneauExact)) {
                                                disponibilite.remove(creneauExact);
                                            } else {
                                                disponibilite.add(creneauExact);
                                            }
                                        } else {
                                            // Mode avec update : gérer à la fois "disponibilite" et "dejaPris"
                                            if (disponibilite.contains(creneauExact)) {
                                                disponibilite.remove(creneauExact);
                                                dejaPris.add(creneauExact);
                                            } else if (dejaPris.contains(creneauExact)) {
                                                dejaPris.remove(creneauExact);
                                                disponibilite.add(creneauExact);
                                            }
                                        }
                                        updateTableColors();
                                    }
                                }
                            });
                        }

                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || getTableRow() == null) {
                                setText(null);
                                setStyle("");
                            } else {
                                int rowIndex = getTableRow().getIndex() + 1;
                                int colIndex = columnIndex + 1;

                                Creneau creneauExact = trouverCreneauExact(rowIndex, colIndex);

                                if (creneauExact != null) {
                                    // Appliquer le style en fonction de la liste où se trouve creneauExact
                                    if (disponibilite.contains(creneauExact)) {
                                        setStyle("-fx-background-color: lightgreen;");
                                    } else if (update && dejaPris.contains(creneauExact)) {
                                        setStyle("-fx-background-color: pink;");
                                    } else {
                                        setStyle("");
                                    }
                                } else {
                                    setStyle("");
                                }
                                setText(item);
                            }
                        }
                    };
                }
            });
            tableView.getColumns().add(tableColumn);

        }

        ObservableList<String[]> data = FXCollections.observableArrayList();
        for (int row = 0; row < 12; row++) {
            data.add(new String[7]);
        }
        tableView.setItems(data);
    }

    private void setupVariables(int semaineOffset, int numSemaine, int numAnnee) {
        // Déterminer la date pivot en fonction des paramètres
        if (semaineOffset == 0) {
            LocalDateTime dateTemp = sessionImpl.getMinDebutCreneauUtiliseParUEDansClasse(idUE, idClasse);

            if (dateTemp != null) {
                datePivot = dateTemp.toLocalDate() ;
            }else{
                dateTemp = sessionImpl.getMinDebutCreneauDisponiblePourClasse(idClasse);

                if (dateTemp != null) {
                    datePivot = dateTemp.toLocalDate() ;
                }else{
                    datePivot = LocalDate.now();
                }
            }

            //datePivot = LocalDateTime.of(2024, 10, 1, 8, 0, 0).toLocalDate();
        } else if (semaineOffset == -1) {
            datePivot = premierLundiDeLaSemaine.minusWeeks(1);  // Lundi de la semaine passée
        } else if (semaineOffset == 1) {
            datePivot = premierLundiDeLaSemaine.plusWeeks(1);   // Lundi de la semaine prochaine
        } else if (semaineOffset == 2) {
            // Utiliser numSemaine et numAnnee pour définir la date pivot
            try {
                datePivot = getLundiDeSemaine(numSemaine, numAnnee);
            } catch (Exception e) {
                datePivot = LocalDate.now();
            }
        } else {
            throw new IllegalArgumentException("Valeur de semaineOffset invalide. Utilisez -1, 0, 1, ou 2.");
        }

        if(datePivot == null){
            datePivot = LocalDate.now();
        }

        // Calcul des valeurs de semaine, année, et premier lundi basé sur la date pivot
        calculerDateSemaine(datePivot);

        // Mise à jour du label dynamique pour les dates de la semaine
        updateDateRangeLabel();

        disponibilite = sessionImpl.getCreneauxDisponiblesPourClasse(idClasse, premierLundiDeLaSemaine.atTime(0,0), premierLundiDeLaSemaine.plusDays(6).atTime(23,59));

        dejaPris = sessionImpl.getCreneauxUtilisesParUEDansClasse(idUE, idClasse, premierLundiDeLaSemaine.atTime(0,0), premierLundiDeLaSemaine.plusDays(6).atTime(23,59));

        // Création des copies initiales pour le suivi des modifications
        disponibiliteInitial = new ArrayList<>(disponibilite);
        dejaPrisInitial = new ArrayList<>(dejaPris);

        // Initialisation des listes de modifications
        nouveauxElements_dejaPris = new ArrayList<>();
        elementsManquants_dejaPris = new ArrayList<>();
        nouveauxElements_disponibilite = new ArrayList<>();
        elementsManquants_disponibilite = new ArrayList<>();
    }

    private void updateDateRangeLabel() {
        try {
            // Calculer la date du lundi de la semaine en utilisant la méthode getLundiDeSemaine
            LocalDate lundi = getLundiDeSemaine(numero_semaine, numero_annee);
            LocalDate dimanche = lundi.plusDays(6); // Dimanche = Lundi + 6 jours

            // Mettre à jour le label avec les vraies dates du lundi et du dimanche
            dateRangeLabel.setText("Semaine de Lundi " + lundi + " au Dimanche " + dimanche);
        } catch (Exception e) {
            // En cas d'erreur, afficher un message dans le label
            dateRangeLabel.setText("Erreur : veuillez vérifier le numéro de semaine et l'année.");
        }
    }

    private void mettreAJourEntetesDeColonnes() {
        for (int col = 0; col < 7; col++) {
            // Mettre à jour l'en-tête de la colonne en appelant `getNomJourAvecNumero()`
            TableColumn<String[], String> tableColumn = (TableColumn<String[], String>) tableView.getColumns().get(col);
            tableColumn.setText(getNomJourAvecNumero(col + 1));
        }
    }

    // Méthode appelée par le bouton "Semaine précédente"
    @FXML
    private void semainePrecedente() {
        // Logique pour accéder à la semaine précédente (à implémenter)
        // Appeler setupVariables avec -1 pour passer à la semaine précédente
        setupVariables(-1, 0, 0);

        mettreAJourEntetesDeColonnes();
        // Mettre à jour le contenu de la TableView
        updateTableColors();
    }

    // Méthode appelée par le bouton "Semaine suivante"
    @FXML
    private void semaineSuivante() {
        // Logique pour accéder à la semaine suivante (à implémenter)
        // Appeler setupVariables avec -1 pour passer à la semaine précédente
        setupVariables(1, 0, 0);

        mettreAJourEntetesDeColonnes();

        // Mettre à jour le contenu de la TableView
        updateTableColors();
    }

    // Méthode appelée par le bouton "Afficher modification"
    @FXML
    private void checkModifications() {
        mettreAJourListesDeModifications();
    }

    public List<Session> createListeSessions(int idUE, int idClasse, List<Creneau> creneaux) {
        List<Session> sessions = new ArrayList<>();
        for (Creneau creneau : creneaux) {
            // Génération d'un identifiant unique pour chaque session
            String identifiant = "UE" + idUE + "-CL" + idClasse + "-CR" + creneau.getIdCreneau();

            // Création d'une nouvelle session à partir des données fournies
            Session session = new Session(
                    0, // idSession (pas encore défini, car il sera généré par la base de données)
                    identifiant,
                    idUE,
                    "UE_CODE_DEFAULT", // Valeur par défaut pour le code de l'unité d'enseignement
                    "UE_DESIGNATION_DEFAULT", // Valeur par défaut pour la désignation de l'unité d'enseignement
                    idClasse,
                    2024, // Valeur par défaut pour la promotion
                    Classe.Specialite.MT, // Valeur par défaut pour la spécialité de la classe
                    creneau.getIdCreneau(),
                    creneau.getDebut() != null ? creneau.getDebut() : LocalDateTime.now(), // Valeur par défaut pour le début
                    creneau.getFin() != null ? creneau.getFin() : LocalDateTime.now().plusHours(1) // Valeur par défaut pour la fin
            );

            // Ajout de la session créée à la liste des sessions
            sessions.add(session);
        }

        return sessions;
    }

    private void mettreAJourListesDeModifications() {
        // Nettoyer les listes de modifications
        nouveauxElements_dejaPris.clear();
        elementsManquants_dejaPris.clear();
        nouveauxElements_disponibilite.clear();
        elementsManquants_disponibilite.clear();

        // Vérifier les éléments ajoutés et supprimés dans dejaPris
        for (Creneau element : dejaPris) {
            if (!dejaPrisInitial.contains(element)) {
                nouveauxElements_dejaPris.add(element);
            }
        }
        for (Creneau element : dejaPrisInitial) {
            if (!dejaPris.contains(element)) {
                elementsManquants_dejaPris.add(element);
            }
        }

        // Vérifier les éléments ajoutés et supprimés dans disponibilite
        for (Creneau element : disponibilite) {
            if (!disponibiliteInitial.contains(element)) {
                nouveauxElements_disponibilite.add(element);
            }
        }
        for (Creneau element : disponibiliteInitial) {
            if (!disponibilite.contains(element)) {
                elementsManquants_disponibilite.add(element);
            }
        }


        /////////////////////////////////////////////////////////////////////////////////////////
        // Afficher l'état des listes de modifications dans la console
        System.out.println("Éléments ajoutés dans dejaPris : " + nouveauxElements_dejaPris);
        System.out.println("Éléments supprimés de dejaPris : " + elementsManquants_dejaPris);

        List<Session> listeDeSessionAAjouter =  createListeSessions(idUE, idClasse, nouveauxElements_dejaPris);
        List<Session> listeDeSessionASupprimees =  createListeSessions(idUE, idClasse, elementsManquants_dejaPris);

        sessionImpl.updateSession(listeDeSessionASupprimees, listeDeSessionAAjouter);
        /////////////////////////////////////////////////////////////////////////////////////

        System.out.println("Éléments ajoutés dans disponibilite : " + nouveauxElements_disponibilite);
        System.out.println("Éléments supprimés de disponibilite : " + elementsManquants_disponibilite);
        System.out.println("\n");

        checkModificationsButton.setDisable(true);

        setupVariables(2, numero_semaine,  numero_annee);
    }

    // Méthode pour rafraîchir et mettre à jour les couleurs du tableau
    private void updateTableColors() {
        tableView.refresh();
    }

    private Creneau trouverCreneauExact(int ligne, int colonne) {
        // Rechercher dans la liste des créneaux disponibles
        for (Creneau creneau : disponibilite) {
            if (creneau.getLigne() == ligne && creneau.getColonne() == colonne) {
                return creneau;
            }
        }

        // Rechercher dans la liste des créneaux déjà pris
        for (Creneau creneau : dejaPris) {
            if (creneau.getLigne() == ligne && creneau.getColonne() == colonne) {
                return creneau;
            }
        }

        // Si aucun créneau n'est trouvé, créer un nouveau créneau en utilisant les coordonnées et premierLundiDeLaSemaine
        if (ligne >= 1 && ligne <= 12 && colonne >= 1 && colonne <= 7) {
            // Calculer la date de début du créneau en fonction de la ligne et de la colonne
            LocalDateTime dateDebut = premierLundiDeLaSemaine
                    .plusDays(colonne - 1)            // Décalage en jours pour obtenir le jour de la semaine
                    .atTime(7 + (ligne - 1), 0);      // Heure de début en fonction de la ligne

            // Calculer la date de fin du créneau
            LocalDateTime dateFin = dateDebut.plusHours(1);

            // Créer et retourner le nouveau créneau
            return new Creneau(-1, dateDebut, dateFin); // Utilisez -1 comme identifiant temporaire
        }

        // Si les coordonnées ne sont pas valides, retourner null
        return null;
    }

    private String getNomJourAvecNumero(int numero) {
        // Vérifier que le numéro est compris entre 1 et 7
        if (numero < 1 || numero > 7) {
            throw new IllegalArgumentException("Le numéro doit être compris entre 1 et 7.");
        }

        // Calculer la date en fonction du numéro (Lundi = 1, Mardi = 2, etc.)
        LocalDate date = premierLundiDeLaSemaine.plusDays(numero - 1);

        // Obtenir le nom du jour
        String nomJour;
        switch (numero) {
            case 1 -> nomJour = "Lundi";
            case 2 -> nomJour = "Mardi";
            case 3 -> nomJour = "Mercredi";
            case 4 -> nomJour = "Jeudi";
            case 5 -> nomJour = "Vendredi";
            case 6 -> nomJour = "Samedi";
            case 7 -> nomJour = "Dimanche";
            default -> throw new IllegalArgumentException("Numéro de jour invalide.");
        }

        // Retourner le nom du jour avec le numéro du jour dans le mois
        return nomJour + " " + date.getDayOfMonth();
    }

    private LocalDate getLundiDeSemaine(int semaine, int annee) {
        // Obtenez la première semaine de l'année spécifiée
        LocalDate date = LocalDate.of(annee, 1, 1)
                .with(WeekFields.of(Locale.getDefault()).weekOfYear(), semaine)
                .with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1); // 1 correspond à Lundi

        return date;
    }

    @FXML
    private void accederSemaine() {
        try {
            // Récupérer le numéro de semaine et l'année des champs de texte
            numero_semaine = Integer.parseInt(numeroSemaineField.getText());
            numero_annee = Integer.parseInt(numeroAnneeField.getText());

            setupVariables(2, numero_semaine, numero_annee);

            premierLundiDeLaSemaine = datePivot;

            mettreAJourEntetesDeColonnes();

            // Mettre à jour le label avec les vraies dates de lundi et dimanche
            updateDateRangeLabel();

            // Mettre à jour le contenu de la TableView
            updateTableColors();
        } catch (NumberFormatException e) {
            // Afficher un message d'erreur dans le label en cas d'erreur de format
            dateRangeLabel.setText("Erreur : veuillez entrer un numéro de semaine et une année valides.");
        }
    }

    private void calculerDateSemaine(LocalDate date) {
        // Obtenir le numéro de la semaine et l'année à partir de la date pivot
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        numero_semaine = date.get(weekFields.weekOfWeekBasedYear());
        numero_annee = date.getYear();

        // Calculer le premier lundi de la semaine
        premierLundiDeLaSemaine = date.with(weekFields.dayOfWeek(), 1); // 1 correspond au lundi
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public void setIdClasse(int idClasse) {
        this.idClasse = idClasse;
    }

    public void setIdUE(int idUE) {
        this.idUE = idUE;
    }

}