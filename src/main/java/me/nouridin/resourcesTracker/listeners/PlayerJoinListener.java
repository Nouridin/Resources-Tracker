package me.nouridin.resourcesTracker.listeners;

import me.nouridin.resourcesTracker.util.UpdateChecker;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) {
            if (UpdateChecker.isUpdateAvailable()) {
                player.sendMessage(ChatColor.GREEN + "A new version of " + ChatColor.BOLD + "ResourcesTracker" + ChatColor.GREEN + " is available: " + ChatColor.YELLOW + UpdateChecker.getLatestVersion());
                player.sendMessage(ChatColor.GRAY + "You can download it from: " + ChatColor.AQUA + "https://modrinth.com/project/resources-tracker");
            }
        }
    }
}
