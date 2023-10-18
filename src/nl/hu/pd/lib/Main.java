package nl.hu.pd.lib;
import nl.hu.pd.lib.dao.AdresDAOPostgres;
import nl.hu.pd.lib.dao.OVChipkaartDAOPostgres;
import nl.hu.pd.lib.dao.ProductDAOPostgres;
import nl.hu.pd.lib.dao.ReizigerDAOPostgres;
import nl.hu.pd.lib.daoInterface.AdresDAO;
import nl.hu.pd.lib.daoInterface.OVChipkaartDAO;
import nl.hu.pd.lib.daoInterface.ProductDAO;
import nl.hu.pd.lib.daoInterface.ReizigerDAO;
import nl.hu.pd.lib.model.Adres;
import nl.hu.pd.lib.model.OVChipkaart;
import nl.hu.pd.lib.model.Product;
import nl.hu.pd.lib.model.Reiziger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String [] args) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/ovchip?user=postgres&password=1234";
        Connection connection = DriverManager.getConnection(url);
        ReizigerDAOPostgres rdao = new ReizigerDAOPostgres(connection);
        AdresDAO adao = new AdresDAOPostgres(connection);
        OVChipkaartDAO odao = new OVChipkaartDAOPostgres(connection);
        ProductDAO pdao = new ProductDAOPostgres(connection);
        pdao.setOVDAO(odao);
        odao.setProductDAO(pdao);
        odao.setReizigerDAO(rdao);
        rdao.setAdresDAO(adao);
        rdao.setChipDAO(odao);
        adao.setReizigerDAO(rdao); // rdao meegeven
        pdao.setOVDAO(odao);
        System.out.println("[TEST 1] : GET ALLE REIZIGERS met adres, ov-chipkaart(en) en product(en) (rdao.findAll())");
        for (Reiziger r:rdao.findAll()) {
            System.out.println(r);
        }
        System.out.println("[TEST 2] : Reiziger opslaan met adres ov en producten erop (rdao.save())");
        Reiziger reiziger = new Reiziger(6,"AM",null,"Alam",LocalDate.of(2000, 4, 1));
        Adres adres = new Adres(6,"9999AB","5B","Katerstraat","Tiel", reiziger);
        reiziger.setAdres(adres);
        OVChipkaart ovChipkaart = new OVChipkaart(12345, LocalDate.of(2025,8,10),
                1,23.50,reiziger);
        OVChipkaart ovChipkaart1 = new OVChipkaart(62345, LocalDate.of(2025,4,1),
                2,43.57,reiziger);
        OVChipkaart ov = new OVChipkaart(35283,LocalDate.of(2018,5,31),2,25.50);
        Product product = new Product(10,"Altijd Vrij","Reis altijd, overal", 420);
        pdao.save(product);
        ovChipkaart.addProduct(product);
        List<OVChipkaart> kaarten = new ArrayList<>();
        kaarten.add(ovChipkaart);
        kaarten.add(ovChipkaart1);
        reiziger.setOvChipkaarten(kaarten);
        rdao.save(reiziger);
        System.out.println("Reiziger uit db ophalen die net opgeslagen is");
        System.out.println(rdao.findById(6));
        System.out.println("Aantal reizigers in db: "+rdao.findAll().size());
        System.out.println("[Test 3]: Test product updaten");
        product.setNaam("Vrij Reizen");
        product.setPrijs(449.99);
        pdao.update(product);
        System.out.println("geupdatet product uit db");
        System.out.println(pdao.findByOVChipkaart(ovChipkaart1));
        System.out.println(rdao.findById(6));
        System.out.println("[Test 4]: Test reizigers verwijderen en zijn kaarten en producten");
        rdao.delete(reiziger);
        pdao.delete(product);
        System.out.println("Aantal reizigers in db na verwijderen van reiziger: "+rdao.findAll().size());
        System.out.println("[Test 5]: Alle ov-chipkaarten ophalen van db met producten");
        for (OVChipkaart ov1: odao.findAll()) {
            System.out.println(ov1);
        }
        System.out.println("[Test 6]: Alle producten ophalen van db met ov_chips");
        for(Product p: pdao.findAll()){
            System.out.println(p);
        }
        connection.close();
    }
}
