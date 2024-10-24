package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        //Classe c = new Classe(1,5, Classe.Specialite.MT);
        //c.displayClasse();
        //UniteEnseignement obj = new UniteEnseignement();

        ImplementationISessions imp = new ImplementationISessions();
        /*
        // 1. Tester la création d'une UE
        String codeUE = "INF102";
        String designationUE = "Informatique de base";
        int idUE = imp.createUE(codeUE, designationUE);
        if (idUE > 0) {
            System.out.println("UE créée avec succès, ID: " + idUE);
        } else {
            System.out.println("Échec de la création de l'UE");
        }

        // 2. Tester la suppression de l'UE
        int idtest = 39;
        int resultDeleteUE = imp.deleteUE(idtest);
        if (resultDeleteUE > 0) {
            System.out.println("UE supprimée avec succès, ID: " + resultDeleteUE);
        } else {
            System.out.println("Échec de la suppression de l'UE");
        }

        // 3. Tester la création d'un créneau
        LocalDateTime debut = LocalDateTime.of(2024, 10, 20, 11, 0); // 24 Oct 2024 à 10h00
        LocalDateTime fin = LocalDateTime.of(2024, 10, 20, 13, 0);   // 24 Oct 2024 à 12h00
        int idCreneau = imp.createCreneau(debut, fin);
        if (idCreneau > 0) {
            System.out.println("Créneau créé avec succès, ID: " + idCreneau);
        } else {
            System.out.println("Échec de la création du créneau");
        }
/*
        // 4. Tester la suppression du créneau
        int resultDeleteCreneau = imp.deleteCreneau(301);
        if (resultDeleteCreneau > 0) {
            System.out.println("Créneau supprimé avec succès, ID: " + resultDeleteCreneau);
        } else {
            System.out.println("Échec de la suppression du créneau");
        }
    }
*/


        /*
        DBConnexion db = new DBConnexion();
        String sqlRequest = "SELECT * FROM sujet WHERE id = ?";
        db.initPrepar(sqlRequest);
        try {
            db.getPstm().setInt(1, 2); //Met la valeur 2 dans le premier ?
            ResultSet rs = db.executeSelect();
            while(rs.next()){
                System.out.println("ID : " + rs.getInt("id") + " Intitulé : " + rs.getString("Intitule"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.closeConnection();*/
    }

}