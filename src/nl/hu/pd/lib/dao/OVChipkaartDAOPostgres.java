package nl.hu.pd.lib.dao;

import nl.hu.pd.lib.daoInterface.OVChipkaartDAO;
import nl.hu.pd.lib.daoInterface.ReizigerDAO;
import nl.hu.pd.lib.model.OVChipkaart;
import nl.hu.pd.lib.model.Reiziger;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOPostgres implements OVChipkaartDAO {
    private Connection conn;
    private ReizigerDAO reizigerDAO;

    public OVChipkaartDAOPostgres(Connection connection) {
        this.conn = connection;
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
                WHERE kaart_nummer = ?
                """;
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDate(1,java.sql.Date.valueOf(ovChipkaart.getGeldigTot()));
            ps.setInt(2,ovChipkaart.getKlasse());
            ps.setDouble(3, ovChipkaart.getSaldo());
            ps.setInt(4,ovChipkaart.getKaartNummer());
            ps.executeUpdate();
            ps.close();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }


    public boolean delete(OVChipkaart ovChipkaart) {
        String query= """
                DELETE FROM ov_chipkaart
                WHERE reiziger_id = ?
                """;
        try {
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
                Double saldo = rs.getDouble("saldo");
                int reizigerID = rs.getInt("reiziger_id");
                Reiziger reiziger = reizigerDAO.findById(reizigerID);
                OVChipkaart chipkaart = new OVChipkaart(kaartNummer,gelid_tot,klasse,saldo, reiziger);
                chipkaarten.add(chipkaart);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return chipkaarten;
    }
}
