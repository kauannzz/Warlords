package com.ebicep.warlords.classes.shaman.specs.earthwarden;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.shaman.AbstractShaman;
import org.bukkit.entity.Player;

public class Earthwarden extends AbstractShaman {

    public Earthwarden(Player player) {
        super(player, 5530, 305, 10,
                new EarthenSpike(),
                new Boulder(),
                new Earthliving(),
                new Chain("Chain Heal", 474, 633, 8, 40, 20, 175,
                        "§7Discharge a beam of energizing lightning\n" +
                                "§7that heals you and a targeted friendly\n" +
                                "§7player for §a474 §7- §a633 §7health and\n" +
                                "§7jumps to §e2 §7additional targets within\n" +
                                "§e10 §7blocks." +
                                "\n\n" +
                                "§7Each ally healed reduces the cooldown of\n" +
                                "§7Boulder by §62 §7seconds."),

                new Totem.TotemEarthwarden());
    }

}