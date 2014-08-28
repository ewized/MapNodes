package net.year4000.mapnodes.api.game;

import net.year4000.mapnodes.game.components.NodeKit;
import net.year4000.mapnodes.game.components.NodeTeam;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.stream.Stream;

public interface GameManager {
    /**
     * Get the info of the map from the json settings.
     * @return The map settings.
     */
    public GameMap getMap();

    /**
     * Get the map config settings.
     * @return The map config.
     */
    public GameConfig getConfig();

    public Map<String, NodeKit> getKits();

    public Map<String, NodeTeam> getTeams();

    public Stream<GamePlayer> getPlayers();

    public Stream<GamePlayer> getPlaying();

    public Stream<GamePlayer> getSpectating();

    public Stream<GamePlayer> getEntering();

    public GamePlayer getPlayer(Player player);

    public int getMaxPlayers();

    public GameStage getStage();
}