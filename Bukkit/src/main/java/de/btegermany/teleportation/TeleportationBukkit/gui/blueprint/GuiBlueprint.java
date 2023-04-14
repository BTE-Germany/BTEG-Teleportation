package de.btegermany.teleportation.TeleportationBukkit.gui.blueprint;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiBlueprint {

    private final ItemStack defaultPlaceholder;
    private final List<BlueprintItem[]> rows = new ArrayList<>();
    private int currentRow = 0;

    public GuiBlueprint(@Nonnull ItemStack defaultPlaceholder) {
        this.defaultPlaceholder = defaultPlaceholder;
    }

    public GuiBlueprint(@Nonnull Material material) {
        defaultPlaceholder = new ItemStack(material);
    }

    public GuiBlueprint() {
        defaultPlaceholder = new ItemStack(Material.AIR);
    }

    public GuiBlueprint addRow(BlueprintRange... rawItems) {
        BlueprintItem[] items = new BlueprintItem[9];
        for (BlueprintRange range : rawItems) {
            for (int j = 0; j < range.positions.size(); j++) {
                items[range.positions.get(j)] = range.getItem();
            }
        }
        rows.add(items);
        return this;
    }

    public BlueprintItem[] getCurrentRow() {
        return rows.get(currentRow);
    }

    public List<BlueprintItem> getAllItems() {
        List<BlueprintItem> items = new ArrayList<>();
        rows.forEach(blueprintItems -> items.addAll(Arrays.asList(blueprintItems)));
        return items;
    }

    public boolean nextRow() {
        if(currentRow < getRowsCount() - 1) {
            currentRow++;
            return true;
        }
        return false;
    }

    public int getRowsCount() {
        return rows.size();
    }

    public ItemStack getDefaultPlaceholder() {
        return defaultPlaceholder;
    }

}
