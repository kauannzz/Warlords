package com.ebicep.warlords.commands.debugcommands.ingame;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.player.WarlordsEntity;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DebugModeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("warlords.game.debugmode")) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }

        WarlordsEntity warlordsPlayer = BaseCommand.requireWarlordsPlayer(sender);
        if (warlordsPlayer == null) {
            sender.sendMessage("§cYou are not in a game");
        }
        if (!warlordsPlayer.getGame().getAddons().contains(GameAddon.PRIVATE_GAME)) {
            sender.sendMessage("§cDebug commands are disabled in public games!");
            return true;
        }

        warlordsPlayer.setNoEnergyConsumption(true);
        warlordsPlayer.setDisableCooldowns(true);
        warlordsPlayer.setTakeDamage(false);
        warlordsPlayer.sendMessage(ChatColor.GREEN + "You now have infinite energy, no cooldowns, and take no damage!");

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("debugmode").setExecutor(this);
    }
}
