package fun.bharatmc.bmcCore.managers;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.models.PlayerData;
import org.bukkit.entity.Player;

public class FlyManager {
    private final BMCCore plugin;
    private final PlayerManager playerManager;

    public FlyManager(BMCCore plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getPlayerManager();
    }

    public boolean toggleFly(Player player) {
        PlayerData playerData = playerManager.getPlayerData(player);
        boolean isFlying = playerData.isFlying();

        if (isFlying) {
            // Disable fly
            disableFly(player);
            playerData.setFlying(false);
        } else {
            // Enable fly
            enableFly(player);
            playerData.setFlying(true);
        }

        // Save to database
        playerManager.savePlayerData(player);

        return !isFlying; // Return new state
    }

    public boolean setFly(Player player, boolean flying) {
        PlayerData playerData = playerManager.getPlayerData(player);

        if (flying) {
            enableFly(player);
        } else {
            disableFly(player);
        }

        playerData.setFlying(flying);
        playerManager.savePlayerData(player);
        return flying;
    }

    public boolean setFlySpeed(Player player, float speed) {
        // Convert to Minecraft's fly speed format (0.1 to 1.0)
        float minecraftSpeed = Math.max(0.1f, Math.min(1.0f, speed));

        PlayerData playerData = playerManager.getPlayerData(player);
        playerData.setFlySpeed(minecraftSpeed);

        // Apply the speed if player is flying
        if (player.isFlying() || playerData.isFlying()) {
            player.setFlySpeed(minecraftSpeed);
        }

        playerManager.savePlayerData(player);
        return true;
    }

    public float getFlySpeed(Player player) {
        PlayerData playerData = playerManager.getPlayerData(player);
        return playerData.getFlySpeed();
    }

    public boolean isFlying(Player player) {
        PlayerData playerData = playerManager.getPlayerData(player);
        return playerData.isFlying();
    }

    private void enableFly(Player player) {
        // Set allow flight and enable flight
        player.setAllowFlight(true);
        player.setFlying(true);

        // Apply their saved fly speed
        PlayerData playerData = playerManager.getPlayerData(player);
        player.setFlySpeed(playerData.getFlySpeed());
    }

    private void disableFly(Player player) {
        player.setFlying(false);
        player.setAllowFlight(false);
    }

    public void handlePlayerJoin(Player player) {
        PlayerData playerData = playerManager.getPlayerData(player);

        // If player had fly enabled, re-enable it
        if (playerData.isFlying()) {
            // Small delay to ensure player is fully loaded
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                enableFly(player);
            }, 10L);
        }
    }
}