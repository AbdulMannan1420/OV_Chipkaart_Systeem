package nl.hu.pd.lib;
import nl.hu.pd.lib.dao.ReizigerDAOPostgres;
import nl.hu.pd.lib.daoInterface.ReizigerDAO;
import nl.hu.pd.lib.model.Reiziger;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String [] args) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/ovchip?user=postgres&password=1234";
        Connection connection = DriverManager.getConnection(url);
        ReizigerDAO rdao = new ReizigerDAOPostgres(connection);
        testReizigerDAO(rdao);
        // code van opdracht p-1
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
    //Opdracht 2
    private static void testReizigerDAO(ReizigerDAO rdao) throws SQLException {
        try {
        System.out.println("\n---------- Test ReizigerDAO -------------");

        //1: Haal alle reizigers op uit de database
        List<Reiziger> reizigers = rdao.findAll();
        System.out.println("[Test] ReizigerDAO.findAll() geeft de volgende reizigers:");
        for (Reiziger r : reizigers) {
            System.out.println(r);
        }
        System.out.println();

        //2: Maak een nieuwe reiziger aan en persisteer deze in de database
        String gbdatum = "1981-03-14";
        Reiziger sietske = new Reiziger(77, "S", "", "Boers", Date.valueOf(gbdatum).toLocalDate());
        System.out.print("[Test] Eerst " + reizigers.size() + " reizigers, na ReizigerDAO.save() ");
        rdao.save(sietske);
        reizigers = rdao.findAll();
        System.out.println(reizigers.size() + " reizigers\n");
        // Voeg aanvullende tests van de ontbrekende CRUD-operaties in.
        // Test other CRUD operations such as update, delete, findById, and findByGbdatum.
        //3: Reizigers data updaten
        System.out.println("\n[Test] Info van reiziger updaten\nVoor:\n"+sietske.toString()+"\nNa: ");

        sietske.setTussenvoegsel("van");
        sietske.setAchternaam("Dijk");
        rdao.update(sietske);
        //4: get user by id
        Reiziger r = rdao.findById(77);
        System.out.println("[Test] :"+r.toString());
        System.out.println("\nEr zijn "+reizigers.size() + " reizigers\n");
        //5: Delete een user
        if (rdao.delete(r)){
            System.out.println("[Test] Een reiziger is Verwijderd");
            reizigers = rdao.findAll();
            System.out.println("\nNu zijn er weer "+reizigers.size() + " reizigers\n");
        }
        // 6: Reizigers van een bepaalde geboortedatum
        LocalDate localDate = Date.valueOf("2002-12-03").toLocalDate();
        List<Reiziger> foundBYDOB = rdao.findByGbdatum(localDate);
        System.out.println("[Test] reiziger met geboortedatum 2002-12-03\n");
        int aantal = 0;
        for (Reiziger rei: foundBYDOB) {
            aantal = aantal + 1;
            System.out.println("#"+aantal+": "+rei.toString());
        }
    }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
