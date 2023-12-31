package nl.hu.pd.lib.model;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private int productNummer;
    private String naam;
    private String beschrijving;
    private double prijs;
    private List<OVChipkaart> ovChipkaarten = new ArrayList<>();

    public Product(int productNummer, String naam, String beschrijving, double prijs) {
        this.productNummer = productNummer;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.prijs = prijs;
    }

    public Product(int productNummer, String naam, String beschrijving, double prijs, List<OVChipkaart> ovChipkaarten) {
        this.productNummer = productNummer;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.prijs = prijs;
        this.ovChipkaarten = ovChipkaarten;
    }

    public boolean addOVChipkaart(OVChipkaart ovChipkaart){
        ovChipkaarten.add(ovChipkaart);
        return true;
    }

    public boolean removeOVChipkaart(OVChipkaart ovChipkaart){
        return ovChipkaarten.remove(ovChipkaart);
    }

    public List<OVChipkaart> getOvChipkaarten() {
        return ovChipkaarten;
    }

    public void setOvChipkaarten(List<OVChipkaart> ovChipkaarten) {
        this.ovChipkaarten = ovChipkaarten;
    }

    public int getProductNummer() {
        return productNummer;
    }

    public void setProductNummer(int productNummer) {
        this.productNummer = productNummer;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public double getPrijs() {
        return prijs;
    }

    public void setPrijs(double prijs) {
        this.prijs = prijs;
    }

    public String toString() {    StringBuilder productInfo = new StringBuilder("Product : Nummer: " + productNummer + ", naam: " + naam + ",\n" +
            "beschrijving: " + beschrijving  + " Prijs: " + prijs+"\n");

        if (ovChipkaarten != null && !ovChipkaarten.isEmpty()) {
            productInfo.append("OVChipkaarten voor deze product: ");
            for (OVChipkaart ovChipkaart : ovChipkaarten) {
                productInfo.append("\nKaartnummer: ").append(ovChipkaart.getKaartNummer());
            }
            productInfo.append("\n");
        }

        return productInfo+"";
    }
}
