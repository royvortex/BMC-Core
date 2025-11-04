package fun.bharatmc.bmcCore.commands;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.managers.GodManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GodCommand extends Command {
    private final BMCCore plugin;
    private final GodManager godManager;

    public GodCommand(BMCCore plugin) {
        super("god", "Toggle god mode", "/god [player]", Arrays.asList("godmode", "gm"));
        this.plugin = plugin;
        this.godManager = plugin.getGodManager();

        this.setPermission("bmccore.god");
        this.setPermissionMessage("§cYou don't have permission to use this command!");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Handle console without arguments
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage("§cUsage: /god <player>");
            return true;
        }

        // No arguments - toggle self
        if (args.length == 0) {
            return toggleSelf((Player) sender);
        }

        // One argument - toggle other player
        if (args.length == 1) {
            return toggleOther(sender, args[0]);
        }

        sender.sendMessage("§cUsage: /god [player]");
        return true;
    }

    private boolean toggleSelf(Player player) {
        if (!player.hasPermission("bmccore.god")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        boolean isNowGodMode = godManager.toggleGodMode(player);

        if (isNowGodMode) {
            player.sendMessage("§aGod mode enabled! You are now invulnerable.");
        } else {
            player.sendMessage("§aGod mode disabled! You can now take damage.");
        }

        return true;
    }

    private boolean toggleOther(CommandSender sender, String targetName) {
        if (!sender.hasPermission("bmccore.god.others")) {
            sender.sendMessage("§cYou don't have permission to toggle god mode for other players!");
            return true;
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cPlayer not found: " + targetName);
            return true;
        }

        boolean isNowGodMode = godManager.toggleGodMode(target);
        boolean isSelf = sender.equals(target);

        if (isNowGodMode) {
            sender.sendMessage("§aEnabled god mode for " + target.getName() + "!");
            if (!isSelf) {
                target.sendMessage("§aGod mode has been enabled by " + sender.getName() + "!");
            }
        } else {
            sender.sendMessage("§aDisabled god mode for " + target.getName() + "!");
            if (!isSelf) {
                target.sendMessage("§aGod mode has been disabled by " + sender.getName() + "!");
            }
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("bmccore.god.others")) {
            String input = args[0].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(input)) {
                    completions.add(player.getName());
                }
            }
        }

        return completions;
    }
}