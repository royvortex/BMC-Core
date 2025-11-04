package fun.bharatmc.bmcCore.commands;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.managers.FlyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlyCommand extends Command {
    private final BMCCore plugin;
    private final FlyManager flyManager;

    public FlyCommand(BMCCore plugin) {
        super("fly", "Toggle flight mode", "/fly [player]", Arrays.asList("f"));
        this.plugin = plugin;
        this.flyManager = plugin.getFlyManager();

        this.setPermission("bmccore.fly");
        this.setPermissionMessage("§cYou don't have permission to use this command!");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        // Handle console without arguments
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage("§cUsage: /fly <player>");
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

        sender.sendMessage("§cUsage: /fly [player]");
        return true;
    }

    private boolean toggleSelf(Player player) {
        if (!player.hasPermission("bmccore.fly")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        boolean isNowFlying = flyManager.toggleFly(player);

        if (isNowFlying) {
            player.sendMessage("§aFlight enabled!");
        } else {
            player.sendMessage("§aFlight disabled!");
        }

        return true;
    }

    private boolean toggleOther(CommandSender sender, String targetName) {
        if (!sender.hasPermission("bmccore.fly.others")) {
            sender.sendMessage("§cYou don't have permission to toggle flight for other players!");
            return true;
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cPlayer not found: " + targetName);
            return true;
        }

        boolean isNowFlying = flyManager.toggleFly(target);
        boolean isSelf = sender.equals(target);

        if (isNowFlying) {
            sender.sendMessage("§aEnabled flight for " + target.getName() + "!");
            if (!isSelf) {
                target.sendMessage("§aYour flight has been enabled by " + sender.getName() + "!");
            }
        } else {
            sender.sendMessage("§aDisabled flight for " + target.getName() + "!");
            if (!isSelf) {
                target.sendMessage("§aYour flight has been disabled by " + sender.getName() + "!");
            }
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("bmccore.fly.others")) {
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