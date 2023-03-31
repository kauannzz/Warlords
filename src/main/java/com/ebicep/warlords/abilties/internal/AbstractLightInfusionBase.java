package com.ebicep.warlords.abilties.internal;

import com.ebicep.warlords.util.java.Pair;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLightInfusionBase extends AbstractAbility implements Duration {

    protected int tickDuration = 60;
    protected int speedBuff = 40;
    protected int energyGiven = 120;

    public AbstractLightInfusionBase(float cooldown) {
        super("Light Infusion", 0, 0, cooldown, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = "You become infused with light, restoring §a" + energyGiven +
                " §7energy and increasing your movement speed by §e" + speedBuff +
                "% §7for §6" + format(tickDuration / 20f) + " §7seconds";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));

        return info;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getSpeedBuff() {
        return speedBuff;
    }

    public void setSpeedBuff(int speedBuff) {
        this.speedBuff = speedBuff;
    }

    public int getEnergyGiven() {
        return energyGiven;
    }

    public void setEnergyGiven(int energyGiven) {
        this.energyGiven = energyGiven;
    }


}
