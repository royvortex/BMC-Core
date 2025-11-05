package fun.bharatmc.bmcCore.managers;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FreezeManager {
    private final BMCCore plugin;
    private final PlayerManager playerManager;
    private final Map<UUID, Location> frozenPlayers; // Store freeze locations

    public FreezeManager(BMCCore plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getPlayerManager();
        this.frozenPlayers = new HashMap<>();
        loadFrozenPlayers();
    }

    private void loadFrozenPlayers() {
        // Load players who were frozen from database
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (playerManager.isPlayerFrozen(player)) {
                frozenPlayers.put(player.getUniqueId(), player.getLocation());
                applyFreezeEffects(player);
            }
        }
    }

    public boolean toggleFreeze(Player target, Player executor) {
        PlayerData playerData = playerManager.getPlayerData(target);
        boolean isFrozen = playerData.isFrozen();

        if (isFrozen) {
            // Unfreeze player
            unfreezePlayer(target);
            playerData.setFrozen(false);
        } else {
            // Freeze player
            freezePlayer(target);
            playerData.setFrozen(true);
        }

        // Save to database
        playerManager.savePlayerData(target);

        return !isFrozen; // Return new state
    }

    public boolean setFrozen(Player player, boolean frozen) {
        PlayerData playerData = playerManager.getPlayerData(player);

        if (frozen) {
            freezePlayer(player);
        } else {
            unfreezePlayer(player);
        }

        playerData.setFrozen(frozen);
        playerManager.savePlayerData(player);
        return frozen;
    }

    private void freezePlayer(Player player) {
        // Store current location
        frozenPlayers.put(player.getUniqueId(), player.getLocation().clone()); // Use clone to avoid reference issues

        // Apply freeze effects
        applyFreezeEffects(player);

        // Send freeze message with clear instructions
        player.sendMessage("§c§l❄ YOU HAVE BEEN FROZEN! ❄");
        player.sendMessage("§7You cannot move, teleport, or use most commands.");
        player.sendMessage("§7Allowed commands: §f/msg, /tell, /r, /help");
        player.sendMessage("§7Staff will contact you shortly.");

        plugin.getLogger().info("Froze player: " + player.getName());

        // Notify online staff
        notifyStaff(player, true);
    }

    private void unfreezePlayer(Player player) {
        // Remove from frozen players map
        frozenPlayers.remove(player.getUniqueId());

        // Remove freeze effects
        removeFreezeEffects(player);

        // Send unfreeze message
        player.sendMessage("§a§l❄ YOU HAVE BEEN UNFROZEN! ❄");
        player.sendMessage("§aYou can now move and use commands again.");

        plugin.getLogger().info("Unfroze player: " + player.getName());

        // Notify online staff
        notifyStaff(player, false);
    }

    private void applyFreezeEffects(Player player) {
        // Cancel any existing flight
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setWalkSpeed(0.0f); // Set walk speed to 0

        // Add visual effects
        player.sendTitle("§c❄ FROZEN ❄", "§7You cannot move or use commands", 10, 60, 20);

        // Send action bar message
        sendActionBar(player, "§c❄ You are frozen! Use /help for allowed commands");
    }

    private void removeFreezeEffects(Player player) {
        // Remove visual effects
        player.sendTitle("§a❄ UNFROZEN ❄", "§7You can move again", 10, 40, 10);

        // Restore normal walk speed
        player.setWalkSpeed(0.2f); // Default walk speed

        // Clear action bar
        sendActionBar(player, "");
    }

    private void sendActionBar(Player player, String message) {
        try {
            player.sendActionBar(message);
        } catch (Exception e) {
            // Fallback if action bar isn't available
            player.sendMessage(message);
        }
    }

    private void notifyStaff(Player target, boolean frozen) {
        String message = frozen ?
                "§6[Staff] §c" + target.getName() + " has been frozen!" :
                "§6[Staff] §a" + target.getName() + " has been unfrozen!";

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("bmccore.freeze") && !player.equals(target)) {
                player.sendMessage(message);
            }
        }

        // Also log to console
        if (frozen) {
            plugin.getLogger().warning("PLAYER FROZEN: " + target.getName() + " at " + target.getLocation());
        } else {
            plugin.getLogger().info("PLAYER UNFROZEN: " + target.getName());
        }
    }

    public boolean isFrozen(Player player) {
        PlayerData playerData = playerManager.getPlayerData(player);
        return playerData != null && playerData.isFrozen();
    }

    public Location getFreezeLocation(Player player) {
        return frozenPlayers.get(player.getUniqueId());
    }

    public void updateFreezeLocation(Player player, Location location) {
        if (isFrozen(player)) {
            frozenPlayers.put(player.getUniqueId(), location);
        }
    }

    public void handlePlayerJoin(Player player) {
        PlayerData playerData = playerManager.getPlayerData(player);

        // If player was frozen, reapply effects
        if (playerData.isFrozen()) {
            frozenPlayers.put(player.getUniqueId(), player.getLocation());
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                applyFreezeEffects(player);
                player.sendMessage("§c§l⚠ You are still frozen from your previous session!");
                player.sendMessage("§7Please wait for staff to unfreeze you.");
            }, 20L); // 1 second delay to ensure player is fully loaded
        }
    }

    public void handlePlayerQuit(Player player) {
        frozenPlayers.remove(player.getUniqueId());
    }

    public void cleanup() {
        // Unfreeze all players on plugin disable
        for (UUID playerUUID : frozenPlayers.keySet()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                unfreezePlayer(player);
            }
        }
        frozenPlayers.clear();
    }

    public Map<UUID, Location> getFrozenPlayers() {
        return new HashMap<>(frozenPlayers);
    }
}