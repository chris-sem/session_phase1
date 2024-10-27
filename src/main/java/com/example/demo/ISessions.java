package com.example.demo;

import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.List;

public interface ISessions {
    // Oscar Debut //
    public int createUE(String code, String designation); //Renvoi l'id de l'objet si créé et 0 sinon
    public int deleteUE(int idUE); // Renvoi 0 ou 1 --> Entraine la suppression des sessions avec cette UE
    public int createCreneau(LocalDateTime debut, LocalDateTime fin);
    public int deleteCreneau(int idCreneau);// --> Entraine la suppression des sessions avec ce creneau
    // Oscar Fin //
    // Myriam Debut //
    public int createClasse(int promotion, Classe.Specialite specialite);
    public int deleteClasse(int idClasse); // --> Entraine la suppression des sessions avec cette classe
    public int createSession(String identifiant, int idUE, int idClasse, int idCreneau);
    public int deleteSession(int idSession); // Ne supprime pas les objets associés
    //Myriam Fin //
    // Houda debut //
    public int updateSession(ObservableList<Integer> sessionsAsupprimer, ObservableList<Session> sessionsAcreer);
    public int createMultipleSessions(String identifiant, int idUE, int idClasse, List<Integer> idCreneau); //Crée une session pour chaque créneau
    // Houda Fin //
    ////////////////////////////////////////////////////--------------Fonctions non éxigées--------------////////////////////////////////////////////////////

    // Chris Debut //
    public ObservableList<Creneau> getCreneauxDisponibles(int idClasse); //Pour afficher les options
    public ObservableList<UniteEnseignement> getUniteEnseignement();
    public ObservableList<Classe> getClasse();
    public ObservableList<Creneau> getCreneau();
    public ObservableList<Session> getSession();
    // Chris Fin //



}
