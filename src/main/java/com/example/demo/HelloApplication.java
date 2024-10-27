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
        Classe c = new Classe(1,5, Classe.Specialite.MT);
        c.displayClasse();



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