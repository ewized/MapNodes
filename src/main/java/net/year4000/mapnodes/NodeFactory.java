package net.year4000.mapnodes;

import lombok.Getter;
import net.year4000.mapnodes.exceptions.InvalidJsonException;
import net.year4000.mapnodes.exceptions.WorldLoadException;
import net.year4000.mapnodes.game.Node;
import net.year4000.mapnodes.map.MapFactory;
import net.year4000.mapnodes.map.MapFolder;
import net.year4000.mapnodes.messages.Msg;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class NodeFactory {
    private static NodeFactory inst;

    /** The current playing game */
    private Node currentNode;

    /** The games loaded into the queue, thread safe */
    @Getter
    private final Queue<Node> queueNodes = new LinkedBlockingQueue<>();

    /** Internal game id counter */
    private int gameID = 0;

    private NodeFactory() {
        MapFactory.getMaps(Settings.get().getLoadMaps()).forEach(world -> {
            try {
                addMap(world);
            } catch (InvalidJsonException | WorldLoadException e) {
                MapNodesPlugin.log(e, true);
            }
        });
    }

    public void addMap(MapFolder world) throws InvalidJsonException, WorldLoadException {
        queueNodes.add(new Node(getGameID(), world));
    }

    public static NodeFactory get() {
        if (inst == null) {
            inst = new NodeFactory();
        }
        return inst;
    }

    /** Create and return a game id for the node */
    private int getGameID() {
        return ++gameID;
    }

    /** Get a list of all que games and current game */
    public List<Node> getAllGames() {
        return new ArrayList<Node>() {{
            try {
                add(getCurrentGame());
                addAll(queueNodes);
            } catch (NullPointerException e) {
                MapNodesPlugin.debug(Msg.util("error.world.none"));
            }
        }};
    }

    /** Get the current node */
    public Node getCurrentGame() {
        if (currentNode == null) {
            currentNode = loadNextQueued();
        }

        return currentNode;
    }

    /** Load the next game */
    public Node loadNextQueued() {
        if (currentNode != null) {
            currentNode.unregister();
        }

        Node newNode = queueNodes.poll();
        currentNode = newNode;

        newNode.register();

        return currentNode;
    }

    /** Peek at the next node */
    public Node peekNextQueued() {
        if (isQueuedGames()) {
            return queueNodes.peek();
        }

        return null;
    }

    /** Is there queued games */
    public boolean isQueuedGames() {
        return queueNodes.size() > 0;
    }

}
