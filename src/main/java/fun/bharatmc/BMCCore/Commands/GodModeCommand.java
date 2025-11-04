package fun.bharatmc.BMCCore.Commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import fun.bharatmc.BMCCore.BMCCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GodModeCommand implements Listener {

    private final Set<UUID> godModePlayers;
    private final JavaPlugin plugin;

    public GodModeCommand() {
        this.godModePlayers = new HashSet<>();
        this.plugin = JavaPlugin.getProvidingPlugin(BMCCore.class);

        // Register event listener
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void register() {
        new CommandAPICommand("god")
                .withPermission("bmccore.god")
                .withAliases("godmode", "invulnerable")
                .withHelp("Toggle god mode", "Toggles invulnerability for yourself or another player")
                .executesPlayer((player, args) -> {
                    // /god - toggle for yourself
                    toggleGodMode(player);
                })
                .register();

        new CommandAPICommand("god")
                .withPermission("bmccore.god.others")
                .withArguments(new PlayerArgument("target"))
                .executes((sender, args) -> {
                    // /god <player> - toggle for other player
                    Player target = (Player) args.get("target");
                    toggleGodMode(target, sender);
                })
                .register();
    }

    private void toggleGodMode(Player player) {
        if (godModePlayers.contains(player.getUniqueId())) {
            disableGodMode(player);
            player.sendMessage("§6God mode §cdisabled§6.");
        } else {
            enableGodMode(player);
            player.sendMessage("§6God mode §aenabled§6.");
        }
    }

    private void toggleGodMode(Player target, Object sender) {
        if (godModePlayers.contains(target.getUniqueId())) {
            disableGodMode(target);
            if (sender instanceof Player) {
                Player executor = (Player) sender;
                executor.sendMessage("§6God mode §cdisabled §6for §e" + target.getName() + "§6.");
            } else {
                Bukkit.getConsoleSender().sendMessage("§6God mode disabled for " + target.getName() + ".");
            }
            target.sendMessage("§6God mode §cdisabled§6 by §e" +
                    (sender instanceof Player ? ((Player) sender).getName() : "console") + "§6.");
        } else {
            enableGodMode(target);
            if (sender instanceof Player) {
                Player executor = (Player) sender;
                executor.sendMessage("§6God mode §aenabled §6for §e" + target.getName() + "§6.");
            } else {
                Bukkit.getConsoleSender().sendMessage("§6God mode enabled for " + target.getName() + ".");
            }
            target.sendMessage("§6God mode §aenabled§6 by §e" +
                    (sender instanceof Player ? ((Player) sender).getName() : "console") + "§6.");
        }
    }

    private void enableGodMode(Player player) {
        godModePlayers.add(player.getUniqueId());
        player.setInvulnerable(true);
        player.setCanPickupItems(false);
        player.setCollidable(false);

        // Send title notification
        player.sendTitle("§aGOD MODE ENABLED", "§eYou are now invulnerable", 10, 40, 10);
    }

    private void disableGodMode(Player player) {
        godModePlayers.remove(player.getUniqueId());
        player.setInvulnerable(false);
        player.setCanPickupItems(true);
        player.setCollidable(true);

        // Send title notification
        player.sendTitle("§cGOD MODE DISABLED", "§eYou are now vulnerable", 10, 40, 10);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        godModePlayers.remove(event.getPlayer().getUniqueId());
    }

    public void cleanupOnDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (godModePlayers.contains(player.getUniqueId())) {
                disableGodMode(player);
                player.sendMessage("§6God mode disabled due to plugin reload/shutdown.");
            }
        }
        godModePlayers.clear();
    }

    // API methods
    public boolean isGodMode(Player player) {
        return godModePlayers.contains(player.getUniqueId());
    }

    public Set<UUID> getGodModePlayers() {
        return new HashSet<>(godModePlayers);
    }
}