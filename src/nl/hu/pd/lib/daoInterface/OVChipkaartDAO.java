package nl.hu.pd.lib.daoInterface;

import nl.hu.pd.lib.model.OVChipkaart;
import nl.hu.pd.lib.model.Product;
import nl.hu.pd.lib.model.Reiziger;

import java.util.List;

public interface OVChipkaartDAO {
    public void setProductDAO(ProductDAO productDAO);
    public void setReizigerDAO(ReizigerDAO reizigerDAO);
    public boolean save(OVChipkaart ovChipkaart);
    public boolean update(OVChipkaart ovChipkaart);
    public boolean delete(OVChipkaart ovChipkaart);
    public List<OVChipkaart> findByReiziger(Reiziger reiziger);
    public List<OVChipkaart> findAll();
}
