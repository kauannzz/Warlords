package com.ebicep.warlords.pve.events.supplydrop;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class SupplyDropManager {

    private static final ConcurrentHashMap<UUID, Boolean> PLAYER_ROLL_COOLDOWN = new ConcurrentHashMap<>();

    public static void sendSupplyDropMessage(UUID uuid, String message) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer.isOnline()) {
            offlinePlayer.getPlayer().sendMessage(ChatColor.GOLD + "Supply Drop" + ChatColor.DARK_GRAY + " > " + message);
        }
    }

    public static void openSupplyDropMenu(Player player) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();

            Menu menu = new Menu("Supply Drop", 9 * 6);

            menu.setItem(
                    4,
                    1,
                    new ItemBuilder(Material.GOLD_NUGGET)
                            .name(Component.text("Click to buy a supply drop token", NamedTextColor.GREEN))
                            .loreLEGACY(
                                    ChatColor.GREEN + "Cost: " + Currencies.COIN.getCostColoredName(10000),
                                    ChatColor.GREEN + "Balance: " + ChatColor.YELLOW + NumberFormat.addCommas(databasePlayerPvE.getCurrencyValue(Currencies.COIN)) + " coins"
                            )
                            .get(),
                    (m, e) -> {
                        if (databasePlayerPvE.getCurrencyValue(Currencies.COIN) < 10000) {
                            player.sendMessage(Component.text("You do not have enough coins to buy a supply drop token!", NamedTextColor.RED));
                            player.closeInventory();
                            return;
                        }
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        databasePlayerPvE.subtractCurrency(Currencies.COIN, 10000);
                        databasePlayerPvE.addCurrency(Currencies.SUPPLY_DROP_TOKEN, 1);
                        openSupplyDropMenu(player);
                    }
            );

            Long tokens = databasePlayerPvE.getCurrencyValue(Currencies.SUPPLY_DROP_TOKEN);
            menu.setItem(
                    2,
                    3,
                    new ItemBuilder(Material.GOLDEN_HORSE_ARMOR)
                            .name(Component.text("Click to call a supply drop", NamedTextColor.GREEN))
                            .loreLEGACY(
                                    ChatColor.GRAY + "Cost: " + Currencies.SUPPLY_DROP_TOKEN.getCostColoredName(1),
                                    ChatColor.GRAY + "Balance: " + Currencies.SUPPLY_DROP_TOKEN.getCostColoredName(tokens),
                                    "",
                                    ChatColor.YELLOW.toString() + ChatColor.BOLD + "SHIFT-CLICK" + ChatColor.GRAY + " to INSTANTLY call a supply drop"
                            )
                            .get(),
                    (m, e) -> {
                        if (PLAYER_ROLL_COOLDOWN.getOrDefault(player.getUniqueId(), false)) {
                            player.sendMessage(Component.text("You must wait for your current roll to end to roll again!", NamedTextColor.RED));
                            return;
                        }
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        if (tokens > 0) {
                            supplyDropRoll(player, 1, e.isShiftClick());
                        } else {
                            player.sendMessage(Component.text("You do not have any supply drop tokens to call a supply drop.", NamedTextColor.RED));
                        }
                        player.closeInventory();
                    }
            );
            menu.setItem(
                    6,
                    3,
                    new ItemBuilder(Material.DIAMOND_HORSE_ARMOR)
                            .name(Component.text("Click to call all available supply drops (Max 25)", NamedTextColor.GREEN))
                            .loreLEGACY(
                                    ChatColor.GRAY + "Cost: " + Currencies.SUPPLY_DROP_TOKEN.getCostColoredName(tokens),
                                    ChatColor.GRAY + "Balance: " + Currencies.SUPPLY_DROP_TOKEN.getCostColoredName(tokens),
                                    "",
                                    ChatColor.GRAY + "NOTE: Max 25 at a time",
                                    "",
                                    ChatColor.YELLOW.toString() + ChatColor.BOLD + "SHIFT-CLICK" + ChatColor.GRAY + " to INSTANTLY call all available supply drops"
                            )
                            .get(),
                    (m, e) -> {
                        if (PLAYER_ROLL_COOLDOWN.getOrDefault(player.getUniqueId(), false)) {
                            player.sendMessage(Component.text("You must wait for your current roll to end to roll again!", NamedTextColor.RED));
                            return;
                        }
                        if (tokens > 0) {
                            supplyDropRoll(player, Math.min(tokens, 25), e.isShiftClick());
                        } else {
                            player.sendMessage(Component.text("You do not have any supply drop tokens to call a supply drop.", NamedTextColor.RED));
                        }
                        player.closeInventory();
                    }
            );

            //last 20 supply drops
            List<SupplyDropEntry> supplyDropEntries = databasePlayerPvE.getSupplyDropEntries();
            List<String> supplyDropHistory = supplyDropEntries
                    .subList(Math.max(0, supplyDropEntries.size() - 20), supplyDropEntries.size())
                    .stream()
                    .map(SupplyDropEntry::getReward)
                    .map(supplyDropRewards -> supplyDropRewards.getChatColor() + supplyDropRewards.name + "\n")
                    .toList();
            menu.setItem(
                    5,
                    5,
                    new ItemBuilder(Material.BOOK)
                            .name(Component.text("Your most recent supply drops", NamedTextColor.GREEN))
                            .loreLEGACY(IntStream.range(0, supplyDropHistory.size())
                                                 .mapToObj(index -> ChatColor.GRAY.toString() + (index + 1) + ". " + supplyDropHistory.get(supplyDropHistory.size() - index - 1))
                                                 .toList())
                            .get(),
                    (m, e) -> {

                    }
            );

            menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }, () -> player.sendMessage(Component.text("Susan does not want to talk to you right now.", NamedTextColor.RED)));
    }

    public static void supplyDropRoll(Player player, long amount, boolean instant) {
        UUID uuid = player.getUniqueId();
        DatabaseManager.getPlayer(uuid, databasePlayer -> {
            PLAYER_ROLL_COOLDOWN.put(uuid, true);
            sendSupplyDropMessage(uuid,
                    ChatColor.GREEN + "Called " + ChatColor.YELLOW + amount + ChatColor.GREEN + " supply drop" + (amount > 1 ? "s" : "") + "!"
            );
            DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();
            databasePlayerPvE.subtractCurrency(Currencies.SUPPLY_DROP_TOKEN, amount);

            int slownessIncrementRate = amount == 1 ? 20 : 9;
            int slownessMax = amount == 1 ? 9 : 5;

            new BukkitRunnable() {

                int counter = 0;
                int slowness = 1;
                int rewardsGained = 0;
                int cooldown = 0;
                SupplyDropRewards reward;

                @Override
                public void run() {
                    if (cooldown > 0) {
                        cooldown--;
                        return;
                    }

                    if (instant || counter % slowness == 0) {
                        reward = SupplyDropRewards.getRandomReward();
                        Random random = new Random();
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, random.nextFloat());
                        PacketUtils.sendTitle(
                                uuid,
                                reward.getChatColor() + reward.name,
                                "",
                                0, 100, 0
                        );
                    }

                    counter++;

                    if (instant || counter % slownessIncrementRate == 0) {
                        slowness++;
                        if (instant || slowness == slownessMax) {
                            reward.givePlayerRewardTitle(player);
                            slowness = 1;
                            rewardsGained++;
                            cooldown = instant ? 3 : 13;
                            sendSupplyDropMessage(uuid, reward.getDropMessage());
                            if (reward.rarity == WeaponsPvE.EPIC) {
                                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                    sendSupplyDropMessage(onlinePlayer.getUniqueId(),
                                            ChatColor.AQUA + player.getName() +
                                                    ChatColor.GRAY + " got lucky and received " + reward.getChatColor() + reward.name +
                                                    ChatColor.GRAY + " from the supply drop!"
                                    );
                                }
                            }
                            databasePlayerPvE.addSupplyDropEntry(new SupplyDropEntry(reward));
                            reward.giveReward.accept(databasePlayerPvE);
                            if (rewardsGained == amount) {
                                PLAYER_ROLL_COOLDOWN.put(uuid, false);
                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                cancel();
                            }
                        }
                    }
                }
            }.runTaskTimer(Warlords.getInstance(), 0, 0);
        });
    }

}
