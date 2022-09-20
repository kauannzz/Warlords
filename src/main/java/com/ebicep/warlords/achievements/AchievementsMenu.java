package com.ebicep.warlords.achievements;

import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.achievements.types.TieredAchievements;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.*;

public class AchievementsMenu {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm");

    //GENERAL - CTF - TDM - GAMEMODE - GAMEMODE
    //TIERED ACHIEVEMENTS - CHALLENGES
    //ACHIEVEMENT HISTORY
    public static void openAchievementsMenu(Player player) {
        Menu menu = new Menu("Achievements", 9 * 4);

        menu.setItem(
                1,
                1,
                new ItemBuilder(Material.STONE_AXE)
                        .name(ChatColor.GREEN + "General")
                        .get(),
                (m, e) -> openAchievementTypeMenu(player, null)
        );

        int x = 0;
        for (GameMode gameMode : GameMode.VALUES) {
            if (gameMode.getItemStack() == null) {
                continue;
            }
            menu.setItem(
                    x + 1,
                    1,
                    new ItemBuilder(gameMode.getItemStack())
                            .name(ChatColor.GREEN + gameMode.getName())
                            .get(),
                    (m, e) -> openAchievementTypeMenu(player, gameMode)
            );
            x++;
        }

        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openAchievementTypeMenu(Player player, GameMode gameMode) {
        Menu menu = new Menu("Achievements - " + (gameMode == null ? "General" : gameMode.getName()), 9 * 4);

        menu.setItem(
                2,
                1,
                new ItemBuilder(Material.DIAMOND)
                        .name(ChatColor.GREEN + "Challenge Achievements")
                        .get(),
                (m, e) -> openAchievementsGameModeMenu(player,
                        gameMode,
                        "Challenge Achievements",
                        ChallengeAchievements.ChallengeAchievementRecord.class,
                        ChallengeAchievements.VALUES
                )
        );
        menu.setItem(
                6,
                1,
                new ItemBuilder(Material.DIAMOND_BLOCK)
                        .name(ChatColor.GREEN + "Tiered Achievements")
                        .get(),
                (m, e) -> openAchievementsGameModeMenu(player,
                        gameMode,
                        "Tiered Achievements",
                        TieredAchievements.TieredAchievementRecord.class,
                        TieredAchievements.VALUES
                )
        );

        menu.setItem(3, 3, MENU_BACK, (m, e) -> openAchievementsMenu(player));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static <T extends Achievement.AbstractAchievementRecord<R>, R extends Enum<R> & Achievement> void openAchievementsGameModeMenu(
            Player player,
            GameMode gameMode,
            String menuName,
            Class<T> recordClass,
            R[] enumsValues
    ) {
        if (DatabaseManager.playerService == null) {
            return;
        }
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        List<R> unlockedAchievements = databasePlayer.getAchievements().stream()
                .filter(recordClass::isInstance)
                .map(recordClass::cast)
                .map(Achievement.AbstractAchievementRecord::getAchievement)
                .collect(Collectors.toList());

        Menu menu = new Menu(menuName + " - " + gameMode.abbreviation, 9 * 6);
        int x = 0;
        int y = 0;
        for (R achievement : enumsValues) {
            if (achievement.getGameMode() != gameMode) {
                continue;
            }
            boolean hasAchievement = unlockedAchievements.contains(achievement);
            boolean shouldObfuscate = !hasAchievement && achievement.isHidden();
            ItemBuilder itemBuilder = new ItemBuilder(hasAchievement ? Material.WATER_BUCKET : Material.BUCKET)
                    .name(ChatColor.GREEN.toString() + (shouldObfuscate ? ChatColor.MAGIC : "") + achievement.getName())
                    .flags(ItemFlag.HIDE_ENCHANTS);
            if (!achievement.getDescription().isEmpty()) {
                itemBuilder.lore(WordWrap.wrapWithNewline(achievement.getDescription(),
                        160,
                        ChatColor.GRAY.toString() + (shouldObfuscate ? ChatColor.MAGIC : "")
                ));
            }
            itemBuilder.addLore(ChatColor.GREEN + (shouldObfuscate ?
                    ChatColor.MAGIC + "\nSpec:" + ChatColor.RESET + " " + ChatColor.GOLD + ChatColor.MAGIC + "hiddenSpec"
                    :
                    "\nSpec: " + ChatColor.GOLD + (achievement.getSpec() != null ? achievement.getSpec().name : "Any")));
            if (hasAchievement) {
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
            }
            menu.setItem(
                    x,
                    y,
                    itemBuilder.get(),
                    (m, e) -> {
                        if (hasAchievement) {
                            openAchievementHistoryMenu(player,
                                    recordClass,
                                    achievement,
                                    (m2, e2) -> openAchievementsGameModeMenu(player, gameMode, menuName,
                                            recordClass, enumsValues
                                    )
                            );
                        }
                    }
            );

            x++;
            if (x == 9) {
                x = 0;
                y++;
            }
        }


        menu.setItem(3, 5, MENU_BACK, (m, e) -> openAchievementTypeMenu(player, gameMode));
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static <T extends Achievement.AbstractAchievementRecord<R>, R extends Enum<R> & Achievement> void openAchievementHistoryMenu(
            Player player,
            Class<T> recordClass,
            R achievement,
            BiConsumer<Menu, InventoryClickEvent> menuBack
    ) {
        if (DatabaseManager.playerService == null) {
            return;
        }
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        List<T> achievementRecords = databasePlayer.getAchievements().stream()
                .filter(recordClass::isInstance)
                .map(recordClass::cast)
                .filter(t -> t.getAchievement() == achievement)
                .collect(Collectors.toList());

        Menu menu = new Menu("Achievement History ", 9 * 6);

        int x = 0;
        int y = 0;
        for (T achievementRecord : achievementRecords) {

            menu.setItem(
                    x,
                    y,
                    new ItemBuilder(Material.BOOK)
                            .name(ChatColor.GREEN + achievement.getName())
                            .lore(ChatColor.GRAY + DATE_FORMAT.format(achievementRecord.getDate()))
                            .get(),
                    (m, e) -> {
                    }
            );

            x++;
            if (x == 8) {
                x = 0;
                y++;
            }
        }

        menu.setItem(3, 5, MENU_BACK, menuBack);
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

}
