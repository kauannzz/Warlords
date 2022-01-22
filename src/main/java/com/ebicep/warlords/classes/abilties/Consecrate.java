package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Consecrate extends AbstractAbility {

    protected int strikeDamageBoost;
    protected float radius;

    public Consecrate(float minDamageHeal, float maxDamageHeal, int energyCost, int critChance, int critMultiplier, int strikeDamageBoost, float radius) {
        super("Consecrate", minDamageHeal, maxDamageHeal, 7.83f, energyCost, critChance, critMultiplier);
        this.strikeDamageBoost = strikeDamageBoost;
        this.radius = radius;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Consecrate the ground below your\n" +
                "§7feet, declaring it sacred. Enemies\n" +
                "§7standing on it will take §c" + format(minDamageHeal) + " §7-\n" +
                "§c" + format(maxDamageHeal) + " §7damage per second and\n" +
                "§7take §c" + strikeDamageBoost + "% §7increased damage from\n" +
                "§7your paladin strikes. Lasts §65\n" +
                "§7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        DamageHealCircle cons = new DamageHealCircle(wp, player.getLocation(), radius, 5, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        wp.subtractEnergy(energyCost);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.consecrate.activation", 2, 1);
        }

        ArmorStand consecrate = player.getLocation().getWorld().spawn(player.getLocation().clone().add(0, -2, 0), ArmorStand.class);
        consecrate.setMetadata("Consecrate - " + player.getName(), new FixedMetadataValue(Warlords.getInstance(), true));
        consecrate.setGravity(false);
        consecrate.setVisible(false);
        consecrate.setMarker(true);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), cons::spawn, 0, 1);

        new GameRunnable(wp.getGame()) {

            @Override
            public void run() {
                if (!wp.getGame().isFrozen()) {
                    cons.setDuration(cons.getDuration() - 1);
                    PlayerFilter.entitiesAround(cons.getLocation(), radius, 6, radius)
                            .aliveEnemiesOf(wp)
                            .forEach(warlordsPlayer -> {
                                warlordsPlayer.addDamageInstance(
                                        cons.getWarlordsPlayer(),
                                        cons.getName(),
                                        cons.getMinDamage(),
                                        cons.getMaxDamage(),
                                        cons.getCritChance(),
                                        cons.getCritMultiplier(),
                                        false);
                            });
                    if (cons.getDuration() == 0) {
                        consecrate.remove();
                        this.cancel();
                        task.cancel();
                    }
                }
            }

        }.runTaskTimer(0, 20);
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
