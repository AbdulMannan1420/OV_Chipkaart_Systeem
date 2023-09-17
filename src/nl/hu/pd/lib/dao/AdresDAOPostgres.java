package nl.hu.pd.lib.dao;

import nl.hu.pd.lib.daoInterface.AdresDAO;
import nl.hu.pd.lib.model.Adres;
import nl.hu.pd.lib.model.Reiziger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdresDAOPostgres implements AdresDAO {
    private Connection conn;

    public AdresDAOPostgres(Connection conn) {
        this.conn = conn;
    }

    public boolean save(Adres adres) {
        String query = """
                    INSERT INTO adres(adres_id, postcode,huisnummer,straat, woonplaats, reiziger_id)
                    VALUES (?,?,?,?,?,?);
                    """;
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1,adres.getAdres_id());
            ps.setString(2, adres.getPostcode());
            ps.setString(3,adres.getHuisnummer());
            ps.setString(4, adres.getStraat());
            ps.setString(5, adres.getWoonplaats());
            ps.setInt(6,adres.getAdres_id());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Adres adres) {
        String query= """
                UPDATE adres
                SET adres_id = ?, postcode = ?, huisnummer= ?,
                straat = ?, woonplaats = ?
                WHERE  reiziger_id = ?
                """;
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1,adres.getAdres_id());
            ps.setString(2, adres.getPostcode());
            ps.setString(3,adres.getHuisnummer());
            ps.setString(4, adres.getStraat());
            ps.setString(5, adres.getWoonplaats());
            ps.setInt(6,adres.getReiziger_id());
            ps.executeUpdate();
            ps.close();
            return true;
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Adres adres) {
        String query= """
                DELETE FROM adres
                WHERE reiziger_id = ?
                """;
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1,adres.getReiziger_id());
            ps.executeUpdate();
            ps.close();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
    }}

    public Adres findByReiziger(Reiziger reiziger) {
        String query= """
                SELECT adres_id,postcode,huisnummer,straat,woonplaats,reiziger_id
                FROM adres
                WHERE reiziger_id = ?
                """;
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1,reiziger.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                int adres_id = rs.getInt("adres_id");
                String postcode = rs.getString("postcode");
                String huisnummer = rs.getString("huisnummer");
                String straat = rs.getString("straat");
                String woonplaats = rs.getString("woonplaats");
                int reiziger_id = rs.getInt("reiziger_id");
                return new Adres(adres_id,postcode,huisnummer,straat,woonplaats,reiziger_id);
            }
            rs.close();
            ps.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<Adres> findAll() {
        List<Adres> adressen = new ArrayList<>();
        String query = """
                SELECT adres_id, postcode,huisnummer,straat,woonplaats,reiziger_id
                FROM adres
                """;
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                int adres_id = rs.getInt("adres_id");
                String postcode = rs.getString("postcode");
                String huisnummer = rs.getString("huisnummer");
                String straat = rs.getString("straat");
                String woonplaats = rs.getString("woonplaats");
                int reiziger_id = rs.getInt("reiziger_id");
                Adres adres = new Adres(adres_id,postcode,huisnummer,straat,woonplaats,reiziger_id);
                adressen.add(adres);
            }
            rs.close();
            ps.close();
        }catch (SQLException e){
         e.printStackTrace();
        }
        return adressen;
    }
}
