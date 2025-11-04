package fun.bharatmc.bmcCore.managers;

import fun.bharatmc.bmcCore.BMCCore;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GamemodeManager {
    private final BMCCore plugin;

    public GamemodeManager(BMCCore plugin) {
        this.plugin = plugin;
    }

    public boolean setGameMode(Player player, GameMode gameMode) {
        if (player == null || gameMode == null) {
            return false;
        }

        try {
            player.setGameMode(gameMode);
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to set gamemode for " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }

    public boolean setGameMode(Player target, GameMode gameMode, Player executor) {
        if (target == null || gameMode == null) {
            return false;
        }

        // Check if executor has permission to change other players' gamemode
        if (executor != null && !executor.hasPermission("bmccore.gamemode.others") && !executor.equals(target)) {
            return false;
        }

        return setGameMode(target, gameMode);
    }

    public GameMode parseGameMode(String input) {
        if (input == null) return null;

        input = input.toLowerCase().trim();

        // Try numeric parsing
        try {
            int mode = Integer.parseInt(input);
            switch (mode) {
                case 0: return GameMode.SURVIVAL;
                case 1: return GameMode.CREATIVE;
                case 2: return GameMode.ADVENTURE;
                case 3: return GameMode.SPECTATOR;
                default: return null;
            }
        } catch (NumberFormatException e) {
            // Not a number, try string parsing
            switch (input) {
                case "survival":
                case "s":
                case "0":
                case "gms":
                    return GameMode.SURVIVAL;
                case "creative":
                case "c":
                case "1":
                case "gmc":
                    return GameMode.CREATIVE;
                case "adventure":
                case "a":
                case "2":
                case "gma":
                    return GameMode.ADVENTURE;
                case "spectator":
                case "sp":
                case "3":
                case "gmsp":
                    return GameMode.SPECTATOR;
                default:
                    return null;
            }
        }
    }

    public String getGameModeName(GameMode gameMode) {
        if (gameMode == null) return "Unknown";

        switch (gameMode) {
            case SURVIVAL: return "Survival";
            case CREATIVE: return "Creative";
            case ADVENTURE: return "Adventure";
            case SPECTATOR: return "Spectator";
            default: return "Unknown";
        }
    }

    public String getGameModeColor(GameMode gameMode) {
        if (gameMode == null) return "§7";

        switch (gameMode) {
            case SURVIVAL: return "§a"; // Green
            case CREATIVE: return "§6"; // Gold
            case ADVENTURE: return "§e"; // Yellow
            case SPECTATOR: return "§9"; // Blue
            default: return "§7"; // Gray
        }
    }
}