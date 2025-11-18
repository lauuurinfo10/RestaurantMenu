package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.OptionalDouble;
import java.util.Optional;
 class Meniu {
     private Map<CategorieMeniu, List<Produs>>meniuStructurat;
     public Meniu(){
         this.meniuStructurat = new HashMap<>();
     }

     public void adaugaProdus(Produs produs){
         CategorieMeniu categorie=produs.getCategorie();
         List<Produs> listaProdus=meniuStructurat.get(categorie);
            if(listaProdus==null){
                listaProdus=new ArrayList<>();
                meniuStructurat.put(categorie,listaProdus);
            }
            listaProdus.add(produs);
     }

     public List<Produs> getAllProduse() {
         List<Produs> toateProdusele = new ArrayList<>();
         for (List<Produs> listaCategorie : meniuStructurat.values()) {
             toateProdusele.addAll(listaCategorie);
         }
         return toateProdusele;
     }

     public List<Produs> getProduseDinCategorie(CategorieMeniu categorie) {

         return meniuStructurat.getOrDefault(categorie, Collections.emptyList());
     }

     public List<Produs> getVegetarieneSortateAlfabetic() {
         return getAllProduse().stream()
                 .filter(Produs::getEsteVegetarian)
                 .sorted(Comparator.comparing(Produs::getNume))
                 .collect(Collectors.toList());
     }

     public OptionalDouble getPretMediuDeserturi() {
         double sumaPreturi = 0.0;
         int numarDeserturi = 0;
         for (Produs p : getAllProduse()) {
             if (p.getCategorie() == CategorieMeniu.Desert) {
                 sumaPreturi += p.getPret();
                 numarDeserturi++;
             }
         }
         if (numarDeserturi == 0) {
             return OptionalDouble.empty();
         } else {
             return OptionalDouble.of(sumaPreturi / numarDeserturi);
         }
     }

     public boolean existaPreparatScump() {
         return getAllProduse().stream()
                 .anyMatch(p -> p.getPret() > 100.00);
     }

     public Optional<Produs> cautaProdusDupaNume(String nume) {
         return getAllProduse().stream()

                 .filter(p -> p.getNume().equalsIgnoreCase(nume))
                 .findFirst();
     }



}
