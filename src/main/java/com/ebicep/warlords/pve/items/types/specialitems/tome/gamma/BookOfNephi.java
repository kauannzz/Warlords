package com.ebicep.warlords.pve.items.types.specialitems.tome.gamma;

import com.ebicep.warlords.player.general.Classes;

public class BookOfNephi extends SpecialGammaTome implements CDRandHealing {

    @Override
    public String getName() {
        return "Book of Nephi";
    }

    @Override
    public String getBonus() {
        return "+5% Cooldown Reduction but -20% Healing.";
    }

    @Override
    public String getDescription() {
        return "A tale of peace.";
    }

    @Override
    public Classes getClasses() {
        return Classes.PALADIN;
    }


}
