package org.example;

public class Mancare extends Produs{
    private int gramaj;
    public Mancare(String nume,float pret,int gramaj){
        super(nume,pret);
        this.gramaj=gramaj;
    }
    @Override
    public String getCantitate(){
        return "Gramaj:" +this.gramaj +"g";

    }
}
