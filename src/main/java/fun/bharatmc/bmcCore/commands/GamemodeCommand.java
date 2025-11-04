package fun.bharatmc.bmcCore.commands;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.managers.GamemodeManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GamemodeCommand extends Command {
    private final BMCCore plugin;
    private final GamemodeManager gamemodeManager;
    private final GameMode specificGameMode; // For specific commands like /gmc, /gms, etc.

    // Constructor for generic /gm command
    public GamemodeCommand(BMCCore plugin) {
        super("gm");
        this.plugin = plugin;
        this.gamemodeManager = plugin.getGamemodeManager();
        this.specificGameMode = null;

        this.setDescription("Change game mode");
        this.setUsage("/gm <mode> [player]");
        this.setPermission("bmccore.gamemode");
        this.setPermissionMessage("§cYou don't have permission to use this command!");

        // Aliases for generic gm command
        this.setAliases(Arrays.asList("gamemode", "mode"));
    }

    // Constructor for specific gamemode commands
    public GamemodeCommand(BMCCore plugin, GameMode gameMode, String commandName, String description) {
        super(commandName);
        this.plugin = plugin;
        this.gamemodeManager = plugin.getGamemodeManager();
        this.specificGameMode = gameMode;

        this.setDescription(description);
        this.setUsage("/" + commandName + " [player]");
        this.setPermission("bmccore.gamemode");
        this.setPermissionMessage("§cYou don't have permission to use this command!");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Handle specific gamemode commands (/gms, /gmc, /gma, /gmsp)
        if (specificGameMode != null) {
            return executeSpecificGamemode(sender, args);
        }

        // Handle generic /gm command
        return executeGenericGamemode(sender, args);
    }

    private boolean executeSpecificGamemode(CommandSender sender, String[] args) {
        Player target;

        if (args.length == 0) {
            // No arguments - change own gamemode
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cConsole usage: /" + getName() + " <player>");
                return true;
            }

            target = (Player) sender;
        } else {
            // One argument - change other player's gamemode
            if (!sender.hasPermission("bmccore.gamemode.others")) {
                sender.sendMessage("§cYou don't have permission to change other players' gamemode!");
                return true;
            }

            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found: " + args[0]);
                return true;
            }
        }

        return setGamemode(sender, target, specificGameMode);
    }

    private boolean executeGenericGamemode(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /gm <mode> [player]");
            sender.sendMessage("§7Modes: 0, 1, 2, 3, survival, creative, adventure, spectator");
            return true;
        }

        GameMode gameMode = gamemodeManager.parseGameMode(args[0]);
        if (gameMode == null) {
            sender.sendMessage("§cInvalid gamemode: " + args[0]);
            sender.sendMessage("§7Valid modes: 0, 1, 2, 3, survival, creative, adventure, spectator");
            return true;
        }

        Player target;

        if (args.length == 1) {
            // No player specified - change own gamemode
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cConsole usage: /gm <mode> <player>");
                return true;
            }

            target = (Player) sender;
        } else {
            // Player specified - change other player's gamemode
            if (!sender.hasPermission("bmccore.gamemode.others")) {
                sender.sendMessage("§cYou don't have permission to change other players' gamemode!");
                return true;
            }

            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found: " + args[1]);
                return true;
            }
        }

        return setGamemode(sender, target, gameMode);
    }

    private boolean setGamemode(CommandSender sender, Player target, GameMode gameMode) {
        boolean success = gamemodeManager.setGameMode(target, gameMode);

        if (success) {
            String gamemodeName = gamemodeManager.getGameModeName(gameMode);
            String gamemodeColor = gamemodeManager.getGameModeColor(gameMode);

            if (sender.equals(target)) {
                sender.sendMessage("§aYour gamemode has been set to " + gamemodeColor + gamemodeName + "§a!");
            } else {
                sender.sendMessage("§aSet " + target.getName() + "'s gamemode to " + gamemodeColor + gamemodeName + "§a!");
                target.sendMessage("§aYour gamemode has been set to " + gamemodeColor + gamemodeName + "§a by " + sender.getName() + "!");
            }
        } else {
            sender.sendMessage("§cFailed to set gamemode!");
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();

        if (specificGameMode != null) {
            // Tab completion for specific gamemode commands (/gms, /gmc, etc.)
            if (args.length == 1 && sender.hasPermission("bmccore.gamemode.others")) {
                String input = args[0].toLowerCase();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(input)) {
                        completions.add(player.getName());
                    }
                }
            }
        } else {
            // Tab completion for generic /gm command
            if (args.length == 1) {
                String input = args[0].toLowerCase();
                String[] modes = {"survival", "creative", "adventure", "spectator"};

                for (String mode : modes) {
                    if (mode.startsWith(input)) {
                        completions.add(mode);
                    }
                }
            } else if (args.length == 2 && sender.hasPermission("bmccore.gamemode.others")) {
                String input = args[1].toLowerCase();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(input)) {
                        completions.add(player.getName());
                    }
                }
            }
        }

        return completions;
    }
}