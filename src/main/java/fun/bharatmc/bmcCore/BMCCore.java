package fun.bharatmc.bmcCore;

import fun.bharatmc.bmcCore.commands.FlyCommand;
import fun.bharatmc.bmcCore.commands.FlySpeedCommand;
import fun.bharatmc.bmcCore.commands.VanishListCommand;
import fun.bharatmc.bmcCore.managers.DatabaseManager;
import fun.bharatmc.bmcCore.managers.FlyManager;
import fun.bharatmc.bmcCore.managers.PlayerManager;
import fun.bharatmc.bmcCore.managers.VanishManager;
import fun.bharatmc.bmcCore.commands.VanishCommand;
import fun.bharatmc.bmcCore.listeners.VanishListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class BMCCore extends JavaPlugin {
    private DatabaseManager databaseManager;
    private PlayerManager playerManager;
    private VanishManager vanishManager;
    private FlyManager flyManager;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        // Initialize managers
        this.databaseManager = new DatabaseManager(this);
        this.playerManager = new PlayerManager(this);
        this.vanishManager = new VanishManager(this);
        this.flyManager = new FlyManager(this);

        // Register commands and events (Paper style)
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
        // Paper-style command registration
        // Vanish
        VanishCommand vanishCommand = new VanishCommand(this);
        VanishListCommand vanishListCommand = new VanishListCommand(this);
        //Fly & Speed
        FlyCommand flyCommand = new FlyCommand(this);
        FlySpeedCommand flySpeedCommand = new FlySpeedCommand(this);

        // Register the command with Paper's command map
        try {
            // This is the Paper-compatible way to register commands

            //Vanish
            getServer().getCommandMap().register("", vanishCommand);
            getServer().getCommandMap().register("", vanishListCommand);

            //Fly & Speed Control
            getServer().getCommandMap().register("", flyCommand);
            getServer().getCommandMap().register("", flySpeedCommand);

            getLogger().info("Registered vanish command using Paper API");
        } catch (Exception e) {
            getLogger().warning("Failed to register command with Paper API: " + e.getMessage());
        }
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
    public FlyManager getFlyManager() {
        return flyManager;
    }
}