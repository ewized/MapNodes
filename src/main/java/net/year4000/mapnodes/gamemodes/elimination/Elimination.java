package net.year4000.mapnodes.gamemodes.elimination;

import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameLoadEvent;
import net.year4000.mapnodes.api.events.game.GameStartEvent;
import net.year4000.mapnodes.api.events.player.*;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.api.game.GameTeam;
import net.year4000.mapnodes.api.game.modes.GameMode;
import net.year4000.mapnodes.api.game.modes.GameModeInfo;
import net.year4000.mapnodes.api.utils.Spectator;
import net.year4000.mapnodes.gamemodes.GameModeTemplate;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.mapnodes.utils.Common;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.*;
import java.util.stream.Collectors;

@GameModeInfo(
    name = "Elimination",
    version = "1.0",
    config = EliminationConfig.class
)
public class Elimination extends GameModeTemplate implements GameMode {
    protected GameManager game;
    protected GameTeam team;
    protected Iterator<Location> spawns;
    protected List<String> alive = new ArrayList<>();
    protected List<String> dead = new ArrayList<>();
    private EliminationConfig gameModeConfig;

    @EventHandler
    public void onLoad(GameLoadEvent event) {
        gameModeConfig = getConfig();
        game = event.getGame();

        if (gameModeConfig.getPlayersTeam() == null) {
            team = game.getPlayingTeams().collect(Collectors.toList()).iterator().next();
        }
        else {
            team = game.getTeams().get(gameModeConfig.getPlayersTeam());
        }

        if (team.getSize() == 1) {
            team.setSize(team.getSpawns().size());
        }

        team.setAllowFriendlyFire(true); // Force enable so players can kill each other
        game.addStartTime(10);

        // Add requirements | Their must be at least 2 players to start
        game.getStartControls().add(() -> team.getPlayers().size() >= gameModeConfig.getStartSize());

        // Create the iterator of spawns
        List<Location> shuffledSpawns = new ArrayList<>(team.getSpawns());
        Collections.shuffle(shuffledSpawns);

        spawns = shuffledSpawns.iterator();
    }

    /** Set the player's spawn to the next spawn in the list */
    @EventHandler
    public void onPlayerJoin(GamePlayerStartEvent event) {
        if (event.getPlayer().getTeam() instanceof Spectator) return;

        event.setImmortal(false);
        event.setSpawn(spawns.next());
    }

    /** Teleport the player to the start if they died */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (dead.contains(event.getPlayer().getName()) && game.getPlayer(event.getPlayer()).getTeam() instanceof Spectator) {
            event.setRespawnLocation(game.getConfig().getSafeRandomSpawn());
        }
    }

    /** Don't allow players to select teams when the game started */
    @EventHandler
    public void onTeamSelect(GamePlayerJoinTeamEvent event) {
        if (event.getTo() instanceof Spectator) return;

        if (MapNodes.getCurrentGame().getStage().isPlaying()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Msg.NOTICE + Msg.locale(event.getPlayer(), "elimination.join.playing"));
        }
    }

    /** Disable menu when game started */
    @EventHandler
    public void onPlayerJoin(GamePlayerJoinEvent event) {
        if (MapNodes.getCurrentGame().getStage().isPlaying()) {
            event.setMenu(false);
        }
    }

    /** Set up the players in the game */
    @EventHandler
    public void onGameStart(GameStartEvent event) {
        new ArrayList<>(team.getQueue()).forEach(GamePlayer::joinSpectatorTeam);
        alive.addAll(team.getPlayers().stream().map(player -> player.getPlayer().getName()).collect(Collectors.toList()));
        buildAndSendList();

        if (alive.size() <= 1 && !MapNodesPlugin.getInst().isDebug()) {
            game.stop();
        }
    }

    /** Show death message to all game players */
    @EventHandler
    public void onPlayerDeath(GamePlayerDeathEvent event) {
        event.getViewers().addAll(game.getPlayers().collect(Collectors.toList()));
    }

    /** Handle raw player death's */
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        deadPlayer(event.getEntity());
    }

    /** When players leave count that as a death */
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        deadPlayer(event.getPlayer());
    }

    /** When players leave count that as a death */
    @EventHandler
    public void onSwitchTeam(GamePlayerJoinSpectatorEvent event) {
        deadPlayer(event.getPlayer().getPlayer());
    }

    /** Handle the dead player */
    public void deadPlayer(Player name) {
        if (!game.getStage().isPlaying()) return;

        if (alive.size() > 1 && alive.contains(name.getName())) {
            alive.remove(name.getName());
            dead.add(name.getName());
            buildAndSendList();
            int eliminationSize = alive.size() - 1;

            game.getPlayers().forEach(player -> {
                if (eliminationSize > 1) {
                    Common.sendAnimatedActionBar(player, Msg.locale(player, "elimination.eliminated.plural", name.getName(), String.valueOf(eliminationSize)));
                }
                else if (eliminationSize == 1) {
                    Common.sendAnimatedActionBar(player, Msg.locale(player, "elimination.eliminated.one", name.getName(), String.valueOf(eliminationSize)));
                }
            });

            if (game.getPlayer(name) != null) {
                GamePlayer player = game.getPlayer(name);
                player.getPlayerTasks().add(SchedulerUtil.runSync(player::joinSpectatorTeam, 5L));
            }
        }

        if (alive.size() == 1) {
            SchedulerUtil.runSync(() -> {
                try {
                    new GamePlayerWinEvent(game, game.getPlayer(Bukkit.getPlayer(alive.iterator().next()))).call();
                }
                catch (NoSuchElementException e) {
                    game.stop();
                }
            }, 8L);
        }
        else if (alive.size() == 0) {
            game.stop();
        }
    }

    /** Build the sidebar and send it to the players */
    public void buildAndSendList() {
        game.getSidebarGoals().clear();

        if (alive.size() + dead.size() > 16) {
            game.addDynamicGoal("alive", MessageUtil.replaceColors("&6Alive&7:"), alive.size());
            game.addDynamicGoal("dead", MessageUtil.replaceColors("&6Dead&7:"), dead.size());
        }
        else {
            alive.forEach(name -> game.addStaticGoal(name, "&a" + name));
            dead.forEach(name -> game.addStaticGoal(name, "&c&m" + name));
        }
    }
}
