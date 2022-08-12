package com.ebicep.warlords.game.option.wavedefense.mobs.magmacube;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.LastStand;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.UUID;

public class MagmaCube extends AbstractMagmaCube implements EliteMob {

    public MagmaCube(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Illumination",
                MobTier.ELITE,
                null,
                4000,
                0.5f,
                20,
                0,
                0
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        getWarlordsNPC().getEntity().getWorld().spigot().strikeLightningEffect(getWarlordsNPC().getLocation(), false);
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 60 != 0) {
            return;
        }
        Location loc = getWarlordsNPC().getLocation();
        WarlordsEntity we = Warlords.getPlayer(getWarlordsNPC().getEntity());
        if (we == null) return;
        EffectUtils.playSphereAnimation(loc, 9, ParticleEffect.SPELL, 1);
        Utils.playGlobalSound(loc, "warrior.laststand.activation", 2, 0.6f);
        for (WarlordsEntity ally : PlayerFilter
                .entitiesAround(we, 9, 9, 9)
                .aliveTeammatesOfExcludingSelf(we)
        ) {
            if (!ally.getEntity().equals(this)) {
                ally.getCooldownManager().addCooldown(new RegularCooldown<LastStand>(
                        name,
                        "",
                        LastStand.class,
                        new LastStand(),
                        we,
                        CooldownTypes.ABILITY,
                        cooldownManager -> {
                        },
                        3 * 20
                ) {
                    @Override
                    public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * .5f;
                    }
                });
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, String ability) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity mob, WarlordsEntity attacker) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        WarlordsEntity we = Warlords.getPlayer(getWarlordsNPC().getEntity());
        if (we != null) {
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(we, 6, 6, 6)
                    .aliveEnemiesOf(we)
            ) {
                enemy.addDamageInstance(we, "Blight", 900, 1200, -1, 100, false);
            }
        }

        for (int i = 0; i < 2; i++) {
            BabyMagmaCube babyMagmaCube = new BabyMagmaCube(warlordsNPC.getLocation());
            babyMagmaCube.toNPC(warlordsNPC.getGame(), Team.RED, UUID.randomUUID());
            option.getGame().addNPC(babyMagmaCube.getWarlordsNPC());
            option.getMobs().add(babyMagmaCube);
        }

        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.RED)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withTrail()
                .build());
        EffectUtils.playHelixAnimation(deathLocation, 6, 255, 40, 40);
        Utils.playGlobalSound(deathLocation, Sound.ENDERMAN_SCREAM, 1, 2);
    }
}
