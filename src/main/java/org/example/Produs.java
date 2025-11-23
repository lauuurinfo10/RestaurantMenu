package org.example;

public abstract sealed class Produs permits Mancare,Bautura,Pizza {
    private String nume;
    private float pret;
    private CategorieMeniu categorie;
    private boolean esteVegetarian;

    public Produs(String nume,float pret,CategorieMeniu categorie,boolean esteVegetarian){
        this.nume=nume;
        this.pret=pret;
        this.categorie=categorie;
        this.esteVegetarian=esteVegetarian;
    }
    public String getNume(){
        return nume;
    }

    public float getPret(){
        return pret;
    }

    public CategorieMeniu getCategorie(){
        return categorie;
    }

    public boolean getEsteVegetarian(){
        return esteVegetarian;
    }

    public String getDetaliiMeniu(){
        return "> "+getNume()+" - "+getPret()+ " RON -  "+getCantitate();

    }
    public abstract String getCantitate();
}
