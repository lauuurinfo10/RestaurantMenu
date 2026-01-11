package org.example.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("MANCARE")
public final class Mancare extends Produs {

    private int gramaj;

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