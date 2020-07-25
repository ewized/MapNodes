/*
 * Copyright 2017 Year4000. All Rights Reserved.
 */
package net.year4000.mapnodes;

import com.google.inject.Inject;
import net.year4000.mapnodes.nodes.SpongeNode;
import net.year4000.mapnodes.webpackdev.WebpackDevClient;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.SpongeExecutorService;

import static net.year4000.mapnodes.MapNodes.NODE_MANAGER;

/** Sponge plugin to provide support for the MapNodes system */
@Plugin(id = "mapnodes_debug", name = "MapNodesDebug", dependencies = {@Dependency(id = "mapnodes")})
public class MapNodesDebugPlugin {

  /** The logger injected from Sponge */
  @Inject private Logger logger;

  @Listener
  public void onEnable(GameAboutToStartServerEvent event) {
    // go to the next game
    Sponge.getCommandManager().register(this, CommandSpec.builder().executor((src, args) -> {
      try {
        SpongeNode node = (SpongeNode) NODE_MANAGER.getNode();
        node.gameManager().cycle();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return CommandResult.success();
    }).build(), "next");

    // start the game
    Sponge.getCommandManager().register(this, CommandSpec.builder().executor((src, args) -> {
      try {
        SpongeNode node = (SpongeNode) NODE_MANAGER.getNode();
        node.gameManager().start();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return CommandResult.success();
    }).build(), "start");
  }

  @Listener(order = Order.LAST)
  public void onStart(GameConstructionEvent event) throws Exception {
    SpongeExecutorService sync = Sponge.getScheduler().createSyncExecutor(this);
    new WebpackDevClient("host.docker.internal" ,3001).open((data, error) -> {
      System.out.println("HOT UPDATE");
      //System.out.println(data);
      //System.out.println(error);
      String update = "((modules) => {" +
        "  Object.entries(modules).forEach(([key, value]) => { \n" +
        "    console.log('updating ' + key); \n" +
        //"    delete global.webpack.c[key]; \n" + // module cache
        //"    global.webpack.m[key] = value; \n" +
        //"    global.webpack(key); \n" +
        "  })" +
        "})(\n\n" + data.get().substring(0, data.get().lastIndexOf(";")) + "\n\n);";
      sync.submit(() -> MapNodesPlugin.get().bindings().v8().executeScript(update));
    }, ((data, error) -> {
      System.out.println("FULL UPDATE");
      System.out.println(data);
      System.out.println(error);
    }));
  }

}
