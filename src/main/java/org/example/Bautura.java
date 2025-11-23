package org.example;

public final class Bautura extends Produs {
    private int volum;
    public Bautura(String nume,float pret,CategorieMeniu categorie,boolean esteVegetarian,int volum){
        super(nume,pret,categorie,esteVegetarian);
        this.volum=volum;
    }
    @Override
    public String getCantitate(){
        return "Volum: "+this.volum+"ml";
    }

    public int getVolum() {
        return volum;
    }
}
