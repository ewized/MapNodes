package net.year4000.mapnodes.clocks;

import com.ewized.utilities.bukkit.util.FunEffectsUtil;
import net.year4000.mapnodes.MapNodes;
import net.year4000.mapnodes.configs.Messages;
import net.year4000.mapnodes.game.GameManager;
import net.year4000.mapnodes.game.GamePlayer;
import net.year4000.mapnodes.game.GameStage;
import net.year4000.mapnodes.utils.BarAPI;
import net.year4000.mapnodes.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

public class RestartClock extends Clocker {
    public RestartClock() {
        super(20); // Restart the server with 20 delay.
    }

    /** Code to ran each clock tock. */
    public void runTock(int position) {
        GameManager gm = WorldManager.get().getCurrentGame();

        Bukkit.getConsoleSender().sendMessage(String.format(Messages.get("clock-restart"), position));
        for (GamePlayer player : gm.getPlayers().values()) {
            FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_PLING);

            BarAPI.removeBar(player.getPlayer());
            BarAPI.setMessage(
                player.getPlayer(),
                String.format(Messages.get(player.getPlayer().getLocale(), "clock-restart"), position),
                (float) ((double) position / (double) getTime()) * 100
            );
        }
    }

    /** Code to be ran on the last clock tick. */
    public void runLast(int position) {
        GameManager gm = WorldManager.get().getCurrentGame();

        Bukkit.getConsoleSender().sendMessage(Messages.get("clock-restart-last"));
        for (GamePlayer player : gm.getPlayers().values()) {
            FunEffectsUtil.playSound(player.getPlayer(), Sound.NOTE_BASS);

            BarAPI.removeBar(player.getPlayer());
            BarAPI.setMessage(player.getPlayer(), Messages.get(player.getPlayer().getLocale(), "clock-restart-last"), 1);
        }

        Bukkit.getScheduler().runTask(MapNodes.getInst(), () -> gm.setStage(GameStage.ENDED));
    }
}