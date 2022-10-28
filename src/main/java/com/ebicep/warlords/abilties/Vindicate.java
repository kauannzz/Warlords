package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Vindicate extends AbstractAbility {

    public int debuffsRemovedOnCast = 0;

    private final int radius = 8;
    private int vindicateDuration = 12;
    private int vindicateSelfDuration = 8;
    private float vindicateDamageReduction = 30;

    public Vindicate() {
        super("Vindicate", 0, 0, 55, 25);
    }

    @Override
    public void updateDescription(Player player) {
        description = "All allies within an §e" + radius + " §7block radius gain the status §6VIND§7, which clears all de-buffs. In addition, the status " +
                "§6VIND §7prevents allies from being affected by de-buffs and grants §650% §7knockback resistance for §6" + vindicateDuration +
                " §7seconds. You gain §e" + format(vindicateDamageReduction) + "% §7damage reduction for §6" + vindicateSelfDuration + " §7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Debuffs Removed On Cast", "" + debuffsRemovedOnCast));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "rogue.vindicate.activation", 2, 0.7f);
        Utils.playGlobalSound(player.getLocation(), "shaman.capacitortotem.pulse", 2, 0.7f);

        new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                player.getLocation(),
                radius,
                new CircumferenceEffect(ParticleEffect.SPELL, ParticleEffect.REDSTONE).particlesPerCircumference(2)
        ).playEffects();

        EffectUtils.playHelixAnimation(player, radius, 230, 130, 5);

        Vindicate tempVindicate = new Vindicate();
        for (WarlordsEntity vindicateTarget : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOf(wp)
                .closestFirst(wp)
        ) {
            if (vindicateTarget != wp) {
                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN +
                        ChatColor.GRAY + " Your Vindicate is now protecting " +
                        ChatColor.YELLOW + vindicateTarget.getName() +
                        ChatColor.GRAY + "!"
                );

                vindicateTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN + " " +
                        ChatColor.GRAY + wp.getName() + "'s" +
                        ChatColor.YELLOW + " Vindicate" +
                        ChatColor.GRAY + " is now protecting you from de-buffs for " +
                        ChatColor.GOLD + vindicateDuration +
                        ChatColor.GRAY + " seconds!"
                );
            }

            // Vindicate Immunity
            vindicateTarget.getSpeed().removeSlownessModifiers();
            debuffsRemovedOnCast += vindicateTarget.getCooldownManager().removeDebuffCooldowns();
            giveVindicateCooldown(wp, vindicateTarget, Vindicate.class, tempVindicate, vindicateDuration * 20);
        }

        wp.getCooldownManager().addCooldown(new RegularCooldown<Vindicate>(
                "Vindicate Resistance",
                "VIND RESIST",
                Vindicate.class,
                tempVindicate,
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                vindicateSelfDuration * 20
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                WarlordsEntity we = event.getAttacker();
                if (pveUpgrade) {
                    if (Utils.isLineOfSightVindicator(event.getPlayer().getEntity(), we.getEntity())) {
                        Utils.addKnockback(wp.getLocation(), event.getAttacker(), -1.25, 0.3);
                        we.addDamageInstance(event.getPlayer(), name, currentDamageValue, currentDamageValue, 0, 100, false);
                        return currentDamageValue * 0;
                    } else {
                        return currentDamageValue * getVindicateDamageReduction();
                    }
                } else {
                    return currentDamageValue * getVindicateDamageReduction();
                }
            }
        });

        return true;
    }

    public static <T> void giveVindicateCooldown(WarlordsEntity from, WarlordsEntity target, Class<T> cooldownClass, T cooldownObject, int tickDuration) {
        // remove other instances of vindicate buff to override
        target.getCooldownManager().removeCooldownByName("Vindicate Debuff Immunity");
        target.getCooldownManager().addCooldown(new RegularCooldown<T>(
                "Vindicate Debuff Immunity",
                "VIND",
                cooldownClass,
                cooldownObject,
                from,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    target.getSpeed().removeSlownessModifiers();
                    target.getCooldownManager().removeDebuffCooldowns();
                })
        ) {
            @Override
            public void multiplyKB(Vector currentVector) {
                currentVector.multiply(0.5);
            }
        });
    }

    public float getVindicateDamageReduction() {
        return (100 - vindicateDamageReduction) / 100f;
    }

    public void setVindicateDamageReduction(float vindicateDamageReduction) {
        this.vindicateDamageReduction = vindicateDamageReduction;
    }


    public int getVindicateDuration() {
        return vindicateDuration;
    }

    public void setVindicateDuration(int vindicateDuration) {
        this.vindicateDuration = vindicateDuration;
    }

    public int getVindicateSelfDuration() {
        return vindicateSelfDuration;
    }

    public void setVindicateSelfDuration(int vindicateSelfDuration) {
        this.vindicateSelfDuration = vindicateSelfDuration;
    }
}
