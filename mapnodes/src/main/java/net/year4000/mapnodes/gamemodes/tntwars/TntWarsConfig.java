/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.gamemodes.tntwars;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import net.year4000.mapnodes.api.exceptions.InvalidJsonException;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.api.game.modes.GameModeConfig;
import net.year4000.mapnodes.api.game.modes.GameModeConfigName;
import net.year4000.mapnodes.api.game.regions.Region;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

@Data
@GameModeConfigName("tnt_wars")
public class TntWarsConfig implements GameModeConfig {
    @SerializedName("max_score")
    private Integer maxScore = null;

    private List<Island> islands = new ArrayList<>();

    @Override
    public void validate() throws InvalidJsonException {
        checkArgument(maxScore != null, Msg.util("deathmatch.error.max_score_null"));

        if (maxScore != null) {
            checkArgument(maxScore > 0, Msg.util("deathmatch.error.max_score"));
        }
    }

    /** Get the owner's island */
    public Island getIsland(String owner) {
        for (Island island : islands) {
            if (island.getOwner().equalsIgnoreCase(owner) || island.getRegion().equalsIgnoreCase(owner)) {
                return island;
            }
        }

        throw new IllegalArgumentException(owner + " island not found!");
    }

    @Data
    public class Island {
        private String owner;
        private String region;

        /** The damage of the island in float */
        private transient float percent = 100;
        private transient GameTeam team;
        private transient Set<Region> regionObject;
        private transient int initSize;
        private transient int count;

        public void initIsland(GameManager game) {
            team = game.getTeams().get(owner);
            regionObject = game.getRegions().get(region).getZones();
            initSize = (int) regionObject.stream().map(Region::getPoints).map(List::size).count();
            count = initSize;
            updatePercent(0);
        }

        public void updatePercent(int amount) {
            count -= amount;
            percent = 100 - ((count / initSize) * (float) -0.1);
            percent = percent < 0 ? 0 : percent;
            // MapNodesPlugin.debug("Island Percent: " + percent);
        }

        public String getPercent() {
            return Common.colorNumber((int) percent, 100) + "%%";
        }

        public String getDisplay() {
            return " " + getPercent() + " " + team.getDisplayName();
        }

        public String getId() {
            return owner + "-" + region;
        }
    }
}