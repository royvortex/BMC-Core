package fun.bharatmc.bmcCore.commands;

import fun.bharatmc.bmcCore.BMCCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ForceCommand extends Command {
    private final BMCCore plugin;

    public ForceCommand(BMCCore plugin) {
        super("forcep");
        this.plugin = plugin;

        this.setDescription("Force a player to execute a command");
        this.setUsage("/forcep <player> <command>");
        this.setPermission("bmccore.forcep");
        this.setPermissionMessage("§cYou don't have permission to use this command!");

        // Add aliases
        this.setAliases(List.of("force", "executeas"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission("bmccore.forcep")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /forcep <player> <command>");
            sender.sendMessage("§7Example: /forcep PlayerName say Hello World");
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            sender.sendMessage("§cPlayer not found: " + targetName);
            return true;
        }

        // Build the command (remove the player name from args)
        String command = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        // Remove leading slash if present
        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        // Execute the command as the player
        boolean success = target.performCommand(command);

        if (success) {
            sender.sendMessage("§aForced " + target.getName() + " to execute: /" + command);

            // Notify the target (unless it's a silent command)
            if (!command.toLowerCase().startsWith("say ")) {
                target.sendMessage("§cYou were forced to execute: /" + command + " by " + sender.getName());
            }
        } else {
            sender.sendMessage("§cFailed to execute command as " + target.getName());
        }

        // Log the action for security
        String senderName = sender instanceof Player ? sender.getName() : "CONSOLE";
        plugin.getLogger().warning("FORCE COMMAND: " + senderName + " forced " + target.getName() + " to run: /" + command);

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(input)) {
                    completions.add(player.getName());
                }
            }
        }
        // For command completion, we could add common commands, but it's complex

        return completions;
    }
}