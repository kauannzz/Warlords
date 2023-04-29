package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilties.SoulShackle;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.TormentedSoul;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class Ghoulcaller extends AbstractZombie implements BossMob {

    private static final HashMap<Integer, Pair<Float, Float>> PLAYER_COUNT_DAMAGE_VALUES = new HashMap<>() {{
        put(1, new Pair<>(736f, 778f));
        put(2, new Pair<>(1121f, 1191f));
        put(3, new Pair<>(1502f, 1599f));
        put(4, new Pair<>(1744f, 1859f));
    }};
    private boolean skipNextAttack = false;
    private int timesInARowDamageMaxReduced = 0;

    public Ghoulcaller(Location spawnLocation) {
        super(spawnLocation,
                "Ghoulcaller",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.DEMON_SKELETON),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 170, 170, 170),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 170, 170, 170),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 170, 170, 170),
                        Weapons.ENDERFIST.getItem()
                ),
                16000,
                0.42f,
                5,
                277,
                416
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
            if (we.getEntity() instanceof Player) {
                PacketUtils.sendTitle(
                        (Player) we.getEntity(),
                        ChatColor.RED + getWarlordsNPC().getName(),
                        ChatColor.GOLD + "Chained Agony",
                        20, 30, 20
                );
            }
        }
        spawnTormentedSouls(option, option.getDifficulty() == DifficultyIndex.EASY ? 5 : 10);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 20 == 0) {
            if (getWarlordsNPC().getCooldownManager().hasCooldown(SoulShackle.class) && !skipNextAttack) {
                skipNextAttack = true;
            }
        }
        //every 5 seconds. [this mob’s fury] deals (Damage values depending on total players in game) * 0.95 ^ x damage, where x is the amount of attacks that hit this mob in the past 5s.
        if (ticksElapsed % 100 == 0) {
            if (skipNextAttack) {
                PlayerFilter.entitiesAround(getWarlordsNPC(), 10, 10, 10)
                            .aliveEnemiesOf(getWarlordsNPC())
                            .forEach(enemyPlayer -> enemyPlayer.sendMessage(Component.text("Ghoulcaller's Fury has been silenced!", NamedTextColor.RED)));
                skipNextAttack = false;
            } else {
                List<WarlordsDamageHealingFinalEvent> eventsInLast5Seconds = getWarlordsNPC().getSecondStats().getEventsAsSelfFromLastSecondStream(5)
                                                                                             .filter(WarlordsDamageHealingFinalEvent::isDamageInstance)
                                                                                             .toList();
                int attacksInLast5Seconds = (int) (eventsInLast5Seconds.size() - eventsInLast5Seconds.stream()
                                                                                                     .filter(event -> event.getAbility()
                                                                                                                           .equals("Windfury Weapon"))
                                                                                                     .count() / 2
                );
                if (attacksInLast5Seconds > 20) {
                    attacksInLast5Seconds = 20;
                    timesInARowDamageMaxReduced++;
                    PlayerFilterGeneric.playingGameWarlordsPlayers(getWarlordsNPC().getGame())
                            .limit(1)
                            .forEach(warlordsPlayer -> ChallengeAchievements.checkForAchievement(warlordsPlayer, ChallengeAchievements.CONTROLLED_FURY));
                } else {
                    timesInARowDamageMaxReduced = 0;
                }

                int playerCount = option.playerCount();
                float minDamage = (float) (PLAYER_COUNT_DAMAGE_VALUES.getOrDefault(
                        playerCount,
                        PLAYER_COUNT_DAMAGE_VALUES.get(1)
                ).getA() * Math.pow(0.95, attacksInLast5Seconds)
                );
                float maxDamage = (float) (PLAYER_COUNT_DAMAGE_VALUES.getOrDefault(
                        playerCount,
                        PLAYER_COUNT_DAMAGE_VALUES.get(1)
                ).getB() * Math.pow(0.95, attacksInLast5Seconds)
                );

                float multiplier = switch (option.getDifficulty()) {
                    case EASY -> 0.5f;
                    case HARD -> 1.5f;
                    default -> 1;
                };
                Location loc = warlordsNPC.getLocation();
                Utils.playGlobalSound(loc, "paladin.consecrate.activation", 2, 0.3f);
                EffectUtils.playHelixAnimation(loc, 10, Particle.VILLAGER_ANGRY, 1, 20);
                PlayerFilter.entitiesAround(getWarlordsNPC(), 10, 10, 10)
                            .aliveEnemiesOf(getWarlordsNPC())
                            .forEach(enemyPlayer -> {
                                enemyPlayer.addDamageInstance(
                                        getWarlordsNPC(),
                                        "Fury",
                                        minDamage * multiplier,
                                        maxDamage * multiplier,
                                        0,
                                        100,
                                    false
                            );
                        });
            }
        }
        //Spawn 2 * (The number of players in the game) Tormented Souls every 20 seconds
        if (ticksElapsed % 400 == 0) {
            spawnTormentedSouls(option, (int) (2 * option.getGame().warlordsPlayers().count()));
        }

        if (ticksElapsed % 10 == 0) {
            EffectUtils.playCylinderAnimation(warlordsNPC.getLocation(), 1.1, 150, 120, 120);
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        //silence player for 2s per melee
        if (event.getAbility().isEmpty()) {
            SoulShackle.shacklePlayer(attacker, receiver, 40);
            PlayerFilter.entitiesAround(getWarlordsNPC(), 3, 3, 3)
                    .aliveEnemiesOf(getWarlordsNPC())
                    .excluding(attacker)
                    .forEach(enemyPlayer -> SoulShackle.shacklePlayer(attacker, enemyPlayer, 40));
        }

        FireWorkEffectPlayer.playFirework(receiver.getLocation(), FireworkEffect.builder()
                .withColor(Color.BLACK)
                .with(FireworkEffect.Type.BURST)
                .build());
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        EffectUtils.playRandomHitEffect(self.getLocation(), 150, 150, 150, 7);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                                                                       .withColor(Color.GRAY)
                                                                       .with(FireworkEffect.Type.BALL_LARGE)
                                                                       .withTrail()
                                                                       .build());
    }

    private void spawnTormentedSouls(PveOption option, int amount) {
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 1.5f);
        for (int i = 0; i < amount; i++) {
            option.spawnNewMob(new TormentedSoul(getWarlordsNPC().getLocation()));
        }
    }

    public int getTimesInARowDamageMaxReduced() {
        return timesInARowDamageMaxReduced;
    }
}
