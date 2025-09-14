package me.nouridin.resourcesTracker.commands;

import me.nouridin.resourcesTracker.data.StorageManager;
import me.nouridin.resourcesTracker.views.StatsBook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class StatsCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    private final StorageManager storageManager;

    public StatsCommand(JavaPlugin plugin, StorageManager storageManager) {
        this.plugin = plugin;
        this.storageManager = storageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (plugin.getConfig().getBoolean("Only-OP") && !player.isOp()) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("notifications")) {
            if (args.length == 2) {
                if (args[1].equalsIgnoreCase("enable")) {
                    storageManager.setNotificationsDisabled(player.getUniqueId(), false);
                    player.sendMessage(ChatColor.GREEN + "Notifications have been enabled.");
                } else if (args[1].equalsIgnoreCase("disable")) {
                    storageManager.setNotificationsDisabled(player.getUniqueId(), true);
                    player.sendMessage(ChatColor.RED + "Notifications have been disabled.");
                } else {
                    player.sendMessage(ChatColor.RED + "Usage: /rstats notifications <enable/disable>");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /rstats notifications <enable/disable>");
            }
            return true;
        }

        if (args.length >= 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            String searchTerm = null;
            if (args.length > 1) {
                searchTerm = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            }

            ItemStack statsBook = new StatsBook(storageManager, target).createBook(player, searchTerm);
            player.getInventory().addItem(statsBook);
            player.sendMessage(ChatColor.GREEN + "A statistics book for " + target.getName() + " has been added to your inventory.");
            return true;
        }

        player.sendMessage(ChatColor.RED + "Usage: /rstats <player> [search term] or /rstats notifications <enable/disable>");
        return true;
    }
}
