package isty.iatic5.session_phase1.Services;

import java.sql.*;

public class DBConnexion {
    //Pour la connexion
    private Connection cnx;
    //Pour les requetes préparées
    private PreparedStatement pstm;
    //Pour les selections : SELECT
    private ResultSet rs;
    //Pour les mise à jour : Insert into, update, delete
    private int ok;

    public Connection getConnection(){
        String host = "localhost";
        String port = "3306";
        String db = "sdial";
        String url = "jdbc:mysql://" + host + ":" + port + "/" + db;
        String user = "groupe3";
        String password = "mdp-groupe3";

        try{
            //Chargement du pilote
            //Class.forName("com.mysql.jdbc.Driver");
            //On essaie d'ouvrir la connexion
            cnx = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database successfully");

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
                System.out.println("Connection closed successfully");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public PreparedStatement getPstm(){
        return pstm;
    }

}
