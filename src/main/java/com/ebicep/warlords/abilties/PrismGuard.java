package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ebicep.warlords.effects.EffectUtils.playSphereAnimation;

public class PrismGuard extends AbstractAbility {
    private boolean pveUpgrade = false;

    protected int timesProjectilesReduced = 0;
    protected int timesOtherReduced = 0;

    private int bubbleRadius = 4;
    private int duration = 6;
    private int bubbleHealing = 200;
    private float bubbleMissingHealing = 1.5f;
    private int projectileDamageReduction = 75;

    private float damageReduced = 0;

    public PrismGuard() {
        super("Prism Guard", 0, 0, 26, 40, -1, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Create a bubble shield around you that\n" +
                "§7lasts §6" + duration + " §7seconds. All projectiles that pass through\n" +
                "§7the barrier have their damage reduced by §c" + projectileDamageReduction + "%§7.\n" +
                "\n" +
                "§7After §6" + duration + " §7seconds the bubble will burst, healing\n" +
                "§7you and all allies for §a" + bubbleHealing + " §7+ §a" + bubbleMissingHealing + "% §7missing health and\n" +
                "§7grant §e3% §7damage reduction (max 30%) for §6" + duration + " §7seconds\n" +
                "§7based on how many hits you took while Prism\n" +
                "§7Guard was active.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Times Projectiles Damage Reduced", "" + timesProjectilesReduced));
        info.add(new Pair<>("Times Other Damage Reduced", "" + timesOtherReduced));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.teleport", 2, 2);
        Utils.playGlobalSound(wp.getLocation(), "warrior.intervene.impact", 2, 0.1f);

        // First Particle Sphere
        playSphereAnimation(wp.getLocation(), bubbleRadius + 2.5, 68, 176, 236);

        // Second Particle Sphere
        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                playSphereAnimation(wp.getLocation(), bubbleRadius + 1, 65, 185, 205);
                Utils.playGlobalSound(wp.getLocation(), "warrior.intervene.impact", 2, 0.2f);
            }
        }.runTaskLater(3);

        Set<WarlordsEntity> isInsideBubble = new HashSet<>();
        PrismGuard tempPrismGuard = new PrismGuard();
        AtomicInteger hits = new AtomicInteger();
        wp.getCooldownManager().addCooldown(new RegularCooldown<PrismGuard>(
                "Prism Guard",
                "GUARD",
                PrismGuard.class,
                tempPrismGuard,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (tempPrismGuard.getDamageReduced() >= 8000) {
                        ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.VENERED_REFRACTION);
                    }
                    if (wp.isDead()) return;
                    Utils.playGlobalSound(wp.getLocation(), "paladin.holyradiance.activation", 2, 1.4f);
                    Utils.playGlobalSound(wp.getLocation(), Sound.AMBIENCE_THUNDER, 2, 1.5f);

                    new CircleEffect(
                            wp.getGame(),
                            wp.getTeam(),
                            wp.getLocation(),
                            bubbleRadius,
                            new CircumferenceEffect(ParticleEffect.SPELL).particlesPerCircumference(2)
                    ).playEffects();

                    for (WarlordsEntity entity : PlayerFilter
                            .entitiesAround(wp, bubbleRadius, bubbleRadius, bubbleRadius)
                            .aliveTeammatesOf(wp)
                    ) {
                        float healingValue = bubbleHealing + (entity.getMaxHealth() - entity.getHealth()) * (hits.get() * (bubbleMissingHealing / 100f));
                        entity.addHealingInstance(
                                wp,
                                name,
                                healingValue,
                                healingValue,
                                -1,
                                100,
                                false,
                                false
                        );
                        Bukkit.broadcastMessage("healingValue:" + healingValue);
                        Bukkit.broadcastMessage("hits:" + hits.get());
                        Bukkit.broadcastMessage("healingValue with missing:" + hits.get() * bubbleMissingHealing);

                        if (hits.get() > 5) {
                            hits.set(5);
                        }

                        Bukkit.broadcastMessage("hits after:" + hits.get());

                        entity.getCooldownManager().addCooldown(new RegularCooldown<PrismGuard>(
                                "Prism Guard",
                                "GUARD RES",
                                PrismGuard.class,
                                tempPrismGuard,
                                wp,
                                CooldownTypes.ABILITY,
                                cm -> {
                                },
                                duration * 20
                        ) {
                            @Override
                            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                float afterReduction;
                                afterReduction = currentDamageValue * (100 - (hits.get() * 3) / 100f);
                                tempPrismGuard.addDamageReduced(currentDamageValue - afterReduction);
                                return afterReduction;
                            }
                        });
                    }
                },
                duration * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed < 5) {
                        return;
                    }

                    if (ticksElapsed % 3 == 0) {
                        playSphereAnimation(wp.getLocation(), bubbleRadius, 120, 120, 220);
                        Utils.playGlobalSound(wp.getLocation(), Sound.CREEPER_DEATH, 2, 2);

                        isInsideBubble.clear();
                        for (WarlordsEntity enemyInsideBubble : PlayerFilter
                                .entitiesAround(wp, bubbleRadius, bubbleRadius, bubbleRadius)
                                .aliveEnemiesOf(wp)
                        ) {
                            isInsideBubble.add(enemyInsideBubble);
                        }

                        for (WarlordsEntity bubblePlayer : PlayerFilter
                                .entitiesAround(wp, bubbleRadius, bubbleRadius, bubbleRadius)
                                .aliveTeammatesOfExcludingSelf(wp)
                        ) {
                            bubblePlayer.getCooldownManager().removeCooldown(PrismGuard.class);
                            bubblePlayer.getCooldownManager().addCooldown(new RegularCooldown<PrismGuard>(
                                    "Prism Guard",
                                    "GUARD",
                                    PrismGuard.class,
                                    tempPrismGuard,
                                    wp,
                                    CooldownTypes.ABILITY,
                                    cooldownManager -> {
                                    },
                                    10
                            ) {
                                @Override
                                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                    float afterReduction;
                                    hits.getAndIncrement();
                                    if (isProjectile(event.getAbility())) {
                                        if (isInsideBubble.contains(event.getAttacker())) {
                                            afterReduction = currentDamageValue;
                                        } else {
                                            timesProjectilesReduced++;
                                            afterReduction = currentDamageValue * (100 - projectileDamageReduction) / 100f;
                                        }
                                    } else {
                                        afterReduction = currentDamageValue;
                                    }
                                    tempPrismGuard.addDamageReduced(currentDamageValue - afterReduction);
                                    return afterReduction;
                                }
                            });
                        }
                    }

                    if (ticksElapsed % 10 == 0) {
                        if (pveUpgrade) {
                            for (WarlordsEntity we : PlayerFilter
                                    .entitiesAround(wp, 15, 15, 15)
                                    .aliveEnemiesOf(wp)
                                    .closestFirst(wp)
                            ) {
                                if (we instanceof WarlordsNPC) {
                                    ((WarlordsNPC) we).getMob().setTarget(wp);
                                }
                            }
                        }
                    }
                })
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float afterReduction;
                hits.getAndIncrement();
                if (isProjectile(event.getAbility())) {
                    if (isInsideBubble.contains(event.getAttacker())) {
                        afterReduction = currentDamageValue;
                    } else {
                        timesProjectilesReduced++;
                        afterReduction = currentDamageValue * (100 - projectileDamageReduction) / 100f;
                    }
                } else {
                    afterReduction = currentDamageValue;
                }
                tempPrismGuard.addDamageReduced(currentDamageValue - afterReduction);
                return afterReduction;
            }
        });

        return true;
    }

    private boolean isProjectile(String ability) {
        return ability.equals("Fireball") ||
                ability.equals("Frostbolt") ||
                ability.equals("Water Bolt") ||
                ability.equals("Lightning Bolt") ||
                ability.equals("Flame Burst") ||
                ability.equals("Fallen Souls") ||
                ability.equals("Soothing Elixir");
    }

    public int getProjectileDamageReduction() {
        return projectileDamageReduction;
    }

    public void setProjectileDamageReduction(int projectileDamageReduction) {
        this.projectileDamageReduction = projectileDamageReduction;
    }

    public int getBubbleHealing() {
        return bubbleHealing;
    }

    public void setBubbleHealing(int bubbleHealing) {
        this.bubbleHealing = bubbleHealing;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getBubbleRadius() {
        return bubbleRadius;
    }

    public void setBubbleRadius(int bubbleRadius) {
        this.bubbleRadius = bubbleRadius;
    }

    public float getBubbleMissingHealing() {
        return bubbleMissingHealing;
    }

    public void setBubbleMissingHealing(float bubbleMissingHealing) {
        this.bubbleMissingHealing = bubbleMissingHealing;
    }

    public void addDamageReduced(float amount) {
        damageReduced += amount;
    }

    public float getDamageReduced() {
        return damageReduced;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }
}
