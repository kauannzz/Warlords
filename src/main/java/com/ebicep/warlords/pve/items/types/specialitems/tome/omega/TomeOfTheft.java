package com.ebicep.warlords.pve.items.types.specialitems.tome.omega;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class TomeOfTheft extends SpecialOmegaTome implements AppliesToWarlordsPlayer {
    public TomeOfTheft(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public TomeOfTheft() {

    }

    @Override
    public String getName() {
        return "Tome of Theft";
    }

    @Override
    public String getBonus() {
        return "5% of all attacks are dodged.";
    }

    @Override
    public String getDescription() {
        return "Finally! A purchase worth my while.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!event.getWarlordsEntity().equals(warlordsPlayer)) {
                    return;
                }
                if (event.isHealingInstance()) {
                    return;
                }
                if (ThreadLocalRandom.current().nextDouble() < .05) {
                    //TODO dodge message
                    event.setCancelled(true);
                }
            }
        });
    }

}
