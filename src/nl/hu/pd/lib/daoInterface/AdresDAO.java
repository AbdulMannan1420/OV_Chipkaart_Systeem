package nl.hu.pd.lib.daoInterface;

import nl.hu.pd.lib.model.Adres;
import nl.hu.pd.lib.model.Reiziger;

import java.util.List;

public interface AdresDAO {

    public void setReizigerDAO(ReizigerDAO reizigerDAO);
    public boolean save(Adres adres);
    public boolean update(Adres adres);
    public boolean delete(Adres adres);
    public Adres findByReiziger(Reiziger reiziger);
    public List<Adres> findAll();
}
