package com.example.demo;


import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class ImplementationISessions implements ISessions {

    @Override
    public int createUE(String code, String designation) {
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
    public int deleteUE(int idUE) {
        DBConnexion db = new DBConnexion();
        String sql = "DELETE FROM unite_enseignement WHERE id = ?";
        int rowsAffected = 0;
        try {
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
        DBConnexion db = new DBConnexion();
        String sql = "INSERT INTO creneau (debut, fin) VALUES (?, ?)";
        int generatedId = 0;
        try {
            db.initPrepar(sql);
            db.getPstm().setTimestamp(1, Timestamp.valueOf(debut));
            db.getPstm().setTimestamp(2, Timestamp.valueOf(fin));

            int result = db.executeMaj();
            if (result > 0) {
                // Récupération de l'ID généré
                ResultSet rs = db.getPstm().getGeneratedKeys();
                if (rs != null && rs.next()) {
                    generatedId = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.closeConnection();
        return generatedId;
    }

    @Override
    public int deleteCreneau(int idCreneau) {
        DBConnexion db = new DBConnexion();
        String sql = "DELETE FROM sdial_creneau WHERE id = ?";
        int rowsAffected = 0;
        try {
            db.initPrepar(sql);
            db.getPstm().setInt(1, idCreneau);
            rowsAffected = db.executeMaj();
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.closeConnection();
        return (rowsAffected > 0) ? idCreneau : 0; // Retourne l'ID du créneau supprimé ou 0 si la suppression a échoué
    }

    // Méthode createClasse pour créer une classe
    @Override
    public int createClasse(int promotion, Classe.Specialite specialite) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int idClasse = -1;

        try {
            // Création d'une instance de DBConnexion et ouverture de la connexion
            DBConnexion dbConnexion = new DBConnexion();
            connection = dbConnexion.getConnection();

            // Requête pour insérer une nouvelle classe
            String sql = "INSERT INTO Classe (promotion, specialite) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, promotion);
            preparedStatement.setString(2, specialite.name());

            // Exécution de la requête
            int rowsAffected = preparedStatement.executeUpdate();

            // Récupération de l'ID généré
            if (rowsAffected > 0) {
                resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    idClasse = resultSet.getInt(1);
                }
            }

            // Vérification et confirmation de la création de la classe
            if (idClasse > 0) {
                System.out.println("Classe créée avec succès. ID: " + idClasse);
            } else {
                System.out.println("Erreur lors de la création de la classe.");
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

        return idClasse;
    }


    // Méthode deleteClasse pour supprimer une classe en fonction de son ID
    @Override
    public int deleteClasse(int idClasse) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int rowsAffected = 0;

        try {
            // Création d'une instance de DBConnexion et ouverture de la connexion
            DBConnexion dbConnexion = new DBConnexion();
            connection = dbConnexion.getConnection();

            // Pré-traitement : Suppression des sessions associées à cette classe
            String deleteSessionsSql = "DELETE FROM Session WHERE id_classe = ?";
            preparedStatement = connection.prepareStatement(deleteSessionsSql);
            preparedStatement.setInt(1, idClasse);
            int sessionsDeleted = preparedStatement.executeUpdate();
            System.out.println(sessionsDeleted + " sessions supprimées pour la classe avec ID " + idClasse);

            // Requête SQL pour supprimer la classe
            String deleteClasseSql = "DELETE FROM Classe WHERE id = ?";
            preparedStatement = connection.prepareStatement(deleteClasseSql);
            preparedStatement.setInt(1, idClasse);
            rowsAffected = preparedStatement.executeUpdate();

            // Vérification du succès de la suppression
            if (rowsAffected > 0) {
                System.out.println("Classe avec ID " + idClasse + " supprimée avec succès.");
            } else {
                System.out.println("Aucune classe trouvée avec l'ID " + idClasse + ".");
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
