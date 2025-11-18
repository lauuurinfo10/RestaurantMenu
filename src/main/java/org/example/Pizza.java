package org.example;
import java.util.Collections;
import java.util.List;
public class Pizza extends Produs {

    private final String blat;
    private final String sos;
    private final List<String> toppinguri;

    private Pizza(PizzaBuilder builder) {
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
    public String toString() {
        return "Pizza finalizată: Blat=" + blat + ", Sos=" + sos +
                ", Toppinguri=" + toppinguri.size();
    }
}