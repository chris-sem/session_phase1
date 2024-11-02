package isty.iatic5.session_phase1.Fonctionnalites.Model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class Creneau {
    private int idCreneau;
    private LocalDateTime debut;
    private LocalDateTime fin;
    private int ligne;
    private int colonne;
    private String Statut;


    public Creneau(int idCreneau, LocalDateTime debut, LocalDateTime fin) {
        this.idCreneau = idCreneau;
        this.debut = debut;
        this.fin = fin;
        this.Statut = "Disponible";

        // Déterminer la colonne en fonction du jour de la semaine
        DayOfWeek dayOfWeek = debut.getDayOfWeek();
        this.colonne = switch (dayOfWeek) {
            case MONDAY -> 1;
            case TUESDAY -> 2;
            case WEDNESDAY -> 3;
            case THURSDAY -> 4;
            case FRIDAY -> 5;
            case SATURDAY -> 6;
            case SUNDAY -> 7;
        };

        // Déterminer la ligne en fonction de l'heure de début
        int hour = debut.getHour();
        this.ligne = hour - 7 + 1; // Ligne 1 pour 7:00, Ligne 2 pour 8:00, etc.
    }

    public int getIdCreneau() {
        return idCreneau;
    }

    public void setIdCreneau(int idCreneau) {
        this.idCreneau = idCreneau;
    }

    public LocalDateTime getDebut() {
        return debut;
    }

    public void setDebut(LocalDateTime debut) {
        this.debut = debut;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public void setFin(LocalDateTime fin) {
        this.fin = fin;
    }

    public int getLigne() {
        return ligne;
    }

    public void setLigne(int ligne) {
        this.ligne = ligne;
    }

    public int getColonne() {
        return colonne;
    }

    public void setColonne(int colonne) {
        this.colonne = colonne;
    }

    @Override
    public String toString() {
        return ("Creneau id: " + idCreneau + " debut: " + debut + " fin: " + fin + " ligne: " + ligne + " colonne: " + colonne);
    }
    public String getStatut() {
        return Statut;
    }

    public void setStatut(String statut) {
        this.Statut = statut;
    }

    // Pour comparer deux objets RepresentationTableau
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Creneau other = (Creneau) obj;
        return ligne == other.ligne && colonne == other.colonne;
    }

    @Override
    public int hashCode() {
        return ligne * 31 + colonne;
    }

    public void displayCreneau() {
        System.out.println("Creneau id: " + idCreneau + " debut: " + debut + " fin: " + fin);
    }
}