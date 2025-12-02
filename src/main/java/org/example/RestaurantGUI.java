package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RestaurantGUI extends Application {
    private static Meniu meniu;
    private static String numeRestaurant;

    private ListView<Produs> listaProduse;
    private Label lblNume;
    private Label lblCategorie;
    private TextField txtPret;
    private Label lblCantitate;

    @Override
    public void start(Stage stage) {

        stage.setTitle("Restaurant " + numeRestaurant);
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setLeft(creeazaLista());
        root.setCenter(creeazaFormular());
        Scene scene = new Scene(root, 700, 500);
        stage.setScene(scene);
        stage.show();
    }

    private VBox creeazaLista() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setPrefWidth(250);
        Label titlu = new Label("Lista Produse");
        listaProduse = new ListView<>();
        listaProduse.getItems().addAll(meniu.getAllProduse());
        listaProduse.getSelectionModel().selectedItemProperty().addListener(
                (obs, vechi, nou) -> {
                    if (nou != null) actualizareFormular(nou);
                }
        );

        box.getChildren().addAll(titlu, listaProduse);
        return box;
    }


    private VBox creeazaFormular() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10, 10, 10, 30));
        Label titlu = new Label("Detalii Produs");
        lblNume = new Label("Selectează un produs");
        lblCategorie = new Label("-");
        txtPret = new TextField();
        txtPret.setPromptText("Preț");
        txtPret.setPrefWidth(150);
        lblCantitate = new Label("-");
        Button btnSalveaza = new Button("Salvează Preț");
        btnSalveaza.setOnAction(e -> salveaza());
        box.getChildren().addAll(titlu, lblNume, lblCategorie,
                new Label("Preț:"), txtPret,
                lblCantitate, btnSalveaza);
        return box;
    }

    private void actualizareFormular(Produs p) {
        lblNume.setText(p.getNume());
        lblCategorie.setText("Categorie: " + p.getCategorie());
        txtPret.setText(String.valueOf(p.getPret()));
        lblCantitate.setText(p.getCantitate());
    }

    private void salveaza() {
        Produs p = listaProduse.getSelectionModel().getSelectedItem();
        if (p == null) return;

        try {
            float pretNou = Float.parseFloat(txtPret.getText());
            p.setPret(pretNou);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Preț salvat: " + pretNou + " RON");
            alert.showAndWait();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Preț invalid!");
            alert.showAndWait();
        }
    }

    public static void lanseaza(Meniu m, String nume) {
        meniu = m;
        numeRestaurant = nume;
        launch();
    }
}