package org.example;

import com.google.gson.*;
import java.io.*;
import java.util.List;

public class MeniuService {
    private final ProdusRepository repository;
    private final Gson gson;

    public MeniuService(ProdusRepository repository) {
        this.repository = repository;
        // Folosim setPrettyPrinting ca să arate frumos JSON-ul în fișier
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    // EXPORT: Din Baza de Date -> Fișier JSON
    public void exportaInJSON(File fisier) {
        List<Produs> produse = repository.gasesteToate();
        try (FileWriter writer = new FileWriter(fisier)) {
            gson.toJson(produse, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // IMPORT: Din Fișier JSON -> Baza de Date
    public void importaDinJSON(File fisier) {
        try (FileReader reader = new FileReader(fisier)) {
            // Citim tot conținutul ca pe un tablou de elemente JSON
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();

            for (JsonElement element : jsonArray) {
                JsonObject obj = element.getAsJsonObject();
                Produs p = null;

                // Detectăm tipul după câmpurile specifice
                if (obj.has("gramaj")) {
                    p = gson.fromJson(obj, Mancare.class);
                } else if (obj.has("volum")) {
                    p = gson.fromJson(obj, Bautura.class);
                } else if (obj.has("blat")) {
                    p = gson.fromJson(obj, Pizza.class);
                }

                if (p != null) {
                    // Setăm ID-ul pe null ca să fim siguri că baza de date
                    // generează un ID nou și nu face conflict cu cel vechi
                    p.setId(null);
                    repository.salveaza(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}