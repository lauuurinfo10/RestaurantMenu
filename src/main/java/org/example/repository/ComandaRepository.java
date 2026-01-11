package org.example.repository;

import jakarta.persistence.*;
import org.example.model.Comanda;
import org.example.model.User;

import java.util.List;

public class ComandaRepository {
    private EntityManagerFactory emf;
    private EntityManager em;

    public ComandaRepository(EntityManagerFactory emf) {
        this.emf = emf;
        this.em = emf.createEntityManager();
    }

    public void salveaza(Comanda comanda) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(comanda);
            tx.commit();
            System.out.println(" Comandă salvată cu succes în DB!");
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println(" EROARE LA SALVARE: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public List<Comanda> gasesteToate() {
        TypedQuery<Comanda> query = em.createQuery(
                "SELECT c FROM Comanda c ORDER BY c.dataOra DESC",
                Comanda.class
        );
        return query.getResultList();
    }

    public List<Comanda> gasesteDupaOspatar(User ospatar) {
        TypedQuery<Comanda> query = em.createQuery(
                "SELECT c FROM Comanda c WHERE c.ospatar = :ospatar ORDER BY c.dataOra DESC",
                Comanda.class
        );
        query.setParameter("ospatar", ospatar);
        return query.getResultList();
    }

    public Comanda gasesteDupaId(Long id) {
        try {
            TypedQuery<Comanda> query = em.createQuery(
                    "SELECT c FROM Comanda c LEFT JOIN FETCH c.items WHERE c.id = :id",
                    Comanda.class
            );
            query.setParameter("id", id);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void actualizeaza(Comanda comanda) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(comanda);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Eroare actualizare comanda: " + e.getMessage(), e);
        }
    }

    public void sterge(Comanda comanda) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Comanda comandaManaged = em.contains(comanda) ? comanda : em.merge(comanda);
            em.remove(comandaManaged);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Eroare stergere comanda: " + e.getMessage(), e);
        }
    }

    public void close() {
        if (em != null && em.isOpen()) em.close();
    }
}
