package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "produse")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tip_produs", discriminatorType = DiscriminatorType.STRING)

public abstract class Produs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nume;

    @Column(nullable = false)
    private float pret;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorieMeniu categorie;

    @Column(name = "este_vegetarian")
    private boolean esteVegetarian;

    protected Produs() {}

    public Produs(String nume, float pret, CategorieMeniu categorie, boolean esteVegetarian) {
        this.nume = nume;
        this.pret = pret;
        this.categorie = categorie;
        this.esteVegetarian = esteVegetarian;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }

    public float getPret() { return pret; }
    public void setPret(float pret) { this.pret = pret; }

    public CategorieMeniu getCategorie() { return categorie; }
    public void setCategorie(CategorieMeniu categorie) { this.categorie = categorie; }

    public boolean getEsteVegetarian() { return esteVegetarian; }
    public void setEsteVegetarian(boolean esteVegetarian) { this.esteVegetarian = esteVegetarian; }

    public String getDetaliiMeniu() {
        return "> " + getNume() + " - " + getPret() + " RON - " + getCantitate();
    }

    public abstract String getCantitate();

    @Override
    public String toString() {
        return nume;
    }
}