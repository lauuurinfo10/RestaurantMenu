package org.example;

public class Bautura extends Produs {
    private int volum;
    public Bautura(String nume,float pret,int volum){
        super(nume,pret);
        this.volum=volum;
    }
    @Override
    public String getCantitate(){
        return "Volum: "+this.volum+"ml";
    }
}
