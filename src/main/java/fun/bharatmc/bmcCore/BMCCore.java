package fun.bharatmc.bmcCore;

import fun.bharatmc.bmcCore.commands.*;
import fun.bharatmc.bmcCore.managers.DatabaseManager;
import fun.bharatmc.bmcCore.managers.FlyManager;
import fun.bharatmc.bmcCore.managers.PlayerManager;
import fun.bharatmc.bmcCore.managers.VanishManager;
import fun.bharatmc.bmcCore.listeners.VanishListener;
import fun.bharatmc.bmcCore.managers.GodManager;
import fun.bharatmc.bmcCore.managers.GamemodeManager;
import fun.bharatmc.bmcCore.managers.BroadcastManager;
import fun.bharatmc.bmcCore.commands.BroadcastCommand;
import fun.bharatmc.bmcCore.managers.FreezeManager;
import fun.bharatmc.bmcCore.commands.FreezeCommand;
import fun.bharatmc.bmcCore.listeners.FreezeListener;
import fun.bharatmc.bmcCore.managers.WhoIsManager;
import fun.bharatmc.bmcCore.commands.WhoIsCommand;
import org.bukkit.GameMode;
import org.bukkit.plugin.java.JavaPlugin;

public final class BMCCore extends JavaPlugin {
    private DatabaseManager databaseManager;
    private PlayerManager playerManager;
    private VanishManager vanishManager;
    private FlyManager flyManager;
    private GodManager godManager;
    private GamemodeManager gamemodeManager;
    private BroadcastManager broadcastManager;
    private FreezeManager freezeManager;
    private WhoIsManager whoIsManager;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        // Initialize managers
        this.databaseManager = new DatabaseManager(this);
        this.playerManager = new PlayerManager(this);
        this.vanishManager = new VanishManager(this);
        this.flyManager = new FlyManager(this);
        this.godManager = new GodManager(this);
        this.gamemodeManager = new GamemodeManager(this);
        this.broadcastManager = new BroadcastManager(this);
        this.freezeManager = new FreezeManager(this);
        this.whoIsManager = new WhoIsManager(this);


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
        //GodMode
        GodCommand godCommand = new GodCommand(this);
        //Gamemode commands
        GamemodeCommand gmCommand = new GamemodeCommand(this);
        GamemodeCommand gmsCommand = new GamemodeCommand(this, GameMode.SURVIVAL, "gms", "Change to survival mode");
        GamemodeCommand gmcCommand = new GamemodeCommand(this, GameMode.CREATIVE, "gmc", "Change to creative mode");
        GamemodeCommand gmaCommand = new GamemodeCommand(this, GameMode.ADVENTURE, "gma", "Change to adventure mode");
        GamemodeCommand gmspCommand = new GamemodeCommand(this, GameMode.SPECTATOR, "gmsp", "Change to spectator mode");
        //BroadCast
        BroadcastCommand broadcastCommand = new BroadcastCommand(this);
        // Freeze
        FreezeCommand freezeCommand = new FreezeCommand(this);
        // WhoIs
        WhoIsCommand whoIsCommand = new WhoIsCommand(this);
        // Register the command with Paper's command map
        try {
            // This is the Paper-compatible way to register commands

            //Vanish
            getServer().getCommandMap().register("", vanishCommand);
            getServer().getCommandMap().register("", vanishListCommand);

            //Fly & Speed Control
            getServer().getCommandMap().register("", flyCommand);
            getServer().getCommandMap().register("", flySpeedCommand);
            //God Mode
            getServer().getCommandMap().register("", godCommand);
            // Game Mode Manager
            getServer().getCommandMap().register("", gmCommand);
            getServer().getCommandMap().register("", gmsCommand);
            getServer().getCommandMap().register("", gmcCommand);
            getServer().getCommandMap().register("", gmaCommand);
            getServer().getCommandMap().register("", gmspCommand);
            // BroadCast
            getServer().getCommandMap().register("", broadcastCommand);
            //Freeze
            getServer().getCommandMap().register("", freezeCommand);
            //Who Is Command
            getServer().getCommandMap().register("", whoIsCommand);

            getLogger().info("Registered vanish command using Paper API");
        } catch (Exception e) {
            getLogger().warning("Failed to register command with Paper API: " + e.getMessage());
        }
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new VanishListener(this), this);
    }

    // Manager getters
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public PlayerManager getPlayerManager() { return playerManager; }
    public VanishManager getVanishManager() { return vanishManager; }
    public FlyManager getFlyManager() { return flyManager; }
    public GodManager getGodManager() { return godManager; }
    public GamemodeManager getGamemodeManager() { return gamemodeManager; }
    public BroadcastManager getBroadcastManager() { return broadcastManager; }
    public FreezeManager getFreezeManager() { return freezeManager; }
    public WhoIsManager getWhoIsManager() { return whoIsManager; }
}