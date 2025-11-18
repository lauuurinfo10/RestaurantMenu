package org.example;

import java.util.ArrayList;
import java.util.List;


public class Comanda {
    private static final float TVA=0.09f;
    private List<ProdusComandat>produseComandate;
    public Comanda(){
        this.produseComandate=new ArrayList<>();
    }
    public void adaugaProdus(Produs p,int cantitate) {
        this.produseComandate.add(new ProdusComandat(p, cantitate));
    }
    public float calculeazasubTotal(){
        float total=0.0f;
        for(ProdusComandat p:produseComandate){
            total+=p.getPret();
        }
        return total;
    }
    public float calculeazaPretcuTva(IstrategieReducere strategieReducere){
        float subtotal=calculeazasubTotal() ;
        float reducere=0;
        if(strategieReducere!=null) {
            reducere = strategieReducere.aplicaReducere(this);
        }
        subtotal-=reducere;
        float tva=subtotal*TVA;
        return subtotal+tva;
    }
    public List<ProdusComandat> getProduseComandate(){
        return produseComandate;
    }
}
