package isty.iatic5.session_phase1.Model;

import java.time.LocalDateTime;

public class Session {
    private int idSession;
    private String identifiant;
    private UniteEnseignement ue;
    private Classe classe;
    private Creneau creneau;

    public Session(int idSession, String identifiant, int idUE, String code, String designation, int idClasse, int promotion, Classe.Specialite specialite, int idCreneau, LocalDateTime debut, LocalDateTime fin) {
        this.idSession = idSession;
        this.identifiant = identifiant;
        this.ue = new UniteEnseignement(idUE, code, designation);
        this.classe = new Classe(idClasse, promotion, specialite);
        this.creneau = new Creneau(idCreneau, debut, fin);
    }

    public int getIdSession() {
        return idSession;
    }

    public void setIdSession(int idSession) {
        this.idSession = idSession;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public UniteEnseignement getUe() {
        return ue;
    }

    public void setUe(UniteEnseignement ue) {
        this.ue = ue;
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public Creneau getCreneau() {
        return creneau;
    }

    public void setCreneau(Creneau creneau) {
        this.creneau = creneau;
    }
    public void displaySession(){
        System.out.println("Session ID: " + this.idSession + "Identifiant: " + this.identifiant);
        ue.displayUE();
        classe.displayClasse();
        creneau.displayCreneau();
        System.out.println("....................................");
    }
}
