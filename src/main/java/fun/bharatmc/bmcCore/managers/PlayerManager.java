package fun.bharatmc.bmcCore.managers;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.models.PlayerData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private final BMCCore plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private final DatabaseManager databaseManager;

    public PlayerManager(BMCCore plugin) {
        this.plugin = plugin;
        this.playerDataMap = new HashMap<>();
        this.databaseManager = plugin.getDatabaseManager();
    }

    public PlayerData getPlayerData(Player player) {
        return playerDataMap.computeIfAbsent(player.getUniqueId(), uuid -> {
            // Try to load from database
            PlayerData data = databaseManager.loadPlayerData(uuid);
            if (data == null) {
                // Create new player data
                data = new PlayerData(uuid, player.getName());
                databaseManager.savePlayerData(data);
            } else {
                // Update player name if changed
                data.setPlayerName(player.getName());
            }
            return data;
        });
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.get(uuid);
    }

    public void savePlayerData(Player player) {
        PlayerData data = playerDataMap.get(player.getUniqueId());
        if (data != null) {
            databaseManager.savePlayerData(data);
        }
    }

    public void saveAllData() {
        plugin.getLogger().info("Saving all player data to database...");
        for (PlayerData data : playerDataMap.values()) {
            databaseManager.savePlayerData(data);
        }
        plugin.getLogger().info("Saved data for " + playerDataMap.size() + " players");
    }

    public void removePlayerData(Player player) {
        savePlayerData(player);
        playerDataMap.remove(player.getUniqueId());
    }

    public boolean isPlayerVanished(Player player) {
        PlayerData data = getPlayerData(player);
        return data != null && data.isVanished();
    }

    public void setPlayerVanished(Player player, boolean vanished) {
        PlayerData data = getPlayerData(player);
        if (data != null) {
            data.setVanished(vanished);
            databaseManager.savePlayerData(data);
        }
    }
    public boolean isPlayerFrozen(Player player) {
        PlayerData data = getPlayerData(player);
        return data != null && data.isFrozen();
    }

    public void setPlayerFrozen(Player player, boolean frozen) {
        PlayerData data = getPlayerData(player);
        if (data != null) {
            data.setFrozen(frozen);
            savePlayerData(player);
        }
    }
}