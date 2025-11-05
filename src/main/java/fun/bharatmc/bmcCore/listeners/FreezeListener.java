package fun.bharatmc.bmcCore.listeners;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.managers.FreezeManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Arrays;
import java.util.List;

public class FreezeListener implements Listener {
    private final BMCCore plugin;
    private final FreezeManager freezeManager;

    // Hardcoded allowed commands for frozen players
    private final List<String> ALLOWED_COMMANDS = Arrays.asList(
            "/msg", "/tell", "/whisper", "/r", "/reply",
            "/help", "/?"
    );

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

                // Send movement blocked message (but not too spammy)
                if (Math.random() < 0.1) { // 10% chance to show message to avoid spam
                    player.sendMessage("§cYou cannot move while frozen!");
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (freezeManager.isFrozen(player)) {
            String message = event.getMessage().toLowerCase();
            String command = message.split(" ")[0]; // Get the first word (the command)

            // Check if this is an allowed command
            boolean allowed = false;
            for (String allowedCmd : ALLOWED_COMMANDS) {
                if (message.startsWith(allowedCmd.toLowerCase())) {
                    allowed = true;
                    break;
                }
            }

            // Also allow the player to check their own freeze status
            if (command.equals("/freeze") && message.split(" ").length == 1) {
                allowed = true;
            }

            if (!allowed) {
                // Block the command
                event.setCancelled(true);
                player.sendMessage("§cYou cannot use commands while frozen!");
                player.sendMessage("§7Allowed commands: /msg, /tell, /r, /help");

                // Log the blocked command for staff
                plugin.getLogger().info("FROZEN BLOCKED CMD: " + player.getName() + " tried: " + event.getMessage());
            }
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