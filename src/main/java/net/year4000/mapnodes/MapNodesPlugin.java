package net.year4000.mapnodes;

import lombok.Getter;
import net.year4000.mapnodes.addons.Addons;
import net.year4000.mapnodes.addons.modules.misc.DeathMessages;
import net.year4000.mapnodes.addons.modules.mapnodes.Internals;
import net.year4000.mapnodes.addons.modules.spectator.*;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.Plugin;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.game.Node;
import net.year4000.mapnodes.game.WorldManager;
import net.year4000.mapnodes.map.MapFactory;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.utilities.bukkit.BukkitPlugin;
import net.year4000.utilities.bukkit.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import java.util.List;

@Getter
public class MapNodesPlugin extends BukkitPlugin implements Plugin {
    @Getter
    private static MapNodesPlugin inst = null;
    private Addons addons = new Addons();

    @Override
    public void onLoad() {
        inst = this;
        MapNodes.init(inst);

        // Clean out old maps
        WorldManager.removeStrayMaps();

        // Load new maps
        new MapFactory();
    }

    @Override
    public void onEnable() {
        List<Node> maps = NodeFactory.get().getAllGames();

        // Disable if no loaded maps
        if (maps.size() == 0) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Generate all the games
        maps.forEach(node -> log(
            Msg.util("debug.map.ready"),
            node.getMatch().getGame().getMap().getName(),
            node.getMatch().getGame().getMap().getVersion()
        ));

        // Addons (The internal system that loads addons)
        // The order is the dependency list
        addons.builder()
            .add(Internals.class)
            .add(GameMenu.class)
            .add(PlayerMenu.class)
            .add(GameServers.class)
            .add(MapBook.class)
            .add(OpenInventories.class)
            .add(DeathMessages.class)
            .register();
    }

    @Override
    public void onDisable() {
        // Tasks that must happen when the plugin loaded with maps
        if (NodeFactory.get().getAllGames().size() > 0) {
            MapNodes.getCurrentGame().getPlayers().forEach(p -> {
                p.getPlayer().kickPlayer(MessageUtil.message(Msg.locale(p, "clocks.restart.last")));
                log(p.getPlayer().getName() + " " + Msg.locale(p, "clocks.restart.last"));
            });

            NodeFactory.get().getCurrentGame().unregister();

            addons.builder().unregister();
        }

        // Tasks that can be ran with out plugin loaded
        Bukkit.getScheduler().cancelTasks(this);
        Bukkit.shutdown();
    }

    /*//----------------------------//
         Current Node Quick Methods
    *///----------------------------//

    @Override
    public GameManager getCurrentGame() {
        return NodeFactory.get().getCurrentGame().getMatch().getGame();
    }

    @Override
    public World getCurrentWorld() {
        return NodeFactory.get().getCurrentGame().getWorld().getWorld();
    }
}