package com.ebicep.warlords.pve.upgrades.rogue.vindicator;

import com.ebicep.warlords.abilties.RighteousStrike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class RighteousStrikeBranch extends AbstractUpgradeBranch<RighteousStrike> {

    public RighteousStrikeBranch(AbilityTree abilityTree, RighteousStrike ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage - Tier I", "+10% Damage", 5000));
        treeA.add(new Upgrade("Damage - Tier II", "+20% Damage", 10000));
        treeA.add(new Upgrade("Damage - Tier III", "+40% Damage", 20000));

        treeB.add(new Upgrade("Energy - Tier I", "-5 Energy cost", 5000));
        treeB.add(new Upgrade("Energy - Tier II", "-10 Energy cost", 10000));
        treeB.add(new Upgrade("Energy - Tier III", "-15 Energy cost", 20000));

        treeC.add(new Upgrade("Utility - Tier I", "+0.5s Active ability timer reduction", 5000));
        treeC.add(new Upgrade("Utility - Tier II", "+1s Active ability timer reduction", 10000));
        treeC.add(new Upgrade("Utility - Tier III", "+1.5s Active ability timer reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "50% of Righteous Strike's damage is now dealt as true\ndamage. Enemies hit by Righteous Strike will be knock\nvery slightly backwards.",
                50000
        );
    }

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    float energyCost = ability.getEnergyCost();

    int abilityReductionInTicks = ability.getAbilityReductionInTicks();

}
