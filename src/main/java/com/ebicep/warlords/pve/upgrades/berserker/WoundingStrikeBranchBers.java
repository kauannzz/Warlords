package com.ebicep.warlords.pve.upgrades.berserker;

import com.ebicep.warlords.abilties.WoundingStrikeBerserker;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class WoundingStrikeBranchBers extends AbstractUpgradeBranch<WoundingStrikeBerserker> {

    public WoundingStrikeBranchBers(AbilityTree abilityTree, WoundingStrikeBerserker ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage - Tier I", "+10% Damage", 5000));
        treeA.add(new Upgrade("Damage - Tier II", "+25% Damage", 10000));
        treeA.add(new Upgrade("Damage - Tier III", "+50% Damage", 20000));

        treeB.add(new Upgrade("Energy - Tier I", "-5 Energy cost", 5000));
        treeB.add(new Upgrade("Energy - Tier II", "-10 Energy cost", 10000));
        treeB.add(new Upgrade("Energy - Tier III", "-15 Energy cost", 20000));

        treeC.add(new Upgrade("Wounding - Tier I", "+1s Wounding Duration", 5000));
        treeC.add(new Upgrade("Wounding - Tier II", "+2s Wounding Duration", 10000));
        treeC.add(new Upgrade("Wounding - Tier III", "+3s Wounding Duration", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Wounding Strike now applies BLEED instead of wounding.\n\nBLEED: Enemies afflicted take 50% more damage\nfrom Wounding Strike while Blood Lust is active.\nBleeding enemies have healing reduced by 70%\nand lose 1% of their max health per second.",
                50000
        );
    }

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float energyCost = ability.getEnergyCost();

    int woundDuration = ability.getWoundingDuration();

}
