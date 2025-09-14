package me.nouridin.resourcesTracker.listeners;

import me.nouridin.resourcesTracker.data.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ItemPickupListener implements Listener {

    private final JavaPlugin plugin;
    private final StorageManager storageManager;
    private final List<Material> notifyMaterials;

    public ItemPickupListener(JavaPlugin plugin, StorageManager storageManager) {
        this.plugin = plugin;
        this.storageManager = storageManager;
        this.notifyMaterials = plugin.getConfig().getStringList("notify-on").stream()
                .map(Material::getMaterial)
                .collect(Collectors.toList());
    }

    @EventHandler
    public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent event) {
        Player player = event.getPlayer();
        Material itemType = event.getItem().getItemStack().getType();
        int amount = event.getItem().getItemStack().getAmount();
        storageManager.addPickedUpItem(player.getUniqueId(), itemType, amount);

        if (notifyMaterials.contains(itemType)) {
            Location loc = player.getLocation();
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            String[] messages = {
                    ChatColor.DARK_RED + "[" + plugin.getName() + "] " + ChatColor.YELLOW + player.getName() + " picked up " + amount + " " + itemType,
                    ChatColor.GRAY + "--------------------",
                    ChatColor.WHITE + "X: " + ChatColor.AQUA + loc.getBlockX(),
                    ChatColor.WHITE + "Y: " + ChatColor.AQUA + loc.getBlockY(),
                    ChatColor.WHITE + "Z: " + ChatColor.AQUA + loc.getBlockZ(),
                    ChatColor.GRAY + "--------------------",
                    ChatColor.DARK_GRAY + date
            };

            // Notify admins
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.hasPermission("resourcestracker.notify") && !storageManager.areNotificationsDisabled(onlinePlayer.getUniqueId())) {
                    onlinePlayer.sendMessage(messages);
                }
            }
        }
    }
}
