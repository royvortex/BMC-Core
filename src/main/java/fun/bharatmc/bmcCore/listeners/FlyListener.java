package fun.bharatmc.bmcCore.listeners;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.managers.FlyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FlyListener implements Listener {
    private final BMCCore plugin;
    private final FlyManager flyManager;

    public FlyListener(BMCCore plugin) {
        this.plugin = plugin;
        this.flyManager = plugin.getFlyManager();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Handle fly for joining player
        flyManager.handlePlayerJoin(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
    }
}