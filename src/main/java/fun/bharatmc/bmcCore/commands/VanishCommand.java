package fun.bharatmc.bmcCore.commands;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.managers.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VanishCommand extends Command {
    private final BMCCore plugin;
    private final VanishManager vanishManager;

    public VanishCommand(BMCCore plugin) {
        super("vanish");
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
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        if (!sender.hasPermission("bmccore.vanish")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            // Toggle own vanish
            Player player = (Player) sender;
            boolean vanished = vanishManager.toggleVanish(player);
            if (vanished) {
                player.sendMessage("§aYou are now vanished!");
            } else {
                player.sendMessage("§aYou are no longer vanished!");
            }
            return true;
        }

        if (args.length == 1) {
            // Toggle other player's vanish
            if (!sender.hasPermission("bmccore.vanish.others")) {
                sender.sendMessage("§cYou don't have permission to vanish other players!");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found: " + args[0]);
                return true;
            }

            boolean vanished = vanishManager.toggleVanish(target);
            if (vanished) {
                sender.sendMessage("§aVanished " + target.getName() + "!");
                target.sendMessage("§aYou have been vanished by " + sender.getName() + "!");
            } else {
                sender.sendMessage("§aUnvanished " + target.getName() + "!");
                target.sendMessage("§aYou have been unvanished by " + sender.getName() + "!");
            }
            return true;
        }

        sender.sendMessage("§cUsage: /vanish [player]");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("bmccore.vanish.others")) {
            // Tab complete player names
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        }

        return completions;
    }
}