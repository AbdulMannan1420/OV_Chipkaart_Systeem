package nl.hu.pd.lib.dao;

import nl.hu.pd.lib.daoInterface.OVChipkaartDAO;
import nl.hu.pd.lib.daoInterface.ProductDAO;
import nl.hu.pd.lib.daoInterface.ReizigerDAO;
import nl.hu.pd.lib.model.OVChipkaart;
import nl.hu.pd.lib.model.Product;
import nl.hu.pd.lib.model.Reiziger;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOPostgres implements OVChipkaartDAO {
    private Connection conn;
    private ReizigerDAO reizigerDAO;
    private ProductDAO productDAO;

    public OVChipkaartDAOPostgres(Connection connection) {
        this.conn = connection;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public void setReizigerDAO(ReizigerDAO reizigerDAO) {
        this.reizigerDAO = reizigerDAO;
    }

    public boolean save(OVChipkaart ovChipkaart) {
        String query = """
                INSERT INTO ov_chipkaart(kaart_nummer, geldig_tot,klasse,saldo,reiziger_id)
                VALUES (?,?,?,?,?);
                """;
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1,ovChipkaart.getKaartNummer());
            ps.setDate(2, java.sql.Date.valueOf(ovChipkaart.getGeldigTot()));
            ps.setInt(3,ovChipkaart.getKlasse());
            ps.setDouble(4,ovChipkaart.getSaldo());
            ps.setInt(5,ovChipkaart.getReiziger().getId());
            ps.executeUpdate();
            // Koppeltabel opslaan
            for (Product product: ovChipkaart.getProducts()) {
                query = """
                INSERT INTO 
                ov_chipkaart_product(kaart_nummer, product_nummer, status, last_update)
                VALUES (?, ?, ?, ?);
                """;
                ps = conn.prepareStatement(query);
                ps.setInt(1, ovChipkaart.getKaartNummer());
                ps.setInt(2, product.getProductNummer());
                ps.setString(3, "Actief");
                java.sql.Date currentDate = Date.valueOf(LocalDate.now());
                ps.setDate(4, currentDate);
                ps.executeUpdate();
            }
            ps.close();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }


    public boolean update(OVChipkaart ovChipkaart) {
        String query = """
                UPDATE ov_chipkaart
                SET geldig_tot = ?, 
                klasse = ?, saldo = ?
                WHERE kaart_nummer = ?;
                """;
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDate(1,java.sql.Date.valueOf(ovChipkaart.getGeldigTot()));
            ps.setInt(2,ovChipkaart.getKlasse());
            ps.setDouble(3, ovChipkaart.getSaldo());
            ps.setInt(4,ovChipkaart.getKaartNummer());
            ps.executeUpdate();
            for (Product p : ovChipkaart.getProducts()) {
                String query1 = """
                UPDATE ov_chipkaart_product
                SET status = ? , last_update = ?
                WHERE kaart_nummer = ? AND product_nummer = ?;
                """;
                PreparedStatement ps1 = conn.prepareStatement(query1);
                ps1.setString(1,"Actief");
                ps1.setDate(2, java.sql.Date.valueOf(ovChipkaart.getGeldigTot()));
                ps1.setInt(3, ovChipkaart.getKaartNummer());
                ps1.setInt(4, p.getProductNummer());
                ps1.executeUpdate();
                ps1.close();
            }
            ps.close();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }


    public boolean delete(OVChipkaart ovChipkaart) {
        try {
            for (Product product: ovChipkaart.getProducts()) {
                String query2 = """
                DELETE FROM ov_chipkaart_product
                WHERE kaart_nummer = ? 
                AND product_nummer = ?;
                """;
                PreparedStatement ps2 = conn.prepareStatement(query2);
                ps2.setInt(1, ovChipkaart.getKaartNummer());
                ps2.setInt(2, product.getProductNummer());
                ps2.executeUpdate();
                ps2.close();
            }
            String query= """
                DELETE FROM ov_chipkaart
                WHERE reiziger_id = ?
                """;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, ovChipkaart.getReiziger().getId());
            ps.executeUpdate();
            ps.close();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }


    public List<OVChipkaart> findByReiziger(Reiziger reiziger) {
        List<OVChipkaart> chipkaarten = new ArrayList<>();
        String query = """
                SELECT kaart_nummer, geldig_tot, klasse, saldo, reiziger_id
                FROM ov_chipkaart
                WHERE reiziger_id = ?;
                """;
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1,reiziger.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                int kaartNummer = rs.getInt("kaart_nummer");
                java.sql.Date date = rs.getDate("geldig_tot");
                LocalDate gelid_tot = date.toLocalDate();
                int klasse = rs.getInt("klasse");
                Double saldo = rs.getDouble("saldo");
                int reizigerID = rs.getInt("reiziger_id");
                OVChipkaart chipkaart = new OVChipkaart(kaartNummer,gelid_tot,klasse,saldo, reiziger);
                List<Product> products = productDAO.findByOVChipkaart(chipkaart);
                chipkaart.setProducts(products);
                chipkaarten.add(chipkaart);
            }
            rs.close();
            ps.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return chipkaarten;
    }

    public List<OVChipkaart> findAll(){
        List<OVChipkaart> chipkaarten = new ArrayList<>();
        String query = """
                SELECT kaart_nummer, geldig_tot, klasse, saldo, reiziger_id
                FROM ov_chipkaart
                """;
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                int kaartNummer = rs.getInt("kaart_nummer");
                java.sql.Date date = rs.getDate("geldig_tot");
                LocalDate gelid_tot = date.toLocalDate();
                int klasse = rs.getInt("klasse");
                double saldo = rs.getDouble("saldo");
                int reizigerID = rs.getInt("reiziger_id");
                Reiziger reiziger = reizigerDAO.findById(reizigerID);
                OVChipkaart chipkaart = new OVChipkaart(kaartNummer,gelid_tot,klasse,saldo, reiziger);
                List<Product> products = productDAO.findByOVChipkaart(chipkaart);
                chipkaart.setProducts(products);
                chipkaarten.add(chipkaart);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return chipkaarten;
    }
}
