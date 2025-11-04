package fun.bharatmc.bmcCore.commands;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.managers.BroadcastManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BroadcastCommand extends Command {
    private final BMCCore plugin;
    private final BroadcastManager broadcastManager;

    public BroadcastCommand(BMCCore plugin) {
        super("broadcast");
        this.plugin = plugin;
        this.broadcastManager = plugin.getBroadcastManager();

        this.setDescription("Broadcast messages to the server");
        this.setUsage("/broadcast <chat|title> <message>");
        this.setPermission("bmccore.broadcast");
        this.setPermissionMessage("§cYou don't have permission to use this command!");

        // Add aliases
        this.setAliases(Arrays.asList("bc", "alert", "announce"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission("bmccore.broadcast")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length < 2) {
            if (sender instanceof Player) {
                broadcastManager.sendHelp((Player) sender);
            } else {
                sender.sendMessage("§cUsage: /broadcast <chat|title> <message>");
                sender.sendMessage("§cFor title: /broadcast title <title> [subtitle]");
            }
            return true;
        }

        String type = args[0].toLowerCase();
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        switch (type) {
            case "chat":
                handleChatBroadcast(sender, message);
                break;
            case "title":
                handleTitleBroadcast(sender, message);
                break;
            case "actionbar":
            case "action":
                handleActionBarBroadcast(sender, message);
                break;
            default:
                sender.sendMessage("§cInvalid broadcast type: " + type);
                sender.sendMessage("§cValid types: chat, title, actionbar");
                break;
        }

        return true;
    }

    private void handleChatBroadcast(CommandSender sender, String message) {
        broadcastManager.broadcastChat(message);
        sender.sendMessage("§aChat broadcast sent successfully!");
    }

    private void handleTitleBroadcast(CommandSender sender, String message) {
        // Check if message contains subtitle separator "|"
        String title;
        String subtitle = "";

        if (message.contains("|")) {
            String[] parts = message.split("\\|", 2);
            title = parts[0].trim();
            subtitle = parts[1].trim();
        } else {
            title = message;
        }

        broadcastManager.broadcastTitle(title, subtitle);
        sender.sendMessage("§aTitle broadcast sent successfully!");
    }

    private void handleActionBarBroadcast(CommandSender sender, String message) {
        broadcastManager.broadcastActionBar(message);
        sender.sendMessage("§aAction bar broadcast sent successfully!");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Tab complete broadcast types
            String input = args[0].toLowerCase();
            String[] types = {"chat", "title", "actionbar"};

            for (String type : types) {
                if (type.startsWith(input)) {
                    completions.add(type);
                }
            }
        }
        // No tab completion for messages

        return completions;
    }
}