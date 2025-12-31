package org.example;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BAUTURA") // Valoarea care va fi salvată în coloana tip_produs
public final class Bautura extends Produs {

    private int volum;

    // Constructor gol necesar pentru Hibernate/JPA
    public Bautura() {
        super();
    }

    public Bautura(String nume, float pret, CategorieMeniu categorie, boolean esteVegetarian, int volum) {
        super(nume, pret, categorie, esteVegetarian);
        this.volum = volum;
    }

    @Override
    public String getCantitate() {
        return "Volum: " + this.volum + "ml";
    }

    public int getVolum() {
        return volum;
    }

    public void setVolum(int volum) {
        this.volum = volum;
    }
}