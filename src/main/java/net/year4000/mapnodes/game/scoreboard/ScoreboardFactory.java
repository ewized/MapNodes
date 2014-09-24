package net.year4000.mapnodes.game.scoreboard;

import lombok.AllArgsConstructor;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.game.NodeTeam;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.utilities.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

@AllArgsConstructor
public class ScoreboardFactory {
    private static final ScoreboardManager manager = Bukkit.getScoreboardManager();
    private final NodeGame game;

    /** Create a scoreboard for the player */
    public Scoreboard createScoreboard(NodePlayer player) {
        Scoreboard scoreboard = manager.getNewScoreboard();

        // Assign default teams (used for seeing invisible and friendly fire)
        game.getTeams().values().forEach(nodeTeam -> {
            Team team = scoreboard.registerNewTeam(nodeTeam.getName());
            team.setAllowFriendlyFire(nodeTeam.isAllowFriendlyFire());
            team.setCanSeeFriendlyInvisibles(nodeTeam.isCanSeeFriendlyInvisibles());
            team.setPrefix(nodeTeam.getColor().toString());
            team.setSuffix(ChatColor.RESET.toString());
        });

        return scoreboard;
    }

    /** Set the team of the player for the player's personal scoreboard */
    public void setTeam(NodePlayer nodePlayer, NodeTeam nodeTeam) {
        // Set all other players to know about the player
        game.getPlayers().forEach(player -> {
            // Remove Player
            ((NodePlayer) player).getScoreboard().getTeams().stream()
                .forEach(team -> team.removePlayer(nodePlayer.getPlayer()));

            // Add Player
            ((NodePlayer) player).getScoreboard().getTeams().stream()
                .filter(team -> team.getName().equals(nodeTeam.getName()))
                .forEach(team -> team.addPlayer(nodePlayer.getPlayer()));

            nodePlayer.getScoreboard().getTeam(player.getTeam().getName()).addPlayer(player.getPlayer());

        });
    }

    public void setPersonalSidebar(NodePlayer nodePlayer) {
        String queue = nodePlayer.getTeam().getQueue().contains(nodePlayer) ? Msg.locale(nodePlayer, "team.queue") : "";
        SidebarManager side = new SidebarManager()
            .addLine(Msg.locale(nodePlayer, "team.name"))
            .addLine("  " + nodePlayer.getTeam().getDisplayName() + " " + queue)
            ;

        side.buildSidebar(nodePlayer.getScoreboard(), "&3&l   [&b&lYear4000&3&l]   ");
    }

    public void setGameSidebar(NodePlayer nodePlayer) {
        // TODO Get data from current game
        SidebarManager side = new SidebarManager()
            .addLine("&cRed Score&7:", 34)
            .addLine("&bBlue Score&7:", 23)
            .addBlank()
            .addLine("&cRed Towers&7:")
            .addLine("  &6Right Tower")
            .addLine("  &6Left Tower")
            .addBlank()
            .addLine("&bBlue Towers&7:")
            .addLine("  &6Left Tower")
            .addLine("  &6Right Tower")
            ;

        side.buildSidebar(nodePlayer.getScoreboard(), "&b" + nodePlayer.getGame().getMap().getName());
    }
}