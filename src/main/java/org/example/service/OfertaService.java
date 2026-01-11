package org.example.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.example.model.*;
import java.util.*;

public class OfertaService {
    private static OfertaService instance;
    private EntityManagerFactory emf;

    private OfertaService() {}

    public static OfertaService getInstance(EntityManagerFactory emf) {
        if (instance == null) instance = new OfertaService();
        instance.emf = emf;
        return instance;
    }

    public boolean isHappyHourActiv() {
        return incarcaSetari().isHappyHourActiv();
    }

    public boolean isMealDealActiv() {
        return incarcaSetari().isMealDealActiv();
    }

    public boolean isPartyPackActiv() {
        return incarcaSetari().isPartyPackActiv();
    }

    public void actualizeazaSetari(boolean hh, boolean md, boolean pp) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            SetariSistem setari = new SetariSistem(hh, md, pp);
            em.merge(setari);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private SetariSistem incarcaSetari() {
        EntityManager em = emf.createEntityManager();
        try {
            SetariSistem s = em.find(SetariSistem.class, "SETARI_OFERTE");
            return (s != null) ? s : new SetariSistem(false, false, false);
        } finally {
            em.close();
        }
    }


    public List<ComandaItem> aplicaOferte(List<ComandaItem> items) {
        List<ComandaItem> reduceri = new ArrayList<>();
        SetariSistem setari = incarcaSetari();

        if (setari.isHappyHourActiv()) {
            ComandaItem reducere = aplicaHappyHour(items);
            if (reducere != null) reduceri.add(reducere);
        }

        if (setari.isMealDealActiv()) {
            ComandaItem reducere = aplicaMealDeal(items);
            if (reducere != null) reduceri.add(reducere);
        }

        if (setari.isPartyPackActiv()) {
            ComandaItem reducere = aplicaPartyPack(items);
            if (reducere != null) reduceri.add(reducere);
        }

        return reduceri;
    }

    private ComandaItem aplicaHappyHour(List<ComandaItem> items) {
        List<Float> preturiBauturi = new ArrayList<>();
        for (ComandaItem item : items) {
            if (item.getProdus() instanceof Bautura) {
                for (int i = 0; i < item.getCantitate(); i++) {
                    preturiBauturi.add(item.getPretUnitar());
                }
            }
        }
        if (preturiBauturi.size() >= 2) {
            preturiBauturi.sort(Collections.reverseOrder());
            float reducere = preturiBauturi.get(1) * 0.5f;
            return new ComandaItem("Happy Hour - A 2-a băutură 50%", reducere);
        }
        return null;
    }

    private ComandaItem aplicaMealDeal(List<ComandaItem> items) {
        boolean arePizza = false;
        Float pretCelMaiIeftinDesert = null;
        for (ComandaItem item : items) {
            Produs p = item.getProdus();
            if (p instanceof Pizza || p.getNume().toLowerCase().contains("pizza")) arePizza = true;
            if (p.getCategorie() == CategorieMeniu.Desert) {
                if (pretCelMaiIeftinDesert == null || p.getPret() < pretCelMaiIeftinDesert) {
                    pretCelMaiIeftinDesert = p.getPret();
                }
            }
        }
        if (arePizza && pretCelMaiIeftinDesert != null) {
            float reducere = pretCelMaiIeftinDesert * 0.25f;
            return new ComandaItem("Meal Deal - Desert 25% reducere", reducere);
        }
        return null;
    }

    private ComandaItem aplicaPartyPack(List<ComandaItem> items) {
        int totalPizze = 0;
        Float pretCeaMaiIeftinaPizza = null;
        for (ComandaItem item : items) {
            Produs p = item.getProdus();
            if (p instanceof Pizza || p.getNume().toLowerCase().contains("pizza")) {
                totalPizze += item.getCantitate();
                if (pretCeaMaiIeftinaPizza == null || p.getPret() < pretCeaMaiIeftinaPizza) {
                    pretCeaMaiIeftinaPizza = p.getPret();
                }
            }
        }
        if (totalPizze >= 4 && pretCeaMaiIeftinaPizza != null) {
            return new ComandaItem("Party Pack - Pizza gratis!", pretCeaMaiIeftinaPizza);
        }
        return null;
    }
}