package de.btegermany.teleportation.TeleportationBukkit.gui;

import de.btegermany.teleportation.TeleportationBukkit.TeleportationBukkit;
import de.btegermany.teleportation.TeleportationBukkit.gui.base.CustomGui;
import de.btegermany.teleportation.TeleportationBukkit.gui.warp.WarpGui;
import de.btegermany.teleportation.TeleportationBukkit.message.PluginMessenger;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jetbrains.annotations.Nullable;

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

        public static GuiItem searchItem(CustomGui gui) {
            TextComponent button = Component.text(" /nwarp ", NamedTextColor.BLUE)
                    .clickEvent(ClickEvent.suggestCommand("/nwarp "))
                    .hoverEvent(HoverEvent.showText(Component.text("Command in der Chatzeile einfügen")));
            TextComponent message = Component.text("ᾠ ")
                    .append(Component.text("Nutze den Command", NamedTextColor.GOLD))
                    .append(button)
                    .append(Component.text("und gib dahinter den Namen eines Warps oder der Stadt, die du suchst, ein.", NamedTextColor.GOLD));

            return emptyItem("Suchen", NamedTextColor.GOLD, () -> {
                gui.close();
                gui.getPlayer().sendMessage(message);
            });
        }

        public static GuiItem homeItem(CustomGui gui, PagedGuiHandler pagedGuiHandler, PluginMessenger pluginMessenger, TeleportationBukkit plugin) {
            return emptyItem("Startseite", NamedTextColor.GOLD, () -> new WarpGui(gui.getPlayer(), pagedGuiHandler, pluginMessenger, plugin));
        }

    }

}
