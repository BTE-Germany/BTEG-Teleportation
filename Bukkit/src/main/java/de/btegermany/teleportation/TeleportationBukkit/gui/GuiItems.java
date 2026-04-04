package de.btegermany.teleportation.TeleportationBukkit.gui;

import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.gui.base.CustomGui;
import de.btegermany.teleportation.TeleportationBukkit.gui.warp.WarpGui;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class GuiItems {

    public static GuiItem closeItem(CustomGui gui) {
        return emptyItem("Schließen", NamedTextColor.DARK_RED, gui::close);
    }

    public static GuiItem blankItem() {
        return new GuiItem(customModelItemStack("", null, "blank"));
    }

    public static GuiItem emptyItem(String name, TextColor textColor, Runnable onClick) {
        return new GuiItem(customModelItemStack(name, textColor, "empty"), event -> onClick.run());
    }

    public static GuiItem fillerItem() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.customName(Component.text(""));
        item.setItemMeta(meta);
        return new GuiItem(item);
    }

    public static ItemStack customModelItemStack(String name, @Nullable TextColor textColor, String customModelData) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        meta.customName(Component.text(name, textColor));
        CustomModelDataComponent modelDataComponent = meta.getCustomModelDataComponent();
        modelDataComponent.setStrings(List.of(customModelData));
        meta.setCustomModelDataComponent(modelDataComponent);

        item.setItemMeta(meta);

        return item;
    }



    public static class Warps {

        public static GuiItem randomWarpItem(CustomGui gui) {
            return emptyItem("Zufälliger Warp", NamedTextColor.GOLD, () -> {
                gui.close();
                gui.getPlayer().performCommand("nwarp random");
            });
        }

        public static GuiItem searchItem(CustomGui gui, TeleportationBukkit plugin) {
            return emptyItem("Suchen", NamedTextColor.GOLD, () -> {
                gui.close();

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    new AnvilGUI.Builder()
                            .onClick((slot, stateSnapshot) -> switch (slot) {
                                case AnvilGUI.Slot.INPUT_RIGHT -> List.of(AnvilGUI.ResponseAction.close());

                                case AnvilGUI.Slot.OUTPUT -> {
                                    if (stateSnapshot.getText().isEmpty()) {
                                        yield Collections.emptyList();
                                    }

                                    gui.getPlayer().performCommand("nwarp %s".formatted(stateSnapshot.getText()));
                                    yield List.of(AnvilGUI.ResponseAction.close());
                                }

                                default -> Collections.emptyList();
                            })
                            .title("Suchbegriff eingeben")
                            .itemLeft(GuiItems.blankItem().getItemStack())
                            .itemRight(GuiItems.customModelItemStack("Schließen", NamedTextColor.DARK_RED, "x"))
                            .itemOutput(GuiItems.customModelItemStack("", null, "search"))
                            .plugin(plugin)
                            .open(gui.getPlayer());
                }, 5);
            });
        }

        public static GuiItem homeItem(CustomGui gui, PagedGuiHandler pagedGuiHandler, PluginMessenger pluginMessenger, TeleportationBukkit plugin) {
            return emptyItem("Startseite", NamedTextColor.GOLD, () -> new WarpGui(gui.getPlayer(), pagedGuiHandler, pluginMessenger, plugin));
        }

    }

}
