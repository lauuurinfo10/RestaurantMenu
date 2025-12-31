package org.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        Configuratie config = Configuratie.incarcaDinFisier("config.json");

        ProdusRepository repository = new ProdusRepository();


        List<Produs> produseExistente = repository.gasesteToate();

        if (produseExistente.isEmpty()) {
            System.out.println("Baza de date e goală. Populez cu date inițiale...");


            repository.salveaza(new Mancare("Pizza Margherita", 45.0f, CategorieMeniu.FelPrincipal, true, 450));
            repository.salveaza(new Mancare("Paste Carbonara", 52.5f, CategorieMeniu.FelPrincipal, false, 400));
            repository.salveaza(new Mancare("Salată Caesar", 35.0f, CategorieMeniu.Aperitive, false, 300));
            repository.salveaza(new Mancare("Tiramisu", 28.0f, CategorieMeniu.Desert, true, 200));

            repository.salveaza(new Bautura("Limonada", 15.0f, CategorieMeniu.BauturiRacoritoare, true, 400));
            repository.salveaza(new Bautura("Apa Plata", 8.0f, CategorieMeniu.BauturiRacoritoare, true, 500));
            repository.salveaza(new Bautura("Vin Rosu", 45.0f, CategorieMeniu.BauturiAlcoolice, false, 150));

            Pizza pizza = Pizza.builder("Quattro Formaggi")
                    .cuBlat("Subtire")
                    .cuSos("Sos de rosii")
                    .cuPret(55.0)
                    .vegetariana(true)
                    .adaugaTopping("Mozzarella")
                    .adaugaTopping("Gorgonzola")
                    .build();
            repository.salveaza(pizza);

            System.out.println("Date inițiale adăugate!");
        } else {
            System.out.println("Baza de date conține " + produseExistente.size() + " produse.");
        }


        System.out.println("Lansare interfață grafică...");
        RestaurantGUI.lanseaza(repository, config.getNumeRestaurant());
    }
}