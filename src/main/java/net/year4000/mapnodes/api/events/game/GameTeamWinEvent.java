package net.year4000.mapnodes.api.events.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.year4000.mapnodes.api.game.GameTeam;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class GameTeamWinEvent extends GameWinEvent {
    private GameTeam winner;
}
