package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class LightningBolt extends AbstractAbility {

    public LightningBolt() {
        super("Lightning Bolt", -249, -462, 0, 60, 20, 200, "lightning bolt description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Location location = player.getLocation();
        Vector direction = location.getDirection();

        Bolt bolt = new Bolt(Warlords.getPlayer(player), (ArmorStand) location.getWorld().spawnEntity(location.subtract(direction.getX() * -.5, .3, direction.getZ() * -.5), EntityType.ARMOR_STAND), location, direction, this);
        Warlords.getBolts().add(bolt);
        Warlords.getPlayer(player).subtractEnergy(energyCost);

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "shaman.lightningbolt.activation", 1, 1);
        }
    }
}
