package org.example;

public class Main {
    public static void main(String[] args) {

        Configuratie config = Configuratie.incarcaDinFisier("config.json");
        Meniu meniu = new Meniu();
        meniu.adaugaProdus(new Mancare("Pizza Margherita", 45.0f, CategorieMeniu.FelPrincipal, true, 450));
        meniu.adaugaProdus(new Mancare("Paste Carbonara", 52.5f, CategorieMeniu.FelPrincipal, false, 400));
        meniu.adaugaProdus(new Mancare("Cheesecake", 32.0f, CategorieMeniu.Desert, true, 150));
        meniu.adaugaProdus(new Bautura("Limonada", 15.0f, CategorieMeniu.BauturiRacoritoare, true, 400));
        meniu.adaugaProdus(new Bautura("Apa Plata", 8.0f, CategorieMeniu.BauturiRacoritoare, true, 500));
        meniu.adaugaProdus(new Bautura("Coca-Cola", 12.0f, CategorieMeniu.BauturiRacoritoare, true, 200));

        Pizza pizza = Pizza.builder("Quattro Formaggi")
                .cuBlat("Subtire")
                .cuSos("Sos de rosii")
                .cuPret(55.0)
                .vegetariana(true)
                .adaugaTopping("Mozzarella")
                .adaugaTopping("Gorgonzola")
                .build();
        meniu.adaugaProdus(pizza);
        meniu.afiseazaMeniu(config.getNumeRestaurant());
        System.out.println();
        Comanda comanda = new Comanda(config.getCotaTVA());
        comanda.adaugaProdus(meniu.getAllProduse().get(0), 2);
        comanda.adaugaProdus(meniu.getAllProduse().get(2), 1);
        comanda.afiseazaDetalii();

        IstrategieReducere happyHour = new HappyHour();
        float total = comanda.calculeazaPretCuTva(happyHour);
        System.out.println("Total cu Happy Hour: " + total + " RON\n");

        System.out.println(" Produse vegetariene ");
        meniu.getVegetarieneSortateAlfabetic().forEach(p ->
                System.out.println("- " + p.getNume())
        );

        System.out.println("\n=== Preț mediu deserturi ===");
        meniu.getPretMediuDeserturi().ifPresent(pret ->
                System.out.println("Preț mediu: " + pret + " RON")
        );

        meniu.exportaInJSON("meniu_export.json");
        RestaurantGUI.lanseaza(meniu, config.getNumeRestaurant());
    }
}