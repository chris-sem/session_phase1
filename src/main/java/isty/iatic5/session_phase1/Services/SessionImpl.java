package isty.iatic5.session_phase1.Services;

import isty.iatic5.session_phase1.Model.Classe;
import isty.iatic5.session_phase1.Model.Creneau;
import isty.iatic5.session_phase1.Model.Session;
import isty.iatic5.session_phase1.Model.UniteEnseignement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.List;
import java.sql.*;

public class SessionImpl implements ISession {
    private DBConnexion db = new DBConnexion();

    @Override
    public int createUE(String code, String designation) {
        String sql = "INSERT INTO unite_enseignement (code, designation) VALUES (?, ?)";
        db.initPrepar(sql);
        try {
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
        String sql = "INSERT INTO classe (promotion, specialite) VALUES (?, ?)";
        db.initPrepar(sql);
        try {
            db.getPstm().setInt(1, promotion);
            db.getPstm().setString(2, specialite.name());
            return db.executeMaj();
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
    public int updateSession(ObservableList<Integer> IdsSessionsAsupprimer, ObservableList<Session> sessionsAcreer) {
        int result = 0;
        for (int idSession : IdsSessionsAsupprimer) {
            result += deleteSession(idSession);
        }
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
        String sql = "SELECT * FROM classe";
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

    private void deleteSessionsByColumn(String columnName, int columnValue) {
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
}


