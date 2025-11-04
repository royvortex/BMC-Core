package fun.bharatmc.bmcCore.commands;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.managers.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VanishCommand extends Command {
    private final BMCCore plugin;
    private final VanishManager vanishManager;

    public VanishCommand(BMCCore plugin) {
        super("vanish", "Toggle vanish mode", "/vanish [player]", Arrays.asList("v"));
        this.plugin = plugin;
        this.vanishManager = plugin.getVanishManager();

        // Set command properties
        this.setDescription("Toggle vanish mode");
        this.setUsage("/vanish [player]");
        this.setPermission("bmccore.vanish");
        this.setPermissionMessage("§cYou don't have permission to use this command!");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Handle console without arguments
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage("§cUsage: /vanish <player>");
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

        sender.sendMessage("§cUsage: /vanish [player]");
        return true;
    }

    private boolean toggleSelf(Player player) {
        if (!player.hasPermission("bmccore.vanish")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        boolean isNowVanished = vanishManager.toggleVanish(player);

        if (isNowVanished) {
            player.sendMessage("§aYou are now vanished!");
        } else {
            player.sendMessage("§aYou are no longer vanished!");
        }

        return true;
    }

    private boolean toggleOther(CommandSender sender, String targetName) {
        if (!sender.hasPermission("bmccore.vanish.others")) {
            sender.sendMessage("§cYou don't have permission to vanish other players!");
            return true;
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cPlayer not found: " + targetName);
            return true;
        }

        boolean isNowVanished = vanishManager.toggleVanish(target);
        boolean isSelf = sender.equals(target);

        if (isNowVanished) {
            sender.sendMessage("§aVanished " + target.getName() + "!");
            if (!isSelf) {
                target.sendMessage("§aYou have been vanished by " + sender.getName() + "!");
            }
        } else {
            sender.sendMessage("§aUnvanished " + target.getName() + "!");
            if (!isSelf) {
                target.sendMessage("§aYou have been unvanished by " + sender.getName() + "!");
            }
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("bmccore.vanish.others")) {
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