package fun.bharatmc.bmcCore.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData {
    private final UUID playerUUID;
    private String playerName;
    private boolean vanished;
    private boolean flying;
    private boolean godMode;
    private float flySpeed;
    private Location lastLocation;
    private long firstJoin;
    private long lastLogin;

    public PlayerData(UUID playerUUID, String playerName) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.vanished = false;
        this.flying = false;
        this.godMode = false;
        this.flySpeed = 0.1f;
        this.firstJoin = System.currentTimeMillis();
        this.lastLogin = System.currentTimeMillis();
    }

    // Getters and Setters
    public UUID getPlayerUUID() { return playerUUID; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public boolean isVanished() { return vanished; }
    public void setVanished(boolean vanished) { this.vanished = vanished; }

    public boolean isFlying() { return flying; }
    public void setFlying(boolean flying) { this.flying = flying; }

    public boolean isGodMode() { return godMode; }
    public void setGodMode(boolean godMode) { this.godMode = godMode; }

    public float getFlySpeed() { return flySpeed; }
    public void setFlySpeed(float flySpeed) { this.flySpeed = flySpeed; }

    public Location getLastLocation() { return lastLocation; }
    public void setLastLocation(Location lastLocation) { this.lastLocation = lastLocation; }

    public long getFirstJoin() { return firstJoin; }
    public void setFirstJoin(long firstJoin) { this.firstJoin = firstJoin; }

    public long getLastLogin() { return lastLogin; }
    public void setLastLogin(long lastLogin) { this.lastLogin = lastLogin; }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }
}