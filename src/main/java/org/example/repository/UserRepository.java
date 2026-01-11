package org.example.repository;

import jakarta.persistence.*;
import org.example.model.RolUser;
import org.example.model.User;

import java.util.List;
import java.util.Optional;

public class UserRepository {
    private EntityManagerFactory emf;
    private EntityManager em;

    public UserRepository(EntityManagerFactory emf) {
        this.emf = emf;
        this.em = emf.createEntityManager();
    }

    public void salveaza(User user) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(user);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Eroare salvare user: " + e.getMessage(), e);
        }
    }

    public Optional<User> gasesteDupaUsername(String username) {
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username",
                    User.class
            );
            query.setParameter("username", username);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<User> gasesteDupaRol(RolUser rol) {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.rol = :rol ORDER BY u.nume",
                User.class
        );
        query.setParameter("rol", rol);
        return query.getResultList();
    }

    public List<User> gasesteToate() {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u ORDER BY u.nume",
                User.class
        );
        return query.getResultList();
    }

    public void actualizeaza(User user) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(user);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Eroare actualizare user: " + e.getMessage(), e);
        }
    }

    public void sterge(User user) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            User userManaged = em.contains(user) ? user : em.merge(user);
            em.remove(userManaged);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Eroare stergere user: " + e.getMessage(), e);
        }
    }

    public void close() {
        if (em != null && em.isOpen()) em.close();
    }
}
