package fun.bharatmc.bmcCore.listeners;

import fun.bharatmc.bmcCore.BMCCore;
import fun.bharatmc.bmcCore.managers.GodManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GodListener implements Listener {
    private final BMCCore plugin;
    private final GodManager godManager;

    public GodListener(BMCCore plugin) {
        this.plugin = plugin;
        this.godManager = plugin.getGodManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        // Check if the entity is a player
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        // Check if player has god mode enabled
        if (godManager.isGodMode(player)) {
            // Cancel all damage types
            event.setCancelled(true);

            // Extinguish fire if damaged by fire
            if (event.getCause() == DamageCause.FIRE ||
                    event.getCause() == DamageCause.FIRE_TICK ||
                    event.getCause() == DamageCause.LAVA) {
                player.setFireTicks(0);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        // Check if the entity is a player
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        // If player has god mode, prevent hunger
        if (godManager.isGodMode(player)) {
            event.setCancelled(true);
            player.setFoodLevel(20); // Keep food level full
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Handle god mode for joining player
        godManager.handlePlayerJoin(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // No special handling needed for quit - data is saved automatically
    }
}