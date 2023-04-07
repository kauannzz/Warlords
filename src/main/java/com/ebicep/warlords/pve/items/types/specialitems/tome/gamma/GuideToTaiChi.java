package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Classes;

public class GuideToTaiChi extends SpecialGammaTome implements CDRandCritChance {

    @Override
    public String getName() {
        return "Guide to Tai Chi";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Crit Chance.";
    }

    @Override
    public String getDescription() {
        return "Center yourself.";
    }

    @Override
    public Classes getClasses() {
        return Classes.WARRIOR;
    }

}
