package org.example.controller;

import javafx.stage.Stage;
import org.example.model.RolUser;
import org.example.model.User;
import org.example.service.AuthService;
import org.example.view.ClientView;
import org.example.view.OspatarView;
import org.example.view.ManagerView;

import jakarta.persistence.EntityManagerFactory;

public class LoginController {
    private AuthService authService;
    private EntityManagerFactory emf;

    public LoginController(AuthService authService, EntityManagerFactory emf) {
        this.authService = authService;
        this.emf = emf;
    }

    public boolean proceseazaLogin(String username, String password, Stage stage) {
        User user = authService.login(username, password);

        if (user != null) {
            redirectionareRol(user, stage);
            return true;
        }

        return false;
    }

    private void redirectionareRol(User user, Stage stage) {
        stage.close();

        Stage nouaFereastra = new Stage();

        switch (user.getRol()) {
            case CLIENT:
                ClientView clientView = new ClientView(emf);
                clientView.start(nouaFereastra);
                break;

            case OSPATAR:
                OspatarView ospatarView = new OspatarView(user, emf);
                ospatarView.start(nouaFereastra);
                break;

            case MANAGER:
                ManagerView managerView = new ManagerView(user, emf);
                managerView.start(nouaFereastra);
                break;
        }
    }
}
