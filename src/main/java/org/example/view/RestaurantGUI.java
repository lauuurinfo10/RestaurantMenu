package org.example.view;

import com.google.gson.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.model.Bautura;
import org.example.model.Mancare;
import org.example.model.Pizza;
import org.example.model.Produs;
import org.example.repository.ProdusRepository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI pentru Restaurant cu MenuBar și persistență PostgreSQL
 */
public class RestaurantGUI extends Application {
    private static String numeRestaurant;
    private static ProdusRepository repository;

    private ListView<Produs> listaProduse;
    private Label lblNume;
    private Label lblCategorie;
    private TextField txtPret;
    private Label lblCantitate;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Restaurant " + numeRestaurant);

        BorderPane root = new BorderPane();

        MenuBar menuBar = creeazaMenuBar(stage);
        root.setTop(menuBar);

        BorderPane content = new BorderPane();
        content.setPadding(new Insets(10));
        content.setLeft(creeazaLista());
        content.setCenter(creeazaFormular());

        root.setCenter(content);

        Scene scene = new Scene(root, 700, 500);
        stage.setScene(scene);
        stage.show();

        incarcaProduseDinDB();
    }

    private MenuBar creeazaMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();

        Menu menuFile = new Menu("File");

        MenuItem exportItem = new MenuItem("Export JSON");
        exportItem.setOnAction(e -> exportJSON(stage));

        MenuItem importItem = new MenuItem("Import JSON");
        importItem.setOnAction(e -> importJSON(stage));

        menuFile.getItems().addAll(exportItem, importItem);
        menuBar.getMenus().add(menuFile);

        return menuBar;
    }

    private VBox creeazaLista() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setPrefWidth(250);

        Label titlu = new Label("Lista Produse");

        listaProduse = new ListView<>();

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

            repository.actualizeaza(p);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Preț salvat: " + pretNou + " RON");
            alert.showAndWait();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Preț invalid!");
            alert.showAndWait();
        }
    }

    private void incarcaProduseDinDB() {
        try {
            List<Produs> produse = repository.gasesteToate();
            listaProduse.getItems().clear();
            listaProduse.getItems().addAll(produse);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Eroare la încărcare din DB: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void exportJSON(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export JSON");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );
        fileChooser.setInitialFileName("meniu_export.json");

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                List<Produs> produse = repository.gasesteToate();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                try (FileWriter writer = new FileWriter(file)) {
                    gson.toJson(produse, writer);
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Export reușit: " + file.getName());
                alert.showAndWait();

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Eroare la export: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    private void importJSON(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Produs.class, new ProdusDeserializer())
                        .create();

                try (FileReader reader = new FileReader(file)) {
                    Produs[] produseArr = gson.fromJson(reader, Produs[].class);

                    if (produseArr != null) {
                        List<Produs> deSalvat = new ArrayList<>();
                        for (Produs p : produseArr) {
                            p.setId(null);
                            deSalvat.add(p);
                        }
                        repository.salveazaToate(deSalvat);
                        incarcaProduseDinDB();

                        new Alert(Alert.AlertType.INFORMATION, "Import reușit!").show();
                    }
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Eroare la import: " + e.getMessage()).show();
            }
        }
    }

    private static class ProdusDeserializer implements JsonDeserializer<Produs> {
        @Override
        public Produs deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                                  JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            if (obj.has("gramaj")) {
                return context.deserialize(json, Mancare.class);
            } else if (obj.has("volum")) {
                return context.deserialize(json, Bautura.class);
            } else if (obj.has("blat") || obj.has("toppinguri")) {
                return context.deserialize(json, Pizza.class);
            }
            throw new JsonParseException("Tip de produs necunoscut!");
        }
    }

    public static void lanseaza(ProdusRepository repo, String nume) {
        repository = repo;
        numeRestaurant = nume;
        launch();
    }

    @Override
    public void stop() {
        if (repository != null) {
            repository.close();
        }
    }
}