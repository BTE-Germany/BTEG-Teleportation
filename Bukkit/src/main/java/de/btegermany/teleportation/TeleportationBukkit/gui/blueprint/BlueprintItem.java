package de.btegermany.teleportation.TeleportationBukkit.gui.blueprint;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlueprintItem {

    private ItemStack item = null;

    // content
    public BlueprintItem() {}

    public BlueprintItem(Material material) {
        item = new ItemStack(material);
    }

    public BlueprintItem(Material material, String name) {
        item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
    }

    public BlueprintItem(ItemStack item) {
        this.item = item;
    }

    public BlueprintItem(ItemStack item, String name) {
        this.item = item;
        ItemMeta meta = item.getItemMeta();
        if(meta != null && name != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
    }

    public boolean isContent() {
        return item == null;
    }

    public ItemStack getItem() {
        return item;
    }

}
