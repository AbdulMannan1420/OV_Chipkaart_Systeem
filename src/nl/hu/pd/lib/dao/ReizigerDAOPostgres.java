package nl.hu.pd.lib.dao;

import nl.hu.pd.lib.daoInterface.ReizigerDAO;
import nl.hu.pd.lib.model.Adres;
import nl.hu.pd.lib.model.Reiziger;
import org.postgresql.util.PSQLException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReizigerDAOPostgres implements ReizigerDAO {
    private Connection conn;
    private AdresDAOPostgres ADP;

    public ReizigerDAOPostgres(Connection conn) {
        this.conn = conn;
        ADP = new AdresDAOPostgres(conn);
    }

    public boolean save(Reiziger reiziger){
        try {
            String query = "INSERT INTO reiziger (reiziger_id, voorletters, tussenvoegsel, achternaam, geboortedatum) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, reiziger.getId());
            ps.setString(2, reiziger.getVoorletters());
            ps.setString(3, reiziger.getTussenvoegsel());
            ps.setString(4, reiziger.getAchternaam());
            ps.setDate(5, Date.valueOf(reiziger.getGeboortedatum()));
            ps.executeUpdate();
            ps.close();
            if (reiziger.getAdres() != null){ // adres niet null is, sla het adres ook op
                ADP.save(reiziger.getAdres());
            }
            return true;
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public boolean update(Reiziger reiziger) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE reiziger SET voorletters=?, tussenvoegsel=?, achternaam=?, geboortedatum=? WHERE reiziger_id=?");
            preparedStatement.setString(1, reiziger.getVoorletters());
            preparedStatement.setString(2, reiziger.getTussenvoegsel());
            preparedStatement.setString(3, reiziger.getAchternaam());
            preparedStatement.setObject(4, java.sql.Date.valueOf(reiziger.getGeboortedatum()));
            preparedStatement.setInt(5, reiziger.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            if (reiziger.getAdres() != null){ // als reiziger adres heeft, moet dat ook opgeslagen worden
                ADP.update(reiziger.getAdres());
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Reiziger reiziger) {
        try {
            ReizigerDAO rdao = new ReizigerDAOPostgres(conn);// checken of er ook adres bestaat ...
            Reiziger r = rdao.findById(reiziger.getId()); // ivm foreign_key constraint violation
            if (r.getAdres() != null){ // eerst adres verwijderen als dat bestaat
                ADP.delete(r.getAdres());
            }
            PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM reiziger WHERE reiziger_id=?");
            preparedStatement.setInt(1, reiziger.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Reiziger findById(int id) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM reiziger WHERE reiziger_id=?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {  // checken of er een reiziger met gegeven id bestaat en is returned
                String voorletters = resultSet.getString("voorletters");
                String tussenvoegsel = resultSet.getString("tussenvoegsel");
                String achternaam = resultSet.getString("achternaam");
                java.sql.Date geboortedatum = resultSet.getDate("geboortedatum");
                LocalDate geboortedatumLocal = geboortedatum.toLocalDate();
                resultSet.close();
                preparedStatement.close();
                Reiziger reiziger = new Reiziger(id, voorletters, tussenvoegsel, achternaam, geboortedatumLocal);
                Adres adres = ADP.findByReiziger(reiziger); // adres opzoeken
                reiziger.setAdres(adres); // adres toevoegen aan reiziger
                return reiziger;
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public List<Reiziger> findByGbdatum(LocalDate ld) {
        List<Reiziger> reizigers = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM reiziger WHERE geboortedatum=?");
            java.sql.Date sqlDate = java.sql.Date.valueOf(ld);
            preparedStatement.setDate(1, sqlDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("reiziger_id");
                String voorletters = resultSet.getString("voorletters");
                String tussenvoegsel = resultSet.getString("tussenvoegsel");
                String achternaam = resultSet.getString("achternaam");
                java.sql.Date geboortedatum = resultSet.getDate("geboortedatum");
                LocalDate geboortedatumLocal = geboortedatum.toLocalDate();
                Reiziger reiziger = new Reiziger(id, voorletters, tussenvoegsel, achternaam, geboortedatumLocal);
                Adres adres = ADP.findByReiziger(reiziger); // adres opzoeken
                reiziger.setAdres(adres); // adres toevoegen aan reiziger
                reizigers.add(reiziger);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reizigers;
    }
    public List<Reiziger> findAll(){
        List<Reiziger> reizigers = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM reiziger");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("reiziger_id");
                String voorletters = resultSet.getString("voorletters");
                String tussenvoegsel = resultSet.getString("tussenvoegsel");
                String achternaam = resultSet.getString("achternaam");
                java.sql.Date geboortedatum = resultSet.getDate("geboortedatum");
                Reiziger reiziger = new Reiziger(id, voorletters, tussenvoegsel, achternaam, geboortedatum.toLocalDate());
                Adres adres = ADP.findByReiziger(reiziger); // adres opzoeken en toevoegen aan reiziger object
                reiziger.setAdres(adres);
                reizigers.add(reiziger);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reizigers;
    }
    }
