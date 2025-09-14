package me.nouridin.resourcesTracker.listeners;

import me.nouridin.resourcesTracker.views.StatsGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof StatsGUI) {
            event.setCancelled(true);

            StatsGUI gui = (StatsGUI) event.getInventory().getHolder();
            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();

            if (slot == 48) { // Previous page
                gui.previousPage(player);
            } else if (slot == 50) { // Next page
                gui.nextPage(player);
            } else if (slot == 49) { // Switch view
                gui.switchView(player);
            }
        }
    }
}
