package fun.bharatmc.bmcCore.managers;

import fun.bharatmc.bmcCore.BMCCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BroadcastManager {
    private final BMCCore plugin;
    private final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public BroadcastManager(BMCCore plugin) {
        this.plugin = plugin;
    }

    public void broadcastChat(String message) {
        String formattedMessage = formatChatMessage(message);
        String hexFormattedMessage = translateHexCodes(formattedMessage);

        // Send to all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(hexFormattedMessage);
        }

        // Also send to console (without hex codes)
        Bukkit.getConsoleSender().sendMessage(ChatColor.stripColor(hexFormattedMessage));

        // Optional: Play sound
        playBroadcastSound();

        plugin.getLogger().info("Chat Broadcast: " + ChatColor.stripColor(message));
    }

    public void broadcastTitle(String title, String subtitle) {
        // Default values if null
        if (title == null) title = "";
        if (subtitle == null) subtitle = "";

        // Format with colors and hex codes
        String formattedTitle = translateHexCodes(ChatColor.translateAlternateColorCodes('&', title));
        String formattedSubtitle = translateHexCodes(ChatColor.translateAlternateColorCodes('&', subtitle));

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
        String formattedMessage = translateHexCodes(ChatColor.translateAlternateColorCodes('&', message));

        // Send to all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formattedMessage));
        }

        plugin.getLogger().info("ActionBar Broadcast: " + message);
    }

    /**
     * Translates hex color codes in the format &#RRGGBB to Minecraft color codes
     */
    private String translateHexCodes(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            ChatColor color = ChatColor.of("#" + hexCode);
            matcher.appendReplacement(buffer, color.toString());
        }

        return matcher.appendTail(buffer).toString();
    }

    private String formatChatMessage(String message) {
        // Get format from config or use default
        String format = plugin.getConfig().getString("broadcast.chat-format", "&6[Broadcast] &f{message}");
        format = ChatColor.translateAlternateColorCodes('&', format);

        // Apply hex codes to format first, then insert message
        format = translateHexCodes(format);
        String formattedMessage = translateHexCodes(ChatColor.translateAlternateColorCodes('&', message));

        return format.replace("{message}", formattedMessage);
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
        player.sendMessage("");
        player.sendMessage("§6§lColor Codes:");
        player.sendMessage("§7Standard: &0&0 &1&1 &2&2 &3&3 &4&4 &5&5 &6&6 &7&7 &8&8 &9&9 &a&a &b&b &c&c &d&d &e&e &f&f");
        player.sendMessage("§7Formatting: &l&lBold &m&mStrike &n&nUnderline &o&oItalic &k&kMagic &rReset");
        player.sendMessage("§6Hex Colors: &#RRGGBB (e.g., &#FF0000Red &#00FF00Green &#0000FFBlue)");
        player.sendMessage("");
        player.sendMessage("§6§lExamples:");
        player.sendMessage("§e/broadcast chat &#FF6B6BWelcome to &#4ECDC4Our Server!");
        player.sendMessage("§e/broadcast title &#FF6B6BEvent Starting|&#4ECDC4Join now!");
        player.sendMessage("§e/broadcast actionbar &#FFE66DWarning: &#FF6B6BBoss spawning!");
    }
}