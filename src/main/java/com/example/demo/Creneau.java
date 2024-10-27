package com.example.demo;

import java.time.LocalDateTime;

public class Creneau {
    private int idCreneau;
    private LocalDateTime debut;
    private LocalDateTime fin;

    public Creneau(int idCreneau, LocalDateTime debut, LocalDateTime fin) {
        this.idCreneau = idCreneau;
        this.debut = debut;
        this.fin = fin;
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

    public void displayCreneau() {
        System.out.println("Creneau id: " + idCreneau + " debut: " + debut + " fin: " + fin);
    }
}
