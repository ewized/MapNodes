/*
 * Copyright 2016 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.google.common.collect.ImmutableSet;
import net.year4000.mapnodes.nodes.Node;
import net.year4000.mapnodes.nodes.NodeFactory;
import net.year4000.mapnodes.nodes.NodeManager;
import net.year4000.utilities.ErrorReporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/** The system to handle the Maps and load their games */
public interface MapNodes {
  Settings SETTINGS = new Settings();
  NodeManager NODE_MANAGER = new NodeManager();

  /** Get the bindings */
  Bindings bindings();

  /** Get the node factory */
  NodeFactory nodeFactory();

  /** Get the current node */
  default Node currentNode() {
    return NODE_MANAGER.getNode();
  }

  /** Load the MapNodes system */
  default void load() {
    // Inject the javascript files into v8
    System.out.println("Loading javascript files into v8 runtime");
    ImmutableSet.of("bindings.js", "game.js", "player.js", "team.js", "utils.js").forEach(file -> {
      System.out.println("Loading javascript file: " + file);
      InputStream stream = MapNodes.class.getResourceAsStream("/js/" + file);
      try (BufferedReader buffer = new BufferedReader(new InputStreamReader(stream))) {
        String script = buffer.lines().collect(Collectors.joining("\n"));
        bindings().v8().executeVoidScript(script);
      } catch (IOException error) {
        ErrorReporter.builder(error).add("file", file).buildAndReport(System.err);
      }
    });
    // Generate the maps
    System.out.println("Generating map packages");
    nodeFactory().generatePackages();
  }

  /** Enable the MapNodes system */
  default void enable() {
    System.out.println("Adding maps to queue");
    nodeFactory().packages().forEach(map -> {
      System.out.println("Adding map: " + map.toString());
      // todo exception handling
      Node node = nodeFactory().create(map);
      NODE_MANAGER.addToQueue(node);
    });
    // Set up the first node if we can
    if (nodeFactory().packages().size() > 0) {
      System.out.println("Creating first node");
      NODE_MANAGER.loadNextNode();
    }
  }

  /** Unload the MapNodes system */
  default void unload() {
    // Unload the last map in the system
    if (nodeFactory().packages().size() > 0) {
      System.out.println("Unloading current node");
      currentNode().unload();
    }
    System.out.println("Releasing runtime bindings");
    bindings().release();
  }
}