package fun.bharatmc.bmcCore.commands;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.managers.WhoIsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WhoIsCommand extends Command {
    private final BMCCore plugin;
    private final WhoIsManager whoIsManager;

    public WhoIsCommand(BMCCore plugin) {
        super("whois");
        this.plugin = plugin;
        this.whoIsManager = plugin.getWhoIsManager();

        this.setDescription("Get detailed information about a player");
        this.setUsage("/whois <player>");
        this.setPermission("bmccore.whois");
        this.setPermissionMessage("§cYou don't have permission to use this command!");

        // Add aliases
        this.setAliases(List.of("playerinfo", "pinfo", "checkplayer"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission("bmccore.whois")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /whois <player>");
            sender.sendMessage("§7Aliases: /playerinfo, /pinfo, /checkplayer");
            sender.sendMessage("§7Supports both online and offline players");
            return true;
        }

        String targetName = args[0];

        // Check if sender has permission to see IP addresses
        boolean canSeeIP = sender.hasPermission("bmccore.whois.ip");

        if (args.length > 1 && args[1].equalsIgnoreCase("quick")) {
            // Quick info mode
            String quickInfo = whoIsManager.getQuickInfo(targetName);
            sender.sendMessage(quickInfo);
        } else {
            // Full detailed info
            String whoIsInfo = whoIsManager.getWhoIsInfo(targetName);

            // If no IP permission, remove IP line
            if (!canSeeIP) {
                whoIsInfo = whoIsInfo.replaceAll("§6IP Address: §f[^\n]+\n",
                        "§6IP Address: §cNo permission to view IP\n");
            }

            // Send the formatted information
            for (String line : whoIsInfo.split("\n")) {
                sender.sendMessage(line);
            }

            // Log the whois lookup for security
            String senderName = sender instanceof Player ? sender.getName() : "CONSOLE";
            plugin.getLogger().info("WHOIS: " + senderName + " looked up " + targetName);
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();

            // Get all player names (online + recent offline)
            List<String> allPlayerNames = whoIsManager.getAllPlayerNames();

            for (String playerName : allPlayerNames) {
                if (playerName.toLowerCase().startsWith(input)) {
                    completions.add(playerName);
                }
            }

            // If we have too many results, limit to 20
            if (completions.size() > 20) {
                completions = completions.subList(0, 20);
            }
        } else if (args.length == 2) {
            String input = args[1].toLowerCase();
            if ("quick".startsWith(input)) {
                completions.add("quick");
            }
        }

        return completions;
    }
}