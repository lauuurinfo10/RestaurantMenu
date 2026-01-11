package org.example.repository;

import jakarta.persistence.*;
import org.example.model.Masa;
import org.example.model.StatusMasa;

import java.util.List;

public class MasaRepository {
    private EntityManagerFactory emf;
    private EntityManager em;

    public MasaRepository(EntityManagerFactory emf) {
        this.emf = emf;
        this.em = emf.createEntityManager();
    }

    public void salveaza(Masa masa) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(masa);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Eroare salvare masa: " + e.getMessage(), e);
        }
    }

    public List<Masa> gasesteToate() {
        TypedQuery<Masa> query = em.createQuery(
                "SELECT m FROM Masa m ORDER BY m.numar",
                Masa.class
        );
        return query.getResultList();
    }

    public List<Masa> gasesteDupaStatus(StatusMasa status) {
        TypedQuery<Masa> query = em.createQuery(
                "SELECT m FROM Masa m WHERE m.status = :status ORDER BY m.numar",
                Masa.class
        );
        query.setParameter("status", status);
        return query.getResultList();
    }

    public Masa gasesteDupaNumar(int numar) {
        TypedQuery<Masa> query = em.createQuery(
                "SELECT m FROM Masa m WHERE m.numar = :numar",
                Masa.class
        );
        query.setParameter("numar", numar);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void actualizeaza(Masa masa) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(masa);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Eroare actualizare masa: " + e.getMessage(), e);
        }
    }

    public void sterge(Masa masa) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Masa masaManaged = em.contains(masa) ? masa : em.merge(masa);
            em.remove(masaManaged);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Eroare stergere masa: " + e.getMessage(), e);
        }
    }

    public void close() {
        if (em != null && em.isOpen()) em.close();
    }
}