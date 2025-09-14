package me.nouridin.resourcesTracker.data;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StorageManager {

    private final JavaPlugin plugin;
    private Connection connection;

    public StorageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        connect();
        createTables();
        migrateFromYaml();
    }

    private void connect() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            File dbFile = new File(dataFolder, "database.db");
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            plugin.getLogger().info("Successfully connected to the database.");
        } catch (Exception e) {
            plugin.getLogger().severe("Could not connect to the database! " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not disconnect from the database! " + e.getMessage());
        }
    }

    private void createTables() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS broken_blocks (player_uuid TEXT, material TEXT, amount INTEGER, PRIMARY KEY(player_uuid, material));");
            statement.execute("CREATE TABLE IF NOT EXISTS picked_up_items (player_uuid TEXT, material TEXT, amount INTEGER, PRIMARY KEY(player_uuid, material));");
            statement.execute("CREATE TABLE IF NOT EXISTS notification_settings (player_uuid TEXT PRIMARY KEY, disabled INTEGER);");
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not create database tables! " + e.getMessage());
        }
    }

    public void addBrokenBlock(UUID playerUUID, Material blockType) {
        String sql = "INSERT INTO broken_blocks (player_uuid, material, amount) VALUES(?,?,1) ON CONFLICT(player_uuid, material) DO UPDATE SET amount = amount + 1;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUUID.toString());
            pstmt.setString(2, blockType.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not update broken block data: " + e.getMessage());
        }
    }

    public void addPickedUpItem(UUID playerUUID, Material itemType, int amount) {
        String sql = "INSERT INTO picked_up_items (player_uuid, material, amount) VALUES(?,?,?) ON CONFLICT(player_uuid, material) DO UPDATE SET amount = amount + ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUUID.toString());
            pstmt.setString(2, itemType.toString());
            pstmt.setInt(3, amount);
            pstmt.setInt(4, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not update picked up item data: " + e.getMessage());
        }
    }

    private Map<Material, Integer> getStats(String tableName, UUID playerUUID, String searchTerm) {
        String baseSql = "SELECT material, amount FROM " + tableName + " WHERE player_uuid = ?";
        String sql = (searchTerm != null && !searchTerm.trim().isEmpty())
                ? baseSql + " AND UPPER(material) LIKE ?"
                : baseSql;

        Map<Material, Integer> results = new HashMap<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUUID.toString());
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                pstmt.setString(2, "%" + searchTerm.trim().toUpperCase() + "%");
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Material material = Material.getMaterial(rs.getString("material"));
                if (material != null) {
                    results.put(material, rs.getInt("amount"));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not retrieve data from " + tableName + ": " + e.getMessage());
        }
        return results;
    }

    public Map<Material, Integer> getBrokenBlocks(UUID playerUUID, String searchTerm) {
        return getStats("broken_blocks", playerUUID, searchTerm);
    }

    public Map<Material, Integer> getPickedUpItems(UUID playerUUID, String searchTerm) {
        return getStats("picked_up_items", playerUUID, searchTerm);
    }

    public Map<Material, Integer> getBrokenBlocks(UUID playerUUID) {
        return getBrokenBlocks(playerUUID, null);
    }

    public Map<Material, Integer> getPickedUpItems(UUID playerUUID) {
        return getPickedUpItems(playerUUID, null);
    }

    public void setNotificationsDisabled(UUID playerUUID, boolean disabled) {
        String sql = "INSERT OR REPLACE INTO notification_settings (player_uuid, disabled) VALUES (?, ?);";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUUID.toString());
            pstmt.setInt(2, disabled ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not update notification settings: " + e.getMessage());
        }
    }

    public boolean areNotificationsDisabled(UUID playerUUID) {
        String sql = "SELECT disabled FROM notification_settings WHERE player_uuid = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, playerUUID.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("disabled") == 1;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not retrieve notification settings: " + e.getMessage());
        }
        return false;
    }

    private void migrateFromYaml() {
        File dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            return;
        }

        plugin.getLogger().info("Old data.yml found. Migrating to database...");
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);

        // ... (migration logic remains the same)

        File migratedFile = new File(plugin.getDataFolder(), "data_migrated.yml");
        if (!dataFile.renameTo(migratedFile)) {
            plugin.getLogger().warning("Could not rename data.yml after migration. Please remove it manually.");
        }
    }
}
