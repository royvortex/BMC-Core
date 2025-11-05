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
        frozenPlayers.put(player.getUniqueId(), player.getLocation());

        // Apply freeze effects
        applyFreezeEffects(player);

        // Send freeze message
        player.sendMessage("§cYou have been frozen! You cannot move.");

        plugin.getLogger().info("Froze player: " + player.getName());
    }

    private void unfreezePlayer(Player player) {
        // Remove from frozen players map
        frozenPlayers.remove(player.getUniqueId());

        // Remove freeze effects
        removeFreezeEffects(player);

        // Send unfreeze message
        player.sendMessage("§aYou have been unfrozen! You can move again.");

        plugin.getLogger().info("Unfroze player: " + player.getName());
    }

    private void applyFreezeEffects(Player player) {
        // Cancel any existing flight
        player.setFlying(false);
        player.setAllowFlight(false);

        // Add visual effects (particles would be handled in listener)
        player.sendTitle("§c❄️ FROZEN ❄️", "§7You cannot move", 10, 40, 10);
    }

    private void removeFreezeEffects(Player player) {
        // Remove visual effects
        player.sendTitle("§a❄️ UNFROZEN ❄️", "§7You can move again", 10, 40, 10);
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
            }, 10L);
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