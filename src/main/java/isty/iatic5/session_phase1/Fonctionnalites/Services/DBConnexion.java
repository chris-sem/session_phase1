package isty.iatic5.session_phase1.Fonctionnalites.Services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DBConnexion {
    //Pour la connexion
    private Connection cnx;
    //Pour les requetes préparées
    private PreparedStatement pstm;
    //Pour les selections : SELECT
    private ResultSet rs;
    //Pour les mise à jour : Insert into, update, delete
    private int ok;

    private static class DBConfig {
        public String host;
        public String port;
        public String db;
        public String user;
        public String password;
    }

    public Connection getConnection(){

        try {

            String configFilePath = "config_BD.txt";

            Map<String, String> config = readConfigFromTxt(configFilePath);

            // Construire l'URL de connexion
            String url = "jdbc:mysql://" + config.get("host") + ":" + config.get("port") + "/" + config.get("db");

            // Charger la connexion
            cnx = DriverManager.getConnection(url, config.get("user"), config.get("password"));
            //System.out.println("Connected to the database successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cnx;
    }

    public void initPrepar(String sql){
        try {
            getConnection();
            pstm = cnx.prepareStatement(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeSelect(){
        rs = null;
        try{
            rs = pstm.executeQuery();
        } catch (Exception e){
            e.printStackTrace();
        }

        return rs;
    }

    public int executeMaj(){
        ok = 0;
        try {
            ok = pstm.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
        }

        return ok;
    }

    public void closeConnection(){
        try {
            if(cnx != null){
                cnx.close();
                //System.out.println("Connection closed successfully");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public PreparedStatement getPstm(){
        return pstm;
    }


    private Map<String, String> readConfigFromTxt(String filePath) throws Exception {
        Map<String, String> config = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignorer les lignes vides ou incorrectes
                if (line.contains(":")) {
                    String[] keyValue = line.split(":", 2);
                    config.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
        }

        return config;
    }

}
