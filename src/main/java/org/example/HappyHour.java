package org.example;

public class HappyHour implements IstrategieReducere{
    private static final float REDUCERE=0.20f;
    @Override
    public float aplicaReducere(Comanda comanda){
        float reducereTotala=0.0f;
        for(ProdusComandat p:comanda.getProduseComandate()){
            if(p.getProdus() instanceof Bautura) {
                float pretBaza=p.getPret();
                reducereTotala += pretBaza * REDUCERE;
            }
        }
        return reducereTotala;
    }
}
