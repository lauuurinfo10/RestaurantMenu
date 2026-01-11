package org.example.view;

import com.google.gson.*;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.model.*;
import org.example.repository.*;
import org.example.service.OfertaService;

import jakarta.persistence.EntityManagerFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ManagerView extends Application {
    private User user;
    private EntityManagerFactory emf;
    private UserRepository userRepository;
    private ProdusRepository produsRepository;
    private ComandaRepository comandaRepository;

    private TabPane tabPane;

    private ListView<Comanda> listaIstoricGlobal = new ListView<>();
    private ProgressIndicator loadingManager = new ProgressIndicator();
    private Label lblStatusManager = new Label("Istoric Global Comenzi");

    public ManagerView(User user, EntityManagerFactory emf) {
        this.user = user;
        this.emf = emf;
        this.userRepository = new UserRepository(emf);
        this.produsRepository = new ProdusRepository(emf);
        this.comandaRepository = new ComandaRepository(emf);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Manager - " + user.getNume());

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        Label lblWelcome = new Label("Bun venit, " + user.getNume() + " (Manager)");
        lblWelcome.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        root.setTop(lblWelcome);

        tabPane = new TabPane();

        Tab tabPersonal = new Tab("Personal", creazaTabPersonal());
        Tab tabMeniu = new Tab("Meniu", creazaTabMeniu());
        Tab tabOferte = new Tab("Oferte", creazaTabOferte());
        Tab tabIstoric = new Tab("Istoric", creazaTabIstoric());

        tabPersonal.setClosable(false);
        tabMeniu.setClosable(false);
        tabOferte.setClosable(false);
        tabIstoric.setClosable(false);

        tabPane.getTabs().addAll(tabPersonal, tabMeniu, tabOferte, tabIstoric);

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null && "Istoric".equals(newTab.getText())) {
                incarcaIstoricManagerAsincron();
            }
        });

        root.setCenter(tabPane);
        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);
        stage.show();
    }

    private VBox creazaTabIstoric() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);

        loadingManager.setVisible(false);
        loadingManager.setManaged(false);
        listaIstoricGlobal.setPrefHeight(500);
        listaIstoricGlobal.getSelectionModel().selectedItemProperty().addListener((obs, vechi, nou) -> {
            if (nou != null) {
                afiseazaBonFiscal(nou);
            }
        });

        box.getChildren().addAll(lblStatusManager, loadingManager, listaIstoricGlobal);
        return box;
    }

    private void incarcaIstoricManagerAsincron() {
        listaIstoricGlobal.setVisible(false);
        loadingManager.setVisible(true);
        loadingManager.setManaged(true);
        lblStatusManager.setText("Se descarcă datele din baza de date...");

        Task<List<Comanda>> task = new Task<>() {
            @Override
            protected List<Comanda> call() throws Exception {
                Thread.sleep(800);
                return comandaRepository.gasesteToate();
            }
        };

        task.setOnSucceeded(e -> {
            listaIstoricGlobal.getItems().setAll(task.getValue());
            loadingManager.setVisible(false);
            loadingManager.setManaged(false);
            listaIstoricGlobal.setVisible(true);
            lblStatusManager.setText("Istoric Global (Click pe comandă pentru detalii)");
        });

        task.setOnFailed(e -> {
            loadingManager.setVisible(false);
            lblStatusManager.setText("Eroare la conectarea cu baza de date!");
        });

        new Thread(task).start();
    }

    private void afiseazaBonFiscal(Comanda c) {
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

    private VBox creazaTabPersonal() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        Label lblTitlu = new Label("Gestiune Personal");
        lblTitlu.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<User> tabelOspatari = new TableView<>();
        TableColumn<User, String> colNume = new TableColumn<>("Nume");
        colNume.setCellValueFactory(new PropertyValueFactory<>("nume"));
        TableColumn<User, String> colUsername = new TableColumn<>("Username");
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));

        tabelOspatari.getColumns().addAll(colNume, colUsername);
        ObservableList<User> listaOspatari = FXCollections.observableArrayList(userRepository.gasesteDupaRol(RolUser.OSPATAR));
        tabelOspatari.setItems(listaOspatari);

        Button btnAdauga = new Button("Adaugă Ospătar");
        btnAdauga.setOnAction(e -> adaugaOspatar(listaOspatari));
        box.getChildren().addAll(lblTitlu, tabelOspatari, btnAdauga);
        return box;
    }

    private VBox creazaTabMeniu() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        Label lblTitlu = new Label("Gestiune Meniu");
        lblTitlu.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<Produs> tabelProduse = new TableView<>();
        TableColumn<Produs, String> colNume = new TableColumn<>("Nume");
        colNume.setCellValueFactory(new PropertyValueFactory<>("nume"));
        TableColumn<Produs, Float> colPret = new TableColumn<>("Preț");
        colPret.setCellValueFactory(new PropertyValueFactory<>("pret"));

        tabelProduse.getColumns().addAll(colNume, colPret);
        tabelProduse.setItems(FXCollections.observableArrayList(produsRepository.gasesteToate()));

        HBox boxGson = new HBox(10);
        Button btnExport = new Button("Export JSON");
        btnExport.setOnAction(e -> exportJSON());
        Button btnImport = new Button("Import JSON");
        btnImport.setOnAction(e -> importJSON());

        boxGson.getChildren().addAll(btnExport, btnImport);
        box.getChildren().addAll(lblTitlu, tabelProduse, new Separator(), new Label("Sincronizare GSON:"), boxGson);
        return box;
    }

    private void exportJSON() {
        FileChooser fc = new FileChooser();
        fc.setInitialFileName("meniu.json");
        File file = fc.showSaveDialog(null);
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(produsRepository.gasesteToate(), writer);
                new Alert(Alert.AlertType.INFORMATION, "Export finalizat!").show();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void importJSON() {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);
        if (file != null) {
            try (FileReader reader = new FileReader(file)) {
                Produs[] produse = new Gson().fromJson(reader, Produs[].class);
                for (Produs p : produse) { p.setId(null); produsRepository.salveaza(p); }
                new Alert(Alert.AlertType.INFORMATION, "Import finalizat!").show();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private VBox creazaTabOferte() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        OfertaService service = OfertaService.getInstance(this.emf);

        CheckBox chkHH = new CheckBox("Happy Hour (Băuturi)"); chkHH.setSelected(service.isHappyHourActiv());
        CheckBox chkMD = new CheckBox("Meal Deal (Pizza+Desert)"); chkMD.setSelected(service.isMealDealActiv());
        CheckBox chkPP = new CheckBox("Party Pack (4x Pizza)"); chkPP.setSelected(service.isPartyPackActiv());

        Button btnSalveaza = new Button("Salvează Strategii");
        btnSalveaza.setOnAction(e -> {
            service.actualizeazaSetari(chkHH.isSelected(), chkMD.isSelected(), chkPP.isSelected());
            new Alert(Alert.AlertType.INFORMATION, "Setări salvate!").show();
        });

        box.getChildren().addAll(new Label("Activare Strategii Oferte:"), chkHH, chkMD, chkPP, btnSalveaza);
        return box;
    }

    private void adaugaOspatar(ObservableList<User> lista) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Nume Ospătar Nou:");
        dialog.showAndWait().ifPresent(nume -> {
            User u = new User(nume.toLowerCase().replace(" ", ""), "parola123", RolUser.OSPATAR, nume);
            userRepository.salveaza(u);
            lista.add(u);
        });
    }

    @Override
    public void stop() {
        userRepository.close(); produsRepository.close(); comandaRepository.close();
    }
}