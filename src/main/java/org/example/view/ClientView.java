package org.example.view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.CategorieMeniu;
import org.example.model.Produs;
import org.example.repository.ProdusRepository;

import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClientView extends Application {
    private EntityManagerFactory emf;
    private ProdusRepository produsRepository;

    private TableView<Produs> tabelProduse;
    private ObservableList<Produs> listaProduse;
    private List<Produs> toateProdusele;

    private CheckBox chkVegetarian;
    private ComboBox<String> cmbTip;
    private TextField txtPretMin;
    private TextField txtPretMax;
    private TextField txtCautare;

    private Label lblNumeDetalii;
    private Label lblPretDetalii;
    private Label lblCategorieDetalii;
    private Label lblCantitateDetalii;
    private Label lblVegetarianDetalii;

    public ClientView(EntityManagerFactory emf) {
        this.emf = emf;
        this.produsRepository = new ProdusRepository(emf);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Client - Vizualizare Meniu");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        root.setTop(creazaPanouFiltre());

        root.setCenter(creazaTabelProduse());

        root.setRight(creazaPanouDetalii());

        incarcaProduse();

        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.show();
    }

    private VBox creazaPanouFiltre() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #f0f0f0;");

        Label lblTitlu = new Label("Filtre și Căutare");
        lblTitlu.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox boxCautare = new HBox(10);
        Label lblCautare = new Label("Căutare:");
        txtCautare = new TextField();
        txtCautare.setPromptText("Caută după nume...");
        txtCautare.setPrefWidth(200);
        txtCautare.textProperty().addListener((obs, old, nou) -> aplicaFiltre());
        Button btnResetCautare = new Button("✕");
        btnResetCautare.setOnAction(e -> {
            txtCautare.clear();
            aplicaFiltre();
        });
        boxCautare.getChildren().addAll(lblCautare, txtCautare, btnResetCautare);

        chkVegetarian = new CheckBox("Doar produse vegetariene");
        chkVegetarian.selectedProperty().addListener((obs, old, nou) -> aplicaFiltre());

        HBox boxTip = new HBox(10);
        Label lblTip = new Label("Tip:");
        cmbTip = new ComboBox<>();
        cmbTip.getItems().addAll("Toate", "Mancare", "Bautura", "Pizza");
        cmbTip.setValue("Toate");
        cmbTip.setOnAction(e -> aplicaFiltre());
        boxTip.getChildren().addAll(lblTip, cmbTip);

        HBox boxPret = new HBox(10);
        Label lblPret = new Label("Interval preț:");
        txtPretMin = new TextField();
        txtPretMin.setPromptText("Min");
        txtPretMin.setPrefWidth(80);
        txtPretMin.textProperty().addListener((obs, old, nou) -> aplicaFiltre());

        Label lblPana = new Label("-");

        txtPretMax = new TextField();
        txtPretMax.setPromptText("Max");
        txtPretMax.setPrefWidth(80);
        txtPretMax.textProperty().addListener((obs, old, nou) -> aplicaFiltre());

        boxPret.getChildren().addAll(lblPret, txtPretMin, lblPana, txtPretMax);

        Button btnReset = new Button("Resetează Filtre");
        btnReset.setOnAction(e -> reseteazaFiltre());

        box.getChildren().addAll(
                lblTitlu,
                new Separator(),
                boxCautare,
                chkVegetarian,
                boxTip,
                boxPret,
                btnReset
        );

        return box;
    }

    private TableView<Produs> creazaTabelProduse() {
        tabelProduse = new TableView<>();
        tabelProduse.setPrefWidth(600);

        TableColumn<Produs, String> colNume = new TableColumn<>("Nume");
        colNume.setCellValueFactory(new PropertyValueFactory<>("nume"));
        colNume.setPrefWidth(250);

        TableColumn<Produs, Float> colPret = new TableColumn<>("Preț (RON)");
        colPret.setCellValueFactory(new PropertyValueFactory<>("pret"));
        colPret.setPrefWidth(100);

        TableColumn<Produs, CategorieMeniu> colCategorie = new TableColumn<>("Categorie");
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colCategorie.setPrefWidth(150);

        TableColumn<Produs, Boolean> colVegetarian = new TableColumn<>("Vegetarian");
        colVegetarian.setCellValueFactory(new PropertyValueFactory<>("esteVegetarian"));
        colVegetarian.setPrefWidth(100);

        tabelProduse.getColumns().addAll(colNume, colPret, colCategorie, colVegetarian);

        tabelProduse.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, nou) -> {
                    if (nou != null) {
                        afiseazaDetaliiProdus(nou);
                    }
                }
        );

        listaProduse = FXCollections.observableArrayList();
        tabelProduse.setItems(listaProduse);

        return tabelProduse;
    }

    private VBox creazaPanouDetalii() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setPrefWidth(350);
        box.setStyle("-fx-background-color: #fafafa; -fx-border-color: #ddd;");

        Label lblTitlu = new Label("Detalii Produs");
        lblTitlu.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        lblNumeDetalii = new Label("Selectează un produs...");
        lblNumeDetalii.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        lblNumeDetalii.setWrapText(true);

        lblPretDetalii = new Label();
        lblPretDetalii.setStyle("-fx-font-size: 20px; -fx-text-fill: #4CAF50;");

        lblCategorieDetalii = new Label();
        lblCantitateDetalii = new Label();
        lblVegetarianDetalii = new Label();

        box.getChildren().addAll(
                lblTitlu,
                new Separator(),
                lblNumeDetalii,
                lblPretDetalii,
                lblCategorieDetalii,
                lblCantitateDetalii,
                lblVegetarianDetalii
        );

        return box;
    }

    private void incarcaProduse() {
        toateProdusele = produsRepository.gasesteToate();
        listaProduse.setAll(toateProdusele);
    }

    private void aplicaFiltre() {
        List<Produs> produseFiltrate = toateProdusele.stream()
                .filter(p -> {
                    if (txtCautare.getText().isEmpty()) return true;
                    return p.getNume().toLowerCase().contains(txtCautare.getText().toLowerCase());
                })

                .filter(p -> !chkVegetarian.isSelected() || p.getEsteVegetarian())

                .filter(p -> {
                    String tip = cmbTip.getValue();
                    if (tip.equals("Toate")) return true;
                    return p.getClass().getSimpleName().equalsIgnoreCase(tip);
                })

                .filter(p -> {
                    if (txtPretMin.getText().isEmpty()) return true;
                    try {
                        float min = Float.parseFloat(txtPretMin.getText());
                        return p.getPret() >= min;
                    } catch (NumberFormatException e) {
                        return true;
                    }
                })

                .filter(p -> {
                    if (txtPretMax.getText().isEmpty()) return true;
                    try {
                        float max = Float.parseFloat(txtPretMax.getText());
                        return p.getPret() <= max;
                    } catch (NumberFormatException e) {
                        return true;
                    }
                })
                .collect(Collectors.toList());

        listaProduse.setAll(produseFiltrate);
    }

    private void reseteazaFiltre() {
        txtCautare.clear();
        chkVegetarian.setSelected(false);
        cmbTip.setValue("Toate");
        txtPretMin.clear();
        txtPretMax.clear();
        listaProduse.setAll(toateProdusele);
    }

    private void afiseazaDetaliiProdus(Produs p) {
        lblNumeDetalii.setText(p.getNume());
        lblPretDetalii.setText(String.format("%.2f RON", p.getPret()));
        lblCategorieDetalii.setText("Categorie: " + p.getCategorie().name());
        lblCantitateDetalii.setText(p.getCantitate());
        lblVegetarianDetalii.setText(p.getEsteVegetarian() ? "✓ Vegetarian" : "");
        lblVegetarianDetalii.setStyle(p.getEsteVegetarian() ? "-fx-text-fill: green;" : "");
    }

    @Override
    public void stop() {
        if (produsRepository != null) {
            produsRepository.close();
        }
    }
}