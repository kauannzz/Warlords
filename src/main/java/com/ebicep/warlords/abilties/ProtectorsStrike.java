package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractStrikeBase;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.ebicep.warlords.util.warlords.Utils.lerp;

public class ProtectorsStrike extends AbstractStrikeBase {
    private boolean pveUpgrade = false;
    // Percentage
    private int minConvert = 75;
    private int maxConvert = 100;
    private int maxAllies = 2;

    public ProtectorsStrike() {
        super("Protector's Strike", 261, 352, 0, 90, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Strike the targeted enemy player,\n" +
                "§7causing §c" + minDamageHeal + " §7- §c" + maxDamageHeal + " §7damage\n" +
                "§7and healing two nearby allies for\n" +
                "§a" + maxConvert + "-" + minConvert + "% §7of the damage done. Also\n" +
                "§7heals yourself by §a50-75% §7of the\n" +
                "§7damage done. Based on your current\n" +
                "health.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Struck", "" + timesUsed));

        return info;
    }

    @Override
    protected boolean onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer) {
        AtomicReference<Float> minDamage = new AtomicReference<>(minDamageHeal);
        AtomicReference<Float> maxDamage = new AtomicReference<>(maxDamageHeal);
        getStandingOnConsecrate(wp, nearPlayer).ifPresent(consecrate -> {
            wp.doOnStaticAbility(Consecrate.class, Consecrate::addStrikesBoosted);
            minDamage.getAndUpdate(value -> value *= (1 + consecrate.getStrikeDamageBoost() / 100f));
            maxDamage.getAndUpdate(value -> value *= (1 + consecrate.getStrikeDamageBoost() / 100f));
        });
        nearPlayer.addDamageInstance(
                wp,
                name,
                minDamage.get(),
                maxDamage.get(),
                critChance,
                critMultiplier,
                false
        ).ifPresent(warlordsDamageHealingFinalEvent -> {
            float currentDamageValue = warlordsDamageHealingFinalEvent.getValue();
            boolean isCrit = warlordsDamageHealingFinalEvent.isCrit();

            float healthFraction = lerp(0, 1, wp.getHealth() / wp.getMaxHealth());

            if (healthFraction > 1) {
                healthFraction = 1; // in the case of overheal
            }

            if (healthFraction < 0) {
                healthFraction = 0;
            }

            float allyHealing = (minConvert / 100f) + healthFraction * 0.25f;
            float ownHealing = ((maxConvert / 100f) / 2f) + (1 - healthFraction) * 0.25f;
            // Self Heal
            wp.addHealingInstance(
                    wp,
                    name,
                    currentDamageValue * ownHealing,
                    currentDamageValue * ownHealing,
                    isCrit ? 100 : -1,
                    100,
                    false,
                    false
            ).ifPresent(event -> {
                new CooldownFilter<>(wp, RegularCooldown.class)
                        .filter(regularCooldown -> regularCooldown.getFrom().equals(wp))
                        .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class)
                        .forEach(hammerOfLight -> hammerOfLight.addAmountHealed(event.getValue()));
            });
            // Ally Heal
            if (pveUpgrade) {
                for (WarlordsEntity ally : PlayerFilter
                        .entitiesAround(wp, 10, 10, 10)
                        .aliveTeammatesOfExcludingSelf(wp)
                        .limit(maxAllies)
                        .leastAliveFirst()
                ) {
                    boolean isLeastAlive = ally.getHealth() < ally.getHealth();
                    float healing = (currentDamageValue * allyHealing) * (isLeastAlive ? 1.7f : 1);
                    ally.addHealingInstance(
                            wp,
                            name,
                            healing,
                            healing,
                            isCrit ? 100 : -1,
                            100,
                            false,
                            false
                    ).ifPresent(event -> {
                        new CooldownFilter<>(wp, RegularCooldown.class)
                                .filter(regularCooldown -> regularCooldown.getFrom().equals(wp))
                                .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class)
                                .forEach(hammerOfLight -> hammerOfLight.addAmountHealed(event.getValue()));
                    });
                }
            } else {
                for (WarlordsEntity ally : PlayerFilter
                        .entitiesAround(wp, 10, 10, 10)
                        .aliveTeammatesOfExcludingSelf(wp)
                        .sorted(Comparator.comparing((WarlordsEntity p) -> p.getCooldownManager().hasCooldown(HolyRadianceProtector.class) ? 0 : 1)
                                .thenComparing(Utils.sortClosestBy(WarlordsEntity::getLocation, wp.getLocation())))
                        .limit(maxAllies)
                ) {
                    if (PlayerSettings.getPlayerSettings(wp.getUuid()).getSkillBoostForClass() == SkillBoosts.PROTECTOR_STRIKE) {
                        ally.addHealingInstance(
                                wp,
                                name,
                                currentDamageValue * allyHealing * 1.2f,
                                currentDamageValue * allyHealing * 1.2f,
                                isCrit ? 100 : -1,
                                100,
                                false,
                                false
                        ).ifPresent(event -> {
                            new CooldownFilter<>(wp, RegularCooldown.class)
                                    .filter(regularCooldown -> regularCooldown.getFrom().equals(wp))
                                    .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class)
                                    .forEach(hammerOfLight -> hammerOfLight.addAmountHealed(event.getValue()));
                        });
                    } else {
                        ally.addHealingInstance(
                                wp,
                                name,
                                currentDamageValue * allyHealing,
                                currentDamageValue * allyHealing,
                                isCrit ? 100 : -1,
                                100,
                                false,
                                false
                        ).ifPresent(event -> {
                            new CooldownFilter<>(wp, RegularCooldown.class)
                                    .filter(regularCooldown -> regularCooldown.getFrom().equals(wp))
                                    .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class)
                                    .forEach(hammerOfLight -> hammerOfLight.addAmountHealed(event.getValue()));
                        });
                    }
                }
            }
        });
        return true;
    }

    @Override
    protected void playSoundAndEffect(Location location) {
        Utils.playGlobalSound(location, "paladin.paladinstrike.activation", 2, 1);
        randomHitEffect(location, 5, 255, 0, 0);
        ParticleEffect.SPELL.display(
                (float) ((Math.random() * 2) - 1),
                (float) ((Math.random() * 2) - 1),
                (float) ((Math.random() * 2) - 1),
                1,
                4,
                location.clone().add(0, 1, 0),
                500);
    }

    public int getMinConvert() {
        return minConvert;
    }

    public void setMinConvert(int convertPercent) {
        this.minConvert = convertPercent;
    }

    public int getMaxConvert() {
        return maxConvert;
    }

    public void setMaxConvert(int selfConvertPercent) {
        this.maxConvert = selfConvertPercent;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }

    public int getMaxAllies() {
        return maxAllies;
    }

    public void setMaxAllies(int maxAllies) {
        this.maxAllies = maxAllies;
    }
}