package org.example;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MANCARE") // Aceasta este valoarea care va apărea în coloana tip_produs
public final class Mancare extends Produs {

    private int gramaj;

    // 1. Constructor obligatoriu pentru JPA (Hibernate are nevoie de el)
    public Mancare() {
        super();
    }

    public Mancare(String nume, float pret, CategorieMeniu categorie, boolean esteVegetarian, int gramaj) {
        super(nume, pret, categorie, esteVegetarian);
        this.gramaj = gramaj;
    }

    @Override
    public String getCantitate() {
        return "Gramaj: " + this.gramaj + "g";
    }

    public int getGramaj() {
        return gramaj;
    }

    public void setGramaj(int gramaj) {
        this.gramaj = gramaj;
    }
}