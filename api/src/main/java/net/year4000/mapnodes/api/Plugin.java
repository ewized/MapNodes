/*
 * Copyright 2015 Year4000. All Rights Reserved.
 */

package net.year4000.mapnodes.api;

import com.comphenix.protocol.ProtocolManager;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.utilities.LogUtil;
import net.year4000.utilities.bukkit.gui.GUIManager;
import org.bukkit.World;

public interface Plugin {
    /** Get the current game */
    public GameManager getCurrentGame();

    /** Get the current game world */
    public World getCurrentWorld();

    /** Get the log util to log things */
    public LogUtil getLogUtil();

    /** Grab the protocol manager for MapNodes */
    ProtocolManager getProtocolManager();

    /** Get the GUI Manager for MapNodes */
    GUIManager getGui();
}
