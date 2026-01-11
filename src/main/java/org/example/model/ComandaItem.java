package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "comanda_items")
public class ComandaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comanda_id", nullable = false)
    private Comanda comanda;

    @ManyToOne
    @JoinColumn(name = "produs_id", nullable = true)
    private Produs produs;

    @Column(nullable = false)
    private int cantitate;

    @Column(nullable = false)
    private float pretUnitar;

    @Column(nullable = false)
    private float pretTotal;

    @Column(nullable = false)
    private boolean esteReducere;

    private String descriereReducere;

    protected ComandaItem() {
    }

    public ComandaItem(Produs produs, int cantitate) {
        this.produs = produs;
        this.cantitate = cantitate;
        this.pretUnitar = produs.getPret();
        this.pretTotal = pretUnitar * cantitate;
        this.esteReducere = false;
    }

    public ComandaItem(String descriereReducere, float reducere) {
        this.cantitate = 1;
        this.pretUnitar = -reducere;
        this.pretTotal = -reducere;
        this.esteReducere = true;
        this.descriereReducere = descriereReducere;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Comanda getComanda() {
        return comanda;
    }

    public void setComanda(Comanda comanda) {
        this.comanda = comanda;
    }

    public Produs getProdus() {
        return produs;
    }

    public void setProdus(Produs produs) {
        this.produs = produs;
    }

    public int getCantitate() {
        return cantitate;
    }

    public void setCantitate(int cantitate) {
        this.cantitate = cantitate;
        if (produs != null) {
            this.pretTotal = this.pretUnitar * cantitate;
        }
    }

    public float getPretUnitar() {
        return pretUnitar;
    }

    public void setPretUnitar(float pretUnitar) {
        this.pretUnitar = pretUnitar;
    }

    public float getPretTotal() {
        return pretTotal;
    }

    public void setPretTotal(float pretTotal) {
        this.pretTotal = pretTotal;
    }

    public boolean isEsteReducere() {
        return esteReducere;
    }

    public void setEsteReducere(boolean esteReducere) {
        this.esteReducere = esteReducere;
    }

    public String getDescriereReducere() {
        return descriereReducere;
    }

    public void setDescriereReducere(String descriereReducere) {
        this.descriereReducere = descriereReducere;
    }

    @Override
    public String toString() {
        if (esteReducere) {
            return descriereReducere + ": " + pretTotal + " RON";
        }
        return cantitate + "x " + produs.getNume() + " - " + pretTotal + " RON";
    }
}
