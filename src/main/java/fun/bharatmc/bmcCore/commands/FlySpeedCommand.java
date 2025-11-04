package fun.bharatmc.bmcCore.commands;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.managers.FlyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FlySpeedCommand extends Command {
    private final BMCCore plugin;
    private final FlyManager flyManager;

    public FlySpeedCommand(BMCCore plugin) {
        super("flyspeed");
        this.plugin = plugin;
        this.flyManager = plugin.getFlyManager();

        this.setDescription("Set flight speed");
        this.setUsage("/flyspeed <speed> [player]");
        this.setPermission("bmccore.flyspeed");
        this.setPermissionMessage("§cYou don't have permission to use this command!");

        // Add aliases
        this.setAliases(List.of("fspeed", "fs"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /flyspeed <speed> [player]");
            sender.sendMessage("§7Speed: 1-10 (1 = slow, 10 = fastest)");
            return true;
        }

        // Parse speed (1-10 scale, convert to 0.1-1.0)
        float speed;
        try {
            int speedInput = Integer.parseInt(args[0]);
            if (speedInput < 1 || speedInput > 10) {
                sender.sendMessage("§cSpeed must be between 1 and 10!");
                return true;
            }
            // Convert 1-10 scale to 0.1-1.0 (Minecraft's format)
            speed = speedInput / 10.0f;
        } catch (NumberFormatException e) {
            sender.sendMessage("§cSpeed must be a number between 1 and 10!");
            return true;
        }

        // No player specified - set own speed
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cConsole usage: /flyspeed <speed> <player>");
                return true;
            }
            return setSpeedSelf((Player) sender, speed);
        }

        // Player specified - set other player's speed
        if (args.length == 2) {
            return setSpeedOther(sender, args[1], speed);
        }

        sender.sendMessage("§cUsage: /flyspeed <speed> [player]");
        return true;
    }

    private boolean setSpeedSelf(Player player, float speed) {
        if (!player.hasPermission("bmccore.flyspeed")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        boolean success = flyManager.setFlySpeed(player, speed);

        if (success) {
            int displaySpeed = (int)(speed * 10); // Convert back to 1-10 for display
            player.sendMessage("§aFlight speed set to " + displaySpeed + "!");
        } else {
            player.sendMessage("§cFailed to set flight speed!");
        }

        return true;
    }

    private boolean setSpeedOther(CommandSender sender, String targetName, float speed) {
        if (!sender.hasPermission("bmccore.flyspeed.others")) {
            sender.sendMessage("§cYou don't have permission to set flight speed for other players!");
            return true;
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cPlayer not found: " + targetName);
            return true;
        }

        boolean success = flyManager.setFlySpeed(target, speed);
        boolean isSelf = sender.equals(target);
        int displaySpeed = (int)(speed * 10);

        if (success) {
            sender.sendMessage("§aSet flight speed to " + displaySpeed + " for " + target.getName() + "!");
            if (!isSelf) {
                target.sendMessage("§aYour flight speed has been set to " + displaySpeed + " by " + sender.getName() + "!");
            }
        } else {
            sender.sendMessage("§cFailed to set flight speed for " + target.getName() + "!");
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Tab complete speed numbers 1-10
            for (int i = 1; i <= 10; i++) {
                if (String.valueOf(i).startsWith(args[0])) {
                    completions.add(String.valueOf(i));
                }
            }
        } else if (args.length == 2 && sender.hasPermission("bmccore.flyspeed.others")) {
            // Tab complete player names
            String input = args[1].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(input)) {
                    completions.add(player.getName());
                }
            }
        }

        return completions;
    }
}