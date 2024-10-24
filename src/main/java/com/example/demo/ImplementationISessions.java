package com.example.demo;


import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class ImplementationISessions implements ISessions {

    @Override
    public int createUE(String code, String designation) {  ////Works
        int idUE = -1; // Valeur par défaut si l'insertion échoue
        DBConnexion dbConnexion = new DBConnexion();

        try {
            // Préparation de la requête SQL en demandant de récupérer les clés générées
            String sql = "INSERT INTO unite_enseignement (code, designation) VALUES (?, ?)";
            dbConnexion.getConnection();
            PreparedStatement pstm = dbConnexion.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Remplissage des paramètres de la requête
            pstm.setString(1, code);
            pstm.setString(2, designation);

            // Exécution de la mise à jour (INSERT)
            int rowsAffected = pstm.executeUpdate();

            // Si l'insertion est réussie, on récupère l'ID généré
            if (rowsAffected > 0) {
                ResultSet rs = pstm.getGeneratedKeys();
                if (rs.next()) {
                    idUE = rs.getInt(1); // Récupération de l'ID auto-généré
                }
                rs.close();
            }
            // Fermeture du PreparedStatement
            pstm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fermeture de la connexion
            dbConnexion.closeConnection();
        }

        return idUE;
    }


    @Override
    public int deleteUE(int idUE) {    /////Works
        DBConnexion db = new DBConnexion();
        String sql = "DELETE FROM unite_enseignement WHERE id = ?";
        String sqlDeleteSessions = "SELECT id FROM session WHERE id_ue = ?";
        int rowsAffected = 0;
        try {
            db.initPrepar(sqlDeleteSessions);
            db.getPstm().setInt(1, idUE);
            ResultSet rs = db.executeSelect();
            // 2. Boucler sur chaque session trouvée et appeler deleteSession
            while (rs.next()) {
                int idSession = rs.getInt("id");
                deleteSession(idSession);  // Appel à la fonction de suppression de session
            }
            db.initPrepar(sql);
            db.getPstm().setInt(1, idUE);
            rowsAffected = db.executeMaj();
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.closeConnection();
        return (rowsAffected > 0) ? idUE : 0; // Retourne l'ID de l'UE supprimée ou 0 si la suppression a échoué
    }


    @Override
    public int createCreneau(LocalDateTime debut, LocalDateTime fin) {
        int idCreneau = -1; // Valeur par défaut si l'insertion échoue
        DBConnexion dbConnexion = new DBConnexion();

        try {
            // Préparation de la requête SQL en demandant de récupérer les clés générées
            String sql = "INSERT INTO creneau (debut, fin) VALUES (?, ?)";
            dbConnexion.getConnection();
            PreparedStatement pstm = dbConnexion.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Conversion des LocalDateTime en Timestamp (car SQL attend un format temporel compatible)
            Timestamp debutTimestamp = Timestamp.valueOf(debut);
            Timestamp finTimestamp = Timestamp.valueOf(fin);

            // Remplissage des paramètres de la requête
            pstm.setTimestamp(1, debutTimestamp);
            pstm.setTimestamp(2, finTimestamp);

            // Exécution de la mise à jour (INSERT)
            int rowsAffected = pstm.executeUpdate();

            // Si l'insertion est réussie, on récupère l'ID généré
            if (rowsAffected > 0) {
                ResultSet rs = pstm.getGeneratedKeys();
                if (rs.next()) {
                    idCreneau = rs.getInt(1); // Récupération de l'ID auto-généré
                }
                rs.close();
            }
            // Fermeture du PreparedStatement
            pstm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fermeture de la connexion
            dbConnexion.closeConnection();
        }

        return idCreneau;
    }

    @Override
    public int deleteCreneau(int idCreneau) {
        DBConnexion db = new DBConnexion();
        String sqlDeleteSessions = "SELECT id FROM session WHERE id_creneau = ?";
        String sql = "DELETE FROM creneau WHERE id = ?";
        int rowsAffected = 0;
        try {
            db.initPrepar(sqlDeleteSessions);
            db.getPstm().setInt(1, idCreneau);
            ResultSet rs = db.executeSelect();
            // 2. Boucler sur chaque session trouvée et appeler deleteSession
            while (rs.next()) {
                int idSession = rs.getInt("id");
                deleteSession(idSession);  // Appel à la fonction de suppression de session
            }
            db.initPrepar(sql);
            db.getPstm().setInt(1, idCreneau);
            rowsAffected = db.executeMaj();
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.closeConnection();
        return (rowsAffected > 0) ? idCreneau : 0; // Retourne l'ID du créneau supprimé ou 0 si la suppression a échoué
    }

    @Override
    public int createClasse(int promotion, Classe.Specialite specialite) {
        int idClasse = -1; // Valeur par défaut si l'insertion échoue
        DBConnexion dbConnexion = new DBConnexion();
        try {
            // Préparation de la requête SQL en demandant de récupérer les clés générées
            String sql = "INSERT INTO classe (promotion, specialite) VALUES (?, ?)";
            dbConnexion.getConnection();
            PreparedStatement pstm = dbConnexion.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            // Remplissage des paramètres de la requête
            pstm.setInt(1, promotion);
            pstm.setString(2, specialite.name()); // On utilise la méthode name() pour convertir l'enum en chaîne de caractères

            // Exécution de la mise à jour (INSERT)
            int rowsAffected = pstm.executeUpdate();

            // Si l'insertion est réussie, on récupère l'ID généré
            if (rowsAffected > 0) {
                ResultSet rs = pstm.getGeneratedKeys();
                if (rs.next()) {
                    idClasse = rs.getInt(1); // Récupération de l'ID auto-généré
                }
                rs.close();
            }
            // Fermeture du PreparedStatement
            pstm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fermeture de la connexion
            dbConnexion.closeConnection();
        }
        return idClasse;
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
