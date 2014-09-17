package net.year4000.mapnodes.listeners;

import net.year4000.mapnodes.NodeFactory;
import net.year4000.mapnodes.api.MapNodes;
import net.year4000.mapnodes.api.events.game.GameStartEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinSpectatorEvent;
import net.year4000.mapnodes.api.events.player.GamePlayerJoinTeamEvent;
import net.year4000.mapnodes.api.game.GameManager;
import net.year4000.mapnodes.api.game.GamePlayer;
import net.year4000.mapnodes.game.NodeGame;
import net.year4000.mapnodes.game.NodeKit;
import net.year4000.mapnodes.game.NodePlayer;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.bukkit.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class SpectatorListener implements Listener {
    /** Stop the event if the player is not playing the game */
    private void stopEvent(Cancellable event, Player player) {
        if (!MapNodes.getCurrentGame().getPlayer(player).isPlaying()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDrop(PlayerDropItemEvent event) {
        stopEvent(event, event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        stopEvent(event, event.getPlayer());

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onInteract(EntityInteractEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        stopEvent(event, (Player) event.getEntity());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onIvnInteract(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        stopEvent(event, (Player) event.getWhoClicked());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        stopEvent(event, (Player) event.getDamager());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPickUp(PlayerPickupItemEvent event) {
        stopEvent(event, event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPickUp(PlayerPickupExperienceEvent event) {
        stopEvent(event, event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onDamage(EntityDamageEvent event) {
        // If not a player don't check
        if (!(event.getEntity() instanceof Player)) return;

        // If not playing send player back to spawn
        if (!MapNodes.getCurrentGame().getPlayer((Player) event.getEntity()).isPlaying()) {
            // If the damage is void reset player
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                event.getEntity().teleport(((NodeGame) MapNodes.getCurrentGame()).getConfig().getSafeRandomSpawn());
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onMove(PlayerMoveEvent event) {
        // Help the spectator ignore side effects
        if (!MapNodes.getCurrentGame().getPlayer(event.getPlayer()).isPlaying()) {
            event.getPlayer().setFireTicks(0);
            event.getPlayer().setFoodLevel(20);
        }
    }

    // Open Player's Inventory //

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            GamePlayer player = MapNodes.getCurrentGame().getPlayer((Player) event.getEntity());
            ((NodePlayer) player).updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerDamage(InventoryInteractEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            GamePlayer player = MapNodes.getCurrentGame().getPlayer((Player) event.getWhoClicked());
            ((NodePlayer) player).updateInventory();
        }
    }

/*    @EventHandler(priority = EventPriority.MONITOR)
    public void playerDamage(PlayerEvent event) {
        GamePlayer player = MapNodes.getCurrentGame().getPlayer(event.getPlayer());
        ((NodePlayer) player).updateInventory();
    }*/

    @EventHandler(priority = EventPriority.HIGHEST)
    public void openInv(PlayerInteractEntityEvent event) {
        GameManager gm = MapNodes.getCurrentGame();
        GamePlayer gPlayer = gm.getPlayer(event.getPlayer());

        if (gPlayer.isSpectator()) {
            if (event.getRightClicked() instanceof Player) {
                GamePlayer rightClicked = gm.getPlayer((Player) event.getRightClicked());

                if (!rightClicked.isSpectator()) {
                    gPlayer.getPlayer().openInventory(rightClicked.getInventory());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void openInv(PlayerInteractEvent event) {
        GameManager gm = MapNodes.getCurrentGame();
        GamePlayer gPlayer = gm.getPlayer(event.getPlayer());

        if (gPlayer.isSpectator() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            if (block.getState() instanceof InventoryHolder) {
                SchedulerUtil.runSync(() -> {
                    Inventory inv = ((InventoryHolder) block.getState()).getInventory();

                    // Create a fake inventory so the chest don't really open
                    Inventory fake = Bukkit.createInventory(null, inv.getSize(), inv.getTitle());
                    fake.setContents(inv.getContents());

                    gPlayer.getPlayer().openInventory(fake);
                });
            }
        }
    }

    // Map Book //

    public ItemStack book(GamePlayer player) {
        GameManager gm = MapNodes.getCurrentGame();

        return ItemUtil.createBook(
            "&b" + gm.getMap().getName(),
            "&5&o" + gm.getMap().getMainAuthor(),
            ((NodeGame) gm).getBookPages(player.getPlayer())
        );
    }

    @EventHandler
    public void onTeamJoin(GamePlayerJoinTeamEvent e) {
        GameManager gm = MapNodes.getCurrentGame();

        gm.getSpectating().parallel().forEach(player -> {
            Inventory inv = player.getPlayer().getInventory();

            inv.setItem(3, book(player));
        });
    }

    @EventHandler
    public void onGameStart(GameStartEvent e) {
        e.getGame().getSpectating().parallel().forEach(player -> {
            Inventory inv = player.getPlayer().getInventory();

            inv.setItem(3, book(player));
        });
    }

    @EventHandler
    public void onJoin(GamePlayerJoinSpectatorEvent e) {
        NodeKit kit = (NodeKit) e.getKit();
        GameManager gm = MapNodes.getCurrentGame();

        // Book
        kit.getItems().set(3, book(e.getPlayer()));
        // Servers
        kit.getItems().set(4, ItemUtil.makeItem("nether_star", "{'display':{'name':'&aGame Servers'}}"));
        // Game Menu
        kit.getItems().set(0, ItemUtil.makeItem("eye_of_ender", "{'display':{'name':'&6Game Menu'}}"));

        // Map icon
        if (NodeFactory.get().getCurrentGame().getMatch().getIcon() != null) {
            MapView view = Bukkit.createMap(MapNodes.getCurrentWorld());
            view.addRenderer(new MapRenderer() {
                @Override
                public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
                    mapView.setCenterX(Integer.MAX_VALUE);
                    mapView.setCenterZ(Integer.MAX_VALUE);
                    mapCanvas.drawImage(0,0, MapPalette.resizeImage(NodeFactory.get().getCurrentGame().getMatch().getIconImage()));
                }
            });
            short damage = view.getId();
            ItemStack item = ItemUtil.makeItem("map", 1, damage);
            item.setItemMeta(ItemUtil.addMeta(item, "{'display':{'name':'&3" + gm.getMap().getName() + "'}}"));
            kit.getItems().set(2, item);
        }
    }
}
