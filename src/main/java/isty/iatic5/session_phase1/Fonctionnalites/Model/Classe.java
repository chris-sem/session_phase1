package isty.iatic5.session_phase1.Fonctionnalites.Model;

public class Classe {
    private int idClasse;
    private int promotion;
    private Specialite specialite;

    // Enum pour les spécialités
    public enum Specialite {
        MT, IATIC, TEST
    }

    public Classe(int idClasse, int promotion, Specialite specialite) {
        this.idClasse = idClasse;
        this.promotion = promotion;
        this.specialite = specialite;
    }

    public int getIdClasse() {
        return idClasse;
    }

    public void setIdClasse(int idClasse) {
        this.idClasse = idClasse;
    }

    public int getPromotion() {
        return promotion;
    }

    public void setPromotion(int promotion) {
        this.promotion = promotion;
    }

    public Specialite getSpecialite() {
        return specialite;
    }

    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }
    public void displayClasse(){
        System.out.println("Classe Id: " + idClasse + " Promotion: " + promotion + " Specialite: " + specialite);
    }
}
