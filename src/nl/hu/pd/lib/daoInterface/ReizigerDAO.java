package nl.hu.pd.lib.daoInterface;

import nl.hu.pd.lib.model.Reiziger;

import java.time.LocalDate;
import java.util.List;

public interface ReizigerDAO {
    public boolean save(Reiziger reiziger);
    public boolean update(Reiziger reiziger);
    public boolean delete(Reiziger reiziger);
    public Reiziger findById(int i);
    public List<Reiziger> findByGbdatum(LocalDate ld);
    public List<Reiziger> findAll();
}
