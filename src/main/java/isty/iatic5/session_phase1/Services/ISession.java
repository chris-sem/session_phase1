package isty.iatic5.session_phase1.Services;

import isty.iatic5.session_phase1.Model.Classe;
import isty.iatic5.session_phase1.Model.Creneau;
import isty.iatic5.session_phase1.Model.Session;
import isty.iatic5.session_phase1.Model.UniteEnseignement;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.List;

public interface ISession {
    public List<UniteEnseignement> getAllUEs();
    public int createUE(String code, String designation);
    public int deleteUE(int idUE);
    public int createCreneau(LocalDateTime debut, LocalDateTime fin);
    public int deleteCreneau(int idCreneau);
    public int createClasse(int promotion, Classe.Specialite specialite);
    public int deleteClasse(int idClasse); // --> Entraine la suppression des sessions avec cette classe
    public int createSession(String identifiant, int idUE, int idClasse, int idCreneau);
    public int deleteSession(int idSession); // Ne supprime pas les objets associés
    public int updateSession(List<Session> sessionsAsupprimer, List<Session> sessionsAcreer);
    public int createMultipleSessions(String identifiant, int idUE, int idClasse, List<Integer> idCreneau); //Crée une session pour chaque créneau
    public ObservableList<Creneau> getCreneauxDisponibles(int idClasse); //Pour afficher les options
    public ObservableList<UniteEnseignement> getUniteEnseignement();
    public ObservableList<Classe> getClasse();
    public ObservableList<Creneau> getCreneau();
    public ObservableList<Session> getSession();

    public LocalDateTime getMinDebutCreneau();
    public List<Creneau> getCreneauxEntreDates(LocalDateTime dateDebut, LocalDateTime dateFin);
    public int createMultipleCreneaux(List<Creneau> creneaux);
    public int deleteMultipleCreneaux(List<Creneau> creneaux);
    public void deleteSessionsByColumn(String columnName, int columnValue);
    public int GetIdSession(int idUE, int idClasse, int idCreneau);
    public int getLastSessionId();
    public ObservableList<UniteEnseignement> getUniteEnseignementByClasse(int idClasse);
    public List<Creneau> getCreneauxDisponiblesPourClasse(int idClasse, LocalDateTime dateDebut, LocalDateTime dateFin);
    public List<Creneau> getCreneauxUtilisesParUEDansClasse(int idUE, int idClasse, LocalDateTime dateDebut, LocalDateTime dateFin);
    public LocalDateTime getMinDebutCreneauUtiliseParUEDansClasse(int idUE, int idClasse);
    public LocalDateTime getMinDebutCreneauDisponiblePourClasse(int idClasse);

}
