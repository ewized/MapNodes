package net.year4000.mapnodes.commands.node;

import net.year4000.mapnodes.NodeFactory;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.game.GameStage;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.exceptions.WorldLoadException;
import net.year4000.mapnodes.game.Node;
import net.year4000.mapnodes.map.MapFactory;
import net.year4000.mapnodes.messages.Msg;
import net.year4000.utilities.bukkit.MessageUtil;
import net.year4000.utilities.bukkit.commands.Command;
import net.year4000.utilities.bukkit.commands.CommandContext;
import net.year4000.utilities.bukkit.commands.CommandException;
import org.bukkit.command.CommandSender;

public final class NodeSub {
    @Command(
        aliases = {"purge"},
        max = 0,
        desc = "Purge all node queued maps to allow for a restart."
    )
    public static void node(CommandContext args, CommandSender sender) throws CommandException {
        GameStage stage = MapNodes.getCurrentGame().getStage();

        if (stage.isEndGame()) {
            throw new CommandException(MessageUtil.message(Msg.locale(sender, "cmd.node.no")));
        }

        NodeFactory.get().getQueueNodes().clear();
        sender.sendMessage(MessageUtil.message(Msg.locale(sender, "cmd.node.purge")));
    }

    @Command(
        aliases = {"add"},
        max = 1,
        min = 1,
        desc = "Add a map to the node queue."
    )
    public static void add(CommandContext args, CommandSender sender) throws CommandException {
        GameStage stage = MapNodes.getCurrentGame().getStage();

        if (stage.isEndGame()) {
            throw new CommandException(MessageUtil.message(Msg.locale(sender, "cmd.node.no")));
        }

        String mapName = args.getString(0);

        if (MapFactory.isMap(mapName)) {
            try {
                NodeFactory.get().addMap(MapFactory.getMap(mapName));
                sender.sendMessage(MessageUtil.message(Msg.locale(sender, "cmd.node.add"), mapName));
            } catch (InvalidJsonException | WorldLoadException e) {
                throw new CommandException(e.getMessage());
            }
        }
        else {
            throw new CommandException(MessageUtil.message(Msg.locale(sender, "cmd.node.add.empty"), mapName));
        }
    }

    @Command(
        aliases = {"remove"},
        max = 1,
        desc = "Remove x maps from node queue."
    )
    public static void remove(CommandContext args, CommandSender sender) throws CommandException {
        GameStage stage = MapNodes.getCurrentGame().getStage();

        if (stage.isEndGame()) {
            throw new CommandException(MessageUtil.message(Msg.locale(sender, "cmd.node.no")));
        }

        if (args.argsLength() > 0 && args.getInteger(0) >= NodeFactory.get().getQueueNodes().size()) {
            throw new CommandException(MessageUtil.message(Msg.locale(sender, "cmd.node.remove.bounds")));
        }

        int limit = args.argsLength() > 0 ? args.getInteger(0) : 1;

        for (int i = 0; i < limit; i++) {
            Node node = NodeFactory.get().getQueueNodes().poll();
            sender.sendMessage(MessageUtil.message(
                Msg.locale(sender, "cmd.node.remove"),
                node.getMatch().getGame().getMap().getName()
            ));
        }
    }

}