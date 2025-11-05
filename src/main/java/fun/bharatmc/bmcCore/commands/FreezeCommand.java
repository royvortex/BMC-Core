package fun.bharatmc.bmcCore.commands;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.managers.FreezeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FreezeCommand extends Command {
    private final BMCCore plugin;
    private final FreezeManager freezeManager;

    public FreezeCommand(BMCCore plugin) {
        super("freeze");
        this.plugin = plugin;
        this.freezeManager = plugin.getFreezeManager();

        this.setDescription("Freeze or unfreeze a player");
        this.setUsage("/freeze <player>");
        this.setPermission("bmccore.freeze");
        this.setPermissionMessage("§cYou don't have permission to use this command!");

        // Add aliases
        this.setAliases(List.of("fr", "ice"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission("bmccore.freeze")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /freeze <player>");
            return true;
        }

        if (args.length == 1) {
            return toggleFreeze(sender, args[0]);
        }

        sender.sendMessage("§cUsage: /freeze <player>");
        return true;
    }

    private boolean toggleFreeze(CommandSender sender, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cPlayer not found: " + targetName);
            return true;
        }

        Player executor = (sender instanceof Player) ? (Player) sender : null;
        boolean isNowFrozen = freezeManager.toggleFreeze(target, executor);

        if (isNowFrozen) {
            sender.sendMessage("§aFroze " + target.getName() + "!");
            if (!sender.equals(target)) {
                target.sendMessage("§cYou have been frozen by " + sender.getName() + "!");
            }
        } else {
            sender.sendMessage("§aUnfroze " + target.getName() + "!");
            if (!sender.equals(target)) {
                target.sendMessage("§aYou have been unfrozen by " + sender.getName() + "!");
            }
        }

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

        return completions;
    }
}