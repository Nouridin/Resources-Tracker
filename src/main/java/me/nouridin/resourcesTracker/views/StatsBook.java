package me.nouridin.resourcesTracker.views;

import me.nouridin.resourcesTracker.data.StorageManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StatsBook {

    private final StorageManager storageManager;
    private final Player target;

    public StatsBook(StorageManager storageManager, Player target) {
        this.storageManager = storageManager;
        this.target = target;
    }

    public ItemStack createBook(Player generator, String searchTerm) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();

        String title = (searchTerm != null && !searchTerm.isEmpty())
                ? "Stats: " + target.getName() + " ('" + searchTerm + "')"
                : "Stats: " + target.getName();

        meta.setTitle(title);
        meta.setAuthor(generator.getName());

        List<BaseComponent[]> pages = new ArrayList<>();

        // Title Page
        TextComponent titleComponent = new TextComponent(title + "\n");
        titleComponent.setColor(net.md_5.bungee.api.ChatColor.DARK_BLUE);
        titleComponent.setBold(true);

        TextComponent dateComponent = new TextComponent("Generated on:\n" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n\n");
        dateComponent.setColor(net.md_5.bungee.api.ChatColor.GRAY);

        TextComponent generatorComponent = new TextComponent("By: " + generator.getName());
        generatorComponent.setColor(net.md_5.bungee.api.ChatColor.DARK_GRAY);
        generatorComponent.setItalic(true);

        pages.add(new BaseComponent[]{titleComponent, dateComponent, generatorComponent});

        // Broken Blocks
        addStatPages(pages, "Broken Blocks", storageManager.getBrokenBlocks(target.getUniqueId(), searchTerm));

        // Picked Up Items
        addStatPages(pages, "Picked-Up Items", storageManager.getPickedUpItems(target.getUniqueId(), searchTerm));

        meta.spigot().setPages(pages);
        book.setItemMeta(meta);
        return book;
    }

    private void addStatPages(List<BaseComponent[]> pages, String title, Map<Material, Integer> data) {
        TextComponent titleComponent = new TextComponent(title + "\n\n");
        titleComponent.setColor(net.md_5.bungee.api.ChatColor.DARK_RED);
        titleComponent.setBold(true);

        if (data.isEmpty()) {
            TextComponent emptyComponent = new TextComponent("None");
            emptyComponent.setColor(net.md_5.bungee.api.ChatColor.GRAY);
            pages.add(new BaseComponent[]{titleComponent, emptyComponent});
            return;
        }

        List<TextComponent> components = new ArrayList<>();
        components.add(titleComponent);

        int lines = 1;
        for (Map.Entry<Material, Integer> entry : data.entrySet()) {
            TextComponent material = new TextComponent(entry.getKey().toString().replace("_", " ").toLowerCase() + ": ");
            material.setColor(net.md_5.bungee.api.ChatColor.BLACK);

            TextComponent count = new TextComponent(entry.getValue() + "\n");
            count.setColor(net.md_5.bungee.api.ChatColor.DARK_BLUE);

            components.add(material);
            components.add(count);
            lines++;

            if (lines >= 7) { // 7 entries per page (each entry is 2 components)
                pages.add(components.toArray(new BaseComponent[0]));
                components.clear();
                lines = 0;
            }
        }

        if (!components.isEmpty()) {
            pages.add(components.toArray(new BaseComponent[0]));
        }
    }
}
