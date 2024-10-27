package com.example.demo;


import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    // Méthode createSession pour créer une session
    @Override
    public int createSession(String identifiant, int idUE, int idClasse, int idCreneau) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int idSession = -1;

        try {
            // Création d'une instance de DBConnexion et ouverture de la connexion
            DBConnexion dbConnexion = new DBConnexion();
            connection = dbConnexion.getConnection();

            // Requête pour insérer une nouvelle session
            String sql = "INSERT INTO Session (identifiant, id_ue, id_classe, id_creneau) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, identifiant);
            preparedStatement.setInt(2, idUE);
            preparedStatement.setInt(3, idClasse);
            preparedStatement.setInt(4, idCreneau);

            // Exécution de la requête
            int rowsAffected = preparedStatement.executeUpdate();

            // Récupération de l'ID généré
            if (rowsAffected > 0) {
                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    idSession = resultSet.getInt(1); // Récupération de l'ID de la session
                }
            }

            // Vérification et confirmation de la création de la session
            if (idSession > 0) {
                System.out.println("Session créée avec succès. ID: " + idSession);
            } else {
                System.out.println("Erreur lors de la création de la session.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fermeture des ressources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return idSession;
    }

    // Méthode deleteSession pour supprimer une session en fonction de son ID
    @Override
    public int deleteSession(int idSession) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int rowsAffected = 0;

        try {
            // Création d'une instance de DBConnexion et ouverture de la connexion
            DBConnexion dbConnexion = new DBConnexion();
            connection = dbConnexion.getConnection();

            // Requête SQL pour supprimer une session en fonction de son ID
            String sql = "DELETE FROM Session WHERE id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, idSession);

            // Exécution de la requête
            rowsAffected = preparedStatement.executeUpdate();

            // Vérification du succès de la suppression
            if (rowsAffected > 0) {
                System.out.println("Session avec ID " + idSession + " supprimée avec succès.");
            } else {
                System.out.println("Aucune session trouvée avec l'ID " + idSession + ".");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fermeture des ressources
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return rowsAffected;
    }

    @Override
    public int updateSession(ObservableList<Integer> sessionsAsupprimer, ObservableList<Session> sessionsAcreer) {
        int resultat = 0;

        // Supprimer les sessions dans sessionsAsupprimer
        for (int idSession : sessionsAsupprimer) {
            resultat += deleteSession(idSession); // Suppression et accumulation des résultats
        }

        // Créer les nouvelles sessions dans sessionsAcreer
        for (Session sessionData : sessionsAcreer) {
            String identifiant = sessionData.getIdentifiant();
            int idUE = sessionData.getUe().getIdUE();
            int idClasse = sessionData.getClasse().getIdClasse();
            int idCreneau = sessionData.getCreneau().getIdCreneau();
            resultat += createSession(identifiant, idUE, idClasse, idCreneau); // Création de la session

        }

        return resultat;
    }


    //works
    @Override
    public int createMultipleSessions(String identifiant, int idUE, int idClasse, List<Integer> idCreneau) {
        int sessionsCreated = 0;

        for (int idC : idCreneau) {
            int result = createSession(identifiant, idUE, idClasse, idC);
            if (result > 0) {  // Assuming createSession returns a positive ID if successful
                sessionsCreated++;
            } else {
                System.out.println("Erreur : impossible de créer la session pour le créneau " + idC);
            }
        }

        return sessionsCreated;
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
