package fun.bharatmc.bmcCore;

import fun.bharatmc.bmcCore.managers.DatabaseManager;
import fun.bharatmc.bmcCore.managers.PlayerManager;
import fun.bharatmc.bmcCore.managers.VanishManager;
import fun.bharatmc.bmcCore.commands.VanishCommand;
import fun.bharatmc.bmcCore.listeners.VanishListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BMCCore extends JavaPlugin {
    private DatabaseManager databaseManager;
    private PlayerManager playerManager;
    private VanishManager vanishManager;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        // Initialize managers
        this.databaseManager = new DatabaseManager(this);
        this.playerManager = new PlayerManager(this);
        this.vanishManager = new VanishManager(this);

        // Register commands and events
        registerCommands();
        registerEvents();

        getLogger().info("BMCCore has been enabled with SQLite!");
        getLogger().info("Database: " + databaseManager.getDatabaseFile().getName());
    }

    @Override
    public void onDisable() {
        // Save all data and close database connection
        if (playerManager != null) {
            playerManager.saveAllData();
        }
        if (vanishManager != null) {
            vanishManager.cleanup();
        }
        if (databaseManager != null) {
            databaseManager.closeConnection();
        }

        getLogger().info("BMCCore has been disabled!");
    }

    private void registerCommands() {
        getCommand("vanish").setExecutor(new VanishCommand(this));
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new VanishListener(this), this);
    }

    // Manager getters
    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public VanishManager getVanishManager() {
        return vanishManager;
    }
}