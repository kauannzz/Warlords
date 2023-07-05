package com.ebicep.warlords.pve.upgrades.arcanist.cleric;

import com.ebicep.warlords.abilities.MercifulHex;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class MercifulHexBranch extends AbstractUpgradeBranch<MercifulHex> {

    float minDamageHeal = ability.getMinDamageHeal();
    float maxDamageHeal = ability.getMaxDamageHeal();
    float minSelfHeal = ability.getMinSelfHeal();
    float maxSelfHeal = ability.getMaxSelfHeal();
    float dotMinHeal = ability.getDotMinHeal();
    float dotMaxHeal = ability.getDotMaxHeal();
    float energyCost = ability.getEnergyCost();
    float minDamage = ability.getMinDamage();
    float maxDamage = ability.getMaxDamage();

    public MercifulHexBranch(AbilityTree abilityTree, MercifulHex ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Alleviate - Tier I",
                "+15% Healing",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamageHeal * 1.15f);
                    ability.setMaxDamageHeal(maxDamageHeal * 1.15f);
                    ability.setMinSelfHeal(minSelfHeal * 1.15f);
                    ability.setMaxSelfHeal(maxSelfHeal * 1.15f);
                    ability.setDotMinHeal(dotMinHeal * 1.15f);
                    ability.setDotMaxHeal(dotMaxHeal * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier II",
                "+30% Healing",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamageHeal * 1.3f);
                    ability.setMaxDamageHeal(maxDamageHeal * 1.3f);
                    ability.setMinSelfHeal(minSelfHeal * 1.3f);
                    ability.setMaxSelfHeal(maxSelfHeal * 1.3f);
                    ability.setDotMinHeal(dotMinHeal * 1.3f);
                    ability.setDotMaxHeal(dotMaxHeal * 1.3f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier III",
                "+45% Healing",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamageHeal * 1.45f);
                    ability.setMaxDamageHeal(maxDamageHeal * 1.45f);
                    ability.setMinSelfHeal(minSelfHeal * 1.45f);
                    ability.setMaxSelfHeal(maxSelfHeal * 1.45f);
                    ability.setDotMinHeal(dotMinHeal * 1.45f);
                    ability.setDotMaxHeal(dotMaxHeal * 1.45f);
                }
        ));
        treeA.add(new Upgrade(
                "Alleviate - Tier IV",
                "+60% Healing",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamageHeal * 1.6f);
                    ability.setMaxDamageHeal(maxDamageHeal * 1.6f);
                    ability.setMinSelfHeal(minSelfHeal * 1.6f);
                    ability.setMaxSelfHeal(maxSelfHeal * 1.6f);
                    ability.setDotMinHeal(dotMinHeal * 1.6f);
                    ability.setDotMaxHeal(dotMaxHeal * 1.6f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-2.5 Energy cost",
                5000,
                () -> {
                    ability.setEnergyCost(energyCost - 2.5f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-5 Energy cost",
                10000,
                () -> {
                    ability.setEnergyCost(energyCost - 5);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-7.5 Energy cost\n+15% Damage",
                15000,
                () -> {
                    ability.setEnergyCost(energyCost - 7.5f);
                    ability.setMinDamage(minDamage * 1.15f);
                    ability.setMaxDamage(maxDamage * 1.15f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-10 Energy cost\n+30% Damage",
                20000,
                () -> {
                    ability.setEnergyCost(energyCost - 10);
                    ability.setMinDamage(minDamage * 1.3f);
                    ability.setMaxDamage(maxDamage * 1.3f);
                }
        ));

        masterUpgrade = new Upgrade(
                "NAME",
                "Merciful Hex - Master Upgrade",
                """
                        All allies hit receive 1 stack of Merciful Hex. Increase additional targets hit healing/damage by 20%.
                        """,
                50000,
                () -> {
                    ability.setSubsequentReduction(ability.getSubsequentReduction() + 20);
                }
        );
    }

}
