package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;


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



        //------------------- TESTS POUR DELETE SESSION -------------------

        /*
        // Création d'une instance de ImplementationISessions
        ImplementationISessions sessionImpl = new ImplementationISessions();

        // Appel de la méthode deleteSession pour supprimer une session
        int result = sessionImpl.deleteSession(2);  // ID de la session à supprimer

        // Vérification simple
        if (result > 0) {
            System.out.println("La session a été supprimée avec succès.");
        } else {
            System.out.println("Aucune session trouvée pour cet ID.");
        }
        */

        //------------------- TESTS POUR CREATE SESSION -------------------

        /*
        // Création d'une instance de ImplementationISessions
        ImplementationISessions sessionImpl = new ImplementationISessions();

        // Appel de la méthode createSession pour créer une nouvelle session
        int idSessionCree = sessionImpl.createSession("TP8SOA", 1, 1, 1);  // Exemple d'identifiants

        // Vérification simple
        if (idSessionCree > 0) {
            System.out.println("La session a été créée avec succès. ID: " + idSessionCree);
        } else {
            System.out.println("Erreur lors de la création de la session.");
        }

         */


        // ------------------- TESTS POUR DELETE CLASSE -------------------
        /*


        // Création d'une instance de ImplementationISessions
        ImplementationISessions sessionImpl = new ImplementationISessions();

        // Appel de la méthode deleteClasse pour supprimer une classe
        int result = sessionImpl.deleteClasse(23);  // ID de la classe à supprimer

        // Vérification simple
        if (result > 0) {
            System.out.println("La méthode deleteClasse fonctionne correctement.");
        } else {
            System.out.println("Erreur lors de la suppression de la classe.");
        }


        */





        // ------------------- TESTS POUR CREATE CLASSE -------------------
        /*

        // Création d'une instance de ImplementationISessions
        ImplementationISessions sessionImpl = new ImplementationISessions();

        // Appel de la méthode createClasse pour créer et sauvegarder une nouvelle classe
        int idClasseCree = sessionImpl.createClasse(2025, Classe.Specialite.TEST);

        // Affichage de l'ID généré
        System.out.println("Classe créée avec l'ID : " + idClasseCree);

        // Vérification simple
        if (idClasseCree > 0) {
            System.out.println("La méthode createClasse fonctionne correctement.");
        } else {
            System.out.println("Erreur lors de la création de la classe.");
        }

        //Classe c = new Classe(1,5, Classe.Specialite.MT);
        //c.displayClasse();
        //UniteEnseignement obj = new UniteEnseignement();

        */





        /*ImplementationISessions imp = new ImplementationISessions();
        // 1. Tester la création d'une UE
        String codeUE = "INF101";
        String designationUE = "Informatique de base";
        int idUE = imp.createUE(codeUE, designationUE);
        if (idUE > 0) {
            System.out.println("UE créée avec succès, ID: " + idUE);
        } else {
            System.out.println("Échec de la création de l'UE");
        }*/
/*
        // 2. Tester la suppression de l'UE
        int resultDeleteUE = obj.deleteUE(idUE);
        if (resultDeleteUE > 0) {
            System.out.println("UE supprimée avec succès, ID: " + resultDeleteUE);
        } else {
            System.out.println("Échec de la suppression de l'UE");
        }

        // 3. Tester la création d'un créneau
        LocalDateTime debut = LocalDateTime.of(2024, 10, 24, 10, 0); // 24 Oct 2024 à 10h00
        LocalDateTime fin = LocalDateTime.of(2024, 10, 24, 12, 0);   // 24 Oct 2024 à 12h00
        int idCreneau = obj.createCreneau(debut, fin);
        if (idCreneau > 0) {
            System.out.println("Créneau créé avec succès, ID: " + idCreneau);
        } else {
            System.out.println("Échec de la création du créneau");
        }

        // 4. Tester la suppression du créneau
        int resultDeleteCreneau = obj.deleteCreneau(idCreneau);
        if (resultDeleteCreneau > 0) {
            System.out.println("Créneau supprimé avec succès, ID: " + resultDeleteCreneau);
        } else {
            System.out.println("Échec de la suppression du créneau");
        }
    }*/



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