package com.example.demo;

public class UniteEnseignement {
    private int idUE;
    private String code;
    private String designation;

    public UniteEnseignement(int idUE, String code, String designation) {
        this.idUE = idUE;
        this.code = code;
        this.designation = designation;
    }

    public int getIdUE() {
        return idUE;
    }

    public void setIdUE(int idUE) {
        this.idUE = idUE;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void displayUE() {
        System.out.println("IdUE: " + idUE + "Code: " + code + "Designation: " + designation);
    }
}
