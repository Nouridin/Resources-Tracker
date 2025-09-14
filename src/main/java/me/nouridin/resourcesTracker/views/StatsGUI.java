package me.nouridin.resourcesTracker.views;

import me.nouridin.resourcesTracker.data.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsGUI implements InventoryHolder {

    private final DataManager dataManager;
    private final Player target;
    private final Inventory inventory;
    private int currentPage = 0;
    private View currentView = View.BROKEN_BLOCKS;

    private enum View {
        BROKEN_BLOCKS,
        PICKED_UP_ITEMS
    }

    public StatsGUI(DataManager dataManager, Player target) {
        this.dataManager = dataManager;
        this.target = target;
        this.inventory = Bukkit.createInventory(this, 54, "Stats: " + target.getName());
    }

    public void open(Player player) {
        updateGUI();
        player.openInventory(inventory);
    }

    private void updateGUI() {
        inventory.clear();

        Map<Material, Integer> data = currentView == View.BROKEN_BLOCKS ?
                dataManager.getBrokenBlocks(target.getUniqueId()) :
                dataManager.getPickedUpItems(target.getUniqueId());

        List<Map.Entry<Material, Integer>> sortedData = data.entrySet().stream()
                .sorted(Map.Entry.<Material, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        int maxItemsPerPage = 45;
        int startIndex = currentPage * maxItemsPerPage;
        int endIndex = Math.min(startIndex + maxItemsPerPage, sortedData.size());

        for (int i = startIndex; i < endIndex; i++) {
            Map.Entry<Material, Integer> entry = sortedData.get(i);
            ItemStack item = new ItemStack(entry.getKey());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(entry.getKey().toString());
                List<String> lore = new ArrayList<>();
                lore.add((currentView == View.BROKEN_BLOCKS ? "Broken: " : "Picked Up: ") + entry.getValue());
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inventory.setItem(i - startIndex, item);
        }

        // Controls
        int maxPages = (int) Math.ceil((double) sortedData.size() / maxItemsPerPage);

        // Previous Page Button
        if (currentPage > 0) {
            inventory.setItem(48, createControlButton(Material.ARROW, "Previous Page"));
        }

        // Switch View Button
        String switchButtonName = currentView == View.BROKEN_BLOCKS ? "View Picked-Up Items" : "View Broken Blocks";
        Material switchButtonMaterial = currentView == View.BROKEN_BLOCKS ? Material.HOPPER : Material.DIAMOND_PICKAXE;
        inventory.setItem(49, createControlButton(switchButtonMaterial, switchButtonName));

        // Next Page Button
        if (currentPage < maxPages - 1) {
            inventory.setItem(50, createControlButton(Material.ARROW, "Next Page"));
        }
    }

    private ItemStack createControlButton(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void nextPage(Player player) {
        currentPage++;
        updateGUI();
    }

    public void previousPage(Player player) {
        if (currentPage > 0) {
            currentPage--;
            updateGUI();
        }
    }

    public void switchView(Player player) {
        currentView = (currentView == View.BROKEN_BLOCKS) ? View.PICKED_UP_ITEMS : View.BROKEN_BLOCKS;
        currentPage = 0; // Reset page when switching views
        updateGUI();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
