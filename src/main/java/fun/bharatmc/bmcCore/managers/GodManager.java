package fun.bharatmc.bmcCore.managers;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.models.PlayerData;
import org.bukkit.entity.Player;

public class GodManager {
    private final BMCCore plugin;
    private final PlayerManager playerManager;

    public GodManager(BMCCore plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getPlayerManager();
    }

    public boolean toggleGodMode(Player player) {
        PlayerData playerData = playerManager.getPlayerData(player);
        boolean isGodMode = playerData.isGodMode();

        if (isGodMode) {
            // Disable god mode
            disableGodMode(player);
            playerData.setGodMode(false);
        } else {
            // Enable god mode
            enableGodMode(player);
            playerData.setGodMode(true);
        }

        // Save to database
        playerManager.savePlayerData(player);

        return !isGodMode; // Return new state
    }

    public boolean setGodMode(Player player, boolean godMode) {
        PlayerData playerData = playerManager.getPlayerData(player);

        if (godMode) {
            enableGodMode(player);
        } else {
            disableGodMode(player);
        }

        playerData.setGodMode(godMode);
        playerManager.savePlayerData(player);
        return godMode;
    }

    public boolean isGodMode(Player player) {
        PlayerData playerData = playerManager.getPlayerData(player);
        return playerData.isGodMode();
    }

    private void enableGodMode(Player player) {
        // Set god mode effects
        player.setInvulnerable(true);
        player.setFireTicks(0); // Extinguish fire

        // Send message (will be handled by command, but we can log it)
        plugin.getLogger().info("God mode enabled for " + player.getName());
    }

    private void disableGodMode(Player player) {
        // Remove god mode effects
        player.setInvulnerable(false);

        // Send message (will be handled by command, but we can log it)
        plugin.getLogger().info("God mode disabled for " + player.getName());
    }

    public void handlePlayerJoin(Player player) {
        PlayerData playerData = playerManager.getPlayerData(player);

        // If player had god mode enabled, re-enable it
        if (playerData.isGodMode()) {
            // Small delay to ensure player is fully loaded
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                enableGodMode(player);
            }, 10L);
        }
    }

    public void cleanup() {
        // Disable god mode for all online players on plugin disable
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (isGodMode(player)) {
                disableGodMode(player);
            }
        }
    }
}