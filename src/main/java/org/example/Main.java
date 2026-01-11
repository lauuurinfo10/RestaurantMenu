package org.example;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.controller.LoginController;
import org.example.model.*;
import org.example.repository.*;
import org.example.service.AuthService;
import org.example.view.LoginView;

public class Main {
    public static void main(String[] args) {

        Configuratie config = Configuratie.incarcaDinFisier("config.json");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("RestaurantPU");

        ProdusRepository produsRepo = new ProdusRepository(emf);
        UserRepository userRepo = new UserRepository(emf);
        MasaRepository masaRepo = new MasaRepository(emf);

        populeazaDateStructurale(userRepo, masaRepo);

        AuthService authService = new AuthService(userRepo);
        LoginController loginController = new LoginController(authService, emf);

        System.out.println("Lansare sistem " + config.getNumeRestaurant() + "...");
        LoginView.lanseaza(loginController, emf);
    }

    private static void populeazaDateStructurale(UserRepository uRepo, MasaRepository mRepo) {
        if (uRepo.gasesteToate().isEmpty()) {
            System.out.println("Baza de date goală. Creez utilizatorii impliciți...");
            uRepo.salveaza(new User("admin", "admin123", RolUser.MANAGER, "Andrei Manager"));
            uRepo.salveaza(new User("ospatar1", "pass1", RolUser.OSPATAR, "Laurr"));
            uRepo.salveaza(new User("ospatar2", "pass2", RolUser.OSPATAR, "Ionel"));
            uRepo.salveaza(new User("guest", "guest", RolUser.CLIENT, "Client Masa"));
        }

        if (mRepo.gasesteToate().isEmpty()) {
            System.out.println("Creez mesele restaurantului...");
            for (int i = 1; i <= 8; i++) {
                mRepo.salveaza(new Masa(i));
            }
        }

    }
}