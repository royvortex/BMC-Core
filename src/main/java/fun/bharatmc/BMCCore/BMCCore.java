package fun.bharatmc.BMCCore;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import fun.bharatmc.BMCCore.Commands.GodModeCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class BMCCore extends JavaPlugin {
    private GodModeCommand godModeCommand;

    @Override
    public void onLoad() {
        // Configure CommandAPI
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this)
                .shouldHookPaperReload(true)
                .verboseOutput(true)
                .silentLogs(false)
        );
    }

    @Override
    public void onEnable() {
        // Enable CommandAPI
        CommandAPI.onEnable();

        // Initialize commands
        this.godModeCommand = new GodModeCommand();

        // Register commands
        registerCommands();

        getLogger().info("BMCCore has been enabled with CommandAPI!");
    }

    @Override
    public void onDisable() {
        // Clean up god mode
        if (godModeCommand != null) {
            godModeCommand.cleanupOnDisable();
        }

        // Disable CommandAPI
        CommandAPI.onDisable();

        getLogger().info("BMCCore has been disabled!");
    }

    private void registerCommands() {
        // Register god mode command
        godModeCommand.register();

        // Register fly command
        new fun.bharatmc.BMCCore.Commands.FlyCommand().register();

        // Add more commands here as needed
    }

    public GodModeCommand getGodModeCommand() {
        return godModeCommand;
    }
}
