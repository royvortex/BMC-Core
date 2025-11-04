package fun.bharatmc.bmcCore.commands;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.managers.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VanishListCommand extends Command {
    private final BMCCore plugin;
    private final VanishManager vanishManager;

    public VanishListCommand(BMCCore plugin) {
        super("vanishlist");
        this.plugin = plugin;
        this.vanishManager = plugin.getVanishManager();

        // Set command properties
        this.setDescription("View all vanished players");
        this.setUsage("/vanishlist");
        this.setPermission("bmccore.vanish.see");
        this.setPermissionMessage("§cYou don't have permission to use this command!");

        // Add aliases
        this.setAliases(List.of("vlist", "vanished", "vanishlist"));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission("bmccore.vanish.see")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        // Get all vanished players
        List<String> vanishedPlayerNames = getVanishedPlayerNames();

        if (vanishedPlayerNames.isEmpty()) {
            sender.sendMessage("§aThere are no vanished players online.");
            return true;
        }

        // Format the message
        String vanishedList = String.join("§7, §a", vanishedPlayerNames);

        sender.sendMessage("§8§m--------------------------------");
        sender.sendMessage("§6Vanished Players §7(§e" + vanishedPlayerNames.size() + "§7)");
        sender.sendMessage("§a" + vanishedList);
        sender.sendMessage("§8§m--------------------------------");

        return true;
    }

    private List<String> getVanishedPlayerNames() {
        List<String> vanishedNames = new ArrayList<>();

        for (UUID playerUUID : vanishManager.getVanishedPlayers()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null && player.isOnline()) {
                vanishedNames.add(player.getName());
            }
        }

        return vanishedNames;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        // No tab completion needed for this command
        return new ArrayList<>();
    }
}