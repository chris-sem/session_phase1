package com.example.demo;


import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.List;

public class ImplementationISessions implements ISessions {

    @Override
    public int createUE(String code, String designation) {
        return 0;
    }

    @Override
    public int deleteUE(int idUE) {
        return 0;
    }

    @Override
    public int createCreneau(LocalDateTime debut, LocalDateTime fin) {
        return 0;
    }

    @Override
    public int deleteCreneau(int idCreneau) {
        return 0;
    }

    @Override
    public int createClasse(int promotion, Classe.Specialite specialite) {
        return 0;
    }

    @Override
    public int deleteClasse(int idClasse) {
        return 0;
    }

    @Override
    public int createSession(String identifiant, int idUE, int idClasse, int idCreneau) {
        return 0;
    }

    @Override
    public int deleteSession(int idSession) {
        return 0;
    }

    @Override
    public int updateSession(ObservableList<Session> sessionsAsupprimer, ObservableList<Session> sessionsAcreer) {
        return 0;
    }

    @Override
    public int createMultipleSessions(String identifiant, int idUE, int idClasse, List<Integer> idCreneau) {
        return 0;
    }

    @Override
    public ObservableList<Creneau> getCreneauxDisponibles(int idClasse) {
        return null;
    }

    @Override
    public ObservableList<UniteEnseignement> getUniteEnseignement() {
        return null;
    }

    @Override
    public ObservableList<Classe> getClasse() {
        return null;
    }

    @Override
    public ObservableList<Creneau> getCreneau() {
        return null;
    }

    @Override
    public ObservableList<Session> getSession() {
        return null;
    }
}
