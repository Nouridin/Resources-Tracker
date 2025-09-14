package me.nouridin.resourcesTracker;

import me.nouridin.resourcesTracker.commands.StatsCommand;
import me.nouridin.resourcesTracker.data.StorageManager;
import me.nouridin.resourcesTracker.listeners.BlockBreakListener;
import me.nouridin.resourcesTracker.listeners.ItemPickupListener;
import me.nouridin.resourcesTracker.listeners.PlayerJoinListener;
import me.nouridin.resourcesTracker.util.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

public final class ResourcesTracker extends JavaPlugin {

    private StorageManager storageManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.storageManager = new StorageManager(this);

        getServer().getPluginManager().registerEvents(new BlockBreakListener(this, storageManager), this);
        getServer().getPluginManager().registerEvents(new ItemPickupListener(this, storageManager), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        getCommand("rstats").setExecutor(new StatsCommand(this, storageManager));

        new UpdateChecker(this).check();
    }

    @Override
    public void onDisable() {
        this.storageManager.disconnect();
    }
}
