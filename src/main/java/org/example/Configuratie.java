package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Configuratie {
    private String numeRestaurant;
    private float cotaTVA;

    public Configuratie() {
    }

    public Configuratie(String numeRestaurant, float cotaTVA) {
        this.numeRestaurant = numeRestaurant;
        this.cotaTVA = cotaTVA;
    }

    public String getNumeRestaurant() {
        return numeRestaurant;
    }

    public float getCotaTVA() {
        return cotaTVA;
    }

    public static Configuratie incarcaDinFisier(String caleFisier) {
        Gson gson = new Gson();

        try {
            String json = new String(Files.readAllBytes(Paths.get(caleFisier)));
            return gson.fromJson(json, Configuratie.class);

        } catch (FileNotFoundException e) {
            System.out.println("Fișier lipsă. Se folosește configurația implicită.");
            return new Configuratie("La Andrei", 0.09f);

        } catch (JsonSyntaxException e) {
            System.out.println("JSON invalid. Se folosește configurația implicită.");
            return new Configuratie("La Andrei", 0.09f);

        } catch (IOException e) {
            System.out.println("Eroare citire fișier. Se folosește configurația implicită.");
            return new Configuratie("La Andrei", 0.09f);
        }
    }

    public void salveazaInFisier(String caleFisier) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(caleFisier)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            System.out.println("Eroare la salvare.");
        }
    }
}
