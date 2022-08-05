package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilties.SoothingElixir;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class SoothingElixirBranch extends AbstractUpgradeBranch<SoothingElixir> {

    public SoothingElixirBranch(AbilityTree abilityTree, SoothingElixir ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Healing - Tier I", "+15% Healing", 5000));
        treeA.add(new Upgrade("Healing - Tier II", "+30% Healing", 10000));
        treeA.add(new Upgrade("Healing - Tier III", "+60% Healing", 20000));

        treeB.add(new Upgrade("Energy - Tier I", "-5 Energy cost", 5000));
        treeB.add(new Upgrade("Energy - Tier II", "-10 Energy cost", 10000));
        treeB.add(new Upgrade("Energy - Tier III", "-15 Energy cost", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-5% Cooldown Reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-10% Cooldown Reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-20% Cooldown Reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "PLACEHOLDER: Double the duration of Soothing Elixir",
                50000
        );
    }

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();

    float puddleRadius = ability.getPuddleRadius();

    float cooldown = ability.getCooldown();

}
