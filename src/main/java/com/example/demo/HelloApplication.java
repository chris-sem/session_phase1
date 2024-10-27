package com.example.demo;

import java.util.ArrayList;
import java.util.List;

public class HelloApplication {
    public static void main(String[] args) {
        // Assuming ImplementationISessions is the class that contains createMultipleSessions
        ImplementationISessions instance = new ImplementationISessions();

        // Define the parameters for testing
        String identifiant = "TestSession";
        int idUE = 1;       // Use a valid idUE for your database
        int idClasse = 2;   // Use a valid idClasse for your database

        // List of idCreneau values for multiple time slots
        List<Integer> idCreneau = new ArrayList<>();
        idCreneau.add(1);  // Replace with actual idCreneau values that exist in your database
        idCreneau.add(2);
        idCreneau.add(3);

        // Call createMultipleSessions and store the result
        int sessionsCreated = instance.createMultipleSessions(identifiant, idUE, idClasse, idCreneau);

        // Print the number of successfully created sessions
        System.out.println("Number of sessions successfully created: " + sessionsCreated);
    }
}
