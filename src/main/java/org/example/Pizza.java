package org.example;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
public class Pizza extends Produs {

    private final String blat;
    private final String sos;
    private final List<String> toppinguri;

    private Pizza(PizzaBuilder builder) {
        super(builder.nume, (float) builder.pret, builder.categorie != null ? builder.categorie : CategorieMeniu.FelPrincipal, builder.esteVegetarian);
        this.blat = builder.blat;
        this.sos = builder.sos;
        this.toppinguri = builder.toppinguri != null ? builder.toppinguri : Collections.emptyList();
    }

    public static PizzaBuilder builder() {
        return new PizzaBuilder();
    }

    public String getBlat() {
        return blat;
    }

    public String getSos() {
        return sos;
    }

    public List<String> getToppinguri() {
        return toppinguri;
    }

    @Override
    public String getCantitate() {
        int nrToppinguri = this.toppinguri.size();
        return "Pizza, Blat: " + this.blat + " (" + nrToppinguri + " topping" + (nrToppinguri != 1 ? "uri" : "") + ")";
    }

    public static class PizzaBuilder {
        private String nume;
        private double pret;
        private CategorieMeniu categorie;
        private boolean esteVegetarian;

        private String blat;
        private String sos;
        private List<String> toppinguri;

        public PizzaBuilder cuBlat(String blat) {
            this.blat = blat;
            return this;
        }

        public PizzaBuilder cuSos(String sos) {
            this.sos = sos;
            return this;
        }
        public PizzaBuilder cuPret(double pret){
            this.pret=pret;
            return this;
        }

        public PizzaBuilder adaugaTopping(String topping) {
            if (this.toppinguri == null) {
                this.toppinguri = new ArrayList<>();
            }
            this.toppinguri.add(topping);
            return this;
        }

        public Pizza build() {
            if (blat == null || sos == null) {
                throw new IllegalStateException("Blatul și sosul sunt obligatorii pentru a construi o pizza.");
            }
            return new Pizza(this);
        }
    }
}