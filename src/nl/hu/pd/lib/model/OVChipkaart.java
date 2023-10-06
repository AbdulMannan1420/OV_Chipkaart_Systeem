package nl.hu.pd.lib.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaart {
    private int kaartNummer;
    private LocalDate geldigTot;
    private int klasse;
    private double saldo;
    private Reiziger reiziger;
    private List<Product> products = new ArrayList<>();

    public OVChipkaart(int kaartNummer, LocalDate geldigTot, int klasse, double saldo, Reiziger reiziger) {
        this.kaartNummer = kaartNummer;
        this.geldigTot = geldigTot;
        this.klasse = klasse;
        this.saldo = saldo;
        this.reiziger = reiziger;
    }
    public OVChipkaart(int kaartNummer, LocalDate geldigTot, int klasse, double saldo, Reiziger reiziger, List<Product> products) {
        this.kaartNummer = kaartNummer;
        this.geldigTot = geldigTot;
        this.klasse = klasse;
        this.saldo = saldo;
        this.reiziger = reiziger;
        this.products = products;
    }

    public OVChipkaart(int kaartNummer, LocalDate geldigTot, int klasse, double saldo) {
        this.kaartNummer = kaartNummer;
        this.geldigTot = geldigTot;
        this.klasse = klasse;
        this.saldo = saldo;
    }


    public void addProduct(Product product){
        products.add(product);
    }
    public void removeProduct(Product product){
        products.remove(product);
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public int getKaartNummer() {
        return kaartNummer;
    }

    public void setKaartNummer(int kaartNummer) {
        this.kaartNummer = kaartNummer;
    }

    public LocalDate getGeldigTot() {
        return geldigTot;
    }

    public void setGeldigTot(LocalDate geldigTot) {
        this.geldigTot = geldigTot;
    }

    public int getKlasse() {
        return klasse;
    }

    public void setKlasse(int klasse) {
        this.klasse = klasse;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public Reiziger getReiziger() {
        return reiziger;
    }

    public void setReiziger(Reiziger reiziger) {
        this.reiziger = reiziger;
    }

    @Override
    public String toString() {
        StringBuilder ovchip = new StringBuilder();
        String id = null;
        if(reiziger != null){
            id = ""+reiziger.getId();
        }
        ovchip.append("kaartNummer: ")
                .append(kaartNummer)
                .append(" geldig tot: ")
                .append(geldigTot)
                .append("\nReisklasse: ")
                .append(klasse).append(" met saldo: ")
                .append(saldo).append(" reiziger id: ")
                .append(id).append("\n");
        if (products != null) {
            if (!products.isEmpty()) {
                int count = 0;
                for (Product p : products) {
                    count = count+1;
                    ovchip.append(count)
                            .append(" :")
                            .append(p.toString())
                            .append("\n");
                }
            }
        }
        return ovchip+"";
    }
}
