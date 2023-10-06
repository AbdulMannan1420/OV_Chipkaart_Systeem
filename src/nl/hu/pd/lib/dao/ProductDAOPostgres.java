package nl.hu.pd.lib.dao;

import nl.hu.pd.lib.daoInterface.OVChipkaartDAO;
import nl.hu.pd.lib.daoInterface.ProductDAO;
import nl.hu.pd.lib.model.OVChipkaart;
import nl.hu.pd.lib.model.Product;
import nl.hu.pd.lib.model.Reiziger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOPostgres implements ProductDAO {
    private Connection conn;
    private OVChipkaartDAO ovDAO;
    public ProductDAOPostgres(Connection conn){
        this.conn = conn;
    }

    public void setOVDAO(OVChipkaartDAO ovDAO) {
        this.ovDAO = ovDAO;
    }

    public boolean save(Product product) {
        String query = """
                INSERT INTO 
                product(product_nummer, naam, beschrijving, prijs)
                VALUES (?,?,?,?);
                """;
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, product.getProductNummer());
            ps.setString(2, product.getNaam());
            ps.setString(3, product.getBeschrijving());
            ps.setDouble(4, product.getPrijs());
            ps.executeUpdate();
            ps.close();
            if(product.getOvChipkaarten() != null){
            if(!product.getOvChipkaarten().isEmpty()){
                for (OVChipkaart ov: product.getOvChipkaarten()) {
                    ovDAO.save(ov);
                }
            }}
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }


    public boolean update(Product product) {
        String query = """
                UPDATE product 
                SET naam = ?,
                    beschrijving = ?,
                    prijs = ?
                WHERE product_nummer = ?
                """;
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, product.getNaam());
            ps.setString(2, product.getBeschrijving());
            ps.setDouble(3, product.getPrijs());
            ps.setInt(4, product.getProductNummer());
            ps.executeUpdate();
            ps.close();
            if (product.getOvChipkaarten() != null) {
                if (!product.getOvChipkaarten().isEmpty()) {
                    for (OVChipkaart ov : product.getOvChipkaarten()) {
                        ovDAO.update(ov);
                    }
                }
            }
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }


    public boolean delete(Product product) {
        String query= """
                DELETE FROM product
                WHERE product_nummer = ?
                """;
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, product.getProductNummer());
            ps.executeUpdate();
            ps.close();
            if(product.getOvChipkaarten() != null){
            if(!product.getOvChipkaarten().isEmpty()){
                for (OVChipkaart ov: product.getOvChipkaarten()) {
                    ovDAO.delete(ov);
                }
            }}
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public List<Product> findByOVChipkaart(OVChipkaart ov){
        List<Product> products = new ArrayList<>();
        String query = """
                SELECT p.naam, p.beschrijving, p.prijs, p.product_nummer
                FROM ov_chipkaart ov
                JOIN ov_chipkaart_product ovp
                ON ov.kaart_nummer = ovp.kaart_nummer
                JOIN product p
                ON p.product_nummer = ovp.product_nummer
                WHERE ov.kaart_nummer = ?
                """;
        try {
            PreparedStatement ps = this.conn.prepareStatement(query);
            ps.setInt(1, ov.getKaartNummer());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                String naam = rs.getString("naam");
                String beschrijving = rs.getString("beschrijving");
                int nummer = rs.getInt("product_nummer");
                double prijs = rs.getDouble("prijs");
                List<OVChipkaart> kaarten = new ArrayList<>();
                kaarten.add(ov);
                Product p = new Product(nummer,naam,beschrijving,prijs, kaarten);
                products.add(p);
            }
            rs.close();
            ps.close();

        }catch (SQLException e){
            e.printStackTrace();
        }

        return products;
    }


    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String query= """
                SELECT ov.kaart_nummer, ov.geldig_tot, ov.klasse, ov.saldo,
                p.naam, p.beschrijving, p.prijs, p.product_nummer
                FROM ov_chipkaart ov
                JOIN ov_chipkaart_product ovp
                ON ov.kaart_nummer = ovp.kaart_nummer
                JOIN product p
                ON p.product_nummer = ovp.product_nummer
                """;
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                int productNummer = rs.getInt("product_nummer");
                String naam = rs.getString("naam");
                String beschrijving = rs.getString("beschrijving");
                double prijs = rs.getDouble("prijs");
//                int kaartNummer = rs.getInt("kaart_nummer");
//                java.sql.Date sqlDate = rs.getDate("geldig_tot");
//                LocalDate geldig = sqlDate.toLocalDate();
//                int klasse = rs.getInt("klasse");
//                double saldo = rs.getDouble("saldo");
                Product product = new Product(productNummer, naam, beschrijving, prijs);
                products.add(product);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return products;
    }

}
