package org.example.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.controller.LoginController;

import jakarta.persistence.EntityManagerFactory;

public class LoginView extends Application {
    private static EntityManagerFactory emf;
    private static LoginController loginController;

    private TextField txtUsername;
    private PasswordField txtPassword;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Restaurant La Andrei - Login");

        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        Label lblTitlu = new Label("Restaurant La Andrei");
        lblTitlu.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label lblSubtitlu = new Label("Autentificare");
        lblSubtitlu.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");

        Label lblUsername = new Label("Username:");
        txtUsername = new TextField();
        txtUsername.setPromptText("Introdu username-ul");
        txtUsername.setPrefWidth(250);

        Label lblPassword = new Label("Parolă:");
        txtPassword = new PasswordField();
        txtPassword.setPromptText("Introdu parola");
        txtPassword.setPrefWidth(250);

        Button btnLogin = new Button("Login");
        btnLogin.setPrefWidth(250);
        btnLogin.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        btnLogin.setOnAction(e -> proceseazaLogin(stage));


        txtPassword.setOnAction(e -> proceseazaLogin(stage));

        Hyperlink linkGuest = new Hyperlink("Intră ca Guest (Client)");
        linkGuest.setOnAction(e -> loginGuest(stage));

        Label lblEroare = new Label();
        lblEroare.setStyle("-fx-text-fill: red;");
        lblEroare.setVisible(false);

        root.getChildren().addAll(
                lblTitlu,
                lblSubtitlu,
                new Label(),
                lblUsername,
                txtUsername,
                lblPassword,
                txtPassword,
                lblEroare,
                btnLogin,
                linkGuest
        );

        Scene scene = new Scene(root, 400, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void proceseazaLogin(Stage stage) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            afiseazaEroare("Completează toate câmpurile!");
            return;
        }

        boolean success = loginController.proceseazaLogin(username, password, stage);

        if (!success) {
            afiseazaEroare("Username sau parolă greșită!");
            txtPassword.clear();
        }
    }

    private void loginGuest(Stage stage) {
        loginController.proceseazaLogin("guest", "guest", stage);
    }

    private void afiseazaEroare(String mesaj) {
        Label lblEroare = (Label) ((VBox) txtPassword.getParent()).getChildren().get(7);
        lblEroare.setText(mesaj);
        lblEroare.setVisible(true);
    }

    public static void lanseaza(LoginController controller, EntityManagerFactory entityManagerFactory) {
        loginController = controller;
        emf = entityManagerFactory;
        launch();
    }
}
