package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mese")
public class Masa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private int numar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMasa status;

    protected Masa() {
    }

    public Masa(int numar) {
        this.numar = numar;
        this.status = StatusMasa.LIBERA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumar() {
        return numar;
    }

    public void setNumar(int numar) {
        this.numar = numar;
    }

    public StatusMasa getStatus() {
        return status;
    }

    public void setStatus(StatusMasa status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Masa " + numar + " (" + status + ")";
    }
}
