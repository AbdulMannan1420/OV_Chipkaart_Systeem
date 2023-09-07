package nl.hu.pd.lib;
import java.sql.*;
public class Main {

    public static void main(String [] args){
        String url = "jdbc:postgresql://localhost:5432/ovchip?user=postgres&password=1234";
        try{
            Connection conn = DriverManager.getConnection(url);
            String query = "SELECT * FROM reiziger";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            int count = 0;
            System.out.println("Alle reizigers:");
            while (rs.next()) {
                count += 1;
                String achternaam = rs.getString("achternaam");
                String voorletters = rs.getString("voorletters");
                String tussenvoegsel = rs.getString("tussenvoegsel");
                String geboortedatum = rs.getString("geboortedatum");
                if (tussenvoegsel == null) {
                    String data = "     #" + count + ": " + voorletters + ". " + achternaam + " (" + geboortedatum + ")";
                    System.out.println(data);
                }
                else {
                    String data = "     #" + count + ": " + voorletters + ". " + tussenvoegsel + " " + achternaam + " (" + geboortedatum + ")";
                    System.out.println(data);
                }

            }
            rs.close();
            ps.close();
            conn.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}
