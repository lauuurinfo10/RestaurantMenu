package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SetariSistem {
    @Id
    private String id = "SETARI_OFERTE";

    private boolean happyHourActiv;
    private boolean mealDealActiv;
    private boolean partyPackActiv;

    public SetariSistem() {}

    public SetariSistem(boolean hh, boolean md, boolean pp) {
        this.happyHourActiv = hh;
        this.mealDealActiv = md;
        this.partyPackActiv = pp;
    }

    public boolean isHappyHourActiv() { return happyHourActiv; }
    public boolean isMealDealActiv() { return mealDealActiv; }
    public boolean isPartyPackActiv() { return partyPackActiv; }
}
