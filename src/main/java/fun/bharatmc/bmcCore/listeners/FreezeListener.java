package fun.bharatmc.bmcCore.listeners;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.managers.FreezeManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class FreezeListener implements Listener {
    private final BMCCore plugin;
    private final FreezeManager freezeManager;

    public FreezeListener(BMCCore plugin) {
        this.plugin = plugin;
        this.freezeManager = plugin.getFreezeManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (freezeManager.isFrozen(player)) {
            // Check if player actually moved (not just rotated)
            Location from = event.getFrom();
            Location to = event.getTo();

            if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
                // Cancel the movement
                event.setCancelled(true);

                // Teleport them back to their freeze location if they moved
                Location freezeLocation = freezeManager.getFreezeLocation(player);
                if (freezeLocation != null && !from.getBlock().equals(freezeLocation.getBlock())) {
                    player.teleport(freezeLocation);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        if (freezeManager.isFrozen(player)) {
            // Cancel teleportation for frozen players
            event.setCancelled(true);
            player.sendMessage("§cYou cannot teleport while frozen!");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Handle freeze for joining player
        freezeManager.handlePlayerJoin(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Handle freeze for quitting player
        freezeManager.handlePlayerQuit(player);
    }
}