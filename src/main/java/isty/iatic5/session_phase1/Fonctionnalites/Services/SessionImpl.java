package isty.iatic5.session_phase1.Fonctionnalites.Services;

import isty.iatic5.session_phase1.Fonctionnalites.Model.Classe;
import isty.iatic5.session_phase1.Fonctionnalites.Model.Creneau;
import isty.iatic5.session_phase1.Fonctionnalites.Model.Session;
import isty.iatic5.session_phase1.Fonctionnalites.Model.UniteEnseignement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class SessionImpl implements ISession {
    private DBConnexion db = new DBConnexion();

    @Override
    public int createMultipleCreneaux(List<Creneau> creneaux) {
        int result = 0;
        String sql = "INSERT INTO creneau (debut, fin) VALUES (?, ?)";
        try {
            db.initPrepar(sql);
            for (Creneau creneau : creneaux) {
                db.getPstm().setObject(1, creneau.getDebut());
                db.getPstm().setObject(2, creneau.getFin());
                result += db.executeMaj();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return result;
    }

    @Override
    public int deleteMultipleCreneaux(List<Creneau> creneaux) {
        int result = 0;
        String deleteCreneauSql = "DELETE FROM creneau WHERE id = ?";

        try {
            for (Creneau creneau : creneaux) {
                // Supprimer toutes les sessions associées à ce créneau
                deleteSessionsByColumn("id_creneau", creneau.getIdCreneau());

                // Supprimer le créneau lui-même
                db.initPrepar(deleteCreneauSql);
                db.getPstm().setInt(1, creneau.getIdCreneau());
                result += db.executeMaj();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }

        return result;
    }

    @Override
    public List<Creneau> getCreneauxEntreDates(LocalDateTime dateDebut, LocalDateTime dateFin) {
        List<Creneau> creneaux = new ArrayList<>();
        String sql = "SELECT * FROM creneau WHERE debut >= ? AND debut <= ?";
        db.initPrepar(sql);
        try {
            db.getPstm().setObject(1, dateDebut);
            db.getPstm().setObject(2, dateFin);
            ResultSet rs = db.executeSelect();
            while (rs.next()) {
                creneaux.add(new Creneau(
                        rs.getInt("id"),
                        rs.getTimestamp("debut").toLocalDateTime(),
                        rs.getTimestamp("fin").toLocalDateTime()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return creneaux;
    }

    @Override
    public LocalDateTime getMinDebutCreneau() {
        LocalDateTime minDebut = null;
        String sql = "SELECT MIN(debut) AS min_debut FROM creneau";
        db.initPrepar(sql);
        try {
            ResultSet rs = db.executeSelect();
            if (rs.next()) {
                minDebut = rs.getTimestamp("min_debut").toLocalDateTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return minDebut;
    }

    @Override
    public int createUE(String code, String designation) {
        try {
            // Vérification de l'existence de l'UE
            String checkSql = "SELECT id FROM unite_enseignement WHERE code = ? AND designation = ?";
            db.initPrepar(checkSql);
            db.getPstm().setString(1, code);
            db.getPstm().setString(2, designation);
            ResultSet rs = db.executeSelect();

            if (rs.next()) {
                // L'UE existe déjà, on informe et on retourne -1 pour indiquer que l'UE existe
                System.out.println("L'unité d'enseignement avec le code " + code + " et la désignation " + designation + " existe déjà.");
                rs.close();
                return -1;
            }
            rs.close(); // Fermer le ResultSet après utilisation

            // Requête pour insérer la nouvelle UE si elle n'existe pas
            String sql = "INSERT INTO unite_enseignement (code, designation) VALUES (?, ?)";
            db.initPrepar(sql);
            db.getPstm().setString(1, code);
            db.getPstm().setString(2, designation);
            return db.executeMaj();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            db.closeConnection();
        }
    }

    @Override
    public int deleteUE(int idUE) {
        // Supprimer toutes les sessions associées à cette UE
        deleteSessionsByColumn("id_ue", idUE);

        String sql = "DELETE FROM unite_enseignement WHERE id = ?";
        db.initPrepar(sql);
        try {
            db.getPstm().setInt(1, idUE);
            return db.executeMaj();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            db.closeConnection();
        }
    }

    @Override
    public int createCreneau(LocalDateTime debut, LocalDateTime fin) {
        String sql = "INSERT INTO creneau (debut, fin) VALUES (?, ?)";
        db.initPrepar(sql);
        try {
            db.getPstm().setObject(1, debut);
            db.getPstm().setObject(2, fin);
            return db.executeMaj();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            db.closeConnection();
        }
    }

    @Override
    public int deleteCreneau(int idCreneau) {
        // Supprimer toutes les sessions associées à ce créneau
        deleteSessionsByColumn("id_creneau", idCreneau);

        String sql = "DELETE FROM creneau WHERE id = ?";
        db.initPrepar(sql);
        try {
            db.getPstm().setInt(1, idCreneau);
            return db.executeMaj();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            db.closeConnection();
        }
    }

    @Override
    public int createClasse(int promotion, Classe.Specialite specialite) {
        // Requête pour vérifier si la classe existe déjà
        String checkSql = "SELECT COUNT(*) FROM classe WHERE promotion = ? AND specialite = ?";
        db.initPrepar(checkSql);
        try {
            db.getPstm().setInt(1, promotion);
            db.getPstm().setString(2, specialite.name());
            ResultSet rs = db.getPstm().executeQuery();

            // Si une classe avec ces propriétés existe déjà, on retourne sans ajouter
            if (rs.next() && rs.getInt(1) > 0) {
                return -2; // Indicateur que l'insertion n'a pas été faite
            }
            // Requête d'insertion si la classe n'existe pas
            String sql = "INSERT INTO classe (promotion, specialite) VALUES (?, ?)";
            db.initPrepar(sql);
            db.getPstm().setInt(1, promotion);
            db.getPstm().setString(2, specialite.name());
            return db.executeMaj(); // Exécute l'insertion et retourne le résultat
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            db.closeConnection();
        }
    }

    @Override
    public int deleteClasse(int idClasse) {
        // Supprimer toutes les sessions associées à cette classe
        deleteSessionsByColumn("id_classe", idClasse);

        String sql = "DELETE FROM classe WHERE id = ?";
        db.initPrepar(sql);
        try {
            db.getPstm().setInt(1, idClasse);
            return db.executeMaj();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            db.closeConnection();
        }
    }

    @Override
    public int createSession(String identifiant, int idUE, int idClasse, int idCreneau) {
        String sql = "INSERT INTO session (identifiant, id_ue, id_classe, id_creneau) VALUES (?, ?, ?, ?)";
        db.initPrepar(sql);
        try {
            db.getPstm().setString(1, identifiant);
            db.getPstm().setInt(2, idUE);
            db.getPstm().setInt(3, idClasse);
            db.getPstm().setInt(4, idCreneau);
            return db.executeMaj();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            db.closeConnection();
        }
    }

    @Override
    public int deleteSession(int idSession) {
        String sql = "DELETE FROM session WHERE id = ?";
        db.initPrepar(sql);
        try {
            db.getPstm().setInt(1, idSession);
            return db.executeMaj();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            db.closeConnection();
        }
    }

    @Override
    public int updateSession(List<Session> sessionsAsupprimer, List<Session> sessionsAcreer) {
        int result = 0;

        // Suppression des sessions
        for (Session session : sessionsAsupprimer) {
            String sqlDelete = "DELETE FROM session WHERE id_ue = ? AND id_classe = ? AND id_creneau = ?";
            db.initPrepar(sqlDelete);
            try {
                db.getPstm().setInt(1, session.getUe().getIdUE());
                db.getPstm().setInt(2, session.getClasse().getIdClasse());
                db.getPstm().setInt(3, session.getCreneau().getIdCreneau());
                result += db.executeMaj();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.closeConnection();
            }
        }

        // Création des nouvelles sessions
        for (Session session : sessionsAcreer) {
            result += createSession(session.getIdentifiant(), session.getUe().getIdUE(), session.getClasse().getIdClasse(), session.getCreneau().getIdCreneau());
        }

        return result;
    }

    @Override
    public int createMultipleSessions(String identifiant, int idUE, int idClasse, List<Integer> idCreneau) {
        int result = 0;
        for (int id : idCreneau) {
            result += createSession(identifiant, idUE, idClasse, id);
        }
        return result;
    }

    @Override
    public ObservableList<Creneau> getCreneauxDisponibles(int idClasse) {
        ObservableList<Creneau> creneaux = FXCollections.observableArrayList();
        String sql = "SELECT * FROM creneau WHERE id NOT IN (SELECT id_creneau FROM session WHERE id_classe = ?)";
        db.initPrepar(sql);
        try {
            db.getPstm().setInt(1, idClasse);
            ResultSet rs = db.executeSelect();
            while (rs.next()) {
                creneaux.add(new Creneau(rs.getInt("id"), rs.getTimestamp("debut").toLocalDateTime(), rs.getTimestamp("fin").toLocalDateTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return creneaux;
    }

    @Override
    public ObservableList<UniteEnseignement> getUniteEnseignement() {
        ObservableList<UniteEnseignement> ues = FXCollections.observableArrayList();
        String sql = "SELECT * FROM unite_enseignement";
        db.initPrepar(sql);
        try {
            ResultSet rs = db.executeSelect();
            while (rs.next()) {
                ues.add(new UniteEnseignement(rs.getInt("id"), rs.getString("code"), rs.getString("designation")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return ues;
    }

    @Override
    public ObservableList<Classe> getClasse() {
        ObservableList<Classe> classes = FXCollections.observableArrayList();
        String sql = "SELECT * FROM classe ORDER BY id";
        db.initPrepar(sql);
        try {
            ResultSet rs = db.executeSelect();
            while (rs.next()) {
                classes.add(new Classe(rs.getInt("id"), rs.getInt("promotion"), Classe.Specialite.valueOf(rs.getString("specialite"))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return classes;
    }
    @Override
    public List<UniteEnseignement> getAllUEs() {
        String sql = "SELECT id, code, designation FROM unite_enseignement ORDER BY id";
        List<UniteEnseignement> ueList = new ArrayList<>();
        db.initPrepar(sql);

        try {
            ResultSet rs = db.executeSelect();
            while (rs.next()) {
                // Crée une instance de UniteEnseignement pour chaque ligne de résultat
                UniteEnseignement ue = new UniteEnseignement(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("designation")
                );
                ueList.add(ue); // Ajoute l'instance à la liste
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }

        return ueList;
    }


    @Override
    public ObservableList<Creneau> getCreneau() {
        ObservableList<Creneau> creneaux = FXCollections.observableArrayList();
        String sql = "SELECT * FROM creneau";
        db.initPrepar(sql);
        try {
            ResultSet rs = db.executeSelect();
            while (rs.next()) {
                creneaux.add(new Creneau(rs.getInt("id"), rs.getTimestamp("debut").toLocalDateTime(), rs.getTimestamp("fin").toLocalDateTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return creneaux;
    }

    @Override
    public ObservableList<Session> getSession() {
        ObservableList<Session> sessions = FXCollections.observableArrayList();
        String sql = "SELECT * FROM session";
        db.initPrepar(sql);
        try {
            ResultSet rs = db.executeSelect();
            while (rs.next()) {
                sessions.add(new Session(
                        rs.getInt("id"),
                        rs.getString("identifiant"),
                        rs.getInt("id_ue"),
                        null, null,
                        rs.getInt("id_classe"),
                        0, Classe.Specialite.MT,
                        rs.getInt("id_creneau"),
                        null, null
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return sessions;
    }
    @Override
    public void deleteSessionsByColumn(String columnName, int columnValue) {
        String sql = "DELETE FROM session WHERE " + columnName + " = ?";
        db.initPrepar(sql);
        try {
            db.getPstm().setInt(1, columnValue);
            db.executeMaj();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
    }
    @Override
    public int GetIdSession(int idUE, int idClasse, int idCreneau){
        String sql = "SELECT id FROM session WHERE id_ue = ? AND id_classe = ? AND id_creneau = ?";
        db.initPrepar(sql);
        try {

            db.getPstm().setInt(1, idUE);
            db.getPstm().setInt(2, idClasse);
            db.getPstm().setInt(3, idCreneau);
            ResultSet rs = db.executeSelect();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return 0;
    }
    @Override
    public int getLastSessionId() {
        String sql = "SELECT MAX(id) FROM session";
        db.initPrepar(sql);
        try {
            ResultSet rs = db.executeSelect();
            if (rs.next()) {
                return rs.getInt(1); // Récupère l'id max
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return 0;
    }
    @Override
    public LocalDateTime getMinDebutCreneauDisponiblePourClasse(int idClasse) {
        LocalDateTime minDebut = null;
        String sql = "SELECT MIN(debut) AS min_debut " +
                "FROM creneau " +
                "WHERE id NOT IN (" +
                "SELECT id_creneau FROM session WHERE id_classe = ?)";
        db.initPrepar(sql);
        try {
            db.getPstm().setInt(1, idClasse);
            ResultSet rs = db.executeSelect();
            if (rs.next()) {
                Timestamp minDebutTimestamp = rs.getTimestamp("min_debut");
                if (minDebutTimestamp != null) {
                    minDebut = minDebutTimestamp.toLocalDateTime();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return minDebut;
    }

    @Override
    public LocalDateTime getMinDebutCreneauUtiliseParUEDansClasse(int idUE, int idClasse) {
        LocalDateTime minDebut = null;
        String sql = "SELECT MIN(c.debut) AS min_debut " +
                "FROM creneau c " +
                "JOIN session s ON s.id_creneau = c.id " +
                "WHERE s.id_ue = ? AND s.id_classe = ?";
        db.initPrepar(sql);
        try {
            db.getPstm().setInt(1, idUE);
            db.getPstm().setInt(2, idClasse);
            ResultSet rs = db.executeSelect();
            if (rs.next()) {
                Timestamp minDebutTimestamp = rs.getTimestamp("min_debut");
                if (minDebutTimestamp != null) {
                    minDebut = minDebutTimestamp.toLocalDateTime();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return minDebut;
    }


    @Override
    public List<Creneau> getCreneauxUtilisesParUEDansClasse(int idUE, int idClasse, LocalDateTime dateDebut, LocalDateTime dateFin) {
        List<Creneau> creneauxUtilises = new ArrayList<>();
        String sql = "SELECT c.id, c.debut, c.fin " +
                "FROM creneau c " +
                "JOIN session s ON s.id_creneau = c.id " +
                "WHERE s.id_ue = ? AND s.id_classe = ? " +
                "AND c.debut >= ? AND c.fin <= ?";
        db.initPrepar(sql);
        try {
            db.getPstm().setInt(1, idUE);
            db.getPstm().setInt(2, idClasse);
            db.getPstm().setObject(3, dateDebut);
            db.getPstm().setObject(4, dateFin);
            ResultSet rs = db.executeSelect();
            while (rs.next()) {
                creneauxUtilises.add(new Creneau(
                        rs.getInt("id"),
                        rs.getTimestamp("debut").toLocalDateTime(),
                        rs.getTimestamp("fin").toLocalDateTime()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return creneauxUtilises;
    }


    @Override
    public List<Creneau> getCreneauxDisponiblesPourClasse(int idClasse, LocalDateTime dateDebut, LocalDateTime dateFin) {
        List<Creneau> creneauxDisponibles = new ArrayList<>();
        String sql = "SELECT * FROM creneau " +
                "WHERE id NOT IN (" +
                "SELECT id_creneau FROM session WHERE id_classe = ?" +
                ") AND debut >= ? AND fin <= ?";
        db.initPrepar(sql);
        try {
            db.getPstm().setInt(1, idClasse);
            db.getPstm().setObject(2, dateDebut);
            db.getPstm().setObject(3, dateFin);
            ResultSet rs = db.executeSelect();
            while (rs.next()) {
                creneauxDisponibles.add(new Creneau(
                        rs.getInt("id"),
                        rs.getTimestamp("debut").toLocalDateTime(),
                        rs.getTimestamp("fin").toLocalDateTime()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return creneauxDisponibles;
    }


    @Override
    public ObservableList<UniteEnseignement> getUniteEnseignementByClasse(int idClasse) {
        ObservableList<UniteEnseignement> ues = FXCollections.observableArrayList();
        String sql = "SELECT ue.id, ue.code, ue.designation " +
                "FROM unite_enseignement ue " +
                "JOIN session s ON s.id_ue = ue.id " +
                "WHERE s.id_classe = ?";
        db.initPrepar(sql);
        try {
            db.getPstm().setInt(1, idClasse);
            ResultSet rs = db.executeSelect();
            while (rs.next()) {
                ues.add(new UniteEnseignement(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("designation")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return ues;
    }

}


