package fun.bharatmc.bmcCore.listeners;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.managers.PlayerManager;
import fun.bharatmc.bmcCore.managers.VanishManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class VanishListener implements Listener {
    private final BMCCore plugin;
    private final VanishManager vanishManager;
    private final PlayerManager playerManager;

    public VanishListener(BMCCore plugin) {
        this.plugin = plugin;
        this.vanishManager = plugin.getVanishManager();
        this.playerManager = plugin.getPlayerManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Handle vanish for joining player
        vanishManager.handlePlayerJoin(player);

        // If player is vanished, hide join message
        if (vanishManager.isVanished(player) && plugin.getConfig().getBoolean("vanish.silent-join", true)) {
            event.setJoinMessage(null);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Handle vanish for quitting player
        vanishManager.handlePlayerQuit(player);
        playerManager.removePlayerData(player);

        // If player is vanished, hide quit message
        if (vanishManager.isVanished(player) && plugin.getConfig().getBoolean("vanish.silent-quit", true)) {
            event.setQuitMessage(null);
        }
    }
}