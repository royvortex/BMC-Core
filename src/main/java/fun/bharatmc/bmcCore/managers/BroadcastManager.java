package fun.bharatmc.bmcCore.managers;

import fun.bharatmc.bmcCore.BMCCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class BroadcastManager {
    private final BMCCore plugin;

    public BroadcastManager(BMCCore plugin) {
        this.plugin = plugin;
    }

    public void broadcastChat(String message) {
        String formattedMessage = formatChatMessage(message);

        // Send to all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(formattedMessage);
        }

        // Also send to console
        Bukkit.getConsoleSender().sendMessage(formattedMessage);

        // Optional: Play sound
        playBroadcastSound();

        plugin.getLogger().info("Chat Broadcast: " + message);
    }

    public void broadcastTitle(String title, String subtitle) {
        // Default values if null
        if (title == null) title = "";
        if (subtitle == null) subtitle = "";

        // Format with colors
        String formattedTitle = ChatColor.translateAlternateColorCodes('&', title);
        String formattedSubtitle = ChatColor.translateAlternateColorCodes('&', subtitle);

        // Send to all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(
                    formattedTitle,
                    formattedSubtitle,
                    10,  // fadeIn (ticks)
                    60,  // stay (ticks)
                    20   // fadeOut (ticks)
            );
        }

        // Optional: Play sound
        playBroadcastSound();

        plugin.getLogger().info("Title Broadcast - Title: " + title + " | Subtitle: " + subtitle);
    }

    public void broadcastActionBar(String message) {
        String formattedMessage = ChatColor.translateAlternateColorCodes('&', message);

        // Send to all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formattedMessage));
        }

        plugin.getLogger().info("ActionBar Broadcast: " + message);
    }

    private String formatChatMessage(String message) {
        // Get format from config or use default
        String format = plugin.getConfig().getString("broadcast.chat-format", "&6[Broadcast] &f{message}");
        format = ChatColor.translateAlternateColorCodes('&', format);
        return format.replace("{message}", ChatColor.translateAlternateColorCodes('&', message));
    }

    private void playBroadcastSound() {
        if (plugin.getConfig().getBoolean("broadcast.play-sound", true)) {
            String soundName = plugin.getConfig().getString("broadcast.sound", "ENTITY_EXPERIENCE_ORB_PICKUP");
            try {
                Sound sound = Sound.valueOf(soundName);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid broadcast sound: " + soundName);
            }
        }
    }

    public void sendHelp(Player player) {
        player.sendMessage("§6§lBroadcast Commands:");
        player.sendMessage("§e/broadcast chat <message> §7- Broadcast message in chat");
        player.sendMessage("§e/broadcast title <title> [subtitle] §7- Broadcast as title");
        player.sendMessage("§e/broadcast actionbar <message> §7- Broadcast as action bar");
        player.sendMessage("§7Use '&' for color codes. Example: &cRed &aGreen &9Blue");
    }
}