package com.ebicep.warlords.pve.bountysystem.bounties;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounties;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class Kill500 extends AbstractBounty implements TracksPostGame, DailyRewardSpendable1 {

    private static final int TARGET_KILLS = 500;
    private int kills = 0;

    @Nullable
    @Override
    public Component getProgress() {
        if (kills >= TARGET_KILLS) {
            return null;
        }
        return getProgress(kills, TARGET_KILLS);
    }

    @Override
    public String getDescription() {
        return "Kill " + TARGET_KILLS + " enemies in any gamemode.";
    }

    @Override
    public Bounties getBounty() {
        return Bounties.KILL500;
    }

    @Override
    public void onGameEnd(Game game, WarlordsPlayer warlordsPlayer) {
        kills += warlordsPlayer.getMinuteStats().total().getKills();
    }

}
