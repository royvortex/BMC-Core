package fun.bharatmc.bmcCore.managers;

import fun.bharatmc.bmcCore.BMCCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager {
    private final BMCCore plugin;
    private final PlayerManager playerManager;
    private final Set<UUID> vanishedPlayers;

    public VanishManager(BMCCore plugin) {
        this.plugin = plugin;
        this.playerManager = plugin.getPlayerManager();
        this.vanishedPlayers = new HashSet<>();
        loadVanishedPlayers();
    }

    private void loadVanishedPlayers() {
        // Load players who were vanished from database
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (playerManager.isPlayerVanished(player)) {
                vanishedPlayers.add(player.getUniqueId());
                applyVanishEffects(player);
            }
        }
    }

    public boolean toggleVanish(Player player) {
        UUID uuid = player.getUniqueId();
        boolean isVanished = vanishedPlayers.contains(uuid);

        if (isVanished) {
            // Remove vanish
            removeVanishEffects(player);
            vanishedPlayers.remove(uuid);
            playerManager.setPlayerVanished(player, false);
        } else {
            // Apply vanish
            applyVanishEffects(player);
            vanishedPlayers.add(uuid);
            playerManager.setPlayerVanished(player, true);
        }

        return !isVanished; // Return new state (true = now vanished, false = now visible)
    }

    private void applyVanishEffects(Player player) {
        // Hide player from all other players
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.hasPermission("bmccore.vanish.see") && !online.equals(player)) {
                online.hidePlayer(plugin, player);
            }
        }

        // Add night vision to prevent vanish darkness
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true, false));

        // REMOVED: Message sending - now handled by command only
    }

    private void removeVanishEffects(Player player) {
        // Show player to all other players
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.canSee(player)) {
                online.showPlayer(plugin, player);
            }
        }

        // Remove night vision
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);

        // REMOVED: Message sending - now handled by command only
    }

    public void handlePlayerJoin(Player player) {
        // If player was vanished, reapply effects
        if (playerManager.isPlayerVanished(player)) {
            vanishedPlayers.add(player.getUniqueId());
            Bukkit.getScheduler().runTaskLater(plugin, () -> applyVanishEffects(player), 10L);
        }

        // Hide vanished players from joining player
        for (UUID vanishedUUID : vanishedPlayers) {
            Player vanished = Bukkit.getPlayer(vanishedUUID);
            if (vanished != null && !player.equals(vanished) && !player.hasPermission("bmccore.vanish.see")) {
                player.hidePlayer(plugin, vanished);
            }
        }
    }

    public void handlePlayerQuit(Player player) {
        vanishedPlayers.remove(player.getUniqueId());
    }

    public boolean isVanished(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    public Set<UUID> getVanishedPlayers() {
        return new HashSet<>(vanishedPlayers);
    }


    public void cleanup() {
        // Remove vanish effects from all players
        for (UUID uuid : vanishedPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                removeVanishEffects(player);
            }
        }
        vanishedPlayers.clear();
    }

    public int getVanishedPlayerCount() {
        return vanishedPlayers.size();
    }

}