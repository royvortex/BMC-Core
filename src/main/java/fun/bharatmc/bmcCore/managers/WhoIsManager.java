package fun.bharatmc.bmcCore.managers;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class WhoIsManager {
    private final BMCCore plugin;
    private final VanishManager vanishManager;
    private final FlyManager flyManager;
    private final GodManager godManager;
    private final FreezeManager freezeManager;
    private final PlayerManager playerManager;
    private final DatabaseManager databaseManager;

    public WhoIsManager(BMCCore plugin) {
        this.plugin = plugin;
        this.vanishManager = plugin.getVanishManager();
        this.flyManager = plugin.getFlyManager();
        this.godManager = plugin.getGodManager();
        this.freezeManager = plugin.getFreezeManager();
        this.playerManager = plugin.getPlayerManager();
        this.databaseManager = plugin.getDatabaseManager();
    }

    public String getWhoIsInfo(String targetName) {
        // Try to find online player first
        Player onlinePlayer = Bukkit.getPlayer(targetName);
        if (onlinePlayer != null) {
            return getOnlineWhoIsInfo(onlinePlayer);
        }

        // If not online, try offline player
        return getOfflineWhoIsInfo(targetName);
    }

    private String getOnlineWhoIsInfo(Player target) {
        StringBuilder info = new StringBuilder();

        // Header
        info.append("§6§lWhoIs: §e").append(target.getName()).append(" §a(Online)").append("\n");
        info.append("§8§m--------------------------------\n");

        // Basic Information
        info.append("§6UUID: §f").append(target.getUniqueId()).append("\n");
        info.append("§6Display Name: §f").append(target.getDisplayName()).append("\n");

        // IP Address (Staff only)
        String ip = getPlayerIP(target);
        info.append("§6IP Address: §f").append(ip).append("\n");

        // Location Information
        Location loc = target.getLocation();
        info.append("§6Location: §f").append(formatLocation(loc)).append("\n");
        info.append("§6World: §f").append(loc.getWorld().getName()).append("\n");
        info.append("§6Biome: §f").append(loc.getBlock().getBiome().toString()).append("\n");

        // Game State
        info.append("§6Gamemode: §f").append(target.getGameMode().toString()).append("\n");
        info.append("§6Health: §f").append(String.format("%.1f", target.getHealth())).append(" / ").append(String.format("%.1f", target.getMaxHealth())).append("\n");
        info.append("§6Hunger: §f").append(target.getFoodLevel()).append(" / 20\n");
        info.append("§6Level: §f").append(target.getLevel()).append("\n");
        info.append("§6XP: §f").append(String.format("%.1f", target.getExp())).append("\n");

        // Player State (from our systems)
        info.append("§6Operator: §f").append(target.isOp() ? "Yes" : "No").append("\n");
        info.append("§6Flying: §f").append(flyManager.isFlying(target) ? "Yes" : "No").append("\n");
        info.append("§6God Mode: §f").append(godManager.isGodMode(target) ? "Yes" : "No").append("\n");
        info.append("§6Vanished: §f").append(vanishManager.isVanished(target) ? "Yes" : "No").append("\n");
        info.append("§6Frozen: §f").append(freezeManager.isFrozen(target) ? "Yes" : "No").append("\n");

        // Connection Information
        info.append("§6Ping: §f").append(target.getPing()).append("ms\n");
        info.append("§6First Played: §f").append(formatDate(target.getFirstPlayed())).append("\n");
        info.append("§6Last Seen: §f").append("Now (Online)").append("\n");
        info.append("§6Player Time: §f").append(formatPlayerTime(target.getPlayerTime())).append("\n");

        // Additional Metadata
        info.append("§6Walking Speed: §f").append(String.format("%.2f", target.getWalkSpeed())).append("\n");
        info.append("§6Flying Speed: §f").append(String.format("%.2f", target.getFlySpeed())).append("\n");
        info.append("§6Allow Flight: §f").append(target.getAllowFlight() ? "Yes" : "No").append("\n");

        // Footer
        info.append("§8§m--------------------------------");

        return info.toString();
    }

    private String getOfflineWhoIsInfo(String playerName) {
        StringBuilder info = new StringBuilder();

        // Try to find offline player by name
        OfflinePlayer offlinePlayer = findOfflinePlayer(playerName);

        if (offlinePlayer == null) {
            return "§cPlayer not found: " + playerName + "\n§7This player has never joined the server.";
        }

        // Try to load player data from database
        PlayerData playerData = databaseManager.loadPlayerData(offlinePlayer.getUniqueId());

        // Header
        info.append("§6§lWhoIs: §e").append(offlinePlayer.getName()).append(" §c(Offline)").append("\n");
        info.append("§8§m--------------------------------\n");

        // Basic Information
        info.append("§6UUID: §f").append(offlinePlayer.getUniqueId()).append("\n");
        info.append("§6Name: §f").append(offlinePlayer.getName()).append("\n");

        // IP Address - Not available for offline players
        info.append("§6IP Address: §f").append("Unknown (Offline)").append("\n");

        // Player Data from Database
        if (playerData != null) {
            info.append("§6Last Known Location: §f").append(formatLocation(playerData.getLastLocation())).append("\n");
            info.append("§6Last Known World: §f").append(playerData.getLastLocation() != null ?
                    playerData.getLastLocation().getWorld().getName() : "Unknown").append("\n");

            // Player State from database
            info.append("§6Flying: §f").append(playerData.isFlying() ? "Yes" : "No").append("\n");
            info.append("§6God Mode: §f").append(playerData.isGodMode() ? "Yes" : "No").append("\n");
            info.append("§6Vanished: §f").append(playerData.isVanished() ? "Yes" : "No").append("\n");
            info.append("§6Frozen: §f").append(playerData.isFrozen() ? "Yes" : "No").append("\n");
            info.append("§6Fly Speed: §f").append(String.format("%.2f", playerData.getFlySpeed())).append("\n");
        } else {
            info.append("§6Data Status: §cNo player data found in database\n");
        }

        // Offline Player Information
        info.append("§6Operator: §f").append(offlinePlayer.isOp() ? "Yes" : "No").append("\n");
        info.append("§6First Played: §f").append(formatDate(offlinePlayer.getFirstPlayed())).append("\n");
        info.append("§6Last Seen: §f").append(formatDate(offlinePlayer.getLastSeen())).append("\n");
        info.append("§6Banned: §f").append(offlinePlayer.isBanned() ? "Yes" : "No").append("\n");
        info.append("§6Whitelisted: §f").append(offlinePlayer.isWhitelisted() ? "Yes" : "No").append("\n");

        // Player Data Timestamps
        if (playerData != null) {
            info.append("§6First Join: §f").append(formatDate(playerData.getFirstJoin())).append("\n");
            info.append("§6Last Login: §f").append(formatDate(playerData.getLastLogin())).append("\n");
        }

        // Footer
        info.append("§8§m--------------------------------");
        info.append("\n§7Note: Some information may be outdated for offline players.");

        return info.toString();
    }

    private OfflinePlayer findOfflinePlayer(String playerName) {
        // First, check if player is in the offline players cache
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();

        for (OfflinePlayer player : offlinePlayers) {
            if (player.getName() != null && player.getName().equalsIgnoreCase(playerName)) {
                return player;
            }
        }

        // If not found by exact name, try partial match
        for (OfflinePlayer player : offlinePlayers) {
            if (player.getName() != null && player.getName().toLowerCase().contains(playerName.toLowerCase())) {
                return player;
            }
        }

        return null;
    }

    public List<String> getAllPlayerNames() {
        Set<String> playerNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        // Add online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerNames.add(player.getName());
        }

        // Add offline players (recent ones to avoid too many)
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();

        // Sort by last played (most recent first) and take top 100 to avoid huge lists
        List<OfflinePlayer> recentPlayers = Arrays.stream(offlinePlayers)
                .sorted((p1, p2) -> Long.compare(p2.getLastSeen(), p1.getLastSeen()))
                .limit(100)
                .collect(Collectors.toList());

        for (OfflinePlayer player : recentPlayers) {
            if (player.getName() != null) {
                playerNames.add(player.getName());
            }
        }

        return new ArrayList<>(playerNames);
    }

    private String getPlayerIP(Player player) {
        try {
            InetSocketAddress address = player.getAddress();
            if (address != null) {
                String ip = address.getAddress().getHostAddress();
                // Mask part of the IP for privacy (e.g., 192.168.xxx.xxx)
                String[] parts = ip.split("\\.");
                if (parts.length == 4) {
                    return parts[0] + "." + parts[1] + ".xxx.xxx";
                }
                return ip;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get IP for player: " + player.getName());
        }
        return "Unknown";
    }

    private String formatLocation(Location location) {
        if (location == null) {
            return "Unknown";
        }
        return String.format("X: %.1f, Y: %.1f, Z: %.1f",
                location.getX(), location.getY(), location.getZ());
    }

    private String formatDate(long timestamp) {
        if (timestamp == 0) return "Never";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }

    private String formatPlayerTime(long time) {
        // Convert ticks to readable time
        long hours = (time / 1000 + 6) % 24;
        long minutes = (time % 1000) * 60 / 1000;

        return String.format("%02d:%02d", hours, minutes);
    }

    public String getQuickInfo(String targetName) {
        Player onlinePlayer = Bukkit.getPlayer(targetName);
        if (onlinePlayer != null) {
            return String.format(
                    "§6%s §a(Online) §7- §fGM: %s §7| §fHealth: %.1f §7| §fLocation: %s §7| §fIP: %s",
                    onlinePlayer.getName(),
                    onlinePlayer.getGameMode().toString(),
                    onlinePlayer.getHealth(),
                    formatLocation(onlinePlayer.getLocation()),
                    getPlayerIP(onlinePlayer)
            );
        } else {
            OfflinePlayer offlinePlayer = findOfflinePlayer(targetName);
            if (offlinePlayer != null) {
                return String.format(
                        "§6%s §c(Offline) §7- §fLast Seen: %s §7| §fBanned: %s",
                        offlinePlayer.getName(),
                        formatDate(offlinePlayer.getLastSeen()),
                        offlinePlayer.isBanned() ? "Yes" : "No"
                );
            } else {
                return "§cPlayer not found: " + targetName;
            }
        }
    }

    public boolean isPlayerOnline(String playerName) {
        return Bukkit.getPlayer(playerName) != null;
    }

    public Player getPlayer(String playerName) {
        return Bukkit.getPlayer(playerName);
    }
}