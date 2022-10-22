package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SoulSwitch extends AbstractAbility {

    private int radius = 13;

    public SoulSwitch() {
        super("Soul Switch", 0, 0, 30, 40, -1, 50);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Switch locations with an enemy, blinding them for §61.5 §7seconds. Has an optimal range of §e" + radius +
                " §7blocks. Soul Switch has low vertical range.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        for (WarlordsEntity swapTarget : PlayerFilter
                .entitiesAround(wp.getLocation(), radius, radius / 2f, radius)
                .aliveEnemiesOf(wp)
                .requireLineOfSight(wp)
                .lookingAtFirst(wp)
        ) {
            if (swapTarget.getCarriedFlag() != null) {
                wp.sendMessage(ChatColor.RED + "You cannot Soul Switch with a player holding the flag!");
            } else if (wp.getCarriedFlag() != null) {
                wp.sendMessage(ChatColor.RED + "You cannot Soul Switch while holding the flag!");
            } else {
                wp.subtractEnergy(energyCost, false);
                Utils.playGlobalSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 2, 1.5f);

                Location swapLocation = swapTarget.getLocation();
                Location ownLocation = wp.getLocation();

                EffectUtils.playCylinderAnimation(swapLocation, 1.05, ParticleEffect.CLOUD, 1);
                EffectUtils.playCylinderAnimation(ownLocation, 1.05, ParticleEffect.CLOUD, 1);

                swapTarget.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0, true, false));
                swapTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED + ChatColor.GRAY + " You've been Soul Swapped by " + ChatColor.YELLOW + wp.getName() + "!");
                swapTarget.teleport(new Location(
                        wp.getWorld(),
                        ownLocation.getX(),
                        ownLocation.getY(),
                        ownLocation.getZ(),
                        swapLocation.getYaw(),
                        swapLocation.getPitch()
                ));

                wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN + ChatColor.GRAY + " You swapped with " + ChatColor.YELLOW + swapTarget.getName() + "!");
                wp.teleport(new Location(
                        swapLocation.getWorld(),
                        swapLocation.getX(),
                        swapLocation.getY(),
                        swapLocation.getZ(),
                        ownLocation.getYaw(),
                        ownLocation.getPitch()
                ));

                if (pveUpgrade && swapTarget instanceof WarlordsNPC) {
                    ArmorStand decoy = wp.getWorld().spawn(ownLocation, ArmorStand.class);
                    decoy.setVisible(false);
                    decoy.setGravity(false);
                    decoy.setCustomNameVisible(true);
                    decoy.setCustomName(wp.getColoredName() + "'s Decoy");
                    decoy.setItemInHand(player.getInventory().getItem(0));
                    decoy.setHelmet(HeadUtils.getHead(player.getUniqueId()));
                    decoy.setChestplate(player.getInventory().getChestplate());
                    decoy.setLeggings(player.getInventory().getLeggings());
                    decoy.setBoots(player.getInventory().getBoots());

                    PlayerFilter.entitiesAround(ownLocation, 10, 10, 10)
                            .aliveEnemiesOf(wp)
                            .forEach(warlordsEntity -> {
                                if (warlordsEntity instanceof WarlordsNPC) {
                                    ((WarlordsNPC) warlordsEntity).getMob().setTarget(decoy);
                                }
                            });
                    new GameRunnable(wp.getGame()) {

                        @Override
                        public void run() {
                            decoy.remove();
                            PlayerFilter.entitiesAround(ownLocation, 5, 5, 5)
                                    .aliveEnemiesOf(wp)
                                    .forEach(warlordsEntity -> {
                                        warlordsEntity.addDamageInstance(wp, name, 1004, 1218, 0, 100, false);
                                    });
                            ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.5F, 5, ownLocation.add(0, 1, 0), 500);
                        }
                    }.runTaskLater(60);
                }

                return true;
            }
        }

        return false;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
