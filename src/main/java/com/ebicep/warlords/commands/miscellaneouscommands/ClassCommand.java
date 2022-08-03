package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.menu.PlayerHotBarItemListener;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("class")
@CommandPermission("warlords.game.changeclass")
public class ClassCommand extends BaseCommand {

    @Default
    @Description("Change your class")
    public void changeClass(@Conditions("outsideGame") Player player, Specializations spec) {
        PlayerSettings settings = Warlords.getPlayerSettings(player.getUniqueId());
        settings.setSelectedSpec(spec);
        player.sendMessage(ChatColor.BLUE + "Your selected spec: §7" + spec);
        PlayerHotBarItemListener.updateWeaponManagerItem(player, spec);
    }

}
