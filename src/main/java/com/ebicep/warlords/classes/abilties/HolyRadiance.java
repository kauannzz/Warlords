package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import com.sun.org.apache.xalan.internal.xsltc.dom.ArrayNodeListIterator;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.management.AttributeList;

public class HolyRadiance extends AbstractAbility {

    private final int radius = 6;
    private final int markRadius = 12;
    boolean hasSneakingAbility;

    public HolyRadiance(float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier, boolean hasSneakingAbility) {
        super("Holy Radiance", minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
        this.hasSneakingAbility = hasSneakingAbility;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Radiate with holy energy, healing\n" +
                "§7yourself and all nearby allies for\n" +
                "§a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health." +
                "\n\n" +
                "§7Has a maximum range of §e" + radius + " §7blocks." +
                "\n\n" + (hasSneakingAbility ?
                "§7You may look at an ally to mark\n" +
                "§7them for §610 §7seconds. Increasing\n" +
                "§7their EPS by §e5 §7and speed by §e20%\n" +
                "§7§7for the duration. Mark has an optimal\n" +
                "§7range of §e" + markRadius + " §7blocks." : "");
    }

    private ArmorStand armorStand;

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {


        if (hasSneakingAbility) {
            for (WarlordsPlayer p : PlayerFilter
                    .entitiesAround(player, markRadius, markRadius, markRadius)
                    .aliveTeammatesOfExcludingSelf(wp)
                    .lookingAtFirst(wp)
                    .limit(1)
            ) {
                if (Utils.isLookingAt(player, p.getEntity()) && Utils.hasLineOfSight(player, p.getEntity())) {
                    wp.subtractEnergy(energyCost);

                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(player.getLocation(), "paladin.consecrate.activation", 2, 0.65f);
                    }

                    PacketPlayOutAnimation playOutAnimation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutAnimation);

                    Location lineLocation = player.getLocation().add(0, 1, 0);
                    lineLocation.setDirection(lineLocation.toVector().subtract(p.getLocation().add(0, 1, 0).toVector()).multiply(-1));
                    for (int i = 0; i < Math.floor(player.getLocation().distance(p.getLocation())) * 2; i++) {
                        ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(250, 70, 200), lineLocation, 500);
                        lineLocation.add(lineLocation.getDirection().multiply(.5));
                    }

                    HolyRadiance tempMark = new HolyRadiance(minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, true);
                    p.getCooldownManager().addCooldown(name, HolyRadiance.this.getClass(), tempMark, "MARK", 10, wp, CooldownTypes.BUFF);
                    p.getSpeed().addSpeedModifier("Mark Speed", 20, 20 * 10, "BASE");
                    player.sendMessage(ChatColor.GRAY + "You have marked §e" + p.getName() + "§7!");

                    wp.getGame().getGameTasks().put(

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (!p.getCooldownManager().getCooldown(HolyRadiance.class).isEmpty()) {
                                        Location playerLoc = p.getLocation();
                                        Location particleLoc = playerLoc.clone();
                                        for (int i = 0; i < 4; i++) {
                                            for (int j = 0; j < 10; j++) {
                                                double angle = j / 6D * Math.PI * 2;
                                                double width = 1;
                                                particleLoc.setX(playerLoc.getX() + Math.sin(angle) * width);
                                                particleLoc.setY(playerLoc.getY() + i / 6D);
                                                particleLoc.setZ(playerLoc.getZ() + Math.cos(angle) * width);

                                                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(250, 70, 200), particleLoc, 500);
                                            }
                                        }
                                    } else {
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(Warlords.getInstance(), 0, 10),
                            System.currentTimeMillis()
                    );
                }
            }
        }

        wp.subtractEnergy(energyCost);
        for (WarlordsPlayer p : PlayerFilter
                .entitiesAround(player, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
        ) {
            //p.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
            wp.getGame().getGameTasks().put(
                    new FlyingArmorStand(wp.getLocation(), p, wp, 1.1).runTaskTimer(Warlords.getInstance(), 1, 1),
                    System.currentTimeMillis()
            );
        }

        wp.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
        wp.getSpeed().addSpeedModifier("Radiance", 20, 3 * 20, "BASE");

        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.holyradiance.activation", 2, 1);
        }

        Location particleLoc = player.getLocation().add(0, 1.2, 0);
        ParticleEffect.VILLAGER_HAPPY.display(1, 1, 1, 0.1F, 2, particleLoc, 500);
        ParticleEffect.SPELL.display(1, 1, 1, 0.06F, 12, particleLoc, 500);
    }

    private class FlyingArmorStand extends BukkitRunnable {

        private WarlordsPlayer target;
        private WarlordsPlayer owner;
        private double speed;
        private ArmorStand armorStand;

        public FlyingArmorStand(Location location, WarlordsPlayer target, WarlordsPlayer owner, double speed) {
            this.armorStand = location.getWorld().spawn(location, ArmorStand.class);
            armorStand.setGravity(false);
            armorStand.setVisible(false);
            this.target = target;
            this.speed = speed;
            this.owner = owner;
        }

        @Override
        public void cancel() {
            super.cancel();
            armorStand.remove();
        }

        @Override
        public void run() {
            if (this.target.isDead()) {
                this.cancel();
                return;
            }

            if (target.getWorld() != armorStand.getWorld()) {
                this.cancel();
                return;
            }

            Location targetLocation = target.getLocation();
            Location armorStandLocation = armorStand.getLocation();
            double distance = targetLocation.distanceSquared(armorStandLocation);

            if (distance < speed * speed) {
                target.addHealth(owner, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
                target.getSpeed().addSpeedModifier("Radiance", 20, 3 * 20, "BASE");
                this.cancel();
                return;
            }

            targetLocation.subtract(armorStandLocation);
            //System.out.println(Math.max(speed * 3.25 / targetLocation.lengthSquared() / 2, speed / 10));
            targetLocation.multiply(Math.max(speed * 3.25 / targetLocation.lengthSquared() / 2, speed / 10));

            armorStandLocation.add(targetLocation);
            this.armorStand.teleport(armorStandLocation);

            ParticleEffect.SPELL.display(0.01f, 0, 0.01f, 0.1f, 2, armorStandLocation.add(0, 1.75, 0), 500);
        }
    }
}
