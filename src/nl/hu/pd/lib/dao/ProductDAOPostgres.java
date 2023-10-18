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

    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String query1 = """
            SELECT product_nummer, naam, beschrijving, prijs FROM product;
            """;
        String query2 = """
            SELECT ov.kaart_nummer, ov.geldig_tot, ov.klasse, ov.saldo 
            FROM ov_chipkaart_product ovp
            JOIN ov_chipkaart ov
            ON ovp.kaart_nummer = ov.kaart_nummer
            WHERE ovp.product_nummer = ?;
            """;
        try {
            PreparedStatement ps1 = conn.prepareStatement(query1);
            ResultSet rs1 = ps1.executeQuery();
            while (rs1.next()) {
                int productNummer = rs1.getInt("product_nummer");
                String naam = rs1.getString("naam");
                String beschrijving = rs1.getString("beschrijving");
                double prijs = rs1.getDouble("prijs");
                Product product = new Product(productNummer, naam, beschrijving, prijs);

                PreparedStatement ps2 = conn.prepareStatement(query2);
                ps2.setInt(1, productNummer);
                ResultSet rs2 = ps2.executeQuery();

                while (rs2.next()) {
                    int kaartNummer = rs2.getInt("kaart_nummer");
                    java.sql.Date sqlDate = rs2.getDate("geldig_tot");
                    LocalDate geldig = sqlDate.toLocalDate();
                    int klasse = rs2.getInt("klasse");
                    double saldo = rs2.getDouble("saldo");
                    OVChipkaart ovChipkaart = new OVChipkaart(kaartNummer,geldig,klasse,saldo);
                    product.addOVChipkaart(ovChipkaart);
                }
                products.add(product);
                ps2.close();
                rs2.close();
            }
            ps1.close();
            rs1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

}
