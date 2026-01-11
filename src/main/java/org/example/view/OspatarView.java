package org.example.view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.model.*;
import org.example.repository.*;
import org.example.service.OfertaService;

import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class OspatarView extends Application {
    private User user;
    private EntityManagerFactory emf;
    private ProdusRepository produsRepository;
    private MasaRepository masaRepository;
    private ComandaRepository comandaRepository;
    private OfertaService ofertaService;

    private ListView<Masa> listaMese;
    private TableView<Produs> tabelProduse;
    private ObservableList<String> itemsCos;
    private Masa masaSelectata;
    private Comanda comandaCurenta;
    private Label lblMasaInfo;
    private Label lblTotal;

    public OspatarView(User user, EntityManagerFactory emf) {
        this.user = user;
        this.emf = emf;
        this.produsRepository = new ProdusRepository(emf);
        this.masaRepository = new MasaRepository(emf);
        this.comandaRepository = new ComandaRepository(emf);
        this.ofertaService = OfertaService.getInstance(this.emf);
        this.itemsCos = FXCollections.observableArrayList();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Ospătar - " + user.getNume());
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        root.setLeft(creazaPanouMese());
        root.setCenter(creazaPanouProduse());
        root.setRight(creazaPanouCos());

        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.show();
        incarcaMese();
    }

    private VBox creazaPanouMese() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setPrefWidth(250);
        box.setStyle("-fx-background-color: #f4f4f4;");

        Label lblTitlu = new Label("Săli de Mese");
        lblTitlu.setStyle("-fx-font-weight: bold;");

        listaMese = new ListView<>();
        listaMese.getSelectionModel().selectedItemProperty().addListener((obs, old, nou) -> {
            if (nou != null) selecteazaMasa(nou);
        });

        box.getChildren().addAll(lblTitlu, listaMese);
        return box;
    }

    private VBox creazaPanouProduse() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        lblMasaInfo = new Label("Selectați o masă");

        tabelProduse = new TableView<>();
        tabelProduse.setDisable(true);
        TableColumn<Produs, String> colNume = new TableColumn<>("Produs");
        colNume.setCellValueFactory(new PropertyValueFactory<>("nume"));
        TableColumn<Produs, Float> colPret = new TableColumn<>("Preț");
        colPret.setCellValueFactory(new PropertyValueFactory<>("pret"));

        tabelProduse.getColumns().addAll(colNume, colPret);
        Button btnAdauga = new Button("Adaugă în Coș");
        btnAdauga.setOnAction(e -> adaugaInCos());

        box.getChildren().addAll(lblMasaInfo, tabelProduse, btnAdauga);
        return box;
    }

    private VBox creazaPanouCos() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setPrefWidth(350);

        ListView<String> lvCos = new ListView<>(itemsCos);
        lblTotal = new Label("Total: 0.00 RON");
        lblTotal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button btnFinalizeaza = new Button("Finalizează Comandă");
        btnFinalizeaza.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnFinalizeaza.setOnAction(e -> finalizeazaComanda());

        Button btnIstoric = new Button("Vezi Istoric Personal");
        btnIstoric.setOnAction(e -> veziIstoric());

        box.getChildren().addAll(new Label("Comanda Curentă:"), lvCos, lblTotal, btnFinalizeaza, new Separator(), btnIstoric);
        return box;
    }

    private void incarcaMese() {
        listaMese.getItems().setAll(masaRepository.gasesteToate());
    }

    private void selecteazaMasa(Masa masa) {
        this.masaSelectata = masa;
        this.comandaCurenta = new Comanda(user, masa);
        lblMasaInfo.setText("Comandă: Masa " + masa.getNumar());
        tabelProduse.setDisable(false);
        tabelProduse.getItems().setAll(produsRepository.gasesteToate());
        itemsCos.clear();
    }

    private void adaugaInCos() {
        Produs p = tabelProduse.getSelectionModel().getSelectedItem();
        if (p != null && comandaCurenta != null) {
            ComandaItem item = new ComandaItem(p, 1);
            comandaCurenta.adaugaItem(item);
            itemsCos.add(p.getNume() + " - " + p.getPret() + " RON");
            calculeazaTotal();
        }
    }

    private void calculeazaTotal() {
        float total = 0;
        for (ComandaItem item : comandaCurenta.getItems()) total += item.getPretTotal();
        lblTotal.setText(String.format("Total: %.2f RON (TVA inclus)", total * 1.09f));
    }

    private void finalizeazaComanda() {
        if (comandaCurenta == null || itemsCos.isEmpty()) return;

        ProgressIndicator pi = new ProgressIndicator();
        pi.setMaxSize(20, 20);


        Task<Void> taskSalvare = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<ComandaItem> reduceri = ofertaService.aplicaOferte(comandaCurenta.getItems());
                for (ComandaItem r : reduceri) {
                    r.setComanda(comandaCurenta);
                    comandaCurenta.adaugaItem(r);
                }

                comandaCurenta.calculeazaTotal(0.09f);
                comandaRepository.salveaza(comandaCurenta);

                masaSelectata.setStatus(StatusMasa.LIBERA);
                masaRepository.actualizeaza(masaSelectata);
                return null;
            }
        };

        taskSalvare.setOnSucceeded(e -> {
            new Alert(Alert.AlertType.INFORMATION, "Comandă finalizată!").show();
            itemsCos.clear();
            tabelProduse.setDisable(true);
            incarcaMese();
        });

        taskSalvare.setOnFailed(e -> {
            new Alert(Alert.AlertType.ERROR, "Eroare la salvare: " + taskSalvare.getException().getMessage()).show();
        });

        new Thread(taskSalvare).start();
    }

    private void veziIstoric() {
        Stage stage = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        ProgressIndicator loading = new ProgressIndicator();
        ListView<Comanda> lvIstoric = new ListView<>();
        lvIstoric.setVisible(false);

        root.getChildren().addAll(new Label("Istoricul tău:"), loading, lvIstoric);

        Task<List<Comanda>> taskIncarcare = new Task<>() {
            @Override
            protected List<Comanda> call() throws Exception {
                Thread.sleep(500); // Simulăm lucrul cu DB (Iteratia 8) [cite: 295]
                return comandaRepository.gasesteDupaOspatar(user);
            }
        };

        taskIncarcare.setOnSucceeded(e -> {
            loading.setVisible(false);
            loading.setManaged(false);
            lvIstoric.getItems().setAll(taskIncarcare.getValue());
            lvIstoric.setVisible(true);
        });

        new Thread(taskIncarcare).start();

        lvIstoric.getSelectionModel().selectedItemProperty().addListener((obs, old, nou) -> {
            if (nou != null) afiseazaBon(nou);
        });

        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }

    private void afiseazaBon(Comanda c) {
        StringBuilder sb = new StringBuilder();
        sb.append("      DETALII COMANDĂ #").append(c.getId()).append("\n");
        sb.append("--------------------------------\n");
        sb.append("Ospătar: ").append(c.getOspatar().getNume()).append("\n");
        sb.append("Masa: ").append(c.getMasa().getNumar()).append("\n");
        sb.append("--------------------------------\n");

        for (ComandaItem item : c.getItems()) {
            String nume = (item.getProdus() != null) ? item.getProdus().getNume() : "REDUCERE: " + item.getDescriereReducere();
            sb.append(String.format("%-20s %8.2f\n", nume, item.getPretTotal()));
        }

        sb.append("--------------------------------\n");
        sb.append(String.format("TOTAL FINAL (TVA 9%%): %.2f RON\n", c.getTotalCuTVA()));

        TextArea txtBon = new TextArea(sb.toString());
        txtBon.setEditable(false);
        txtBon.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13;");

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Vizualizare Bon - Manager");
        alert.getDialogPane().setContent(txtBon);
        alert.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        alert.showAndWait();
    }

    @Override
    public void stop() {
        produsRepository.close(); masaRepository.close(); comandaRepository.close();
    }
}