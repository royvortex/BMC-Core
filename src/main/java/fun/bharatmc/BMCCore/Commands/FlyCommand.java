package fun.bharatmc.BMCCore.Commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FlyCommand {

    public void register() {
        new CommandAPICommand("fly")
                .withPermission("bmccore.fly")
                .withAliases("flight")
                .withHelp("Toggle flight mode", "Toggles flight for yourself or another player")
                .executesPlayer((player, args) -> {
                    // /fly - toggle for yourself
                    toggleFlight(player);
                })
                .register();

        new CommandAPICommand("fly")
                .withPermission("bmccore.fly.others")
                .withArguments(new PlayerArgument("target"))
                .executes((sender, args) -> {
                    // /fly <player> - toggle for other player
                    Player target = (Player) args.get("target");
                    toggleFlight(target);

                    if (sender instanceof Player) {
                        Player executor = (Player) sender;
                        executor.sendMessage("§6Flight §e" +
                                (target.getAllowFlight() ? "enabled" : "disabled") +
                                " §6for §e" + target.getName() + "§6.");
                    } else {
                        Bukkit.getConsoleSender().sendMessage("§6Flight " +
                                (target.getAllowFlight() ? "enabled" : "disabled") +
                                " for " + target.getName() + ".");
                    }
                })
                .register();
    }

    private void toggleFlight(Player player) {
        boolean newState = !player.getAllowFlight();
        player.setAllowFlight(newState);
        player.setFlying(newState);

        player.sendMessage("§6Flight mode " + (newState ? "§aenabled" : "§cdisabled") + "§6.");

        // Send title
        player.sendTitle(
                newState ? "§aFLIGHT ENABLED" : "§cFLIGHT DISABLED",
                newState ? "§eYou can now fly" : "§eFlight disabled",
                10, 40, 10
        );
    }
}