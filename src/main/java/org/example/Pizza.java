package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Pizza extends Produs {
    private final String blat;
    private final String sos;
    private final List<String> toppinguri;

    private Pizza(PizzaBuilder builder) {
        super("Pizza " + builder.numePizza, (float) builder.pret, CategorieMeniu.FelPrincipal, builder.esteVegetarian);
        this.blat = builder.blat;
        this.sos = builder.sos;
        this.toppinguri = new ArrayList<>(builder.toppinguri);
    }

    public static PizzaBuilder builder(String numePizza) {
        return new PizzaBuilder(numePizza);
    }

    public String getBlat() {
        return blat;
    }

    public String getSos() {
        return sos;
    }

    public List<String> getToppinguri() {
        return Collections.unmodifiableList(toppinguri);
    }

    @Override
    public String getCantitate() {
        int nrToppinguri = this.toppinguri.size();
        return "Pizza, Blat: " + this.blat + ", Sos: " + this.sos + " (" + nrToppinguri + " topping" + (nrToppinguri != 1 ? "uri" : "") + ")";
    }

    public static class PizzaBuilder {
        private final String numePizza;
        private double pret;
        private boolean esteVegetarian;

        private String blat;
        private String sos;
        private List<String> toppinguri;

        private PizzaBuilder(String numePizza) {
            this.numePizza = numePizza;
            this.toppinguri = new ArrayList<>();
            this.esteVegetarian = false;
        }

        public PizzaBuilder cuBlat(String blat) {
            this.blat = blat;
            return this;
        }

        public PizzaBuilder cuSos(String sos) {
            this.sos = sos;
            return this;
        }

        public PizzaBuilder cuPret(double pret) {
            this.pret = pret;
            return this;
        }

        public PizzaBuilder vegetariana(boolean vegetariana) {
            this.esteVegetarian = vegetariana;
            return this;
        }

        public PizzaBuilder adaugaTopping(String topping) {
            this.toppinguri.add(topping);
            return this;
        }

        public Pizza build() {
            if (blat == null || blat.isEmpty()) {
                throw new IllegalStateException("Blatul este obligatoriu pentru a construi  pizza.");
            }
            if (sos == null || sos.isEmpty()) {
                throw new IllegalStateException("Sosul este obligatoriu pentru a construi  pizza.");
            }
            return new Pizza(this);
        }
    }
}