package fun.bharatmc.bmcCore.commands;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.managers.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {
    private final BMCCore plugin;
    private final VanishManager vanishManager;

    public VanishCommand(BMCCore plugin) {
        this.plugin = plugin;
        this.vanishManager = plugin.getVanishManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        if (args.length == 0) {
            // Toggle own vanish
            Player player = (Player) sender;
            if (!player.hasPermission("bmccore.vanish")) {
                player.sendMessage("§cYou don't have permission to use this command!");
                return true;
            }

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
}