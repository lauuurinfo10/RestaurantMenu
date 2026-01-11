package org.example.repository;

import jakarta.persistence.*;
import org.example.model.Produs;
import java.util.List;

public class ProdusRepository {
    private EntityManagerFactory emf;
    private EntityManager em;

    public ProdusRepository(EntityManagerFactory emf) {
        this.emf = emf;
        this.em = emf.createEntityManager();
    }

    public void salveaza(Produs produs) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(produs);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void salveazaToate(List<Produs> produse) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            for (Produs p : produse) {
                em.merge(p);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public List<Produs> gasesteToate() {
        em.getEntityManagerFactory().getCache().evictAll();
        return em.createQuery("SELECT p FROM Produs p ORDER BY p.categorie, p.nume", Produs.class)
                .getResultList();
    }

    public void actualizeaza(Produs produs) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(produs);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void sterge(Produs produs) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Produs managed = em.merge(produs);
            em.remove(managed);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void close() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}