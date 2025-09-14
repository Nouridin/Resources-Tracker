package me.nouridin.resourcesTracker.data;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class DataManager {

    private final JavaPlugin plugin;
    private final Map<UUID, Map<Material, Integer>> brokenBlocks = new HashMap<>();
    private final Map<UUID, Map<Material, Integer>> pickedUpItems = new HashMap<>();
    private final Set<UUID> notificationsDisabled = new HashSet<>();

    public DataManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void addBrokenBlock(UUID playerUUID, Material blockType) {
        Map<Material, Integer> playerBlocks = brokenBlocks.computeIfAbsent(playerUUID, k -> new HashMap<>());
        playerBlocks.put(blockType, playerBlocks.getOrDefault(blockType, 0) + 1);
    }

    public void addPickedUpItem(UUID playerUUID, Material itemType, int amount) {
        Map<Material, Integer> playerItems = pickedUpItems.computeIfAbsent(playerUUID, k -> new HashMap<>());
        playerItems.put(itemType, playerItems.getOrDefault(itemType, 0) + amount);
    }

    public Map<Material, Integer> getBrokenBlocks(UUID playerUUID) {
        return brokenBlocks.getOrDefault(playerUUID, new HashMap<>());
    }

    public Map<Material, Integer> getPickedUpItems(UUID playerUUID) {
        return pickedUpItems.getOrDefault(playerUUID, new HashMap<>());
    }

    public void setNotificationsDisabled(UUID playerUUID, boolean disabled) {
        if (disabled) {
            notificationsDisabled.add(playerUUID);
        } else {
            notificationsDisabled.remove(playerUUID);
        }
    }

    public boolean areNotificationsDisabled(UUID playerUUID) {
        return notificationsDisabled.contains(playerUUID);
    }

    public void saveData() {
        File dataFile = new File(plugin.getDataFolder(), "data.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        config.set("broken-blocks", null);
        config.set("picked-up-items", null);

        for (Map.Entry<UUID, Map<Material, Integer>> entry : brokenBlocks.entrySet()) {
            for (Map.Entry<Material, Integer> dataEntry : entry.getValue().entrySet()) {
                config.set("broken-blocks." + entry.getKey().toString() + "." + dataEntry.getKey().toString(), dataEntry.getValue());
            }
        }

        for (Map.Entry<UUID, Map<Material, Integer>> entry : pickedUpItems.entrySet()) {
            for (Map.Entry<Material, Integer> dataEntry : entry.getValue().entrySet()) {
                config.set("picked-up-items." + entry.getKey().toString() + "." + dataEntry.getKey().toString(), dataEntry.getValue());
            }
        }

        config.set("notifications-disabled", notificationsDisabled.stream().map(UUID::toString).collect(Collectors.toList()));

        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data to data.yml!");
            e.printStackTrace();
        }
    }

    public void loadData() {
        File dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            return;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        ConfigurationSection brokenBlocksSection = config.getConfigurationSection("broken-blocks");
        if (brokenBlocksSection != null) {
            for (String uuidString : brokenBlocksSection.getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                ConfigurationSection playerSection = brokenBlocksSection.getConfigurationSection(uuidString);
                if (playerSection != null) {
                    Map<Material, Integer> playerBlocks = new HashMap<>();
                    for (String materialString : playerSection.getKeys(false)) {
                        Material material = Material.getMaterial(materialString);
                        if (material != null) {
                            playerBlocks.put(material, playerSection.getInt(materialString));
                        }
                    }
                    brokenBlocks.put(uuid, playerBlocks);
                }
            }
        }

        ConfigurationSection pickedUpItemsSection = config.getConfigurationSection("picked-up-items");
        if (pickedUpItemsSection != null) {
            for (String uuidString : pickedUpItemsSection.getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                ConfigurationSection playerSection = pickedUpItemsSection.getConfigurationSection(uuidString);
                if (playerSection != null) {
                    Map<Material, Integer> playerItems = new HashMap<>();
                    for (String materialString : playerSection.getKeys(false)) {
                        Material material = Material.getMaterial(materialString);
                        if (material != null) {
                            playerItems.put(material, playerSection.getInt(materialString));
                        }
                    }
                    pickedUpItems.put(uuid, playerItems);
                }
            }
        }

        notificationsDisabled.clear();
        config.getStringList("notifications-disabled").forEach(uuidString -> notificationsDisabled.add(UUID.fromString(uuidString)));
    }
}
