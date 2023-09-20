package nl.hu.pd.lib;
import nl.hu.pd.lib.dao.AdresDAOPostgres;
import nl.hu.pd.lib.dao.ReizigerDAOPostgres;
import nl.hu.pd.lib.daoInterface.AdresDAO;
import nl.hu.pd.lib.daoInterface.ReizigerDAO;
import nl.hu.pd.lib.model.Adres;
import nl.hu.pd.lib.model.Reiziger;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String [] args) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/ovchip?user=postgres&password=1234";
        Connection connection = DriverManager.getConnection(url);
        ReizigerDAOPostgres rdao = new ReizigerDAOPostgres(connection);
        AdresDAO adao = new AdresDAOPostgres(connection);
        rdao.setAdresDAO(adao);
        adao.setReizigerDAO(rdao); // rdao meegeven
        //test: alle adressen
        System.out.println("[TEST 1] : TEST ALLE ADRESSEN(adao.findAll())");
        int count = 0;
        for (Adres a : adao.findAll()) {
            count = count + 1;
            System.out.println(count+"#\n"+a.getReiziger());
        }
        //test: Voeg reiziger toe met zijn adres
        System.out.println("[TEST 2] : TEST REIZIGER OPSLAAN MET ZIJN ADRES(rdao.save())");
        Reiziger reiziger = new Reiziger(6,"AM",null,"Alam",LocalDate.of(2000, 4, 1));
        Adres adres = new Adres(6,"9999AB","5B","Katerstraat","Tiel", reiziger);
        reiziger.setAdres(adres);
        rdao.save(reiziger);
        System.out.println(rdao.findById(6));
        System.out.println("Aantal reizigers in database: "+rdao.findAll().size()+"\n");
        //test update
        System.out.println("[TEST 3] : TEST REIZIGER UPDATEN MET ZIJN ADRES(rdao.update())");
        adres.setPostcode("1234ZZ");
        adres.setWoonplaats("UTRECHT");
        adres.setHuisnummer("15");
        rdao.update(reiziger);
        System.out.println(rdao.findById(6));
        // test: deze test wordt automatisch aangeroepen in rdao
        // hier wordt het apart aangeroepen voor test
        System.out.println("[TEST 4] : TEST ADRES_BY_REIZIGER(adao.findByReiziger())");
        System.out.println(adao.findByReiziger(reiziger).getReiziger());
        //test: Reiziger verwijderen met zijn adres
        System.out.println("[TEST 5] : TEST REIZIGER VERWIJDEREN MET ADRES(rdao.delete())");
        rdao.delete(reiziger);
        System.out.println("Lijst na verwijderen van reiziger\n");
        System.out.println("Aantal reizigers in database: "+rdao.findAll().size());
        connection.close();
    }
}
