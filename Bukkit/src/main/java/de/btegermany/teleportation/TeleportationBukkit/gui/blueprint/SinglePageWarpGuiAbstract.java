package de.btegermany.teleportation.TeleportationBukkit.gui.blueprint;

import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import de.btegermany.teleportation.TeleportationBukkit.registry.RegistriesProvider;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class SinglePageWarpGuiAbstract extends WarpGuiAbstract {

    protected List<BlueprintItem> blueprintItems;

    public SinglePageWarpGuiAbstract(GuiArgs guiArgs, String title) {
        super(guiArgs, title, false);

        currentInventory = Bukkit.createInventory(player, (blueprint.getRowsCount() + 1) * 9, title);
        blueprintItems = blueprint.getAllItems();
        setContent(getContent());
    }

    public abstract @Nonnull List<ItemStack> getContent();

    public void setContent(List<ItemStack> items) {
        clearPages();
        int itemsIndex = 0;
        for(int i = 0; i < currentInventory.getSize() - 9; i++) {
            if(blueprintItems.get(i).isContent()) {
                ItemStack item = items.get(itemsIndex);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.AQUA + meta.getDisplayName());
                item.setItemMeta(meta);
                currentInventory.setItem(i, item);
                itemsIndex++;
                continue;
            }
            currentInventory.setItem(i, blueprintItems.get(i).getItem());
        }
        inventory.addPage(currentInventory);
    }

    public void clearPages() {
        for(int i = 0; i < inventory.getSize(); i++) {
            inventory.removePage(i);
        }
    }

}
