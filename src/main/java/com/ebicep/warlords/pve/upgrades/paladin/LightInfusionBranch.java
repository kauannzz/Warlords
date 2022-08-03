package com.ebicep.warlords.pve.upgrades.paladin;

import com.ebicep.warlords.abilties.LightInfusion;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class LightInfusionBranch extends AbstractUpgradeBranch<LightInfusion> {

    public LightInfusionBranch(AbilityTree abilityTree, LightInfusion ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Speed - Tier I", "+10% Speed", 5000));
        treeA.add(new Upgrade("Speed - Tier II", "+20% Speed", 10000));
        treeA.add(new Upgrade("Speed - Tier III", "+30% Speed", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "+50% Energy given\n+100% Duration\n\nReduce all knockback by 20% while Light Infusion is active.",
                50000
        );
    }

    int speedBuff = ability.getSpeedBuff();

    float cooldown = ability.getCooldown();

}
