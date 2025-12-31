package org.example;

import jakarta.persistence.*;
import java.util.List;

public class ProdusRepository {
    private EntityManagerFactory emf;
    private EntityManager em;
    public ProdusRepository() {
        this.emf = Persistence.createEntityManagerFactory("RestaurantPU");
        this.em = emf.createEntityManager();
    }

    public void salveaza(Produs produs) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(produs);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Eroare la salvare: " + e.getMessage(), e);
        }
    }

    public void salveazaToate(List<Produs> produse) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            for (Produs p : produse) {
                em.persist(p);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Eroare la salvare multipla: " + e.getMessage(), e);
        }
    }

    public List<Produs> gasesteToate() {
        try {
            TypedQuery<Produs> query = em.createQuery(
                    "SELECT p FROM Produs p ORDER BY p.categorie, p.nume",
                    Produs.class
            );
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Eroare la citire: " + e.getMessage(), e);
        }
    }

    public Produs gasesteDupaId(Long id) {
        return em.find(Produs.class, id);
    }

    public void actualizeaza(Produs produs) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(produs);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Eroare la actualizare: " + e.getMessage(), e);
        }
    }

    public void sterge(Produs produs) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Produs produsManaged = em.contains(produs) ? produs : em.merge(produs);
            em.remove(produsManaged);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Eroare la stergere: " + e.getMessage(), e);
        }
    }


    public void stergeTot() {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery("DELETE FROM Produs").executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Eroare la stergere totala: " + e.getMessage(), e);
        }
    }

    public void close() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
