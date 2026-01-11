package org.example.model;

import com.google.gson.*;
import org.example.repository.ProdusRepository;

import java.io.*;
import java.util.List;

public class MeniuService {
    private final ProdusRepository repository;
    private final Gson gson;

    public MeniuService(ProdusRepository repository) {
        this.repository = repository;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void exportaInJSON(File fisier) {
        List<Produs> produse = repository.gasesteToate();
        try (FileWriter writer = new FileWriter(fisier)) {
            gson.toJson(produse, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importaDinJSON(File fisier) {
        try (FileReader reader = new FileReader(fisier)) {

            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();

            for (JsonElement element : jsonArray) {
                JsonObject obj = element.getAsJsonObject();
                Produs p = null;

                if (obj.has("gramaj")) {
                    p = gson.fromJson(obj, Mancare.class);
                } else if (obj.has("volum")) {
                    p = gson.fromJson(obj, Bautura.class);
                } else if (obj.has("blat")) {
                    p = gson.fromJson(obj, Pizza.class);
                }

                if (p != null) {
                    p.setId(null);
                    repository.salveaza(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}