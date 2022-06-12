package com.ebicep.warlords.game.option.marker.scoreboard;

import com.ebicep.warlords.player.WarlordsEntity;
import javax.annotation.Nullable;


public abstract class SimpleScoreboardHandler extends AbstractScoreboardHandler {
    
    protected int priority;
    protected String group;

    public SimpleScoreboardHandler(int priority, String group) {
        this.priority = priority;
        this.group = group;
    }

    @Override
    public int getPriority(@Nullable WarlordsEntity player) {
        return priority;
    }

    @Override
    public String getGroup() {
        return group;
    }

}
