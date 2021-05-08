package com.ebicep.warlords.classes.abilties;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class DamageHealCircle {

    private Player player;
    private Location location;
    private int radius;
    private int duration;
    private int minDamage;
    private int maxDamage;
    private int critChance;
    private int critMultiplier;
    private String name;
    private ArmorStand hammer;

    public DamageHealCircle(Player player, Location location, int radius, int duration, int minDamage, int maxDamage, int critChance, int critMultiplier, String name) {
        this.player = player;
        this.location = location;
        for (int i = 0; i < 10; i++) {
            if (location.getWorld().getBlockAt(location.clone().add(0, -1, 0)).getType() == Material.AIR) {
                location.add(0, -1, 0);
            }
        }
        location.add(0, -1, 0);
        this.radius = radius;
        this.duration = duration;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.critChance = critChance;
        this.critMultiplier = critMultiplier;
        this.name = name;
    }

    public void spawnHammer() {
        Location newLocation = location.clone();
        for (int i = 0; i < 10; i++) {
            if (newLocation.getWorld().getBlockAt(newLocation.clone().add(0, -1, 0)).getType() == Material.AIR) {
                newLocation.add(0, -1, 0);
            }
        }
        newLocation.add(0, -1, 0);
        hammer = (ArmorStand) location.getWorld().spawnEntity(newLocation.clone().add(.25, 2.9, -.25), EntityType.ARMOR_STAND);
        hammer.setRightArmPose(new EulerAngle(20.25, 0, 0));
        hammer.setItemInHand(new ItemStack(Material.STRING));
        hammer.setGravity(false);
        hammer.setVisible(false);
    }

    public void removeHammer() {
        hammer.remove();
    }

    public void spawn() {
        float angle = 0;
        for (int i = 0; i < Math.PI * 20; i++) {
            float x = (float) (radius * Math.sin(angle));
            float z = (float) (radius * Math.cos(angle));
            angle += 0.2;
            if (name.contains("Hammer")) {
                location.getWorld().playEffect(new Location(location.getWorld(), location.getX() + x, location.getY() + 2, location.getZ() + z), Effect.HAPPY_VILLAGER, 0);

            } else {
                location.getWorld().playEffect(new Location(location.getWorld(), location.getX() + x, location.getY() + 1, location.getZ() + z), Effect.HAPPY_VILLAGER, 0);
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public void setMinDamage(int minDamage) {
        this.minDamage = minDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    public int getCritChance() {
        return critChance;
    }

    public void setCritChance(int critChance) {
        this.critChance = critChance;
    }

    public int getCritMultiplier() {
        return critMultiplier;
    }

    public void setCritMultiplier(int critMultiplier) {
        this.critMultiplier = critMultiplier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
