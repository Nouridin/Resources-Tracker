package me.nouridin.resourcesTracker.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateChecker {

    private final JavaPlugin plugin;
    private final String currentVersion;
    private static final String API_URL = "https://api.modrinth.com/v2/project/resources-tracker/version";
    private static boolean updateAvailable = false;
    private static String latestVersion = "";

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
    }

    public void check() {
        if (!plugin.getConfig().getBoolean("check-for-updates", true)) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "ResourcesTracker/UpdateChecker");

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    Pattern pattern = Pattern.compile("\"version_number\":\"(.*?)\"");
                    Matcher matcher = pattern.matcher(response.toString());

                    if (matcher.find()) {
                        latestVersion = matcher.group(1);
                        if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                            updateAvailable = true;
                            plugin.getLogger().info("A new version of ResourcesTracker is available: " + latestVersion);
                            notifyAdmins();
                        }
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Could not check for updates: " + e.getMessage());
            }
        });
    }

    private void notifyAdmins() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.isOp()) {
                    player.sendMessage(ChatColor.GREEN + "A new version of " + ChatColor.BOLD + "ResourcesTracker" + ChatColor.GREEN + " is available: " + ChatColor.YELLOW + latestVersion);
                    player.sendMessage(ChatColor.GRAY + "You can download it from: " + ChatColor.AQUA + "https://modrinth.com/project/resources-tracker");
                }
            }
        });
    }
    public static boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public static String getLatestVersion() {
        return latestVersion;
    }
}
