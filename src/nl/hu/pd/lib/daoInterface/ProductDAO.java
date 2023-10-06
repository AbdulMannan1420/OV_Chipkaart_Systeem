package nl.hu.pd.lib.daoInterface;

import nl.hu.pd.lib.model.OVChipkaart;
import nl.hu.pd.lib.model.Product;

import java.util.List;

public interface ProductDAO {

    public void setOVDAO(OVChipkaartDAO ovdao);
    public boolean save(Product product);
    public boolean update(Product product);
    public boolean delete(Product product);
    public List<Product> findByOVChipkaart(OVChipkaart ov);
    public List<Product> findAll();
}
