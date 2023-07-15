package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.sentinel.MysticalBarrierBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MysticalBarrier extends AbstractAbility implements BlueAbilityIcon, Duration {

    private float runeTimerIncrease = 0.5f;
    private int tickDuration = 100;
    private float meleeDamageReduction = 50;
    private int shieldBase = 200;
    private int shieldIncrease = 80;
    private int shieldMaxHealth = 1000;
    private int reactivateTickDuration = 100;

    public MysticalBarrier() {
        super("Mystical Barrier", 0, 0, 30, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Surround yourself with magical spirits that reduce the melee damage you take by")
                               .append(Component.text(format(meleeDamageReduction) + "%", NamedTextColor.YELLOW))
                               .append(Component.text("and increase the attacker’s cooldowns by "))
                               .append(Component.text(formatHundredths(runeTimerIncrease), NamedTextColor.GOLD))
                               .append(Component.text(" seconds for every instance of damage they deal to you for "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. Reactivate the ability to grant yourself a shield equal to"))
                               .append(Component.text(shieldBase, NamedTextColor.YELLOW))
                               .append(Component.text(" + "))
                               .append(Component.text(shieldIncrease, NamedTextColor.YELLOW))
                               .append(Component.text(" for each instance of damage you took, up to a maximum of "))
                               .append(Component.text(shieldMaxHealth, NamedTextColor.YELLOW))
                               .append(Component.text(" health, that lasts "))
                               .append(Component.text(format(reactivateTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. Not reactivating the ability will instead grant the nearest ally the shield for "))
                               .append(Component.text(format(reactivateTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        AtomicInteger damageInstances = new AtomicInteger();
        Utils.playGlobalSound(wp.getLocation(), Sound.ITEM_ARMOR_EQUIP_DIAMOND, 2, 0.4f);
        Utils.playGlobalSound(wp.getLocation(), "arcanist.mysticalbarrier.activation", 2, 1);
        RegularCooldown<MysticalBarrier> mysticalBarrierCooldown = new RegularCooldown<>(
                name,
                "MYSTIC",
                MysticalBarrier.class,
                new MysticalBarrier(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    PlayerFilter.playingGame(wp.getGame())
                                .teammatesOfExcludingSelf(wp)
                                .closestFirst(wp)
                                .limit(1)
                                .forEach(ally -> {
                                    int shieldHealth = Math.min(shieldMaxHealth, shieldBase + shieldIncrease * damageInstances.get());
                                    giveShield(ally, shieldHealth);
                                });
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    EffectUtils.playCircularEffectAround(
                            wp.getGame(),
                            wp.getLocation(),
                            Particle.TOTEM,
                            3,
                            1,
                            0.15,
                            2.2,
                            8,
                            1,
                            4,
                            ticksElapsed
                    );
                })
        ) {
            @Override
            public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                event.getAttacker().getSpec().increaseAllCooldownTimersBy(runeTimerIncrease);
                damageInstances.getAndIncrement();
            }

            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return event.getAbility().isEmpty() ? currentDamageValue * convertToDivisionDecimal(meleeDamageReduction) : 1;
            }
        };
        wp.getCooldownManager().addCooldown(mysticalBarrierCooldown);

        addSecondaryAbility(() -> {
                    if (!wp.isAlive()) {
                        return;
                    }
                    wp.getCooldownManager().removeCooldownNoForce(mysticalBarrierCooldown);
                    int shieldHealth = Math.min(shieldMaxHealth, shieldBase + shieldIncrease * damageInstances.get());
                    giveShield(wp, shieldHealth);
                },
                false,
                secondaryAbility -> !wp.getCooldownManager().hasCooldown(mysticalBarrierCooldown)
        );
        return true;
    }

    private void giveShield(@Nonnull WarlordsEntity giveTo, int shieldHealth) {
        giveTo.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "SHIELD",
                Shield.class,
                new Shield(name, shieldHealth),
                giveTo,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                })
        ));
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new MysticalBarrierBranch(abilityTree, this);
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public float getRuneTimerIncrease() {
        return runeTimerIncrease;
    }

    public void setRuneTimerIncrease(float runeTimerIncrease) {
        this.runeTimerIncrease = runeTimerIncrease;
    }

    public int getShieldMaxHealth() {
        return shieldMaxHealth;
    }

    public void setShieldMaxHealth(int shieldMaxHealth) {
        this.shieldMaxHealth = shieldMaxHealth;
    }

    public int getShieldIncrease() {
        return shieldIncrease;
    }

    public void setShieldIncrease(int shieldIncrease) {
        this.shieldIncrease = shieldIncrease;
    }

    public int getReactivateTickDuration() {
        return reactivateTickDuration;
    }

    public void setReactivateTickDuration(int reactivateTickDuration) {
        this.reactivateTickDuration = reactivateTickDuration;
    }

    public float getMeleeDamageReduction() {
        return meleeDamageReduction;
    }

    public void setMeleeDamageReduction(float meleeDamageReduction) {
        this.meleeDamageReduction = meleeDamageReduction;
    }
}
