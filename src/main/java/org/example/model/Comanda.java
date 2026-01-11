package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comenzi")
public class Comanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ospatar_id", nullable = false)
    private User ospatar;

    @ManyToOne
    @JoinColumn(name = "masa_id", nullable = false)
    private Masa masa;

    @Column(nullable = false)
    private LocalDateTime dataOra;

    @Column(nullable = false)
    private float total;

    @Column(nullable = false)
    private float totalCuTVA;

    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComandaItem> items = new ArrayList<>();

    protected Comanda() {
    }

    public Comanda(User ospatar, Masa masa) {
        this.ospatar = ospatar;
        this.masa = masa;
        this.dataOra = LocalDateTime.now();
        this.total = 0;
        this.totalCuTVA = 0;
    }


    public void adaugaItem(ComandaItem item) {
        items.add(item);
        item.setComanda(this);
    }

    public void stergeItem(ComandaItem item) {
        items.remove(item);
        item.setComanda(null);
    }

    public void calculeazaTotal(float cotaTVA) {
        this.total = 0;
        for (ComandaItem item : items) {
            this.total += item.getPretTotal();
        }
        this.totalCuTVA = this.total * (1 + cotaTVA);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOspatar() {
        return ospatar;
    }

    public void setOspatar(User ospatar) {
        this.ospatar = ospatar;
    }

    public Masa getMasa() {
        return masa;
    }

    public void setMasa(Masa masa) {
        this.masa = masa;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }

    public void setDataOra(LocalDateTime dataOra) {
        this.dataOra = dataOra;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getTotalCuTVA() {
        return totalCuTVA;
    }

    public void setTotalCuTVA(float totalCuTVA) {
        this.totalCuTVA = totalCuTVA;
    }

    public List<ComandaItem> getItems() {
        return items;
    }

    public void setItems(List<ComandaItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Comanda #" + id + " - Masa " + masa.getNumar() + " - " + dataOra.toLocalDate();
    }
}