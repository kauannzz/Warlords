package com.ebicep.warlords.pve.events.mastersworkfair;

import com.ebicep.customentities.npc.traits.MasterworksFairTrait;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFairPlayerEntry;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.IntStream;

public class MasterworksFairMenu {

    public static void openMasterworksFairMenu(Player player) {
        if (MasterworksFairManager.currentFair == null) {
            if (MasterworksFairTrait.startTime != null) {
                player.sendMessage(Component.text("The Masterworks Fair is starting soon!", NamedTextColor.RED));
            } else {
                player.sendMessage(Component.text("The Masterworks Fair is currently closed!", NamedTextColor.RED));
            }
            return;
        }

        Menu menu = new Menu("Masterworks Fair", 9 * 6);
        UUID uuid = player.getUniqueId();
        DatabaseManager.getPlayer(uuid, databasePlayer -> {
            DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();
            List<AbstractWeapon> weaponInventory = databasePlayerPvE.getWeaponInventory();

            WeaponsPvE[] values = WeaponsPvE.VALUES;
            int column = 2;
            for (WeaponsPvE value : values) {
                if (value.getPlayerEntries == null) {
                    continue;
                }
                List<MasterworksFairPlayerEntry> weaponPlayerEntries = value.getPlayerEntries.apply(MasterworksFairManager.currentFair);
                Optional<MasterworksFairPlayerEntry> playerEntry = weaponPlayerEntries.stream()
                                                                                      .filter(masterworksFairPlayerEntry -> masterworksFairPlayerEntry.getUuid()
                                                                                                                                                      .equals(uuid))
                                                                                      .findFirst();

                ItemBuilder itemBuilder;
                if (playerEntry.isEmpty()) {
                    itemBuilder = new ItemBuilder(value.glassItem);
                    itemBuilder.name(Component.text("Click to submit a " + value.name + " weapon", NamedTextColor.GREEN));
                } else {
                    itemBuilder = new ItemBuilder(playerEntry.get().getWeapon().generateItemStack(false));
                    itemBuilder.addLore(
                            "",
                            ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to change your submission"
                    );
                }
                menu.setItem(column, 2,
                        itemBuilder.get(),
                        (m, e) -> openSubmissionMenu(player, databasePlayer, value, 1)
                );

                //last 10 placements
                List<MasterworksFairEntry> masterworksFairEntries = databasePlayerPvE.getMasterworksFairEntries();
                List<String> placementHistory = masterworksFairEntries
                        .stream()
                        .filter(masterworksFairEntry -> masterworksFairEntry.getRarity() == value)
                        .collect(Utils.lastN(10))
                        .stream()
                        .map(masterworksFairEntry -> ChatColor.GRAY + MasterworksFairManager.FORMATTER.format(masterworksFairEntry.getTime()) + ": " + value.chatColor + "#" + masterworksFairEntry.getPlacement() + ChatColor.GRAY + " - " + ChatColor.YELLOW + masterworksFairEntry.getScore() + "\n")
                        .toList();
                menu.setItem(column, 3,
                        new ItemBuilder(Material.BOOK)
                                .name(Component.text("Your most recent placements", NamedTextColor.GREEN))
                                .loreLEGACY(IntStream.range(0, placementHistory.size())
                                                     .mapToObj(index -> placementHistory.get(placementHistory.size() - index - 1))
                                                     .toList())
                                .get(), (m, e) -> {

                        }
                );
                column += 2;
            }

            ItemBuilder infoItemBuilder = new ItemBuilder(Material.FIREWORK_ROCKET)
                    .name(Component.text("Current Submissions", NamedTextColor.GREEN));
            List<String> infoLore = new ArrayList<>();
            for (WeaponsPvE value : values) {
                if (value.getPlayerEntries != null) {
                    List<MasterworksFairPlayerEntry> weaponPlayerEntries = value.getPlayerEntries.apply(MasterworksFairManager.currentFair);
                    infoLore.add(value.getChatColorName() + ": " + ChatColor.AQUA + weaponPlayerEntries.size());
                }
            }
            infoItemBuilder.loreLEGACY(infoLore);
            menu.setItem(4, 0, infoItemBuilder.get(), (m, e) -> {
            });

            menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        });
    }

    public static void openSubmissionMenu(Player player, DatabasePlayer databasePlayer, WeaponsPvE weaponType, int page) {
        Menu menu = new Menu("Choose a weapon", 9 * 6);
        UUID uuid = player.getUniqueId();
        List<AbstractWeapon> weaponInventory = databasePlayer.getPveStats().getWeaponInventory();
        List<AbstractWeapon> filteredWeaponInventory = new ArrayList<>(weaponInventory);
        filteredWeaponInventory.removeIf(weapon -> weapon.getRarity() != weaponType);
        filteredWeaponInventory.sort(WeaponManagerMenu.SortOptions.WEAPON_SCORE.comparator.reversed());

        List<MasterworksFairPlayerEntry> weaponPlayerEntries = weaponType.getPlayerEntries.apply(MasterworksFairManager.currentFair);
        Optional<MasterworksFairPlayerEntry> playerEntry = weaponPlayerEntries.stream()
                                                                              .filter(masterworksFairPlayerEntry -> masterworksFairPlayerEntry.getUuid()
                                                                                                                                              .equals(uuid))
                                                                              .findFirst();

        for (int i = 0; i < 45; i++) {
            int weaponNumber = ((page - 1) * 45) + i;
            if (weaponNumber < filteredWeaponInventory.size()) {
                AbstractWeapon weapon = filteredWeaponInventory.get(weaponNumber);

                int column = i % 9;
                int row = i / 9;

                menu.setItem(
                        column,
                        row,
                        weapon.generateItemStack(false),
                        (m, e) -> {
                            //check bound
                            if (weapon.isBound()) {
                                MasterworksFairManager.sendMasterworksFairMessage(player, ChatColor.RED + "You cannot submit a bound weapon. Unbind it first!");
                                return;
                            }
                            Menu.openConfirmationMenu0(
                                    player,
                                    "Submit Weapon",
                                    3,
                                    Arrays.asList(
                                            Component.text("Submit " + weapon.getName(), NamedTextColor.GRAY),
                                            Component.text("to the Masterworks Fair?", NamedTextColor.GRAY),
                                            Component.empty(),
                                            Component.textOfChildren(
                                                    Component.text("WARNING: ", NamedTextColor.RED),
                                                    Component.text("This will override any previous", NamedTextColor.GRAY)
                                            ),
                                            Component.text("weapon and you cannot get this weapon back!", NamedTextColor.GRAY)
                                    ),
                                    Menu.GO_BACK,
                                    (m2, e2) -> {
                                        //submit weapon to fair
                                        MasterworksFairPlayerEntry masterworksFairPlayerEntry = playerEntry.orElseGet(() -> new MasterworksFairPlayerEntry(uuid));
                                        if (playerEntry.isEmpty()) {
                                            //add new entry if there wasnt already one
                                            weaponPlayerEntries.add(masterworksFairPlayerEntry);
                                        }
                                        //remove new weapon
                                        weaponInventory.remove(weapon);
                                        //set new weapon
                                        masterworksFairPlayerEntry.setWeapon(weapon);

                                        //update database stuff
                                        MasterworksFairManager.updateFair.set(true);
                                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                                        MasterworksFairManager.sendMasterworksFairMessage(player,
                                                Component.text(ChatColor.GRAY + "Submitted ")
                                                         .append(weapon.getHoverComponent(false))
                                                         .append(Component.text(ChatColor.GRAY + " to the Masterworks Fair!"))
                                        );

                                        openMasterworksFairMenu(player);
                                    },
                                    (m2, e2) -> openSubmissionMenu(player, databasePlayer, weaponType, page),
                                    (m2) -> {
                                        m2.setItem(4, 1,
                                                weapon.generateItemStack(false),
                                                (m3, e3) -> {
                                                }
                                        );
                                    }
                            );
                        }
                );
            }
        }

        if (page - 1 > 0) {
            menu.setItem(0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Previous Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page - 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> {
                        openSubmissionMenu(player, databasePlayer, weaponType, page - 1);
                    }
            );
        }
        if (filteredWeaponInventory.size() > (page * 45)) {
            menu.setItem(8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Next Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page + 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> {
                        openSubmissionMenu(player, databasePlayer, weaponType, page + 1);
                    }
            );
        }

        menu.setItem(4, 5, Menu.MENU_BACK, (m, e) -> openMasterworksFairMenu(player));
        menu.openForPlayer(player);
    }

}
