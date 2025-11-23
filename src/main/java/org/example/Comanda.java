package org.example;

import java.util.ArrayList;
import java.util.List;


public class Comanda {
    private  float cotaTVA =0.09f;
    private List<ProdusComandat>produseComandate;

    public Comanda(float cotaTVA) {
        this.cotaTVA = cotaTVA;
        this.produseComandate=new ArrayList<>();
    }
    public void adaugaProdus(Produs p,int cantitate) {
        this.produseComandate.add(new ProdusComandat(p, cantitate));
    }
    public float calculeazaSubTotal(){
        float total=0.0f;
        for(ProdusComandat p:produseComandate){
            total+=p.getPret();
        }
        return total;
    }
    public float calculeazaPretCuTva(IstrategieReducere strategieReducere){
        float subtotal=calculeazaSubTotal() ;
        float reducere=0;
        if(strategieReducere!=null) {
            reducere = strategieReducere.aplicaReducere(this);
        }
        subtotal-=reducere;
        float tva=subtotal* cotaTVA;
        return subtotal+tva;
    }
    public List<ProdusComandat> getProduseComandate(){
        return produseComandate;
    }

    public void afiseazaDetalii() {
        System.out.println("\n=== Comandă ===");
        for (ProdusComandat pc : produseComandate) {
            System.out.println(pc.getCantitate() + "x " + pc.getProdus().getNume());
        }
        System.out.println("Total: " + calculeazaPretCuTva(null) + " RON\n");
    }



}
